package net.flectone.cookieclicker.utility;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.items.itemstacks.base.data.ItemTag;
import net.flectone.cookieclicker.utility.data.Pair;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class ItemsCompactor {
    private final StatsUtils statsUtils;

    @Inject
    public ItemsCompactor(StatsUtils statsUtils) {
        this.statsUtils = statsUtils;
    }
    public void compact(net.minecraft.world.entity.player.Inventory inv, ItemTag findItemTag, ItemStack resultItem, Integer countToFind) {
        compact(inv, findItemTag, resultItem, countToFind, 100);
    }

    public void compact(Inventory inv, ItemStack findItem, ItemStack resultItem, Integer countToFind, Integer times) {
        compact(inv, statsUtils.getItemTag(findItem), resultItem, countToFind, times);
    }

    public void compact(Inventory inv, ItemTag findItemTag, ItemStack resultItem, Integer countToFind, Integer times) {
        HashMap<Integer, Integer> mapa = new HashMap<>();// Слот : количество предметов
        int itemsCount = 0;
        for (int i = 0; i <= 35; i++) {
            ItemStack itemInSlot = inv.getItem(i);
            if (itemInSlot.getItem() == Items.AIR) continue;
            if (statsUtils.hasTag(itemInSlot, findItemTag)) {
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

    public List<Pair<ItemTag, Integer>> compactFromValue(ItemTag from, ItemTag to, Integer amount, Integer amountToFind) {
        List<Pair<ItemTag, Integer>> items = new ArrayList<>();
        int compacted = amount / amountToFind;
        int remaining = amount - (compacted * amountToFind);

        if (compacted > 0) {
            items.add(new Pair<>(to, compacted));
        }

        items.add(new Pair<>(from, remaining));
        return items;
    }

    public List<Pair<ItemTag, Integer>> doubleCompactFromValue(ItemTag from, ItemTag toFirst, ItemTag toSecond,
                                                               Integer amount, Integer amountToFind1, Integer amountToFind2) {
        List<Pair<ItemTag, Integer>> items = compactFromValue(from, toFirst, amount, amountToFind1);

        if (items.getFirst().left() == toFirst) {
            items.addAll(compactFromValue(toFirst, toSecond, items.getFirst().right(),amountToFind2));
            items.removeFirst();
        }

        return items;
    }

}
