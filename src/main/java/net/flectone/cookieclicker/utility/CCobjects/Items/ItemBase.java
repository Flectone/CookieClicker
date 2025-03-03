package net.flectone.cookieclicker.utility.CCobjects.Items;

import com.mojang.serialization.JavaOps;
import io.papermc.paper.adventure.WrapperAwareSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.ItemLore;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.*;
public abstract class ItemBase implements ClickerItems{
    protected final String displayName;
    protected final String itemTag;
    protected final String category;
    protected Material itemType;
    protected final List<Component> fullLore = new ArrayList<>(), preStatsLore = new ArrayList<>();
    protected final HashMap<NamespacedKey, Integer> stats = new HashMap<>();
    protected String ability = "none";
    protected final MiniMessage miniMessage = MiniMessage.miniMessage();


    // на этом держатся все предметы этого плагина
    protected final List<DataComponentPatch> components = new ArrayList<>();

    protected ItemBase(String displayName, String itemTag, String category, Material itemType) {
        this.displayName = displayName;
        this.itemTag = itemTag;
        this.category = category;
        this.itemType = itemType;
    }

    //конвертация компонентов
    public net.minecraft.network.chat.Component convertToNMSComponent(net.kyori.adventure.text.Component component) {
        ComponentSerializer<Component, Component, net.minecraft.network.chat.Component> componentSerializer;
        componentSerializer = new WrapperAwareSerializer(() -> MinecraftServer.getServer().registryAccess().createSerializationContext(JavaOps.INSTANCE));
        return componentSerializer.serialize(component);
    }

    @Deprecated
    public void setColor(Integer color) {
        DataComponentPatch dyedColor = DataComponentPatch.builder()
                .set(DataComponents.DYED_COLOR, new DyedItemColor(color, false))
                .build();
        components.add(dyedColor);
        if (!(this.itemType.equals(Material.LEATHER_HORSE_ARMOR)))
            this.setColorable();
    }

    @Deprecated
    public void setColorable() {
        DataComponentPatch itemModelComponent = DataComponentPatch.builder()
                .set(DataComponents.ITEM_MODEL, ResourceLocation.tryBuild(ResourceLocation.DEFAULT_NAMESPACE, itemType.toString().toLowerCase()))
                .build();
        if (!(itemType.equals(Material.LEATHER_HORSE_ARMOR)))
            components.add(itemModelComponent);
        itemType = Material.LEATHER_HORSE_ARMOR;

    }

    public void addLore(String... lores) {
        Arrays.stream(lores).forEach(b -> {
            fullLore.add(miniMessage.deserialize(b));
        });
    }

    protected void addStat(NamespacedKey statKey, Integer value) {
        String lorePart = category.equals("tool") ? "в ведущей руке" : "экипировано";
        if (value > 0) {
            this.stats.put(statKey, value);
            if (preStatsLore.isEmpty()) preStatsLore.add(miniMessage.deserialize("<gray><italic:false>Когда " + lorePart + ":"));
        }
        //firstMeta.getPersistentDataContainer().set(statKey, PersistentDataType.INTEGER, value);
    }

    public net.minecraft.world.item.ItemStack toItemStack() {
        ItemStack createdItem = new ItemStack(itemType);
        net.minecraft.world.item.ItemStack createdItemNMS = CraftItemStack.asNMSCopy(createdItem);

        //в начале пишется тег предмета
        if (!itemTag.equals("none")) {
            fullLore.addFirst(miniMessage.deserialize("<dark_gray> #" + itemTag));
        }

        //фулл лор собирается тут и создаётся компонент
        ItemLore finallore = new ItemLore(combineLore(fullLore, stats, preStatsLore));
        DataComponentPatch loreComponent = DataComponentPatch.builder()
                .set(DataComponents.LORE, finallore)
                .build();
        DataComponentPatch nameComponent = DataComponentPatch.builder()
                .set(DataComponents.CUSTOM_NAME, convertToNMSComponent(miniMessage.deserialize(displayName)))
                .build();

        //добавление всех компонентов, которые были
        for (DataComponentPatch dataComponentPatch : components) {
            createdItemNMS.applyComponents(dataComponentPatch);
        }
        createdItemNMS.remove(DataComponents.ATTRIBUTE_MODIFIERS);

        createdItemNMS.applyComponents(loreComponent);
        createdItemNMS.applyComponents(nameComponent);
        createdItemNMS.applyComponents(combineCustomData());
        return createdItemNMS;
    }
    //объединение всего лора в один
    protected List<net.minecraft.network.chat.Component> combineLore(List<Component> fullLore, HashMap<NamespacedKey, Integer> stats, List<Component> preStatsLore) {
        List<net.minecraft.network.chat.Component> itemLore = new ArrayList<>();

        for (Component fullLoreComponent : fullLore)
            itemLore.add(convertToNMSComponent(fullLoreComponent));

        //itemLore.addFirst(miniMessage.deserialize("<dark_gray> #cookieclicker"));
        if (stats.isEmpty() && preStatsLore.isEmpty()) return itemLore;
        itemLore.add(net.minecraft.network.chat.Component.empty());

        for (Component component : preStatsLore) {
            itemLore.add(convertToNMSComponent(component));
        }
        //если есть статы в предмете, то добавление их в лор, чтобы были видны
        for (Map.Entry<NamespacedKey, Integer> a : stats.entrySet()) {
            StringBuilder statforLore = new StringBuilder("<blue><italic:false>+");
            double statValue = (double) a.getValue();
            statforLore.append(statValue == Math.floor(statValue) ? String.format("%d", (int) statValue) : statValue);
            switch (a.getKey().getKey()) {
                case "ff" -> statforLore.append(" Удача фермера");
                case "dmg" -> statforLore.append(" Урон");
                case "mf" -> statforLore.append(" Удача шахтёра");
            }
            itemLore.add(convertToNMSComponent(miniMessage.deserialize(statforLore.toString())));
        }
        return itemLore;
    }

    private DataComponentPatch combineCustomData() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putString("item_tag", itemTag);
        compoundTag.putString("category", category);
        if (!ability.equals("none")) {
            compoundTag.putString("ability", ability);
        }

        for (Map.Entry<NamespacedKey, Integer> a : stats.entrySet()) {
            compoundTag.putInt(a.getKey().getKey(), a.getValue());
        }

        CompoundTag finaCompoundTag = new CompoundTag();
        finaCompoundTag.put("cookies", compoundTag);
        CustomData customData = CustomData.of(finaCompoundTag);
        return DataComponentPatch.builder().set(DataComponents.CUSTOM_DATA, customData).build();
    }
}
