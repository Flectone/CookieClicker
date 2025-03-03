package net.flectone.cookieclicker.utility;

import com.github.retrooper.packetevents.protocol.component.ComponentTypes;
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemModel;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.protocol.particle.Particle;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.sound.Sound;
import com.github.retrooper.packetevents.protocol.sound.SoundCategory;
import com.github.retrooper.packetevents.resources.ResourceLocation;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import net.flectone.cookieclicker.CookieClicker;
import net.minecraft.core.component.DataComponents;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Singleton
public class PacketUtils {
    private final CCConversionUtils converter;

    @Inject
    public PacketUtils(CCConversionUtils CCConverter) {
        this.converter = CCConverter;
    }

    public void spawnItem(User user, com.github.retrooper.packetevents.protocol.world.Location location, net.minecraft.world.item.ItemStack item) {
        //тут наверное есть вероятность, что uuid будет уже существующего моба, но хз чё сделать
        UUID newEntitiUUID = UUID.randomUUID();
        int newEntityId = SpigotReflectionUtil.generateEntityId();
        //пакет на спавн предмета
        WrapperPlayServerSpawnEntity spawnPacket = new WrapperPlayServerSpawnEntity(newEntityId,
                newEntitiUUID,
                EntityTypes.ITEM,
                location,
                1f, //бесполезно по сути
                1, //не знаю, что это и зачем
                null);

        //данные для предмета, так как пакет на спавн только призывает предмет без данных
        List<EntityData> entityDataList = new ArrayList<>();
        ItemStack.Builder itemBuilderPE = ItemStack.builder()
                .type(ItemTypes.getByName(item.getItem().toString()))
                .amount(item.getCount());

        net.minecraft.resources.ResourceLocation resourceLocation = item.getComponents().get(DataComponents.ITEM_MODEL);
        if (resourceLocation != null) {
            //Bukkit.getLogger().info(resourceLocation.toString());
            itemBuilderPE.component(ComponentTypes.ITEM_MODEL,
                    new ItemModel(new ResourceLocation(resourceLocation.toString()))
            );
        }

        entityDataList.add(new EntityData(8, EntityDataTypes.ITEMSTACK, itemBuilderPE.build()));
        //пакет на изменение данных предмета
        WrapperPlayServerEntityMetadata metadataPacket = new WrapperPlayServerEntityMetadata(newEntityId, entityDataList);
        //пакет на уничтожение предмета, позже понадобится
        WrapperPlayServerDestroyEntities destroyPacket = new WrapperPlayServerDestroyEntities(newEntityId);
        WrapperPlayServerCollectItem collectPacket = new WrapperPlayServerCollectItem(newEntityId, user.getEntityId(), item.getCount());

        user.sendPacket(spawnPacket);
        user.sendPacket(metadataPacket);
        net.minecraft.world.entity.player.Player player = converter.userToNMS(user);
        //вот тут реализовано подбирание предмета
        new BukkitRunnable() {

            @Override
            public void run() {

                //расстояние от игрока до предмета
                double distance = Math.sqrt(Math.pow(location.getX() - player.getX(), 2)
                        + 0 //Math.pow(location.getY() - player.getY(), 2)
                        + Math.pow(location.getZ() - player.getZ(), 2));
                //Если расстояние меньше 2
                if (distance < 2d) {
                    //уничтожение предмета
                    user.sendPacket(collectPacket);
                    user.sendPacket(destroyPacket);
                    //добавление предмета в инвентарь
                    player.getInventory().add(item.copy());
                    cancel();
                }
            }
        }.runTaskTimer(CookieClicker.getPlugin(CookieClicker.class), 0L, 2L);
    }

    public void playSound(User user, Sound sound, Float volume, Float pitch) {
        net.minecraft.world.entity.player.Player player = converter.userToNMS(user);
        Vector3i position = new Vector3i((int) player.getX(), (int) player.getY(), (int) player.getZ());

        WrapperPlayServerSoundEffect soundPacket = new WrapperPlayServerSoundEffect(sound,
                SoundCategory.MASTER,
                position.multiply(8), // надо умножать координаты на 8
                volume, pitch);


        user.sendPacket(soundPacket);
    }

    public void spawnParticle(User user, Particle<?> particle, Integer particleCount, Vector3d vector3d, Float offset) {
        WrapperPlayServerParticle particlePacket = new WrapperPlayServerParticle(particle,
                true,
                vector3d,
                new Vector3f(offset, offset, offset),
                0f,
                particleCount);
        user.sendPacket(particlePacket);
    }
}
