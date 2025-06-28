package net.flectone.cookieclicker.items.attributes;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Stat {
    private int baseValue = 0;
    private int additionalValue = 0;

    public Stat(Integer baseValue) {
        this.baseValue = baseValue;
    }

    public Stat() {}
}
