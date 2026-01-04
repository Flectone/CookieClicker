package net.flectone.cookieclicker.items.attributes;

import lombok.Getter;

@Getter
public enum ToolType {

    HIDDEN ("hidden", "используется?"),
    NONE ("common", "используется"),
    HOE ("hoe", "в ведущей руке"),
    PICKAXE ("pickaxe", "в ведущей руке"),
    AXE ("axe", "в ведущей руке"),
    SWORD ("sword", "в ведущей руке"),
    EQUIPMENT ("equipment", "экипировано"),
    ENCHANTMENT ("book", "зачаровано на предмет"),
    BACKPACK ("backpack", "открыто"),
    MINING_COMMON ("mining_common", "используется");

    private final String type;
    private final String view;

    ToolType(String name, String display) {
        this.type = name;
        this.view = display;
    }

    public static ToolType from(String name) {
        return switch (name) {
            case "tool", "hoe" -> HOE;
            case "armor", "equipment" -> EQUIPMENT;
            case "book" -> ENCHANTMENT;
            case "pickaxe" -> PICKAXE;
            case "axe" -> AXE;
            case "sword" -> SWORD;
            case "backpack" -> BACKPACK;
            case "mining_common" -> MINING_COMMON;
            default -> NONE; // также basic, common и item
        };
    }
}
