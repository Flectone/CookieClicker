package net.flectone.cookieclicker.gameplay.cookiepart.listeners;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.entities.ConnectedPlayers;
import net.flectone.cookieclicker.gameplay.cookiepart.InteractionController;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.UUID;

@Singleton
public class BukkitPlayerInteractListener implements Listener {

    private final InteractionController interactionController;
    private final ConnectedPlayers connectedPlayers;

    @Inject
    public BukkitPlayerInteractListener(InteractionController interactionController,
                                        ConnectedPlayers connectedPlayers) {
        this.interactionController = interactionController;
        this.connectedPlayers = connectedPlayers;
    }

    @EventHandler
    public void onEntityClick(PlayerInteractEntityEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();

        boolean cancel = interactionController.checkEntity(event.getRightClicked(),
                connectedPlayers.getServerCookiePlayer(playerId));
        event.setCancelled(cancel);
    }
}
