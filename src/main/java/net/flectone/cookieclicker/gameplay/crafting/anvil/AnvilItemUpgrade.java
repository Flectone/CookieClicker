package net.flectone.cookieclicker.gameplay.crafting.anvil;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.entities.playerdata.ServerCookiePlayer;
import net.flectone.cookieclicker.items.attributes.StatType;
import net.flectone.cookieclicker.items.attributes.ToolType;
import net.flectone.cookieclicker.items.itemstacks.GeneratedCookieItem;
import net.flectone.cookieclicker.items.itemstacks.base.data.ItemTag;
import net.flectone.cookieclicker.utility.ConversionUtils;
import net.flectone.cookieclicker.utility.StatsUtils;
import net.flectone.cookieclicker.utility.config.EquipmentUpgradeConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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

import javax.annotation.Nullable;
import java.util.Optional;

@Singleton
public class AnvilItemUpgrade {

    private final EquipmentUpgradeConfig equipmentUpgradeConfig;
    private final StatsUtils statsUtils;

    @Inject
    public AnvilItemUpgrade(EquipmentUpgradeConfig equipmentUpgradeConfig,
                            StatsUtils statsUtils) {
        this.statsUtils = statsUtils;
        this.equipmentUpgradeConfig = equipmentUpgradeConfig;
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

    @Deprecated
    public void checkForUpgrade(ServerCookiePlayer serverCookiePlayer) {
        if (!(serverCookiePlayer.getPlayer().containerMenu instanceof AnvilMenu anvilMenu))
            return;

        checkForUpgrade(anvilMenu);
    }

    public void checkForUpgrade(AnvilMenu anvilMenu) {
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

    @Nullable
    private ItemStack addStarToEquipment(ItemStack itemStack, ItemStack upgradeItem) {
        int currentTier = statsUtils.getTier(itemStack);

        Optional<ItemTag> requiredItem = equipmentUpgradeConfig.getRequiredItem(currentTier);

        if (requiredItem.isEmpty()) return null;

        if (!statsUtils.hasTag(upgradeItem, requiredItem.get())) return null;

        ArmorTrim armorTrim = new ArmorTrim(
                getCurrentTrimMaterial(equipmentUpgradeConfig.getLvlMaterial(currentTier).orElse("copper")),
                getTrimPattern(equipmentUpgradeConfig.getArmorTrim())
        );

        GeneratedCookieItem updatedItem = GeneratedCookieItem.fromItemStack(itemStack);
        updatedItem.multiplyAllStats(equipmentUpgradeConfig.getMultiplicationValue());
        updatedItem.setStat(StatType.EQUIPMENT_TIER, currentTier + 1);

        updatedItem.setComponent(DataComponents.TRIM, armorTrim);
        updatedItem.setComponent(DataComponents.CUSTOM_NAME, createNameComponent(itemStack.getCustomName()));

        return updatedItem.toMinecraftStack();
    }

    private Component createNameComponent(Component originalName) {
        MutableComponent mutableComponent = MutableComponent.create(PlainTextContents.EMPTY);

        if (originalName != null) {
            mutableComponent.append(originalName);
        }
        mutableComponent.append(ConversionUtils.convertToNMSComponent(MiniMessage.miniMessage().deserialize("<italic:false>" + equipmentUpgradeConfig.getUpgradeSymbol())));
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

    private Holder<TrimPattern> getTrimPattern(String name) {
        return getFromRegistry(Registries.TRIM_PATTERN, "minecraft:" + name);
    }

    private Holder<TrimMaterial> getCurrentTrimMaterial(String material) {
        return getFromRegistry(Registries.TRIM_MATERIAL, "minecraft:" + material);
    }
}
