package net.flectone.cookieclicker.entities.objects.display;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import lombok.Getter;
import lombok.Setter;
import net.flectone.cookieclicker.entities.objects.CookieEntity;
import net.flectone.cookieclicker.entities.playerdata.ServerCookiePlayer;

import java.util.List;

@Getter
@Setter
public class InteractionEntity extends CookieEntity {
    private float width = 1;
    private float height = 1;

    public InteractionEntity() {
        super(EntityTypes.INTERACTION);
    }

    public InteractionEntity(Integer entityId) {
        super(EntityTypes.INTERACTION, entityId);
    }

    @Override
    public void spawn(ServerCookiePlayer serverCookiePlayer) {
        EntityData<?> widthData = new EntityData<>(8, EntityDataTypes.FLOAT, width);
        EntityData<?> heightData = new EntityData<>(9, EntityDataTypes.FLOAT, height);
        entityData.addAll(List.of(widthData, heightData));

        super.spawn(serverCookiePlayer);
    }
}
