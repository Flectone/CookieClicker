package net.flectone.cookieclicker.eventdata;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.event.ProtocolPacketEvent;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.wrapper.play.client.*;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import net.flectone.cookieclicker.entities.ConnectedPlayers;
import net.flectone.cookieclicker.entities.playerdata.ServerCookiePlayer;
import net.flectone.cookieclicker.eventdata.events.*;
import net.flectone.cookieclicker.eventdata.events.base.BasePlayerEvent;
import net.flectone.cookieclicker.inventories.ContainerManager;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.MenuType;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
@Singleton
public class CookieEventManager {

    private final Map<EventType, Set<RegisteredListener>> registeredEvents = new EnumMap<>(EventType.class);

    private final ContainerManager containerManager;
    private final ConnectedPlayers connectedPlayers;
    private final Logger logger;

    @Inject
    public CookieEventManager(Logger logger, ConnectedPlayers connectedPlayers, ContainerManager containerManager) {
        this.logger = logger;
        this.connectedPlayers = connectedPlayers;
        this.containerManager = containerManager;
    }

    private void addListener(EventType eventType, RegisteredListener registeredListener) {
        registeredEvents.computeIfAbsent(eventType, k -> new HashSet<>()).add(registeredListener);
    }

    public void register(CookieListener listener) {
        List<Method> methods = Arrays.stream(listener.getClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(CookieEventHandler.class) && method.getParameterCount() == 1)
                .toList();

        for (Method method : methods) {
            // находим тип ивента по параметру в методе
            EventType eventType = EventType.fromClass(method.getParameterTypes()[0]);
            if (eventType == EventType.NONE) continue;

            try {
                addListener(eventType, new RegisteredListener(listener, method));
                logger.info("registered " + method.getName());
            } catch (IllegalAccessException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    public void processPacketEvent(ProtocolPacketEvent event, UUID uuid) {
        Object packetWrapper = getWrapper(event);

        if (packetWrapper == null) return;

        ServerCookiePlayer serverCookiePlayer = connectedPlayers.getServerCookiePlayer(uuid);
        if (serverCookiePlayer == null) return;


        switch (packetWrapper) {
            // Interact at entity
            case WrapperPlayClientInteractEntity interact ->
                    dispatchPlayerEvent(event, new ClickerInteractAtEntity(serverCookiePlayer, interact));
            // Player eat
            case WrapperPlayServerEntityStatus status when status.getEntityId() == serverCookiePlayer.getId() && status.getStatus() == 9 ->
                    dispatchPlayerEvent(event, new ClickerPlayerEat(serverCookiePlayer, status));
            // Player move
            case WrapperPlayClientPlayerPosition pos1 ->
                    dispatchPlayerEvent(event, new ClickerPlayerMove(serverCookiePlayer));
            case WrapperPlayClientPlayerPositionAndRotation pos2 ->
                    dispatchPlayerEvent(event, new ClickerPlayerMove(serverCookiePlayer));
            // Open window
            case WrapperPlayServerOpenWindow openWindow ->
                    dispatchPlayerEvent(event, new ClickerOpenWindow(serverCookiePlayer, openWindow));
            // Close window (by player)
            case WrapperPlayClientCloseWindow closeWindow ->
                    dispatchPlayerEvent(event, new ClickerCloseWindow(serverCookiePlayer, closeWindow));
            // Prepare craft
            case WrapperPlayServerSetSlot setSlot when serverCookiePlayer.getPlayer().containerMenu.menuType == MenuType.CRAFTING ->
                    dispatchPlayerEvent(event, new ClickerPrepareCraft(serverCookiePlayer));
            // Player pickup item
            case WrapperPlayServerCollectItem collectItem ->
                    dispatchPlayerEvent(event, new ClickerPickupItem(serverCookiePlayer));
            // Prepare anvil
            case WrapperPlayServerWindowProperty property when serverCookiePlayer.getPlayer().containerMenu instanceof AnvilMenu anvilMenu ->
                    dispatchPlayerEvent(event, new ClickerPrepareAnvil(serverCookiePlayer, anvilMenu));
            // Click window
            case WrapperPlayClientClickWindow clickWindow ->
                    dispatchPlayerEvent(event, new ClickerInventoryClick(serverCookiePlayer, clickWindow, containerManager.getOpenedContainer(serverCookiePlayer)));
            // Player use item (interact)
            case WrapperPlayClientUseItem useItem ->
                    dispatchPlayerEvent(event, new ClickerInteract(serverCookiePlayer, useItem));
            // Player drop item
            case WrapperPlayClientPlayerDigging digging when digging.getAction().equals(DiggingAction.DROP_ITEM) || digging.getAction().equals(DiggingAction.DROP_ITEM_STACK) ->
                    dispatchPlayerEvent(event, new ClickerDropItem(serverCookiePlayer));
            // Player animation (swing arm)
            case WrapperPlayClientAnimation animation ->
                    dispatchPlayerEvent(event, new ClickerPlayerSwingArm(serverCookiePlayer, animation));
            // Player interact with block
            case WrapperPlayClientPlayerBlockPlacement blockPlacement ->
                    dispatchPlayerEvent(event, new ClickerInteractBlock(serverCookiePlayer, blockPlacement));

            default -> {
                // некоторые пакеты могут не выполнять условия, из-за чего ивент не вызывается,
                // поэтому нужен этот default,
                // но ничего делать не надо в таких случаях
            }
        }
    }

    private void dispatchPlayerEvent(ProtocolPacketEvent event, BasePlayerEvent playerEvent) {
        EventType eventType = EventType.fromClass(playerEvent.getClass());

        Set<RegisteredListener> listeners = registeredEvents.getOrDefault(eventType, new HashSet<>());

        listeners.forEach(registeredListener -> executeEventHandler(registeredListener, playerEvent));

        event.setCancelled(playerEvent.isCancelled());
    }

    private void executeEventHandler(RegisteredListener registeredListener, BasePlayerEvent cookieEvent) {
        try {
            registeredListener.invoke(cookieEvent);
        } catch (Throwable e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Nullable
    private Object getWrapper(ProtocolPacketEvent event) {
        if (event.getPacketType() == null) return null;
        Class<?> clazz = event.getPacketType().getWrapperClass();
        if (clazz == null) return null;

        return switch (event) {
            case PacketReceiveEvent receiveEvent -> createInstance(clazz, PacketReceiveEvent.class, receiveEvent);
            case PacketSendEvent sendEvent -> createInstance(clazz, PacketSendEvent.class, sendEvent);
            default -> null;
        };
    }

    @Nullable
    private <T> Object createInstance(Class<?> clazz, Class<T> parameter, T value) {
        try {
            return clazz.getConstructor(parameter).newInstance(value);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            return null;
        }
    }
}
