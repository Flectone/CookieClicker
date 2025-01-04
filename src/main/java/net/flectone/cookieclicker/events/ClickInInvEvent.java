package net.flectone.cookieclicker.events;

import com.google.inject.Inject;
import net.flectone.cookieclicker.CompactItems;
import net.flectone.cookieclicker.CookieClicker;
import net.flectone.cookieclicker.utility.UtilsCookie;
import net.flectone.cookieclicker.items.ShopManager;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

public class ClickInInvEvent implements Listener {
    private final ShopManager shopManager;
    private final CompactItems compactItems;
    private final UtilsCookie utilsCookie;
    @Inject
    public ClickInInvEvent(ShopManager shopManager, CompactItems compactItems, UtilsCookie utilsCookie) {
        this.shopManager = shopManager;
        this.compactItems = compactItems;
        this.utilsCookie = utilsCookie;
    }

    @EventHandler
    public void shopClick (InventoryClickEvent event) {
        HumanEntity he = event.getWhoClicked();
        if (he.getMetadata("inv").isEmpty()
                || !he.getMetadata("inv").getFirst().asString().equals("shop")
                || event.getClickedInventory() == null
                || event.getClickedInventory().getType().equals(InventoryType.PLAYER)) return;
        he.sendMessage(Component.text(event.getSlot()));
        event.setCancelled(true);
        int slot = event.getSlot();
        if (slot >= 9) {
            if (shopManager.itemsLength() <= slot - 9) return;

            ItemStack priceItem = shopManager.getPrice(slot - 9);
            //compactItems.compact(he.getInventory(), new ItemStack(priceItem), shopManager.getItem(slot - 9), priceItem.getAmount(), 1);
        }
    }
    @EventHandler
    public void clickInAnvil(InventoryClickEvent event) {
        if (!(event.getClickedInventory() instanceof AnvilInventory) || event.getCurrentItem() == null) return;
        if (event.getSlot() != 2) return;
        utilsCookie.updateStats(event.getCurrentItem());
    }

    @EventHandler
    public void closeInv (InventoryCloseEvent event) {
        HumanEntity pl = event.getPlayer();
        if (!(pl.getMetadata("inv").isEmpty()) && pl.getMetadata("inv").getFirst().asString().equals("shop"))
            pl.removeMetadata("inv", CookieClicker.getPlugin(CookieClicker.class));
    }
}
