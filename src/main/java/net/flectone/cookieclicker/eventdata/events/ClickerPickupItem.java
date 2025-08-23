package net.flectone.cookieclicker.eventdata.events;

import net.flectone.cookieclicker.entities.playerdata.ServerCookiePlayer;
import net.flectone.cookieclicker.eventdata.events.base.BasePlayerEvent;

public class ClickerPickupItem extends BasePlayerEvent {
    public ClickerPickupItem(ServerCookiePlayer cookiePlayer) {
        super(cookiePlayer);
    }
}
