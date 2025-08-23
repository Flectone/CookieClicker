package net.flectone.cookieclicker.gameplay.cookiepart;

import com.github.retrooper.packetevents.protocol.color.Color;
import com.github.retrooper.packetevents.protocol.particle.Particle;
import com.github.retrooper.packetevents.protocol.particle.data.ParticleDustColorTransitionData;
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
import net.flectone.cookieclicker.gameplay.cookiepart.data.DropType;
import net.flectone.cookieclicker.items.ItemsRegistry;
import net.flectone.cookieclicker.items.attributes.CookieAbility;
import net.flectone.cookieclicker.items.attributes.StatType;
import net.flectone.cookieclicker.items.itemstacks.base.data.Features;
import net.flectone.cookieclicker.items.itemstacks.base.data.ItemTag;
import net.flectone.cookieclicker.utility.ItemsCompactor;
import net.flectone.cookieclicker.utility.PacketUtils;
import net.flectone.cookieclicker.utility.StatsUtils;
import net.flectone.cookieclicker.utility.data.Pair;
import net.flectone.cookieclicker.utility.hoes.EpicHoeUtils;
import net.flectone.cookieclicker.utility.hoes.LegendaryHoeUpgrade;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Singleton
public class CookieSpawningLogic {

    private final PacketUtils packetUtils;
    private final ItemsRegistry itemsRegistry;
    private final LegendaryHoeUpgrade legendaryHoeUpgrade;
    private final EpicHoeUtils epicHoeUtils;
    private final StatsUtils statsUtils;
    private final ConnectedPlayers connectedPlayers;
    private final ItemsCompactor itemsCompactor;
    private final StatisticDisplay statisticDisplay;

    @Inject
    public CookieSpawningLogic(PacketUtils packetUtils, ItemsRegistry itemsRegistry,
                               StatsUtils statsUtils, LegendaryHoeUpgrade legendaryHoeUpgrade,
                               EpicHoeUtils epicHoeUtils, ConnectedPlayers connectedPlayers,
                               ItemsCompactor itemsCompactor, StatisticDisplay statisticDisplay) {
        this.itemsRegistry = itemsRegistry;
        this.packetUtils = packetUtils;
        this.statsUtils = statsUtils;
        this.legendaryHoeUpgrade = legendaryHoeUpgrade;
        this.epicHoeUtils = epicHoeUtils;
        this.connectedPlayers = connectedPlayers;
        this.itemsCompactor = itemsCompactor;
        this.statisticDisplay = statisticDisplay;
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

        prepareSpawnItems(serverCookiePlayer, droppedAmount,
                location);

        // запись клика по рамке
        serverCookiePlayer.setIFrameClicks(serverCookiePlayer.getIFrameClicks() + 1);
        serverCookiePlayer.addXp(droppedAmount);

        connectedPlayers.save(serverCookiePlayer, true);

        // частицы и звук при клике
        packetUtils.spawnParticle(user, new Particle<>(ParticleTypes.TRIAL_SPAWNER_DETECTION), 2, itemFramePos, 0.2f);
        packetUtils.playSound(user, Sounds.ENTITY_GENERIC_EAT, 0.3f, 1f);
    }

    public void prepareSpawnItems(ServerCookiePlayer serverCookiePlayer, Integer droppedAmount, Location location) {
        Player player = serverCookiePlayer.getPlayer();
        //тут вероятность на спец. предмет
        Random random = new Random(System.currentTimeMillis());

        boolean isSpecialClick = random.nextInt(1, 100) >= 96;
        boolean isTransform = getSecondAbility(player) == CookieAbility.TRANSFORM;
        boolean isRose = getMainAbility(player) == CookieAbility.ROSE_BUSH;

        DropType dropType = chooseItemDrops(serverCookiePlayer, isTransform, isRose);

        // короче это для альтернативного предмета или ягод
        if (isSpecialClick && (isTransform || isRose)) {
            spawnSpecial(serverCookiePlayer, dropType, location);
        }

        // это если в левой руке какао боб, а в правой уничтожитель печенья
        if (new Features(player.getOffhandItem()).getItemTag() == ItemTag.ENCHANTED_COCOA_BEANS
                && getMainAbility(player) == CookieAbility.DESTROYER) {
            spawnChocolate(serverCookiePlayer, location);
        }

        spawnItems(serverCookiePlayer, dropType, droppedAmount, location);
    }

