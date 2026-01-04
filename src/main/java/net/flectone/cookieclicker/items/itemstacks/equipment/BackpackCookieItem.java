package net.flectone.cookieclicker.items.itemstacks.equipment;

import net.flectone.cookieclicker.items.attributes.StatType;
import net.flectone.cookieclicker.items.attributes.ToolType;
import net.flectone.cookieclicker.items.itemstacks.BaseCookieItem;
import net.flectone.cookieclicker.items.itemstacks.base.data.Features;
import net.flectone.cookieclicker.items.itemstacks.base.data.ItemTag;
import net.minecraft.world.item.Item;

public class BackpackCookieItem extends BaseCookieItem {

    public BackpackCookieItem(Item originalMaterial, ItemTag itemTag, int slots, String name) {
        super(originalMaterial, new Features(itemTag, ToolType.BACKPACK));
        setName(name);

        setStat(StatType.ADDITIONAL_SLOT, slots);
    }
}
