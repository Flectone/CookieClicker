package net.flectone.cookieclicker.events;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.entities.CookieItemEntityData;
import net.flectone.cookieclicker.items.ItemManager;
import net.flectone.cookieclicker.playerdata.ServerCookiePlayer;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class PacketMoveEvent {
    private final ItemManager loadedItems;
    private final ConnectedPlayers connectedPlayers;

    @Inject
    public PacketMoveEvent(ItemManager loadedItems, ConnectedPlayers connectedPlayers) {
        this.loadedItems = loadedItems;
        this.connectedPlayers = connectedPlayers;
    }

    public void processPlayerMove(ServerCookiePlayer serverCookiePlayer) {
        List<CookieItemEntityData> itemsToPickUp = new ArrayList<>();
        Player player = serverCookiePlayer.getPlayer();
        if (serverCookiePlayer.getItems().isEmpty())
            return;

        for (CookieItemEntityData itemEntityData : serverCookiePlayer.getItems()) {
            if (itemEntityData.getPosition().distance(player.getX(), player.getY(), player.getZ()) < 2d) {
                player.getInventory().add(loadedItems.get(itemEntityData.getItemTag(), itemEntityData.getCount()));

                itemsToPickUp.add(itemEntityData);
            }
        }

        if (!itemsToPickUp.isEmpty()) {
            itemsToPickUp.forEach(serverCookiePlayer::pickUpItem);
            connectedPlayers.save(serverCookiePlayer, true);
        }
    }
}
