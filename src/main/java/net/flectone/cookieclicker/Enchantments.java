package net.flectone.cookieclicker;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.keys.EnchantmentKeys;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import net.flectone.cookieclicker.items.itemstacks.base.CookieItems;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.jetbrains.annotations.NotNull;

public class Enchantments implements PluginBootstrap {
    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        MiniMessage miniMessage = MiniMessage.miniMessage();
        context.getLifecycleManager().registerEventHandler(RegistryEvents.ENCHANTMENT.compose().newHandler(event -> {
            event.registry().register(

                    EnchantmentKeys.create(Key.key(CookieItems.COOKIE_BOOST_ENCHANTMENT)),
                    b -> b.description(miniMessage.deserialize("<gradient:#d8bfa2:#fcce99><italic:false>Cookie Boost</gradient>"))
                            .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.HOES))
                            .anvilCost(1)
                            .maxLevel(10)
                            .weight(10)
                            .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(0, 0))
                            .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(0, 0))
                            .activeSlots(EquipmentSlotGroup.ANY)
            );
            event.registry().register(
                    EnchantmentKeys.create(Key.key(CookieItems.MINING_BOOST_ENCHANTMENT)),
                    b -> b.description(miniMessage.deserialize("<gradient:#736c6b:#a6a09f><italic:false>Mining Boost</gradient>"))
                            .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.PICKAXES))
                            .anvilCost(1)
                            .maxLevel(10)
                            .weight(10)
                            .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(0, 0))
                            .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(0, 0))
                            .activeSlots(EquipmentSlotGroup.ANY)
            );
        }));
    }

}
