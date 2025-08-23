package net.flectone.cookieclicker.inventories;

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.entities.playerdata.ServerCookiePlayer;
import net.flectone.cookieclicker.gameplay.cookiepart.StatisticDisplay;
import net.flectone.cookieclicker.inventories.containers.ClickerContainer;
import net.flectone.cookieclicker.inventories.containers.MenuContainer;
import net.flectone.cookieclicker.items.ItemsRegistry;
import net.flectone.cookieclicker.items.RecipesRegistry;
import net.flectone.cookieclicker.items.itemstacks.CommonCookieItem;
import net.flectone.cookieclicker.items.itemstacks.base.CookieItemStack;
import net.flectone.cookieclicker.items.itemstacks.base.data.ItemTag;
import net.flectone.cookieclicker.items.recipe.CustomRecipe;
import net.flectone.cookieclicker.utility.data.Pair;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.LinkedHashSet;
import java.util.List;

@Singleton
public class MainMenu {

    private final ItemsRegistry loadedItems;
    private final ContainerManager containerManager;
    private final RecipesRegistry recipes;
    private final StatisticDisplay statisticDisplay;

    @Inject
    public MainMenu(ContainerManager containerManager, ItemsRegistry loadedItems, RecipesRegistry recipes,
                    StatisticDisplay statisticDisplay) {
        this.containerManager = containerManager;
        this.loadedItems = loadedItems;
        this.recipes = recipes;
        this.statisticDisplay = statisticDisplay;
    }

    private ItemStack getRecipeButton() {
        CommonCookieItem recipe = new CommonCookieItem(Items.KNOWLEDGE_BOOK, ItemTag.EMPTY,
                "<gradient:#01e14f:#30a257><italic:false>Посмотреть все рецепты");

        return recipe.toMinecraftStack();
    }

    private ItemStack getCreativeButton() {
        CommonCookieItem allItems = new CommonCookieItem(Items.BOOK, ItemTag.EMPTY,
                "<gradient:#ffc900:#f3e736:#f7d760:#e1b926:#f3e736:#ffc900><italic:false>Посмотреть все предметы");

        return allItems.toMinecraftStack();
    }

    private ItemStack getStatButton(ServerCookiePlayer serverCookiePlayer) {
        CommonCookieItem stats = new CommonCookieItem(Items.WRITABLE_BOOK, ItemTag.EMPTY,
                "<gradient:#e5fffe:#e7f0ef><italic:false>Статистика игрока");

        statisticDisplay.getStatsAsList(serverCookiePlayer).forEach(stats::addLore);
        return stats.toMinecraftStack();
    }

    public void openMainMenu(ServerCookiePlayer serverCookiePlayer) {
        MenuContainer menu = new MenuContainer(ClickerContainer.generateId(), 2,
                "main");

        menu.setItem(11, getRecipeButton());
        menu.setItem(13, getStatButton(serverCookiePlayer));
        menu.setItem(15, getCreativeButton());

        menu.setAction(11, (player, clickType) -> openAllRecipes(player));
        menu.setDefaultAction(containerManager::cancelClick);
        menu.setAction(15, (player, type) -> openAllItems(player));

        containerManager.openContainer(serverCookiePlayer, menu);
    }

    public void openAllItems(ServerCookiePlayer serverCookiePlayer) {
        MenuContainer allItemsScreen = new MenuContainer(ClickerContainer.generateId(), 4,
                "all_items");

        int slot = 0;
        for (CookieItemStack cookieItem : loadedItems.allItemsRaw()) {
            ItemStack item = cookieItem.toMinecraftStack();

            allItemsScreen.setItem(slot, item);
            allItemsScreen.setAction(slot, (player, clickType) -> {
                containerManager.cancelClick(serverCookiePlayer);
                if (player.getPlayer().isCreative()) {
                    int amount = (clickType == WrapperPlayClientClickWindow.WindowClickType.QUICK_MOVE) && item.getMaxStackSize() > 1
                            ? item.getMaxStackSize()
                            : 1;
                    player.getPlayer().addItem(item.copyWithCount(amount));
                }
            });

            slot++;
        }

        containerManager.openContainer(serverCookiePlayer, allItemsScreen);
    }

    public void openAllRecipes(ServerCookiePlayer serverCookiePlayer) {
        MenuContainer recipesWindow = new MenuContainer(ClickerContainer.generateId(), 4,
                "all_recipes");

        int slot = 0;
        for (CustomRecipe recipe : recipes.getAllRecipes().values()) {
            recipesWindow.setItem(slot, loadedItems.get(recipe.getResultTag()));
            recipesWindow.setAction(slot, (player, clickType) -> openRecipe(player, recipe));
            slot++;
        }

        containerManager.openContainer(serverCookiePlayer, recipesWindow);
    }

    public void openRecipe(ServerCookiePlayer serverCookiePlayer, CustomRecipe recipe) {
        MenuContainer singleRecipe = new MenuContainer(ClickerContainer.generateId(), 2, "recipe");

        int slot = 0;
        for (Pair<ItemTag, Integer> ingredient : recipe.getAllIngredients()) {
            ItemStack item = loadedItems.get(ingredient.left());
            item.setCount(ingredient.right());

            singleRecipe.setItem(slot, item);
            slot++;
            if (slot == 3 || slot == 12)
                slot += 6;
        }
        ItemStack fillerItem = new ItemStack(Items.WHITE_STAINED_GLASS_PANE);
        fillerItem.set(DataComponents.TOOLTIP_DISPLAY, new TooltipDisplay(true, new LinkedHashSet<>()));
        ItemStack closeWindowItem = new ItemStack(Items.RED_STAINED_GLASS_PANE);
        closeWindowItem.set(DataComponents.TOOLTIP_DISPLAY, new TooltipDisplay(true, new LinkedHashSet<>()));

        for (int i : List.of(3, 12, 21)) {
            singleRecipe.setItem(i, fillerItem);
        }
        singleRecipe.setItem(13, loadedItems.get(recipe.getResultTag()));
        singleRecipe.setItem(8, closeWindowItem);
        singleRecipe.setAction(8, (player, clickType) -> openAllRecipes(player));

        singleRecipe.setDefaultAction(containerManager::cancelClick);

        containerManager.openContainer(serverCookiePlayer, singleRecipe);
    }
}
