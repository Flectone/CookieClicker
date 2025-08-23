package net.flectone.cookieclicker.utility.hoes;


import com.github.retrooper.packetevents.protocol.color.Color;
import com.github.retrooper.packetevents.protocol.particle.Particle;
import com.github.retrooper.packetevents.protocol.particle.data.ParticleDustColorTransitionData;
import com.github.retrooper.packetevents.protocol.particle.type.ParticleTypes;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.sound.Sounds;
import com.github.retrooper.packetevents.util.Vector3d;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.entities.playerdata.ServerCookiePlayer;
import net.flectone.cookieclicker.items.attributes.CookieAbility;
import net.flectone.cookieclicker.items.attributes.StatType;
import net.flectone.cookieclicker.items.itemstacks.GeneratedCookieItem;
import net.flectone.cookieclicker.utility.PacketUtils;
import net.flectone.cookieclicker.utility.StatsUtils;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@Singleton
public class LegendaryHoeUpgrade {

    private final PacketUtils packetUtils;
    private final StatsUtils statsUtils;


    @Inject
    public LegendaryHoeUpgrade(PacketUtils packetUtils,
                               StatsUtils statsUtils) {
        this.packetUtils = packetUtils;
        this.statsUtils = statsUtils;
    }

    public void tryUpdateHoe(ServerCookiePlayer serverCookiePlayer, ItemStack legendaryHoe) {
        Player player = serverCookiePlayer.getPlayer();
        User user = serverCookiePlayer.getUser();

        //получаем текущую прочность
        Object currentDamage = legendaryHoe.getComponents().get(DataComponents.DAMAGE);
        int damage = currentDamage == null ? 0 : (int) currentDamage;

        setDamage(legendaryHoe, damage - 1);
        //Если "прочность" заполнилась, то добавляется одна удача и сбрасывается прочность
        if (damage <= 1) {
            setDamage(legendaryHoe, 99);

            GeneratedCookieItem upgradeableItem = GeneratedCookieItem.fromItemStack(legendaryHoe);
            upgradeableItem.addStat(StatType.FARMING_FORTUNE, 1);
            player.setItemInHand(InteractionHand.MAIN_HAND, upgradeableItem.toMinecraftStack());
        }

        packetUtils.playSound(user, Sounds.BLOCK_NETHERITE_BLOCK_BREAK, 1f, 1.8f);
    }

    public void setDamage(net.minecraft.world.item.ItemStack item, Integer value) {
        item.applyComponents(DataComponentPatch.builder()
                .set(DataComponents.DAMAGE,
                        value)
                .build()
        );
    }

    public void legHoeChange (ServerCookiePlayer serverCookiePlayer) {
        Player player = serverCookiePlayer.getPlayer();
        User user = serverCookiePlayer.getUser();
        ItemStack itemInHand = player.getMainHandItem();

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
