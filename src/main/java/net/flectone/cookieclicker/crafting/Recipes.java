package net.flectone.cookieclicker.crafting;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.items.CustomRecipe;
import net.flectone.cookieclicker.items.ItemManager;
import net.flectone.cookieclicker.utility.UtilsCookie;
import org.bukkit.Bukkit;

import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

@Singleton
public class Recipes {
    public List<ShapedRecipe> existingRecipes = new ArrayList<>();
    private final ItemManager manager;
    private final UtilsCookie utilsCookie;
    Map<List<ItemStack>, CustomRecipe> recipesToCheck = new HashMap<>();

    @Inject
    public Recipes (ItemManager manager, UtilsCookie utilsCookie) {
        this.utilsCookie = utilsCookie;
        this.manager = manager;
    }

    public void addRecipes() {

        CustomRecipe bread = new CustomRecipe(manager.get("bread"));
        bread.setIngredient(0, manager.get("ench_wheat"), 1);
        bread.setIngredient(1, manager.get("ench_wheat"), 1);
        bread.setIngredient(2, manager.get("ench_wheat"), 1);
        registerRecipe(bread);

        CustomRecipe test = new CustomRecipe(manager.get("baguette"));
        test.setIngredient(0, manager.get("bread"), 1);
        test.setIngredient(1, manager.get("bread"), 1);
        test.setIngredient(2, manager.get("bread"), 1);
        registerRecipe(test);

        CustomRecipe epic_hoe = new CustomRecipe(manager.get("epic_hoe"));
        epic_hoe.setIngredient(0, manager.get("rose_bush"), 1);
        epic_hoe.setIngredient(1, manager.get("ench_cookie"), 20);
        epic_hoe.setIngredient(2, manager.get("ench_cocoa"), 64);
        epic_hoe.setIngredient(3, manager.get("ench_cookie"), 20);
        epic_hoe.setIngredient(4, manager.get("wood_hoe"), 1);
        epic_hoe.setIngredient(5, manager.get("ench_cookie"), 20);
        epic_hoe.setIngredient(6, manager.get("berries"), 64);
        epic_hoe.setIngredient(7, manager.get("ench_cookie"), 20);
        epic_hoe.setIngredient(8, manager.get("baguette"), 64);
        registerRecipe(epic_hoe);

        CustomRecipe legendary_hoe = new CustomRecipe(manager.get("leg_hoe"));
        legendary_hoe.setIngredient(0, manager.get("ench_cookie"), 64);
        legendary_hoe.setIngredient(1, manager.get("wood_hoe"), 1);
        legendary_hoe.setIngredient(2, manager.get("berries"), 64);
        legendary_hoe.setIngredient(3, manager.get("ench_cocoa"), 64);
        legendary_hoe.setIngredient(4, manager.get("epic_hoe"), 1);
        legendary_hoe.setIngredient(5, manager.get("baguette"), 32);
        legendary_hoe.setIngredient(6, manager.get("glow_berries"), 64);
        legendary_hoe.setIngredient(7, manager.get("pie"), 64);
        legendary_hoe.setIngredient(8, manager.get("pumpkin"), 64);
        registerRecipe(legendary_hoe);

        //Bukkit.updateRecipes();
    }

    public List<ItemStack> listWithItemStacks(List<ItemStack> legacyList) {
        List<ItemStack> itmList = new ArrayList<>(), airList = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            ItemStack item = legacyList.get(i);
            if (item.getType().equals(Material.AIR) && !(itmList.isEmpty())) {
                airList.add(new ItemStack(Material.AIR));
                continue;
            }
            if (item.getType().equals(Material.AIR)) continue;
            itmList.addAll(airList);
            itmList.add(utilsCookie.createItemAmount(item, 1));
            airList.clear();
            if (List.of(2, 5).contains(i) && !(legacyList.get(i+1).getType().equals(Material.AIR)))
                itmList.add(new ItemStack(Material.STRUCTURE_VOID));

        }
        return itmList;
    }

    public Map<List<ItemStack>, CustomRecipe> getAllRecipes() {
        return recipesToCheck;
    }

    public void registerRecipe(CustomRecipe recipe) {
        recipesToCheck.put(listWithItemStacks(recipe.getAllIngredients()), recipe);
    }
    public boolean checkForRecipe(List<ItemStack> rec1) {
        if (!(getAllRecipes().containsKey(listWithItemStacks(rec1)))) return false;
        for (Map.Entry<ItemStack, ItemStack> i : combineListsToListOfMap(rec1, getAllRecipes().get(listWithItemStacks(rec1)).getAllIngredients()))
            if (i.getKey().getAmount() < i.getValue().getAmount()) return false;
        return true;

    }
    public CustomRecipe findRecipe(List<ItemStack> rec1) {
        if (checkForRecipe(rec1))
            return getAllRecipes().get(listWithItemStacks(rec1));
        return null;
    }

    private List<ItemStack> makeClearList(List<ItemStack> list1) {
        List<ItemStack> itemStackList = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            ItemStack ingredient = list1.get(i);
            if (!(ingredient.getType().equals(Material.AIR))) itemStackList.add(ingredient);
        }
        return itemStackList;
    }

    public List<Map.Entry<ItemStack, ItemStack>> combineListsToListOfMap(List<ItemStack> list1, List<ItemStack> list2) {
        List<Map.Entry<ItemStack, ItemStack>> combined = new ArrayList<>();
        List<ItemStack> clear1 = makeClearList(list1), clear2 = makeClearList(list2);
        for (int i = 0; i < clear1.size(); i++)
            combined.add(Map.entry(clear1.get(i), clear2.get(i)));
        return combined;
    }

}
