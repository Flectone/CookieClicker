package net.flectone.cookieclicker.events;

import com.github.retrooper.packetevents.protocol.color.Color;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.particle.Particle;
import com.github.retrooper.packetevents.protocol.particle.data.ParticleTrailData;
import com.github.retrooper.packetevents.protocol.particle.type.ParticleTypes;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.sound.Sounds;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerActionBar;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.cookiePart.LegendaryHoeUpgrade;
import net.flectone.cookieclicker.cookiePart.EpicHoeUtils;
import net.flectone.cookieclicker.items.ItemManager;
import net.flectone.cookieclicker.items.attributes.CookieAbility;
import net.flectone.cookieclicker.items.attributes.StatType;
import net.flectone.cookieclicker.utility.*;
import net.flectone.cookieclicker.entities.CookieEntity;
import net.flectone.cookieclicker.entities.CookieTextDisplay;
import net.flectone.cookieclicker.playerdata.ServerCookiePlayer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Singleton
public class PacketCookieClickEvent {
    //это жесть
    private final ItemManager manager;
    private final StatsUtils statsUtils;
    private final PacketUtils packetUtils;
    private final EpicHoeUtils epicHoeUtils;
    private final LegendaryHoeUpgrade legendaryHoeUpgrade;
    private final ConnectedPlayers connectedPlayers;

    private final List<Integer> bonusEntities = new ArrayList<>();

    @Inject
    public PacketCookieClickEvent(StatsUtils statsUtils, ItemManager manager,
                                  PacketUtils packetUtils, EpicHoeUtils epicHoeUtils, LegendaryHoeUpgrade legendaryHoeUpgrade,
                                  ConnectedPlayers connectedPlayers) {
        this.manager = manager;
        this.statsUtils = statsUtils;
        this.packetUtils = packetUtils;
        this.epicHoeUtils = epicHoeUtils;
        this.legendaryHoeUpgrade = legendaryHoeUpgrade;
        this.connectedPlayers = connectedPlayers;
    }

    public void onCookieClick(ServerCookiePlayer serverCookiePlayer, Location location) {
        User user = serverCookiePlayer.getUser();
        Player player = serverCookiePlayer.getPlayer();

        Vector3d itemFrameVector3d = new Vector3d(location.getX(), location.getY(), location.getZ());

        int maxAmount = 0;

        maxAmount += statsUtils.extractStat(player, StatType.FARMING_FORTUNE);
        int droppedAmount = statsUtils.convertFortuneToAmount(maxAmount);

        droppedAmount += Math.round(droppedAmount * (0.5f * epicHoeUtils.getTier(player.getUUID())));

        //Проверка на эпическую мотыгу
        if (statsUtils.getItemTag(player.getMainHandItem()).equals("epic_hoe")) {
            onClickWithHoe(user, itemFrameVector3d);
        }

        //Проверка на легендарную мотыгу
        legendaryHoeUpgrade.tryUpdateHoe(user, itemFrameVector3d);

        displayActionBar(user, maxAmount, droppedAmount);

        spawnItems(serverCookiePlayer, droppedAmount,
                location);

        //для базы данных
        serverCookiePlayer.setIFrameClicks(serverCookiePlayer.getIFrameClicks() + 1);
        serverCookiePlayer.addXp(droppedAmount);

        connectedPlayers.save(serverCookiePlayer, true);
        //

        packetUtils.spawnParticle(user,
                new Particle<>(ParticleTypes.TRIAL_SPAWNER_DETECTION),
                2,
                itemFrameVector3d,
                0.2f);
        packetUtils.playSound(user, Sounds.ENTITY_GENERIC_EAT, 0.3f, 1f);
    }

