package net.flectone.cookieclicker.utility;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mojang.serialization.JavaOps;
import io.papermc.paper.adventure.WrapperAwareSerializer;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.flectone.cookieclicker.items.ItemManager;
import net.flectone.cookieclicker.utility.CCobjects.ClickerItems;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Singleton
public class UtilsCookie {
    private final NamespacedKey fortuneKey = new NamespacedKey("cc2", "ff");
    private final ItemManager manager;
    private final ItemTagsUtility itemTagsUtility;
    @Inject
    public UtilsCookie (ItemManager manager, ItemTagsUtility itemTagsUtility) {
        this.manager = manager;
        this.itemTagsUtility = itemTagsUtility;
    }

    //конвертация компонентов
    public net.minecraft.network.chat.Component convertToNMSComponent(net.kyori.adventure.text.Component component) {
        ComponentSerializer<Component, Component, net.minecraft.network.chat.Component> componentSerializer;
        componentSerializer = new WrapperAwareSerializer(() -> MinecraftServer.getServer().registryAccess().createSerializationContext(JavaOps.INSTANCE));
        return componentSerializer.serialize(component);
    }

    public boolean compare(ItemStack item, ItemStack checkItem) {
        ItemStack checkItem2 = new ItemStack(checkItem.getType());
        checkItem2.setItemMeta(checkItem.getItemMeta());
        checkItem2.setAmount(item.getAmount());
        if (item.equals(checkItem2)) return true;
        return false;
    }
    public boolean compare(net.minecraft.world.item.ItemStack item1, net.minecraft.world.item.ItemStack item2) {
        net.minecraft.world.item.ItemStack checkItem2 = new net.minecraft.world.item.ItemStack(item2.getItem(), item1.getCount());
        checkItem2.applyComponents(item2.getComponents());
        boolean equals = true;
        if (item1.getItem() != item2.getItem()) equals = false;
        for (TypedDataComponent<?> b : item1.getComponents()) {
            if (!b.equals(checkItem2.getComponents().getTyped(b.type()))) {
                equals = false;
                break;

            }
        }
        return equals;
    }
    public ItemStack createItemAmount(ItemStack item, Integer cost) {
        ItemStack priceItem = new ItemStack(item.getType(), cost);
        ItemMeta priceMeta = item.getItemMeta();
        priceItem.setItemMeta(priceMeta);
        return new ItemStack(priceItem);
    }
    public net.minecraft.world.item.ItemStack createItemAmountNMS(net.minecraft.world.item.ItemStack item, Integer cost) {
        net.minecraft.world.item.ItemStack itemWithAmount = new net.minecraft.world.item.ItemStack(item.getItem(), cost);
        itemWithAmount.applyComponents(item.getComponents());
        return itemWithAmount.copy();
    }

    public Integer getFullFortune(ItemStack item) {
        int fortune = Math.max(0, itemTagsUtility.getBaseFortune(item));
        Registry<Enchantment> enchantmentRegistry = RegistryAccess
                .registryAccess()
                .getRegistry(RegistryKey.ENCHANTMENT);
        Enchantment enchantment = enchantmentRegistry.getOrThrow(TypedKey.create(
                RegistryKey.ENCHANTMENT, Key.key("cookie:ccboost"))
        );
        Map<Enchantment, Integer> enchants = item.getEnchantments();
        if (!(item.getType().equals(Material.AIR)) && !(enchants.isEmpty())) {
            fortune += enchants.getOrDefault(enchantment, 0);
        }
        return fortune;
    }
    public Integer getFullFortune(net.minecraft.world.item.ItemStack item) {
        int fortune = Math.max(0, itemTagsUtility.getBaseFortune(item));
        for (Object2IntMap.Entry<Holder<net.minecraft.world.item.enchantment.Enchantment>> i : item.getEnchantments().entrySet()) {
            if (i.getKey().getRegisteredName().equals("cookie:ccboost"))
                fortune += i.getIntValue();
        }
        return fortune;
    }


    public Integer extractFortune(Player player) {
        int farmingFortune = 0;
        PlayerInventory inv = player.getInventory();
        for (int i = 36; i < 40; i++) {
            if (inv.getItem(i) != null) farmingFortune += getFullFortune(inv.getItem(i));
        }

        farmingFortune += getFullFortune(inv.getItemInMainHand());
        return farmingFortune;
    }
    public Integer extractFortune(net.minecraft.world.entity.player.Player player) {
        int farminFortune = 0;
        Inventory inv = player.getInventory();
        for (int i = 36; i < 40; i++) {
            if (inv.getItem(i).getItem() != Items.AIR) farminFortune += getFullFortune(inv.getItem(i));
        }

        farminFortune += getFullFortune(player.getItemInHand(InteractionHand.MAIN_HAND));
        return farminFortune;
    }

    public void updateStats(net.minecraft.world.item.ItemStack item) {
        if (item.getItem().equals(Items.AIR) || getFullFortune(item) == 0) return;
        ItemLore itemLore = item.getComponents().get(DataComponents.LORE);
        List<net.minecraft.network.chat.Component> lores;
        net.minecraft.network.chat.Component stat = convertToNMSComponent(
                MiniMessage.miniMessage().deserialize("<blue><italic:false>+" + getFullFortune(item) + " Удача фермера"));
        lores = itemLore == null ? new ArrayList<>() : itemLore.lines();
        if (lores.isEmpty()) {
            lores.add(convertToNMSComponent(MiniMessage.miniMessage().deserialize("<gray><italic:false>Когда в ведущей руке:")));
        }
        if (itemTagsUtility.getBaseFortune(item) == 0) {
            lores.add(stat);
            itemTagsUtility.setStat(item, ClickerItems.fortuneTag, -1);
        }
        else
            lores.set(lores.size()-1, stat);
        item.applyComponents(DataComponentPatch.builder()
                        .set(DataComponents.LORE, new ItemLore(lores))
                        .build()
        );
    }
}
