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
import net.flectone.cookieclicker.gameplay.cookiepart.data.DropType;
import net.flectone.cookieclicker.items.ItemsRegistry;
import net.flectone.cookieclicker.items.attributes.StatType;
import net.flectone.cookieclicker.items.itemstacks.base.data.Features;
import net.flectone.cookieclicker.items.itemstacks.base.data.ItemTag;
import net.flectone.cookieclicker.utility.PacketUtils;
import net.flectone.cookieclicker.utility.StatsUtils;
import net.flectone.cookieclicker.utility.data.Pair;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Singleton
public class CookieBonusSpawn {

    private final List<Integer> bonusEntities = new ArrayList<>();
    private final Random random = new Random();

    private final PacketUtils packetUtils;
    private final StatsUtils statsUtils;
    private final ItemsRegistry itemsRegistry;
    private final CookieSpawningLogic cookieSpawningLogic;

    @Inject
    public CookieBonusSpawn(PacketUtils packetUtils, StatsUtils statsUtils,
                            ItemsRegistry itemsRegistry, CookieSpawningLogic cookieSpawningLogic) {
        this.packetUtils = packetUtils;
        this.statsUtils = statsUtils;
        this.itemsRegistry = itemsRegistry;
        this.cookieSpawningLogic = cookieSpawningLogic;

    }

    public void checkForBonusClick(ServerCookiePlayer serverCookiePlayer, Integer entityId) {
        if (bonusEntities.isEmpty() || !bonusEntities.contains(entityId))
            return;

        Player player = serverCookiePlayer.getPlayer();

        // удаляем существ
        removeBonus(serverCookiePlayer, entityId);

        int amount = statsUtils.convertFortuneToAmount(statsUtils.extractStat(serverCookiePlayer.getPlayer(), StatType.FARMING_FORTUNE)) * 10;

        serverCookiePlayer.swingArm();

        DropType drops = cookieSpawningLogic.chooseItemDrops(serverCookiePlayer, false, false);
        boolean isCookieCrafter = new Features(player.getOffhandItem()).getItemTag() == ItemTag.COOKIE_CRAFTER;

        for (Pair<ItemTag, Integer> singleDrop : cookieSpawningLogic.compactItems(drops.getTags(), amount, serverCookiePlayer.getLvlScaling(), isCookieCrafter)) {
            player.getInventory().add(itemsRegistry.get(singleDrop.getKey(), singleDrop.getValue()));
        }
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
