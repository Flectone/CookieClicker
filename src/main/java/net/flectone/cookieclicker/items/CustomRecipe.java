package net.flectone.cookieclicker.items;

import com.google.inject.Singleton;
import lombok.Getter;
import net.flectone.cookieclicker.items.itemstacks.base.data.ItemTag;
import net.flectone.cookieclicker.utility.Pair;

import java.util.ArrayList;
import java.util.List;
@Singleton
public class CustomRecipe {
    @Getter
    private final ItemTag resultTag;
    private final List<Pair<ItemTag, Integer>> ingredients = new ArrayList<>(9);

    public CustomRecipe(ItemTag resultItemTag) {
        resultTag = resultItemTag;
        for (int i = 0; i < 9; i++)
            ingredients.add(new Pair<>(ItemTag.AIR, 0));
    }
    public void setIngredient(Integer slot, ItemTag itemTag, int amount) {
        ingredients.set(slot, new Pair<>(itemTag, amount));
    }

    public List<Pair<ItemTag, Integer>> getAllIngredients() {
        return ingredients;
    }
}
