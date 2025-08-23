package net.flectone.cookieclicker.eventdata.events;

import com.github.retrooper.packetevents.protocol.player.InteractionHand;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientUseItem;
import lombok.Getter;
import net.flectone.cookieclicker.entities.playerdata.ServerCookiePlayer;
import net.flectone.cookieclicker.eventdata.events.base.TypedPlayerEvent;

@Getter
public class ClickerInteract extends TypedPlayerEvent<WrapperPlayClientUseItem> {

    private final InteractionHand interactionHand;

    public ClickerInteract(ServerCookiePlayer cookiePlayer, WrapperPlayClientUseItem packetWrapper) {
        super(cookiePlayer, packetWrapper);

        this.interactionHand = packetWrapper.getHand();
    }
}
