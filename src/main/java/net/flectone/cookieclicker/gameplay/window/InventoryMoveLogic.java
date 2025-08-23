package net.flectone.cookieclicker.gameplay.window;

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.entities.playerdata.ServerCookiePlayer;
import net.flectone.cookieclicker.inventories.ContainerManager;
import net.flectone.cookieclicker.inventories.containers.ItemStorage;
import net.flectone.cookieclicker.items.attributes.ToolType;
import net.flectone.cookieclicker.items.itemstacks.base.data.Features;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;

@Singleton
public class InventoryMoveLogic {

    private final ContainerManager containerManager;

    @Inject
    public InventoryMoveLogic(ContainerManager containerManager) {
        this.containerManager = containerManager;
    }

    public void applyContentsToItem(ItemStack itemStack, ItemStorage container) {
        ItemContainerContents containerContents = ItemContainerContents.fromItems(container.getContainerItems());

        itemStack.applyComponents(DataComponentPatch.builder()
                .set(DataComponents.CONTAINER, containerContents)
                .build());
    }

    public void placeItems(ServerCookiePlayer serverCookiePlayer, int slot,
                           WrapperPlayClientClickWindow.WindowClickType windowClickType, int button) {
        if (!(containerManager.getOpenedContainer(serverCookiePlayer) instanceof ItemStorage container)) {
            return;
        }

        Player player = serverCookiePlayer.getPlayer();
        boolean isPlayerInvClick = slot >= container.getContainerItems().size();
        //реальный слот, по которому кликнул игрок
        int workSlot = isPlayerInvClick ? getPlayerSlot(slot, container.getWindowType()) : slot;

        ItemStack carried = player.containerMenu.getCarried();
        ItemStack inSlotItem = isPlayerInvClick
                ? player.getInventory().getItem(workSlot)
                : container.getContainerItems().get(workSlot);

        // если игрок кликнул по backpack предмету, то отмена
        if (new Features(inSlotItem).getCategory() == ToolType.BACKPACK) {
            containerManager.updateWindow(serverCookiePlayer);
            return;
        }

        switch (windowClickType) {
            // лкм
            case WrapperPlayClientClickWindow.WindowClickType type when type == WrapperPlayClientClickWindow.WindowClickType.PICKUP
                    && button == 0 -> leftClick(serverCookiePlayer, carried, inSlotItem, workSlot, isPlayerInvClick);

            // пкм
            case WrapperPlayClientClickWindow.WindowClickType type when type == WrapperPlayClientClickWindow.WindowClickType.PICKUP
                    && button == 1 -> rightClick(serverCookiePlayer, carried, inSlotItem, workSlot, isPlayerInvClick);

            // шифт клик
            case QUICK_MOVE -> shiftClick(
                    serverCookiePlayer,
                    inSlotItem, workSlot,
                    isPlayerInvClick ? container : new ItemStorage(player.getInventory()),
                    isPlayerInvClick
            );

            default -> containerManager.updateWindow(serverCookiePlayer);
        }
    }

    private void leftClick(ServerCookiePlayer serverCookiePlayer, ItemStack cursorItem, ItemStack inSlot, int slot, boolean isPlayerInv) {
        //Если в слоте уже есть такой предмет
        //Надо объединять предметы в таком случае
        if (ItemStorage.hasRemainingSpaceForItem(inSlot, cursorItem)) {
            int carriedCount = cursorItem.getCount();
            int inSlotCountRemains = inSlot.getComponents().getOrDefault(DataComponents.MAX_STACK_SIZE, 1) - inSlot.getCount();

            inSlot.setCount(inSlot.getCount() + Math.min(inSlotCountRemains, carriedCount));
            cursorItem.setCount(Math.max(carriedCount - inSlotCountRemains, 0));
            moveBetweenSlotAndCursor(serverCookiePlayer, inSlot, cursorItem, slot, isPlayerInv);
        } else {
            moveBetweenSlotAndCursor(serverCookiePlayer, cursorItem, inSlot, slot, isPlayerInv);
        }
    }

