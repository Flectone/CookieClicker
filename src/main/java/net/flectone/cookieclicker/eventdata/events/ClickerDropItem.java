package net.flectone.cookieclicker.eventdata.events;

import net.flectone.cookieclicker.entities.playerdata.ServerCookiePlayer;
import net.flectone.cookieclicker.eventdata.events.base.BasePlayerEvent;

public class ClickerDropItem extends BasePlayerEvent {

    public ClickerDropItem(ServerCookiePlayer cookiePlayer) {
        super(cookiePlayer);
    }
}
