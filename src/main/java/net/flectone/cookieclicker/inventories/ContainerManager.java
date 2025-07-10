package net.flectone.cookieclicker.inventories;

import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.utility.CCConversionUtils;
import net.flectone.cookieclicker.playerdata.ServerCookiePlayer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.core.NonNullList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Singleton
public class ContainerManager {
    private final HashMap<UUID, ClickerContainer> openedContainers = new HashMap<>();

    private final CCConversionUtils converter;

    @Inject
    public ContainerManager(CCConversionUtils converter) {
        this.converter = converter;
    }

    public void setOpenedContainer(UUID uuid, ClickerContainer container) {
        openedContainers.put(uuid, container);
    }
    public void setOpenedContainer(UUID uuid, WrapperPlayServerOpenWindow packet, String data) {
        ClickerContainer container = new ClickerContainer(packet.getContainerId(), packet.getType(), data);
        setOpenedContainer(uuid, container);
    }

    public Integer closeContainer(UUID uuid) {
        int containerId = 0;
        if (!openedContainers.isEmpty() && openedContainers.containsKey(uuid)) {
            containerId = openedContainers.get(uuid).getWindowId();
            openedContainers.remove(uuid);
        }
        return containerId;
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

    public ClickerContainer getOpenedContainer(UUID uuid) {
        if (openedContainers.isEmpty() || !openedContainers.containsKey(uuid))
            return new ClickerContainer(0, 0, "none");
        return openedContainers.get(uuid);
    }

    public void openContainer(ServerCookiePlayer serverCookiePlayer, ClickerContainer container) {
        WrapperPlayServerOpenWindow openWindowPacket = new WrapperPlayServerOpenWindow(container.getWindowId(),
                container.getWindowType(),
                MiniMessage.miniMessage().deserialize(container.getTitle()));

        setOpenedContainer(serverCookiePlayer.getUuid(), container);
        serverCookiePlayer.sendPEpacket(openWindowPacket, true);

        List<com.github.retrooper.packetevents.protocol.item.ItemStack> convertedList = new ArrayList<>();
        container.getContainerItems().forEach(itemStack -> {
            convertedList.add(converter.fromMinecraftStack(itemStack, MinecraftServer.getServer().registryAccess()));
        });

        serverCookiePlayer.sendPEpacket(new WrapperPlayServerWindowItems(container.getWindowId(), 1, convertedList, null));

//        ClientboundContainerSetContentPacket packet = new ClientboundContainerSetContentPacket(container.getWindowId(),
//                1, container.getContainerItems(), new net.minecraft.world.item.ItemStack(Items.AIR));
//        cookiePlayer.sendNMSpacket(packet);
    }

    private void setSlot(ServerCookiePlayer serverCookiePlayer, ClickerContainer container, Integer slot, ItemStack itemStack, boolean isPlayerInventory) {
        com.github.retrooper.packetevents.protocol.item.ItemStack convertedStack = converter.fromMinecraftStack(itemStack, MinecraftServer.getServer().registryAccess());

        if (slot == -1) {
            serverCookiePlayer.sendPEpacket(new WrapperPlayServerSetCursorItem(convertedStack));
            return;
        }

        serverCookiePlayer.sendPEpacket(isPlayerInventory
                    ? new WrapperPlayServerSetPlayerInventory(slot, convertedStack)
                    : new WrapperPlayServerSetSlot(container.getWindowId(), 1, slot, convertedStack),
                true
        );
    }

    public void setContainerSlot(ServerCookiePlayer serverCookiePlayer, ClickerContainer container, Integer slot, ItemStack itemStack) {
        setSlot(serverCookiePlayer, container, slot, itemStack, false);
    }

    public void setPlayerSlot(ServerCookiePlayer serverCookiePlayer, Integer slot, ItemStack itemStack) {
        setSlot(serverCookiePlayer, null, slot, itemStack, true);
    }

    public void updatePlayerInventory(ServerCookiePlayer serverCookiePlayer) {
        for (int i = 0; i < 36; i++) {
            setPlayerSlot(serverCookiePlayer, i, serverCookiePlayer.getPlayer().getInventory().getItem(i));
        }
        //почему слот 40 - левая рука, я хз, должен быть слот 45
        setPlayerSlot(serverCookiePlayer, 40, serverCookiePlayer.getPlayer().getItemInHand(InteractionHand.OFF_HAND));
    }

    public void updateContainer(ServerCookiePlayer serverCookiePlayer, ClickerContainer container) {
        NonNullList<ItemStack> itemsInContainer = container.getContainerItems();
        for (int slot = 0; slot < itemsInContainer.size(); slot++) {
            setContainerSlot(serverCookiePlayer, container,
                    slot, itemsInContainer.get(slot));
        }
    }

    public void cancelClick(ServerCookiePlayer serverCookiePlayer, ClickerContainer container, Integer slot, WrapperPlayClientClickWindow.WindowClickType clickType) {
        if (slot < container.getContainerItems().size()) {
            setContainerSlot(serverCookiePlayer, container, slot, container.getContainerItems().get(slot));
        }
        setContainerSlot(serverCookiePlayer, container, -1, new ItemStack(Items.AIR));

        updatePlayerInventory(serverCookiePlayer);

        //если шифт клик
        if (clickType.equals(WrapperPlayClientClickWindow.WindowClickType.QUICK_MOVE)) {
            updateContainer(serverCookiePlayer, container);
        }
    }
}
