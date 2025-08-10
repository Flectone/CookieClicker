package net.flectone.cookieclicker.events;

import com.github.retrooper.packetevents.event.*;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityStatus;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.entities.playerdata.ServerCookiePlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;

@Singleton
public class Packets implements PacketListener {

    private final PacketSetSlotEvent setSlotEvent;
    private final PacketMoveEvent packetMoveEvent;
    private final PacketCookieClickEvent packetCookieClickEvent;
    private final PacketEatingEvent packetEatingEvent;
    private final PacketClickInventoryEvent inventoryClickEvent;

    private final PacketInteractAtEntityEvent packetInteractAtEntityEvent;
    private final ConnectedPlayers connectedPlayers;

    private final PacketInteractEvent packetInteractEvent;

    @Inject
    public Packets(PacketSetSlotEvent setSlotEvent, PacketMoveEvent packetMoveEvent, PacketEatingEvent packetEatingEvent,
                   PacketCookieClickEvent packetCookieClickEvent,
                   PacketInteractAtEntityEvent packetInteractAtEntityEvent, PacketInteractEvent packetInteractEvent,
                   ConnectedPlayers connectedPlayers, PacketClickInventoryEvent inventoryEvent) {
        this.setSlotEvent = setSlotEvent;
        this.packetMoveEvent = packetMoveEvent;
        this.packetCookieClickEvent = packetCookieClickEvent;
        this.packetEatingEvent = packetEatingEvent;
        this.inventoryClickEvent = inventoryEvent;

        this.packetInteractAtEntityEvent = packetInteractAtEntityEvent;
        this.connectedPlayers = connectedPlayers;

        this.packetInteractEvent = packetInteractEvent;
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
            case PacketType.Play.Client.CLOSE_WINDOW -> inventoryClickEvent.onClose(serverCookiePlayer);
            case PacketType.Play.Client.ANIMATION -> packetCookieClickEvent.changeLegendaryHoeMode(user);
            case PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT -> packetCookieClickEvent.bookShelfClick(serverCookiePlayer);
            case PacketType.Play.Client.CLICK_WINDOW -> {
                event.setCancelled(inventoryClickEvent.manageWindow(serverCookiePlayer, new WrapperPlayClientClickWindow(event)));
            }
            case PacketType.Play.Client.PLAYER_DIGGING -> {
                boolean cancel = packetInteractEvent.checkForDropAction(serverCookiePlayer, new WrapperPlayClientPlayerDigging(event).getAction());
                event.setCancelled(cancel);
            }
            case PacketType.Play.Client.INTERACT_ENTITY -> {
                if (packetInteractAtEntityEvent.checkEntity(new WrapperPlayClientInteractEntity(event), serverCookiePlayer)) {
                    event.setCancelled(true);
                }
            }
            case PacketType.Play.Client.USE_ITEM -> {
                packetInteractEvent.onRightClick(serverCookiePlayer);
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
