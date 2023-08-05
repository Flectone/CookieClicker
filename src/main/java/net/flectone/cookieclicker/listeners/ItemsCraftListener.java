package net.flectone.cookieclicker.listeners;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTCompoundList;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTList;
import net.flectone.cookieclicker.CookieClicker;
import net.flectone.cookieclicker.ItemManager;
import net.flectone.cookieclicker.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemsCraftListener implements Listener {
    @EventHandler
    public void OnPickaxeCraft (PrepareItemCraftEvent event) {
        Inventory inventory = event.getInventory();
        ItemStack goldenPick = inventory.getItem(5);
        if (goldenPick == null || !goldenPick.getType().equals(Material.GOLDEN_PICKAXE)) return;
        for (int i = 1; i < 10; i++) {
            if (inventory.getItem(i) == null) return;
            if (i == 5) continue;
            ItemStack ruby = inventory.getItem(i);
            if (!ruby.getType().equals(Material.RED_DYE) || ruby.getEnchantments().isEmpty()) return;
        }

        event.getInventory().setResult(ItemManager.get("RUBY_PICKAXE"));

    }

    @EventHandler
    public void onCakeCraft (PrepareItemCraftEvent event) {
        Inventory inventory = event.getInventory();
        if (!equalsItem(inventory.getItem(1), Material.MILK_BUCKET)) return;
        if (!equalsItem(inventory.getItem(2), Material.MILK_BUCKET)) return;
        if (!equalsItem(inventory.getItem(3), Material.MILK_BUCKET)) return;
        if (!equalsItem(inventory.getItem(4), Material.EGG, 2)) return;
        if (!equalsItem(inventory.getItem(5), Material.MELON, 3)) return;
        if (!equalsItem(inventory.getItem(6), Material.EGG, 2)) return;
        if (!equalsItem(inventory.getItem(7), Material.HAY_BLOCK)) return;
        if (!equalsItem(inventory.getItem(8), Material.HAY_BLOCK)) return;
        if (!equalsItem(inventory.getItem(9), Material.HAY_BLOCK)) return;
        event.getInventory().setResult(ItemManager.get("CAKE"));

    }
    @EventHandler
    public void onSmithingCraft (PrepareAnvilEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory.getItem(1) == null || inventory.getItem(0) == null || !inventory.getItem(1).getType().equals(Material.CAKE)) return;
        ArrayList<Material> armor = new ArrayList<>();
        armor.add(Material.LEATHER_HELMET);
        armor.add(Material.LEATHER_CHESTPLATE);
        armor.add(Material.LEATHER_LEGGINGS);
        armor.add(Material.LEATHER_BOOTS);
        NBTItem nbtItem = new NBTItem(inventory.getItem(0));
        if (!armor.contains(inventory.getItem(0).getType()) || !nbtItem.hasTag("ff")) return;

        ItemStack secondArmor = new ItemStack(inventory.getItem(0).getType()); //бля может просто 4 ифа написать и нормально будет
        ItemMeta sArmorMeta = secondArmor.getItemMeta();
        List<String> list = new ArrayList<>();
        list.add(ChatColor.GRAY + "Farming fortune II");
        sArmorMeta.setDisplayName(inventory.getItem(0).getItemMeta().getDisplayName() + ChatColor.GOLD + " ★");
        sArmorMeta.setLore(list);
        sArmorMeta.addItemFlags(ItemFlag.HIDE_DYE);
        sArmorMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        sArmorMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        sArmorMeta.setUnbreakable(true);
        secondArmor.setItemMeta(sArmorMeta);
        NBTItem sArmornbt = new NBTItem(secondArmor);
        sArmornbt.setInteger("farmfortune2", 1);
        NBTCompound display = sArmornbt.addCompound("display");
        display.setInteger("color", 13921578);
        secondArmor = sArmornbt.getItem();
        secondArmor.addUnsafeEnchantment(Enchantment.MENDING, 1);


        event.setResult(secondArmor);
        event.getInventory().setRepairCost(10);
        List<HumanEntity> viewers = event.getViewers();
        viewers.forEach(humanEntity -> ((Player)humanEntity).updateInventory());
    }
    @EventHandler
    public void cakeCraftEvent (InventoryClickEvent event) {
        if (!(event.getInventory() instanceof CraftingInventory)) return;
        if (!equalsItem(event.getCurrentItem(), Material.CAKE)) return;
        Bukkit.getScheduler().runTask(CookieClicker.getPlugin(), new Runnable() {
            @Override
            public void run() {
                event.getInventory().clear();
            }
        });
    }
    private boolean equalsItem(ItemStack item, Material material, int count) {
        if (item == null || item.getAmount() != count) return false;
        return item.getType().equals(material);
    }
    private boolean equalsItem(ItemStack item, Material material) {
        return equalsItem(item, material, 1);
    }
}

