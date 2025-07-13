package net.flectone.cookieclicker.items;

import com.google.inject.Singleton;
import net.flectone.cookieclicker.items.itemstacks.base.data.ItemTag;
import net.flectone.cookieclicker.utility.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class Recipes {
    private final Map<String, CustomRecipe> recipesToCheck = new HashMap<>();

    public void addRecipes() {

        CustomRecipe bread = new CustomRecipe(ItemTag.BREAD);
        bread.setIngredient(0, ItemTag.ENCHANTED_WHEAT, 1);
        bread.setIngredient(1, ItemTag.ENCHANTED_WHEAT, 1);
        bread.setIngredient(2, ItemTag.ENCHANTED_WHEAT, 1);
        registerRecipe(bread);

        CustomRecipe baguette = new CustomRecipe(ItemTag.BAGUETTE);
        baguette.setIngredient(0, ItemTag.BREAD, 1);
        baguette.setIngredient(1, ItemTag.BREAD, 1);
        baguette.setIngredient(2, ItemTag.BREAD, 1);
        registerRecipe(baguette);

        CustomRecipe epicHoe = new CustomRecipe(ItemTag.EPIC_HOE);
        epicHoe.setIngredient(0, ItemTag.ROSE_BUSH_HOE, 1);
        epicHoe.setIngredient(1, ItemTag.ENCHANTED_COOKIE, 20);
        epicHoe.setIngredient(2, ItemTag.ENCHANTED_COCOA_BEANS, 64);
        epicHoe.setIngredient(3, ItemTag.ENCHANTED_COOKIE, 20);
        epicHoe.setIngredient(4, ItemTag.WOODEN_HOE, 1);
        epicHoe.setIngredient(5, ItemTag.ENCHANTED_COOKIE, 20);
        epicHoe.setIngredient(6, ItemTag.SWEET_BERRIES, 64);
        epicHoe.setIngredient(7, ItemTag.ENCHANTED_COOKIE, 20);
        epicHoe.setIngredient(8, ItemTag.BAGUETTE, 64);
        registerRecipe(epicHoe);

        CustomRecipe legendaryHoe = new CustomRecipe(ItemTag.LEGENDARY_HOE);
        legendaryHoe.setIngredient(0, ItemTag.ENCHANTED_COOKIE, 64);
        legendaryHoe.setIngredient(1, ItemTag.CAKE_UPGRADE_ITEM, 1);
        legendaryHoe.setIngredient(2, ItemTag.SWEET_BERRIES, 64);
        legendaryHoe.setIngredient(3, ItemTag.ENCHANTED_COCOA_BEANS, 64);
        legendaryHoe.setIngredient(4, ItemTag.EPIC_HOE, 1);
        legendaryHoe.setIngredient(5, ItemTag.BAGUETTE, 32);
        legendaryHoe.setIngredient(6, ItemTag.GLOW_BERRIES, 64);
        legendaryHoe.setIngredient(7, ItemTag.PUMPKIN_PIE, 64);
        legendaryHoe.setIngredient(8, ItemTag.PUMPKIN, 64);
        registerRecipe(legendaryHoe);

        CustomRecipe piston = new CustomRecipe(ItemTag.COOKIE_CRAFTER);
        piston.setIngredient(0, ItemTag.ENCHANTED_COOKIE, 64);
        piston.setIngredient(1, ItemTag.ENCHANTED_WHEAT, 99);
        piston.setIngredient(2, ItemTag.ENCHANTED_COOKIE, 64);
        piston.setIngredient(3, ItemTag.ENCHANTED_COOKIE, 64);
        piston.setIngredient(4, ItemTag.STONE_HOE, 1);
        piston.setIngredient(5, ItemTag.ENCHANTED_COOKIE, 64);
        piston.setIngredient(6, ItemTag.ENCHANTED_COOKIE, 64);
        piston.setIngredient(7, ItemTag.CHOCOLATE, 64);
        piston.setIngredient(8, ItemTag.ENCHANTED_COOKIE, 64);
        registerRecipe(piston);

        CustomRecipe cake = new CustomRecipe(ItemTag.CAKE_UPGRADE_ITEM);
        cake.setIngredient(0, ItemTag.GLOW_BERRIES, 64);
        cake.setIngredient(1, ItemTag.PUMPKIN_PIE, 64);
        cake.setIngredient(2, ItemTag.PUMPKIN, 64);
        cake.setIngredient(3, ItemTag.ENCHANTED_COOKIE, 64);
        cake.setIngredient(4, ItemTag.CHOCOLATE, 64);
        cake.setIngredient(5, ItemTag.ENCHANTED_COOKIE, 64);
        cake.setIngredient(6, ItemTag.BLOCK_OF_COOKIE, 1);
        cake.setIngredient(7, ItemTag.BLOCK_OF_COOKIE, 1);
        cake.setIngredient(8, ItemTag.BLOCK_OF_COOKIE, 1);
        registerRecipe(cake);
    }

    public String convertToRecipeKey(List<Pair<ItemTag, Integer>> ingredients) {
        StringBuilder key = new StringBuilder();
        ItemTag itemTag;
        int airCount = 0;

        for (int i = 0; i < 9; i++) {
            itemTag = ingredients.get(i).left();

            //Если предмета нет, то мы пропускаем, если ключ (из тегов) пустой,
            //а если ключ уже содержит что-то, то мы сохраняем количество пустых слотов,
            //пока не найдём предмет
            if (itemTag == ItemTag.AIR) {
                airCount = key.isEmpty() ? airCount : airCount + 1;
                continue;
            }

            //Нашли предмет, теперь добавляем все предметы "воздух" в ключ, которые были до этого предмета
            if (airCount > 0) {
                key.append("air".repeat(airCount));
                airCount = 0;
            }
            //Добавляем тег предмета в ключ
            key.append(itemTag.getRealTag());

            //Если этот предмет находится на краю линии (3-й или 6-й слот в верстаке)
            //и следующий предмет не воздух, то в ключ добавляется "конец линии"
            if ((i == 2 || i == 5) && ingredients.get(i + 1).left() != ItemTag.AIR) {
                key.append("EndOfLine");
            }
        }
        return key.toString();
    }

    public CustomRecipe getRecipeIfExists(List<Pair<ItemTag, Integer>> craftingTableSlots) {
        String key = convertToRecipeKey(craftingTableSlots);
        if (recipesToCheck.isEmpty() || !recipesToCheck.containsKey(key))
            return null;

        return recipesToCheck.get(key);
    }

    public List<Integer> getAmountsList(List<Pair<ItemTag, Integer>> slots) {
        List<Integer> amountList = new ArrayList<>();
        slots.forEach(slot -> {
            if (slot.left() != ItemTag.AIR) {
                amountList.add(slot.right());
            }
        });
        return amountList;
    }

    public List<Pair<Integer, Integer>> createListOfAmounts(List<Pair<ItemTag, Integer>> craftingSlots, CustomRecipe recipe) {
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
