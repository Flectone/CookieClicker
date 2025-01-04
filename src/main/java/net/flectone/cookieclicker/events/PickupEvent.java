package net.flectone.cookieclicker.events;

import com.google.inject.Inject;
import net.flectone.cookieclicker.CompactItems;
import net.flectone.cookieclicker.items.ItemManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class PickupEvent implements Listener {
    private final CompactItems compactItems;
    private final ItemManager manager;
    @Inject
    public PickupEvent(CompactItems compactItems, ItemManager manager) {
        this.compactItems = compactItems;
        this.manager = manager;
    }

//    @EventHandler
//    public void pickupEvent(EntityPickupItemEvent event) {
//        if (!(event.getEntity() instanceof Player player)) return;
//        compactItems.compact(player.getInventory(), manager.get("cookie"), manager.get("ench_cookie"), 160);
//        compactItems.compact(player.getInventory(), manager.get("cocoa_beans"), manager.get("ench_cocoa"), 320);
//        compactItems.compact(player.getInventory(), manager.get("wheat"), manager.get("ench_wheat"), 160);
//    }
}
