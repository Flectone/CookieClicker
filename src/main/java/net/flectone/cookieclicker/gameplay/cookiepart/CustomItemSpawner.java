package net.flectone.cookieclicker.gameplay.cookiepart;

import com.github.retrooper.packetevents.protocol.color.Color;
import com.github.retrooper.packetevents.protocol.particle.Particle;
import com.github.retrooper.packetevents.protocol.particle.data.ParticleDustColorTransitionData;
import com.github.retrooper.packetevents.protocol.particle.type.ParticleTypes;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.util.Vector3d;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.entities.playerdata.ServerCookiePlayer;
import net.flectone.cookieclicker.gameplay.cookiepart.data.DropType;
import net.flectone.cookieclicker.items.ItemsRegistry;
import net.flectone.cookieclicker.items.attributes.CookieAbility;
import net.flectone.cookieclicker.items.itemstacks.base.data.Features;
import net.flectone.cookieclicker.items.itemstacks.base.data.ItemTag;
import net.flectone.cookieclicker.utility.ItemsCompactor;
import net.flectone.cookieclicker.utility.PacketUtils;
import net.flectone.cookieclicker.utility.data.Pair;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Singleton
public class CustomItemSpawner {

    private final ItemsCompactor itemsCompactor;
    private final PacketUtils packetUtils;
    private final ItemsRegistry itemsRegistry;

    @Inject
    public CustomItemSpawner(ItemsCompactor itemsCompactor,
                             PacketUtils packetUtils,
                             ItemsRegistry itemsRegistry) {
        this.itemsCompactor = itemsCompactor;
        this.packetUtils = packetUtils;
        this.itemsRegistry = itemsRegistry;
    }

    public void spawnItems(ServerCookiePlayer serverCookiePlayer, DropType dropType, Integer amount, Location location) {
        boolean isCookieCrafter = new Features(serverCookiePlayer.getPlayer().getOffhandItem()).getItemTag() == ItemTag.COOKIE_CRAFTER;
        int lvlScaling = serverCookiePlayer.getLvlScaling();

        compactItems(dropType.getTags(), amount, lvlScaling, isCookieCrafter).forEach(drop ->
                packetUtils.spawnItem(serverCookiePlayer, location, itemsRegistry.get(drop.getKey(), drop.getValue())));
    }

    public void prepareSpawnItems(ServerCookiePlayer serverCookiePlayer, Integer droppedAmount, Location location) {
        Player player = serverCookiePlayer.getPlayer();
        //тут вероятность на спец. предмет
        Random random = new Random(System.currentTimeMillis());

        boolean isSpecialClick = random.nextInt(1, 100) >= 96;
        boolean isTransform = serverCookiePlayer.getSecondAbility() == CookieAbility.TRANSFORM;
        boolean isRose = serverCookiePlayer.getMainAbility() == CookieAbility.ROSE_BUSH;

        DropType dropType = chooseItemDrops(serverCookiePlayer, isTransform, isRose);

        // короче это для альтернативного предмета или ягод
        if (isSpecialClick && (isTransform || isRose)) {
            spawnSpecial(serverCookiePlayer, dropType, location);
        }

        // это если в левой руке какао боб, а в правой уничтожитель печенья
        if (new Features(player.getOffhandItem()).getItemTag() == ItemTag.ENCHANTED_COCOA_BEANS
                && serverCookiePlayer.getMainAbility() == CookieAbility.DESTROYER) {
            spawnChocolate(serverCookiePlayer, location);
        }

        spawnItems(serverCookiePlayer, dropType, droppedAmount, location);
    }

    public void addItemsToInventory(ServerCookiePlayer serverCookiePlayer, int amount) {
        Player player = serverCookiePlayer.getPlayer();
        DropType drops = chooseItemDrops(serverCookiePlayer, false, false);

        boolean isCookieCrafter = new Features(player.getOffhandItem()).getItemTag() == ItemTag.COOKIE_CRAFTER;

        for (Pair<ItemTag, Integer> singleDrop : compactItems(drops.getTags(), amount, serverCookiePlayer.getLvlScaling(), isCookieCrafter)) {
            player.getInventory().add(itemsRegistry.get(singleDrop.getKey(), singleDrop.getValue()));
        }
    }

    private DropType chooseItemDrops(ServerCookiePlayer serverCookiePlayer, boolean isTransform, boolean isRose) {
        DropType dropType = DropType.fromAbility(serverCookiePlayer.getMainAbility());

        if (isRose && isTransform) {
            dropType = DropType.BERRIES_ALT;
        }

        return dropType;
    }

    private List<Pair<ItemTag, Integer>> compactItems(List<ItemTag> tags, Integer amount, Integer lvlScaling, boolean isCookieCrafter) {
        List<Pair<ItemTag, Integer>> compacted = new ArrayList<>();

        tags.forEach(itemTag -> compacted.addAll(switch (itemTag) {
            case ItemTag t when t == ItemTag.COOKIE && isCookieCrafter -> itemsCompactor.doubleCompactFromValue(
                    ItemTag.COOKIE, ItemTag.ENCHANTED_COOKIE, ItemTag.BLOCK_OF_COOKIE,
                    amount, 160 + lvlScaling, 512
            );
            case ItemTag t when t == ItemTag.COOKIE -> itemsCompactor.compactFromValue(ItemTag.COOKIE, ItemTag.ENCHANTED_COOKIE,
                    amount, 160 + lvlScaling);
            case WHEAT -> itemsCompactor.compactFromValue(ItemTag.WHEAT, ItemTag.ENCHANTED_WHEAT, amount, 160 + lvlScaling);
            case COCOA_BEANS -> itemsCompactor.compactFromValue(ItemTag.COCOA_BEANS, ItemTag.ENCHANTED_COCOA_BEANS, amount, 320 + lvlScaling);
            default -> new ArrayList<>();
        }));

        return compacted;
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
}
