package net.flectone.cookieclicker.inventories;

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.CompactItems;
import net.flectone.cookieclicker.items.VillagerTrades;
import net.flectone.cookieclicker.playerdata.ServerCookiePlayer;

@Singleton
public class Shops {
    private final ContainerManager containerManager;
    private final VillagerTrades villagerTrades;
    private final CompactItems compactItems;

    @Inject
    public Shops(ContainerManager containerManager, VillagerTrades villagerTrades,
                 CompactItems compactItems) {
        this.containerManager = containerManager;
        this.villagerTrades = villagerTrades;
        this.compactItems = compactItems;
    }

    public void openAnyShop(ServerCookiePlayer serverCookiePlayer, String traderType) {
        containerManager.openContainer(serverCookiePlayer, villagerTrades.createAnyShop(traderType));
    }

    public void buyItem(ServerCookiePlayer serverCookiePlayer, WrapperPlayClientClickWindow packet) {
        int slot = packet.getSlot();

        ClickerContainer container = containerManager.getOpenedContainer(serverCookiePlayer.getUser());

        containerManager.cancelClick(serverCookiePlayer, container, slot, packet.getWindowClickType());

        if (container.getContainerItems().size() - 1 < slot) return;

        String traderType = container.getCustomData();

        if (slot < 9 || villagerTrades.itemsLength(traderType) <= slot - 9)
            return;

        compactItems.compact(serverCookiePlayer.getPlayer().getInventory(),
                villagerTrades.getPriceItem(traderType, slot - 9),
                villagerTrades.getItem(traderType, slot - 9),
                villagerTrades.getPriceCount(traderType, slot - 9), 1);
    }

}
