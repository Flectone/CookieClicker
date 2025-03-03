package net.flectone.cookieclicker.utility.CCobjects;

import lombok.Getter;
import net.flectone.cookieclicker.utility.Pair;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

@Getter
public class TradeItem {
    //можно сделать много предметов, но не будет работать пока что
    //private final HashMap<String, Integer> priceItems = new HashMap<>();
    private Pair<String, Integer> price;
    private final String sellingItemTag;

    public TradeItem(String sellingItemTag) {
        this.sellingItemTag = sellingItemTag;
    }

    public TradeItem withPrice(String itemTag, Integer count) {
        //priceItems.put(itemTag, count);
        price = new Pair<>(itemTag, count);
        return this;
    }

    private String getTag(ItemStack itemStack) {
        CustomData customData = itemStack.get(DataComponents.CUSTOM_DATA);
        if (customData == null || !customData.contains("cookies")) return "none";

        return customData.copyTag().getCompound("cookies").getString("item_tag");
    }
}
