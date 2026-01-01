package net.flectone.cookieclicker.gameplay.cookiepart.listeners;

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.gameplay.cookiepart.InteractionController;
import net.flectone.cookieclicker.eventdata.CookieEventHandler;
import net.flectone.cookieclicker.eventdata.CookieListener;
import net.flectone.cookieclicker.eventdata.events.ClickerInteractAtEntity;

@Singleton
public class PlayerInteractEntityListener implements CookieListener {

    private final InteractionController interactionController;

    @Inject
    public PlayerInteractEntityListener(InteractionController controller) {
        this.interactionController = controller;
    }

    @CookieEventHandler
    public void onInteract(ClickerInteractAtEntity event) {
        if (event.getInteractAction() != WrapperPlayClientInteractEntity.InteractAction.INTERACT) return;

        interactionController.isBonusClick(event.getInteractedEntityId(), event.getCookiePlayer());

//        boolean cancel = interactionController.checkEntity(event.getInteractedEntityId(), event.getCookiePlayer());
//        event.setCancelled(cancel);
    }
}
