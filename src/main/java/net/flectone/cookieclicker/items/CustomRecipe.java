package net.flectone.cookieclicker.items;

import com.google.inject.Singleton;
import lombok.Getter;
import net.flectone.cookieclicker.utility.Pair;

import java.util.ArrayList;
import java.util.List;
@Singleton
public class CustomRecipe {
    @Getter
    private final String resultTag;
    private final List<Pair<String, Integer>> ingredients = new ArrayList<>(9);

    public CustomRecipe(String resultItemTag) {
        resultTag = resultItemTag;
        for (int i = 0; i < 9; i++)
            ingredients.add(new Pair<>("air", 0));
    }
    public void setIngredient(Integer slot, String itemTag, int amount) {
        ingredients.set(slot, new Pair<>(itemTag, amount));
    }

    public List<Pair<String, Integer>> getAllIngredients() {
        return ingredients;
    }
}
