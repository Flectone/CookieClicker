package net.flectone.cookieclicker.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class HayBlockCraftCancel implements Listener {
    @EventHandler
    public void onBlockCraft (PrepareItemCraftEvent event) {
        Inventory inventory = event.getInventory();
        for (int i = 1; i < 10; i++) {
            if (inventory.getItem(i) == null) return;
            if (!inventory.getItem(i).getType().equals(Material.WHEAT)) return;

        }
        ItemStack air = new ItemStack(Material.AIR);
        event.getInventory().setResult(air);
    }

}
