package net.flectone.cookieclicker.entities;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class CookieEntity {
    private final int entityId;
    private final UUID uuid;
    protected final EntityType entityType;
    @Setter
    protected Location location = new Location(0, 0, 0, 0, 0);
    protected final List<EntityData> entityData = new ArrayList<>();

    //private final List<User> seeingPlayers = new ArrayList<>();

    public CookieEntity(EntityType entityType) {
        this(entityType, SpigotReflectionUtil.generateEntityId());
    }

    public CookieEntity(EntityType entityType, Integer customId) {
        entityId = customId;
        uuid = UUID.randomUUID();
        this.entityType = entityType;
    }

    public void spawn(User user) {
        WrapperPlayServerSpawnEntity spawnPacket = new WrapperPlayServerSpawnEntity(
                entityId,
                uuid,
                entityType,
                location,
                0,
                0,
                new Vector3d()
        );
        WrapperPlayServerEntityMetadata metadataPacket = new WrapperPlayServerEntityMetadata(
                entityId, entityData
        );
        user.sendPacket(spawnPacket);
        user.sendPacket(metadataPacket);
    }

    public void setLocation(Double x, Double y, Double z) {
        location = new Location(x, y, z, 0, 0);
    }

    public void remove(User user) {
        removeById(entityId, user);
    }

    public static void removeById(Integer id, User user) {
        WrapperPlayServerDestroyEntities destroyPacket = new WrapperPlayServerDestroyEntities(id);
        user.sendPacket(destroyPacket);
    }
}
