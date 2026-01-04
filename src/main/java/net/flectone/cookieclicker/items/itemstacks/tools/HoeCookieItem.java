package net.flectone.cookieclicker.items.itemstacks.tools;

import net.flectone.cookieclicker.items.attributes.ToolType;
import net.flectone.cookieclicker.items.itemstacks.base.data.ItemTag;
import net.minecraft.world.item.Item;

public class HoeCookieItem extends ToolCookieItem {
    public HoeCookieItem(Item originalMaterial, ItemTag itemTag, String name) {
        super(originalMaterial, itemTag, name, ToolType.HOE);
    }
}
