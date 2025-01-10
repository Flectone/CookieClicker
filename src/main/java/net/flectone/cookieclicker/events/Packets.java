package net.flectone.cookieclicker.events;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenWindow;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.CompactItems;
import net.flectone.cookieclicker.cookiePart.CookiePartBase;
import net.flectone.cookieclicker.inventories.ClickerContainer;
import net.flectone.cookieclicker.inventories.ContainerManager;
import net.flectone.cookieclicker.inventories.Shops;
import net.flectone.cookieclicker.items.ItemManager;
import net.flectone.cookieclicker.utility.CCConversionUtils;
import net.flectone.cookieclicker.utility.PacketUtils;
import net.flectone.cookieclicker.utility.UtilsCookie;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

@Singleton
public class Packets implements PacketListener {

    private final UtilsCookie utilsCookie;
    private final CompactItems compact;
    private final ItemManager manager;
    private final PacketUtils packetUtils;
    private final CookiePartBase cookiePartBase;
    private final ContainerManager containerManager;
    private final CCConversionUtils conversionUtils;
    private final Shops shops;

    @Inject
    public Packets(UtilsCookie utilsCookie, CompactItems compact, ItemManager manager, PacketUtils packetUtils,
                   CookiePartBase cookiePartBase, ContainerManager containerManager, CCConversionUtils conversionUtils,
                   Shops shops) {
        this.utilsCookie = utilsCookie;
        this.compact = compact;
        this.manager = manager;
        this.packetUtils = packetUtils;
        this.cookiePartBase = cookiePartBase;
        this.containerManager = containerManager;
        this.conversionUtils = conversionUtils;
        this.shops = shops;
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getUser() == null) return;
        Player player = conversionUtils.userToNMS(event.getUser());

        switch (event.getPacketType()) {
            case PacketType.Play.Client.CLICK_WINDOW -> {
                WrapperPlayClientClickWindow clickWindowPacket = new WrapperPlayClientClickWindow(event);
                manageWindow(event.getUser(), player, clickWindowPacket);
            }
            case PacketType.Play.Client.CLOSE_WINDOW -> {
                containerManager.closeContainer(event.getUser());
            }
            case PacketType.Play.Client.ANIMATION -> {
                cookiePartBase.changeLegendaryHoeMode(event.getUser());
            }
            case PacketType.Play.Client.INTERACT_ENTITY -> {
                if (manageInteract(event.getUser(), player, new WrapperPlayClientInteractEntity(event)))
                    event.setCancelled(true);
            }
            case PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT -> {
                bookShelfClick(event.getUser());
            }
            default -> {
                return;
            }
        }

    }

    private boolean manageInteract(User user, Player player, WrapperPlayClientInteractEntity interactPacket) {
        if (interactPacket.getAction() != WrapperPlayClientInteractEntity.InteractAction.INTERACT) return false;
        EntityHitResult res = player.getTargetEntity(5);

        boolean triggered = false;

        if (res == null) return false;
        if ((res.getEntity() instanceof ItemFrame itemFrame)) {
            if (!itemFrame.getItem().getItem().equals(Items.COOKIE)) return false;
            cookiePartBase.cookieClickPacketEvent(user, itemFrame);
            triggered = true;
        }
        if ((res.getEntity() instanceof Villager villager) && villager.getVillagerData().getProfession().equals(VillagerProfession.FLETCHER)) {
            shops.openCookiesShop(user, player);
            triggered = true;
        }
        return triggered;
    }

    private void manageWindow(User user, Player player, WrapperPlayClientClickWindow clickPacket) {
        if (clickPacket.getSlot() == -999) return;
        ClickerContainer container = containerManager.getOpenedContainer(user);
        switch (container.getWindowType()) {
            //наковальня
            case 8 -> containerManager.anvilClick(player, clickPacket.getSlot());
            //инвентари 9 * x
            case 1, 2, 3, 4, 5 -> manageContainers(user, container, clickPacket);
            //верстак
            case 12 -> {return;}
        }
    }

    private void manageContainers(User user, ClickerContainer container, WrapperPlayClientClickWindow clickPacket) {
        switch (container.getCustomData()) {
            case "trading_farm" -> shops.buyItemFarmer(user, conversionUtils.userToNMS(user), clickPacket);
            //потом для других инвентарей тут будет
        }
    }

    private void bookShelfClick(User user) {
        Player player = conversionUtils.userToNMS(user);
        ItemStack enchantedCookiesInHand = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (!utilsCookie.compare(enchantedCookiesInHand, manager.getNMS("ench_cookie"))) return;
        if (enchantedCookiesInHand.getCount() < 15) return;

        HitResult hitResult = player.getRayTrace(5, ClipContext.Fluid.NONE);
        if (!hitResult.getType().equals(HitResult.Type.BLOCK)) return;
        BlockHitResult blockHitResult = (BlockHitResult) hitResult;
        //книжная полка, координаты и blockstate
        BlockPos shelfPos = blockHitResult.getBlockPos();
        BlockState bookshelfBlockState = player.level().getBlockState(shelfPos);

        if (!bookshelfBlockState
                .getBlock().asItem().equals(Items.CHISELED_BOOKSHELF)) return;

        double x = shelfPos.getX() + 0.5;
        double y = shelfPos.getY() + 0.5;
        double z = shelfPos.getZ() + 0.5;
        //facing вроде бы всегда первая в списке
        EnumProperty<?> facingProperty = (EnumProperty<?>) player.level().getBlockState(shelfPos).getProperties().toArray()[0];

        if (bookshelfBlockState.getOptionalValue(facingProperty).isEmpty()) return;
        //проверка, куда смотрит блок, чтобы спереди призывать предмет
        switch (bookshelfBlockState.getOptionalValue(facingProperty).get().toString()) {
            case "north":
                z--;
                break;
            case "south":
                z++;
                break;
            case "west":
                x--;
                break;
            case "east":
                x++;
                break;
        }

        Location bookLocation = new Location(x, y, z, 1, 1);
        //spawning item
        user.sendMessage(MiniMessage.miniMessage().deserialize("<#f4a91c>\uD83C\uDF6A <#f7f4b5>Вы купили книгу!"));
        packetUtils.spawnItem(user, bookLocation, manager.getNMS("book_boost1"));
        enchantedCookiesInHand.setCount(enchantedCookiesInHand.getCount() - 15);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getUser() == null) return;
        Player player = conversionUtils.userToNMS(event.getUser());
        if (player == null) return;

        if (event.getPacketType() == PacketType.Play.Server.OPEN_WINDOW) {
            WrapperPlayServerOpenWindow openContainerPacket = new WrapperPlayServerOpenWindow(event);
            containerManager.setOpenedContainer(event.getUser(), openContainerPacket, "default");
        }
        if (event.getPacketType() == PacketType.Play.Server.SET_SLOT) {
            compact.compact(player.getInventory(), manager.getNMS("cookie"), manager.getNMS("ench_cookie"), 160);
            compact.compact(player.getInventory(), manager.getNMS("cocoa_beans"), manager.getNMS("ench_cocoa"), 320);
            compact.compact(player.getInventory(), manager.getNMS("wheat"), manager.getNMS("ench_wheat"), 160);
            return;
        }
    }
}
