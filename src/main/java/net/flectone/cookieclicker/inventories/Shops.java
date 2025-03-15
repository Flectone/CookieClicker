package net.flectone.cookieclicker.inventories;

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.CompactItems;
import net.flectone.cookieclicker.items.VillagerTrades;
import net.flectone.cookieclicker.utility.CCConversionUtils;
import net.flectone.cookieclicker.utility.CCobjects.CookiePlayer;

@Singleton
public class Shops {
    private final ContainerManager containerManager;
    private final VillagerTrades villagerTrades;
    private final CCConversionUtils converter;
    private final CompactItems compactItems;

    @Inject
    public Shops(ContainerManager containerManager, VillagerTrades villagerTrades, CCConversionUtils converter,
                 CompactItems compactItems) {
        this.containerManager = containerManager;
        this.villagerTrades = villagerTrades;
        this.converter = converter;
        this.compactItems = compactItems;
    }

    public void openAnyShop(CookiePlayer cookiePlayer, String traderType) {
        containerManager.openContainer(cookiePlayer, villagerTrades.createAnyShop(traderType));
    }

    public void buyItem(CookiePlayer cookiePlayer, WrapperPlayClientClickWindow packet) {
        int slot = packet.getSlot();

        ClickerContainer container = containerManager.getOpenedContainer(cookiePlayer.getUser());

        containerManager.cancelClick(cookiePlayer, container, slot, packet.getWindowClickType());

        if (container.getContainerItems().size() - 1 < slot) return;

        String traderType = container.getCustomData();

        if (slot < 9 || villagerTrades.itemsLength(traderType) <= slot - 9)
            return;

        compactItems.compact(cookiePlayer.getPlayer().getInventory(),
                villagerTrades.getPriceItem(traderType, slot - 9),
                villagerTrades.getItem(traderType, slot - 9),
                villagerTrades.getPriceCount(traderType, slot - 9), 1);
    }

}
