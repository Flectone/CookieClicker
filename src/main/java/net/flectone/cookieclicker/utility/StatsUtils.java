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
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

@Singleton
public class StatsUtils {

    public int getFarmingFortune(Player player) {
        int amount = getStatAmount(player.getMainHandItem(), StatType.FARMING_FORTUNE, CookieItems.COOKIE_BOOST_ENCHANTMENT);
        amount += getStatFromArmor(player, StatType.FARMING_FORTUNE);

        return amount;
    }

    public int getMiningFortune(Player player) {
        int amount = getStatAmount(player.getMainHandItem(), StatType.MINING_FORTUNE, CookieItems.MINING_BOOST_ENCHANTMENT);
        amount += getStatFromArmor(player, StatType.MINING_FORTUNE);

        return amount;
    }

    public int getBlockDamage(ItemStack itemStack) {
        return getStatAmount(itemStack, StatType.BLOCK_DAMAGE, CookieItems.BLOCK_DAMAGE_ENCHANTMENT);
    }

    public Integer extractStat(Player player, StatType statType) {
        return switch (statType) {
            case FARMING_FORTUNE -> getFarmingFortune(player);
            case MINING_FORTUNE -> getMiningFortune(player);
            case BLOCK_DAMAGE -> getBlockDamage(player.getMainHandItem());
            default -> getStatAmount(player.getMainHandItem(), statType);
        };
    }

    private int getStatFromArmor(Player player, StatType statType) {
        int fortune = 0;

        Inventory inventory = player.getInventory();

        for (ItemStack itemStack : inventory) {
            fortune += getStatAmount(itemStack, statType);
        }
        return fortune;
    }

    private int getStatAmount(ItemStack itemStack, StatType statType) {
        return getFeatures(itemStack).getStat(statType);
    }

    private int getStatAmount(ItemStack itemStack, StatType statType, String enchantmentName) {
        int amount = getFeatures(itemStack).getStat(statType);

        for (Object2IntMap.Entry<Holder<Enchantment>> enchantment : itemStack.getEnchantments().entrySet()) {
            if (enchantment.getKey().getRegisteredName().equals(enchantmentName))
                amount += 1 << (enchantment.getIntValue() - 1);
        }

        return amount;
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

    private Features getFeatures(@NotNull ItemStack itemStack) {
        if (itemStack.isEmpty())
            return Features.EMPTY;

        return new Features(itemStack);
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
