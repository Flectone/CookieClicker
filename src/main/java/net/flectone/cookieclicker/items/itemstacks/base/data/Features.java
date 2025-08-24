package net.flectone.cookieclicker.items.itemstacks.base.data;

import lombok.Getter;
import lombok.Setter;
import net.flectone.cookieclicker.items.attributes.CookieAbility;
import net.flectone.cookieclicker.items.attributes.Stat;
import net.flectone.cookieclicker.items.attributes.StatType;
import net.flectone.cookieclicker.items.attributes.ToolType;
import net.flectone.cookieclicker.items.itemstacks.base.CookieItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.*;

@Getter
public class Features {
    //тег предмета
    private final ItemTag itemTag;
    private CompoundTag additionalData;
    @Setter //способность
    public CookieAbility ability;
    //статы
    private final Map<StatType, Stat> stats = new EnumMap<>(StatType.class);
    //категория, по сути нужна только для лора
    @Setter
    private ToolType category;

    public static final Features EMPTY = new Features(ItemTag.EMPTY, ToolType.NONE);

    public Features(ItemTag itemTag, ToolType category) {
        this.itemTag = itemTag;
        this.category = category;
    }

    public Features(ItemStack itemStack) {
        CompoundTag compoundTag = itemStack.getComponents()
                .getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                .copyTag();

        Optional<CompoundTag> optionalCompoundTag = compoundTag.getCompound(CookieItems.PLUGIN_KEY)
                .or(() -> compoundTag.getCompound("cookies"));

        CompoundTag tag = optionalCompoundTag.orElse(new CompoundTag());
        this.category = ToolType.from(tag.getString("category").orElse(ToolType.NONE.getType()));
        this.itemTag = ItemTag.fromString(tag.getString(CookieItems.ITEM_TAG_KEY).orElse("none"));

        //способность
        tag.getString(CookieItems.ABILITY_KEY).ifPresent(s -> ability = CookieAbility.from(s));

        //В списке LOADED_STATS статы, которые используются в предметах
        CookieItems.LOADED_STATS.forEach(stat -> tag.getInt(stat)
                .ifPresent(integer -> stats.put(StatType.from(stat), new Stat(integer))));

        compoundTag.remove(CookieItems.PLUGIN_KEY);
        if (!compoundTag.isEmpty()) {
            additionalData = compoundTag;
        }
    }

    public void applyStat(StatType statType, Integer value) {
        Stat stat = stats.getOrDefault(statType, new Stat());
        stat.setBaseValue(value);
        stats.put(statType, stat);
    }

    public void removeStat(StatType statType) {
        stats.remove(statType);
    }

    public Integer getStat(StatType statType) {
        return stats.getOrDefault(statType, new Stat()).getBaseValue();
    }

    public void setStatFromEnchant(StatType statType, Integer value) {
        Stat stat = stats.getOrDefault(statType, new Stat());
        stat.setAdditionalValue(value);
        stats.put(statType, stat);
    }

    public CompoundTag createCompoundTag() {
        CompoundTag compoundTag = new CompoundTag();
        //Добавление тега предмета
        compoundTag.putString(CookieItems.ITEM_TAG_KEY, itemTag.getRealTag());
        //Добавление категории
        compoundTag.putString(CookieItems.ITEM_CATEGORY_KEY, category.getType());

        //Добавление способности, если она есть
        if (ability != null) {
            compoundTag.putString(CookieItems.ABILITY_KEY, ability.getType());
        }

        //Добавление всех статов, какие есть
        if (!stats.isEmpty()) {
            stats.forEach((key, value) -> compoundTag.putInt(key.getTag(), value.getBaseValue()));
        }

        CompoundTag createdTag = additionalData != null ? additionalData : new CompoundTag();
        createdTag.put(CookieItems.PLUGIN_KEY, compoundTag);

        return createdTag;
    }

    public List<String> getStatsAsLoreList() {
        //Возвращается список из строк. При создании предмета, все эти строки переделаются в компоненты
        List<String> listOfFeatures = new ArrayList<>();

        //Надпись "Когда в руке/экипировано", появляется, если есть способность или статы
        if (ability != null || !stats.isEmpty()) {
            listOfFeatures.add("");
            listOfFeatures.add(String.format("<italic:false><gray>Когда %s:", category.getView()));
        }

        //Cпособность, если есть
        //Описание способности пишется золотым цветом, перед статами
        if (ability != null) {
            ability.getInfo().forEach(info -> listOfFeatures.add("<gold><italic:false> " + info));
        }

        //Характеристики предмета
        if (!stats.isEmpty()) {
            for (Map.Entry<StatType, Stat> singleStat : stats.entrySet()) {
                //если у предмета был чар на увеличение стата, то стат увеличивается
                int value = singleStat.getValue().getBaseValue() + singleStat.getValue().getAdditionalValue();
                listOfFeatures.add(String.format("<blue><italic:false>+%d %s", value, singleStat.getKey().getName()));
            }
        }

        return listOfFeatures;
    }
}
