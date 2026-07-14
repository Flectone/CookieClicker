package net.flectone.cookieclicker.eventdata.events;

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientAttack;
import lombok.Getter;
import net.flectone.cookieclicker.entities.playerdata.ServerCookiePlayer;
import net.flectone.cookieclicker.eventdata.events.base.TypedPlayerEvent;

@Getter
public class ClickerAttackEntity extends TypedPlayerEvent<WrapperPlayClientAttack> {
    private final int interactedEntityId;

    public ClickerAttackEntity(ServerCookiePlayer cookiePlayer, WrapperPlayClientAttack attackPacket) {
        super(cookiePlayer, attackPacket);

        this.interactedEntityId = attackPacket.getEntityId();
    }
}
