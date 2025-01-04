package net.flectone.cookieclicker.utility.CCobjects;

import com.mojang.serialization.JavaOps;
import io.papermc.paper.adventure.WrapperAwareSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class ItemBase implements ClickerItems{
    ItemMeta firstMeta;
    String displayName, itemTag, equipType;
    Material itemType;
    List<Component> fullLore = new ArrayList<>(), preStatsLore = new ArrayList<>();
    HashMap<NamespacedKey, Integer> stats = new HashMap<>();
    String ability = "none";
    MiniMessage miniMessage = MiniMessage.miniMessage();


    // на этом держатся все предметы этого плагина
    List<DataComponentPatch> components = new ArrayList<>();

    //конвертация компонентов
    public static net.minecraft.network.chat.Component convertToNMSComponent(net.kyori.adventure.text.Component comp) {
        GsonComponentSerializer ser = net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson();
        ComponentSerializer<Component, Component, net.minecraft.network.chat.Component> sss;
        sss = new WrapperAwareSerializer(() -> MinecraftServer.getServer().registryAccess().createSerializationContext(JavaOps.INSTANCE));
        return sss.serialize(comp);
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
        String lorePart = equipType.equals("tool") ? "в ведущей руке" : "экипировано";
        if (value > 0) {
            this.stats.put(statKey, value);
            if (preStatsLore.isEmpty()) preStatsLore.add(miniMessage.deserialize("<gray><italic:false>Когда " + lorePart + ":"));
        }
        //firstMeta.getPersistentDataContainer().set(statKey, PersistentDataType.INTEGER, value);
    }

    public net.minecraft.world.item.ItemStack toItemStack() {
        ItemStack createdItem = new ItemStack(itemType);

        //в начале пишется тег предмета
        fullLore.addFirst(miniMessage.deserialize("<dark_gray> #" + itemTag));

        //фулл лор собирается тут и создаётся компонент
        ItemLore lore = new ItemLore(combineLore(fullLore, stats, preStatsLore));
        DataComponentPatch loreComponent = DataComponentPatch.builder()
                .set(DataComponents.LORE, lore)
                .build();

        net.minecraft.world.item.ItemStack createdItemNMS = CraftItemStack.asNMSCopy(createdItem);
        //добавление всех компонентов, которые были
        for (DataComponentPatch b : components) {
            createdItemNMS.applyComponents(b);
        }
        createdItemNMS.remove(DataComponents.ATTRIBUTE_MODIFIERS);

        DataComponentPatch name = DataComponentPatch.builder()
                .set(DataComponents.CUSTOM_NAME, convertToNMSComponent(miniMessage.deserialize(displayName)))
                .build();
        createdItemNMS.applyComponents(loreComponent);
        createdItemNMS.applyComponents(name);
        createdItemNMS.applyComponents(combineCustomData());
        return createdItemNMS;
    }
    //объединение всего лора в один
    protected List<net.minecraft.network.chat.Component> combineLore(List<Component> fullLore, HashMap<NamespacedKey, Integer> stats, List<Component> preStatsLore) {
        List<net.minecraft.network.chat.Component> itemLore = new ArrayList<>();
        for (Component a : fullLore)
            itemLore.add(convertToNMSComponent(a));
        //itemLore.addFirst(miniMessage.deserialize("<dark_gray> #cookieclicker"));
        if (stats.isEmpty() && preStatsLore.isEmpty()) return itemLore;
        itemLore.add(net.minecraft.network.chat.Component.empty());
        for (Component b : preStatsLore)
            itemLore.add(convertToNMSComponent(b));

        for (Map.Entry<NamespacedKey, Integer> a : stats.entrySet()) {
            StringBuilder stat = new StringBuilder("<blue><italic:false>+");
            double value = (double) a.getValue();
            stat.append(value == Math.floor(value) ? String.format("%d", (int) value) : value);
            switch (a.getKey().getKey()) {
                case "ff":
                    stat.append(" Удача фермера");
                    break;
                case "dmg":
                    stat.append(" Урон");
                    break;
                case "mf":
                    stat.append(" Удача шахтёра");
                    break;
            }
            itemLore.add(convertToNMSComponent(miniMessage.deserialize(stat.toString())));
        }
        return itemLore;
    }
    private DataComponentPatch combineCustomData() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putString("item_tag", itemTag);
        if (!ability.equals("none")) compoundTag.putString("ability", ability);
        for (Map.Entry<NamespacedKey, Integer> a : stats.entrySet()) {
            compoundTag.putInt(a.getKey().getKey(), a.getValue());

        }

        CompoundTag finalTag = new CompoundTag();
        finalTag.put("cookies", compoundTag);
        CustomData cd = CustomData.of(finalTag);
        return DataComponentPatch.builder().set(DataComponents.CUSTOM_DATA, cd).build();
    }
}
