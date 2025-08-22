package net.flectone.cookieclicker.items.itemstacks;

import com.mojang.serialization.JavaOps;
import io.papermc.paper.adventure.WrapperAwareSerializer;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.flectone.cookieclicker.items.attributes.StatType;
import net.flectone.cookieclicker.items.itemstacks.base.data.Features;
import net.flectone.cookieclicker.items.itemstacks.base.data.ItemTag;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

public class GeneratedCookieItem extends BaseCookieItem {
    private GeneratedCookieItem(Item originalMaterial, Features features) {
        super(originalMaterial, features);
    }

    @ApiStatus.Experimental
    public static GeneratedCookieItem fromItemStack(ItemStack itemStack) {
        Features feature = new Features(itemStack);
        GeneratedCookieItem customItem = new GeneratedCookieItem(itemStack.getItem(), feature);

        itemStack.getComponents().forEach(customItem::applyComponent);

        ItemLore itemLore = itemStack.getComponents().has(DataComponents.LORE) ? itemStack.getComponents().get(DataComponents.LORE) : null;
        if (itemLore != null && !itemLore.lines().isEmpty()) {
            extractDescription(itemLore, hasTag(feature)).forEach(customItem::addLore);
        }

        if (itemStack.getItem() == Items.LEATHER_HORSE_ARMOR) {
            customItem.fixOldItem();
        }

        customItem.applyEnchantments(itemStack.getEnchantments());
        customItem.removeComponent(DataComponents.REPAIR_COST);

        return customItem;
    }

    //хз как нормально это сделать, ну вот
    private static List<String> extractDescription(ItemLore itemLore, boolean removeFirstLine) {
        List<String> description = new ArrayList<>();
        String singleLine;

        List<net.minecraft.network.chat.Component> lore = new ArrayList<>(itemLore.lines());
        if (removeFirstLine) lore.removeFirst(); //Убираем первую строку, в которой "#предмет"

        //Штука, которая переделывает nms компонент в adventure
        ComponentSerializer<Component, Component, net.minecraft.network.chat.Component> componentSerializer;
        componentSerializer = new WrapperAwareSerializer(() -> MinecraftServer.getServer().registryAccess().createSerializationContext(JavaOps.INSTANCE));

        if (lore.isEmpty())
            return description;

        for (net.minecraft.network.chat.Component component : lore) {
            singleLine = miniMessage.serialize(componentSerializer.deserialize(component));
            //Если строка "Когда используется/в ведущей руке", то значит дальше статы и их вытаскивать не надо
            //При сборке предмета перед статами добавляется пустая строка, поэтому тут её надо будет убрать
            if (singleLine.contains("Когда")) {
                break;
            }

            description.add(singleLine);
        }

        description.removeIf(string -> description.indexOf(string) == description.size() - 1 && string.isEmpty());

        return description;
    }

    private static boolean hasTag(Features feature) {
        return feature.getItemTag() != ItemTag.EMPTY;
    }

    private void fixOldItem() {
        removeComponent(DataComponents.EQUIPPABLE);
        removeComponent(DataComponents.ITEM_NAME);
        removeComponent(DataComponents.DYED_COLOR);
        originalMaterial = HIDDEN_ITEM;

        //У меня пока что предметы становятся mojang_banner_pattern,
        //а у него есть редкость
        removeComponent(DataComponents.RARITY);
    }

    public void applyEnchantments(ItemEnchantments enchantments) {
        if (enchantments == null)
            return;

        for (Object2IntMap.Entry<Holder<Enchantment>> enchantment : enchantments.entrySet()) {
            switch (enchantment.getKey().getRegisteredName()) {
                case COOKIE_BOOST_ENCHANTMENT -> features.setStatFromEnchant(
                        StatType.FARMING_FORTUNE, 1 << (enchantment.getIntValue() - 1));
                case MINING_BOOST_ENCHANTMENT -> features.setStatFromEnchant(
                        StatType.MINING_FORTUNE, enchantment.getIntValue());
            }
        }
    }

    public void addStat(StatType statType, Integer valueToAdd) {
        features.applyStat(statType, features.getStat(statType) + valueToAdd);
    }

    public <T> void setComponent(DataComponentType<T> type, T value) {
        applyComponent(type, value);
    }
}
