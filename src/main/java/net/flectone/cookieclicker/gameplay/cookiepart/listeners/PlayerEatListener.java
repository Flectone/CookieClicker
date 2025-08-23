package net.flectone.cookieclicker.gameplay.cookiepart.listeners;

import com.google.inject.Singleton;
import net.flectone.cookieclicker.eventdata.CookieEventHandler;
import net.flectone.cookieclicker.eventdata.CookieListener;
import net.flectone.cookieclicker.eventdata.events.ClickerPlayerEat;
import net.flectone.cookieclicker.items.itemstacks.base.data.Features;
import net.flectone.cookieclicker.items.itemstacks.base.data.ItemTag;

@Singleton
public class PlayerEatListener implements CookieListener {

    @CookieEventHandler
    public void onPlayerEat(ClickerPlayerEat event) {
        if (new Features(event.getEatenItem()).getItemTag() == ItemTag.ENCHANTED_COOKIE) {
            event.getCookiePlayer().getPlayer().giveExperiencePoints(3);
        }
    }
}
