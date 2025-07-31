package net.flectone.cookieclicker.inventories;

import lombok.Getter;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

import java.util.Optional;
import java.util.stream.Stream;

@Getter
public class ItemStorage extends ClickerContainer {
    public static final String STORAGE_CONTAINER = "shulker";
    private ItemStack boundItem;
    private boolean playerInv = false;

    public ItemStorage(ItemContainerContents containerContents, int type, ItemStack boundItem) {
        super(generateId(), type, STORAGE_CONTAINER);
        this.boundItem = boundItem;
        NonNullList<ItemStack> items = containerContents.items;
        for (int i = 0; i < items.size(); i++) {
            containerItems.set(i, items.get(i));
        }
    }

    public ItemStorage(Inventory playerInventory) {
        super(generateId(), 3, "player_inventory");
        int j = 0;
        playerInv = true;

        for (int i = 9; i < 36; i++) {
            containerItems.set(j, playerInventory.getItem(i));
            j++;
        }

        for (int i = 0; i < 9; i++) {
            containerItems.set(j, playerInventory.getItem(i));
            j++;
        }
    }

    public int getFreeSlot() {
        for (int i = 0; i < containerItems.size(); i++) {
            if (containerItems.get(i).isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    public static boolean hasRemainingSpaceForItem(ItemStack destination, ItemStack origin) {
        return !destination.isEmpty()
                && destination.isStackable()
                && destination.getCount() < destination.getComponents().getOrDefault(DataComponents.MAX_STACK_SIZE, 1)
                && ItemStack.isSameItemSameComponents(destination, origin);
    }

    public int getSlotWithRemainingSpace(ItemStack stack) {
        Stream<ItemStack> stream = containerItems.stream().filter(itemStack -> hasRemainingSpaceForItem(itemStack, stack));
        Optional<ItemStack> optional = isPlayerInv()
                ? stream.reduce((first, second) -> second)
                : stream.findFirst();

        return optional.map(containerItems::indexOf).orElse(-1);
    }


}
