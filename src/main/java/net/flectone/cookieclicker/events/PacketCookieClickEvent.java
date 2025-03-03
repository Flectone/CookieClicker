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
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.cookiePart.BagHoeUpgrade;
import net.flectone.cookieclicker.cookiePart.EpicHoeUtils;
import net.flectone.cookieclicker.items.ItemManager;
import net.flectone.cookieclicker.utility.CCConversionUtils;
import net.flectone.cookieclicker.utility.CCobjects.CookieEntity;
import net.flectone.cookieclicker.utility.CCobjects.CookiePlayer;
import net.flectone.cookieclicker.utility.ItemTagsUtility;
import net.flectone.cookieclicker.utility.PacketUtils;
import net.flectone.cookieclicker.utility.UtilsCookie;
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
    private final ItemTagsUtility itemTagsUtility;
    private final PacketUtils packetUtils;
    private final UtilsCookie utilsCookie;
    private final EpicHoeUtils epicHoeUtils;
    private final BagHoeUpgrade bagHoeUpgrade;
    private final CCConversionUtils converter;

    private final List<Integer> bonusEntities = new ArrayList<>();

    @Inject
    public PacketCookieClickEvent(ItemTagsUtility itemTagsUtility, ItemManager manager, UtilsCookie utilsCookie,
                                  PacketUtils packetUtils, EpicHoeUtils epicHoeUtils, BagHoeUpgrade bagHoeUpgrade,
                                  CCConversionUtils converter) {
        this.manager = manager;
        this.itemTagsUtility = itemTagsUtility;
        this.packetUtils = packetUtils;
        this.utilsCookie = utilsCookie;
        this.epicHoeUtils = epicHoeUtils;
        this.bagHoeUpgrade = bagHoeUpgrade;
        this.converter = converter;
    }

    public void onCookieClick(CookiePlayer cookiePlayer, Location location) {
        User user = cookiePlayer.getUser();

        Player player = converter.userToNMS(user);

        //
        int maxAmount = 0;

        maxAmount += utilsCookie.extractFortune(player);
        int droppedAmount = utilsCookie.convertFortune(maxAmount);

        droppedAmount += Math.round(droppedAmount * (0.5f * epicHoeUtils.getTier(player.getUUID())));
        String itemAbility = itemTagsUtility.getItemTag(player.getItemInHand(InteractionHand.MAIN_HAND));

        //если эпическая мотыга
        if (itemAbility != null && itemAbility.equals("epic_hoe")) {
            onClickWithHoe(user, new Vector3d(location.getX(), location.getY(), location.getZ()));
        }

        displayActionBar(player, maxAmount, droppedAmount);

        Vector3d itemFrameVector3d = new Vector3d(location.getX(), location.getY(), location.getZ());

        if (bagHoeUpgrade.updateHoe(user)) {
            packetUtils.spawnParticle(user, new Particle<>(ParticleTypes.TRIAL_SPAWNER_DETECTION_OMINOUS), 1,
                    itemFrameVector3d, 0.2f);
        }

        spawnItems(cookiePlayer, droppedAmount,
                location);

        packetUtils.spawnParticle(user,
                new Particle<>(ParticleTypes.TRIAL_SPAWNER_DETECTION),
                2,
                itemFrameVector3d,
                0.2f);
        packetUtils.playSound(user, Sounds.ENTITY_GENERIC_EAT, 0.3f, 1f);
    }

    private List<String> obtainAbilities(Player player) {
        List<String> abilities = new ArrayList<>();

        abilities.add(itemTagsUtility.getAbility(player.getItemInHand(InteractionHand.MAIN_HAND)));
        abilities.add(abilities.getFirst().equals("transform") ? itemTagsUtility.getAbility(player.getItemInHand(InteractionHand.OFF_HAND)) : "none");

        return abilities;
    }

    private String getMainAbility(Player player) {
        List<String> abilities = obtainAbilities(player);

        return abilities.get(1).equals("none") ? abilities.getFirst() : abilities.get(1);
    }

    private String getSecondAbility(Player player) {
        List<String> abilities = obtainAbilities(player);

        return abilities.get(1).equals("none") ? "none" : abilities.getFirst();
    }

    public List<ItemStack> calculateFullCount(String baseVariant, String enchantedVariant, Integer countNeeded, Integer currentCount) {
        List<ItemStack> items = new ArrayList<>();

        if (currentCount < countNeeded) {
            items.add(utilsCookie.createItemAmountNMS(manager.getNMS(baseVariant), currentCount));
        }
        if (currentCount >= countNeeded) {
            if (currentCount % countNeeded > 0) {
                items.add(utilsCookie.createItemAmountNMS(manager.getNMS(baseVariant), currentCount % countNeeded));
            }
            items.add(utilsCookie.createItemAmountNMS(manager.getNMS(enchantedVariant), currentCount / countNeeded));
        }
        return items;
    }

    private List<ItemStack> chooseItemDrops(CookiePlayer cookiePlayer, Integer finalFortune, boolean isAlternative) {
        Player player = cookiePlayer.getPlayer();

        //альтернативный предмет, если спец. условие выполнено, то этот предмет выпадет
        net.minecraft.world.item.ItemStack altItem = null;
        List<ItemStack> dropItems = new ArrayList<>();

        //проверка на способность у мотыги
        switch (getMainAbility(player)) {
            case "destroyer" -> {
                if (utilsCookie.compare(player.getOffhandItem(), manager.getNMS("ench_cocoa"))) {
                    dropItems.addAll(List.of());
                    dropItems.add(utilsCookie.createItemAmountNMS(manager.getNMS("chocolate"), 1)); //если в левой руке какао-бобы
                    player.getOffhandItem().setCount(player.getOffhandItem().getCount() - 1);
                } else {
                    dropItems.add(utilsCookie.createItemAmountNMS(manager.getNMS("cocoa_beans"), finalFortune));
                    dropItems.add(utilsCookie.createItemAmountNMS(manager.getNMS("wheat"), finalFortune));
                }
                altItem = manager.getNMS("pumpkin");
            }
            case "rose_bush" -> {
                altItem = getSecondAbility(player).equals("transform") ? manager.getNMS("glow_berries") : manager.getNMS("berries");
                dropItems.add(utilsCookie.createItemAmountNMS(manager.getNMS("cookie"), finalFortune));
            }
            default -> {
                dropItems.addAll(calculateFullCount("cookie", "ench_cookie", 160, finalFortune));
                altItem = manager.getNMS("pie");
            }
        }

        return isAlternative ? List.of(altItem) : dropItems;
    }

    public void spawnItems(CookiePlayer cookiePlayer, Integer droppedAmount, Location loca) {
        User user = cookiePlayer.getUser();
        Player player = cookiePlayer.getPlayer();
        //тут вероятность на спец. предмет
        Random rndB = new Random(System.currentTimeMillis());

        boolean altItem = false;
        if (rndB.nextInt(1, 100) >= 95
                && (getSecondAbility(player).equals("transform") || getMainAbility(player).equals("rose_bush"))) {
            loca = new Location(loca.getX() + rndB.nextDouble(-2.5, 2.5),
                    loca.getY() + rndB.nextDouble(0, 4),
                    loca.getZ() + rndB.nextDouble(-2.5, 2.5), 1, 1);
            altItem = true;
        }


        List<ItemStack> dropItems = chooseItemDrops(cookiePlayer, droppedAmount, altItem);

        for (net.minecraft.world.item.ItemStack i : dropItems)
            packetUtils.spawnItem(user, loca, i);

        createBonus(user, loca);
    }

    public void changeLegendaryHoeMode(User user) {
        bagHoeUpgrade.LegHoeChange(user);
    }

    public void bookShelfClick(User user, Player player) {
        ItemStack enchantedCookiesInHand = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (!utilsCookie.compare(enchantedCookiesInHand, manager.getNMS("ench_cookie"))) return;
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
            case "north":
                z--;
                break;
            case "south":
                z++;
                break;
            case "west":
                x--;
                break;
            case "east":
                x++;
                break;
        }

        Location bookLocation = new Location(x, y, z, 1, 1);
        //spawning item
        user.sendMessage(MiniMessage.miniMessage().deserialize("<#f4a91c>\uD83C\uDF6A <#f7f4b5>Вы купили книгу!"));
        packetUtils.spawnItem(user, bookLocation, manager.getNMS("book_boost1"));
        enchantedCookiesInHand.setCount(enchantedCookiesInHand.getCount() - 15);
    }

    private void displayActionBar(Player player, Integer maxAmount, Integer droppedAmount) {
        // Показ статистики
        // Первое число - общее количество удачи
        // Второе число - число выпавших предметов
        // В скобках первое - заряд от эпической мотыги
        // В скобках второе - уровень заряда, который увеличивает количество выпавших предметов (не удачу)
        player.displayClientMessage(converter.convertToNMSComponent(MiniMessage.miniMessage().deserialize("<#eb6514>" + maxAmount + "⯫ "
                + "<#e4a814>" + droppedAmount +"★ "
                + "<#b014eb>[" + epicHoeUtils.getCharge(player.getUUID()) + "% " +  epicHoeUtils.getTier(player.getUUID()) + "☄]")), true);
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

    public void checkForBonus(CookiePlayer cookiePlayer, Integer entityId) {
        if (bonusEntities.isEmpty() || !bonusEntities.contains(entityId))
            return;
        //удаляем существ
        CookieEntity.removeById(entityId, cookiePlayer.getUser());
        CookieEntity.removeById(entityId + 10000, cookiePlayer.getUser());
        bonusEntities.remove(entityId);
        packetUtils.playSound(cookiePlayer.getUser(), Sounds.ITEM_TRIDENT_RETURN, 1f, 0.7f);

        int amount = utilsCookie.convertFortune(utilsCookie.extractFortune(cookiePlayer.getPlayer())) * 50;

        cookiePlayer.swingArm();

        for (ItemStack itemStack : chooseItemDrops(cookiePlayer, amount, false)) {
            cookiePlayer.getPlayer().getInventory().add(itemStack);
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
        CookieEntity textDisplay = new CookieEntity(EntityTypes.TEXT_DISPLAY, interactEntity.getEntityId() + 10000);
        textDisplay.setText("клик");
        textDisplay.setLocation(randomLocation.getX(), randomLocation.getY() + 0.5, randomLocation.getZ());
        textDisplay.spawn(user);

        packetUtils.spawnParticle(user,
                new Particle<>(ParticleTypes.SONIC_BOOM), 1,
                new Vector3d(randomLocation.getX(), randomLocation.getY() + 0.5, randomLocation.getZ()), 0f);

        bonusEntities.add(interactEntity.getEntityId());
    }
}
