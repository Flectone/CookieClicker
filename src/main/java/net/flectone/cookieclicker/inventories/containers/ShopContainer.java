package net.flectone.cookieclicker.inventories.containers;

import net.flectone.cookieclicker.items.ItemsRegistry;
import net.flectone.cookieclicker.items.itemstacks.base.data.Features;
import net.flectone.cookieclicker.items.itemstacks.base.data.ItemTag;
import net.flectone.cookieclicker.items.trade.TradeItem;
import net.flectone.cookieclicker.utility.data.Pair;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ShopContainer extends ClickerContainer {
    // Доступные торги
    private final List<Pair<TradeItem, ItemStack>> availableTrades = new ArrayList<>();

    private int slot = 9;       // Текущий слот
    private int startSlot = 9;  // Слот, с которого начинаются предметы
    private int step = 1;       // Шаг

    private ShopContainer(int windowType, String customData) {
        super(generateId(), windowType, customData);
    }

    private ShopContainer(int windowType, String customData, int slot, int step) {
        super(generateId(), windowType, customData);
        this.slot = slot;
        this.step = step;
        this.startSlot = slot;
    }

    // Для любых магазинов
    public static ShopContainer createScreen9x4(String customData) {
        ShopContainer container = new ShopContainer(3, customData);

        container.setTitle("торговля жесть в шоке все");

        for (int i = 0; i < 9; i++) {
            container.setItem(i, fillerItem);
        }
        return container;
    }

    // Для книжной полки
    public static ShopContainer createScreenSpecial(String customData) {
        ShopContainer container = new ShopContainer(2, customData, 11, 2);

        container.setTitle("special");

        for (int i = 1; i < 8; i++) {
            container.setItem(i, fillerItem);
        }
        for (int i = 19; i < 26; i++) {
            container.setItem(i, fillerItem);
        }
        container.setItem(9, fillerItem);
        container.setItem(10, fillerItem);
        container.setItem(16, fillerItem);
        container.setItem(17, fillerItem);
        return container;
    }

    public void addTrade(TradeItem tradeItem, ItemsRegistry itemsRegistry) {
        this.setItem(slot, getItemWithPrice(tradeItem, itemsRegistry));
        slot += step;
        availableTrades.add(new Pair<>(tradeItem, itemsRegistry.get(tradeItem.getSellingItemTag())));
    }

    public boolean buyItem(Inventory inventory, int slot) {
        // Реальный индекс торга
        int tradeIndex = ((slot - startSlot) / step);

        if (tradeIndex < 0) return false;
        if (tradeIndex > availableTrades.size() - 1) return false;

        ItemTag priceTag = availableTrades.get(tradeIndex).left().getPrice().left();
        int priceCount = availableTrades.get(tradeIndex).left().getPrice().right();

        int countSum = 0;

        Predicate<ItemStack> pricePredicate = itemStack -> new Features(itemStack).getItemTag() == priceTag;

        // Подсчёт общего количества нужных предметов
        for (int i = 0; i <= inventory.getContainerSize(); i++) {
            // Если общее кол-во уже равно или больше нужного кол-ва, то покупка предмета
            if (countSum >= priceCount) {
                ContainerHelper.clearOrCountMatchingItems(inventory, pricePredicate, priceCount, false);
                inventory.add(availableTrades.get(tradeIndex).right().copy());
                return true;
            }
            ItemStack itemInSlot = inventory.getItem(i);
            if (itemInSlot.isEmpty()) continue;
            if (new Features(itemInSlot).getItemTag() == priceTag) {
                countSum += itemInSlot.getCount();
            }
        }

        return false;
    }

    private ItemStack getItemWithPrice(TradeItem tradeItem, ItemsRegistry itemsRegistry) {
        ItemStack finalItem = itemsRegistry.get(tradeItem.getSellingItemTag());
        List<Component> lore = new ArrayList<>();
        ItemLore itemLore = finalItem.getComponents().get(DataComponents.LORE);
        if (itemLore != null) lore.addAll(itemLore.lines());

        Component cost = Component.literal(String.format(
                "Стоимость: %d %s", tradeItem.getPrice().getValue(), itemsRegistry.get(tradeItem.getPrice().getKey()).getDisplayName().getString()
        )).withColor(13041721);

        lore.addFirst(cost);

        finalItem.applyComponents(DataComponentPatch.builder()
                .set(DataComponents.LORE, new ItemLore(lore))
                .build()
        );
        return finalItem.copy();
    }
}
