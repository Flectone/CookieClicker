package net.flectone.cookieclicker.events;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.CompactItems;
import net.flectone.cookieclicker.items.ItemManager;
import net.flectone.cookieclicker.playerdata.ServerCookiePlayer;
import net.flectone.cookieclicker.utility.StatsUtils;
import net.minecraft.world.entity.player.Player;

@Singleton
public class PacketSetSlotEvent {
    private final CompactItems compact;
    private final StatsUtils statsUtils;
    private final ItemManager loadedItems;

    @Inject
    public PacketSetSlotEvent(CompactItems compact, StatsUtils statsUtils, ItemManager loadedItems) {
        this.compact = compact;
        this.statsUtils = statsUtils;
        this.loadedItems = loadedItems;
    }

    public void compactItems(ServerCookiePlayer serverCookiePlayer) {
        Player player = serverCookiePlayer.getPlayer();

        if (statsUtils.hasTag(player.getOffhandItem(), "cookie_crafter")) {
            compact.compact(player.getInventory(), "ench_cookie", loadedItems.getNMS("cookie_block"), 512);
        }

        compact.compact(player.getInventory(), "cookie", loadedItems.getNMS("ench_cookie"), 160);
        compact.compact(player.getInventory(), "cocoa_beans", loadedItems.getNMS("ench_cocoa"), 320);
        compact.compact(player.getInventory(), "wheat", loadedItems.getNMS("ench_wheat"), 160);
    }
}
