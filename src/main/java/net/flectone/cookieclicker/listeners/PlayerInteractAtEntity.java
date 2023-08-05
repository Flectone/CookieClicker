package net.flectone.cookieclicker.listeners;

import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTType;
import de.tr7zw.nbtapi.data.NBTData;
import net.flectone.cookieclicker.ItemManager;
import net.flectone.cookieclicker.ItemUtils;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class PlayerInteractAtEntity implements Listener {
    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof ItemFrame itemframe)) return;
        if (!itemframe.getItem().getType().equals(Material.COOKIE)) return;
        itemframe.setFixed(true);
        event.setCancelled(true);
        ItemStack cookie = ItemManager.get("COOKIE");
        int maxCookie = 1;
        int melonChance = 10;

        ItemStack playerItem = event.getPlayer().getInventory().getItemInMainHand();
        if (playerItem.getType().equals(Material.GOLDEN_HOE)) {
            Object object = playerItem.getEnchantments().get(Enchantment.LOOT_BONUS_BLOCKS);
            if (object != null) {
                maxCookie = (int)object + 1;
            }


        }
        // Тута кароче проверка на броню эта с лилгрином
        Inventory inventory = event.getPlayer().getInventory();
        maxCookie += ItemUtils.addFortune(inventory, "ff", 1);
        maxCookie += ItemUtils.addFortune(inventory, "farmfortune2", 2);
        melonChance += ItemUtils.addFortune(inventory, "farmfortune2", 2);

        Location location = event.getRightClicked().getLocation();
        location.setY(location.getY() + 1);
        //Тут короче проверка на мотыгу с опытом
        ItemStack itemInHand = event.getPlayer().getInventory().getItemInMainHand();
        if (!itemInHand.getType().equals(Material.AIR)) {
            NBTItem itemInHandnbt = new NBTItem(itemInHand);
            if (itemInHandnbt.hasTag("xpbonus")) {
                ExperienceOrb xpOrb = location.getWorld().spawn(location, ExperienceOrb.class);
                xpOrb.setExperience(1);
            }
        }
        location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EAT, 0.1f, 1.0f);


        //Тута будет проверка на топор с арбузами
        ItemStack melonItem = new ItemStack(Material.MELON_SLICE);
        ItemMeta melonMeta = melonItem.getItemMeta();
        melonMeta.setDisplayName(ChatColor.GREEN + "Арбузик");
        melonItem.setItemMeta(melonMeta);
        if (!itemInHand.getType().equals(Material.AIR)) {
            NBTItem itemInHandnbt = new NBTItem(itemInHand);
            if (itemInHandnbt.hasTag("melon")) {
                Random melonRandom = new Random();
                int r = melonRandom.nextInt();
                r = melonRandom.nextInt(1, 100);
                if (r <= melonChance) {
                    Item melon = location.getWorld().dropItem(location, melonItem);
                }
            }
        }
        Random random = new Random();
        int randomAmount = random.nextInt(1, maxCookie + 1);
        cookie.setAmount(randomAmount);

        //Разрушитель печенья
        ItemStack wheatItem = new ItemStack(Material.WHEAT);
        wheatItem.setAmount(randomAmount);
        ItemStack cocoaBeansItem = new ItemStack(Material.COCOA_BEANS);
        cocoaBeansItem.setAmount(randomAmount);
        if (!itemInHand.getType().equals(Material.AIR)) {
            NBTItem itemInHandnbt = new NBTItem(itemInHand);
            if (itemInHandnbt.hasTag("cookiebreaker")) {
                Item wheat = location.getWorld().dropItem(location, wheatItem);
                wheat.setVelocity(new Vector(0, 0, 0));
                Item cocoaBeans = location.getWorld().dropItem(location, cocoaBeansItem);
                cocoaBeans.setVelocity(new Vector(0, 0, 0));
                return;
            }
        }

        Item item = location.getWorld().dropItem(location, cookie);
        item.setVelocity(new Vector(0, 0, 0));

    }

}
