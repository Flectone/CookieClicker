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
import net.flectone.cookieclicker.utility.PacketUtils;
import net.flectone.cookieclicker.utility.CCConversionUtils;
import net.flectone.cookieclicker.utility.CCobjects.ClickerItems;
import net.flectone.cookieclicker.utility.ItemTagsUtility;
import net.flectone.cookieclicker.utility.UtilsCookie;
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
public class BagHoeUpgrade {
    private final UtilsCookie utilsCookie;
    private final ItemTagsUtility itemTagsUtility;
    private final PacketUtils packetUtils;
    private final CCConversionUtils conversionUtils;


    @Inject
    public BagHoeUpgrade(UtilsCookie utilsCookie, ItemTagsUtility itemTagsUtility, PacketUtils packetUtils,
                         CCConversionUtils conversionUtils) {
        this.utilsCookie = utilsCookie;
        this.itemTagsUtility = itemTagsUtility;
        this.packetUtils = packetUtils;
        this.conversionUtils = conversionUtils;
    }

    public boolean updateHoe(User user) {
        Player player = conversionUtils.userToNMS(user);
        ItemStack itemInHand = player.getItemInHand(InteractionHand.MAIN_HAND);

        if (itemInHand.getItem().equals(Items.AIR)) return false;

        String ability = itemTagsUtility.getAbility(itemInHand);
        if (!(ability.equals("infinity"))) return false;

        //itemNMS.getComponents().get(DataComponents.DAMAGE) может выдать null и это плохо
        //Но теперь оно не может выдать null и это хорошо
        //200iq момент
        Object currentDamage = itemInHand.getComponents().get(DataComponents.DAMAGE);
        int damage = currentDamage == null ? 0 : (int) currentDamage;

        setDamage(itemInHand, damage - 1);
        //Если "прочность" заполнилась, то добавляется одна удача и сбрасывается прочность
        if (damage <= 1) {
            setDamage(itemInHand, 99);

            itemTagsUtility.setStat(itemInHand, ClickerItems.fortuneTag, itemTagsUtility.getBaseFortune(itemInHand) + 1);
        }
        packetUtils.playSound(user, Sounds.BLOCK_NETHERITE_BLOCK_BREAK, 1f, 1.8f);

        //кринж штука, чисто для теста
        player.setItemInHand(InteractionHand.MAIN_HAND, itemInHand);
        utilsCookie.updateStats(player.getItemInHand(InteractionHand.MAIN_HAND));
        return true;
    }

    public void setDamage(net.minecraft.world.item.ItemStack item, Integer value) {
        item.applyComponents(DataComponentPatch.builder()
                .set(DataComponents.DAMAGE,
                        value)
                .build()
        );
    }

    public void LegHoeChange (User user) {
        Player player = conversionUtils.userToNMS(user);

        //проверка, клик в воздух или нет
        HitResult hitResult = player.getRayTrace(7, ClipContext.Fluid.NONE);
        if (!hitResult.getType().equals(HitResult.Type.MISS)) return;

        //предмет в руке
        ItemStack itemInHand = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (!(itemTagsUtility.getItemTag(itemInHand).equals("leg_hoe"))) return;

        //компонент item_model не берёт просто Item, ему надо ResourceLocation, поэтому вот
        ResourceLocation resourceLocation = itemInHand.getComponents().get(DataComponents.ITEM_MODEL);
        if (resourceLocation == null) return;

        String newCurrentItemType = resourceLocation.toString().equals("minecraft:golden_hoe") ? "iron_hoe" : "golden_hoe";
        DataComponentPatch itemModelComponent = DataComponentPatch.builder()
                .set(DataComponents.ITEM_MODEL, ResourceLocation.tryBuild(ResourceLocation.DEFAULT_NAMESPACE, newCurrentItemType))
                .build();

        //частицы красивые
        ParticleDustColorTransitionData particleDustColorTransitionData = new ParticleDustColorTransitionData(1f,
                new Color(14606046), new Color(16758272));
        Particle<?> particle = new Particle<>(ParticleTypes.DUST_COLOR_TRANSITION, particleDustColorTransitionData);

        packetUtils.spawnParticle(user, particle, 50, new Vector3d(player.getX(), player.getY() + 1, player.getZ()), 0.5f);
        player.getItemInHand(InteractionHand.MAIN_HAND).applyComponents(itemModelComponent);
        itemTagsUtility.setAbility(itemInHand, resourceLocation.toString().equals("minecraft:golden_hoe") ? "transform" : "infinity");
        packetUtils.playSound(user, Sounds.ENTITY_ENDER_EYE_DEATH, 1f, 0.8f);
    }
}
