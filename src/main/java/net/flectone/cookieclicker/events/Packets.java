package net.flectone.cookieclicker.events;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientAnimation;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenWindow;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.ClickerContainer;
import net.flectone.cookieclicker.CompactItems;
import net.flectone.cookieclicker.PacketUtils;
import net.flectone.cookieclicker.cookiePart.BagHoeUpgrade;
import net.flectone.cookieclicker.cookiePart.CookiePartBase;
import net.flectone.cookieclicker.items.ItemManager;
import net.flectone.cookieclicker.utility.UtilsCookie;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Singleton
public class Packets implements PacketListener {
    public static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    private final UtilsCookie utilsCookie;
    private final CompactItems compact;
    private final ItemManager manager;
    private final PacketUtils packetUtils;
    private final CookiePartBase cookiePartBase;
    private final BagHoeUpgrade bagHoeUpgrade;

    @Inject
    public Packets(UtilsCookie utilsCookie, CompactItems compact, ItemManager manager, PacketUtils packetUtils,
                   CookiePartBase cookiePartBase, BagHoeUpgrade bagHoeUpgrade) {
        this.utilsCookie = utilsCookie;
        this.compact = compact;
        this.manager = manager;
        this.packetUtils = packetUtils;
        this.cookiePartBase = cookiePartBase;
        this.bagHoeUpgrade = bagHoeUpgrade;
    }

    //короче я только начал тут что-то писать, поэтому немного кринж


    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getUser() == null) return;
        Player player = packetUtils.userToNMS(event.getUser());
        if (player == null) return;
        if (event.getPacketType() == PacketType.Play.Client.CLIENT_TICK_END) return;
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_POSITION) return;
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_ROTATION) return;
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION) return;
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_INPUT) return;
        //debug, потом уберу
        if (player.getItemInHand(InteractionHand.OFF_HAND).getItem().equals(Items.RED_DYE)) {
            event.getUser().sendMessage(String.valueOf(event.getPacketType()) + " " + String.valueOf(event.getPacketId()));
        }

//        if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW) {
//            WrapperPlayClientClickWindow clickWindowPacket = new WrapperPlayClientClickWindow(event);
//        }

        if (event.getPacketType() == PacketType.Play.Client.ANIMATION) {
            WrapperPlayClientAnimation animationPacket = new WrapperPlayClientAnimation(event);
            bagHoeUpgrade.LegHoeChange(event.getUser());
        }
        if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            WrapperPlayClientInteractEntity interactPacket = new WrapperPlayClientInteractEntity(event);
            if (interactPacket.getAction() != WrapperPlayClientInteractEntity.InteractAction.INTERACT) return;
            EntityHitResult res = player.getTargetEntity(5);

            if (res == null || !(res.getEntity() instanceof ItemFrame itemFrame)) return;
            if (!itemFrame.getItem().getItem().equals(Items.COOKIE)) return;
            cookiePartBase.cookieClickPacketEvent(event.getUser(), itemFrame);
            event.setCancelled(true);
        }
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
            bookShelfClick(event.getUser());
        }

    }


    public void bookShelfClick(User user) {
        Player player = packetUtils.userToNMS(user);
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
        Player player = packetUtils.userToNMS(event.getUser());
        if (player == null) return;
        if (event.getPacketType() == PacketType.Play.Server.SYSTEM_CHAT_MESSAGE) return;
        if (event.getPacketType() == PacketType.Play.Server.ENTITY_POSITION_SYNC) return;
        if (event.getPacketType() == PacketType.Play.Server.ENTITY_RELATIVE_MOVE) return;
        if (event.getPacketType() == PacketType.Play.Server.ENTITY_VELOCITY) return;
        if (event.getPacketType() == PacketType.Play.Server.ENTITY_HEAD_LOOK) return;
        if (event.getPacketType() == PacketType.Play.Server.ENTITY_RELATIVE_MOVE_AND_ROTATION) return;
        if (event.getPacketType() == PacketType.Play.Server.ENTITY_METADATA) return;
        if (event.getPacketType() == PacketType.Play.Server.OPEN_WINDOW) {
            WrapperPlayServerOpenWindow openContainerPacket = new WrapperPlayServerOpenWindow(event);
            ClickerContainer container = new ClickerContainer(openContainerPacket.getContainerId(), openContainerPacket.getType(), "default");

        }

        if (event.getPacketType() == PacketType.Play.Server.SET_SLOT) {
            compact.compact(player.getInventory(), manager.getNMS("cookie"), manager.getNMS("ench_cookie"), 160);
            compact.compact(player.getInventory(), manager.getNMS("cocoa_beans"), manager.getNMS("ench_cocoa"), 320);
            compact.compact(player.getInventory(), manager.getNMS("wheat"), manager.getNMS("ench_wheat"), 160);
            return;
        }
        if (player.getItemInHand(InteractionHand.OFF_HAND).getItem().equals(Items.GREEN_DYE)) {
            event.getUser().sendMessage(String.valueOf(event.getPacketType()) + " " + String.valueOf(event.getPacketId()));
        }
    }
}
