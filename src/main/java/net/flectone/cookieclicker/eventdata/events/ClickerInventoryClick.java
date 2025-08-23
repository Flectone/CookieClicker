package net.flectone.cookieclicker.eventdata.events;

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import lombok.Getter;
import net.flectone.cookieclicker.entities.playerdata.ServerCookiePlayer;
import net.flectone.cookieclicker.eventdata.events.base.TypedPlayerEvent;
import net.flectone.cookieclicker.inventories.containers.ClickerContainer;

@Getter
public class ClickerInventoryClick extends TypedPlayerEvent<WrapperPlayClientClickWindow> {

    private final WrapperPlayClientClickWindow.WindowClickType clickType;
    private final int slot;
    private final int windowId;
    private final int button;

    private final ClickerContainer openedContainer;

    public ClickerInventoryClick(ServerCookiePlayer cookiePlayer, WrapperPlayClientClickWindow packetWrapper, ClickerContainer openedContainer) {
        super(cookiePlayer, packetWrapper);

        this.windowId = packetWrapper.getWindowId();
        this.button = packetWrapper.getButton();
        this.clickType = packetWrapper.getWindowClickType();
        this.slot = packetWrapper.getSlot();

        this.openedContainer = openedContainer;
    }
}
