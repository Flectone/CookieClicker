package net.flectone.cookieclicker.gameplay.cookiepart;

import com.github.retrooper.packetevents.protocol.particle.Particle;
import com.github.retrooper.packetevents.protocol.particle.type.ParticleTypes;
import com.github.retrooper.packetevents.protocol.sound.Sounds;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.util.Vector3d;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.entities.objects.CookieEntity;
import net.flectone.cookieclicker.entities.objects.display.CookieTextDisplay;
import net.flectone.cookieclicker.entities.objects.display.InteractionEntity;
import net.flectone.cookieclicker.entities.playerdata.ServerCookiePlayer;
import net.flectone.cookieclicker.items.attributes.StatType;
import net.flectone.cookieclicker.utility.PacketUtils;
import net.flectone.cookieclicker.utility.StatsUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Singleton
public class CookieBonusSpawn {

    private final List<Integer> bonusEntities = new ArrayList<>();
    private final Random random = new Random();

    private final PacketUtils packetUtils;
    private final StatsUtils statsUtils;
    private final CustomItemSpawner itemSpawner;

    @Inject
    public CookieBonusSpawn(PacketUtils packetUtils, StatsUtils statsUtils,
                            CustomItemSpawner itemSpawner) {
        this.packetUtils = packetUtils;
        this.statsUtils = statsUtils;
        this.itemSpawner = itemSpawner;

    }

    public void checkForBonusClick(ServerCookiePlayer serverCookiePlayer, Integer entityId) {
        if (bonusEntities.isEmpty() || !bonusEntities.contains(entityId))
            return;

        // удаляем существ
        removeBonus(serverCookiePlayer, entityId);

        int amount = statsUtils.convertFortuneToAmount(statsUtils.extractStat(serverCookiePlayer.getPlayer(), StatType.FARMING_FORTUNE)) * 10;

        serverCookiePlayer.swingArm();

        itemSpawner.addItemsToInventory(serverCookiePlayer, amount);
    }

    public void checkBonusChance(ServerCookiePlayer serverCookiePlayer, Location location) {
        int chance = 3;
        if (random.nextInt(1, 100 + 1) <= (100 - chance))
            return;

        createBonus(serverCookiePlayer, location);
    }

    public void createBonus(ServerCookiePlayer serverCookiePlayer, Location location) {

        Location randomLocation = new Location(location.getX() + random.nextInt(-2, 3),
                location.getY(), location.getZ() + random.nextInt(-2, 3), 0f, 0f);

        InteractionEntity interactionEntity = new InteractionEntity();
        interactionEntity.setLocation(randomLocation);
        interactionEntity.spawn(serverCookiePlayer);

        // 200iq, тупо к айдишнику interaction прибавить 10000 и норм
        CookieTextDisplay textDisplay = new CookieTextDisplay(interactionEntity.getEntityId() + 10000);
        textDisplay.setText("клик");
        textDisplay.setLocation(randomLocation.getX(), randomLocation.getY() + 0.5, randomLocation.getZ());
        textDisplay.spawn(serverCookiePlayer);

        packetUtils.spawnParticle(serverCookiePlayer.getUser(),
                new Particle<>(ParticleTypes.SONIC_BOOM), 1,
                new Vector3d(randomLocation.getX(), randomLocation.getY() + 0.5, randomLocation.getZ()), 0f);

        bonusEntities.add(interactionEntity.getEntityId());
    }

    private void removeBonus(ServerCookiePlayer serverCookiePlayer, Integer entityId) {
        CookieEntity.removeById(entityId, serverCookiePlayer);
        CookieEntity.removeById(entityId + 10000, serverCookiePlayer);
        bonusEntities.remove(entityId);

        packetUtils.playSound(
                serverCookiePlayer.getUser(), Sounds.BLOCK_NOTE_BLOCK_BIT,
                1f, (random.nextInt(9, 15) / 10f)
        );
    }
}
