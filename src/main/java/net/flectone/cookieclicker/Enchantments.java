package net.flectone.cookieclicker;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.jetbrains.annotations.NotNull;

public class Enchantments implements PluginBootstrap {
    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        MiniMessage miniMessage = MiniMessage.miniMessage();
        context.getLifecycleManager().registerEventHandler(RegistryEvents.ENCHANTMENT.freeze().newHandler(event -> {
            event.registry().register(

                    TypedKey.create(RegistryKey.ENCHANTMENT, Key.key("cookie:ccboost")),

                    b -> b.description(miniMessage.deserialize("<rainbow>Cookie Boost</rainbow>"))
                            .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.HOES))
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
