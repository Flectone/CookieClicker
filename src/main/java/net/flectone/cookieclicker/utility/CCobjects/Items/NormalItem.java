package net.flectone.cookieclicker.utility.CCobjects.Items;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemUseAnimation;
import org.bukkit.Material;

public class NormalItem extends ItemBase {

    public NormalItem(Material itemType, String displayName, String itemTag, Integer stackSize) {
        super(displayName, itemTag, "item", itemType);
        components.add(DataComponentPatch.builder().set(DataComponents.MAX_STACK_SIZE, stackSize).build());
    }

    public void makeEnchGlint() {
        components.add(DataComponentPatch.builder()
                        .set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
                        .build()
        );
    }

    public void setEatable() {
        net.minecraft.world.item.component.Consumable consumable = net.minecraft.world.item.component.Consumable.builder()
                .consumeSeconds(1.6f)
                .animation(ItemUseAnimation.EAT)
                .hasConsumeParticles(true)
                .build();

        DataComponentPatch consumableComponent = DataComponentPatch.builder()
                .set(DataComponents.CONSUMABLE, consumable)
                .build();
        components.add(consumableComponent);
    }


}
