package net.flectone.cookieclicker.items;

import com.google.inject.Singleton;
import lombok.Getter;
import net.flectone.cookieclicker.utility.CCobjects.CookieTrader;
import net.flectone.cookieclicker.utility.CCobjects.TradeItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

@Getter
@Singleton
public class VillagerTrades {
    private final HashMap<String, CookieTrader> allTraders = new HashMap<>();

    public void loadSellingItems(Logger information) {
        CookieTrader farmer = new CookieTrader("trading_farm");
        farmer.addTrade(new TradeItem("wood_hoe")
                .withPrice("ench_cookie", 10));
        farmer.addTrade(new TradeItem("destroyer")
                .withPrice("ench_cookie", 100));
        farmer.addTrade(new TradeItem("stone_hoe")
                .withPrice("ench_cookie", 30));
        farmer.addTrade(new TradeItem("rose_bush")
                .withPrice("baguette", 60));

        registerTrader(farmer);

        CookieTrader armorer = new CookieTrader("trading_armorer");
        armorer.addTrade(new TradeItem("fHelmet")
                .withPrice("ench_cookie", 450));
        armorer.addTrade(new TradeItem("fChest")
                .withPrice("berries", 300));
        armorer.addTrade(new TradeItem("fLegs")
                .withPrice("baguette", 150));
        armorer.addTrade(new TradeItem("fBoots")
                .withPrice("ench_cocoa", 250));

        registerTrader(armorer);

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

    public String getItem(String traderType, Integer num) {
        return getShopItems(traderType).get(num).getSellingItemTag();
    }

    public String getPriceItem(String traderType, Integer num) {
        return getShopItems(traderType).get(num).getPrice().getKey();
    }

    public Integer getPriceCount(String traderType, Integer num) {
        return getShopItems(traderType).get(num).getPrice().getValue();
    }

}
