package net.flectone.cookieclicker.items;

import com.google.inject.Singleton;
import net.flectone.cookieclicker.utility.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class Recipes {
    private final Map<String, CustomRecipe> recipesToCheck = new HashMap<>();

    public void addRecipes() {

        CustomRecipe bread = new CustomRecipe("bread");
        bread.setIngredient(0, "ench_wheat", 1);
        bread.setIngredient(1, "ench_wheat", 1);
        bread.setIngredient(2, "ench_wheat", 1);
        registerRecipe(bread);

        CustomRecipe baguette = new CustomRecipe("baguette");
        baguette.setIngredient(0, "bread", 1);
        baguette.setIngredient(1, "bread", 1);
        baguette.setIngredient(2, "bread", 1);
        registerRecipe(baguette);

        CustomRecipe epicHoe = new CustomRecipe("epic_hoe");
        epicHoe.setIngredient(0, "rose_bush", 1);
        epicHoe.setIngredient(1, "ench_cookie", 20);
        epicHoe.setIngredient(2, "ench_cocoa", 64);
        epicHoe.setIngredient(3, "ench_cookie", 20);
        epicHoe.setIngredient(4, "wood_hoe", 1);
        epicHoe.setIngredient(5, "ench_cookie", 20);
        epicHoe.setIngredient(6, "berries", 64);
        epicHoe.setIngredient(7, "ench_cookie", 20);
        epicHoe.setIngredient(8, "baguette", 64);
        registerRecipe(epicHoe);

        CustomRecipe legendaryHoe = new CustomRecipe("leg_hoe");
        legendaryHoe.setIngredient(0, "ench_cookie", 64);
        legendaryHoe.setIngredient(1, "final_cake", 1);
        legendaryHoe.setIngredient(2, "berries", 64);
        legendaryHoe.setIngredient(3, "ench_cocoa", 64);
        legendaryHoe.setIngredient(4, "epic_hoe", 1);
        legendaryHoe.setIngredient(5, "baguette", 32);
        legendaryHoe.setIngredient(6, "glow_berries", 64);
        legendaryHoe.setIngredient(7, "pie", 64);
        legendaryHoe.setIngredient(8, "pumpkin", 64);
        registerRecipe(legendaryHoe);

        CustomRecipe piston = new CustomRecipe("cookie_crafter");
        piston.setIngredient(0, "ench_cookie", 64);
        piston.setIngredient(1, "ench_wheat", 99);
        piston.setIngredient(2, "ench_cookie", 64);
        piston.setIngredient(3, "ench_cookie", 64);
        piston.setIngredient(4, "stone_hoe", 1);
        piston.setIngredient(5, "ench_cookie", 64);
        piston.setIngredient(6, "ench_cookie", 64);
        piston.setIngredient(7, "chocolate", 64);
        piston.setIngredient(8, "ench_cookie", 64);
        registerRecipe(piston);

        CustomRecipe cake = new CustomRecipe("final_cake");
        cake.setIngredient(0, "glow_berries", 64);
        cake.setIngredient(1, "pie", 64);
        cake.setIngredient(2, "pumpkin", 64);
        cake.setIngredient(3, "ench_cookie", 64);
        cake.setIngredient(4, "chocolate", 64);
        cake.setIngredient(5, "ench_cookie", 64);
        cake.setIngredient(6, "cookie_block", 1);
        cake.setIngredient(7, "cookie_block", 1);
        cake.setIngredient(8, "cookie_block", 1);
        registerRecipe(cake);

        //Bukkit.updateRecipes();
    }

    public String convertToRecipeKey(List<Pair<String, Integer>> ingredients) {
        StringBuilder key = new StringBuilder();
        String itemTag;
        int airCount = 0;

        for (int i = 0; i < 9; i++) {
            itemTag = ingredients.get(i).left();

            //Если предмета нет, то мы пропускаем, если ключ (из тегов) пустой,
            //а если ключ уже содержит что-то, то мы сохраняем количество пустых слотов,
            //пока не найдём предмет
            if (itemTag.equals("air")) {
                airCount = key.isEmpty() ? airCount : airCount + 1;
                continue;
            }

            //Нашли предмет, теперь добавляем все предметы "воздух" в ключ, которые были до этого предмета
            if (airCount > 0) {
                key.append("air".repeat(airCount));
                airCount = 0;
            }
            //Добавляем тег предмета в ключ
            key.append(itemTag);

            //Если этот предмет находится на краю линии (3-й или 6-й слот в верстаке)
            //и следующий предмет не воздух, то в ключ добавляется "конец линии"
            if ((i == 2 || i == 5) && !ingredients.get(i + 1).left().equals("air")) {
                key.append("EndOfLine");
            }
        }
        return key.toString();
    }

    public CustomRecipe getRecipeIfExists(List<Pair<String, Integer>> craftingTableSlots) {
        String key = convertToRecipeKey(craftingTableSlots);
        if (recipesToCheck.isEmpty() || !recipesToCheck.containsKey(key))
            return null;

        return recipesToCheck.get(key);
    }

    public List<Integer> getAmountsList(List<Pair<String, Integer>> slots) {
        List<Integer> amountList = new ArrayList<>();
        slots.forEach(slot -> {
            if (!slot.left().equals("air")) {
                amountList.add(slot.right());
            }
        });
        return amountList;
    }

    public List<Pair<Integer, Integer>> createListOfAmounts(List<Pair<String, Integer>> craftingSlots, CustomRecipe recipe) {
        List<Integer> craftingInventory = getAmountsList(craftingSlots);
        List<Integer> recipeSlots = getAmountsList(recipe.getAllIngredients());

        //Слева количество в верстаке, справа - в рецепте
        List<Pair<Integer, Integer>> list = new ArrayList<>();

        if (craftingInventory.size() != recipeSlots.size())
            return list;

        for (int i = 0; i < recipeSlots.size(); i++) {
            list.add(new Pair<>(craftingInventory.get(i), recipeSlots.get(i)));
        }
        return list;

    }

    public Map<String, CustomRecipe> getAllRecipes() {
        return recipesToCheck;
    }

    private void registerRecipe(CustomRecipe recipe) {
        recipesToCheck.put(convertToRecipeKey(recipe.getAllIngredients()), recipe);
    }
}
