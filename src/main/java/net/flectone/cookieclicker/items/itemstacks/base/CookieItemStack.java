package net.flectone.cookieclicker.items.itemstacks.base;

import net.flectone.cookieclicker.items.attributes.StatType;
import net.flectone.cookieclicker.items.itemstacks.base.data.Features;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class CookieItemStack implements CookieItems {
    //тип предмета
    protected Item originalMaterial;

    //тег, категория, способности и статы
    protected final Features features;

    private boolean removeAttribute = false;

    private final HashMap<String, TypedDataComponent<?>> DATA_COMPONENTS = new HashMap<>();
    protected final List<String> lore = new ArrayList<>();

    protected CookieItemStack(Item originalMaterial, Features features) {
        this.originalMaterial = originalMaterial;

        this.features = features;

        lore.add("<dark_gray>#" + features.getItemTag());
    }

    public String getItemTag() {
        return features.getItemTag();
    }

    protected void setStat(StatType statType, Integer value) {
        features.applyStat(statType, value);
    }

    protected <T> void applyComponent(DataComponentType<T> type, T value) {
        DATA_COMPONENTS.put(type.toString(), new TypedDataComponent<>(type, value));
    }

    protected void applyComponent(TypedDataComponent<?> typedDataComponent) {
       DATA_COMPONENTS.put(typedDataComponent.type().toString(), typedDataComponent);
    }

    protected void removeComponent(DataComponentType<?> type) {
        DATA_COMPONENTS.remove(type.toString());
    }

    public void removeVisibleAttributes(boolean b) {
        removeAttribute = b;
    }

    public ItemStack toNMS() {
        combineLore();
        applyFeatures();

        ItemStack itemStack = new ItemStack(originalMaterial);

        //Применение всех компонентов на предмет
        for (TypedDataComponent<?> dataComponent : DATA_COMPONENTS.values()) {
            itemStack.applyComponents(DataComponentPatch.builder()
                    .set(dataComponent).build());
        }

        if (removeAttribute) {
            itemStack.remove(DataComponents.ATTRIBUTE_MODIFIERS);
        }

        return itemStack;
    }

    private void combineLore() {
        List<Component> fullLore = new ArrayList<>();
        //Сначала запись описания предмета в лор
        lore.forEach(string -> fullLore.add(convertToNMSComponent(miniMessage.deserialize(string))));

        //Запись всех характеристик и способностей в лор
        List<String> featuresList = features.getStatsAsLoreList();

        featuresList.forEach(string -> {
            fullLore.add(convertToNMSComponent(miniMessage.deserialize(string)));
        });

        //Создание компонента для предмета
        TypedDataComponent<ItemLore> loreData = new TypedDataComponent<>(DataComponents.LORE, new ItemLore(fullLore));

        applyComponent(loreData);
    }

    private void applyFeatures() {
        CompoundTag createdTag = features.createCompoundTag();

        TypedDataComponent<CustomData> customData = new TypedDataComponent<>(DataComponents.CUSTOM_DATA, CustomData.of(createdTag));
        applyComponent(customData);
    }
}
