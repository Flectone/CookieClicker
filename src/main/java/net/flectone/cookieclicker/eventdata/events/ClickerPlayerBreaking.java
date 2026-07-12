package net.flectone.cookieclicker.eventdata.events;

import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.protocol.world.BlockFace;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import lombok.Getter;
import net.flectone.cookieclicker.entities.playerdata.ServerCookiePlayer;
import net.flectone.cookieclicker.eventdata.events.base.TypedPlayerEvent;
import net.flectone.cookieclicker.utility.data.Position;

@Getter
public class ClickerPlayerBreaking extends TypedPlayerEvent<WrapperPlayClientPlayerDigging> {

    private final DiggingAction diggingAction;
    private final Position blockPos;
    private final BlockFace blockFace;

    public ClickerPlayerBreaking(ServerCookiePlayer cookiePlayer, WrapperPlayClientPlayerDigging packetWrapper) {
        super(cookiePlayer, packetWrapper);

        this.diggingAction = packetWrapper.getAction();
        this.blockPos = new Position(packetWrapper.getBlockPosition());
        this.blockFace = packetWrapper.getBlockFace();
    }
}
