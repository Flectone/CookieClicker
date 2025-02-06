package net.flectone.cookieclicker.inventories.crafting;

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.inventories.ContainerManager;
import net.flectone.cookieclicker.items.Recipes;
import net.flectone.cookieclicker.utility.CCobjects.CookiePlayer;
import net.flectone.cookieclicker.utility.Pair;
import net.flectone.cookieclicker.utility.UtilsCookie;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class Crafting {
    private final UtilsCookie utilsCookie;
    private final ContainerManager containerManager;
    private final Recipes recipes;

    @Inject
    public Crafting(UtilsCookie utilsCookie, ContainerManager containerManager, Recipes recipes) {
        this.utilsCookie = utilsCookie;
        this.containerManager = containerManager;
        this.recipes = recipes;
    }

    private List<ItemStack> createList (AbstractContainerMenu inv) {
        List<ItemStack> itemStackList = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            ItemStack item = inv.getSlot(i).getItem();
            itemStackList.add(item.copy());
        }
        return itemStackList;
    }

    public CustomRecipe findRecipe(AbstractContainerMenu craftingContainer) {
        List<ItemStack> ingredients = createList(craftingContainer);
        return recipes.findRecipe(ingredients);
    }

    public Integer calculateAmount(CustomRecipe recipe, AbstractContainerMenu craftingGrid) {
        int amount = 0;

        for (Pair<Integer, Integer> pair : recipes.getAmountPairs(createList(craftingGrid), recipe.getAllIngredients())) {
            int possibleAmount = Math.round(pair.getKey()) / pair.getValue();
            amount = amount == 0 ? possibleAmount : Math.min(amount, possibleAmount);
        }
        return amount;
    }

    public void onCraft(CookiePlayer cookiePlayer, WrapperPlayClientClickWindow.WindowClickType windowClickType) {
        AbstractContainerMenu craftingContainer = cookiePlayer.getPlayer().containerMenu;
        CustomRecipe recipe = findRecipe(craftingContainer);

        if (recipe == null) return;
        if (!windowClickType.equals(WrapperPlayClientClickWindow.WindowClickType.QUICK_MOVE)
                && (!utilsCookie.compare(craftingContainer.getCarried(), recipe.getResult())
                && !craftingContainer.getCarried().getItem().equals(Items.AIR))) return;

        //тут будет проблема, что при крафте всегда получается один предмет, если сделать больше, то оно не будет работать
        int amount = windowClickType.equals(WrapperPlayClientClickWindow.WindowClickType.QUICK_MOVE)
                ? calculateAmount(recipe, craftingContainer)
                : 1;

        List<ItemStack> recipeIngredients = recipes.makeCleanList(recipe.getAllIngredients());
        int index = 0;
        for (int i = 1; i < 10; i++) {
            //получение предмета в сетке крафта
            ItemStack item = craftingContainer.getSlot(i).getItem();
            if (item.getItem().equals(Items.AIR))
                continue;
            item.setCount(item.getCount() - recipeIngredients.get(index).getCount() * amount);
            index++;
        }
        //Bukkit.getLogger().info("done crafting!");
        ItemStack resultItem = recipe.getResult().copy();

        if (windowClickType.equals(WrapperPlayClientClickWindow.WindowClickType.QUICK_MOVE)) {
            resultItem.setCount(amount);
            cookiePlayer.getPlayer().getInventory().add(resultItem);
        } else {
            amount += utilsCookie.compare(craftingContainer.getCarried(), recipe.getResult()) ? craftingContainer.getCarried().getCount() : 0;
            resultItem.setCount(amount);
            craftingContainer.setCarried(resultItem);
        }
    }

    public void prepareCraft(CookiePlayer cookiePlayer) {
        AbstractContainerMenu craftingContainer = cookiePlayer.getPlayer().containerMenu;
        CustomRecipe recipe = findRecipe(craftingContainer);
        if (recipe == null) return;

        if (!utilsCookie.compare(craftingContainer.getSlot(0).getItem(), recipe.getResult())) {
            containerManager.setContainerSlot(cookiePlayer.getPlayer(),
                    containerManager.getOpenedContainer(cookiePlayer),
                    0,
                    recipe.getResult().copy()
            );
            //craftingContainer.setItem(0, 0, recipe.getResult().copy());
            //Bukkit.getLogger().info("found recipe " + recipe.getResult().getItem());
        }
    }
}
