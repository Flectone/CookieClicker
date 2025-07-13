package net.flectone.cookieclicker.playerdata;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityAnimation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class ServerCookiePlayer extends CookiePlayer {
    public ServerCookiePlayer(UUID uuid, int iFrameClicks, int remainXp, int lvl) {
        super(uuid, iFrameClicks, remainXp, lvl);
    }

    public ServerCookiePlayer(UUID uuid) {
        super(uuid);
    }

    public void addXp(Integer amount) {
        int currentXP = remainingXp - amount;
        int newXp;

        while (currentXP <= 0) {
            lvl++;
            newXp = (int) ((Math.log(lvl)/Math.log(1.1d)) + 50 * lvl + 150000);
            currentXP = newXp - Math.abs(currentXP);
        }
        remainingXp = currentXP;
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
            PacketEvents.getAPI().getProtocolManager().sendPacketSilently(
                    PacketEvents.getAPI().getProtocolManager().getChannel(uuid),
                    packetWrapper
            );
        else
            PacketEvents.getAPI().getProtocolManager().sendPacket(
                    PacketEvents.getAPI().getProtocolManager().getChannel(uuid),
                    packetWrapper
            );
    }

    public void sendPEpacket(PacketWrapper<?> packetWrapper) {
        sendPEpacket(packetWrapper, false);
    }

    public void swingArm() {
        WrapperPlayServerEntityAnimation animation = new WrapperPlayServerEntityAnimation(
                getId(),
                WrapperPlayServerEntityAnimation.EntityAnimationType.SWING_MAIN_ARM
        );
        sendPEpacket(animation);
    }
}
