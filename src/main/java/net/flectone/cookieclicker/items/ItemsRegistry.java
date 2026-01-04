package net.flectone.cookieclicker.items;

import com.google.inject.Singleton;
import net.flectone.cookieclicker.items.attributes.CookieAbility;
import net.flectone.cookieclicker.items.itemstacks.CommonCookieItem;
import net.flectone.cookieclicker.items.itemstacks.CookieEnchantmentBook;
import net.flectone.cookieclicker.items.itemstacks.equipment.EquipmentCookieItem;
import net.flectone.cookieclicker.items.itemstacks.tools.AxeCookieItem;
import net.flectone.cookieclicker.items.itemstacks.tools.HoeCookieItem;
import net.flectone.cookieclicker.items.itemstacks.equipment.BackpackCookieItem;
import net.flectone.cookieclicker.items.itemstacks.base.CookieItemStack;
import net.flectone.cookieclicker.items.itemstacks.base.data.EquipmentData;
import net.flectone.cookieclicker.items.itemstacks.base.data.ItemTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.EquipmentAssets;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

@Singleton
public class ItemsRegistry {
    private final LinkedHashMap<String, CookieItemStack> itemsToLoad = new LinkedHashMap<>();

    private final CommonCookieItem emptyItem = new CommonCookieItem(
            Items.STRUCTURE_VOID,
            ItemTag.EMPTY,
            "Этого предмета не существует"
    );

    private void registerItem(CookieItemStack item) {
        itemsToLoad.put(item.getItemTag().getRealTag(), item);
    }

