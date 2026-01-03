package net.flectone.cookieclicker.gameplay.cookiepart.listeners;

import com.github.retrooper.packetevents.protocol.sound.Sounds;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.entities.playerdata.ServerCookiePlayer;
import net.flectone.cookieclicker.eventdata.CookieEventHandler;
import net.flectone.cookieclicker.eventdata.CookieListener;
import net.flectone.cookieclicker.eventdata.events.ClickerInventoryClick;
import net.flectone.cookieclicker.gameplay.window.InventoryMoveLogic;
import net.flectone.cookieclicker.inventories.ContainerManager;
import net.flectone.cookieclicker.inventories.containers.ItemStorage;
import net.flectone.cookieclicker.inventories.containers.MenuContainer;
import net.flectone.cookieclicker.inventories.containers.ShopContainer;
import net.flectone.cookieclicker.utility.PacketUtils;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Singleton
public class InventoryClickListener implements CookieListener {

    private final InventoryMoveLogic inventoryMoveLogic;
    private final ContainerManager containerManager;
    private final PacketUtils packetUtils;

    @Inject
    public InventoryClickListener(InventoryMoveLogic inventoryMoveLogic, ContainerManager containerManager,
                                  PacketUtils packetUtils) {
        this.inventoryMoveLogic = inventoryMoveLogic;
        this.containerManager = containerManager;
        this.packetUtils = packetUtils;
    }

    @CookieEventHandler
    public void onMenuClick(ClickerInventoryClick event) {
        // Покупка предметов, если игрок открыл инвентарь магазина
        if (event.getOpenedContainer() instanceof ShopContainer shopContainer) {
            containerManager.cancelClick(event.getCookiePlayer());
            boolean isSuccessful = shopContainer.buyItem(event.getCookiePlayer().getPlayer().getInventory(), event.getSlot());
            if (isSuccessful) {
                packetUtils.playSound(event.getCookiePlayer().getUser(), Sounds.BLOCK_WOODEN_BUTTON_CLICK_ON, 1f, 1f);
            }
            return;
        }

        if (!(event.getOpenedContainer() instanceof MenuContainer menuContainer)) {
            return;
        }

        BiConsumer<ServerCookiePlayer, WrapperPlayClientClickWindow.WindowClickType> action = menuContainer.getAction(event.getSlot());
        if (action != null) {
            action.accept(event.getCookiePlayer(), event.getClickType());
            return;
        }

        Consumer<ServerCookiePlayer> defaultAction = menuContainer.getDefaultAction();
        if (defaultAction != null) {
            defaultAction.accept(event.getCookiePlayer());
        }
    }

    @CookieEventHandler
    public void onContainerClick(ClickerInventoryClick event) {
        if (!(event.getOpenedContainer() instanceof ItemStorage itemStorage)) {
            return;
        }

        if (event.getSlot() == -999) return;

        inventoryMoveLogic.placeItems(event.getCookiePlayer(), event.getSlot(), event.getClickType(), event.getButton());
    }
}
