package net.flectone.cookieclicker.items.itemstacks.base;

import com.mojang.serialization.JavaOps;
import io.papermc.paper.adventure.WrapperAwareSerializer;
import net.flectone.cookieclicker.items.attributes.CookieAbility;
import net.flectone.cookieclicker.items.attributes.ToolType;
import net.flectone.cookieclicker.items.itemstacks.base.data.Features;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BaseCookieItem extends CookieItemStack {

    public BaseCookieItem(Item originalMaterial, Features features) {
        super(originalMaterial, features);

        if (features.getCategory() != ToolType.NONE) {
            removeVisibleAttributes(true);
        }
    }

    @ApiStatus.Experimental
    public static BaseCookieItem fromItemStack(ItemStack itemStack) {
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

        BaseCookieItem customItem = new BaseCookieItem(itemStack.getItem(), new Features(cookieClickerTag));

        itemStack.getComponents().forEach(customItem::applyComponent);

        ItemLore itemLore = itemStack.getComponents().has(DataComponents.LORE) ? itemStack.getComponents().get(DataComponents.LORE) : null;
        if (itemLore != null && !itemLore.lines().isEmpty()) {
            extractDescription(itemLore).forEach(customItem::addLore);
        }

        if (compoundTag.contains("cookies")) {
            customItem.fixOldItem();
        }

        return customItem;
    }

    //хз как нормально это сделать, ну вот
    private static List<String> extractDescription(ItemLore itemLore) {
        List<String> description = new ArrayList<>();
        String singleLine;

        List<net.minecraft.network.chat.Component> lore = new ArrayList<>(itemLore.lines());
        lore.removeFirst(); //Убираем первую строку, в которой "#предмет"

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

    public void setItemModel(Item to) {
        StringBuilder stringBuilder = new StringBuilder(to.toString().toLowerCase());
        stringBuilder.delete(0, 10);
        applyComponent(
                DataComponents.ITEM_MODEL,
                ResourceLocation.tryBuild(ResourceLocation.DEFAULT_NAMESPACE, stringBuilder.toString())
        );
    }

    public void hideItem() {
        setItemModel(originalMaterial);
        originalMaterial = HIDDEN_ITEM;
    }

    public void setEnchantmentGlint() {
        applyComponent(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
    }

    public void setName(String text) {
        applyComponent(
                DataComponents.CUSTOM_NAME,
                convertToNMSComponent(miniMessage.deserialize(text))
        );
    }

    public void addLore(String... strings) {
        //если в списке только тег, то пусть после него будет пробел
        lore.addAll(Arrays.asList(strings));
    }

    public void setAbility(CookieAbility cookieAbility) {
        features.ability = cookieAbility;

        if (cookieAbility.getType().equals("infinity")) {
            applyComponent(DataComponents.MAX_DAMAGE, 100);
        }
    }
}
