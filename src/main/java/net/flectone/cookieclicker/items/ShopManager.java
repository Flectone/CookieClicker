package net.flectone.cookieclicker.items;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.utility.UtilsCookie;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Singleton
public class ShopManager {
    private final HashMap<ItemStack, ItemStack> sellingItems = new HashMap<>();
    private final ItemManager manager;
    private final UtilsCookie utilsCookie;

    @Inject
    public ShopManager(ItemManager manager, UtilsCookie utilsCookie) {
        this.manager = manager;
        this.utilsCookie = utilsCookie;
    }

    public void loadSellingItems() {
        sellingItems.put(manager.getNMS("destroyer"), utilsCookie.createItemAmountNMS(manager.getNMS("ench_cookie"), 100));
        sellingItems.put(manager.getNMS("wood_hoe"), utilsCookie.createItemAmountNMS(manager.getNMS("ench_cookie"), 10));
        sellingItems.put(manager.getNMS("stone_hoe"), utilsCookie.createItemAmountNMS(manager.getNMS("ench_cookie"), 30));
        sellingItems.put(manager.getNMS("rose_bush"), utilsCookie.createItemAmountNMS(manager.getNMS("baguette"), 60));
    }
    public Integer itemsLength() {
        return sellingItems.size();
    }
    public ItemStack getItem(Integer num) {
        return (ItemStack) sellingItems.keySet().toArray()[num];
    }
    public ItemStack getPrice(Integer num) {
        return (ItemStack) sellingItems.values().toArray()[num];
    }
    public Set<Map.Entry<ItemStack, ItemStack>> getEntrySet() {
        return sellingItems.entrySet();
    }

}
