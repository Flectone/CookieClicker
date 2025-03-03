package net.flectone.cookieclicker.items;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.utility.ItemTagsUtility;
import net.flectone.cookieclicker.utility.Pair;
import net.flectone.cookieclicker.utility.UtilsCookie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.ShapedRecipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class Recipes {
    public List<ShapedRecipe> existingRecipes = new ArrayList<>();
    private final ItemManager manager;
    private final UtilsCookie utilsCookie;
    private final ItemTagsUtility itemTagsUtility;

    Map<String, CustomRecipe> recipesToCheck = new HashMap<>();

    @Inject
    public Recipes (ItemManager manager, UtilsCookie utilsCookie, ItemTagsUtility itemTagsUtility) {
        this.utilsCookie = utilsCookie;
        this.manager = manager;
        this.itemTagsUtility = itemTagsUtility;
    }

    public void addRecipes() {

        CustomRecipe bread = new CustomRecipe(manager.getNMS("bread"));
        bread.setIngredient(0, manager.getNMS("ench_wheat"), 1);
        bread.setIngredient(1, manager.getNMS("ench_wheat"), 1);
        bread.setIngredient(2, manager.getNMS("ench_wheat"), 1);
        registerRecipe(bread);

        CustomRecipe test = new CustomRecipe(manager.getNMS("baguette"));
        test.setIngredient(0, manager.getNMS("bread"), 1);
        test.setIngredient(1, manager.getNMS("bread"), 1);
        test.setIngredient(2, manager.getNMS("bread"), 1);
        registerRecipe(test);

        CustomRecipe epic_hoe = new CustomRecipe(manager.getNMS("epic_hoe"));
        epic_hoe.setIngredient(0, manager.getNMS("rose_bush"), 1);
        epic_hoe.setIngredient(1, manager.getNMS("ench_cookie"), 20);
        epic_hoe.setIngredient(2, manager.getNMS("ench_cocoa"), 64);
        epic_hoe.setIngredient(3, manager.getNMS("ench_cookie"), 20);
        epic_hoe.setIngredient(4, manager.getNMS("wood_hoe"), 1);
        epic_hoe.setIngredient(5, manager.getNMS("ench_cookie"), 20);
        epic_hoe.setIngredient(6, manager.getNMS("berries"), 64);
        epic_hoe.setIngredient(7, manager.getNMS("ench_cookie"), 20);
        epic_hoe.setIngredient(8, manager.getNMS("baguette"), 64);
        registerRecipe(epic_hoe);

        CustomRecipe legendary_hoe = new CustomRecipe(manager.getNMS("leg_hoe"));
        legendary_hoe.setIngredient(0, manager.getNMS("ench_cookie"), 64);
        legendary_hoe.setIngredient(1, manager.getNMS("final_cake"), 1);
        legendary_hoe.setIngredient(2, manager.getNMS("berries"), 64);
        legendary_hoe.setIngredient(3, manager.getNMS("ench_cocoa"), 64);
        legendary_hoe.setIngredient(4, manager.getNMS("epic_hoe"), 1);
        legendary_hoe.setIngredient(5, manager.getNMS("baguette"), 32);
        legendary_hoe.setIngredient(6, manager.getNMS("glow_berries"), 64);
        legendary_hoe.setIngredient(7, manager.getNMS("pie"), 64);
        legendary_hoe.setIngredient(8, manager.getNMS("pumpkin"), 64);
        registerRecipe(legendary_hoe);

        CustomRecipe piston = new CustomRecipe(manager.getNMS("cookie_crafter"));
        piston.setIngredient(0, manager.getNMS("ench_cookie"), 64);
        piston.setIngredient(1, manager.getNMS("ench_wheat"), 99);
        piston.setIngredient(2, manager.getNMS("ench_cookie"), 64);
        piston.setIngredient(3, manager.getNMS("ench_cookie"), 64);
        piston.setIngredient(4, manager.getNMS("stone_hoe"), 1);
        piston.setIngredient(5, manager.getNMS("ench_cookie"), 64);
        piston.setIngredient(6, manager.getNMS("ench_cookie"), 64);
        piston.setIngredient(7, manager.getNMS("chocolate"), 64);
        piston.setIngredient(8, manager.getNMS("ench_cookie"), 64);
        registerRecipe(piston);

        CustomRecipe cake = new CustomRecipe(manager.getNMS("final_cake"));
        cake.setIngredient(0, manager.getNMS("glow_berries"), 64);
        cake.setIngredient(1, manager.getNMS("pie"), 64);
        cake.setIngredient(2, manager.getNMS("pumpkin"), 64);
        cake.setIngredient(3, manager.getNMS("ench_cookie"), 64);
        cake.setIngredient(4, manager.getNMS("chocolate"), 64);
        cake.setIngredient(5, manager.getNMS("ench_cookie"), 64);
        cake.setIngredient(6, manager.getNMS("cookie_block"), 1);
        cake.setIngredient(7, manager.getNMS("cookie_block"), 1);
        cake.setIngredient(8, manager.getNMS("cookie_block"), 1);
        registerRecipe(cake);

        //Bukkit.updateRecipes();
    }

//    public List<ItemStack> listWithItemStacks(List<ItemStack> legacyList) {
//        List<ItemStack> itmList = new ArrayList<>();
//        List<ItemStack> airList = new ArrayList<>();
//
//        for (int i = 0; i < 9; i++) {
//            ItemStack item = legacyList.get(i);
//            if (item.getItem().equals(Items.AIR) && !(itmList.isEmpty())) {
//                airList.add(new ItemStack(Items.AIR));
//                continue;
//            }
//            if (item.getItem().equals(Items.AIR)) continue;
//            itmList.addAll(airList);
//            itmList.add(utilsCookie.createItemAmountNMS(item, 1));
//            airList.clear();
//            if (List.of(2, 5).contains(i) && !(legacyList.get(i+1).getItem().equals(Items.AIR)))
//                itmList.add(new ItemStack(Items.STRUCTURE_VOID));
//
//        }
//        return itmList;
//    }

    public String convertToRecipeString(List<ItemStack> legacyList) {
        StringBuilder itmList = new StringBuilder();
        StringBuilder airList = new StringBuilder();

        for (int i = 0; i < 9; i++) {
            ItemStack item = legacyList.get(i);
            if (item.getItem().equals(Items.AIR) && !(itmList.isEmpty())) {
                airList.append("air");
                continue;
            }
            if (item.getItem().equals(Items.AIR)) continue;
            itmList.append(airList);
            itmList.append(itemTagsUtility.getItemTag(item));
            airList = new StringBuilder();
            if (List.of(2, 5).contains(i) && !(legacyList.get(i+1).getItem().equals(Items.AIR)))
                itmList.append("void");

        }
        return itmList.toString();
    }

    public Map<String, CustomRecipe> getAllRecipes() {
        return recipesToCheck;
    }

    public void registerRecipe(CustomRecipe recipe) {
        recipesToCheck.put(convertToRecipeString(recipe.getAllIngredients()), recipe);
    }

    public boolean checkForRecipe(List<ItemStack> rec1) {
        if (!(getAllRecipes().containsKey(convertToRecipeString(rec1)))) return false;
        for (Pair<Integer, Integer> pair : getAmountPairs(rec1, getAllRecipes().get(convertToRecipeString(rec1)).getAllIngredients()))
            if (pair.getKey() < pair.getValue()) {
                return false;
            }
        return true;

    }

    public CustomRecipe findRecipe(List<ItemStack> rec1) {
        if (checkForRecipe(rec1))
            return getAllRecipes().get(convertToRecipeString(rec1));
        return null;
    }

    //кринж какой-то
    public List<ItemStack> makeCleanList(List<ItemStack> list1) {
        List<ItemStack> itemStackList = new ArrayList<>();
        for (ItemStack item : list1) {
            if (item.getItem().equals(Items.AIR))
                continue;
            itemStackList.add(item);
        }
        return itemStackList;
    }

    public List<Pair<Integer, Integer>> getAmountPairs(List<ItemStack> list1, List<ItemStack> list2) {
        List<Pair<Integer, Integer>> combined = new ArrayList<>();
        List<ItemStack> clearList1 = makeCleanList(list1);
        List<ItemStack> clearList2 = makeCleanList(list2);

        for (int i = 0; i < clearList1.size(); i++)
            combined.add(new Pair<>(clearList1.get(i).getCount(), clearList2.get(i).getCount()));
        return combined;
    }

}
