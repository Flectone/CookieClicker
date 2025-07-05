package net.flectone.cookieclicker.items.attributes;

import lombok.Getter;
import net.flectone.cookieclicker.items.itemstacks.base.CookieItems;

@Getter
public enum StatType {

    UNUSED ("none", "чего-то"),
    EQUIPMENT_TIER (CookieItems.EQUIPMENT_TIER, "Уровень предмета"),
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
            default -> UNUSED;
        };
    }
}
