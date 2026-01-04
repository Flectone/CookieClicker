package net.flectone.cookieclicker.items.itemstacks.tools;

import net.flectone.cookieclicker.items.attributes.StatType;
import net.flectone.cookieclicker.items.attributes.ToolType;
import net.flectone.cookieclicker.items.itemstacks.BaseCookieItem;
import net.flectone.cookieclicker.items.itemstacks.base.data.Features;
import net.flectone.cookieclicker.items.itemstacks.base.data.ItemTag;
import net.minecraft.world.item.Item;

public class ToolCookieItem extends BaseCookieItem {
    protected ToolCookieItem(Item originalMaterial, ItemTag tag, String name, ToolType category) {
        super(originalMaterial, new Features(tag, category));
        setName(name);
    }

    public void setFarmingFortune(Integer amount) {
        setStat(StatType.FARMING_FORTUNE, amount);
    }

    public void setMiningFortune(Integer amount) {
        setStat(StatType.MINING_FORTUNE, amount);
    }

    public void setMiningPower(Integer amount) {
        setStat(StatType.MINING_POWER, amount);
    }

    public void setBlockDamage(Integer amount) {
        setStat(StatType.BLOCK_DAMAGE, amount);
    }
}
