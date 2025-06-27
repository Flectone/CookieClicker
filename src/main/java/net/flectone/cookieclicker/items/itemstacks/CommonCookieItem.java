package net.flectone.cookieclicker.items.itemstacks;

import net.flectone.cookieclicker.items.attributes.ToolType;
import net.flectone.cookieclicker.items.itemstacks.base.BaseCookieItem;
import net.flectone.cookieclicker.items.itemstacks.base.data.Features;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.Consumable;

public class CommonCookieItem extends BaseCookieItem {
    public CommonCookieItem(Item originalMaterial, String tag, String name) {
        super(originalMaterial, new Features(tag, ToolType.NONE));
        setName(name);
        removeVisibleAttributes(false);
    }

    public void setAmount(Integer amount) {
        applyComponent(DataComponents.MAX_STACK_SIZE, amount);
    }

    public void setEatable() {
        Consumable consumable = net.minecraft.world.item.component.Consumable.builder()
                .consumeSeconds(1.6f)
                .animation(ItemUseAnimation.EAT)
                .hasConsumeParticles(true)
                .build();

        applyComponent(DataComponents.CONSUMABLE, consumable);
    }
}
