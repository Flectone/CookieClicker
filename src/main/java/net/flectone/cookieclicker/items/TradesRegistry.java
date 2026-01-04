package net.flectone.cookieclicker.items;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import net.flectone.cookieclicker.items.itemstacks.base.data.ItemTag;
import net.flectone.cookieclicker.items.trade.CookieTrader;
import net.flectone.cookieclicker.items.trade.TradeItem;
import net.flectone.cookieclicker.utility.config.CookieClickerConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

@Getter
@Singleton
public class TradesRegistry {

    private final CookieClickerConfig config;

    @Inject
    public TradesRegistry(CookieClickerConfig config) {
        this.config = config;
    }

    private final HashMap<String, CookieTrader> allTraders = new HashMap<>();

    public void loadSellingItems(Logger information) {
        CookieTrader farmer = new CookieTrader("trading_farm");
        farmer.addTrade(new TradeItem(ItemTag.WOODEN_HOE)
                .withPrice(ItemTag.ENCHANTED_COOKIE, 20));
        farmer.addTrade(new TradeItem(ItemTag.STONE_HOE)
                .withPrice(ItemTag.ENCHANTED_COOKIE, 50));
        farmer.addTrade(new TradeItem(ItemTag.COOKIE_DESTROYER_HOE)
                .withPrice(ItemTag.ENCHANTED_COOKIE, 150));
        farmer.addTrade(new TradeItem(ItemTag.ROSE_BUSH_HOE)
                .withPrice(ItemTag.BAGUETTE, 45));

        registerTrader(farmer);

        CookieTrader armorer = new CookieTrader("trading_armorer");
        armorer.addTrade(new TradeItem(ItemTag.FARMER_HELMET)
                .withPrice(ItemTag.ENCHANTED_COOKIE, 450));
        armorer.addTrade(new TradeItem(ItemTag.FARMER_CHESTPLATE)
                .withPrice(ItemTag.SWEET_BERRIES, 300));
        armorer.addTrade(new TradeItem(ItemTag.FARMER_LEGGINGS)
                .withPrice(ItemTag.BAGUETTE, 150));
        armorer.addTrade(new TradeItem(ItemTag.FARMER_BOOTS)
                .withPrice(ItemTag.ENCHANTED_COCOA_BEANS, 250));
        armorer.addTrade(new TradeItem(ItemTag.BAG_18)
                .withPrice(ItemTag.ENCHANTED_COOKIE, 192));
        armorer.addTrade(new TradeItem(ItemTag.BAG_45)
                .withPrice(ItemTag.GLOW_BERRIES, 64));

        registerTrader(armorer);

        CookieTrader bookshelf = new CookieTrader("bookshelf");
        bookshelf.addTrade(new TradeItem(ItemTag.BOOK_COOKIE_BOOST)
                .withPrice(ItemTag.ENCHANTED_COOKIE, config.getCookieBoostCost()));
        bookshelf.addTrade(new TradeItem(ItemTag.BOOK_MINING_BOOST)
                .withPrice(ItemTag.EMPTY, 999));
        bookshelf.addTrade(new TradeItem(ItemTag.BOOK_BLOCK_DAMAGE)
                .withPrice(ItemTag.ENCHANTED_MELON, 20));

        registerTrader(bookshelf);

        getAllTraders().forEach((type, cookieTrader) -> information.info(String.format("+%s (%d)", type, cookieTrader.getTraderShop().size())));
    }

    public void registerTrader(CookieTrader cookieTrader) {
        allTraders.put(cookieTrader.getTraderType(), cookieTrader);
    }

    public List<TradeItem> getShopItems(String traderType) {
        if (allTraders.isEmpty() || !allTraders.containsKey(traderType))
            return new ArrayList<>();
        return allTraders.get(traderType).getTraderShop();
    }

    public Integer itemsLength(String traderType) {
        return getShopItems(traderType).size();
    }

    public ItemTag getItem(String traderType, Integer num) {
        return getShopItems(traderType).get(num).getSellingItemTag();
    }

    public ItemTag getPriceItem(String traderType, Integer num) {
        return getShopItems(traderType).get(num).getPrice().getKey();
    }

    public Integer getPriceCount(String traderType, Integer num) {
        return getShopItems(traderType).get(num).getPrice().getValue();
    }

}
