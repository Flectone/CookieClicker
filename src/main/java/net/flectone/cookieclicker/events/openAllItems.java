package net.flectone.cookieclicker.events;

import com.google.inject.Inject;
import net.flectone.cookieclicker.CookieClicker;
import net.flectone.cookieclicker.items.ItemManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Map;

public class openAllItems implements Listener {
    private final ItemManager manager;
    @Inject
    public openAllItems(ItemManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!(player.getInventory().getItemInMainHand().getType().equals(Material.JIGSAW))) return;
        Inventory creative = Bukkit.createInventory(player, 9 * 3, Component.text("Cookie Clicker menu"));
        player.openInventory(creative);
        player.setMetadata("inv", new FixedMetadataValue(CookieClicker.getPlugin(CookieClicker.class), "menu_selector"));
        ItemStack allItems = new ItemStack(Material.COMMAND_BLOCK), recipes = new ItemStack(Material.CRAFTING_TABLE);
        ItemMeta meta = allItems.getItemMeta();
        meta.displayName(Component.text("Посмотреть все предметы"));
        allItems.setItemMeta(meta);
        creative.setItem(12, allItems);
        ItemMeta meta2 = recipes.getItemMeta();
        meta2.displayName(Component.text("Посмотреть все рецепты"));
        recipes.setItemMeta(meta2);
        creative.setItem(14, recipes);

    }
}
