package net.flectone.cookieclicker.items.itemstacks;

import net.flectone.cookieclicker.items.attributes.ToolType;
import net.flectone.cookieclicker.items.itemstacks.base.data.Features;
import net.flectone.cookieclicker.items.itemstacks.base.data.ItemTag;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.LinkedHashSet;

public class CommonCookieItem extends BaseCookieItem {
    public CommonCookieItem(Item originalMaterial, ItemTag tag, String name) {
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

    public CommonCookieItem withoutTooltip() {
        applyComponent(DataComponents.TOOLTIP_DISPLAY, new TooltipDisplay(true, new LinkedHashSet<>()));
        return this;
    }
}
