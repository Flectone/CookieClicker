package net.flectone.cookieclicker.entities.playerdata;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerCollectItem;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityAnimation;
import net.flectone.cookieclicker.entities.objects.item.CookieItemEntityData;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class ServerCookiePlayer extends CookiePlayer {
    public ServerCookiePlayer(CookiePlayer cookiePlayer) {
        super(cookiePlayer);
    }

    public ServerCookiePlayer(UUID uuid) {
        super(uuid);
    }

    public void addXp(Integer amount) {
        int currentXP = remainingXp - amount;
        int newXp;

        while (currentXP <= 0) {
            lvl++;
            newXp = (int) ((Math.log(lvl)/Math.log(1.1d)) + 50 * lvl + 90000);
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

    private Object getChannel() {
        return PacketEvents.getAPI().getProtocolManager().getChannel(uuid);
    }

    public void receivePacket(PacketWrapper<?> packetWrapper) {
        PacketEvents.getAPI().getProtocolManager().receivePacketSilently(getChannel(), packetWrapper);
    }

    public void sendPEpacket(PacketWrapper<?> packetWrapper, boolean silent) {
        if (silent) {
            PacketEvents.getAPI().getProtocolManager().sendPacketSilently(
                    getChannel(), packetWrapper);
        } else {
            PacketEvents.getAPI().getProtocolManager().sendPacket(
                    getChannel(), packetWrapper);
        }
    }

    public void sendPEpacket(PacketWrapper<?> packetWrapper) {
        sendPEpacket(packetWrapper, false);
    }

    public void sendMinecraftPacket(Packet<?> packet) {
        ServerPlayer serverPlayer = MinecraftServer.getServer().getPlayerList().getPlayer(uuid);
        if (serverPlayer == null) return;

        serverPlayer.connection.send(packet);
    }

    public void swingArm() {
        WrapperPlayServerEntityAnimation animation = new WrapperPlayServerEntityAnimation(
                getId(),
                WrapperPlayServerEntityAnimation.EntityAnimationType.SWING_MAIN_ARM
        );
        sendPEpacket(animation);
    }

    public void addSpawnedItem(CookieItemEntityData itemEntityData) {
        if (items.size() >= 10) {
            sendPEpacket(new WrapperPlayServerDestroyEntities(items.getFirst().getId()));
            items.removeFirst();
        }
        items.add(itemEntityData);
    }

    public void pickUpItem(CookieItemEntityData itemData) {
        WrapperPlayServerDestroyEntities destroyPacket = new WrapperPlayServerDestroyEntities(itemData.getId());
        WrapperPlayServerCollectItem collectPacket = new WrapperPlayServerCollectItem(itemData.getId(), getId(), itemData.getCount());

        sendPEpacket(collectPacket);
        sendPEpacket(destroyPacket);

        items.remove(itemData);
    }

    //обычный getFreeSlot() возвращает неправильный слот.
    //Точнее он правильный, но для шифт клика вычисляется другой слот
    public int getFreeSlot() {
        Inventory inventory = getPlayer().getInventory();

        for (int i = 8; i >= 0; i--) {
            if (inventory.getItem(i).isEmpty()) return i;
        }

        for (int i = 35; i >= 9; i--) {
            if (inventory.getItem(i).isEmpty()) return i;
        }
        return -1;
    }
}
