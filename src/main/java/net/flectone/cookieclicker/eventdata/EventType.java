package net.flectone.cookieclicker.eventdata;

import lombok.Getter;
import net.flectone.cookieclicker.eventdata.events.*;
import net.flectone.cookieclicker.eventdata.events.base.BasePlayerEvent;

import java.util.Arrays;

@Getter
public enum EventType {

    NONE (BasePlayerEvent.class),
    INTERACT_AT_ENTITY (ClickerInteractAtEntity.class),
    INTERACT_AT_BLOCK (ClickerInteractBlock.class),
    INTERACT_WITH_ITEM(ClickerInteract.class),
    OPEN_WINDOW (ClickerOpenWindow.class),
    CLOSE_WINDOW (ClickerCloseWindow.class),
    CLICK_WINDOW (ClickerInventoryClick.class),
    PREPARE_CRAFT (ClickerPrepareCraft.class),
    EAT (ClickerPlayerEat.class),
    MOVE (ClickerPlayerMove.class),
    PICKUP_ITEM (ClickerPickupItem.class),
    DROP_ITEM (ClickerDropItem.class),
    SWING_ARM (ClickerPlayerSwingArm.class),
    PREPARE_ANVIL (ClickerPrepareAnvil.class);

    private final Class<?> clazz;

    EventType(Class<?> clazz) {
        this.clazz = clazz;
    }

    public static EventType fromClass(Class<?> clazz) {
        return Arrays.stream(values())
                .filter(eventType -> eventType.getClazz().equals(clazz))
                .findAny()
                .orElse(NONE);
    }
}
