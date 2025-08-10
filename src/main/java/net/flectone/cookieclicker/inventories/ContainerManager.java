package net.flectone.cookieclicker.inventories;

import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenWindow;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetCursorItem;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPlayerInventory;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.inventories.containers.ClickerContainer;
import net.flectone.cookieclicker.entities.playerdata.ServerCookiePlayer;
import net.flectone.cookieclicker.utility.ConversionUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.HashMap;
import java.util.UUID;

@Singleton
public class ContainerManager {

    private final HashMap<UUID, ClickerContainer> openedContainers = new HashMap<>();

    public void setOpenedContainer(UUID uuid, ClickerContainer container) {
        openedContainers.put(uuid, container);
    }
    public void setOpenedContainer(UUID uuid, WrapperPlayServerOpenWindow packet, String data) {
        ClickerContainer container = new ClickerContainer(packet.getContainerId(), packet.getType(), data);
        setOpenedContainer(uuid, container);
    }

    public void closeContainer(UUID uuid) {
        if (!openedContainers.isEmpty()) {
            openedContainers.remove(uuid);
        }
    }

    public ClickerContainer getOpenedContainer(UUID uuid) {
        return openedContainers.getOrDefault(uuid, ClickerContainer.EMPTY);
    }

    public ClickerContainer getOpenedContainer(Player player) {
        return getOpenedContainer(player.getUUID());
    }

    public ClickerContainer getOpenedContainer(User user) {
        return getOpenedContainer(user.getUUID());
    }

    public ClickerContainer getOpenedContainer(ServerCookiePlayer serverCookiePlayer) {
        return getOpenedContainer(serverCookiePlayer.getUuid());
    }

    private void updateContainerContents(ServerCookiePlayer serverCookiePlayer, ClickerContainer container, ItemStack carried) {
        serverCookiePlayer.sendMinecraftPacket(new ClientboundContainerSetContentPacket(
                container.getWindowId(), 0,
                container.getContainerItems(), carried));
    }

    public void openContainer(ServerCookiePlayer serverCookiePlayer, ClickerContainer container) {
        WrapperPlayServerOpenWindow openWindowPacket = new WrapperPlayServerOpenWindow(container.getWindowId(),
                container.getWindowType(),
                MiniMessage.miniMessage().deserialize(container.getTitle()));

        setOpenedContainer(serverCookiePlayer.getUuid(), container);
        serverCookiePlayer.sendPEpacket(openWindowPacket, true);

        updateContainerContents(serverCookiePlayer, container, new ItemStack(Items.AIR));
    }

    public void cancelClick(ServerCookiePlayer serverCookiePlayer) {
        updateWindow(serverCookiePlayer);
    }

    private com.github.retrooper.packetevents.protocol.item.ItemStack getPacketEventsStack(ItemStack itemStackMinecraft) {
        return ConversionUtils.fromMinecraftStack(itemStackMinecraft, MinecraftServer.getServer().registryAccess());
    }

    public void setContainerItem(ServerCookiePlayer serverCookiePlayer, int slot, ItemStack itemStack) {
        ClickerContainer openedContainer = getOpenedContainer(serverCookiePlayer);
        serverCookiePlayer.sendPEpacket(new WrapperPlayServerSetSlot(
                openedContainer.getWindowId(), 1, slot, getPacketEventsStack(itemStack)
        ), true);

        if (openedContainer.getWindowType() <= 6) {
            openedContainer.setItem(slot, itemStack);
        }
    }

    public void setPlayerInvItem(ServerCookiePlayer serverCookiePlayer, int slot, ItemStack itemStack) {
        serverCookiePlayer.sendPEpacket(new WrapperPlayServerSetPlayerInventory(slot, getPacketEventsStack(itemStack)));

        serverCookiePlayer.getPlayer().getInventory().setItem(slot, itemStack);
    }

    public void setCursorItem(ServerCookiePlayer serverCookiePlayer, ItemStack itemStack) {
        serverCookiePlayer.sendPEpacket(new WrapperPlayServerSetCursorItem(getPacketEventsStack(itemStack)));

        serverCookiePlayer.getPlayer().containerMenu.setCarried(itemStack);
    }

    public void setItem(ServerCookiePlayer serverCookiePlayer, ItemStack stack, int slot, boolean isPlayerInv) {
        if (isPlayerInv) {
            setPlayerInvItem(serverCookiePlayer, slot, stack);
        } else {
            setContainerItem(serverCookiePlayer, slot, stack);
        }
    }

    public void updatePlayerInventory(ServerCookiePlayer serverCookiePlayer) {
        Player player = serverCookiePlayer.getPlayer();

        for (int i = 0; i < 36; i++) {
            setPlayerInvItem(serverCookiePlayer, i, player.getInventory().getItem(i));
        }
        //почему слот 40 - левая рука, я хз, должен быть слот 45
        setPlayerInvItem(serverCookiePlayer, 40, player.getItemInHand(InteractionHand.OFF_HAND));
    }

    public void updateWindow(ServerCookiePlayer serverCookiePlayer) {
        ClickerContainer container = getOpenedContainer(serverCookiePlayer);
        Player player = serverCookiePlayer.getPlayer();

        updateContainerContents(serverCookiePlayer, container, player.containerMenu.getCarried());

        updatePlayerInventory(serverCookiePlayer);
    }
}
