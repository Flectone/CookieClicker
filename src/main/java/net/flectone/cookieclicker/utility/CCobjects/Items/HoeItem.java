package net.flectone.cookieclicker.utility.CCobjects.Items;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import org.bukkit.Material;

import java.util.Arrays;

public class HoeItem extends ItemBase{

    public HoeItem(Material itemType, String displayName, String itemTag, Integer fortune) {
        super(displayName, itemTag, "tool", itemType);
        addStat(fortuneKey, fortune);
    }

    public void setAbility(String newAbility, String... description) {
        String lorePart = category.equals("tool") ? "в ведущей руке" : "экипировано";
        if (preStatsLore.isEmpty()) preStatsLore.add(miniMessage.deserialize("<gray><italic:false>Когда " + lorePart + ":"));
        ability = newAbility;
        Arrays.stream(description).forEach(b -> preStatsLore.add(miniMessage.deserialize("<gold><italic:false> " + b)));

        if (ability.equals("infinity"))
            components.add(DataComponentPatch.builder()
                            .set(DataComponents.MAX_DAMAGE, 100)
                            .build()
            );
    }
}
