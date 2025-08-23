package net.flectone.cookieclicker.gameplay.itempickup.listeners;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.eventdata.CookieEventHandler;
import net.flectone.cookieclicker.eventdata.CookieListener;
import net.flectone.cookieclicker.eventdata.events.ClickerPickupItem;
import net.flectone.cookieclicker.gameplay.itempickup.ItemPickupLogic;

@Singleton
public class ItemPickupListener implements CookieListener {

    private final ItemPickupLogic itemPickupLogic;

    @Inject
    public ItemPickupListener(ItemPickupLogic itemPickupLogic) {
        this.itemPickupLogic = itemPickupLogic;
    }

    @CookieEventHandler
    public void onItemPick(ClickerPickupItem event) {
        itemPickupLogic.compactItems(event.getCookiePlayer());
    }
}
