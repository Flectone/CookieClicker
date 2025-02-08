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
import net.flectone.cookieclicker.CompactItems;
import net.flectone.cookieclicker.inventories.ClickerContainer;
import net.flectone.cookieclicker.inventories.ContainerManager;
import net.flectone.cookieclicker.inventories.MainMenu;
import net.flectone.cookieclicker.inventories.Shops;
import net.flectone.cookieclicker.items.ItemManager;
import net.flectone.cookieclicker.utility.CCobjects.CookiePlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.EntityHitResult;

@Singleton
public class Packets implements PacketListener {

    private final CompactItems compact;
    private final ItemManager manager;
    private final PacketCookieClickEvent packetCookieClickEvent;
    private final PacketCraftingEvent packetCraftingEvent;
    private final PacketEatingEvent packetEatingEvent;
    private final ContainerManager containerManager;
    private final Shops shops;
    private final MainMenu mainMenu;

    @Inject
    public Packets(CompactItems compact, ItemManager manager, MainMenu mainMenu, PacketEatingEvent packetEatingEvent,
                   PacketCookieClickEvent packetCookieClickEvent, ContainerManager containerManager,
                   PacketCraftingEvent packetCraftingEvent, Shops shops) {
        this.compact = compact;
        this.manager = manager;
        this.packetCookieClickEvent = packetCookieClickEvent;
        this.packetEatingEvent = packetEatingEvent;
        this.containerManager = containerManager;
        this.packetCraftingEvent = packetCraftingEvent;
        this.shops = shops;
        this.mainMenu = mainMenu;
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
                if (manageInteract(cookiePlayer, new WrapperPlayClientInteractEntity(event)))
                    event.setCancelled(true);
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

    private boolean manageInteract(CookiePlayer cookiePlayer, WrapperPlayClientInteractEntity interactPacket) {
        if (interactPacket.getAction() != WrapperPlayClientInteractEntity.InteractAction.INTERACT) return false;
        packetCookieClickEvent.checkForBonus(cookiePlayer, interactPacket.getEntityId());

        EntityHitResult res = cookiePlayer.getPlayer().getTargetEntity(5);

        boolean triggered = false;

        if (res == null) return false;
        if ((res.getEntity() instanceof ItemFrame itemFrame)) {
            if (!itemFrame.getItem().getItem().equals(Items.COOKIE)) return false;
            packetCookieClickEvent.cookieClickPacketEvent(cookiePlayer.getUser(), itemFrame);
            triggered = true;
        }
        if ((res.getEntity() instanceof Villager villager) && villager.getVillagerData().getProfession().equals(VillagerProfession.FLETCHER)) {
            shops.openCookiesShop(cookiePlayer);
            triggered = true;
        }
        return triggered;
    }

    private void manageWindow(CookiePlayer cookiePlayer, WrapperPlayClientClickWindow clickPacket) {
        if (clickPacket.getSlot() == -999) return;
        ClickerContainer container = containerManager.getOpenedContainer(cookiePlayer.getUuid());

        switch (container.getWindowType()) {
            //наковальня
            case 8 -> containerManager.anvilClick(cookiePlayer.getPlayer(), clickPacket.getSlot());
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

        if (event.getPacketType() == PacketType.Play.Server.SET_SLOT) {
            WrapperPlayServerSetSlot setSlotPacket = new WrapperPlayServerSetSlot(event);

            if (containerManager.getOpenedContainer(cookiePlayer).getWindowType() == 12 && setSlotPacket.getSlot() != 0) {
                packetCraftingEvent.prepareCraft(cookiePlayer);
            }

            compact.compact(player.getInventory(), manager.getNMS("cookie"), manager.getNMS("ench_cookie"), 160);
            compact.compact(player.getInventory(), manager.getNMS("cocoa_beans"), manager.getNMS("ench_cocoa"), 320);
            compact.compact(player.getInventory(), manager.getNMS("wheat"), manager.getNMS("ench_wheat"), 160);
            return;
        }

        if (event.getPacketType() == PacketType.Play.Server.ENTITY_STATUS) {
            packetEatingEvent.onEat(new WrapperPlayServerEntityStatus(event), cookiePlayer);
        }


    }
}
