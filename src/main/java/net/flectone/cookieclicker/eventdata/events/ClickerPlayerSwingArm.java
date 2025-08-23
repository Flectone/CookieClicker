package net.flectone.cookieclicker.eventdata.events;

import com.github.retrooper.packetevents.protocol.player.InteractionHand;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientAnimation;
import lombok.Getter;
import net.flectone.cookieclicker.entities.playerdata.ServerCookiePlayer;
import net.flectone.cookieclicker.eventdata.events.base.TypedPlayerEvent;

@Getter
public class ClickerPlayerSwingArm extends TypedPlayerEvent<WrapperPlayClientAnimation> {

    private final InteractionHand interactionHand;

    public ClickerPlayerSwingArm(ServerCookiePlayer cookiePlayer, WrapperPlayClientAnimation packetWrapper) {
        super(cookiePlayer, packetWrapper);

        this.interactionHand = packetWrapper.getHand();
    }
}
