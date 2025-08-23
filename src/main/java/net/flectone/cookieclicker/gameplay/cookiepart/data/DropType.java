package net.flectone.cookieclicker.gameplay.cookiepart.data;

import lombok.Getter;
import net.flectone.cookieclicker.items.attributes.CookieAbility;
import net.flectone.cookieclicker.items.itemstacks.base.data.ItemTag;

import java.util.List;

@Getter
public enum DropType {

    DEFAULT (ItemTag.PUMPKIN_PIE, ItemTag.COOKIE),
    COOKIE_PARTS(ItemTag.PUMPKIN, ItemTag.COCOA_BEANS, ItemTag.WHEAT),
    BERRIES (ItemTag.SWEET_BERRIES, ItemTag.COOKIE),
    BERRIES_ALT(ItemTag.GLOW_BERRIES, ItemTag.COOKIE);

    private final List<ItemTag> tags;
    private final ItemTag altTag;

    DropType(ItemTag altTag, ItemTag... baseDrops) {
        this.tags = List.of(baseDrops);
        this.altTag = altTag;
    }

    public static DropType fromAbility(CookieAbility ability) {
        return switch (ability) {
            case DESTROYER -> COOKIE_PARTS;
            case ROSE_BUSH -> BERRIES;
            case null, default -> DEFAULT;
        };
    }
}
