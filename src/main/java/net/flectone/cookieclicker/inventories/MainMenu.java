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
import net.flectone.cookieclicker.items.attributes.ToolType;
import net.flectone.cookieclicker.items.itemstacks.CommonCookieItem;
import net.flectone.cookieclicker.items.itemstacks.base.data.ItemTag;
import net.flectone.cookieclicker.items.recipe.CustomRecipe;
import net.flectone.cookieclicker.utility.config.ItemsDescription;
import net.flectone.cookieclicker.utility.data.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ClientboundShowDialogPacket;
import net.minecraft.server.dialog.*;
import net.minecraft.server.dialog.body.DialogBody;
import net.minecraft.server.dialog.body.ItemBody;
import net.minecraft.server.dialog.body.PlainMessage;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.*;
import java.util.function.BiConsumer;

@Singleton
public class MainMenu {

    private final ItemsRegistry loadedItems;
    private final ContainerManager containerManager;
    private final RecipesRegistry recipes;
    private final StatisticDisplay statisticDisplay;

    private final ItemsDescription itemsDescription;

    @Inject
    public MainMenu(ContainerManager containerManager, ItemsRegistry loadedItems, RecipesRegistry recipes,
                    StatisticDisplay statisticDisplay, ItemsDescription itemsDescription) {
        this.containerManager = containerManager;
        this.loadedItems = loadedItems;
        this.recipes = recipes;
        this.statisticDisplay = statisticDisplay;
        this.itemsDescription = itemsDescription;
    }

    public void openMainMenu(ServerCookiePlayer serverCookiePlayer) {
        MenuContainer menu = new MenuContainer(ClickerContainer.generateId(), 2,
                "main");

        menu.setItem(11, getRecipeButton());
        menu.setItem(13, getStatButton(serverCookiePlayer));
        menu.setItem(15, getCreativeButton());

        menu.setAction(11, (player, clickType) -> openAllRecipes(player));
        menu.setDefaultAction(containerManager::cancelClick);
        menu.setAction(15, (player, type) -> openCategorySelector(player));

        containerManager.openContainer(serverCookiePlayer, menu);
    }

    private void openCategorySelector(ServerCookiePlayer serverCookiePlayer) {
        MenuContainer categorySelector = createMenuWindow(3, "selector", (player, click) -> openMainMenu(player));

        // Common items
        categorySelector.setItem(19, getCategoryButton(Items.COOKIE, "Обычные предметы"));
        categorySelector.setAction(19, (player, click) -> openAllItemsByCategory(player, ToolType.NONE));

        // Hoes
        categorySelector.setItem(20, getCategoryButton(Items.DIAMOND_HOE, "Мотыги"));
        categorySelector.setAction(20, (player, click) -> openAllItemsByCategory(player, ToolType.HOE));

        // Equipment (Armor)
        categorySelector.setItem(21, getCategoryButton(Items.NETHERITE_CHESTPLATE, "Экипировка"));
        categorySelector.setAction(21, (player, click) -> openAllItemsByCategory(player, ToolType.EQUIPMENT, ToolType.BACKPACK));

        // Enchantments
        categorySelector.setItem(22, getCategoryButton(Items.ENCHANTED_BOOK, "Зачарования"));
        categorySelector.setAction(22, (player, click) -> openAllItemsByCategory(player, ToolType.ENCHANTMENT));

        // [Mining] Pickaxes
        categorySelector.setItem(23, getCategoryButton(Items.GOLDEN_PICKAXE, "Кирки (не используется)"));
        categorySelector.setAction(23, (player, click) -> openAllItemsByCategory(player, ToolType.PICKAXE));

        // [Mining] Common items
        categorySelector.setItem(24, getCategoryButton(Items.AMETHYST_SHARD, "Шахтёрские предметы (не используется)"));
        categorySelector.setAction(24, (player, click) -> openAllItemsByCategory(player, ToolType.MINING_COMMON));

        containerManager.openContainer(serverCookiePlayer, categorySelector);
    }

    private void openAllItemsByCategory(ServerCookiePlayer serverCookiePlayer, ToolType... toolTypes) {
        MenuContainer allItems = createMenuWindow(3, "creative", (player, click) -> openCategorySelector(player));
        List<ToolType> validToolTypes = Arrays.asList(toolTypes);

        int slot = 9;
        for (ItemTag tag : ItemTag.values()) {
            if (!validToolTypes.contains(tag.getCategory())) continue;

            ItemStack itemStack = loadedItems.get(tag);

            allItems.setItem(slot, itemStack);
            allItems.setAction(slot, (player, clickType) -> {
                containerManager.cancelClick(serverCookiePlayer);
                if (player.getPlayer().isCreative()) {
                    giveItem(serverCookiePlayer, itemStack, clickType);
                } else {
                    openGuide(serverCookiePlayer, tag, itemStack);
                }
            });
            slot++;
        }

        containerManager.openContainer(serverCookiePlayer, allItems);
    }

