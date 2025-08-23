package net.flectone.cookieclicker.eventdata.events;

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCloseWindow;
import lombok.Getter;
import net.flectone.cookieclicker.entities.playerdata.ServerCookiePlayer;
import net.flectone.cookieclicker.eventdata.events.base.TypedPlayerEvent;

@Getter
public class ClickerCloseWindow extends TypedPlayerEvent<WrapperPlayClientCloseWindow> {

    private final int windowId;

    public ClickerCloseWindow(ServerCookiePlayer cookiePlayer, WrapperPlayClientCloseWindow packetWrapper) {
        super(cookiePlayer, packetWrapper);
        this.windowId = packetWrapper.getWindowId();
    }
}
