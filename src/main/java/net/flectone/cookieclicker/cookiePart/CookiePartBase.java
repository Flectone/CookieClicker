package net.flectone.cookieclicker.cookiePart;

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
import net.minecraft.world.entity.decoration.ItemFrame;
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
public class CookiePartBase {
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
    public CookiePartBase (ItemTagsUtility itemTagsUtility, ItemManager manager, UtilsCookie utilsCookie,
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

    public void cookieClickPacketEvent(User user, ItemFrame itemFrame) {
        Player player = converter.userToNMS(user);

        //
        int maxAmount = 0;

        maxAmount += utilsCookie.extractFortune(player);
        int droppedAmount = utilsCookie.convertFortune(maxAmount);

        droppedAmount += Math.round(droppedAmount * (0.5f * epicHoeUtils.getTier(player)));
        String pdcValue = itemTagsUtility.getItemTag(player.getItemInHand(InteractionHand.MAIN_HAND));
        //если эпическая мотыга
        if (pdcValue != null && pdcValue.equals("epic_hoe")) {
            //спавн частиц
            ParticleTrailData ptd = new ParticleTrailData(new Vector3d(itemFrame.getX(), itemFrame.getY(), itemFrame.getZ()), new Color(142, 0, 99));
            com.github.retrooper.packetevents.protocol.particle.Particle<?> particle1 = new Particle<>(ParticleTypes.TRAIL, ptd);
            packetUtils.spawnParticle(user,
                    particle1,
                    50,
                    new Vector3d(itemFrame.getX(), itemFrame.getY(), itemFrame.getZ()),
                    3f);
            packetUtils.playSound(user, Sounds.BLOCK_AMETHYST_BLOCK_RESONATE, 0.5f, (float) (0.4 * epicHoeUtils.getTier(player)));
            //добавление заряда
            epicHoeUtils.addCharge(player, 1);
        }

        // Показ статистики
        // Первое число - общее количество удачи
        // Второе число - число выпавших предметов
        // В скобках первое - заряд от эпической мотыги
        // В скобках второе - уровень заряда, который увеличивает количество выпавших предметов (не удачу)
        player.displayClientMessage(converter.convertToNMSComponent(MiniMessage.miniMessage().deserialize("<#eb6514>" + maxAmount + "⯫ "
                + "<#e4a814>" + droppedAmount +"★ "
                + "<#b014eb>[" + epicHoeUtils.getCharge(player) + "% " +  epicHoeUtils.getTier(player) + "☄]")), true);

        Vector3d itemFrameVector3d = new Vector3d(itemFrame.getX(), itemFrame.getY(), itemFrame.getZ());

        if (bagHoeUpgrade.updateHoe(user)) {
            packetUtils.spawnParticle(user, new Particle<>(ParticleTypes.TRIAL_SPAWNER_DETECTION_OMINOUS), 1,
                    itemFrameVector3d, 0.2f);
        }

        calculateItemDrops(user,
                droppedAmount,
                converter.userToNMS(user),
                new Location(itemFrame.getX(), itemFrame.getY(), itemFrame.getZ(), 1, 1));

        packetUtils.spawnParticle(user,
                new Particle<>(ParticleTypes.TRIAL_SPAWNER_DETECTION),
                2,
                itemFrameVector3d,
                0.2f);
        packetUtils.playSound(user, Sounds.ENTITY_GENERIC_EAT, 0.3f, 1f);
    }

    public void calculateItemDrops(User user, Integer finalFortune, net.minecraft.world.entity.player.Player pl, com.github.retrooper.packetevents.protocol.world.Location loca) {
        String value = itemTagsUtility.getAbility(pl.getItemInHand(InteractionHand.MAIN_HAND));
        String value2 = value.equals("transform") ? itemTagsUtility.getAbility(pl.getItemInHand(InteractionHand.OFF_HAND)) : value;
        //альтернативный предмет, если спец. условие выполнено, то этот предмет выпадет
        net.minecraft.world.item.ItemStack altItem = null;
        List<ItemStack> dropItems = new ArrayList<>();

        //проверка на способность у мотыги
        switch (value2) {
            case "destroyer": //для уничтожителя печенья
                dropItems.add(utilsCookie.createItemAmountNMS(manager.getNMS("cocoa_beans"), finalFortune));
                dropItems.add(utilsCookie.createItemAmountNMS(manager.getNMS("wheat"), finalFortune));
                altItem = manager.getNMS("pumpkin");
                break;

            case "rose_bush": //для куста роз
                altItem = value.equals(value2) ? manager.getNMS("berries") : manager.getNMS("glow_berries");
                //тут нет break; потому что мне надо, чтобы default тоже выполнился

            default: //основной предмет, то есть печенье
                if (utilsCookie.compare(pl.getItemInHand(InteractionHand.MAIN_HAND), manager.getNMS("ench_cocoa"))) {
                    dropItems.add(utilsCookie.createItemAmountNMS(manager.getNMS("coal"), finalFortune)); //если в левой руке какао-бобы
                    pl.getItemInHand(InteractionHand.OFF_HAND).setCount(pl.getItemInHand(InteractionHand.OFF_HAND).getCount() - 1);
                } else {
                    dropItems.add(utilsCookie.createItemAmountNMS(manager.getNMS("cookie"), finalFortune));
                }
                altItem = altItem != null ? altItem : manager.getNMS("pie");
        }
        //тут вероятность на спец. предмет
        Random rndB = new Random(System.currentTimeMillis());
        if (rndB.nextInt(1, 100) >= 95 && (value.equals("transform") || value2.equals("rose_bush"))) {
            loca = new Location(loca.getX() + rndB.nextDouble(-2.5, 2.5),
                    loca.getY() + rndB.nextDouble(0, 4),
                    loca.getZ() + rndB.nextDouble(-2.5, 2.5), 1, 1);

            dropItems = List.of(altItem);
        }

        //тут спавн предмета идёт уже
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

        cookiePlayer.getPlayer().getInventory().add(utilsCookie.createItemAmountNMS(manager.getNMS("cookie"), amount));
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
