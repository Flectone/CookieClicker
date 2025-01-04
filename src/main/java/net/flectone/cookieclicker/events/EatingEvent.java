package net.flectone.cookieclicker.events;

import com.google.inject.Inject;
import net.flectone.cookieclicker.items.ItemManager;
import net.flectone.cookieclicker.utility.UtilsCookie;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class EatingEvent implements Listener {
    private final ItemManager manager;
    private final UtilsCookie utilsCookie;

    @Inject
    public EatingEvent(ItemManager manager, UtilsCookie utilsCookie) {
        this.manager = manager;
        this.utilsCookie = utilsCookie;
    }
    @EventHandler
    public void onEat(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        Player pl = event.getPlayer();
        int amount = item.getAmount();
        item.setAmount(1);
        if (utilsCookie.compare(item, manager.get("cookie"))) pl.giveExp(1);
        if (utilsCookie.compare(item, manager.get("ench_cookie"))) pl.giveExp(160);
    }
}
