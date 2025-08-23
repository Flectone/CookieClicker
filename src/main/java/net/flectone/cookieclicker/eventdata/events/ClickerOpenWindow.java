package net.flectone.cookieclicker.eventdata.events;

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenWindow;
import lombok.Getter;
import net.flectone.cookieclicker.entities.playerdata.ServerCookiePlayer;
import net.flectone.cookieclicker.eventdata.events.base.TypedPlayerEvent;

@Getter
public class ClickerOpenWindow extends TypedPlayerEvent<WrapperPlayServerOpenWindow> {
    private final int windowId;
    private final int windowType;

    public ClickerOpenWindow(ServerCookiePlayer cookiePlayer, WrapperPlayServerOpenWindow packetWrapper) {
        super(cookiePlayer, packetWrapper);
        this.windowId = packetWrapper.getContainerId();
        this.windowType = packetWrapper.getType();
    }
}
