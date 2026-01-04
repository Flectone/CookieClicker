package net.flectone.cookieclicker.items.itemstacks.base.data;

import lombok.Getter;
import net.flectone.cookieclicker.items.attributes.ToolType;

import java.util.Arrays;

@Getter
public enum ItemTag {

    AIR ("air", ToolType.HIDDEN),
    EMPTY ("none", ToolType.HIDDEN),
    COOKIE ("cookie", ToolType.NONE),
    ENCHANTED_COOKIE ("ench_cookie", ToolType.NONE),
    WOODEN_HOE ("wood_hoe", ToolType.HOE),
    STONE_HOE ("stone_hoe", ToolType.HOE),
    COOKIE_DESTROYER_HOE ("destroyer", ToolType.HOE),
    ROSE_BUSH_HOE ("rose_bush", ToolType.HOE),
    EPIC_HOE ("epic_hoe", ToolType.HOE),
    LEGENDARY_HOE ("leg_hoe", ToolType.HOE),
    COCOA_BEANS ("cocoa_beans", ToolType.NONE),
    ENCHANTED_COCOA_BEANS ("ench_cocoa", ToolType.NONE),
    WHEAT ("wheat", ToolType.NONE),
    ENCHANTED_WHEAT ("ench_wheat", ToolType.NONE),
    BREAD ("bread", ToolType.NONE),
    BAGUETTE ("baguette", ToolType.NONE),
    CHOCOLATE ("chocolate", ToolType.NONE),
    SWEET_BERRIES ("berries", ToolType.NONE),
    FARMER_HELMET ("fHelmet", ToolType.EQUIPMENT),
    FARMER_CHESTPLATE ("fChest", ToolType.EQUIPMENT),
    FARMER_LEGGINGS ("fLegs", ToolType.EQUIPMENT),
    FARMER_BOOTS ("fBoots", ToolType.EQUIPMENT),
    CAKE_UPGRADE_ITEM ("final_cake", ToolType.NONE),
    COOKIE_CRAFTER ("cookie_crafter", ToolType.NONE),
    BLOCK_OF_COOKIE ("cookie_block", ToolType.NONE),
    GLOW_BERRIES ("glow_berries", ToolType.NONE),
    PUMPKIN_PIE ("pie", ToolType.NONE),
    PUMPKIN ("pumpkin", ToolType.NONE),
    BAG_18 ("common_bag", ToolType.BACKPACK),
    BAG_45 ("rare_bag", ToolType.BACKPACK),

    BOOK_COOKIE_BOOST ("ench_book", ToolType.ENCHANTMENT),
    BOOK_MINING_BOOST ("ench_book_mining", ToolType.ENCHANTMENT),
    BOOK_BLOCK_DAMAGE("ench_book_block_dmg", ToolType.ENCHANTMENT),

    MELON_DICER ("melon_axe", ToolType.AXE),
    MELON ("melon", ToolType.NONE),
    ENCHANTED_MELON ("ench_melon", ToolType.NONE),
    MELON_BLOCK ("melon_block", ToolType.NONE),

    HEALING_MELON ("heal_melon", ToolType.NONE),

    PICKAXE ("unused_remove_pls", ToolType.PICKAXE),
    STONE_PICKAXE ("stone_pickaxe", ToolType.PICKAXE),
    COPPER_PICKAXE ("copper_pickaxe", ToolType.PICKAXE),
    STARMETAL_PICKAXE ("starmetal_pickaxe", ToolType.PICKAXE),
    WIND_PICKAXE ("wind_pickaxe", ToolType.PICKAXE),
    EPIC_PICKAXE ("epic_pickaxe", ToolType.PICKAXE),
    LEGENDARY_PICKAXE ("legendary_pickaxe", ToolType.PICKAXE),
    SUNMETAL_PICKAXE ("sunmetal_pickaxe", ToolType.PICKAXE),

    AMBER ("amber", ToolType.MINING_COMMON),
    AMETHYST ("amethyst", ToolType.MINING_COMMON),
    STARMETAL ("starmetal", ToolType.MINING_COMMON),
    WINDMETAL ("wind", ToolType.MINING_COMMON),
    FLAME_METAL ("flame", ToolType.MINING_COMMON),
    SUNMETAL ("sunmetal", ToolType.MINING_COMMON),

    RUNE_BAG ("rune_bag", ToolType.MINING_COMMON),
    AMETHYST_RUNE ("amethyst_rune", ToolType.MINING_COMMON),
    AMBER_RUNE ("amber_rune", ToolType.MINING_COMMON),
    WIND_RUNE ("wind_rune", ToolType.MINING_COMMON),
    STAR_RUNE ("star_rune", ToolType.MINING_COMMON),

    STAR_SHARD ("star_shard", ToolType.MINING_COMMON),
    WIND_ROD ("wind_rod", ToolType.MINING_COMMON);

    private final String realTag;
    private final ToolType category;

    ItemTag(String realTag, ToolType category) {
        this.realTag = realTag;
        this.category = category;
    }

    public static ItemTag fromString(String realTag) {
        return Arrays.stream(values())
                .filter(itemTag -> itemTag.getRealTag().equals(realTag))
                .findAny()
                .orElse(EMPTY);
    }
}
