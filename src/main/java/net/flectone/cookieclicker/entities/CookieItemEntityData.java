package net.flectone.cookieclicker.entities;

import lombok.Getter;
import lombok.Setter;
import net.flectone.cookieclicker.Position;
import net.flectone.cookieclicker.items.itemstacks.base.data.ItemTag;

@Getter
public class CookieItemEntityData {
    private final ItemTag itemTag;
    private final int count;
    private final int id;
    @Setter
    private Position position;

    public CookieItemEntityData(ItemTag itemTag, Integer count, Integer id) {
        this.itemTag = itemTag;
        this.count = count;
        this.id = id;
    }

}
