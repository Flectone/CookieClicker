package net.flectone.cookieclicker.listeners;

import de.tr7zw.nbtapi.NBTItem;
import net.flectone.cookieclicker.CookieClicker;
import net.flectone.cookieclicker.ItemManager;
import net.flectone.cookieclicker.ItemUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Random;

public class BlockBreakListener implements Listener {
    @EventHandler
    public void breakRubyBlock (BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!item.getType().equals(Material.GOLDEN_PICKAXE) && !item.getType().equals(Material.DIAMOND_PICKAXE)) {
            return;
        }
        Block block = event.getBlock();
        if (!block.getType().equals(Material.RED_STAINED_GLASS)) return;

        int amountMultiplier = 0;
        Inventory inventory = event.getPlayer().getInventory();
        amountMultiplier += ItemUtils.addFortune(inventory, "rubyTest", 1);

        Random random = new Random();
        int randomCount = random.nextInt(1 + amountMultiplier,6 + amountMultiplier);

        ItemStack ruby = ItemManager.get("RUBY");
        ruby.setAmount(randomCount);

        event.setDropItems(false);
        block.getWorld().dropItemNaturally(block.getLocation(), ruby);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_BREAK, 1, 1);
        // Респавн рубинов
        Location gemstoneLocation = event.getBlock().getLocation();
        Bukkit.getScheduler().runTaskLater(CookieClicker.getPlugin(), new Runnable() {
            @Override
            public void run() {
                Block rubyGem = gemstoneLocation.getBlock();
                rubyGem.setType(Material.RED_STAINED_GLASS);

            }
        }, 200L);


    }
    @EventHandler
    public void breakAmethystBlock (BlockBreakEvent event) {
        Block block = event.getBlock();
        if (!block.getType().equals(Material.AMETHYST_BLOCK)) return;
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        if (!item.getType().equals(Material.DIAMOND_PICKAXE)) return;

        int amountMultiplier = 0;

        ItemStack playerItem = event.getPlayer().getInventory().getItemInMainHand();
        Object object = playerItem.getEnchantments().get(Enchantment.LOOT_BONUS_BLOCKS);
        if (object != null) {
            amountMultiplier = (int)object + 1;
        }

        Inventory inventory = event.getPlayer().getInventory();
        amountMultiplier += ItemUtils.addFortune(inventory, "rubyTest", 3);

        Random random = new Random();
        int randomCount = random.nextInt(2 + amountMultiplier,8 + amountMultiplier);

        ItemStack amethyst = ItemManager.get("AMETHYST");
        amethyst.setAmount(randomCount);


        Location gemstoneALocation = event.getBlock().getLocation();
        block.getWorld().dropItemNaturally(block.getLocation(), amethyst);
        event.setDropItems(false);
        Bukkit.getScheduler().runTaskLater(CookieClicker.getPlugin(), new Runnable() {
            @Override
            public void run() {
                Block AmethystGem = gemstoneALocation.getBlock();
                AmethystGem.setType(Material.AMETHYST_BLOCK);

            }
        }, 200L);
    }


}
