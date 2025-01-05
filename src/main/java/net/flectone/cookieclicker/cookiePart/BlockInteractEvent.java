package net.flectone.cookieclicker.cookiePart;

import com.google.inject.Inject;
import net.flectone.cookieclicker.items.ItemManager;
import net.flectone.cookieclicker.utility.UtilsCookie;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.block.ChiseledBookshelf;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ChiseledBookshelfInventory;
import org.bukkit.inventory.ItemStack;

public class BlockInteractEvent implements Listener {
    private final ItemManager manager;
    private final UtilsCookie utilsCookie;

    @Inject
    public BlockInteractEvent(ItemManager manager, UtilsCookie utilsCookie) {
        this.manager = manager;
        this.utilsCookie = utilsCookie;
    }

    @EventHandler
    public void clickOnBookshelf(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null || !(event.getClickedBlock().getState() instanceof ChiseledBookshelf bookshelf)) return;
        Player pl = event.getPlayer();
        ItemStack itemInHand = pl.getInventory().getItemInMainHand();

        if (!(utilsCookie.compare(itemInHand, manager.get("ench_cookie"))) || itemInHand.getAmount() < 15) return;
        ChiseledBookshelfInventory inv = bookshelf.getInventory();
        for (int i = 0; i < 6; i++) {
            if ((inv.getItem(i) != null)) continue;
            bookshelf.getInventory().setItem(i, manager.get("book_boost1"));
            itemInHand.setAmount(itemInHand.getAmount() - 15);

            MiniMessage miniMessage = MiniMessage.miniMessage();
            pl.sendMessage(miniMessage.deserialize("<#f4a91c>\uD83C\uDF6A <#f7f4b5>Вы купили книгу!"));
            return;
        }

    }
}