    private void rightClick(ServerCookiePlayer serverCookiePlayer, ItemStack cursorItem, ItemStack inSlot, int slot, boolean isPlayerInv) {
        if (cursorItem.isEmpty()) { //Если курсор ничего не держит, то надо взять половину
            ItemStack newCursorItem = inSlot.copyWithCount(inSlot.getCount() - (inSlot.getCount() / 2));
            inSlot.setCount(inSlot.getCount() / 2);
            moveBetweenSlotAndCursor(serverCookiePlayer, inSlot, newCursorItem, slot, isPlayerInv);
        } else if (ItemStorage.hasRemainingSpaceForItem(inSlot, cursorItem) || inSlot.isEmpty()) { //Если слот содержит предмет или пустой, то положить 1
            ItemStack modifiedItem = cursorItem.copyWithCount(inSlot.getCount() + 1);
            cursorItem.setCount(cursorItem.getCount() - 1);
            moveBetweenSlotAndCursor(serverCookiePlayer, modifiedItem, cursorItem, slot, isPlayerInv);
        }
    }

    private void shiftClick(ServerCookiePlayer serverCookiePlayer, ItemStack movingItem,
                            int slot, ItemStorage container, boolean isFromPlayerInv) {
        int count = movingItem.getCount();
        int slotWithSpace = container.getSlotWithRemainingSpace(movingItem);

        while (slotWithSpace != -1 && count > 0) {
            count = modifySlotAndGetRemaining(serverCookiePlayer, container, slotWithSpace, count);
            slotWithSpace = container.getSlotWithRemainingSpace(movingItem);
        }

        if (count != 0) {
            moveBetweenSlots(serverCookiePlayer, movingItem.copyWithCount(count), slot,
                    isFromPlayerInv ? container.getFreeSlot() : serverCookiePlayer.getFreeSlot(),
                    isFromPlayerInv);
            return;
        }

        containerManager.setItem(serverCookiePlayer, ItemStack.EMPTY, slot, isFromPlayerInv);
    }

    private void moveBetweenSlotAndCursor(ServerCookiePlayer serverCookiePlayer, ItemStack carried,
                                          ItemStack inSlot, int slot, boolean isPlayerInv) {
        containerManager.setCursorItem(serverCookiePlayer, inSlot);

        containerManager.setItem(serverCookiePlayer, carried, slot, isPlayerInv);
    }

    private void moveBetweenSlots(ServerCookiePlayer serverCookiePlayer, ItemStack itemStack, int slotFrom,
                                  int slotTo, boolean isFromPlayerInv) {
        if (slotTo == -1)
            return;

        if (isFromPlayerInv) { //Шифт клик, из инвентаря игрока
            containerManager.setPlayerInvItem(serverCookiePlayer, slotFrom, new ItemStack(Items.AIR));
            containerManager.setContainerItem(serverCookiePlayer, slotTo, itemStack);
        } else { //Шифт клик, из инвентаря хранилища какого-то
            containerManager.setPlayerInvItem(serverCookiePlayer, slotTo, itemStack);
            containerManager.setContainerItem(serverCookiePlayer, slotFrom, new ItemStack(Items.AIR));
        }
    }

    private int modifySlotAndGetRemaining(ServerCookiePlayer serverCookiePlayer, ItemStorage container, int slot, int countToAdd) {
        ItemStack item = container.getContainerItems().get(slot);
        int fullCount = item.getCount() + countToAdd;

        item.setCount(Math.min(item.getMaxStackSize(), fullCount));
        if (container.isPlayerInv()) {
            containerManager.setPlayerInvItem(serverCookiePlayer, getPlayerSlot(slot, 6), item);
        } else {
            containerManager.setContainerItem(serverCookiePlayer, slot, item);
        }
        return Math.max(0, fullCount - item.getMaxStackSize());
    }

    private int getPlayerSlot(int slot, int inventoryType) {
        //от 0 до 5 это инвентари, как сундук короче
        int containerSlots = inventoryType <= 5 ? 9 * (inventoryType + 1) : 0;

        int invSlot = slot - containerSlots;
        //Разрабы для удобства поменяли нумерацию слотов в инвентаре.
        //От 0 до 9 это хотбар (0 слот это самый левый).
        //От 9 до 36 это слоты инвентаря (9 слот это самый левый сверху)
        return invSlot <= 26 ? invSlot + 9 : invSlot - 27;
    }

}
