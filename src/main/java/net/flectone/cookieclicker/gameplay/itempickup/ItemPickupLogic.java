package net.flectone.cookieclicker.gameplay.itempickup;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.entities.objects.item.CookieItemEntityData;
import net.flectone.cookieclicker.entities.playerdata.ServerCookiePlayer;
import net.flectone.cookieclicker.entities.ConnectedPlayers;
import net.flectone.cookieclicker.items.ItemsRegistry;
import net.flectone.cookieclicker.items.itemstacks.base.data.Features;
import net.flectone.cookieclicker.items.itemstacks.base.data.ItemTag;
import net.flectone.cookieclicker.utility.ItemsCompactor;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

import java.util.List;

@Singleton
public class ItemPickupLogic {

    private final ItemsCompactor compactor;
    private final ItemsRegistry loadedItems;
    private final ConnectedPlayers connectedPlayers;

    @Inject
    public ItemPickupLogic(ItemsRegistry loadedItems, ConnectedPlayers connectedPlayers, ItemsCompactor compactor) {
        this.loadedItems = loadedItems;
        this.connectedPlayers = connectedPlayers;
        this.compactor = compactor;
    }

    public void processPlayerMove(ServerCookiePlayer serverCookiePlayer) {
        List<CookieItemEntityData> itemsToPickUp = List.copyOf(serverCookiePlayer.getItems());
        Player player = serverCookiePlayer.getPlayer();
        boolean isPickup = false;

        if (serverCookiePlayer.getItems().isEmpty())
            return;

        for (CookieItemEntityData itemEntityData : itemsToPickUp) {
            if (itemEntityData.getPosition().distance(player.getX(), player.getY(), player.getZ()) < 1.8d) {
                player.getInventory().add(loadedItems.get(itemEntityData.getItemTag(), itemEntityData.getCount()));
                serverCookiePlayer.pickUpItem(itemEntityData);
                isPickup = true;
            }
        }

        if (isPickup) {
            connectedPlayers.save(serverCookiePlayer, true);
        }
    }

    public void compactItems(ServerCookiePlayer serverCookiePlayer) {
        Player player = serverCookiePlayer.getPlayer();
        Inventory inventory = player.getInventory();

        if ((new Features(player.getOffhandItem())).getItemTag() == ItemTag.COOKIE_CRAFTER) {
            compactor.compact(inventory, ItemTag.ENCHANTED_COOKIE, loadedItems.get(ItemTag.BLOCK_OF_COOKIE), 512);
        }

        compactor.compact(inventory, ItemTag.COOKIE, loadedItems.get(ItemTag.ENCHANTED_COOKIE), 160);
        compactor.compact(inventory, ItemTag.COCOA_BEANS, loadedItems.get(ItemTag.ENCHANTED_COCOA_BEANS), 320);
        compactor.compact(inventory, ItemTag.WHEAT, loadedItems.get(ItemTag.ENCHANTED_WHEAT), 160);
    }
}
