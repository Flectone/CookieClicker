package net.flectone.cookieclicker.gameplay.cookiepart.listeners;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.eventdata.CookieEventHandler;
import net.flectone.cookieclicker.eventdata.CookieListener;
import net.flectone.cookieclicker.eventdata.events.ClickerInteractBlock;
import net.flectone.cookieclicker.gameplay.cookiepart.BookshelfClickLogic;

@Singleton
public class PlayerInteractBlockListener implements CookieListener {

    private final BookshelfClickLogic bookshelfClickLogic;

    @Inject
    public PlayerInteractBlockListener(BookshelfClickLogic bookshelfClickLogic) {
        this.bookshelfClickLogic = bookshelfClickLogic;
    }

    @CookieEventHandler
    public void onBlockClick(ClickerInteractBlock event) {
        bookshelfClickLogic.bookShelfClick(event.getCookiePlayer());
    }
}
