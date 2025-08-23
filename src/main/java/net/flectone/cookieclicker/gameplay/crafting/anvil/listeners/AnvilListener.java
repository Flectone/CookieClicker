package net.flectone.cookieclicker.gameplay.crafting.anvil.listeners;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.eventdata.CookieEventHandler;
import net.flectone.cookieclicker.eventdata.CookieListener;
import net.flectone.cookieclicker.eventdata.events.ClickerInventoryClick;
import net.flectone.cookieclicker.eventdata.events.ClickerPrepareAnvil;
import net.flectone.cookieclicker.gameplay.crafting.anvil.AnvilItemUpgrade;
import net.flectone.cookieclicker.inventories.containers.ClickerContainer;

@Singleton
public class AnvilListener implements CookieListener {

    private final AnvilItemUpgrade anvilItemUpgrade;

    @Inject
    public AnvilListener(AnvilItemUpgrade anvilItemUpgrade) {
        this.anvilItemUpgrade = anvilItemUpgrade;
    }

    @CookieEventHandler
    public void anvilPrepare(ClickerPrepareAnvil event) {
        anvilItemUpgrade.checkForUpgrade(event.getAnvilMenu());
    }

    @CookieEventHandler
    public void onAnvilInvClick(ClickerInventoryClick event) {
        if (event.getOpenedContainer().getWindowType() != ClickerContainer.ANVIL_TYPE) return;

        anvilItemUpgrade.anvilClick(event.getCookiePlayer().getPlayer(), event.getSlot());
    }
}