    public void openAllRecipes(ServerCookiePlayer serverCookiePlayer) {
        MenuContainer recipesWindow = createMenuWindow(2, "all_recipes", (player, click) -> openMainMenu(player));

        int slot = 9;
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
        ItemStack fillerItem = getFiller();

        for (int i : List.of(3, 12, 21)) {
            singleRecipe.setItem(i, fillerItem);
        }
        singleRecipe.setItem(13, loadedItems.get(recipe.getResultTag()));
        singleRecipe.setItem(8, getReturnButton());
        singleRecipe.setAction(8, (player, clickType) -> openAllRecipes(player));

        singleRecipe.setDefaultAction(containerManager::cancelClick);

        containerManager.openContainer(serverCookiePlayer, singleRecipe);
    }

    private void giveItem(ServerCookiePlayer serverCookiePlayer, ItemStack itemStack,
                          WrapperPlayClientClickWindow.WindowClickType clickType) {
        boolean isShiftClick = clickType == WrapperPlayClientClickWindow.WindowClickType.QUICK_MOVE;
        boolean isStackable = itemStack.getMaxStackSize() > 1;

        int amount = (isShiftClick && isStackable) ? itemStack.getMaxStackSize() : 1;
        serverCookiePlayer.getPlayer().addItem(itemStack.copyWithCount(amount));

    }

    private void openGuide(ServerCookiePlayer serverCookiePlayer, ItemTag itemTag, ItemStack itemStack) {

        List<DialogBody> lines = new ArrayList<>();

        // Добавление предмета перед описанием, чтобы было понятнее
        lines.add(new ItemBody(itemStack, Optional.empty(),
                true, true, 16, 16));

        itemsDescription.getText(itemTag).forEach(line ->
                lines.add(new PlainMessage(Component.literal(line), 300)));

        CommonDialogData commonDialogData = new CommonDialogData(
                Component.literal("Описание предмета"),
                Optional.empty(), true, false,
                DialogAction.CLOSE,
                lines, List.of()
        );

        NoticeDialog noticeDialog = new NoticeDialog(
                commonDialogData,
                new ActionButton(new CommonButtonData(Component.literal("Ok"), 100), Optional.empty())
        );

        // NMS пакет, потому что Packet Events нормально не отображает предмет
        serverCookiePlayer.sendMinecraftPacket(new ClientboundShowDialogPacket(Holder.direct(noticeDialog)));
    }

    private MenuContainer createMenuWindow(Integer windowType, String customData, BiConsumer<ServerCookiePlayer, WrapperPlayClientClickWindow.WindowClickType> closeAction) {
        MenuContainer menuContainer = new MenuContainer(ClickerContainer.generateId(), windowType, customData);

        // Заполнение верхней панели (первые 9 слотов)
        ItemStack filler = getFiller();
        ItemStack close = getReturnButton();

        for (int i = 0; i < 8; i++) {
            menuContainer.setItem(i, filler);
        }
        menuContainer.setItem(8, close);

        menuContainer.setAction(8, closeAction);
        menuContainer.setDefaultAction(containerManager::cancelClick);

        return menuContainer;
    }

    private ItemStack getRecipeButton() {
        CommonCookieItem recipe = new CommonCookieItem(Items.KNOWLEDGE_BOOK, ItemTag.EMPTY,
                "<gradient:#01e14f:#30a257><italic:false>Посмотреть все рецепты");

        return recipe.toMinecraftStack();
    }

    private ItemStack getCreativeButton() {
        CommonCookieItem allItems = new CommonCookieItem(Items.BOOK, ItemTag.EMPTY,
                "<gradient:#ffc900:#f3e736:#f7d760:#e1b926:#f3e736:#ffc900><italic:false>Посмотреть все предметы");

        allItems.addLore("<#e7f0ef><italic:false>Кликни на предмет, чтобы узнать больше");

        return allItems.toMinecraftStack();
    }

    private ItemStack getStatButton(ServerCookiePlayer serverCookiePlayer) {
        CommonCookieItem stats = new CommonCookieItem(Items.WRITABLE_BOOK, ItemTag.EMPTY,
                "<gradient:#e5fffe:#e7f0ef><italic:false>Статистика игрока");

        statisticDisplay.getStatsAsList(serverCookiePlayer).forEach(stats::addLore);
        return stats.toMinecraftStack();
    }

    private ItemStack getFiller() {
        CommonCookieItem filler = new CommonCookieItem(Items.WHITE_STAINED_GLASS_PANE, ItemTag.EMPTY,
                "<gradient:#C30707:#D43030>???</gradient>");

        ItemStack itemStack = filler.toMinecraftStack();
        itemStack.applyComponents(DataComponentPatch.builder()
                .set(DataComponents.TOOLTIP_DISPLAY, new TooltipDisplay(true, new LinkedHashSet<>()))
                .build());
        return itemStack;
    }

    private ItemStack getReturnButton() {
        CommonCookieItem close = new CommonCookieItem(Items.RED_STAINED_GLASS_PANE, ItemTag.EMPTY,
                "<gradient:#C30707:#D43030>Назад</gradient>");

        return close.toMinecraftStack();
    }

    private ItemStack getCategoryButton(Item type, String name) {
        CommonCookieItem category = new CommonCookieItem(type, ItemTag.EMPTY,
                "<gradient:#5FFF35:#54B693><italic:false>Категория: " + name);

        ItemStack categoryButton = category.toMinecraftStack();
        categoryButton.remove(DataComponents.ATTRIBUTE_MODIFIERS);
        return categoryButton;
    }
}
