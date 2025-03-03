package net.flectone.cookieclicker.items;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import net.flectone.cookieclicker.inventories.ClickerContainer;
import net.flectone.cookieclicker.utility.CCConversionUtils;
import net.flectone.cookieclicker.utility.CCobjects.CookieTrader;
import net.flectone.cookieclicker.utility.CCobjects.Items.NormalItem;
import net.flectone.cookieclicker.utility.CCobjects.TradeItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Singleton
public class VillagerTrades {
    @Getter
    private final HashMap<String, CookieTrader> allTraders = new HashMap<>();

    private final ItemManager manager;
    private final CCConversionUtils converter;

    @Inject
    public VillagerTrades(ItemManager manager, CCConversionUtils converter) {
        this.manager = manager;
        this.converter = converter;
    }

    public void loadSellingItems() {
        CookieTrader farmer = new CookieTrader("trading_farm");
        farmer.addTrade(new TradeItem("wood_hoe")
                .withPrice("ench_cookie", 10));
        farmer.addTrade(new TradeItem("destroyer")
                .withPrice("ench_cookie", 100));
        farmer.addTrade(new TradeItem("stone_hoe")
                .withPrice("ench_cookie", 30));
        farmer.addTrade(new TradeItem("rose_bush")
                .withPrice("baguette", 60));

        registerTrader(farmer);

        CookieTrader armorer = new CookieTrader("trading_armorer");
        armorer.addTrade(new TradeItem("fHelmet")
                .withPrice("ench_cookie", 450));
        armorer.addTrade(new TradeItem("fChest")
                .withPrice("berries", 300));
        armorer.addTrade(new TradeItem("fLegs")
                .withPrice("baguette", 150));
        armorer.addTrade(new TradeItem("fBoots")
                .withPrice("ench_cocoa", 250));

        registerTrader(armorer);

    }

    public void registerTrader(CookieTrader cookieTrader) {
        allTraders.put(cookieTrader.getTraderType(), cookieTrader);
        Bukkit.getServer().getLogger().info("[CookieClicker] registered new trader "
                + cookieTrader.getTraderType()
                + " with " + cookieTrader.getTraderShop().size() + " trades");
    }

    private ClickerContainer createBasicShop(Integer windowType, String customData) {
        ClickerContainer basicContainer = new ClickerContainer(ClickerContainer.generateId(),
                windowType, customData);
        NormalItem upperItem = new NormalItem(Material.WHITE_STAINED_GLASS_PANE,
                "<gradient:#ffffff:#cccccc><italic:false>Здесь вы можете купить",
                "none", 1);
        upperItem.addLore("<gradient:#ffffff:#cccccc><italic:false>различные вещи");

        basicContainer.setTitle("торговля жесть в шоке все");

        for (int i = 0; i < 9; i++) {
            basicContainer.setItem(i, upperItem.toItemStack());
        }

        int slot = 9;
        for (TradeItem tradeItem : getShopItems(customData)) {
            basicContainer.setItem(slot, addPriceToSellItem(tradeItem));
            slot++;
        }

        return basicContainer;
    }

    private ItemStack addPriceToSellItem(TradeItem tradeItem) {
        ItemStack finalItem = manager.getNMS(tradeItem.getSellingItemTag());
        List<net.minecraft.network.chat.Component> lore = new ArrayList<>();
        ItemLore itemLore = finalItem.getComponents().get(DataComponents.LORE);
        if (itemLore != null) lore.addAll(itemLore.lines());

        Component cost = MiniMessage.miniMessage().deserialize("<#C70039>Стоимость: "
                + tradeItem.getPrice().getValue() + " "
                + manager.getNMS(tradeItem.getPrice().getKey()).getDisplayName().getString());
        lore.addFirst(converter.convertToNMSComponent(cost));

        finalItem.applyComponents(DataComponentPatch.builder()
                .set(DataComponents.LORE, new ItemLore(lore))
                .build()
        );
        return finalItem.copy();
    }

    public ClickerContainer createAnyShop(String type) {
        return createBasicShop(3, type);
    }
    public ClickerContainer createCookieShop() {
        return createBasicShop(3, "trading_farm");
    }
    public ClickerContainer createArmorShop() {
        return createBasicShop(3, "trading_armorer");
    }

    private List<TradeItem> getShopItems(String traderType) {
        if (allTraders.isEmpty() || !allTraders.containsKey(traderType))
            return new ArrayList<>();
        return allTraders.get(traderType).getTraderShop();
    }

    public Integer itemsLength(String traderType) {
        return getShopItems(traderType).size();
    }

    public ItemStack getItem(String traderType, Integer num) {
        return manager.getNMS(getShopItems(traderType).get(num).getSellingItemTag());
    }

    public ItemStack getPriceItem(String traderType, Integer num) {
        return manager.getNMS(getShopItems(traderType).get(num).getPrice().getKey());
    }

    public Integer getPriceCount(String traderType, Integer num) {
        return getShopItems(traderType).get(num).getPrice().getValue();
    }

}
