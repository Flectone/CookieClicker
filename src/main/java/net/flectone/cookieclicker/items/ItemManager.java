package net.flectone.cookieclicker.items;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.flectone.cookieclicker.utility.CCobjects.Items.ClickerItems;
import net.flectone.cookieclicker.utility.CCobjects.Items.EquipmentItem;
import net.flectone.cookieclicker.utility.CCobjects.Items.HoeItem;
import net.flectone.cookieclicker.utility.CCobjects.Items.NormalItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.equipment.EquipmentAssets;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

@Singleton
public class ItemManager {
    //LinkedHashMap использую, потому что тут предметы добавляются в мапу в таком же порядке,
    //в каком они написаны в коде, в игре проще понять, где какой предмет
    private final LinkedHashMap<String, ItemStack> items = new LinkedHashMap<>();
    //public final HashMap<String, ItemStack> items = new HashMap<>();

    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final Registry<Enchantment> enchantmentRegistry = RegistryAccess
            .registryAccess()
            .getRegistry(RegistryKey.ENCHANTMENT);

    @Inject
    public ItemManager() {
    }

    public void load() {
        // Base Cookie
        // +
        NormalItem basicCookie = new NormalItem(Material.COOKIE,
                "<gradient:#ff8009:#ffdd09><italic:false>Печенье",
                "cookie",
                99);

        items.put("cookie", basicCookie.toItemStack());

        // Compressed Cookie
        // +
        NormalItem enchCookie = new NormalItem(Material.COOKIE,
                "<gradient:#992e7a:#ff8009:#992e7a><italic:false>Зачарованное печенье",
                "ench_cookie",
                64);
        enchCookie.makeEnchGlint();
        enchCookie.setColor(16443647);
        enchCookie.setEatable();
        items.put("ench_cookie", enchCookie.toItemStack());

        // Enchanted Book(Cookie Boost)
        // a
        ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
        Optional<net.minecraft.core.Registry<net.minecraft.world.item.enchantment.Enchantment>> enchantmentRegistry =
                MinecraftServer.getServer().registryAccess().lookup(Registries.ENCHANTMENT);

        enchantmentRegistry.ifPresent(enchantments -> enchantments.asHolderIdMap().forEach(b -> {
            if (b.value().description().getString().equals("Cookie Boost")) {
                ItemEnchantments.Mutable itemEnchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
                itemEnchantments.set(b, 1);

                DataComponentPatch storedEnchantsComponent = DataComponentPatch.builder()
                        .set(DataComponents.STORED_ENCHANTMENTS, itemEnchantments.toImmutable())
                        .build();
                book.applyComponents(storedEnchantsComponent);
            }
        }));
        DataComponentPatch lore = DataComponentPatch.builder()
                .set(DataComponents.LORE, new ItemLore(List.of(net.minecraft.network.chat.Component.literal("Этот чар повышает количество печенья!").withColor(11119017),
                        net.minecraft.network.chat.Component.literal("(Опыт можно получить, если съесть").withColor(11119017),
                        net.minecraft.network.chat.Component.literal("зачарованное печенье)").withColor(11119017))))
                .build();
        book.applyComponents(lore);

        items.put("book_boost1", book);

        // Wooden hoe (+1)
        // +
        HoeItem wood_hoe = new HoeItem(Material.WOODEN_HOE,
                "<white><italic:false>Деревянная мотыга",
                "wood_hoe",
                1);
        items.put("wood_hoe", wood_hoe.toItemStack());

        // Stone hoe (+2)
        // +
        HoeItem stone_hoe = new HoeItem(Material.STONE_HOE,
                "<gradient:#535351:#878781><italic:false>Каменная <white>мотыга",
                "stone_hoe",
                2);
        items.put("stone_hoe", stone_hoe.toItemStack());

        // Destroyer of Cookies
        // +
        HoeItem destroyer_hoe = new HoeItem(Material.NETHERITE_HOE,
                "<#f79459><italic:false>Уничтожитель печенья",
                "destroyer",
                0);
        destroyer_hoe.setAbility("destroyer",
                "Разделяет печенье на какао-бобы",
                "и пшеницу.");
        items.put("destroyer", destroyer_hoe.toItemStack());

        // Rose bush
        // +
        HoeItem rose_bush = new HoeItem(Material.ROSE_BUSH,
                "<gradient:#e16953:#f7b8ac><italic:false>Розовый (или ягодный) <dark_green>куст",
                "rose_bush",
                0);
        rose_bush.setAbility("rose_bush",
                "C небольшой вероятностью создаёт",
                "ягоды вокруг игрока.");
        rose_bush.setColor(16722731);
        items.put("rose_bush", rose_bush.toItemStack());

        // Epic hoe
        // +
        HoeItem epic_hoe = new HoeItem(Material.DIAMOND_HOE,
                "<gradient:#9e29ff:#c44dff><italic:false>Эпическая мотыга",
                "epic_hoe",
                25);
        //epic_hoe.setColor(10354943);
        epic_hoe.addLore("<gray> С помощью частиц вокруг создаёт печенье");
        epic_hoe.setAbility("transform", "С небольшой вероятность создаёт альтернативный", "предмет. Тип предмета также зависит от", "предмета в левой руке.");
        items.put("epic_hoe", epic_hoe.toItemStack());

        // Legendary Hoe
        // +
        HoeItem leg_hoe = new HoeItem(Material.GOLDEN_HOE,
                "<gradient:#790dbf:#ffae00:#ffae00:#d27f16:#d27f16:#790dbf><italic:false>Легендарная мотыга",
                "leg_hoe",
                10);
        leg_hoe.setAbility("infinity", "<yellow>Золотая версия</yellow>:",
                " Каждые 100 кликов улучшается,",
                " до бесконечности",
                "<gray>Железная версия</gray>:",
                " Создаёт альтернативные версии предметов.");
        leg_hoe.setColor(16758272);
        items.put("leg_hoe", leg_hoe.toItemStack());

        // Cocoa beans (cocoa_beans)
        // +
        NormalItem cocoa = new NormalItem(Material.COCOA_BEANS,
                "<gradient:#964b00:#c17529><italic:false>Какао-бобы",
                "cocoa_beans",
                99);
        items.put("cocoa_beans", cocoa.toItemStack());

        // Wheat
        // +
        NormalItem wheat = new NormalItem(Material.WHEAT,
                "<gradient:#d5a51b:#ffe506><italic:false>Пшеница",
                "wheat",
                99);
        items.put("wheat", wheat.toItemStack());

        // Enchanted Wheat
        // +
        NormalItem ench_wheat = new NormalItem(Material.WHEAT,
                "<gradient:#992e7a:#d5a51b:#ffe506:#992e7a><italic:false>Зачарованная пшеница",
                "ench_wheat",
                99);
        ench_wheat.setColorable();
        ench_wheat.makeEnchGlint();
        items.put("ench_wheat", ench_wheat.toItemStack());

        // Bread
        // +
        NormalItem bread = new NormalItem(Material.BREAD,
                "<gradient:#a28e63:#c9ac6f><italic:false>Хлеб",
                "bread",
                52);
        bread.setEatable();
        items.put("bread", bread.toItemStack());

        // Compressed cocoa beans (ench_cocoa)
        // +
        NormalItem ench_beans = new NormalItem(Material.COCOA_BEANS,
                "<gradient:#992e7a:#964b00:#c17529:#992e7a><italic:false>Зачарованные какао-бобы",
                "ench_cocoa",
                64);
        ench_beans.addLore("<gray> Если держать в левой руке, будет</gray>",
                "<gray>перекрашивать печенье в очень тёмный цвет.",
                "<gray> Можно переплавить в шоколад");
        ench_beans.makeEnchGlint();
        items.put("ench_cocoa", ench_beans.toItemStack());

        // Baguette (baguette)
        // +
        NormalItem baguette = new NormalItem(Material.BREAD,
                "<gradient:#ac7604:#f7eb91><italic:false>Багет",
                "baguette",
                64);
        baguette.makeEnchGlint();
        baguette.addLore("<gray> Используется для создания</gray>", "<gray>финальной мотыги.");
        items.put("baguette", baguette.toItemStack());

        // Печенье или уголь? (coal)
        // +
        NormalItem coal = new NormalItem(Material.COAL,
                "<gradient:#444444:#e78a1b><italic:false>Печенье в шоколаде",
                "coal",
                64);
        coal.addLore("<gray> Сложно понять, это печенье</gray>", "<gray>или уголь.");
        items.put("coal", coal.toItemStack());

        // Chocolate
        // +
        NormalItem chocolate = new NormalItem(Material.DARK_OAK_TRAPDOOR,
                "<gradient:#884e0a:#b36810><italic:false>Шоколад",
                "chocolate",
                64);
        chocolate.addLore("<gray> Используется для создания</gray>", "<gray>финальной мотыги.");
        items.put("chocolate", chocolate.toItemStack());

        // Chocolate stick
        //items.put("stick", placeholder("Палка (из шоколада)", "stick"));

        // Sweet Berries
        // +
        NormalItem berry = new NormalItem(Material.SWEET_BERRIES,
                "<gradient:#c80b47:#ff286c><italic:false>Сладкие ягоды",
                "berries",
                64);
        items.put("berries", berry.toItemStack());

        // Glow Berries (sweet_berries alt)
        // +
        NormalItem glow_berry = new NormalItem(Material.GLOW_BERRIES,
                "<gradient:#ffb128:#ffde28:#efab0f:#f7990a:#ffb748><italic:false>Светящиеся ягоды",
                "glow_berries",
                64);
        glow_berry.addLore("<gray> Создано эпической мотыгой",
                "<gray> Альтернатива сладким ягодам");
        items.put("glow_berries", glow_berry.toItemStack());

        // Pumpkin pie (cookie alt)
        // +
        NormalItem pie = new NormalItem(Material.PUMPKIN_PIE,
                "<gradient:#ffd96c:#ff8700:#ffd96c><italic:false>Тыквенный пирог",
                "pie",
                64);
        pie.addLore("<gray> Создано эпической мотыгой",
                "<gray> Альтернатива печенью");
        items.put("pie", pie.toItemStack());

        // Pumpkin
        // +
        NormalItem pumpkin = new NormalItem(Material.PUMPKIN,
                "<gradient:#ff8f00:#ff7000><italic:false>Тыква",
                "pumpkin",
                64);
        pumpkin.addLore("<gray> Создано эпической мотыгой",
                "<gray> Альтернатива пшенице и какао-бобам");
        pumpkin.setColorable();
        items.put("pumpkin", pumpkin.toItemStack());

        //exp
        EquipmentItem fhelmet = new EquipmentItem(Material.LEATHER_HELMET,
                EquipmentSlot.HEAD,
                EquipmentAssets.LEATHER,
                "<gradient:#f5bb37:#fcda8c:#f99300:#b37113><italic:false>Шляпа фермера",
                "fHelmet");
        fhelmet.setFarmingFortune(100);
        fhelmet.addLore("unused");
        fhelmet.setDyedColor(16493613);

        EquipmentItem fChest = new EquipmentItem(fhelmet, Material.LEATHER_CHESTPLATE, EquipmentSlot.CHEST,
                "<gradient:#f5bb37:#fcda8c:#f99300:#b37113><italic:false>Нагрудник фермера", "fChest");
        EquipmentItem fLegs = new EquipmentItem(fhelmet, Material.LEATHER_LEGGINGS, EquipmentSlot.LEGS,
                "<gradient:#f5bb37:#fcda8c:#f99300:#b37113><italic:false>Штаны фермера", "fLegs");
        EquipmentItem fBoots = new EquipmentItem(fhelmet, Material.LEATHER_BOOTS, EquipmentSlot.FEET,
                "<gradient:#f5bb37:#fcda8c:#f99300:#b37113><italic:false>Ботинки фермера", "fBoots");

        items.put("fHelmet", fhelmet.toItemStack());
        items.put("fChest", fChest.toItemStack());
        items.put("fLegs", fLegs.toItemStack());
        items.put("fBoots", fBoots.toItemStack());

        // Healing melon
        items.put("heal_melon", placeholder("[combat] Healing melon", "heal_melon"));

        // Pickaxe
        items.put("miningPart_pickaxe", placeholder("[mining] Кирка", "miningPart_pickaxe"));

        // Cookie part final item
        items.put("cake", placeholder("[cookie] Тортик", "cake"));



    }
    @Deprecated
    public org.bukkit.inventory.ItemStack get(String str) {
        return CraftItemStack.asBukkitCopy(items.get(str).copy());
    }
    public ItemStack getNMS(String str) {
        return items.get(str).copy();
    }
    public Collection<ItemStack> allItems() {
        return items.values();
    }

    @Deprecated
    private ItemStack placeholder(String name, String persistentData) {
        org.bukkit.inventory.ItemStack pholder = new org.bukkit.inventory.ItemStack(Material.BARRIER);
        ItemMeta meta = pholder.getItemMeta();
        meta.displayName(MiniMessage.miniMessage().deserialize("<gradient:#a50404:#f34b4b><italic:false>Предмет"));
        meta.lore(List.of(Component.text(name), Component.text(persistentData)));
        meta.getPersistentDataContainer().set(ClickerItems.itemTagKey, PersistentDataType.STRING, persistentData);
        pholder.setItemMeta(meta);
        return CraftItemStack.asNMSCopy(pholder);
    }
}
