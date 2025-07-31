package net.flectone.cookieclicker.items.attributes;

import lombok.Getter;

@Getter
public enum ToolType {

    NONE ("basic", "используется"),
    HOE ("hoe", "в ведущей руке"),
    PICKAXE ("pickaxe", "в ведущей руке"),
    EQUIPMENT ("equipment", "экипировано"),
    ENCHANTMENT ("book", "зачаровано на предмет"),
    BACKPACK ("backpack", "открыто");

    private final String type;
    private final String view;

    ToolType(String name, String statView) {
        this.type = name;
        this.view = statView;
    }

    public static ToolType from(String name) {
        return switch (name) {
            case "tool", "hoe" -> HOE;
            case "armor", "equipment" -> EQUIPMENT;
            case "book" -> ENCHANTMENT;
            case "basic", "item" -> NONE;
            case "pickaxe" -> PICKAXE;
            case "backpack" -> BACKPACK;
            default -> NONE;
        };
    }
}
