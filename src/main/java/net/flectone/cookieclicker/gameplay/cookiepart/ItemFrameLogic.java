package net.flectone.cookieclicker.gameplay.cookiepart;

import com.github.retrooper.packetevents.protocol.color.Color;
import com.github.retrooper.packetevents.protocol.particle.Particle;
import com.github.retrooper.packetevents.protocol.particle.data.ParticleTrailData;
import com.github.retrooper.packetevents.protocol.particle.type.ParticleTypes;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.sound.Sounds;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.util.Vector3d;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.entities.playerdata.ServerCookiePlayer;
import net.flectone.cookieclicker.entities.ConnectedPlayers;
import net.flectone.cookieclicker.items.attributes.CookieAbility;
import net.flectone.cookieclicker.items.attributes.StatType;
import net.flectone.cookieclicker.items.itemstacks.base.data.Features;
import net.flectone.cookieclicker.items.itemstacks.base.data.ItemTag;
import net.flectone.cookieclicker.utility.PacketUtils;
import net.flectone.cookieclicker.utility.StatsUtils;
import net.flectone.cookieclicker.utility.hoes.EpicHoeUtils;
import net.flectone.cookieclicker.utility.hoes.LegendaryHoeUpgrade;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@Singleton
public class ItemFrameLogic {

    private final PacketUtils packetUtils;
    private final LegendaryHoeUpgrade legendaryHoeUpgrade;
    private final EpicHoeUtils epicHoeUtils;
    private final StatsUtils statsUtils;
    private final ConnectedPlayers connectedPlayers;
    private final StatisticDisplay statisticDisplay;
    private final CustomItemSpawner itemSpawner;

    @Inject
    public ItemFrameLogic(PacketUtils packetUtils, CustomItemSpawner itemSpawner,
                          StatsUtils statsUtils, LegendaryHoeUpgrade legendaryHoeUpgrade,
                          EpicHoeUtils epicHoeUtils, ConnectedPlayers connectedPlayers,
                          StatisticDisplay statisticDisplay) {
        this.packetUtils = packetUtils;
        this.statsUtils = statsUtils;
        this.legendaryHoeUpgrade = legendaryHoeUpgrade;
        this.epicHoeUtils = epicHoeUtils;
        this.connectedPlayers = connectedPlayers;
        this.statisticDisplay = statisticDisplay;
        this.itemSpawner = itemSpawner;
    }

    public void onCookieClick(ServerCookiePlayer serverCookiePlayer, Location location) {
        User user = serverCookiePlayer.getUser();
        Player player = serverCookiePlayer.getPlayer();

        Vector3d itemFramePos = new Vector3d(location.getX(), location.getY(), location.getZ());

        int maxAmount = 0;

        maxAmount += statsUtils.extractStat(player, StatType.FARMING_FORTUNE);
        int droppedAmount = statsUtils.convertFortuneToAmount(maxAmount);

        droppedAmount += Math.round(droppedAmount * (0.5f * epicHoeUtils.getTier(serverCookiePlayer.getUuid())));

        // проверка на уникальную мотыгу (легендарная или эпическая)
        checkUniqueHoe(serverCookiePlayer, location);

        statisticDisplay.displayActionBar(serverCookiePlayer, maxAmount, droppedAmount);

        itemSpawner.prepareSpawnItems(serverCookiePlayer, droppedAmount,
                location);

        // запись клика по рамке
        serverCookiePlayer.setIFrameClicks(serverCookiePlayer.getIFrameClicks() + 1);
        serverCookiePlayer.addXp(droppedAmount);

        connectedPlayers.save(serverCookiePlayer, true);

        // частицы и звук при клике
        packetUtils.spawnParticle(user, new Particle<>(ParticleTypes.TRIAL_SPAWNER_DETECTION), 2, itemFramePos, 0.2f);
        packetUtils.playSound(user, Sounds.ENTITY_GENERIC_EAT, 0.3f, 1f);
    }

    private void checkUniqueHoe(ServerCookiePlayer serverCookiePlayer, Location location) {
        ItemStack mainHand = serverCookiePlayer.getPlayer().getMainHandItem();
        Features itemFeature = new Features(mainHand);

        // если это эпическая мотыга (конкретно предмет)
        if (itemFeature.getItemTag() == ItemTag.EPIC_HOE) {
            processEpicClick(serverCookiePlayer, location);
        }

        // если у предмета способность на апгрейд (легендарная мотыга)
        if (itemFeature.getAbility() == CookieAbility.INFINITY_UPGRADE) {
            legendaryHoeUpgrade.tryUpdateHoe(serverCookiePlayer, mainHand);
            packetUtils.spawnParticle(serverCookiePlayer.getUser(), new Particle<>(ParticleTypes.TRIAL_SPAWNER_DETECTION_OMINOUS),
                    1, new Vector3d(location.getX(), location.getY(), location.getZ()), 0.2f);
        }
    }

    private void processEpicClick(ServerCookiePlayer serverCookiePlayer, Location location) {
        User user = serverCookiePlayer.getUser();
        Vector3d vector3d = new Vector3d(location.getX(), location.getY(), location.getZ());

        int charge = epicHoeUtils.getCharge(serverCookiePlayer.getUuid());
        int blue = 2 * charge;

        // спавн частиц
        ParticleTrailData ptd = new ParticleTrailData(vector3d, new Color(249, 150, blue));
        com.github.retrooper.packetevents.protocol.particle.Particle<?> particle1 = new Particle<>(ParticleTypes.TRAIL, ptd);

        packetUtils.spawnParticle(user, particle1, charge, vector3d, 3f);

        packetUtils.playSound(user, Sounds.BLOCK_AMETHYST_BLOCK_RESONATE, 0.5f, (float) (0.4 * epicHoeUtils.getTier(user.getUUID())));
        // добавление заряда
        epicHoeUtils.addCharge(user.getUUID(), 1);
    }
}
