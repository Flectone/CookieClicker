package net.flectone.cookieclicker.eventdata.events;

import lombok.Getter;
import net.flectone.cookieclicker.entities.playerdata.ServerCookiePlayer;
import net.flectone.cookieclicker.eventdata.events.base.BasePlayerEvent;
import net.minecraft.world.inventory.AnvilMenu;

@Getter
public class ClickerPrepareAnvil extends BasePlayerEvent {

    private final AnvilMenu anvilMenu;

    public ClickerPrepareAnvil(ServerCookiePlayer cookiePlayer, AnvilMenu anvilMenu) {
        super(cookiePlayer);
        this.anvilMenu = anvilMenu;
    }
}
