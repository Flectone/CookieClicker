package net.flectone.cookieclicker.inventories;

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.utility.ItemsCompactor;
import net.flectone.cookieclicker.inventories.containers.ClickerContainer;
import net.flectone.cookieclicker.items.ItemsRegistry;
import net.flectone.cookieclicker.items.VillagerTradesRegistry;
import net.flectone.cookieclicker.items.itemstacks.CommonCookieItem;
import net.flectone.cookieclicker.items.itemstacks.base.data.ItemTag;
import net.flectone.cookieclicker.entities.playerdata.ServerCookiePlayer;
import net.flectone.cookieclicker.items.trade.TradeItem;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class Shops {
    private final ContainerManager containerManager;
    private final VillagerTradesRegistry villagerTrades;
    private final ItemsCompactor itemsCompactor;
    private final ItemsRegistry loadedItems;

    @Inject
    public Shops(ContainerManager containerManager, VillagerTradesRegistry villagerTrades,
                 ItemsCompactor itemsCompactor, ItemsRegistry loadedItems) {
        this.containerManager = containerManager;
        this.villagerTrades = villagerTrades;
        this.itemsCompactor = itemsCompactor;
        this.loadedItems = loadedItems;
    }

    public void openAnyShop(ServerCookiePlayer serverCookiePlayer, String traderType) {
        containerManager.openContainer(serverCookiePlayer, createAnyShop(traderType));
    }

    public void buyItem(ServerCookiePlayer serverCookiePlayer, WrapperPlayClientClickWindow packet) {
        int slot = packet.getSlot();

        ClickerContainer container = containerManager.getOpenedContainer(serverCookiePlayer.getUser());

        containerManager.cancelClick(serverCookiePlayer);

        if (container.getContainerItems().size() - 1 < slot) return;

        String traderType = container.getCustomData();

        if (slot < 9 || villagerTrades.itemsLength(traderType) <= slot - 9)
            return;

        itemsCompactor.compact(
                serverCookiePlayer.getPlayer().getInventory(),
                villagerTrades.getPriceItem(traderType, slot - 9),
                loadedItems.get(villagerTrades.getItem(traderType, slot - 9)),
                villagerTrades.getPriceCount(traderType, slot - 9),
                1
        );
    }

    private ClickerContainer createBasicShop(Integer windowType, String customData) {
        ClickerContainer basicContainer = new ClickerContainer(ClickerContainer.generateId(),
                windowType, customData);
        CommonCookieItem upperItem = new CommonCookieItem(Items.WHITE_STAINED_GLASS_PANE, ItemTag.EMPTY,
                "<gradient:#ffffff:#cccccc><italic:false>Здесь вы можете купить");
        upperItem.addLore("<gradient:#ffffff:#cccccc><italic:false>различные вещи");

        basicContainer.setTitle("торговля жесть в шоке все");

        for (int i = 0; i < 9; i++) {
            basicContainer.setItem(i, upperItem.toMinecraftStack());
        }

        int slot = 9;
        for (TradeItem tradeItem : villagerTrades.getShopItems(customData)) {
            basicContainer.setItem(slot, addPriceToSellItem(tradeItem));
            slot++;
        }

        return basicContainer;
    }

    private ItemStack addPriceToSellItem(TradeItem tradeItem) {
        ItemStack finalItem = loadedItems.get(tradeItem.getSellingItemTag());
        List<Component> lore = new ArrayList<>();
        ItemLore itemLore = finalItem.getComponents().get(DataComponents.LORE);
        if (itemLore != null) lore.addAll(itemLore.lines());

        Component cost = Component.literal(String.format(
                "Стоимость: %d %s", tradeItem.getPrice().getValue(), loadedItems.get(tradeItem.getPrice().getKey()).getDisplayName().getString()
        )).withColor(13041721);

        lore.addFirst(cost);

        finalItem.applyComponents(DataComponentPatch.builder()
                .set(DataComponents.LORE, new ItemLore(lore))
                .build()
        );
        return finalItem.copy();
    }

    public ClickerContainer createAnyShop(String type) {
        return createBasicShop(3, type);
    }
    public ClickerContainer createCookieShop() {
        return createBasicShop(3, "trading_farm");
    }
    public ClickerContainer createArmorShop() {
        return createBasicShop(3, "trading_armorer");
    }

}
