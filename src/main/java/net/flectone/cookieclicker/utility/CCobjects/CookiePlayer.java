package net.flectone.cookieclicker.utility.CCobjects;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityAnimation;
import lombok.Getter;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class CookiePlayer {
    @Getter
    private final UUID uuid;
    public boolean jigsawLock = true;

    public CookiePlayer(UUID uuid) {
        this.uuid = uuid;
    }

    private User getUser(UUID uuid) {
        Object channel = PacketEvents.getAPI().getProtocolManager().getChannel(uuid);
        if (channel == null) return null;

        return PacketEvents.getAPI().getProtocolManager().getUser(channel);
    }

    public User getUser() {
        return getUser(uuid);
    }

    private Player getPlayer(UUID uuid) {
        //короче, тут я беру всех игроков на сервере и с помощью UUID нахожу нужного
        Player player = null;
        for (Player i : MinecraftServer.getServer().getPlayerList().players) {
            if (i.getUUID().equals(uuid))
                player = i;
        }
        return player;
    }

    public Player getPlayer() {
        return getPlayer(uuid);
    }

    public Integer getId() {
        return getUser().getEntityId();
    }

    public void sendPEpacket(PacketWrapper<?> packetWrapper, boolean silent) {
        if (silent)
            getUser().sendPacketSilently(packetWrapper);
        else
            getUser().sendPacket(packetWrapper);
    }

    public void sendPEpacket(PacketWrapper<?> packetWrapper) {
        sendPEpacket(packetWrapper, false);
    }

    public void sendNMSpacket(Packet<?> packet) {
        ((ServerPlayer) getPlayer()).connection.connection.send(packet);
    }

    public void swingArm() {
        WrapperPlayServerEntityAnimation animation = new WrapperPlayServerEntityAnimation(getId(), WrapperPlayServerEntityAnimation.EntityAnimationType.SWING_MAIN_ARM);
        sendPEpacket(animation);
    }
}
