package net.flectone.cookieclicker.items.itemstacks.base.data;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.EquipmentAsset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class EquipmentData {
    private final ResourceKey<EquipmentAsset> assets;
    private final List<String> lore = new ArrayList<>();
    @Setter
    private Integer color = 0;

    public EquipmentData(ResourceKey<EquipmentAsset> assets) {
        this.assets = assets;
    }

    public void addLore(String... text) {
        lore.addAll(Arrays.asList(text));
    }

}