    public DropType chooseItemDrops(ServerCookiePlayer serverCookiePlayer, boolean isTransform, boolean isRose) {
        Player player = serverCookiePlayer.getPlayer();

        DropType dropType = DropType.fromAbility(getMainAbility(player));

        if (isRose && isTransform) {
            dropType = DropType.BERRIES_ALT;
        }

        return dropType;
    }

    private void spawnSpecial(ServerCookiePlayer serverCookiePlayer, DropType dropType, Location location) {
        Random random = new Random(System.currentTimeMillis());
        Location randomPosition = new Location(
                location.getX() + random.nextDouble(-2.5, 2.5),
                location.getY() + random.nextDouble(0, 2),
                location.getZ() + random.nextDouble(-2.5, 2.5),
                1, 1
        );

        packetUtils.spawnItem(serverCookiePlayer, randomPosition, itemsRegistry.get(dropType.getAltTag()));
    }

    private void spawnItems(ServerCookiePlayer serverCookiePlayer, DropType dropType, Integer amount, Location location) {
        //int stackSize = 64 * Math.min((amount / 100) + 1, 35);
        boolean isCookieCrafter = new Features(serverCookiePlayer.getPlayer().getOffhandItem()).getItemTag() == ItemTag.COOKIE_CRAFTER;

        compactItems(dropType.getTags(), amount, isCookieCrafter).forEach(drop -> {
            packetUtils.spawnItem(serverCookiePlayer, location, itemsRegistry.get(drop.getKey(), drop.getValue()));
        });
    }

    private void spawnChocolate(ServerCookiePlayer serverCookiePlayer, Location location) {
        ItemStack offHand = serverCookiePlayer.getPlayer().getOffhandItem();
        offHand.setCount(offHand.getCount() - 1);

        packetUtils.spawnItem(serverCookiePlayer, location, itemsRegistry.get(ItemTag.CHOCOLATE));

        ParticleDustColorTransitionData particleDustColorTransitionData = new ParticleDustColorTransitionData(1f,
                new Color(16753920), new Color(16718080));
        Particle<?> particle = new Particle<>(ParticleTypes.DUST_COLOR_TRANSITION, particleDustColorTransitionData);
        Vector3d position = new Vector3d(location.getX(), location.getY(), location.getZ());

        packetUtils.spawnParticle(serverCookiePlayer.getUser(), particle, 20, position, 0.3f);
    }

    private Pair<CookieAbility, CookieAbility> obtainAbilities(Player player) {
        CookieAbility first = new Features(player.getItemInHand(InteractionHand.MAIN_HAND)).getAbility();
        CookieAbility second = new Features(player.getItemInHand(InteractionHand.OFF_HAND)).getAbility();

        //Главная способность не может быть Transform
        //Поэтому их надо менять местами

        return first == CookieAbility.TRANSFORM
                ? new Pair<>(second, first)
                : new Pair<>(first, second);
    }

    private CookieAbility getMainAbility(Player player) {
        return obtainAbilities(player).getKey();
    }

    private CookieAbility getSecondAbility(Player player) {
        return obtainAbilities(player).getValue();
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

    public List<Pair<ItemTag, Integer>> compactItems(List<ItemTag> tags, Integer amount, boolean isCookieCrafter) {
        List<Pair<ItemTag, Integer>> compacted = new ArrayList<>();

        tags.forEach(itemTag -> compacted.addAll(switch (itemTag) {
            case ItemTag t when t == ItemTag.COOKIE && isCookieCrafter -> itemsCompactor.doubleCompactFromValue(
                    ItemTag.COOKIE, ItemTag.ENCHANTED_COOKIE, ItemTag.BLOCK_OF_COOKIE,
                    amount, 160, 512
            );
            case ItemTag t when t == ItemTag.COOKIE -> itemsCompactor.compactFromValue(ItemTag.COOKIE, ItemTag.ENCHANTED_COOKIE,
                                                                                        amount, 160);
            case WHEAT -> itemsCompactor.compactFromValue(ItemTag.WHEAT, ItemTag.ENCHANTED_WHEAT, amount, 160);
            case COCOA_BEANS -> itemsCompactor.compactFromValue(ItemTag.COCOA_BEANS, ItemTag.ENCHANTED_COCOA_BEANS, amount, 320);
            default -> new ArrayList<>();
        }));

        return compacted;
    }
}
