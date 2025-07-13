package net.flectone.cookieclicker.items.itemstacks;

import net.flectone.cookieclicker.items.attributes.StatType;
import net.flectone.cookieclicker.items.attributes.ToolType;
import net.flectone.cookieclicker.items.itemstacks.base.BaseCookieItem;
import net.flectone.cookieclicker.items.itemstacks.base.data.EquipmentData;
import net.flectone.cookieclicker.items.itemstacks.base.data.Features;
import net.flectone.cookieclicker.items.itemstacks.base.data.ItemTag;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.Equippable;

import java.util.LinkedHashSet;
import java.util.List;

public class EquipmentCookieItem extends BaseCookieItem {

    public EquipmentCookieItem(Item originalMaterial, ItemTag itemTag, String name, EquipmentData equipmentData, EquipmentSlot slot) {
        super(originalMaterial, new Features(itemTag, ToolType.EQUIPMENT));
        setName(name);
        if (!equipmentData.getLore().isEmpty()) {
            equipmentData.getLore().forEach(this::addLore);
        }

        createEquippableComponent(equipmentData, slot);
    }

    private void createEquippableComponent(EquipmentData equipmentData, EquipmentSlot slot) {
        Equippable equip = Equippable.builder(slot)
                .setAsset(equipmentData.getAssets())
                .build();

        if (equipmentData.getColor() != 0) {
            applyComponent(DataComponents.TOOLTIP_DISPLAY,
                    new TooltipDisplay(false, new LinkedHashSet<>(List.of(DataComponents.TRIM, DataComponents.DYED_COLOR))));
            applyComponent(DataComponents.DYED_COLOR, new DyedItemColor(equipmentData.getColor()));
        }
        applyComponent(DataComponents.EQUIPPABLE, equip);
    }

    public void setFarmingFortune(Integer amount) {
        setStat(StatType.FARMING_FORTUNE, amount);
    }
}
