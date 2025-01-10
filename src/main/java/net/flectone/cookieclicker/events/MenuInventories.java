package net.flectone.cookieclicker.events;

import com.google.inject.Inject;
import net.flectone.cookieclicker.CompactItems;
import net.flectone.cookieclicker.CookieClicker;
import net.flectone.cookieclicker.items.Recipes;
import net.flectone.cookieclicker.inventories.crafting.CustomRecipe;
import net.flectone.cookieclicker.items.ItemManager;
import net.flectone.cookieclicker.items.ShopManager;
import net.flectone.cookieclicker.utility.ItemTagsUtility;
import net.flectone.cookieclicker.utility.UtilsCookie;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;
import java.util.Map;

public class MenuInventories implements Listener {
    private final ItemManager manager;
    private final ShopManager shopManager;
    private final CompactItems compactItems;
    private final UtilsCookie utilsCookie;
    private final Recipes recipes;
    private final ItemTagsUtility itemTagsUtility;
    @Inject
    public MenuInventories(Recipes recipes, ShopManager shopManager, CompactItems compactItems,
                           UtilsCookie utilsCookie, ItemManager manager, ItemTagsUtility itemTagsUtility) {
        this.shopManager = shopManager;
        this.compactItems = compactItems;
        this.utilsCookie = utilsCookie;
        this.manager = manager;
        this.recipes = recipes;
        this.itemTagsUtility = itemTagsUtility;
    }


    public boolean checkIfInv (HumanEntity he, String menuName) {
        return !he.getMetadata("inv").isEmpty()
                && he.getMetadata("inv").getFirst().asString().equals(menuName);
    }
    public void openAllRecipes(HumanEntity he) {
        Inventory recipesInv = Bukkit.createInventory(he, 9 * 4, Component.text("Все рецепты"));
        int slot = 0;
        for (CustomRecipe b : recipes.getAllRecipes().values()) {
            recipesInv.setItem(slot, b.getResult());
            slot++;
        }

        he.openInventory(recipesInv);
        he.setMetadata("inv", new FixedMetadataValue(CookieClicker.getPlugin(CookieClicker.class), "menu_recipes"));
    }
    public void openRecipe(HumanEntity humanEntity, CustomRecipe se) {
        Inventory recipe = Bukkit.createInventory(humanEntity, 9 * 3, Component.text("рецепт"));
        humanEntity.openInventory(recipe);

        humanEntity.setMetadata("inv", new FixedMetadataValue(CookieClicker.getPlugin(CookieClicker.class), "recipe"));
        int slot = 0;
        for (ItemStack itm : se.getAllIngredients()) {
            recipe.setItem(slot, itm);
            slot++;
            if (slot == 3 || slot == 12)
                slot += 6;
        }
        for (int i : List.of(3, 12, 21))
            recipe.setItem(i, new ItemStack(Material.WHITE_STAINED_GLASS_PANE));
        recipe.setItem(13, se.getResult());
        recipe.setItem(8, new ItemStack(Material.RED_STAINED_GLASS_PANE));
    }

    @EventHandler
    public void invSelectorClick (InventoryClickEvent event) {
        HumanEntity humanEntity = event.getWhoClicked();
        if (event.getClickedInventory() == null || event.getClickedInventory().isEmpty()) return;
        if (!(checkIfInv(humanEntity, "menu_selector"))) return;
        event.setCancelled(true);
        switch (event.getSlot()) {
            case 12:
                humanEntity.closeInventory();
                Inventory allItems = Bukkit.createInventory(humanEntity, 9 * 4, Component.text("халява"));
                humanEntity.openInventory(allItems);
                int slot = 0;
                for (Map.Entry<String, net.minecraft.world.item.ItemStack> entry : manager.allItems()) {
                    allItems.setItem(slot, new ItemStack(CraftItemStack.asBukkitCopy(entry.getValue())));
                    slot++;
                }
                break;
            case 14:
                humanEntity.closeInventory();
                openAllRecipes(humanEntity);
                break;
        }

    }

    @EventHandler
    public void recipesSelector(InventoryClickEvent event) {
        HumanEntity he = event.getWhoClicked();
        if (event.getClickedInventory() == null || event.getClickedInventory().isEmpty()) return;
        if (!(checkIfInv(he, "menu_recipes"))) return;
        event.setCancelled(true);
        if (event.getSlot() >= recipes.getAllRecipes().size()) return;
        he.closeInventory();
        openRecipe(he, (CustomRecipe) recipes.getAllRecipes().values().toArray()[event.getSlot()]);
    }
    @EventHandler
    public void recipeInvClick (InventoryClickEvent event) {
        HumanEntity he = event.getWhoClicked();
        if (event.getClickedInventory() == null || event.getClickedInventory().isEmpty()) return;
        if (!(checkIfInv(he, "recipe"))) return;
        event.setCancelled(true);
        if (event.getSlot() != 8) return;
        he.closeInventory();
        openAllRecipes(he);
    }

    @EventHandler
    public void closeInvEvent (InventoryCloseEvent event) {
        HumanEntity humanEntity = event.getPlayer();
        String name = null;
        if (checkIfInv(humanEntity, "menu_selector"))
            name = "menu_selector";
        if (checkIfInv(humanEntity, "menu_recipes"))
            name = "menu_recipes";
        if (checkIfInv(humanEntity, "recipe"))
            name = "recipe";
        if (name != null) humanEntity.sendMessage(Component.text("closed! " + name));

        if (name != null)
            humanEntity.removeMetadata("inv", CookieClicker.getPlugin(CookieClicker.class));
    }
}
