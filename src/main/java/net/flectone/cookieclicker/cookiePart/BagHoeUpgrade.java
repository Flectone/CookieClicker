package net.flectone.cookieclicker.cookiePart;


import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.utility.CCobjects.ClickerItems;
import net.flectone.cookieclicker.utility.ItemTagsUtility;
import net.flectone.cookieclicker.utility.UtilsCookie;
import net.flectone.cookieclicker.items.ItemManager;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Singleton
public class BagHoeUpgrade {
    final UtilsCookie utilsCookie;
    final ItemManager manager;
    final ItemTagsUtility itemTagsUtility;
    @Inject
    public BagHoeUpgrade(UtilsCookie utilsCookie, ItemManager manager, ItemTagsUtility itemTagsUtility) {
        this.utilsCookie = utilsCookie;
        this.manager = manager;
        this.itemTagsUtility = itemTagsUtility;
    }
    public boolean updateHoe (Player pl) {
        ItemStack itemInHand = pl.getInventory().getItemInMainHand();
        net.minecraft.world.item.ItemStack itemNMS = CraftItemStack.asNMSCopy(itemInHand);


        if (itemInHand.getType().equals(Material.AIR)) return false;

        String value = itemTagsUtility.getAbility(itemNMS);
        if (!(value.equals("infinity"))) return false;

        //itemNMS.getComponents().get(DataComponents.DAMAGE) может выдать null и это плохо
        //Но теперь оно не может выдать null и это хорошо
        //200iq момент
        Object currentDamage = itemNMS.getComponents().get(DataComponents.DAMAGE);
        int dmg = currentDamage == null ? 0 : (int) currentDamage;

        setDamage(itemNMS, dmg - 1);
        //Если "прочность" заполнилась, то добавляется одна удача и сбрасывается прочность
        if (dmg <= 1) {
            setDamage(itemNMS, 99);

            itemTagsUtility.setStat(itemNMS, ClickerItems.fortuneTag, itemTagsUtility.getBaseFortune(itemInHand) + 1);
        }
        pl.getWorld().playSound(pl, Sound.BLOCK_NETHERITE_BLOCK_PLACE, 1f, 1.8f);

        //кринж штука, чисто для теста
        pl.getInventory().setItemInMainHand(CraftItemStack.asBukkitCopy(itemNMS));
        utilsCookie.updateStats(pl.getInventory().getItemInMainHand());
        return true;
    }

    public void setDamage(net.minecraft.world.item.ItemStack item, Integer value) {
        item.applyComponents(DataComponentPatch.builder()
                .set(DataComponents.DAMAGE,
                        value)
                .build());
    }
}
