package net.flectone.cookieclicker.events;

import com.github.retrooper.packetevents.event.*;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityStatus;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenWindow;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.inventories.ContainerManager;
import net.flectone.cookieclicker.inventories.MainMenu;
import net.flectone.cookieclicker.playerdata.ServerCookiePlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;

@Singleton
public class Packets implements PacketListener {

    private final PacketSetSlotEvent setSlotEvent;
    private final PacketMoveEvent packetMoveEvent;
    private final PacketCookieClickEvent packetCookieClickEvent;
    private final PacketEatingEvent packetEatingEvent;
    private final ContainerManager containerManager;
    private final MainMenu mainMenu;
    private final PacketClickInventoryEvent inventoryClickEvent;

    private final PacketInteractAtEntityEvent packetInteractAtEntityEvent;
    private final ConnectedPlayers connectedPlayers;

    @Inject
    public Packets(PacketSetSlotEvent setSlotEvent, PacketMoveEvent packetMoveEvent, MainMenu mainMenu, PacketEatingEvent packetEatingEvent,
                   PacketCookieClickEvent packetCookieClickEvent, ContainerManager containerManager,
                   PacketInteractAtEntityEvent packetInteractAtEntityEvent,
                   ConnectedPlayers connectedPlayers, PacketClickInventoryEvent inventoryEvent) {
        this.setSlotEvent = setSlotEvent;
        this.packetMoveEvent = packetMoveEvent;
        this.packetCookieClickEvent = packetCookieClickEvent;
        this.packetEatingEvent = packetEatingEvent;
        this.containerManager = containerManager;
        this.mainMenu = mainMenu;
        this.inventoryClickEvent = inventoryEvent;

        this.packetInteractAtEntityEvent = packetInteractAtEntityEvent;
        this.connectedPlayers = connectedPlayers;
    }

    @Override
    public void onUserLogin(UserLoginEvent event) {
        connectedPlayers.processPlayerJoin(event.getUser().getUUID(), event.getUser().getName());
    }

    @Override
    public void onUserDisconnect(UserDisconnectEvent event) {
        connectedPlayers.saveAndDeleteLocal(event.getUser().getUUID());
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        User user = event.getUser();
        if (user == null) return;

        ServerCookiePlayer serverCookiePlayer = connectedPlayers.getServerCookiePlayer(user.getUUID());
        if (serverCookiePlayer == null) {
            return;
        }

        switch (event.getPacketType()) {
            case PacketType.Play.Client.PLAYER_POSITION, PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION -> packetMoveEvent.processPlayerMove(serverCookiePlayer);
            case PacketType.Play.Client.CLICK_WINDOW -> inventoryClickEvent.manageWindow(serverCookiePlayer, new WrapperPlayClientClickWindow(event));
            case PacketType.Play.Client.CLOSE_WINDOW -> containerManager.closeContainer(serverCookiePlayer.getUuid());
            case PacketType.Play.Client.ANIMATION -> packetCookieClickEvent.changeLegendaryHoeMode(user);
            case PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT -> packetCookieClickEvent.bookShelfClick(serverCookiePlayer);
            case PacketType.Play.Client.INTERACT_ENTITY -> {
                if (packetInteractAtEntityEvent.checkEntity(new WrapperPlayClientInteractEntity(event), serverCookiePlayer)) {
                    event.setCancelled(true);
                }
            }
            case PacketType.Play.Client.USE_ITEM -> {
                if (serverCookiePlayer.getPlayer().getItemInHand(InteractionHand.MAIN_HAND).getItem().equals(Items.JIGSAW)) {
                    mainMenu.openMainMenu(serverCookiePlayer);
                }
            }
            default -> {
                return;
            }
        }

    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getUser() == null)
            return;
        ServerCookiePlayer serverCookiePlayer = new ServerCookiePlayer(event.getUser().getUUID());
        Player player = serverCookiePlayer.getPlayer();

        if (event.getPacketType() == PacketType.Play.Server.OPEN_WINDOW) {
            WrapperPlayServerOpenWindow openContainerPacket = new WrapperPlayServerOpenWindow(event);
            containerManager.setOpenedContainer(event.getUser().getUUID(), openContainerPacket, "default");
        }

        //200iq костыль, ну а хули
        if (event.getPacketType() == PacketType.Play.Server.WINDOW_PROPERTY) {
            inventoryClickEvent.checkForEquipUpgrade(serverCookiePlayer);
        }

        if (event.getPacketType() == PacketType.Play.Server.SET_SLOT) {
            //&& setSlotPacket.getSlot() != 0
            inventoryClickEvent.checkAndPrepareCraft(serverCookiePlayer);

            setSlotEvent.compactItems(serverCookiePlayer);
            return;
        }

        if (event.getPacketType() == PacketType.Play.Server.ENTITY_STATUS) {
            packetEatingEvent.onEat(new WrapperPlayServerEntityStatus(event), serverCookiePlayer);
        }
    }
}
