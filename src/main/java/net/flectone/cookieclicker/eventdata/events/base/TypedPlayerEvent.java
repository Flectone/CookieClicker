package net.flectone.cookieclicker.eventdata.events.base;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import lombok.Getter;
import net.flectone.cookieclicker.entities.playerdata.ServerCookiePlayer;

@Getter
public abstract class TypedPlayerEvent<T extends PacketWrapper<T>> extends BasePlayerEvent {

    private final int packetId;

    protected TypedPlayerEvent(ServerCookiePlayer cookiePlayer, PacketWrapper<T> packetWrapper) {
        super(cookiePlayer);
        this.packetId = packetWrapper.getNativePacketId();
    }
}
