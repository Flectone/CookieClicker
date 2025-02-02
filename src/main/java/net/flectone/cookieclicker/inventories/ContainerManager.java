package net.flectone.cookieclicker.inventories;

import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenWindow;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.utility.CCobjects.CookiePlayer;
import net.flectone.cookieclicker.utility.UtilsCookie;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.core.NonNullList;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundSetCursorItemPacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerInventoryPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.HashMap;
import java.util.UUID;

@Singleton
public class ContainerManager {
    private final HashMap<UUID, ClickerContainer> openedContainers = new HashMap<>();

    private final UtilsCookie utilsCookie;
    @Inject
    public ContainerManager(UtilsCookie utilsCookie) {
        this.utilsCookie = utilsCookie;
    }

    public void setOpenedContainer(UUID uuid, ClickerContainer container) {
        openedContainers.put(uuid, container);
    }
    public void setOpenedContainer(UUID uuid, WrapperPlayServerOpenWindow packet, String data) {
        ClickerContainer container = new ClickerContainer(packet.getContainerId(), packet.getType(), data);
        setOpenedContainer(uuid, container);
    }

    public Integer closeContainer(User user) {
        int containerId = 0;
        UUID uuid = user.getUUID();
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

    public ClickerContainer getOpenedContainer(CookiePlayer cookiePlayer) {
        return getOpenedContainer(cookiePlayer.getUuid());
    }

    public ClickerContainer getOpenedContainer(UUID uuid) {
        if (openedContainers.isEmpty() || !openedContainers.containsKey(uuid))
            return new ClickerContainer(0, 0, "none");
        return openedContainers.get(uuid);
    }

    public void openContainer(CookiePlayer cookiePlayer, ClickerContainer container) {
        WrapperPlayServerOpenWindow openWindowPacket = new WrapperPlayServerOpenWindow(container.getWindowId(),
                container.getWindowType(),
                MiniMessage.miniMessage().deserialize(container.getTitle()));

        setOpenedContainer(cookiePlayer.getUuid(), container);
        cookiePlayer.sendPEpacket(openWindowPacket, true);

        ClientboundContainerSetContentPacket packet = new ClientboundContainerSetContentPacket(container.getWindowId(),
                1, container.getContainerItems(), new net.minecraft.world.item.ItemStack(Items.AIR));
        cookiePlayer.sendNMSpacket(packet);
    }

    public void anvilClick(Player player, Integer slot) {
        if (slot != 2) return;
        //в updateStats() есть проверка на воздух, поэтому можно не делать тут проверку
        utilsCookie.updateStats(player.containerMenu.getSlot(2).getItem());
    }

    private void setSlot(Player player, ClickerContainer container, Integer slot, ItemStack itemStack, boolean isPlayerInventory) {
        Connection playerConnection = ((ServerPlayer) player).connection.connection;

        if (slot == -1) {
            playerConnection.send(new ClientboundSetCursorItemPacket(itemStack));
            return;
        }

        playerConnection.send(isPlayerInventory
                    ? new ClientboundSetPlayerInventoryPacket(slot, itemStack)
                    : new ClientboundContainerSetSlotPacket(container.getWindowId(), 1, slot, itemStack));
    }

    public void setContainerSlot(Player player, ClickerContainer container, Integer slot, ItemStack itemStack) {
        setSlot(player, container, slot, itemStack, false);
    }

    public void setPlayerSlot(Player player, Integer slot, ItemStack itemStack) {
        setSlot(player, null, slot, itemStack, true);
    }

    public void updatePlayerInventory(Player player) {
        for (int i = 0; i < 36; i++) {
            setPlayerSlot(player, i, player.getInventory().getItem(i));
        }
        //почему слот 40 - левая рука, я хз, должен быть слот 45
        setPlayerSlot(player, 40, player.getItemInHand(InteractionHand.OFF_HAND));
    }

    public void updateContainer(Player player, ClickerContainer container) {
        NonNullList<ItemStack> itemsInContainer = container.getContainerItems();
        for (int slot = 0; slot < itemsInContainer.size(); slot++) {
            setContainerSlot(player, container,
                    slot, itemsInContainer.get(slot));
        }
    }

    public void cancelClick(Player player, ClickerContainer container, Integer slot, WrapperPlayClientClickWindow.WindowClickType clickType) {
        if (slot < container.getContainerItems().size()) {
            setContainerSlot(player, container, slot, container.getContainerItems().get(slot));
        }
        setContainerSlot(player, container, -1, new ItemStack(Items.AIR));

        updatePlayerInventory(player);

        //если шифт клик
        if (clickType.equals(WrapperPlayClientClickWindow.WindowClickType.QUICK_MOVE)) {
            updateContainer(player, container);
        }
    }
}
