package net.flectone.cookieclicker.cookiePart;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.CookieClicker;
import net.flectone.cookieclicker.items.ItemManager;
import net.flectone.cookieclicker.utility.UtilsCookie;
import net.flectone.cookieclicker.items.ShopManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.*;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;


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
    public void playerInteract (PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof ItemFrame frame)) return;
        World world = frame.getWorld();
        Location loca = frame.getLocation();
        Player pl = event.getPlayer();
        boolean spawnBerries = false;
        boolean cookieCoal = utilsCookie.compare(pl.getInventory().getItemInOffHand(), manager.get("ench_cocoa"));


        ItemStack dropItem;
        int maxAmount = 1;
        Random rnd = new Random();

        ItemStack itemInHand = pl.getInventory().getItemInMainHand();

        maxAmount += utilsCookie.extractFortune(pl);
        // Количество предметов
        int droppedAmount = rnd.nextInt(maxAmount, maxAmount * 2);
        List<ItemStack> dropItems = new ArrayList<>();

        //droppedAmount += Math.round(droppedAmount * (0.5f * epicHoeUtils.getTier(pl)));

        // Показ статистики
//        pl.sendActionBar(MiniMessage.miniMessage().deserialize("<#eb6514>" + maxAmount + "⯫ "
//                + "<#e4a814>" + droppedAmount +"★ "
//                + "<#b014eb>[" + epicHoeUtils.getCharge(pl) + "% " +  epicHoeUtils.getTier(pl) + "☄]"));


//        Object pdcValue = utilsCookie.getPDCValue(itemInHand, key, PersistentDataType.STRING);
//        if (pdcValue != null && pdcValue.equals("epic_hoe")) {
//            world.spawnParticle(Particle.TRAIL, loca, 50, 2, 2, 2, 0, new Particle.Trail(loca, Color.PURPLE, 20));
//            epicHoeUtils.addCharge(pl, 1);
//        }

        if (!(frame.getItem().getType().equals(Material.COOKIE))) return;
        //Спавн предметов
        utilsCookie.spawnItemLegacy(droppedAmount, pl, loca);

        world.playSound(loca, Sound.ENTITY_GENERIC_EAT, 1, 1);
        world.spawnParticle(Particle.TRIAL_SPAWNER_DETECTION, loca, 2, 0.25, 0.1, 0.25, 0);

        if (bagHoe.updateHoe(pl))
            world.spawnParticle(Particle.TRIAL_SPAWNER_DETECTION_OMINOUS, loca, 1, 0.25, 0.1, 0.25, 0);

        event.setCancelled(true);

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

    @EventHandler
    public void LegHoeChange (PlayerInteractEvent event) {
        Player pl = event.getPlayer();
        ItemStack itemInHand = pl.getInventory().getItemInMainHand();
        ItemMeta meta = itemInHand.getItemMeta();
        Object pdcValue = utilsCookie.getPDCValue(pl.getInventory().getItemInMainHand(), key, PersistentDataType.STRING);
        if (meta == null || meta.getItemModel() == null) return;
        if (!(event.getAction().equals(Action.LEFT_CLICK_AIR))) return;
        if (pdcValue == null || !(pdcValue.equals("leg_hoe"))) return;
        Material current = Material.getMaterial(meta.getItemModel().value().toUpperCase());
        if (current == null) return;
        meta.setItemModel(NamespacedKey.minecraft(current.equals(Material.GOLDEN_HOE) ?
                Material.IRON_HOE.toString().toLowerCase()
                : Material.GOLDEN_HOE.toString().toLowerCase()));
        meta.getPersistentDataContainer().set(new NamespacedKey("cc2", "ability"),
                PersistentDataType.STRING,
                current.equals(Material.GOLDEN_HOE) ? "transform" : "infinity");
        itemInHand.setItemMeta(meta);
        pl.playSound(pl, Sound.ENTITY_ENDER_EYE_DEATH, 1f, 0.2f);
    }
}
