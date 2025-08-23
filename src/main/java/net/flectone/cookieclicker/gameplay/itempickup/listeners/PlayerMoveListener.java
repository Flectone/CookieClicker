package net.flectone.cookieclicker.gameplay.itempickup.listeners;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.eventdata.CookieEventHandler;
import net.flectone.cookieclicker.eventdata.CookieListener;
import net.flectone.cookieclicker.eventdata.events.ClickerPlayerMove;
import net.flectone.cookieclicker.gameplay.itempickup.ItemPickupLogic;

@Singleton
public class PlayerMoveListener implements CookieListener {

    private final ItemPickupLogic itemPickupLogic;

    @Inject
    public PlayerMoveListener(ItemPickupLogic itemPickupLogic) {
        this.itemPickupLogic = itemPickupLogic;
    }

    @CookieEventHandler
    public void onPlayerMove(ClickerPlayerMove event) {
        itemPickupLogic.processPlayerMove(event.getCookiePlayer());
    }
}
