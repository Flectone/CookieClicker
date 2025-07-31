package net.flectone.cookieclicker.events;

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.inventories.*;
import net.flectone.cookieclicker.items.attributes.ToolType;
import net.flectone.cookieclicker.items.itemstacks.base.data.Features;
import net.flectone.cookieclicker.playerdata.ServerCookiePlayer;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;

@Singleton
public class PacketClickInventoryEvent {
    private static final int ANVIL_WINDOW_TYPE = 8;
    private static final int CRAFTING_TABLE_WINDOW_TYPE = 12;

    private final ContainerManager containerManager;
    private final MainMenu mainMenu;
    private final Shops shops;
    private final AnvilEvent anvilEvent;
    private final PacketCraftingEvent packetCraftingEvent;

    @Inject
    public PacketClickInventoryEvent(ContainerManager containerManager, MainMenu mainMenu, Shops shops, AnvilEvent anvilEvent,
                                     PacketCraftingEvent packetCraftingEvent) {
        this.anvilEvent = anvilEvent;
        this.packetCraftingEvent = packetCraftingEvent;
        this.shops = shops;
        this.mainMenu = mainMenu;
        this.containerManager = containerManager;
    }

    private void manageContainers(ServerCookiePlayer serverCookiePlayer, ClickerContainer container, WrapperPlayClientClickWindow clickPacket) {
        switch (container.getCustomData()) {
            //покупка предмета
            case "trading_farm", "trading_armorer" -> shops.buyItem(serverCookiePlayer, clickPacket);
            //главное меню, выбор
            case "main_menu" ->  {
                selectMainMenu(serverCookiePlayer, clickPacket.getSlot());
                containerManager.cancelClick(serverCookiePlayer);
            }
            //возврат назад в меню выбора рецепта
            case "recipe" -> {
                containerManager.cancelClick(serverCookiePlayer);
                if (clickPacket.getSlot() == 8) {
                    mainMenu.openAllRecipes(serverCookiePlayer);
                }
            }
            //выбор рецепта
            case "all_recipes" -> mainMenu.selectRecipe(serverCookiePlayer, clickPacket.getSlot(), clickPacket.getWindowClickType());
            //выбор предмета во всех предметах
            case "all_items" -> mainMenu.getItemInMenu(clickPacket.getSlot(), serverCookiePlayer, clickPacket.getWindowClickType());
            default -> placeItems(serverCookiePlayer, clickPacket.getSlot(),
                    clickPacket.getWindowClickType(), clickPacket.getButton());
        }
    }

    public boolean manageWindow(ServerCookiePlayer serverCookiePlayer, WrapperPlayClientClickWindow clickPacket) {
        if (clickPacket.getSlot() == -999) return false;
        ClickerContainer container = containerManager.getOpenedContainer(serverCookiePlayer.getUuid());

        switch (container.getWindowType()) {
            case ANVIL_WINDOW_TYPE -> {
                anvilEvent.anvilClick(serverCookiePlayer.getPlayer(), clickPacket.getSlot());
                return false;
            }
            //инвентари 9 * x
            case 1, 2, 3, 4, 5 -> {
                manageContainers(serverCookiePlayer, container, clickPacket);
                return true;
            }
            case CRAFTING_TABLE_WINDOW_TYPE -> {
                checkForCraftSlot(serverCookiePlayer, clickPacket);
                return false;
            }

            default -> {
                return false;
            }
        }
    }

    private void checkForCraftSlot(ServerCookiePlayer serverCookiePlayer, WrapperPlayClientClickWindow clickPacket) {
        if (clickPacket.getSlot() != 0)
            return;

        packetCraftingEvent.onCraft(serverCookiePlayer, clickPacket.getWindowClickType());
    }

    private void selectMainMenu(ServerCookiePlayer serverCookiePlayer, int slot) {
        if (slot == 15) mainMenu.openAllItems(serverCookiePlayer);
        if (slot == 11) mainMenu.openAllRecipes(serverCookiePlayer);
    }

    public void checkAndPrepareCraft(ServerCookiePlayer serverCookiePlayer) {
        if (containerManager.getOpenedContainer(serverCookiePlayer).getWindowType() == 12) {
            packetCraftingEvent.prepareCraft(serverCookiePlayer);
        }
    }

    public void checkForEquipUpgrade(ServerCookiePlayer serverCookiePlayer) {
        anvilEvent.processUpgrade(serverCookiePlayer);
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

        if (new Features(inSlotItem).getCategory() == ToolType.BACKPACK) {
            containerManager.updateWindow(serverCookiePlayer);
            return;
        }

        switch (windowClickType) {
            case WrapperPlayClientClickWindow.WindowClickType type when type == WrapperPlayClientClickWindow.WindowClickType.PICKUP
                    && button == 0 -> leftClick(serverCookiePlayer, carried, inSlotItem, workSlot, isPlayerInvClick);

            case WrapperPlayClientClickWindow.WindowClickType type when type == WrapperPlayClientClickWindow.WindowClickType.PICKUP
                    && button == 1 -> rightClick(serverCookiePlayer, carried, inSlotItem, workSlot, isPlayerInvClick);

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

    private void applyContentsToItem(ItemStack itemStack, ItemStorage container) {
        ItemContainerContents containerContents = ItemContainerContents.fromItems(container.getContainerItems());

        itemStack.applyComponents(DataComponentPatch.builder()
                .set(DataComponents.CONTAINER, containerContents)
                .build());
    }

    public void onClose(ServerCookiePlayer serverCookiePlayer) {
        if (containerManager.getOpenedContainer(serverCookiePlayer) instanceof ItemStorage storage) {
            applyContentsToItem(storage.getBoundItem(), storage);
        }

        containerManager.closeContainer(serverCookiePlayer.getUuid());
    }

}
