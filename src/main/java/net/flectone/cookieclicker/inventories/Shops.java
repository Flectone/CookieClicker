package net.flectone.cookieclicker.inventories;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.entities.playerdata.ServerCookiePlayer;
import net.flectone.cookieclicker.inventories.containers.ShopContainer;
import net.flectone.cookieclicker.items.ItemsRegistry;
import net.flectone.cookieclicker.items.TradesRegistry;
import net.flectone.cookieclicker.items.trade.TradeItem;
import net.flectone.cookieclicker.utility.ItemsCompactor;

@Singleton
public class Shops {
    private final ContainerManager containerManager;
    private final TradesRegistry villagerTrades;
    private final ItemsCompactor itemsCompactor;
    private final ItemsRegistry loadedItems;

    @Inject
    public Shops(ContainerManager containerManager, TradesRegistry villagerTrades,
                 ItemsCompactor itemsCompactor, ItemsRegistry loadedItems) {
        this.containerManager = containerManager;
        this.villagerTrades = villagerTrades;
        this.itemsCompactor = itemsCompactor;
        this.loadedItems = loadedItems;
    }

    public void openAnyShop(ServerCookiePlayer serverCookiePlayer, String traderType) {
        ShopContainer shopContainer = ShopContainer.createScreen9x4(traderType);

        fillWithTrades(shopContainer, traderType);

        containerManager.openContainer(serverCookiePlayer, shopContainer);
    }

    public void openSpecialShop(ServerCookiePlayer serverCookiePlayer, String shopType) {
        ShopContainer shopContainer = ShopContainer.createScreenSpecial(shopType);

        fillWithTrades(shopContainer, shopType);

        containerManager.openContainer(serverCookiePlayer, shopContainer);
    }

    private void fillWithTrades(ShopContainer shopContainer, String shopData) {
        for (TradeItem tradeItem : villagerTrades.getShopItems(shopData)) {
            shopContainer.addTrade(tradeItem, loadedItems);
        }
    }
}