    public void load(Logger logger) {
        //cookie
        CommonCookieItem cookie = new CommonCookieItem(
                Items.COOKIE, ItemTag.COOKIE,
                "<gradient:#ff8009:#ffdd09><italic:false>Печенье");
        cookie.setAmount(99);
        registerItem(cookie);

        //Enchanted Cookie
        CommonCookieItem enchantedCookie = new CommonCookieItem(
                Items.COOKIE, ItemTag.ENCHANTED_COOKIE,
                "<gradient:#992e7a:#ff8009:#992e7a><italic:false>Зачарованное печенье"
        );
        enchantedCookie.addLore("<gray> Съешьте, чтобы получить опыт");
        enchantedCookie.setAmount(64);
        enchantedCookie.setEnchantmentGlint();
        enchantedCookie.hideItem();
        enchantedCookie.setEatable();
        registerItem(enchantedCookie);

        //Enchantment Cookie Boost
        CookieEnchantmentBook cookieEnchantmentBook = new CookieEnchantmentBook(ItemTag.BOOK_COOKIE_BOOST);
        cookieEnchantmentBook.setStoredEnchantment("Cookie Boost");
        registerItem(cookieEnchantmentBook);

        //Enchantment Mining Boost
        CookieEnchantmentBook miningBoostBook = new CookieEnchantmentBook(ItemTag.BOOK_MINING_BOOST);
        miningBoostBook.setStoredEnchantment("Mining Boost");
        registerItem(miningBoostBook);

        //Enchantment Elemental Sharpness
        CookieEnchantmentBook elementalBook = new CookieEnchantmentBook(ItemTag.BOOK_BLOCK_DAMAGE);
        elementalBook.setStoredEnchantment("Block Damage");
        registerItem(elementalBook);

        //Wood hoe
        HoeCookieItem woodenHoe = new HoeCookieItem(
                Items.WOODEN_HOE, ItemTag.WOODEN_HOE,
                "<white><italic:false>Деревянная мотыга"
        );
        woodenHoe.setFarmingFortune(1);
        registerItem(woodenHoe);

        //Stone hoe
        HoeCookieItem stoneHoe = new HoeCookieItem(
                Items.STONE_HOE, ItemTag.STONE_HOE,
                "<gradient:#535351:#878781><italic:false>Каменная <white>мотыга"
        );
        stoneHoe.setFarmingFortune(2);
        registerItem(stoneHoe);

        //Destroyer
        HoeCookieItem destroyer = new HoeCookieItem(
                Items.NETHERITE_HOE, ItemTag.COOKIE_DESTROYER_HOE,
                "<#f79459><italic:false>Уничтожитель печенья"
        );
        destroyer.setAbility(CookieAbility.DESTROYER);
        registerItem(destroyer);

        //Rose bush
        HoeCookieItem roseBush = new HoeCookieItem(
                Items.ROSE_BUSH, ItemTag.ROSE_BUSH_HOE,
                "<gradient:#e16953:#f7b8ac><italic:false>Розовый (или ягодный) <dark_green>куст"
        );
        roseBush.setAbility(CookieAbility.ROSE_BUSH);
        roseBush.hideItem();
        registerItem(roseBush);

        //Epic hoe
        HoeCookieItem epicHoe = new HoeCookieItem(
                Items.DIAMOND_HOE, ItemTag.EPIC_HOE,
                "<gradient:#9e29ff:#c44dff><italic:false>Эпическая мотыга"
        );
        epicHoe.addLore("<gray> Чем дольше вы кликаете, тем больше печенья получите");
        epicHoe.setAbility(CookieAbility.TRANSFORM);
        epicHoe.setFarmingFortune(25);
        registerItem(epicHoe);

        //Legendary hoe
        HoeCookieItem legHoe = new HoeCookieItem(
                Items.GOLDEN_HOE, ItemTag.LEGENDARY_HOE,
                "<gradient:#790dbf:#ffae00:#ffae00:#d27f16:#d27f16:#790dbf><italic:false>Легендарная мотыга"
        );
        legHoe.addLore("<gray> Работает в двух режимах: <italic:false><yellow>Золотом</yellow><italic:true> и <italic:false><gray>Железном</gray>.",
                "<gray> (Чтобы переключить режим, лкм по воздуху)");
        legHoe.setFarmingFortune(10);
        legHoe.setAbility(CookieAbility.INFINITY_UPGRADE);
        registerItem(legHoe);

        //Cocoa beans
        CommonCookieItem cocoaBeans = new CommonCookieItem(
                Items.COCOA_BEANS, ItemTag.COCOA_BEANS,
                "<gradient:#964b00:#c17529><italic:false>Какао-бобы"
        );
        cocoaBeans.setAmount(99);
        registerItem(cocoaBeans);

        //Enchanted cocoa
        CommonCookieItem enchantedCocoaBeans = new CommonCookieItem(
                Items.COCOA_BEANS, ItemTag.ENCHANTED_COCOA_BEANS,
                "<gradient:#992e7a:#964b00:#c17529:#992e7a><italic:false>Зачарованные какао-бобы"
        );
        enchantedCocoaBeans.setEnchantmentGlint();
        enchantedCocoaBeans.addLore("<gray> Если держать в левой руке, то</gray>",
                "<gray>с помощью <color:#f79459><italic:false>Уничтожителя печенья</color><italic:true>",
                "<gray>можно создать шоколад");
        registerItem(enchantedCocoaBeans);

        //Wheat
        CommonCookieItem wheat = new CommonCookieItem(
                Items.WHEAT, ItemTag.WHEAT,
                "<gradient:#d5a51b:#ffe506><italic:false>Пшеница"
        );
        wheat.hideItem();
        wheat.setAmount(99);
        registerItem(wheat);

        //Enchanted wheat
        CommonCookieItem enchantedWheat = new CommonCookieItem(
                Items.WHEAT, ItemTag.ENCHANTED_WHEAT,
                "<gradient:#992e7a:#d5a51b:#ffe506:#992e7a><italic:false>Зачарованная пшеница"
        );
        enchantedWheat.addLore("<gray> Можно сделать хлеб");
        enchantedWheat.hideItem();
        enchantedWheat.setAmount(99);
        enchantedWheat.setEnchantmentGlint();
        registerItem(enchantedWheat);

        //Bread
        CommonCookieItem bread = new CommonCookieItem(
                Items.BREAD, ItemTag.BREAD,
                "<gradient:#a28e63:#c9ac6f><italic:false>Хлеб"
        );
        bread.setAmount(52);
        registerItem(bread);

        //Baguette
        CommonCookieItem baguette = new CommonCookieItem(
                Items.BREAD, ItemTag.BAGUETTE,
                "<gradient:#ac7604:#f7eb91><italic:false>Багет"
        );
        baguette.setEnchantmentGlint();
        baguette.addLore("<gray> Используется для создания</gray>", "<gray>финальной мотыги.");
        registerItem(baguette);

        //Chocolate
        CommonCookieItem chocolate = new CommonCookieItem(
                Items.DARK_OAK_TRAPDOOR, ItemTag.CHOCOLATE,
                "<gradient:#884e0a:#b36810><italic:false>Шоколад"
        );
        chocolate.hideItem();
        chocolate.setAmount(64);
        chocolate.addLore("<gray> Используется для создания</gray>", "<gray>предмета <black>???</black>.");
        registerItem(chocolate);

        //Sweet berries
        CommonCookieItem berries = new CommonCookieItem(
                Items.SWEET_BERRIES,
                ItemTag.SWEET_BERRIES,
                "<gradient:#c80b47:#ff286c><italic:false>Сладкие ягоды"
        );
        berries.addLore("<gray> Можно получить с помощью Розового куста</gray>", "<gray> (см. жителя фермера)</gray>");
        registerItem(berries);

        //Farmer Armor base
        EquipmentData equipmentData = new EquipmentData(EquipmentAssets.LEATHER);
        equipmentData.setColor(16493613);
        equipmentData.addLore("<gray> Можно улучшать с помощью", "<gray>тортиков на наковальне");

        //Farmer Armor
        EquipmentCookieItem farmHelmet = new EquipmentCookieItem(
                Items.LEATHER_HELMET, ItemTag.FARMER_HELMET,
                "<gradient:#f5bb37:#fcda8c:#f99300:#b37113><italic:false>Шляпа фермера",
                equipmentData, EquipmentSlot.HEAD
        );
        farmHelmet.setFarmingFortune(10);
        registerItem(farmHelmet);

        EquipmentCookieItem farmChest = new EquipmentCookieItem(
                Items.LEATHER_CHESTPLATE, ItemTag.FARMER_CHESTPLATE,
                "<gradient:#f5bb37:#fcda8c:#f99300:#b37113><italic:false>Нагрудник фермера",
                equipmentData, EquipmentSlot.CHEST
        );
        farmChest.setFarmingFortune(40);
        registerItem(farmChest);

        EquipmentCookieItem farmLeggings = new EquipmentCookieItem(
                Items.LEATHER_LEGGINGS, ItemTag.FARMER_LEGGINGS,
                "<gradient:#f5bb37:#fcda8c:#f99300:#b37113><italic:false>Штаны фермера",
                equipmentData, EquipmentSlot.LEGS
        );
        farmLeggings.setFarmingFortune(25);
        registerItem(farmLeggings);

        EquipmentCookieItem farmBoots = new EquipmentCookieItem(
                Items.LEATHER_BOOTS, ItemTag.FARMER_BOOTS,
                "<gradient:#f5bb37:#fcda8c:#f99300:#b37113><italic:false>Ботинки фермера",
                equipmentData, EquipmentSlot.FEET
        );
        farmBoots.setFarmingFortune(15);
        registerItem(farmBoots);

        //(test) healing melon
        CommonCookieItem healMelon = new CommonCookieItem(
                Items.GLISTERING_MELON_SLICE,
                ItemTag.HEALING_MELON,
                "<gradient:#ff9b00:#ff2300:#ff9b00><italic:false>Сверкающий ломтик арбуза"
        );
        healMelon.addLore("<gray> Пока что ничего не делает");
        healMelon.setEatable();
        registerItem(healMelon);

        //(test) pickaxe
        CommonCookieItem pickaxe = new CommonCookieItem(
                Items.GOLDEN_PICKAXE,
                ItemTag.PICKAXE,
                "<gradient:#ffbd00:#ffec00><italic:false>Золотая <white>кирка"
        );
        pickaxe.addLore("<gray> Пока что ничего не делает.", "<gray> Скоро тут будет", "<gray> +1 удача шахтёра");
        registerItem(pickaxe);

        //Cake
        CommonCookieItem finalCake = new CommonCookieItem(
                Items.CAKE,
                ItemTag.CAKE_UPGRADE_ITEM,
                "<gradient:#ece7d2:#ecdb95><italic:false>Тортик"
        );
        finalCake.setEnchantmentGlint();
        registerItem(finalCake);

        //Cookie compactor
        CommonCookieItem cookieCompactor = new CommonCookieItem(
                Items.PISTON,
                ItemTag.COOKIE_CRAFTER,
                "<italic:false>Сборщик печенья"
        );
        cookieCompactor.addLore("<gray> Если положить в левую руку, позволяет создавать</gray>", "<gray> блок печенья из 512 чар. печенья.</gray>");
        cookieCompactor.hideItem();
        registerItem(cookieCompactor);

        CommonCookieItem cookieBlock = new CommonCookieItem(
                Items.RESIN_BLOCK,
                ItemTag.BLOCK_OF_COOKIE,
                "<gradient:#992e7a:#c66618:#f57b18:#992e7a><italic:false>Блок зачарованного печенья"
        );
        cookieBlock.setAmount(99);
        cookieBlock.setEnchantmentGlint();
        registerItem(cookieBlock);

        //Glow berries
        CommonCookieItem glowBerries = new CommonCookieItem(
                Items.GLOW_BERRIES,
                ItemTag.GLOW_BERRIES,
                "<gradient:#ffb128:#ffde28:#efab0f:#f7990a:#ffb748><italic:false>Светящиеся ягоды"
        );
        glowBerries.addLore("<gray> Создано эпической мотыгой",
                "<gray> Альтернатива сладким ягодам");
        registerItem(glowBerries);

        //Pumpkin pie
        CommonCookieItem pumpkinPie = new CommonCookieItem(
                Items.PUMPKIN_PIE,
                ItemTag.PUMPKIN_PIE,
                "<gradient:#ffd96c:#ff8700:#ffd96c><italic:false>Тыквенный пирог"
        );
        pumpkinPie.addLore("<gray> Создано эпической мотыгой",
                "<gray> Альтернатива печенью");
        registerItem(pumpkinPie);

        //Pumpkin
        CommonCookieItem pumpkin = new CommonCookieItem(
                Items.PUMPKIN,
                ItemTag.PUMPKIN,
                "<gradient:#ff8f00:#ff7000><italic:false>Тыква"
        );
        pumpkin.hideItem();
        pumpkin.setAmount(64);
        pumpkin.addLore("<gray> Создано эпической мотыгой",
                "<gray> Альтернатива пшенице и какао-бобам");
        registerItem(pumpkin);

        //Backpack 18 slots
        BackpackCookieItem bag18 = new BackpackCookieItem(Items.GREEN_BUNDLE, ItemTag.BAG_18, 18,
                "<gradient:#32a208:#46d610><italic:false>Обычный мешок");
        bag18.hideItem();
        registerItem(bag18);

        //Backpack 45 slots
        BackpackCookieItem bag45 = new BackpackCookieItem(Items.PURPLE_BUNDLE, ItemTag.BAG_45, 45,
                "<gradient:#8315b6:#c21fe7><italic:false>Редкий мешок");
        bag45.hideItem();
        registerItem(bag45);

        // Axe
        AxeCookieItem melonBreaker = new AxeCookieItem(Items.DIAMOND_AXE, ItemTag.MELON_DICER,
                "<italic:false><gradient:#6AB848:#12A503>Разрушитель арбузов</gradient>");
        melonBreaker.addBlocks(Items.MELON);
        melonBreaker.addLore("<gray> Может ломать арбузные блоки");
        melonBreaker.setMiningPower(1);
        melonBreaker.setBlockDamage(1);
        registerItem(melonBreaker);

        // Melon
        CommonCookieItem melon = new CommonCookieItem(Items.MELON_SLICE, ItemTag.MELON,
                "<italic:false><gradient:#6AB848:#FF2323>Лом</gradient><gradient:#FF2323:#FF2323>тик ар</gradient><gradient:#FF2323:#12A503>буза</gradient>");
        melon.hideItem();
        melon.setAmount(99);
        registerItem(melon);

        // Enchanted melon
        CommonCookieItem enchantedMelon = new CommonCookieItem(Items.MELON_SLICE, ItemTag.ENCHANTED_MELON,
                "<italic:false><gradient:#B029F5:#591FE3>Зачар</gradient><gradient:#591FE3:#B029F5>ованный</gradient><gradient:#B029F5:#6AB848> </gradient><gradient:#6AB848:#FF2323>ломт</gradient><gradient:#FF2323:#FF2323>ик арб</gradient><gradient:#FF2323:#12A503>уза</gradient>");
        enchantedMelon.hideItem();
        enchantedMelon.setAmount(64);
        enchantedMelon.setEnchantmentGlint();
        registerItem(enchantedMelon);

        // Melon Block
        CommonCookieItem blockMelon = new CommonCookieItem(Items.MELON, ItemTag.MELON_BLOCK,
                "<italic:false><gradient:#6AB848:#12A503>Арбуз</gradient>");
        registerItem(blockMelon);

        logger.info("loaded items: " + itemsToLoad.size());
    }

    public ItemStack get(ItemTag tag) {
        return itemsToLoad.getOrDefault(tag.getRealTag(), emptyItem).toMinecraftStack().copy();
    }

    public ItemStack get(ItemTag tag, Integer amount) {
        return itemsToLoad.getOrDefault(tag.getRealTag(), emptyItem).toMinecraftStack().copyWithCount(amount);
    }

    public boolean has(String itemTag) {
        return itemsToLoad.containsKey(itemTag);
    }

    public Collection<CookieItemStack> allItemsRaw() {
        return itemsToLoad.values();
    }
}
