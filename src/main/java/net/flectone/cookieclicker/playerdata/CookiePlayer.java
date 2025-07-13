package net.flectone.cookieclicker.playerdata;

import lombok.Getter;
import lombok.Setter;

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

    public CookiePlayer(UUID uuid, int iFrameClicks, int remainingXp, int lvl) {
        this.uuid = uuid;
        this.iFrameClicks = iFrameClicks;
        this.remainingXp = remainingXp;
        this.lvl = lvl;
    }

    public CookiePlayer(UUID uuid) {
        this.uuid = uuid;
    }
}
