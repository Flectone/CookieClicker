package net.flectone.cookieclicker.gameplay.window.listeners;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.entities.playerdata.ServerCookiePlayer;
import net.flectone.cookieclicker.eventdata.CookieEventHandler;
import net.flectone.cookieclicker.eventdata.CookieListener;
import net.flectone.cookieclicker.eventdata.events.ClickerCloseWindow;
import net.flectone.cookieclicker.eventdata.events.ClickerOpenWindow;
import net.flectone.cookieclicker.gameplay.window.InventoryMoveLogic;
import net.flectone.cookieclicker.inventories.ContainerManager;
import net.flectone.cookieclicker.inventories.containers.ClickerContainer;
import net.flectone.cookieclicker.inventories.containers.ItemStorage;

@Singleton
public class WindowListener implements CookieListener {

    private final ContainerManager containerManager;
    private final InventoryMoveLogic inventoryMoveLogic;

    @Inject
    public WindowListener(ContainerManager containerManager,
                          InventoryMoveLogic inventoryMoveLogic) {
        this.containerManager = containerManager;
        this.inventoryMoveLogic = inventoryMoveLogic;
    }

    @CookieEventHandler
    public void onWindowOpen(ClickerOpenWindow event) {
        int type = event.getWindowType();
        if (type != 8 && type != 12) return;

        containerManager.openContainer(event.getCookiePlayer(), new ClickerContainer(event.getWindowId(), type, "vanilla"));
    }

    @CookieEventHandler
    public void onWindowClose(ClickerCloseWindow event) {
        ServerCookiePlayer serverCookiePlayer = event.getCookiePlayer();

        if (containerManager.getOpenedContainer(serverCookiePlayer) instanceof ItemStorage storage) {
            inventoryMoveLogic.applyContentsToItem(storage.getBoundItem(), storage);
        }

        containerManager.closeContainer(serverCookiePlayer.getUuid());
    }
}
