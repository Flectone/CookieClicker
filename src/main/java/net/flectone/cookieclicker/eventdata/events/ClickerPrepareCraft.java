package net.flectone.cookieclicker.eventdata.events;

import lombok.Getter;
import net.flectone.cookieclicker.entities.playerdata.ServerCookiePlayer;
import net.flectone.cookieclicker.eventdata.events.base.BasePlayerEvent;
import net.minecraft.world.inventory.AbstractContainerMenu;

@Getter
public class ClickerPrepareCraft extends BasePlayerEvent {

    private final AbstractContainerMenu container;

    public ClickerPrepareCraft(ServerCookiePlayer cookiePlayer) {
        super(cookiePlayer);

        this.container = cookiePlayer.getPlayer().containerMenu;
    }
}
