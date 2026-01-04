package net.flectone.cookieclicker.items.itemstacks.tools;

import net.flectone.cookieclicker.items.attributes.ToolType;
import net.flectone.cookieclicker.items.itemstacks.base.data.ItemTag;
import net.minecraft.world.item.Item;

public class PickaxeCookieItem extends CookieBlockBreaker {
    public PickaxeCookieItem(Item originalMaterial, ItemTag itemTag, String name) {
        super(originalMaterial, itemTag, name, ToolType.PICKAXE);
    }

    public void setPower(Integer value) {
        // чуть позже
    }
}
