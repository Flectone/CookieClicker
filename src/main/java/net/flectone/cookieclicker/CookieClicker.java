package net.flectone.cookieclicker;

import net.flectone.cookieclicker.commands.CommandGiveitem;
import net.flectone.cookieclicker.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Predicate;

public final class CookieClicker extends JavaPlugin {
    private static CookieClicker plugin;

    public static CookieClicker getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new BlockBreakListener(), this);
        Bukkit.getPluginManager().registerEvents(new ItemPickup(), this);
        Bukkit.getPluginManager().registerEvents(new ItemsCraftListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerInteractAtEntity(), this);
        Bukkit.getPluginManager().registerEvents(new HayBlockCraftCancel(), this);
        Bukkit.getPluginManager().registerEvents(new TeleportRod(), this);


        Bukkit.getPluginManager().registerEvents(new OpenChest(), this);

        getCommand("giveitem").setExecutor(new CommandGiveitem());
        getCommand("giveitem").setTabCompleter(new CommandGiveitem());
        plugin = this;
        ItemManager.loadItems();

        NamespacedKey key = new NamespacedKey(this, "secondStarUpgrade");
        SmithingTransformRecipe recipe = new SmithingTransformRecipe(key, ItemManager.get("GEMSTONE_MIXTURE"),
                new RecipeChoice.ExactChoice(ItemManager.get("FINE_TOPAZ")),
                new RecipeChoice.ExactChoice(ItemManager.get("FINE_AMETHYST")),
                new RecipeChoice.ExactChoice(ItemManager.get("ENCHANTED_RUBY")));
        Bukkit.addRecipe(recipe);

        // Plugin startup logic
        getLogger().info("шок");

    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
