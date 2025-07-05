package net.flectone.cookieclicker.items.attributes;

import lombok.Getter;

import java.util.*;

@Getter
public enum CookieAbility {

    NONE ("none", "вообще этого текста не должно быть,", "вероятно при конвертации плагин не понял,", "чё за способность, и написал это"),
    DESTROYER ("destroyer", "Разделяет печенье на какао-бобы", "и пшеницу."),
    ROSE_BUSH ("rose_bush", "C небольшой вероятностью создаёт", "ягоды вокруг игрока."),
    TRANSFORM ("transform", "С небольшой вероятность создаёт альтернативный", "предмет. Тип предмета также зависит от", "предмета в левой руке."),
    INFINITY_UPGRADE ("infinity", "Каждые 100 кликов повышает удачу фермера на 1,", "до бесконечности");

    private final String type;
    private final List<String> info = new ArrayList<>();

    CookieAbility(String type, String... info) {
        this.type = type;
        this.info.addAll(Arrays.asList(info));
    }

    public static CookieAbility from(String name) {
        return switch (name) {
            case "transform" -> TRANSFORM;
            case "destroyer" -> DESTROYER;
            case "rose_bush" -> ROSE_BUSH;
            case "infinity" -> INFINITY_UPGRADE;
            default -> NONE;
        };
    }
}
