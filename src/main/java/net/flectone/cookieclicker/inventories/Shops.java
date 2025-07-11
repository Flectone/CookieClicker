package net.flectone.cookieclicker.inventories;

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.CompactItems;
import net.flectone.cookieclicker.items.ItemManager;
import net.flectone.cookieclicker.items.VillagerTrades;
import net.flectone.cookieclicker.items.itemstacks.CommonCookieItem;
import net.flectone.cookieclicker.playerdata.ServerCookiePlayer;
import net.flectone.cookieclicker.utility.CCobjects.TradeItem;
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
    private final VillagerTrades villagerTrades;
    private final CompactItems compactItems;
    private final ItemManager loadedItems;

    @Inject
    public Shops(ContainerManager containerManager, VillagerTrades villagerTrades,
                 CompactItems compactItems, ItemManager loadedItems) {
        this.containerManager = containerManager;
        this.villagerTrades = villagerTrades;
        this.compactItems = compactItems;
        this.loadedItems = loadedItems;
    }

    public void openAnyShop(ServerCookiePlayer serverCookiePlayer, String traderType) {
        containerManager.openContainer(serverCookiePlayer, createAnyShop(traderType));
    }

    public void buyItem(ServerCookiePlayer serverCookiePlayer, WrapperPlayClientClickWindow packet) {
        int slot = packet.getSlot();

        ClickerContainer container = containerManager.getOpenedContainer(serverCookiePlayer.getUser());

        containerManager.cancelClick(serverCookiePlayer, container, slot, packet.getWindowClickType());

        if (container.getContainerItems().size() - 1 < slot) return;

        String traderType = container.getCustomData();

        if (slot < 9 || villagerTrades.itemsLength(traderType) <= slot - 9)
            return;

        compactItems.compact(
                serverCookiePlayer.getPlayer().getInventory(),
                villagerTrades.getPriceItem(traderType, slot - 9),
                loadedItems.getNMS(villagerTrades.getItem(traderType, slot - 9)),
                villagerTrades.getPriceCount(traderType, slot - 9),
                1
        );
    }

    private ClickerContainer createBasicShop(Integer windowType, String customData) {
        ClickerContainer basicContainer = new ClickerContainer(ClickerContainer.generateId(),
                windowType, customData);
        CommonCookieItem upperItem = new CommonCookieItem(Items.WHITE_STAINED_GLASS_PANE, "none",
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
        ItemStack finalItem = loadedItems.getNMS(tradeItem.getSellingItemTag());
        List<Component> lore = new ArrayList<>();
        ItemLore itemLore = finalItem.getComponents().get(DataComponents.LORE);
        if (itemLore != null) lore.addAll(itemLore.lines());

        Component cost = Component.literal(String.format(
                "Стоимость: %d %s", tradeItem.getPrice().getValue(), loadedItems.getNMS(tradeItem.getPrice().getKey()).getDisplayName().getString()
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
