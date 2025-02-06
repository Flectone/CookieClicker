package net.flectone.cookieclicker.inventories.crafting;

import com.google.inject.Singleton;
import lombok.Getter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
@Singleton
public class CustomRecipe {
    @Getter
    private final ItemStack result;
    private final List<ItemStack> ingredients = new ArrayList<>(9);

    public CustomRecipe(ItemStack resultItem) {
        result = resultItem;
        for (int i = 0; i < 9; i++)
            ingredients.add(new ItemStack(Items.AIR));
    }
    public void setIngredient(Integer slot, ItemStack itemStack, int amount) {
        ItemStack ingredient = itemStack.copy();
        ingredient.setCount(amount);
        ingredients.set(slot, ingredient);
    }

    public List<ItemStack> getAllIngredients() {
        return ingredients;
    }
}
