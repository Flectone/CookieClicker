package net.flectone.cookieclicker.utility;

import com.google.inject.Singleton;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.flectone.cookieclicker.items.attributes.CookieAbility;
import net.flectone.cookieclicker.items.attributes.StatType;
import net.flectone.cookieclicker.items.attributes.ToolType;
import net.flectone.cookieclicker.items.itemstacks.base.CookieItems;
import net.flectone.cookieclicker.items.itemstacks.base.data.Features;
import net.flectone.cookieclicker.items.itemstacks.base.data.ItemTag;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.Random;

@Singleton
public class StatsUtils {
    private Features getFeatures(ItemStack itemStack) {
        if (itemStack == null)
            return Features.EMPTY;

        return new Features(itemStack);
    }

    public ItemTag getItemTag(ItemStack itemStack) {
        return getFeatures(itemStack).getItemTag();
    }

    public boolean hasTag(ItemStack item, ItemTag tag) {
        return getItemTag(item) == tag;
    }

    public boolean compareTags(ItemStack firstItem, ItemStack secondItem) {
        return hasTag(firstItem, getItemTag(secondItem));
    }

    public CookieAbility getAbility(ItemStack itemStack) {
        CookieAbility ability = getFeatures(itemStack).getAbility();
        return ability == null ? CookieAbility.NONE : ability;
    }

    public ToolType getCategory(ItemStack itemStack) {
        return getFeatures(itemStack).getCategory();
    }

    public Integer getTier(ItemStack itemStack) {
        return getFeatures(itemStack).getStat(StatType.EQUIPMENT_TIER);
    }

    private Integer getBaseFarmingFortune(ItemStack itemStack) {
        return getFeatures(itemStack).getStat(StatType.FARMING_FORTUNE);
    }

    public Integer getFarmingFortune(ItemStack itemStack) {
        int amount = getBaseFarmingFortune(itemStack);

        for (Object2IntMap.Entry<Holder<Enchantment>> enchantment : itemStack.getEnchantments().entrySet()) {
            if (enchantment.getKey().getRegisteredName().equals(CookieItems.COOKIE_BOOST_ENCHANTMENT))
                amount += 1 << (enchantment.getIntValue() - 1);
        }
        return amount;
    }

    public Integer extractStat(Player player, StatType statType) {
        int fortune = 1;

        Inventory inventory = player.getInventory();
        //броня, если есть
        for (int i = 36; i < 40; i++) {
            if (inventory.getItem(i).getItem() != Items.AIR)
                fortune += getFarmingFortune(inventory.getItem(i));
        }

        ItemStack itemInHand = player.getMainHandItem();
        fortune += getFarmingFortune(itemInHand);
//        if (getFeatures(itemInHand).getCategory() == ToolType.HOE) {
//            fortune += getFarmingFortune(itemInHand);
//        }
        return fortune;
    }

    //оно там как-то вычисляет,
    //работает и ладно
    public Integer convertFortuneToAmount(Integer fortune) {
        Random random = new Random(System.currentTimeMillis());

        int x = random.nextInt(Math.max(fortune - 3, 1), fortune + 3);

        double first = Math.log(x) / Math.log(25);
        double second = Math.pow(9, first);

        return (int) (x - second + 1);
    }
}
