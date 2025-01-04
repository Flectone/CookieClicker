package net.flectone.cookieclicker.utility.CCobjects;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.Equippable;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

public class EquipmentItem extends ItemBase{
    private ResourceKey<EquipmentAsset> assets;
    Integer color = 0;

    @ApiStatus.Experimental
    public EquipmentItem(EquipmentItem eItem, Material material, EquipmentSlot slot, String displayName, String itemTag) {
        this(material, slot, eItem.assets, displayName, itemTag);
        eItem.stats.forEach(this::addStat);
        if (eItem.color != 0)
            this.setDyedColor(eItem.color);
        if (!(eItem.fullLore.isEmpty()))
            fullLore.addAll(eItem.fullLore);
    }

    public EquipmentItem(Material material, EquipmentSlot slot, ResourceKey<EquipmentAsset> assets, String displayName, String itemTag) {
        this.itemTag = itemTag;
        this.itemType = material;
        this.displayName = displayName;
        this.equipType = "armor";
        this.firstMeta = (new ItemStack(itemType)).getItemMeta();
        this.assets = assets;

        components.add(createEquipComponent(slot));
    }
    private DataComponentPatch createEquipComponent(EquipmentSlot slot) {
        Equippable equip = Equippable.builder(slot)
                .setAsset(assets)
                .build();
        return DataComponentPatch.builder()
                .set(DataComponents.EQUIPPABLE, equip)
                .build();
    }

    public void farmingFortune(Integer value) {
        this.addStat(fortuneKey, value);
    }
    public void setDyedColor(Integer color) {
        DataComponentPatch dyedColor = DataComponentPatch.builder()
                .set(DataComponents.DYED_COLOR, new DyedItemColor(color, false))
                .build();
        components.add(dyedColor);
        this.color = color;
    }

}
