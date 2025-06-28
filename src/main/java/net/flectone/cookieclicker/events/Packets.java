package net.flectone.cookieclicker.events;

import com.github.retrooper.packetevents.event.*;
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
import net.flectone.cookieclicker.playerdata.ServerCookiePlayer;
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
    private final ConnectedPlayers connectedPlayers;

    @Inject
    public Packets(PacketSetSlotEvent setSlotEvent, ItemManager manager, MainMenu mainMenu, PacketEatingEvent packetEatingEvent,
                   PacketCookieClickEvent packetCookieClickEvent, ContainerManager containerManager,
                   PacketCraftingEvent packetCraftingEvent, Shops shops, PacketInteractEvent packetInteractEvent,
                   AnvilEvent anvilEvent, ConnectedPlayers connectedPlayers) {
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
        this.connectedPlayers = connectedPlayers;
    }

    @Override
    public void onUserLogin(UserLoginEvent event) {
        System.out.println("кто-то зашёл");
        connectedPlayers.processPlayerJoin(event.getUser().getUUID(), event.getUser().getName());
    }

    @Override
    public void onUserDisconnect(UserDisconnectEvent event) {
        connectedPlayers.saveAndDeleteLocal(event.getUser().getUUID());
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        User user = event.getUser();
        if (user == null) return;
        CookiePlayer cookiePlayer = new CookiePlayer(user.getUUID());
        Player player = cookiePlayer.getPlayer();

        ServerCookiePlayer serverCookiePlayer = connectedPlayers.getServerCookiePlayer(user.getUUID());
        if (serverCookiePlayer == null) {
            return;
        }

        switch (event.getPacketType()) {
            case PacketType.Play.Client.CLICK_WINDOW -> {
                manageWindow(serverCookiePlayer, new WrapperPlayClientClickWindow(event));
            }
            case PacketType.Play.Client.CLOSE_WINDOW -> {
                containerManager.closeContainer(serverCookiePlayer.getUuid());
            }
            case PacketType.Play.Client.ANIMATION -> {
                packetCookieClickEvent.changeLegendaryHoeMode(user);
            }
            case PacketType.Play.Client.INTERACT_ENTITY -> {
                if (packetInteractEvent.checkEntity(new WrapperPlayClientInteractEntity(event), serverCookiePlayer)) {
                    event.setCancelled(true);
                }
//                if (packetInteractEvent.manageInteract(cookiePlayer, new WrapperPlayClientInteractEntity(event)))
//                    event.setCancelled(true);
            }
            case PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT -> {
                packetCookieClickEvent.bookShelfClick(serverCookiePlayer);
            }
            case PacketType.Play.Client.USE_ITEM -> {
                if (serverCookiePlayer.getPlayer().getItemInHand(InteractionHand.MAIN_HAND).getItem().equals(Items.JIGSAW)) {
                    mainMenu.openMainMenu(serverCookiePlayer);
                }
            }
            default -> {
                return;
            }
        }

    }

    private void manageWindow(ServerCookiePlayer serverCookiePlayer, WrapperPlayClientClickWindow clickPacket) {
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

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getUser() == null)
            return;
        CookiePlayer cookiePlayer = new CookiePlayer(event.getUser().getUUID());
        Player player = cookiePlayer.getPlayer();
        ServerCookiePlayer serverCookiePlayer = new ServerCookiePlayer(event.getUser().getUUID());
        Player player = serverCookiePlayer.getPlayer();

        if (event.getPacketType() == PacketType.Play.Server.OPEN_WINDOW) {
            WrapperPlayServerOpenWindow openContainerPacket = new WrapperPlayServerOpenWindow(event);
            containerManager.setOpenedContainer(event.getUser().getUUID(), openContainerPacket, "default");
        }

        //200iq костыль, ну а хули
        if (event.getPacketType() == PacketType.Play.Server.WINDOW_PROPERTY) {
            anvilEvent.processUpgrade(serverCookiePlayer);
        }

        if (event.getPacketType() == PacketType.Play.Server.SET_SLOT) {;
            WrapperPlayServerSetSlot setSlotPacket = new WrapperPlayServerSetSlot(event);
            //&& setSlotPacket.getSlot() != 0
            if (containerManager.getOpenedContainer(serverCookiePlayer).getWindowType() == 12) {
                packetCraftingEvent.prepareCraft(serverCookiePlayer);
            }

            setSlotEvent.compactItems(serverCookiePlayer);
            return;
        }

        if (event.getPacketType() == PacketType.Play.Server.ENTITY_STATUS) {
            packetEatingEvent.onEat(new WrapperPlayServerEntityStatus(event), serverCookiePlayer);
        }


    }
}
