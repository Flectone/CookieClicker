package net.flectone.cookieclicker.inventories.containers;

import net.flectone.cookieclicker.items.ItemsRegistry;
import net.flectone.cookieclicker.items.attributes.ToolType;
import net.flectone.cookieclicker.items.itemstacks.base.data.ItemTag;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;

public class ItemViewContainer extends ClickerContainer {

    private int lastSlot = 9;

    public ItemViewContainer(String customData) {
        super(generateId(), 3, customData);
        for (int i = 0; i < 8; i++) {
            this.setItem(i, fillerItem);
        }
        this.setItem(8, closeItem);
    }

    public void addCategory(ToolType category, ItemsRegistry itemsRegistry) {
        Arrays.stream(ItemTag.values())
                .filter(itemTag -> itemTag.getCategory() == category)
                .forEach(tag -> {
                    containerItems.set(lastSlot, itemsRegistry.get(tag));
                    lastSlot++;
                });
    }

    public ItemStack getItem(int slot) {
        return containerItems.get(slot);
    }
}
