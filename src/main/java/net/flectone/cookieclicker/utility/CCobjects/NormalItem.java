package net.flectone.cookieclicker.utility.CCobjects;

import com.github.retrooper.packetevents.protocol.item.consumables.ConsumeEffect;
import io.papermc.paper.datacomponent.DataComponentBuilder;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.key.Key;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.Consumable;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class NormalItem extends ItemBase {

    public NormalItem(Material itemType, String displayName, String itemTag, Integer stackSize) {
        this.itemTag = itemTag;
        this.itemType = itemType;
        this.displayName = displayName;
        this.firstMeta = (new ItemStack(itemType)).getItemMeta();
        components.add(DataComponentPatch.builder().set(DataComponents.MAX_STACK_SIZE, stackSize).build());
    }

    public void makeEnchGlint() {
        components.add(DataComponentPatch.builder()
                        .set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
                        .build());
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
