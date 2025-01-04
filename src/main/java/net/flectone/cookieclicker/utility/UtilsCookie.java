package net.flectone.cookieclicker.utility;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.color.Color;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.protocol.particle.Particle;
import com.github.retrooper.packetevents.protocol.particle.data.ParticleTrailData;
import com.github.retrooper.packetevents.protocol.particle.type.ParticleType;
import com.github.retrooper.packetevents.protocol.particle.type.ParticleTypes;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.sound.Sound;
import com.github.retrooper.packetevents.protocol.sound.SoundCategory;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPickItem;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.flectone.cookieclicker.CookieClicker;
import net.flectone.cookieclicker.items.ItemManager;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.core.Holder;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Items;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;
import java.util.*;
import java.util.List;

@Singleton
public class UtilsCookie {
    final NamespacedKey fortuneKey = new NamespacedKey("cc2", "ff");
    private final ItemManager manager;
    private final ItemTagsUtility itemTagsUtility;
    @Inject
    public UtilsCookie (ItemManager manager, ItemTagsUtility itemTagsUtility) {
        this.manager = manager;
        this.itemTagsUtility = itemTagsUtility;
    }
    @Deprecated
    public Object getPDCValue (ItemStack item, NamespacedKey key, PersistentDataType<?, ?> dtp) {
        if (item.getItemMeta() == null) return null;
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        if (!(container.has(key, dtp))) return null;
        return container.get(key, dtp);
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
        int ff = 0;
        PlayerInventory inv = player.getInventory();
        for (int i = 36; i < 40; i++) {
            if (inv.getItem(i) != null) ff += getFullFortune(inv.getItem(i));
        }

        ff += getFullFortune(inv.getItemInMainHand());
        return ff;
    }
    public Integer extractFortune(net.minecraft.world.entity.player.Player player) {
        int ff = 0;
        Inventory inv = player.getInventory();
        for (int i = 36; i < 40; i++) {
            if (inv.getItem(i).getItem() != Items.AIR) ff += getFullFortune(inv.getItem(i));
        }

        ff += getFullFortune(player.getItemInHand(InteractionHand.MAIN_HAND));
        return ff;
    }

    public void setBaseFortune (ItemStack item, Integer ff) {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (!(container.has(fortuneKey, PersistentDataType.INTEGER))) return;
        meta.getPersistentDataContainer().set(fortuneKey, PersistentDataType.INTEGER, ff);
        item.setItemMeta(meta);

    }
    public void updateStats(ItemStack item) {
        if (item.getType().equals(Material.AIR) || getFullFortune(item) == 0) return;
        List<Component> lores = item.lore();
        Component stat = MiniMessage.miniMessage().deserialize("<blue><italic:false>+" + getFullFortune(item) + " Удача фермера");
        lores = lores == null ? new ArrayList<>() : lores;
        if (lores.isEmpty())
            lores.add(MiniMessage.miniMessage().deserialize("<gray><italic:false>Когда в ведущей руке:"));
        if (itemTagsUtility.getBaseFortune(item) == 0) {
            lores.add(stat);
            setBaseFortune(item, -1);
        }
        else
            lores.set(lores.size()-1, stat);
        item.lore(lores);
    }

    //Тут полностью написано, как выпадают предметы
    public void spawnItemLegacy(Integer finalFortune, Player pl, Location loca) {
        String value = itemTagsUtility.getAbility(pl.getInventory().getItemInMainHand());
        String value2 = value.equals("transform") ? itemTagsUtility.getAbility(pl.getInventory().getItemInOffHand()) : value;
        //альтернативный предмет, если спец. условие выполнено, то этот предмет выпадет
        ItemStack altItem = null;
        List<ItemStack> dropItems = new ArrayList<>();

        //проверка на способность у мотыги
        switch (value2) {
            case "destroyer": //для уничтожителя печенья
                dropItems.add(createItemAmount(manager.get("cocoa_beans"), finalFortune));
                dropItems.add(createItemAmount(manager.get("wheat"), finalFortune));
                altItem = manager.get("pumpkin");
                break;
            case "rose_bush": //для куста роз
                altItem = value.equals(value2) ? manager.get("berries") : manager.get("glow_berries");
            default: //основной предмет, то есть печенье
                if (compare(pl.getInventory().getItemInOffHand(), manager.get("ench_cocoa"))) {
                    dropItems.add(createItemAmount(manager.get("coal"), finalFortune)); //если в левой руке какао-бобы
                    pl.getInventory().getItemInOffHand().setAmount(pl.getInventory().getItemInOffHand().getAmount() - 1);
                }
                else dropItems.add(createItemAmount(manager.get("cookie"), finalFortune));
                altItem = altItem != null ? altItem : manager.get("pie");
        }
        //тут вероятность на спец. предмет
        Random rndB = new Random(System.currentTimeMillis());
        if (rndB.nextInt(1, 100) >= 95 && (value.equals("transform") || value2.equals("rose_bush"))) {
            loca.set(loca.x() + rndB.nextDouble(-2.5, 2.5),
                    loca.y() + rndB.nextDouble(0, 4),
                    loca.z() + rndB.nextDouble(-2.5, 2.5));
            dropItems = List.of(altItem);
        }

        //тут спавн предмета идёт уже
        for (ItemStack i : dropItems)
            pl.getWorld().dropItem(loca, i);
    }

}
