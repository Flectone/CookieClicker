package net.flectone.cookieclicker.crafting;

import com.google.inject.Inject;
import net.flectone.cookieclicker.items.CustomRecipe;
import net.flectone.cookieclicker.utility.UtilsCookie;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CraftingEvent implements Listener {
    private final Recipes recipes;
    private final UtilsCookie utilsCookie;
    @Inject
    public CraftingEvent(Recipes recipes, UtilsCookie utilsCookie) {
        this.recipes = recipes;
        this.utilsCookie = utilsCookie;
    }

    @EventHandler
    public void prepareCraft (PrepareItemCraftEvent event) {
        List<ItemStack> listIS = createList(event.getInventory());
//        event.getViewers().forEach(b -> {
//            b.sendMessage(Component.text(String.valueOf(recipes.listWithItemStacks(listIS))));
//        });
        CustomRecipe recipe = recipes.findRecipe(listIS);
        if (recipe == null) return;

//        for (Map.Entry<ItemStack, ItemStack> i : combineListsToListOfMap(listIS, recipe.getAllIngredients()))
//            if (i.getKey().getAmount() < i.getValue().getAmount()) return;

        event.getInventory().setResult(recipe.getResult());
    }

    @EventHandler
    public void onCraft (InventoryClickEvent event) {
        if (!(event.getClickedInventory() instanceof CraftingInventory inv)) return;
        if (event.getSlot() != 0 || inv.getItem(0) == null) return;
        List<ItemStack> listIS = createList(inv);
        HumanEntity humanEntity = event.getWhoClicked();

        if (!(recipes.checkForRecipe(listIS))) return;
        CustomRecipe recipe = recipes.findRecipe(listIS);
        event.setCancelled(true);
        boolean compareResult = utilsCookie.compare(humanEntity.getItemOnCursor(), recipe.getResult());
        if (!(compareResult || humanEntity.getItemOnCursor().getType().equals(Material.AIR))) {
            return;
        }

        for (Map.Entry<ItemStack, ItemStack> i : recipes.combineListsToListOfMap(listIS, recipe.getAllIngredients())) {
            i.getKey().setAmount(i.getKey().getAmount() - i.getValue().getAmount());
        }


        if (!(recipes.checkForRecipe(createList(inv)))) inv.setResult(null);
        humanEntity.setItemOnCursor(utilsCookie.createItemAmount(recipe.getResult(), compareResult ?
                humanEntity.getItemOnCursor().getAmount()+1 : 1));
    }

    private List<ItemStack> createList (CraftingInventory inv) {
        List<ItemStack> itemStackList = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            ItemStack item = inv.getItem(i);
            itemStackList.add(item != null ? item : new ItemStack(Material.AIR));
        }
        return itemStackList;
    }

}
