package net.flectone.cookieclicker.eventdata.events.base;

import lombok.Getter;
import lombok.Setter;
import net.flectone.cookieclicker.entities.playerdata.ServerCookiePlayer;

@Getter
public abstract class BasePlayerEvent {

    @Setter
    private boolean cancelled = false;
    private final ServerCookiePlayer cookiePlayer;

    protected BasePlayerEvent(ServerCookiePlayer cookiePlayer) {
        this.cookiePlayer = cookiePlayer;
    }
}
