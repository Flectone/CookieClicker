package net.flectone.cookieclicker.entities;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class CookieTextDisplay extends CookieEntity {

    public CookieTextDisplay(EntityType entityType) {
        super(entityType);
    }

    public CookieTextDisplay(EntityType entityType, Integer customId) {
        super(entityType, customId);
    }

    public void setText(String text) {
        if (!entityType.equals(EntityTypes.TEXT_DISPLAY)) return;
        EntityData textData = new EntityData(23, EntityDataTypes.ADV_COMPONENT, MiniMessage.miniMessage().deserialize(text));
        EntityData billboard = new EntityData(15, EntityDataTypes.BYTE,  (byte) 3);
        entityData.add(textData);
        entityData.add(billboard);
    }
}
