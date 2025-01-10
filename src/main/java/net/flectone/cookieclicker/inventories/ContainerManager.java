package net.flectone.cookieclicker.inventories;

import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenWindow;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.utility.UtilsCookie;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundSetCursorItemPacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerInventoryPacket;
import net.minecraft.server.level.ServerPlayer;
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

    public void setOpenedContainer(User user, ClickerContainer container) {
        openedContainers.put(user.getUUID(), container);
    }
    public void setOpenedContainer(User user, WrapperPlayServerOpenWindow packet, String data) {
        ClickerContainer container = new ClickerContainer(packet.getContainerId(), packet.getType(), data);
        setOpenedContainer(user, container);
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

    public ClickerContainer getOpenedContainer(User user) {
        if (openedContainers.isEmpty() || !openedContainers.containsKey(user.getUUID()))
            return new ClickerContainer(0, 0, "none");
        return openedContainers.get(user.getUUID());
    }

    public void openContainer(User user, Player player, ClickerContainer container) {
        WrapperPlayServerOpenWindow openWindowPacket = new WrapperPlayServerOpenWindow(container.getWindowId(),
                container.getWindowType(),
                MiniMessage.miniMessage().deserialize(container.getTitle()));
        setOpenedContainer(user, container);
        user.sendPacketSilently(openWindowPacket);
        ClientboundContainerSetContentPacket packet = new ClientboundContainerSetContentPacket(container.getWindowId(),
                1, container.getContainerItems(), new net.minecraft.world.item.ItemStack(Items.AIR));
        ((ServerPlayer) player).connection.connection.send(packet);



    }

    public void anvilClick(Player player, Integer slot) {
        if (slot != 2) return;
        //в updateStats() есть проверка на воздух, поэтому можно не делать тут проверку
        utilsCookie.updateStats(player.containerMenu.getSlot(2).getItem());
    }

    public void setSlot(Player player, ClickerContainer container, Integer slot, ItemStack itemStack, boolean isPlayerInventory) {
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


}
