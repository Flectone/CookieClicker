package net.flectone.cookieclicker.inventories;

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.cookiePart.EpicHoeUtils;
import net.flectone.cookieclicker.items.CustomRecipe;
import net.flectone.cookieclicker.items.ItemManager;
import net.flectone.cookieclicker.items.Recipes;
import net.flectone.cookieclicker.items.attributes.StatType;
import net.flectone.cookieclicker.items.itemstacks.CommonCookieItem;
import net.flectone.cookieclicker.items.itemstacks.base.CookieItemStack;
import net.flectone.cookieclicker.playerdata.ServerCookiePlayer;
import net.flectone.cookieclicker.utility.Pair;
import net.flectone.cookieclicker.utility.StatsUtils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Collection;
import java.util.List;

@Singleton
public class MainMenu {
    private final ItemManager loadedItems;
    private final ContainerManager containerManager;
    private final StatsUtils statsUtils;
    private final Recipes recipes;
    private final EpicHoeUtils epicHoeUtils;

    @Inject
    public MainMenu(ContainerManager containerManager, ItemManager loadedItems, StatsUtils statsUtils, Recipes recipes,
                    EpicHoeUtils epicHoeUtils) {
        this.containerManager = containerManager;
        this.loadedItems = loadedItems;
        this.statsUtils = statsUtils;
        this.recipes = recipes;
        this.epicHoeUtils = epicHoeUtils;
    }

    public void openMainMenu(ServerCookiePlayer serverCookiePlayer) {
        ClickerContainer menu = new ClickerContainer(ClickerContainer.generateId(), 2,
                "main_menu");

        CommonCookieItem recipe = new CommonCookieItem(Items.KNOWLEDGE_BOOK, "none",
                "<gradient:#01e14f:#30a257><italic:false>Посмотреть все рецепты");
        CommonCookieItem stats = new CommonCookieItem(Items.WRITABLE_BOOK, "none",
                "<gradient:#e5fffe:#e7f0ef><italic:false>Статистика игрока");
        stats.addLore(String.format("<#eee2d2><italic:false>   Уровень: <#f28423>%d", serverCookiePlayer.getLvl()));
        stats.addLore(String.format("<#e7f0ef><italic:false>(До %d-го: <#f7bb86>%d<#e7f0ef>)", serverCookiePlayer.getLvl() + 1, serverCookiePlayer.getRemainingXp()));
        stats.addLore("<#e7f0ef><italic:false>Удача фермера: <#ffc40a>" + statsUtils.extractStat(serverCookiePlayer.getPlayer(), StatType.FARMING_FORTUNE).toString());
        stats.addLore("<#e7f0ef><italic:false>Шанс на бонус: <#ffb652>наверное 3%");
        stats.addLore("<#e7f0ef><italic:false>Уровень заряда: <#7524f1>" + epicHoeUtils.getCharge(serverCookiePlayer.getUuid()));
        stats.addLore(String.format("<#e7f0ef><italic:false>Множитель от заряда: <#9631e1>x%.1f", 1f + (0.5f * epicHoeUtils.getTier(serverCookiePlayer.getUuid()))));
        stats.addLore(String.format("<#e7f0ef><italic:false>Кликов по рамке: <#bd702d>%d", serverCookiePlayer.getIFrameClicks()));
        CommonCookieItem allItems = new CommonCookieItem(Items.BOOK, "none",
                "<gradient:#ffc900:#f3e736:#f7d760:#e1b926:#f3e736:#ffc900><italic:false>Посмотреть все предметы");

        //beta version item
        CommonCookieItem info = new CommonCookieItem(Items.LECTERN, "none",
                "<gradient:#a50404:#f34b4b><italic:false>v2.0-beta3.1");
        info.addLore("<#e7f0ef><italic:false> Это третья тестовая версия плагина!",
                " ",
                "<#e7f0ef><italic:false> -Предметы на земле теперь отображаются нормально",
                "<#e7f0ef><italic:false> -Добавлена статистика: кликов по рамке",
                "<#e7f0ef><italic:false> -Добавлен уровень",
                "<#e7f0ef><italic:false> -Полностью переписаны предметы",
                "",
                "<gray><italic:false> Предметы, созданные до этой версии, теперь не работают.",
                "<gray><italic:false> Но их можно переделать в предмет нового формата",
                "<gray><italic:false> с помощью команды /cookieclicker2 convert",
                "<gray><italic:false> А также теперь некоторые предметы и механики работают",
                "<gray><italic:false> немного иначе."
        );
        menu.setItem(0, info.toMinecraftStack());
        //


        menu.setItem(11, recipe.toMinecraftStack());
        menu.setItem(13, stats.toMinecraftStack());
        menu.setItem(15, allItems.toMinecraftStack());

        containerManager.openContainer(serverCookiePlayer, menu);
    }

