package net.flectone.cookieclicker.items.itemstacks;

import net.flectone.cookieclicker.items.attributes.StatType;
import net.flectone.cookieclicker.items.attributes.ToolType;
import net.flectone.cookieclicker.items.itemstacks.base.data.Features;
import net.flectone.cookieclicker.items.itemstacks.base.data.ItemTag;
import net.minecraft.world.item.Item;

public class HoeCookieItem extends BaseCookieItem {
    public HoeCookieItem(Item originalMaterial, ItemTag itemTag, String name) {
        super(originalMaterial, new Features(itemTag, ToolType.HOE));
        setName(name);
    }

    public void setFarmingFortune(Integer amount) {
        setStat(StatType.FARMING_FORTUNE, amount);
    }
}
