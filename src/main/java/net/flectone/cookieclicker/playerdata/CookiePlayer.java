package net.flectone.cookieclicker.playerdata;

import lombok.Getter;
import lombok.Setter;
import net.flectone.cookieclicker.entities.CookieItemEntityData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class CookiePlayer {
    protected final UUID uuid;
    @Setter
    protected int iFrameClicks = 0;
    @Setter
    protected int remainingXp = 150000;
    @Setter
    protected int lvl = 0;

    protected final List<CookieItemEntityData> items = new ArrayList<>();

    public CookiePlayer(UUID uuid, int iFrameClicks, int remainingXp, int lvl) {
        this.uuid = uuid;
        this.iFrameClicks = iFrameClicks;
        this.remainingXp = remainingXp;
        this.lvl = lvl;
    }

    protected CookiePlayer(CookiePlayer player) {
        this.uuid = player.uuid;
        this.iFrameClicks = player.iFrameClicks;
        this.remainingXp = player.remainingXp;
        this.lvl = player.lvl;
        this.items.addAll(player.getItems());
    }

    public CookiePlayer(UUID uuid) {
        this.uuid = uuid;
    }
}
