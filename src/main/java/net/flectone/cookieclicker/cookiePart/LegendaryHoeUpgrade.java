package net.flectone.cookieclicker.cookiePart;


import com.github.retrooper.packetevents.protocol.color.Color;
import com.github.retrooper.packetevents.protocol.particle.Particle;
import com.github.retrooper.packetevents.protocol.particle.data.ParticleDustColorTransitionData;
import com.github.retrooper.packetevents.protocol.particle.type.ParticleTypes;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.sound.Sounds;
import com.github.retrooper.packetevents.util.Vector3d;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.items.attributes.CookieAbility;
import net.flectone.cookieclicker.items.attributes.StatType;
import net.flectone.cookieclicker.items.itemstacks.GeneratedCookieItem;
import net.flectone.cookieclicker.utility.*;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;

@Singleton
public class LegendaryHoeUpgrade {
    private final UtilsCookie utilsCookie;
    private final ItemTagsUtility itemTagsUtility;
    private final PacketUtils packetUtils;
    private final CCConversionUtils conversionUtils;
    private final StatsUtils statsUtils;


    @Inject
    public LegendaryHoeUpgrade(UtilsCookie utilsCookie, ItemTagsUtility itemTagsUtility, PacketUtils packetUtils,
                               CCConversionUtils conversionUtils, StatsUtils statsUtils) {
        this.utilsCookie = utilsCookie;
        this.itemTagsUtility = itemTagsUtility;
        this.packetUtils = packetUtils;
        this.conversionUtils = conversionUtils;
        this.statsUtils = statsUtils;
    }

    public void tryUpdateHoe(User user, Vector3d itemFrameLocation) {
        Player player = conversionUtils.userToNMS(user);
        ItemStack itemInHand = player.getItemInHand(InteractionHand.MAIN_HAND);

        if (itemInHand.getItem().equals(Items.AIR)) return;

        CookieAbility ability = statsUtils.getAbility(itemInHand);
        if (ability != CookieAbility.INFINITY_UPGRADE) return;

        //получаем текущую прочность
        Object currentDamage = itemInHand.getComponents().get(DataComponents.DAMAGE);
        int damage = currentDamage == null ? 0 : (int) currentDamage;

        setDamage(itemInHand, damage - 1);
        //Если "прочность" заполнилась, то добавляется одна удача и сбрасывается прочность
        if (damage <= 1) {
            setDamage(itemInHand, 99);

            GeneratedCookieItem upgradeableItem = GeneratedCookieItem.fromItemStack(itemInHand);
            upgradeableItem.addStat(StatType.FARMING_FORTUNE, 1);
            player.setItemInHand(InteractionHand.MAIN_HAND, upgradeableItem.toMinecraftStack());
        }

        packetUtils.playSound(user, Sounds.BLOCK_NETHERITE_BLOCK_BREAK, 1f, 1.8f);
        packetUtils.spawnParticle(user, new Particle<>(ParticleTypes.TRIAL_SPAWNER_DETECTION_OMINOUS), 1,
                itemFrameLocation, 0.2f);

    }

    public void setDamage(net.minecraft.world.item.ItemStack item, Integer value) {
        item.applyComponents(DataComponentPatch.builder()
                .set(DataComponents.DAMAGE,
                        value)
                .build()
        );
    }

    public void legHoeChange (User user) {
        Player player = conversionUtils.userToNMS(user);

        //проверка, клик в воздух или нет
        HitResult hitResult = player.getRayTrace(7, ClipContext.Fluid.NONE);
        if (!hitResult.getType().equals(HitResult.Type.MISS)) return;

        //предмет в руке
        ItemStack itemInHand = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (!statsUtils.getItemTag(itemInHand).equals("leg_hoe")) return;

        GeneratedCookieItem legendaryHoe = GeneratedCookieItem.fromItemStack(itemInHand);

        //Текущий режим предмета
        //Мне лень делать это как-то по-другому,
        //поэтому я просто беру из nms предмета модель
        ResourceLocation resourceLocation = itemInHand.getComponents().get(DataComponents.ITEM_MODEL);
        String currentMode = resourceLocation != null
                ? resourceLocation.toString()
                : "none";


        legendaryHoe.setItemModel(currentMode.equals("minecraft:golden_hoe")
                ? Items.IRON_HOE
                : Items.GOLDEN_HOE);

        legendaryHoe.setAbility(currentMode.equals("minecraft:golden_hoe")
                ? CookieAbility.TRANSFORM
                : CookieAbility.INFINITY_UPGRADE);

        player.setItemInHand(InteractionHand.MAIN_HAND, legendaryHoe.toMinecraftStack());

        //частицы красивые
        ParticleDustColorTransitionData particleDustColorTransitionData = new ParticleDustColorTransitionData(1f,
                new Color(14606046), new Color(16758272));
        Particle<?> particle = new Particle<>(ParticleTypes.DUST_COLOR_TRANSITION, particleDustColorTransitionData);

        packetUtils.spawnParticle(user, particle, 50, new Vector3d(player.getX(), player.getY() + 1, player.getZ()), 0.5f);
        packetUtils.playSound(user, Sounds.ENTITY_ENDER_EYE_DEATH, 1f, 0.8f);
    }
}
