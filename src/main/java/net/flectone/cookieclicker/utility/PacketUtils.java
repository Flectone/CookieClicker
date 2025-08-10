package net.flectone.cookieclicker.utility;

import com.github.retrooper.packetevents.protocol.particle.Particle;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.sound.Sound;
import com.github.retrooper.packetevents.protocol.sound.SoundCategory;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerParticle;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSoundEffect;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.entities.objects.item.CookieItemEntity;
import net.flectone.cookieclicker.entities.objects.item.CookieItemEntityData;
import net.flectone.cookieclicker.entities.playerdata.ServerCookiePlayer;
import net.flectone.cookieclicker.utility.data.Position;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@Singleton
public class PacketUtils {

    public void spawnItem(ServerCookiePlayer serverCookiePlayer, Location itemLocation, ItemStack itemStack) {
        CookieItemEntity itemEntity = new CookieItemEntity(itemStack);
        CookieItemEntityData data = itemEntity.getData();

        itemEntity.setVisual(ConversionUtils.fromMinecraftStack(itemStack, MinecraftServer.getServer().registryAccess()));

        itemEntity.setLocation(itemLocation);
        itemEntity.spawn(serverCookiePlayer);

        serverCookiePlayer.addSpawnedItem(data);

        Player player = serverCookiePlayer.getPlayer();
        Position playerPosition = new Position(player.position());

        if (playerPosition.distance(itemLocation) < 2d) {
            serverCookiePlayer.pickUpItem(itemEntity.getData());
            player.getInventory().add(itemStack);
        }
    }

    public void playSound(User user, Sound sound, Float volume, Float pitch) {
        net.minecraft.world.entity.player.Player player = ConversionUtils.userToNMS(user);

        Vector3i position = new Vector3i((int) (player.getX() * 8), (int) (player.getY() * 8), (int) (player.getZ() * 8));

        WrapperPlayServerSoundEffect soundPacket = new WrapperPlayServerSoundEffect(sound,
                SoundCategory.MASTER,
                position,
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
