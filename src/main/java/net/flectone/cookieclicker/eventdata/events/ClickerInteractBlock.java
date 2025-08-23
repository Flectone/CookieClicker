package net.flectone.cookieclicker.eventdata.events;

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement;
import net.flectone.cookieclicker.entities.playerdata.ServerCookiePlayer;
import net.flectone.cookieclicker.eventdata.events.base.TypedPlayerEvent;

public class ClickerInteractBlock extends TypedPlayerEvent<WrapperPlayClientPlayerBlockPlacement> {

    public ClickerInteractBlock(ServerCookiePlayer cookiePlayer, WrapperPlayClientPlayerBlockPlacement packetWrapper) {
        super(cookiePlayer, packetWrapper);
    }
}