    private Pair<CookieAbility, CookieAbility> obtainAbilities(Player player) {
        CookieAbility first = statsUtils.getAbility(player.getItemInHand(InteractionHand.MAIN_HAND));
        CookieAbility second = statsUtils.getAbility(player.getItemInHand(InteractionHand.OFF_HAND));

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

    public List<ItemStack> calculateFullCount(String baseVariant, String enchantedVariant, Integer countNeeded, Integer currentCount) {
        List<ItemStack> items = new ArrayList<>();

        if (currentCount < countNeeded) {
            items.add(manager.getWithAmount(baseVariant, currentCount));
        }
        if (currentCount >= countNeeded) {
            if (currentCount % countNeeded > 0) {
                items.add(manager.getWithAmount(baseVariant, currentCount % countNeeded));
            }
            items.add(manager.getWithAmount(enchantedVariant, currentCount / countNeeded));
        }
        return items;
    }

    private List<ItemStack> chooseItemDrops(ServerCookiePlayer serverCookiePlayer, Integer finalFortune, boolean isAlternative) {
        Player player = serverCookiePlayer.getPlayer();

        //альтернативный предмет, если спец. условие выполнено, то этот предмет выпадет
        net.minecraft.world.item.ItemStack altItem = null;
        List<ItemStack> dropItems = new ArrayList<>();

        //проверка на способность у мотыги
        switch (getMainAbility(player)) {
            case DESTROYER -> {
                if (statsUtils.hasTag(player.getOffhandItem(), "ench_cocoa")) {
                    dropItems.addAll(List.of());
                    dropItems.add(manager.getWithAmount("chocolate", 1)); //если в левой руке какао-бобы
                    player.getOffhandItem().setCount(player.getOffhandItem().getCount() - 1);
                } else {
                    dropItems.add(manager.getWithAmount("cocoa_beans", finalFortune));
                    dropItems.add(manager.getWithAmount("wheat", finalFortune));
                }
                altItem = manager.getNMS("pumpkin");
            }
            case ROSE_BUSH -> {
                altItem = getSecondAbility(player) == CookieAbility.TRANSFORM
                        ? manager.getNMS("glow_berries")
                        : manager.getNMS("berries");
                dropItems.add(manager.getWithAmount("cookie", finalFortune));
            }
            default -> {
                dropItems.addAll(calculateFullCount("cookie", "ench_cookie", 160, finalFortune));
                altItem = manager.getNMS("pie");
            }
        }

        return isAlternative ? List.of(altItem) : dropItems;
    }

    public void spawnItems(ServerCookiePlayer serverCookiePlayer, Integer droppedAmount, Location loca) {
        User user = serverCookiePlayer.getUser();
        Player player = serverCookiePlayer.getPlayer();
        //тут вероятность на спец. предмет
        Random rndB = new Random(System.currentTimeMillis());

        boolean altItem = false;
        if (rndB.nextInt(1, 100) >= 95
                && (getSecondAbility(player) == CookieAbility.TRANSFORM || getMainAbility(player) == CookieAbility.ROSE_BUSH)) {
            loca = new Location(loca.getX() + rndB.nextDouble(-2.5, 2.5),
                    loca.getY() + rndB.nextDouble(0, 4),
                    loca.getZ() + rndB.nextDouble(-2.5, 2.5), 1, 1);
            altItem = true;
        }


        List<ItemStack> dropItems = chooseItemDrops(serverCookiePlayer, droppedAmount, altItem);

        for (net.minecraft.world.item.ItemStack i : dropItems)
            packetUtils.spawnItem(user, loca, i);

        createBonus(user, loca);
    }

    public void changeLegendaryHoeMode(User user) {
        legendaryHoeUpgrade.legHoeChange(user);
    }

    public void bookShelfClick(ServerCookiePlayer serverCookiePlayer) {
        Player player = serverCookiePlayer.getPlayer();
        User user = serverCookiePlayer.getUser();

        ItemStack enchantedCookiesInHand = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (!statsUtils.hasTag(enchantedCookiesInHand, "ench_cookie")) return;
        if (enchantedCookiesInHand.getCount() < 15) return;

        HitResult hitResult = player.getRayTrace(5, ClipContext.Fluid.NONE);
        if (!hitResult.getType().equals(HitResult.Type.BLOCK)) return;
        BlockHitResult blockHitResult = (BlockHitResult) hitResult;
        //книжная полка, координаты и blockstate
        BlockPos shelfPos = blockHitResult.getBlockPos();
        BlockState bookshelfBlockState = player.level().getBlockState(shelfPos);

        if (!bookshelfBlockState
                .getBlock().asItem().equals(Items.CHISELED_BOOKSHELF)) return;

        double x = shelfPos.getX() + 0.5;
        double y = shelfPos.getY() + 0.5;
        double z = shelfPos.getZ() + 0.5;
        //facing вроде бы всегда первая в списке
        EnumProperty<?> facingProperty = (EnumProperty<?>) player.level().getBlockState(shelfPos).getProperties().toArray()[0];

        if (bookshelfBlockState.getOptionalValue(facingProperty).isEmpty()) return;
        //проверка, куда смотрит блок, чтобы спереди призывать предмет
        switch (bookshelfBlockState.getOptionalValue(facingProperty).get().toString()) {
            case "north" -> z--;
            case "south" -> z++;
            case "west" -> x--;
            case "east" -> x++;
        }

        Location bookLocation = new Location(x, y, z, 1, 1);
        //spawning item
        user.sendMessage(MiniMessage.miniMessage().deserialize("<#f4a91c>\uD83C\uDF6A <#f7f4b5>Вы купили книгу!"));
        packetUtils.spawnItem(user, bookLocation, manager.getNMS("book_boost1"));
        enchantedCookiesInHand.setCount(enchantedCookiesInHand.getCount() - 15);
    }

    private void displayActionBar(User user, Integer maxAmount, Integer droppedAmount) {
        // Показ статистики
        // Первое число - общее количество удачи
        // Второе число - число выпавших предметов
        // В скобках первое - заряд от эпической мотыги
        // В скобках второе - уровень заряда, который увеличивает количество выпавших предметов (не удачу)
        WrapperPlayServerActionBar bar = new WrapperPlayServerActionBar(
                MiniMessage.miniMessage().deserialize("<#eb6514>" + maxAmount + "⯫ "
                + "<#e4a814>" + droppedAmount +"★ "
                + "<#b014eb>[" + epicHoeUtils.getCharge(user.getUUID()) + "% " +  epicHoeUtils.getTier(user.getUUID()) + "☄]"));
        user.sendPacketSilently(bar);
    }

    public void onClickWithHoe(User user, Vector3d vector3d) {
        //спавн частиц
        ParticleTrailData ptd = new ParticleTrailData(vector3d, new Color(142, 0, 99));
        com.github.retrooper.packetevents.protocol.particle.Particle<?> particle1 = new Particle<>(ParticleTypes.TRAIL, ptd);
        packetUtils.spawnParticle(user,
                particle1,
                50,
                vector3d,
                3f);
        packetUtils.playSound(user, Sounds.BLOCK_AMETHYST_BLOCK_RESONATE, 0.5f, (float) (0.4 * epicHoeUtils.getTier(user.getUUID())));
        //добавление заряда
        epicHoeUtils.addCharge(user.getUUID(), 1);
    }

    public void checkForBonus(ServerCookiePlayer serverCookiePlayer, Integer entityId) {
        if (bonusEntities.isEmpty() || !bonusEntities.contains(entityId))
            return;
        //удаляем существ
        CookieEntity.removeById(entityId, serverCookiePlayer.getUser());
        CookieEntity.removeById(entityId + 10000, serverCookiePlayer.getUser());
        bonusEntities.remove(entityId);
        packetUtils.playSound(serverCookiePlayer.getUser(), Sounds.ITEM_TRIDENT_RETURN, 1f, 0.7f);

        int amount = statsUtils.convertFortuneToAmount(statsUtils.extractStat(serverCookiePlayer.getPlayer(), StatType.FARMING_FORTUNE)) * 50;

        serverCookiePlayer.swingArm();

        for (ItemStack itemStack : chooseItemDrops(serverCookiePlayer, amount, false)) {
            serverCookiePlayer.getPlayer().getInventory().add(itemStack);
        }
    }

    public void createBonus(User user, Location location) {
        //random
        Random random = new Random();
        int chance = 3;
        if (random.nextInt(1, 100 + 1) <= (100 - chance))
            return;

        Location randomLocation = new Location(location.getX() + random.nextInt(-2, 3),
                location.getY(), location.getZ() + random.nextInt(-2, 3), 0f, 0f);

        CookieEntity interactEntity = new CookieEntity(EntityTypes.INTERACTION);
        interactEntity.setLocation(randomLocation);
        interactEntity.spawn(user);

        //200iq, тупо к айдишнику interaction прибавить 10000 и норм
        CookieTextDisplay textDisplay = new CookieTextDisplay(EntityTypes.TEXT_DISPLAY, interactEntity.getEntityId() + 10000);
        textDisplay.setText("клик");
        textDisplay.setLocation(randomLocation.getX(), randomLocation.getY() + 0.5, randomLocation.getZ());
        textDisplay.spawn(user);

        packetUtils.spawnParticle(user,
                new Particle<>(ParticleTypes.SONIC_BOOM), 1,
                new Vector3d(randomLocation.getX(), randomLocation.getY() + 0.5, randomLocation.getZ()), 0f);

        bonusEntities.add(interactEntity.getEntityId());
    }
}
