package net.flectone.cookieclicker.events;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.items.attributes.StatType;
import net.flectone.cookieclicker.items.attributes.ToolType;
import net.flectone.cookieclicker.items.itemstacks.GeneratedCookieItem;
import net.flectone.cookieclicker.items.itemstacks.base.data.ItemTag;
import net.flectone.cookieclicker.playerdata.ServerCookiePlayer;
import net.flectone.cookieclicker.utility.Pair;
import net.flectone.cookieclicker.utility.StatsUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimPattern;

import java.util.List;
import java.util.Optional;

@Singleton
public class AnvilEvent {
    private final StatsUtils statsUtils;

    private final List<Pair<ItemTag, String>> stages = List.of(
            new Pair<>(ItemTag.CAKE_UPGRADE_ITEM, "quartz"),
            new Pair<>(ItemTag.CAKE_UPGRADE_ITEM, "emerald"),
            new Pair<>(ItemTag.CAKE_UPGRADE_ITEM, "diamond"),
            new Pair<>(ItemTag.CAKE_UPGRADE_ITEM, "amethyst"),
            new Pair<>(ItemTag.CAKE_UPGRADE_ITEM, "gold"),
            new Pair<>(ItemTag.COOKIE, "netherite")
    );

    @Inject
    public AnvilEvent(StatsUtils statsUtils) {
        this.statsUtils = statsUtils;
    }

    public void anvilClick(Player player, Integer slot) {
        if (slot != 2) return;

        ItemStack anvilItem = player.containerMenu.getSlot(2).getItem();
        if (anvilItem.getItem() == Items.AIR)
            return;

        //Конвертируется предмет в куки предмет, а потом обратно.
        //Наверное не самый оптимизированный вариант...
        GeneratedCookieItem cookieAnvilItem = GeneratedCookieItem.fromItemStack(anvilItem);

        player.containerMenu.setItem(2, player.containerMenu.getStateId(), cookieAnvilItem.toMinecraftStack());
    }

    public void processUpgrade(ServerCookiePlayer serverCookiePlayer) {
        if (!(serverCookiePlayer.getPlayer().containerMenu instanceof AnvilMenu anvilMenu))
            return;
        ItemStack firstItem = anvilMenu.getItems().getFirst();
        ItemStack secondItem = anvilMenu.getItems().get(1);

        if (firstItem.getItem().equals(Items.AIR) || secondItem.getItem().equals(Items.AIR))
            return;

        if (statsUtils.getCategory(firstItem) != ToolType.EQUIPMENT)
            return;

        ItemStack upgraded = addStarToEquipment(firstItem, secondItem);
        if (upgraded == null)
            return;

        anvilMenu.setItem(2, anvilMenu.getStateId(), upgraded);
        anvilMenu.cost.set(1);
    }

    private ItemStack addStarToEquipment(ItemStack itemStack, ItemStack upgradeItem) {
        int currentTier = statsUtils.getTier(itemStack);

        if (currentTier == stages.size() || !statsUtils.hasTag(upgradeItem, stages.get(currentTier).left()))
            return null;

        ArmorTrim armorTrim = new ArmorTrim(
                getCurrentTrimMaterial(stages.get(currentTier).getValue()),
                getSentryTrimPattern()
        );

        GeneratedCookieItem updatedItem = GeneratedCookieItem.fromItemStack(itemStack);
        updatedItem.addStat(StatType.EQUIPMENT_TIER, 1);
        updatedItem.addStat(StatType.FARMING_FORTUNE, 30);

        updatedItem.setComponent(DataComponents.TRIM, armorTrim);
        updatedItem.setComponent(DataComponents.CUSTOM_NAME, createNameComponent(itemStack.getCustomName()));

        return updatedItem.toMinecraftStack();
    }

    private Component createNameComponent(Component originalName) {
        MutableComponent mutableComponent = MutableComponent.create(PlainTextContents.EMPTY);

        if (originalName != null) {
            mutableComponent.append(originalName);
        }
        mutableComponent.append(Component.literal("+").setStyle(Style.EMPTY.withColor(10630898).withItalic(false)));
        return mutableComponent;
    }

    private <E> Holder<E> getFromRegistry(ResourceKey<? extends Registry<E>> registry, String toFind) {
        Optional<Registry<E>> registryOptional = MinecraftServer.getServer().registryAccess().lookup(registry);
        if (registryOptional.isEmpty()) {
            return null;
        }
        for (Holder<E> holder : registryOptional.get().asHolderIdMap()) {
            if (holder.getRegisteredName().equals(toFind)) {
                return holder;
            }
        }
        return null;
    }

    private Holder<TrimPattern> getSentryTrimPattern() {
        return getFromRegistry(Registries.TRIM_PATTERN, "minecraft:sentry");
    }

    private Holder<TrimMaterial> getCurrentTrimMaterial(String material) {
        return getFromRegistry(Registries.TRIM_MATERIAL, "minecraft:" + material);
    }
}
