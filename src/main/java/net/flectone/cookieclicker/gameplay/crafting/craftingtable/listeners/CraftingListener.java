package net.flectone.cookieclicker.gameplay.crafting.craftingtable.listeners;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.eventdata.events.ClickerInventoryClick;
import net.flectone.cookieclicker.gameplay.crafting.craftingtable.CraftingLogic;
import net.flectone.cookieclicker.eventdata.CookieEventHandler;
import net.flectone.cookieclicker.eventdata.CookieListener;
import net.flectone.cookieclicker.eventdata.events.ClickerPrepareCraft;
import net.flectone.cookieclicker.inventories.containers.ClickerContainer;

@Singleton
public class CraftingListener implements CookieListener {

    private final CraftingLogic craftingLogic;

    @Inject
    public CraftingListener(CraftingLogic craftingLogic) {
        this.craftingLogic = craftingLogic;
    }

    @CookieEventHandler
    public void onCraftPrepare(ClickerPrepareCraft event) {
        craftingLogic.prepareCraft(event.getContainer());
    }

    @CookieEventHandler
    public void onResultClick(ClickerInventoryClick event) {
        if (event.getOpenedContainer().getWindowType() != ClickerContainer.CRAFTING_TABLE_TYPE) return;

        if (event.getSlot() != 0) return;

        craftingLogic.onCraft(event.getCookiePlayer(), event.getClickType());
    }
}
