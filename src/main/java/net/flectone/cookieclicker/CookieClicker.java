package net.flectone.cookieclicker;

import net.flectone.cookieclicker.commands.CommandGiveitem;
import net.flectone.cookieclicker.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

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
        getCommand("giveitem").setExecutor(new CommandGiveitem());
        getCommand("giveitem").setTabCompleter(new CommandGiveitem());
        plugin = this;
        ItemManager.loadItems();

        // Plugin startup logic
        getLogger().info("шок");

    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
