package net.flectone.cookieclicker.events;

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.items.CustomRecipe;
import net.flectone.cookieclicker.items.ItemManager;
import net.flectone.cookieclicker.items.Recipes;
import net.flectone.cookieclicker.playerdata.ServerCookiePlayer;
import net.flectone.cookieclicker.utility.Pair;
import net.flectone.cookieclicker.utility.StatsUtils;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class PacketCraftingEvent {
    private final StatsUtils statsUtils;
    private final Recipes recipes;
    private final ItemManager loadedItems;

    @Inject
    public PacketCraftingEvent(Recipes recipes, StatsUtils statsUtils, ItemManager loadedItems) {
        this.recipes = recipes;
        this.statsUtils = statsUtils;
        this.loadedItems = loadedItems;
    }

    private List<Pair<String, Integer>> createList (AbstractContainerMenu inv) {
        List<Pair<String, Integer>> itemsList = new ArrayList<>();

        for (int i = 1; i < 10; i++) {
            ItemStack item = inv.getSlot(i).getItem();
            itemsList.add(new Pair<>(
                    item.getItem() == Items.AIR ? "air" : statsUtils.getItemTag(item),
                    item.getCount()
            ));
        }
        return itemsList;
    }

    public CustomRecipe findRecipe(AbstractContainerMenu craftingContainer) {
        List<Pair<String, Integer>> ingredients = createList(craftingContainer);
        return recipes.getRecipeIfExists(ingredients);
    }

    public Integer calculateAmount(CustomRecipe recipe, AbstractContainerMenu craftingGrid) {
        int amount = 0;

        for (Pair<Integer, Integer> pair : recipes.createListOfAmounts(createList(craftingGrid), recipe)) {
            int possibleAmount = pair.left() / pair.right();
            amount = amount == 0 ? possibleAmount : Math.min(amount, possibleAmount);
        }
        return amount;
    }

    public void onCraft(ServerCookiePlayer serverCookiePlayer, WrapperPlayClientClickWindow.WindowClickType windowClickType) {
        AbstractContainerMenu craftingContainer = serverCookiePlayer.getPlayer().containerMenu;
        CustomRecipe recipe = findRecipe(craftingContainer);

        if (recipe == null) return;
        //Если курсор уже держит предмет, то в крафте можно взять только шифтом
        if (!windowClickType.equals(WrapperPlayClientClickWindow.WindowClickType.QUICK_MOVE)
                && (!statsUtils.hasTag(craftingContainer.getCarried(), recipe.getResultTag())
                && !craftingContainer.getCarried().getItem().equals(Items.AIR))) return;

        craftingContainer.setItem(0, craftingContainer.getStateId(), new ItemStack(Items.AIR));

        //тут будет проблема, что при крафте всегда получается один предмет, если сделать больше, то оно не будет работать
        int amount = windowClickType.equals(WrapperPlayClientClickWindow.WindowClickType.QUICK_MOVE)
                ? calculateAmount(recipe, craftingContainer)
                : 1;

        List<Integer> recipeIng = recipes.getAmountsList(recipe.getAllIngredients());
        int ingIndex = 0;
        for (int i = 1; i < 10; i++) {
            ItemStack itemStack = craftingContainer.getSlot(i).getItem();
            if (itemStack.getItem() == Items.AIR)
                continue;
            itemStack.setCount(itemStack.getCount() - recipeIng.get(ingIndex) * amount);
            ingIndex++;

        }

        ItemStack resultItem = loadedItems.getNMS(recipe.getResultTag());

        if (windowClickType.equals(WrapperPlayClientClickWindow.WindowClickType.QUICK_MOVE)) {
            resultItem.setCount(amount);
            serverCookiePlayer.getPlayer().getInventory().add(resultItem);
        } else {
            amount += getCarriedAmount(craftingContainer.getCarried(), recipe.getResultTag());
            resultItem.setCount(amount);
            craftingContainer.setCarried(resultItem);
        }
    }

    private Integer getCarriedAmount(ItemStack carriedItemStack, String resultTag) {
        int amount = 0;
        if (carriedItemStack.getItem() == Items.AIR)
            return amount;

        if (statsUtils.hasTag(carriedItemStack, resultTag)) {
            amount += carriedItemStack.getCount();
        }
        return amount;
    }

    public void prepareCraft(ServerCookiePlayer serverCookiePlayer) {
        AbstractContainerMenu craftingContainer = serverCookiePlayer.getPlayer().containerMenu;
        if (!craftingContainer.getSlot(0).getItem().getItem().equals(Items.AIR))
            return;
        CustomRecipe recipe = findRecipe(craftingContainer);
        if (recipe == null) return;

        List<Pair<Integer, Integer>> craftingAndRecipeIngredients = recipes.createListOfAmounts(createList(craftingContainer), recipe);
        for (Pair<Integer, Integer> pair : craftingAndRecipeIngredients) {
            if (pair.left() < pair.right()) return;
        }

        craftingContainer.setItem(0, craftingContainer.getStateId(), loadedItems.getNMS(recipe.getResultTag()));
    }
}
