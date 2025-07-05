package net.flectone.cookieclicker.items.itemstacks;

import com.mojang.serialization.JavaOps;
import io.papermc.paper.adventure.WrapperAwareSerializer;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.flectone.cookieclicker.items.attributes.StatType;
import net.flectone.cookieclicker.items.itemstacks.base.BaseCookieItem;
import net.flectone.cookieclicker.items.itemstacks.base.CookieItems;
import net.flectone.cookieclicker.items.itemstacks.base.data.Features;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
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
        CustomData customData = itemStack.getComponents().get(DataComponents.CUSTOM_DATA);
        CompoundTag compoundTag = customData != null ? customData.copyTag() : new CompoundTag();
        CompoundTag cookieClickerTag = compoundTag.contains(CookieItems.PLUGIN_KEY)
                ? compoundTag.getCompound(CookieItems.PLUGIN_KEY)
                : new CompoundTag();

        //конвертация старых предметов
        cookieClickerTag = compoundTag.contains("cookies")
                ? compoundTag.getCompound("cookies")
                : cookieClickerTag;
        //

        GeneratedCookieItem customItem = new GeneratedCookieItem(itemStack.getItem(), new Features(cookieClickerTag));

        itemStack.getComponents().forEach(customItem::applyComponent);

        ItemLore itemLore = itemStack.getComponents().has(DataComponents.LORE) ? itemStack.getComponents().get(DataComponents.LORE) : null;
        if (itemLore != null && !itemLore.lines().isEmpty()) {
            extractDescription(itemLore, hasTag(cookieClickerTag)).forEach(customItem::addLore);
        }

        if (compoundTag.contains("cookies")) {
            customItem.fixOldItem();
        }

        customItem.applyEnchantments(itemStack.getEnchantments());

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

    private static boolean hasTag(CompoundTag tag) {
        if (!tag.contains(ITEM_TAG_KEY))
            return false;

        return !tag.getString(ITEM_TAG_KEY).equals("none");

    }

    private void fixOldItem() {
        if (originalMaterial == Items.LEATHER_HORSE_ARMOR) {
            removeComponent(DataComponents.EQUIPPABLE);
            removeComponent(DataComponents.ITEM_NAME);
            removeComponent(DataComponents.DYED_COLOR);
            originalMaterial = HIDDEN_ITEM;

            //У меня пока что предметы становятся mojang_banner_pattern,
            //а у него есть редкость
            removeComponent(DataComponents.RARITY);
        }
    }

    public void applyEnchantments(ItemEnchantments enchantments) {
        if (enchantments == null)
            return;

        for (Object2IntMap.Entry<Holder<Enchantment>> enchantment : enchantments.entrySet()) {
            switch (enchantment.getKey().getRegisteredName()) {
                case "cookie:ccboost" -> {
                    features.setStatFromEnchant(StatType.FARMING_FORTUNE, (int) Math.pow(2, enchantment.getIntValue() - 1));
                }
                case "cookie:mining_boost" -> {
                    features.setStatFromEnchant(StatType.MINING_FORTUNE, enchantment.getIntValue());
                }
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
