package net.flectone.cookieclicker.utility.CCobjects;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CookieTrader {
    private final List<TradeItem> traderShop = new ArrayList<>();
    private final String traderType;

    public CookieTrader(String traderName) {
        this.traderType = traderName;
    }

    public void addTrade(TradeItem tradeItem) {
        traderShop.add(tradeItem);
    }
}
