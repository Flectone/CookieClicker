package net.flectone.cookieclicker.items;

import io.papermc.paper.configuration.type.fallback.FallbackValue;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CustomRecipe {
    public
    ItemStack result;
    List<ItemStack> ingredients = new ArrayList<>(9);

    public CustomRecipe(ItemStack resultItem) {
        this.result = resultItem;
        for (int i = 0; i < 9; i++)
            ingredients.add(new ItemStack(Material.AIR));
    }
    public void setIngredient(Integer slot, ItemStack itemStack, int amount) {
        ItemStack ingredient = new ItemStack(itemStack);
        ingredient.setAmount(amount);
        ingredients.set(slot, ingredient);
    }

    public List<ItemStack> getAllIngredients() {
        return ingredients;
    }

    public ItemStack getResult() {
        return result;
    }
}
