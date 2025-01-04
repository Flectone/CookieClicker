package net.flectone.cookieclicker.items;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.utility.UtilsCookie;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Singleton
public class ShopManager {
    public final HashMap<ItemStack, ItemStack> sellingItems = new HashMap<>();
    private final ItemManager manager;
    private final UtilsCookie utilsCookie;

    @Inject
    public ShopManager(ItemManager manager, UtilsCookie utilsCookie) {
        this.manager = manager;
        this.utilsCookie = utilsCookie;
    }

    public void loadSellingItems() {
        sellingItems.put(manager.get("destroyer"), utilsCookie.createItemAmount(manager.get("ench_cookie"), 100));
        sellingItems.put(manager.get("wood_hoe"), utilsCookie.createItemAmount(manager.get("ench_cookie"), 10));
        sellingItems.put(manager.get("stone_hoe"), utilsCookie.createItemAmount(manager.get("ench_cookie"), 30));
        sellingItems.put(manager.get("rose_bush"), utilsCookie.createItemAmount(manager.get("baguette"), 60));
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
