package net.flectone.cookieclicker.events;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.utility.ItemsCompactor;
import net.flectone.cookieclicker.items.ItemsRegistry;
import net.flectone.cookieclicker.items.itemstacks.base.data.ItemTag;
import net.flectone.cookieclicker.entities.playerdata.ServerCookiePlayer;
import net.flectone.cookieclicker.utility.StatsUtils;
import net.minecraft.world.entity.player.Player;

@Singleton
public class PacketSetSlotEvent {
    private final ItemsCompactor compact;
    private final StatsUtils statsUtils;
    private final ItemsRegistry loadedItems;

    @Inject
    public PacketSetSlotEvent(ItemsCompactor compact, StatsUtils statsUtils, ItemsRegistry loadedItems) {
        this.compact = compact;
        this.statsUtils = statsUtils;
        this.loadedItems = loadedItems;
    }

    public void compactItems(ServerCookiePlayer serverCookiePlayer) {
        Player player = serverCookiePlayer.getPlayer();

        if (statsUtils.hasTag(player.getOffhandItem(), ItemTag.COOKIE_CRAFTER)) {
            compact.compact(player.getInventory(), ItemTag.ENCHANTED_COOKIE, loadedItems.get(ItemTag.BLOCK_OF_COOKIE), 512);
        }

        compact.compact(player.getInventory(), ItemTag.COOKIE, loadedItems.get(ItemTag.ENCHANTED_COOKIE), 160);
        compact.compact(player.getInventory(), ItemTag.COCOA_BEANS, loadedItems.get(ItemTag.ENCHANTED_COCOA_BEANS), 320);
        compact.compact(player.getInventory(), ItemTag.WHEAT, loadedItems.get(ItemTag.ENCHANTED_WHEAT), 160);
    }
}
