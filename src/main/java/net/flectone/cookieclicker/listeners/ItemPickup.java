package net.flectone.cookieclicker.listeners;

import net.flectone.cookieclicker.CookieClicker;
import net.flectone.cookieclicker.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;

public class ItemPickup implements Listener {

    @EventHandler
    public void pickupitem (EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof  Player player)) return;
        Bukkit.getScheduler().runTask(CookieClicker.getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        mergeItem(player, ItemManager.get("ENCHANTED_RUBY"), event.getItem().getItemStack());
                        mergeItem(player, ItemManager.get("ENCHANTED_COOKIE"), event.getItem().getItemStack());
                        mergeItem(player, ItemManager.get("64ENCH_COOKIE"), event.getItem().getItemStack(), ItemManager.getType("ENCHANTED_COOKIE"), false);
                        mergeItem(player, ItemManager.get("MELON_BLOCK"), event.getItem().getItemStack(), Material.MELON_SLICE, false);
                        mergeItem(player, ItemManager.get("ENCHANTED_WHEAT"), event.getItem().getItemStack());
                        mergeItem(player, ItemManager.get("HAY_BLOCK"), event.getItem().getItemStack(), Material.WHEAT, false);
                        mergeItem(player, ItemManager.get("ENCH_COCOA_BEANS"), event.getItem().getItemStack());
                        mergeItem(player, ItemManager.get("FINE_AMETHYST"), event.getItem().getItemStack());
                        mergeItem(player, ItemManager.get("PERFECT_AMETHYST"), event.getItem().getItemStack(), Material.AMETHYST_SHARD, false);
                    }
                });

    }


    private void mergeItem (Player player, ItemStack mergeItem, ItemStack eventItem) {
        mergeItem(player, mergeItem, eventItem, mergeItem.getType(), true);
    }
    private void mergeItem (Player player, ItemStack mergeItem, ItemStack eventItem, Material mergedMaterial, boolean checkEnch) {
        if (!eventItem.getType().equals(mergedMaterial)) return;
        int countMergeItem = 0;
        for (ItemStack item : player.getInventory()) {
            if (item == null) continue;
            if (!item.getType().equals(mergedMaterial)) continue;
            if (item.getAmount() != 64) continue;
            if (checkEnch && !item.getEnchantments().isEmpty()) continue;
            player.getInventory().removeItem(item);
            countMergeItem++;
        }
        for (ItemStack item : player.getInventory()) {
            if (item == null) continue;
            if (!item.getType().equals(mergeItem.getType())) continue;
            if (item.getAmount() == 64) continue;
            if (!(checkEnch && !item.getEnchantments().isEmpty()) && !item.getType().equals(Material.PLAYER_HEAD)) continue;
            int itemAmount = item.getAmount() + countMergeItem;
            int remainder = 64 - itemAmount;
            countMergeItem = remainder < 0 ? remainder * -1 : 0;
            item.setAmount(remainder < 0 ? 64 : itemAmount);


        }
        mergeItem.setAmount(countMergeItem);
        player.getInventory().addItem(mergeItem);
        player.updateInventory();
    }




}