    public void openAllItems(ServerCookiePlayer serverCookiePlayer) {
        ClickerContainer allItemsScreen = new ClickerContainer(ClickerContainer.generateId(), 4,
                "all_items");

        int slot = 0;
        for (CookieItemStack cookieItem : loadedItems.allItemsRaw()) {
            allItemsScreen.setItem(slot, cookieItem.toMinecraftStack());
            slot++;
        }

        containerManager.openContainer(serverCookiePlayer, allItemsScreen);
    }

    public void getItemInMenu(Integer slot, ServerCookiePlayer serverCookiePlayer, WrapperPlayClientClickWindow.WindowClickType clickType) {
        Player player = serverCookiePlayer.getPlayer();
        ClickerContainer container = containerManager.getOpenedContainer(player);

        containerManager.cancelClick(serverCookiePlayer, container, slot, clickType);
        if (!player.isCreative())
            return;
        if (slot > loadedItems.allItemsRaw().size() - 1)
            return;

        ItemStack itemToAdd = container.getContainerItems().get(slot).copy();
        itemToAdd.setCount(clickType.equals(WrapperPlayClientClickWindow.WindowClickType.QUICK_MOVE) && itemToAdd.getMaxStackSize() > 1
                ? itemToAdd.getMaxStackSize()
                : 1);

        player.getInventory().add(itemToAdd);
    }

    public void openAllRecipes(ServerCookiePlayer serverCookiePlayer) {
        ClickerContainer recipesWindow = new ClickerContainer(ClickerContainer.generateId(), 4,
                "all_recipes");

        int slot = 0;
        for (CustomRecipe recipe : recipes.getAllRecipes().values()) {
            recipesWindow.setItem(slot, loadedItems.getNMS(recipe.getResultTag()));
            slot++;
        }

        containerManager.openContainer(serverCookiePlayer, recipesWindow);
    }

    public void openRecipe(ServerCookiePlayer serverCookiePlayer, CustomRecipe recipe) {
        ClickerContainer singleRecipe = new ClickerContainer(ClickerContainer.generateId(), 2, "recipe");

        int slot = 0;
        for (Pair<String, Integer> ingredient : recipe.getAllIngredients()) {
            ItemStack item = loadedItems.getNMS(ingredient.left());
            item.setCount(ingredient.right());

            singleRecipe.setItem(slot, item);
            slot++;
            if (slot == 3 || slot == 12)
                slot += 6;
        }
        ItemStack fillerItem = new ItemStack(Items.WHITE_STAINED_GLASS_PANE);
        fillerItem.set(DataComponents.HIDE_TOOLTIP, Unit.INSTANCE);
        ItemStack closeWindowItem = new ItemStack(Items.RED_STAINED_GLASS_PANE);
        closeWindowItem.set(DataComponents.HIDE_TOOLTIP, Unit.INSTANCE);

        for (int i : List.of(3, 12, 21)) {
            singleRecipe.setItem(i, fillerItem);
        }
        singleRecipe.setItem(13, loadedItems.getNMS(recipe.getResultTag()));
        singleRecipe.setItem(8, closeWindowItem);

        containerManager.openContainer(serverCookiePlayer, singleRecipe);
    }

    public void selectRecipe(ServerCookiePlayer serverCookiePlayer, Integer id, WrapperPlayClientClickWindow.WindowClickType clickType) {
        containerManager.cancelClick(serverCookiePlayer, containerManager.getOpenedContainer(serverCookiePlayer), id, clickType);

        Collection<CustomRecipe> allRecipes = recipes.getAllRecipes().values();
        if (id >= allRecipes.size() || id > containerManager.getOpenedContainer(serverCookiePlayer).getContainerItems().size())
            return;
        openRecipe(serverCookiePlayer, (CustomRecipe) allRecipes.toArray()[id]);
    }
}
