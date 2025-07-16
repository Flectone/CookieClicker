package net.flectone.cookieclicker.events;

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.inventories.ClickerContainer;
import net.flectone.cookieclicker.inventories.ContainerManager;
import net.flectone.cookieclicker.inventories.MainMenu;
import net.flectone.cookieclicker.inventories.Shops;
import net.flectone.cookieclicker.playerdata.ServerCookiePlayer;

@Singleton
public class PacketClickInventoryEvent {
    private final ContainerManager containerManager;
    private final MainMenu mainMenu;
    private final Shops shops;
    private final AnvilEvent anvilEvent;
    private final PacketCraftingEvent packetCraftingEvent;

    @Inject
    public PacketClickInventoryEvent(ContainerManager containerManager, MainMenu mainMenu, Shops shops, AnvilEvent anvilEvent,
                                     PacketCraftingEvent packetCraftingEvent) {
        this.anvilEvent = anvilEvent;
        this.packetCraftingEvent = packetCraftingEvent;
        this.shops = shops;
        this.mainMenu = mainMenu;
        this.containerManager = containerManager;
    }

    private void manageContainers(ServerCookiePlayer serverCookiePlayer, ClickerContainer container, WrapperPlayClientClickWindow clickPacket) {
        switch (container.getCustomData()) {
            //покупка предмета
            case "trading_farm", "trading_armorer" -> shops.buyItem(serverCookiePlayer, clickPacket);
            //главное меню, выбор
            case "main_menu" ->  {
                if (clickPacket.getSlot() == 15) mainMenu.openAllItems(serverCookiePlayer);
                if (clickPacket.getSlot() == 11) mainMenu.openAllRecipes(serverCookiePlayer);
                containerManager.cancelClick(serverCookiePlayer, container, clickPacket.getSlot(), clickPacket.getWindowClickType());
            }
            //возврат назад в меню выбора рецепта
            case "recipe" -> {
                containerManager.cancelClick(serverCookiePlayer, container, clickPacket.getSlot(), clickPacket.getWindowClickType());
                if (clickPacket.getSlot() == 8) {
                    mainMenu.openAllRecipes(serverCookiePlayer);
                }
            }
            //выбор рецепта
            case "all_recipes" -> mainMenu.selectRecipe(serverCookiePlayer, clickPacket.getSlot(), clickPacket.getWindowClickType());
            //выбор предмета во всех предметах
            case "all_items" -> mainMenu.getItemInMenu(clickPacket.getSlot(), serverCookiePlayer, clickPacket.getWindowClickType());
        }
    }

    public void manageWindow(ServerCookiePlayer serverCookiePlayer, WrapperPlayClientClickWindow clickPacket) {
        if (clickPacket.getSlot() == -999) return;
        ClickerContainer container = containerManager.getOpenedContainer(serverCookiePlayer.getUuid());

        switch (container.getWindowType()) {
            //наковальня
            case 8 -> anvilEvent.anvilClick(serverCookiePlayer.getPlayer(), clickPacket.getSlot());
            //инвентари 9 * x
            case 1, 2, 3, 4, 5 -> manageContainers(serverCookiePlayer, container, clickPacket);
            //верстак
            case 12 -> {
                if (clickPacket.getSlot() == 0) {
                    packetCraftingEvent.onCraft(serverCookiePlayer, clickPacket.getWindowClickType());
                }
            }
        }
    }

    public void checkAndPrepareCraft(ServerCookiePlayer serverCookiePlayer) {
        if (containerManager.getOpenedContainer(serverCookiePlayer).getWindowType() == 12) {
            packetCraftingEvent.prepareCraft(serverCookiePlayer);
        }
    }

    public void checkForEquipUpgrade(ServerCookiePlayer serverCookiePlayer) {
        anvilEvent.processUpgrade(serverCookiePlayer);
    }


}
