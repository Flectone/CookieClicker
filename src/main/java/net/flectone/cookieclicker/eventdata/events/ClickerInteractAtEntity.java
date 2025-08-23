package net.flectone.cookieclicker.eventdata.events;

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import lombok.Getter;
import net.flectone.cookieclicker.entities.playerdata.ServerCookiePlayer;
import net.flectone.cookieclicker.eventdata.events.base.TypedPlayerEvent;

@Getter
public class ClickerInteractAtEntity extends TypedPlayerEvent<WrapperPlayClientInteractEntity> {
    private final int interactedEntityId;
    private final WrapperPlayClientInteractEntity.InteractAction interactAction;

    public ClickerInteractAtEntity(ServerCookiePlayer cookiePlayer, WrapperPlayClientInteractEntity interactPacket) {
        super(cookiePlayer, interactPacket);

        this.interactedEntityId = interactPacket.getEntityId();
        this.interactAction = interactPacket.getAction();
    }
}
