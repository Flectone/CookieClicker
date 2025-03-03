package net.flectone.cookieclicker.events;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityStatus;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenWindow;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.inventories.ClickerContainer;
import net.flectone.cookieclicker.inventories.ContainerManager;
import net.flectone.cookieclicker.inventories.MainMenu;
import net.flectone.cookieclicker.inventories.Shops;
import net.flectone.cookieclicker.items.ItemManager;
import net.flectone.cookieclicker.utility.CCobjects.CookiePlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;

@Singleton
public class Packets implements PacketListener {

    private final PacketSetSlotEvent setSlotEvent;
    private final ItemManager manager;
    private final PacketCookieClickEvent packetCookieClickEvent;
    private final PacketCraftingEvent packetCraftingEvent;
    private final PacketEatingEvent packetEatingEvent;
    private final ContainerManager containerManager;
    private final Shops shops;
    private final MainMenu mainMenu;
    private final AnvilEvent anvilEvent;

    private final PacketInteractEvent packetInteractEvent;

    @Inject
    public Packets(PacketSetSlotEvent setSlotEvent, ItemManager manager, MainMenu mainMenu, PacketEatingEvent packetEatingEvent,
                   PacketCookieClickEvent packetCookieClickEvent, ContainerManager containerManager,
                   PacketCraftingEvent packetCraftingEvent, Shops shops, PacketInteractEvent packetInteractEvent,
                   AnvilEvent anvilEvent) {
        this.setSlotEvent = setSlotEvent;
        this.manager = manager;
        this.packetCookieClickEvent = packetCookieClickEvent;
        this.packetEatingEvent = packetEatingEvent;
        this.containerManager = containerManager;
        this.packetCraftingEvent = packetCraftingEvent;
        this.shops = shops;
        this.mainMenu = mainMenu;
        this.anvilEvent = anvilEvent;

        this.packetInteractEvent = packetInteractEvent;
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        User user = event.getUser();
        if (user == null) return;
        CookiePlayer cookiePlayer = new CookiePlayer(user.getUUID());
        Player player = cookiePlayer.getPlayer();

        switch (event.getPacketType()) {
            case PacketType.Play.Client.CLICK_WINDOW -> {
                manageWindow(cookiePlayer, new WrapperPlayClientClickWindow(event));
            }
            case PacketType.Play.Client.CLOSE_WINDOW -> {
                containerManager.closeContainer(user);
            }
            case PacketType.Play.Client.ANIMATION -> {
                packetCookieClickEvent.changeLegendaryHoeMode(user);
            }
            case PacketType.Play.Client.INTERACT_ENTITY -> {
                if (packetInteractEvent.checkEntity(new WrapperPlayClientInteractEntity(event), cookiePlayer)) {
                    event.setCancelled(true);
                }
//                if (packetInteractEvent.manageInteract(cookiePlayer, new WrapperPlayClientInteractEntity(event)))
//                    event.setCancelled(true);
            }
            case PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT -> {
                packetCookieClickEvent.bookShelfClick(user, player);
            }
            case PacketType.Play.Client.USE_ITEM -> {
                if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem().equals(Items.JIGSAW)) {
                    mainMenu.openMainMenu(cookiePlayer);
                }
            }
            default -> {
                return;
            }
        }

    }

    private void manageWindow(CookiePlayer cookiePlayer, WrapperPlayClientClickWindow clickPacket) {
        if (clickPacket.getSlot() == -999) return;
        ClickerContainer container = containerManager.getOpenedContainer(cookiePlayer.getUuid());

        switch (container.getWindowType()) {
            //наковальня
            case 8 -> anvilEvent.anvilClick(cookiePlayer.getPlayer(), clickPacket.getSlot());
            //инвентари 9 * x
            case 1, 2, 3, 4, 5 -> manageContainers(cookiePlayer, container, clickPacket);
            //верстак
            case 12 -> {
                if (clickPacket.getSlot() == 0) {
                    packetCraftingEvent.onCraft(cookiePlayer, clickPacket.getWindowClickType());
                }
            }
        }
    }

    private void manageContainers(CookiePlayer cookiePlayer, ClickerContainer container, WrapperPlayClientClickWindow clickPacket) {
        switch (container.getCustomData()) {
            //покупка предмета
            case "trading_farm" -> shops.buyItemFarmer(cookiePlayer, clickPacket);
            //главное меню, выбор
            case "main_menu" ->  {
                if (clickPacket.getSlot() == 15) mainMenu.openAllItems(cookiePlayer);
                if (clickPacket.getSlot() == 11) mainMenu.openAllRecipes(cookiePlayer);
                containerManager.cancelClick(cookiePlayer.getPlayer(), container, clickPacket.getSlot(), clickPacket.getWindowClickType());
            }
            //возврат назад в меню выбора рецепта
            case "recipe" -> {
                containerManager.cancelClick(cookiePlayer.getPlayer(), container, clickPacket.getSlot(), clickPacket.getWindowClickType());
                if (clickPacket.getSlot() == 8) {
                    mainMenu.openAllRecipes(cookiePlayer);
                }
            }
            //выбор рецепта
            case "all_recipes" -> mainMenu.selectRecipe(cookiePlayer, clickPacket.getSlot(), clickPacket.getWindowClickType());
            //выбор предмета во всех предметах
            case "all_items" -> mainMenu.getItemInMenu(clickPacket.getSlot(), cookiePlayer.getPlayer(), clickPacket.getWindowClickType());
        }
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getUser() == null)
            return;
        CookiePlayer cookiePlayer = new CookiePlayer(event.getUser().getUUID());
        Player player = cookiePlayer.getPlayer();

        if (event.getPacketType() == PacketType.Play.Server.OPEN_WINDOW) {
            WrapperPlayServerOpenWindow openContainerPacket = new WrapperPlayServerOpenWindow(event);
            containerManager.setOpenedContainer(event.getUser().getUUID(), openContainerPacket, "default");
        }

        //200iq костыль, ну а хули
        if (event.getPacketType() == PacketType.Play.Server.WINDOW_PROPERTY) {
            anvilEvent.processUpgrade(cookiePlayer);
        }

        if (event.getPacketType() == PacketType.Play.Server.SET_SLOT) {;
            WrapperPlayServerSetSlot setSlotPacket = new WrapperPlayServerSetSlot(event);
            //&& setSlotPacket.getSlot() != 0
            if (containerManager.getOpenedContainer(cookiePlayer).getWindowType() == 12) {
                packetCraftingEvent.prepareCraft(cookiePlayer);
            }

            setSlotEvent.compactItems(cookiePlayer);
            return;
        }

        if (event.getPacketType() == PacketType.Play.Server.ENTITY_STATUS) {
            packetEatingEvent.onEat(new WrapperPlayServerEntityStatus(event), cookiePlayer);
        }


    }
}
