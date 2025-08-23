package net.flectone.cookieclicker.eventdata.events;

import net.flectone.cookieclicker.entities.playerdata.ServerCookiePlayer;
import net.flectone.cookieclicker.eventdata.events.base.BasePlayerEvent;
import net.flectone.cookieclicker.utility.data.Position;
import net.minecraft.world.entity.player.Player;

// в PE три пакета на движение и все они наследуют пакет на полёт
public class ClickerPlayerMove extends BasePlayerEvent {

    public ClickerPlayerMove(ServerCookiePlayer cookiePlayer) {
        super(cookiePlayer);
    }

    public Position getPlayerPos() {
        Player player = getCookiePlayer().getPlayer();
        return new Position(player.getX(), player.getY(), player.getZ());
    }
}
