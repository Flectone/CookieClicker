package net.flectone.cookieclicker.utility.CCobjects;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class HoeItem extends ItemBase{

    public HoeItem(Material itemType, String displayName, String itemTag, Integer fortune) {
        this.itemTag = itemTag;
        this.itemType = itemType;
        this.displayName = displayName;
        this.firstMeta = (new ItemStack(itemType)).getItemMeta();

        this.equipType = "tool";
        this.addStat(fortuneKey, fortune);

    }

    public void setAbility(String ability, String... description) {
        String lorePart = equipType.equals("tool") ? "в ведущей руке" : "экипировано";
        if (preStatsLore.isEmpty()) preStatsLore.add(miniMessage.deserialize("<gray><italic:false>Когда " + lorePart + ":"));
        this.ability = ability;
        Arrays.stream(description).forEach(b -> {
            preStatsLore.add(miniMessage.deserialize("<gold><italic:false> " + b));});

        if (ability.equals("infinity"))
            components.add(DataComponentPatch.builder()
                            .set(DataComponents.MAX_DAMAGE, 100)
                            .build());
    }
}
