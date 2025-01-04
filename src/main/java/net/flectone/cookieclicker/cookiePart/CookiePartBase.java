package net.flectone.cookieclicker.cookiePart;

import com.github.retrooper.packetevents.protocol.color.Color;
import com.github.retrooper.packetevents.protocol.particle.Particle;
import com.github.retrooper.packetevents.protocol.particle.data.ParticleTrailData;
import com.github.retrooper.packetevents.protocol.particle.type.ParticleTypes;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.util.Vector3d;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.PacketUtils;
import net.flectone.cookieclicker.items.ItemManager;
import net.flectone.cookieclicker.utility.ItemTagsUtility;
import net.flectone.cookieclicker.utility.UtilsCookie;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Singleton
public class CookiePartBase {
    private final ItemManager manager;
    private final ItemTagsUtility itemTagsUtility;
    private final PacketUtils packetUtils;
    private final UtilsCookie utilsCookie;
    private final EpicHoeUtils epicHoeUtils;
    private final BagHoeUpgrade bagHoeUpgrade;

    @Inject
    public CookiePartBase (ItemTagsUtility itemTagsUtility, ItemManager manager, UtilsCookie utilsCookie,
                           PacketUtils packetUtils, EpicHoeUtils epicHoeUtils, BagHoeUpgrade bagHoeUpgrade) {
        this.manager = manager;
        this.itemTagsUtility = itemTagsUtility;
        this.packetUtils = packetUtils;
        this.utilsCookie = utilsCookie;
        this.epicHoeUtils = epicHoeUtils;
        this.bagHoeUpgrade = bagHoeUpgrade;
    }

    public void cookieClickPacketEvent(User user, ItemFrame itemFrame) {
        Player player = packetUtils.userToNMS(user);

        //
        int maxAmount = 1;
        Random rnd = new Random();


        maxAmount += utilsCookie.extractFortune(player);
        int droppedAmount = rnd.nextInt(maxAmount, maxAmount * 2);

        droppedAmount += Math.round(droppedAmount * (0.5f * epicHoeUtils.getTier(player)));
        String pdcValue = itemTagsUtility.getItemTag(player.getItemInHand(InteractionHand.MAIN_HAND));
        //если эпическая мотыга
        if (pdcValue != null && pdcValue.equals("epic_hoe")) {
            //world.spawnParticle(Particle.TRAIL, loca, 50, 2, 2, 2, 0, new Particle.Trail(loca, Color.PURPLE, 20));
            ParticleTrailData ptd = new ParticleTrailData(new Vector3d(itemFrame.getX(), itemFrame.getY(), itemFrame.getZ()), new Color(142, 0, 99));
            com.github.retrooper.packetevents.protocol.particle.Particle<?> particle1 = new Particle<>(ParticleTypes.TRAIL, ptd);
            packetUtils.spawnParticle(user,
                    particle1,
                    50,
                    new Vector3d(itemFrame.getX(), itemFrame.getY(), itemFrame.getZ()),
                    3f);
            packetUtils.playSound(user, 34, 1f, 0.5f);
            epicHoeUtils.addCharge(player, 1);
        }

        // Показ статистики
        player.displayClientMessage(utilsCookie.convertToNMSComponent(MiniMessage.miniMessage().deserialize("<#eb6514>" + maxAmount + "⯫ "
                + "<#e4a814>" + droppedAmount +"★ "
                + "<#b014eb>[" + epicHoeUtils.getCharge(player) + "% " +  epicHoeUtils.getTier(player) + "☄]")), true);

        Vector3d itemFrameVector3d = new Vector3d(itemFrame.getX(), itemFrame.getY(), itemFrame.getZ());

        if (bagHoeUpgrade.updateHoe(user)) {
            packetUtils.spawnParticle(user, new Particle<>(ParticleTypes.TRIAL_SPAWNER_DETECTION_OMINOUS), 1,
                    itemFrameVector3d, 0.2f);
        }

        calculateItemDrops(user,
                droppedAmount,
                packetUtils.userToNMS(user),
                new Location(itemFrame.getX(), itemFrame.getY(), itemFrame.getZ(), 1, 1));

        packetUtils.spawnParticle(user,
                new Particle<>(ParticleTypes.TRIAL_SPAWNER_DETECTION),
                2,
                itemFrameVector3d,
                0.2f);
        packetUtils.playSound(user, 575, 1f, 1f);
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
            default: //основной предмет, то есть печенье
                if (utilsCookie.compare(pl.getItemInHand(InteractionHand.MAIN_HAND), manager.getNMS("ench_cocoa"))) {
                    dropItems.add(utilsCookie.createItemAmountNMS(manager.getNMS("coal"), finalFortune)); //если в левой руке какао-бобы
                    pl.getItemInHand(InteractionHand.OFF_HAND).setCount(pl.getItemInHand(InteractionHand.OFF_HAND).getCount() - 1);
                }
                else dropItems.add(utilsCookie.createItemAmountNMS(manager.getNMS("cookie"), finalFortune));
                altItem = altItem != null ? altItem : manager.getNMS("pie");
        }
        //тут вероятность на спец. предмет
        Random rndB = new Random(System.currentTimeMillis());
        if (rndB.nextInt(1, 100) >= 95 && (value.equals("transform") || value2.equals("rose_bush"))) {
            loca = new com.github.retrooper.packetevents.protocol.world.Location(loca.getX() + rndB.nextDouble(-2.5, 2.5),
                    loca.getY() + rndB.nextDouble(0, 4),
                    loca.getZ() + rndB.nextDouble(-2.5, 2.5), 1, 1);

            dropItems = List.of(altItem);
        }

        //тут спавн предмета идёт уже
        for (net.minecraft.world.item.ItemStack i : dropItems)
            packetUtils.spawnItem(user, loca, i);
    }
}
