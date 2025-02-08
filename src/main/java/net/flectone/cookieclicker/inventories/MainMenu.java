package net.flectone.cookieclicker.inventories;

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.items.CustomRecipe;
import net.flectone.cookieclicker.items.ItemManager;
import net.flectone.cookieclicker.items.Recipes;
import net.flectone.cookieclicker.utility.CCobjects.CookiePlayer;
import net.flectone.cookieclicker.utility.CCobjects.Items.NormalItem;
import net.flectone.cookieclicker.utility.UtilsCookie;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.bukkit.Material;

import java.util.Collection;
import java.util.List;

@Singleton
public class MainMenu {
    private final ItemManager itemManager;
    private final ContainerManager containerManager;
    private final UtilsCookie utilsCookie;
    private final Recipes recipes;

    @Inject
    public MainMenu(ContainerManager containerManager, ItemManager manager, UtilsCookie utilsCookie, Recipes recipes) {
        this.containerManager = containerManager;
        this.itemManager = manager;
        this.utilsCookie = utilsCookie;
        this.recipes = recipes;
    }

    public void openMainMenu(CookiePlayer cookiePlayer) {
        ClickerContainer menu = new ClickerContainer(ClickerContainer.generateId(), 2,
                "main_menu");

        NormalItem recipe = new NormalItem(Material.KNOWLEDGE_BOOK,
                "<gradient:#01e14f:#30a257><italic:false>Посмотреть все рецепты",
                "none", 1);
        NormalItem stats = new NormalItem(Material.WRITABLE_BOOK,
                "<gradient:#e5fffe:#e7f0ef><italic:false>Статистика игрока",
                "none", 1);
        stats.addLore("<#ffc40a><italic:false>Удача фермера: " + utilsCookie.extractFortune(cookiePlayer.getPlayer()).toString());
        stats.addLore("<#ffb652><italic:false>Шанс на бонус: наверное 3%");
        NormalItem allItems = new NormalItem(Material.BOOK,
                "<gradient:#ffc900:#f3e736:#f7d760:#e1b926:#f3e736:#ffc900><italic:false>Посмотреть все предметы",
                "none", 1);

        menu.setItem(11, recipe.toItemStack());
        menu.setItem(13, stats.toItemStack());
        menu.setItem(15, allItems.toItemStack());

        containerManager.openContainer(cookiePlayer, menu);
    }

    public void openAllItems(CookiePlayer cookiePlayer) {
        ClickerContainer allItemsScreen = new ClickerContainer(ClickerContainer.generateId(), 4,
                "all_items");

        int slot = 0;
        for (ItemStack item : itemManager.allItems()) {
            allItemsScreen.setItem(slot, item);
            slot++;
        }

        containerManager.openContainer(cookiePlayer, allItemsScreen);
    }

    public void getItemInMenu(Integer slot, Player player, WrapperPlayClientClickWindow.WindowClickType clickType) {
        ClickerContainer container = containerManager.getOpenedContainer(player);

        containerManager.cancelClick(player, container, slot, clickType);
        if (!player.isCreative())
            return;
        if (slot > itemManager.allItems().size() - 1)
            return;

        ItemStack itemToAdd = container.getContainerItems().get(slot).copy();
        itemToAdd.setCount(clickType.equals(WrapperPlayClientClickWindow.WindowClickType.QUICK_MOVE) && itemToAdd.getMaxStackSize() > 1
                ? itemToAdd.getMaxStackSize()
                : 1);

        player.getInventory().add(itemToAdd);
    }

    public void openAllRecipes(CookiePlayer cookiePlayer) {
        ClickerContainer recipesWindow = new ClickerContainer(ClickerContainer.generateId(), 4,
                "all_recipes");

        int slot = 0;
        for (CustomRecipe recipe : recipes.getAllRecipes().values()) {
            recipesWindow.setItem(slot, recipe.getResult());
            slot++;
        }

        containerManager.openContainer(cookiePlayer, recipesWindow);
    }

    public void openRecipe(CookiePlayer cookiePlayer, CustomRecipe recipe) {
        ClickerContainer singleRecipe = new ClickerContainer(ClickerContainer.generateId(), 2, "recipe");

        int slot = 0;
        for (ItemStack itm : recipe.getAllIngredients()) {
            singleRecipe.setItem(slot, itm);
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
        singleRecipe.setItem(13, recipe.getResult());
        singleRecipe.setItem(8, closeWindowItem);

        containerManager.openContainer(cookiePlayer, singleRecipe);
    }

    public void selectRecipe(CookiePlayer cookiePlayer, Integer id, WrapperPlayClientClickWindow.WindowClickType clickType) {
        containerManager.cancelClick(cookiePlayer.getPlayer(), containerManager.getOpenedContainer(cookiePlayer), id, clickType);

        Collection<CustomRecipe> allRecipes = recipes.getAllRecipes().values();
        if (id >= allRecipes.size() || id > containerManager.getOpenedContainer(cookiePlayer).getContainerItems().size())
            return;
        openRecipe(cookiePlayer, (CustomRecipe) allRecipes.toArray()[id]);
    }
}
