package net.flectone.cookieclicker.items.trades;

import lombok.Getter;
import net.flectone.cookieclicker.items.itemstacks.base.data.ItemTag;
import net.flectone.cookieclicker.utility.Pair;

@Getter
public class TradeItem {
    private Pair<ItemTag, Integer> price;
    private final ItemTag sellingItemTag;

    public TradeItem(ItemTag sellingItemTag) {
        this.sellingItemTag = sellingItemTag;
    }

    public TradeItem withPrice(ItemTag itemTag, Integer count) {
        price = new Pair<>(itemTag, count);
        return this;
    }
}
