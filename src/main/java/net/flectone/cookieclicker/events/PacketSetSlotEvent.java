package net.flectone.cookieclicker.events;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.CompactItems;
import net.flectone.cookieclicker.items.ItemManager;
import net.flectone.cookieclicker.playerdata.ServerCookiePlayer;
import net.flectone.cookieclicker.utility.ItemTagsUtility;
import net.minecraft.world.entity.player.Player;

@Singleton
public class PacketSetSlotEvent {
    private final CompactItems compact;
    private final ItemTagsUtility itemTagsUtility;
    private final ItemManager manager;

    @Inject
    public PacketSetSlotEvent(CompactItems compact, ItemTagsUtility itemTagsUtility, ItemManager manager) {
        this.compact = compact;
        this.itemTagsUtility = itemTagsUtility;
        this.manager = manager;
    }

    public void compactItems(ServerCookiePlayer serverCookiePlayer) {
        Player player = serverCookiePlayer.getPlayer();

        if (itemTagsUtility.getItemTag(player.getOffhandItem()).equals("cookie_crafter")) {
            compact.compact(player.getInventory(), manager.getNMS("ench_cookie"), manager.getNMS("cookie_block"), 512);
        }

        compact.compact(player.getInventory(), manager.getNMS("cookie"), manager.getNMS("ench_cookie"), 160);
        compact.compact(player.getInventory(), manager.getNMS("cocoa_beans"), manager.getNMS("ench_cocoa"), 320);
        compact.compact(player.getInventory(), manager.getNMS("wheat"), manager.getNMS("ench_wheat"), 160);
    }
}
