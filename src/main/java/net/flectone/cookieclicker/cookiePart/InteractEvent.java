package net.flectone.cookieclicker.cookiePart;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.CookieClicker;
import net.flectone.cookieclicker.items.ItemManager;
import net.flectone.cookieclicker.items.ShopManager;
import net.flectone.cookieclicker.utility.UtilsCookie;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;
import java.util.Map;


@Singleton
public class InteractEvent implements Listener {
    private final ItemManager manager;
    private final ShopManager shopManager;
    private final UtilsCookie utilsCookie;
    private final BagHoeUpgrade bagHoe;
    private final EpicHoeUtils epicHoeUtils;
    final NamespacedKey key = new NamespacedKey("cc2", "custom");

    @Inject
    public InteractEvent(ItemManager manager, ShopManager shopManager, UtilsCookie utilsCookie,
                         BagHoeUpgrade bagHoeUpgrade, EpicHoeUtils epicHoeUtils) {
        this.manager = manager;
        this.shopManager = shopManager;
        this.utilsCookie = utilsCookie;
        this.bagHoe = bagHoeUpgrade;
        this.epicHoeUtils = epicHoeUtils;
    }

    @EventHandler
    public void playerInteractVillager (PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Villager villager)) return;
        if (!(villager.getProfession().equals(Villager.Profession.FARMER))) return;
        event.setCancelled(true);
        Player pl = event.getPlayer();
        Inventory shop = Bukkit.createInventory(pl, 9 * 4, Component.text("Торговля"));
        pl.openInventory(shop);
        pl.setMetadata("inv", new FixedMetadataValue(CookieClicker.getPlugin(CookieClicker.class), "shop"));

        ItemStack glassPane = new ItemStack(Material.GLASS_PANE);
        ItemMeta glassMeta = glassPane.getItemMeta();
        MiniMessage mm = MiniMessage.miniMessage();
        glassMeta.displayName(mm.deserialize("<gradient:#ffffff:#cccccc><italic:false>Здесь вы можете купить"));
        glassPane.setItemMeta(glassMeta);
        glassPane.lore(List.of(mm.deserialize("<gradient:#ffffff:#cccccc><italic:false>различные вещи за печенье")));
        for (int i = 0; i < 9; i++)
            shop.setItem(i, glassPane);
        int slot = 9;
        for (Map.Entry<ItemStack, ItemStack> mapa : shopManager.getEntrySet()) {
            ItemStack entryItem = mapa.getKey();
            ItemStack finalItem = new ItemStack(entryItem.getType());
            ItemMeta finalItemMeta = entryItem.getItemMeta();
            Component component = MiniMessage.miniMessage().deserialize("<#C70039>Стоимость: "
                                                                    + mapa.getValue().getAmount() + " "
                                                                    + PlainTextComponentSerializer.plainText().serialize(mapa.getValue().getItemMeta().displayName()));
            List<Component> lore = List.of(component);
            List<Component> oldLore = entryItem.lore();
            if (oldLore != null)
                oldLore.addFirst(component);
            else
                oldLore = List.of(component);

            finalItemMeta.lore(oldLore);
            finalItemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            finalItem.setItemMeta(finalItemMeta);
            shop.setItem(slot, finalItem);
            slot++;
        }
    }
}
