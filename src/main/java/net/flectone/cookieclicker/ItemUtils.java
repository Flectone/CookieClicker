package net.flectone.cookieclicker;

import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTList;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class ItemUtils {
    private static ItemStack forgeCanItem(ItemStack item, String can, String[] blocks, ItemFlag itemFlag) {
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(itemFlag);
        item.setItemMeta(meta);
        NBTItem nbt = new NBTItem(item);
        NBTList<String> canNbt = nbt.getStringList(can);
        for (int i = 0; i < blocks.length; i ++) {
            canNbt.add(blocks[i]);
        }
        return nbt.getItem();
    }

    public static ItemStack forgeCanDestroyItem(ItemStack item, String... blocks) {
        return forgeCanItem(item, "CanDestroy", blocks, ItemFlag.HIDE_DESTROYS);
    }

    public static ItemStack forgeCanBePlacedOnItem(ItemStack item, String... blocks) {
        return forgeCanItem(item, "CanPlaceOn", blocks, ItemFlag.HIDE_PLACED_ON);
    }

    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public static Integer addFortune(Inventory inventory, String tag, Integer multiplier) {
        ArrayList <ItemStack> list = new ArrayList<>();
        int addChance = 0;

        for (int i = 36; i < 40; i++) {
            list.add(inventory.getItem(i));
        }

        for (ItemStack armor : list) {
            if (armor == null) continue;
            NBTItem nbtItem = new NBTItem(armor);
            if (nbtItem.hasTag(tag)) {
                addChance += multiplier;
            }
        }
    return addChance;
    }

    public static ItemStack createDefaultMeta(String displayName, Material material) {
        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(displayName);
        item.setItemMeta(itemMeta);
        return item;
    }

    public static ItemStack createEnchantedMeta(String displayName, Material material, Enchantment enchant) {
        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(displayName);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(itemMeta);
        item.addUnsafeEnchantment(enchant, 1);
        return item;
    }

}
