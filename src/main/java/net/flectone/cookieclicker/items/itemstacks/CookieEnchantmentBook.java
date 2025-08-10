package net.flectone.cookieclicker.items.itemstacks;

import net.flectone.cookieclicker.items.attributes.ToolType;
import net.flectone.cookieclicker.items.itemstacks.base.data.Features;
import net.flectone.cookieclicker.items.itemstacks.base.data.ItemTag;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.Optional;

public class CookieEnchantmentBook extends BaseCookieItem {
    public CookieEnchantmentBook(ItemTag itemTag) {
        super(Items.ENCHANTED_BOOK, new Features(itemTag, ToolType.ENCHANTMENT));
    }

    public void setStoredEnchantment(String name) {
        Optional<Registry<Enchantment>> enchantmentRegistry =
                MinecraftServer.getServer().registryAccess().lookup(Registries.ENCHANTMENT);

        if (enchantmentRegistry.isEmpty())
            return;

        enchantmentRegistry.get().asHolderIdMap().forEach(b -> {
            if (b.value().description().getString().equals(name)) {
                ItemEnchantments.Mutable itemEnchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
                itemEnchantments.set(b, 1);

                applyComponent(DataComponents.STORED_ENCHANTMENTS, itemEnchantments.toImmutable());
            }
        });
    }
}
