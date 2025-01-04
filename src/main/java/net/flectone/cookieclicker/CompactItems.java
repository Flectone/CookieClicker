package net.flectone.cookieclicker;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.utility.UtilsCookie;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class CompactItems {
    private final UtilsCookie utilsCookie;
    @Inject
    public CompactItems(UtilsCookie utilsCookie) {
        this.utilsCookie = utilsCookie;
    }
    public void compact(net.minecraft.world.entity.player.Inventory inv, ItemStack findItem, ItemStack resultItem, Integer countToFind) {
        compact(inv, findItem, resultItem, countToFind, 100);
    }
    public void compact(Inventory inv, ItemStack findItem, ItemStack resultItem, Integer countToFind, Integer times) {
        HashMap<Integer, Integer> mapa = new HashMap<>();// Слот : количество предметов
        int itemsCount = 0;
        for (int i = 0; i <= 35; i++) {
            ItemStack itemInSlot = inv.getItem(i);
            if (itemInSlot.getItem() == Items.AIR) continue;
            if (utilsCookie.compare(itemInSlot, findItem)) {
                itemsCount += itemInSlot.getCount();
                mapa.put(i, itemInSlot.getCount());
            }
        }
        while (itemsCount >= countToFind && times > 0) {
            int currentCount = countToFind;
            HashMap<Integer, Integer> toChange = new HashMap<>();
            for (Map.Entry<Integer, Integer> entry : mapa.entrySet()) {
                if (currentCount <= 0) break;
                Integer itmAmount = entry.getValue();
                int value = itmAmount - currentCount;
                currentCount -= itmAmount;
                if (value <= 0) {
                    toChange.put(entry.getKey(), 0);
                    inv.getItem(entry.getKey()).setCount(0);
                }
                else {
                    inv.getItem(entry.getKey()).setCount(value);
                    toChange.put(entry.getKey(), value);
                }
            }
            toChange.forEach((key, value) -> {

                if (value == 0) mapa.remove(key);
                else mapa.put(key, value);
            });
            toChange.clear();
            inv.add(resultItem.copy());
            itemsCount -= countToFind;
            times--;
        }
    }
}
