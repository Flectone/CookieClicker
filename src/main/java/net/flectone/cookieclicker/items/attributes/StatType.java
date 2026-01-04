package net.flectone.cookieclicker.items.attributes;

import lombok.Getter;
import net.flectone.cookieclicker.items.itemstacks.base.CookieItems;

@Getter
public enum StatType {

    UNUSED ("none", "чего-то"),
    EQUIPMENT_TIER (CookieItems.EQUIPMENT_TIER, "Уровень предмета"),
    ADDITIONAL_SLOT(CookieItems.ADDITIONAL_SLOTS_KEY, "Слоты хранения"),
    MINING_POWER (CookieItems.MINING_POWER_KEY, "Сила шахтёра"),
    BLOCK_DAMAGE (CookieItems.BLOCK_DAMAGE_KEY, "Урон по блоку"),
    FARMING_FORTUNE (CookieItems.FORTUNE_KEY, "Удача фермера"),
    MINING_FORTUNE (CookieItems.MINING_FORTUNE_KEY, "Удача шахтёра");

    private final String tag;
    private final String name;

    StatType(String tag, String name) {
        this.tag = tag;
        this.name = name;
    }

    public static StatType from(String name) {
        return switch (name) {
            case CookieItems.FORTUNE_KEY, CookieItems.OLD_FORTUNE_KEY -> FARMING_FORTUNE;
            case CookieItems.MINING_FORTUNE_KEY -> MINING_FORTUNE;
            case CookieItems.EQUIPMENT_TIER -> EQUIPMENT_TIER;
            case CookieItems.ADDITIONAL_SLOTS_KEY -> ADDITIONAL_SLOT;
            case CookieItems.MINING_POWER_KEY -> MINING_POWER;
            case CookieItems.BLOCK_DAMAGE_KEY -> BLOCK_DAMAGE;
            default -> UNUSED;
        };
    }
}
