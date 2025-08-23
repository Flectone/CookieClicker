package net.flectone.cookieclicker.eventdata.listener;

import com.github.retrooper.packetevents.event.*;
import com.github.retrooper.packetevents.protocol.player.User;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.entities.ConnectedPlayers;
import net.flectone.cookieclicker.eventdata.CookieEventManager;

@Singleton
public class PacketDispatcher implements PacketListener {

    private final CookieEventManager eventManager;
    private final ConnectedPlayers connectedPlayers;

    @Inject
    public PacketDispatcher(CookieEventManager eventManager, ConnectedPlayers connectedPlayers) {
        this.eventManager = eventManager;
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

        eventManager.processPacketEvent(event, event.getUser().getUUID());
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        User user = event.getUser();
        if (user == null) return;

        eventManager.processPacketEvent(event, event.getUser().getUUID());
    }
}
