package net.flectone.cookieclicker.gameplay.cookiepart;

import com.github.retrooper.packetevents.protocol.world.Location;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.entities.playerdata.ServerCookiePlayer;
import net.flectone.cookieclicker.entities.ConnectedPlayers;
import net.flectone.cookieclicker.items.ItemsRegistry;
import net.flectone.cookieclicker.items.itemstacks.base.data.Features;
import net.flectone.cookieclicker.items.itemstacks.base.data.ItemTag;
import net.flectone.cookieclicker.utility.PacketUtils;
import net.flectone.cookieclicker.utility.config.CookieClickerConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Optional;

@Singleton
public class BookshelfClickLogic {

    private final ItemsRegistry itemsRegistry;
    private final ConnectedPlayers connectedPlayers;
    private final PacketUtils packetUtils;

    private final CookieClickerConfig config;

    @Inject
    public BookshelfClickLogic(ItemsRegistry itemsRegistry, ConnectedPlayers connectedPlayers,
                               PacketUtils packetUtils, CookieClickerConfig config) {
        this.connectedPlayers = connectedPlayers;
        this.itemsRegistry = itemsRegistry;
        this.packetUtils = packetUtils;
        this.config = config;
    }

    public void bookShelfClick(ServerCookiePlayer serverCookiePlayer) {
        Player player = serverCookiePlayer.getPlayer();

        ItemStack enchantedCookiesInHand = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (new Features(enchantedCookiesInHand).getItemTag() != ItemTag.ENCHANTED_COOKIE) return;
        if (enchantedCookiesInHand.getCount() < config.getCookieBoostCost()) return;

        HitResult hitResult = player.getRayTrace(5, ClipContext.Fluid.NONE);
        if (!hitResult.getType().equals(HitResult.Type.BLOCK)) return;
        BlockHitResult blockHitResult = (BlockHitResult) hitResult;
        //книжная полка, координаты и blockstate
        BlockPos shelfPos = blockHitResult.getBlockPos();

        Level level = player.level();

        BlockState bookshelfBlockState = level.getBlockState(shelfPos);

        if (!bookshelfBlockState.getBlock().asItem().equals(Items.CHISELED_BOOKSHELF)) return;

        spawnEnchantedBook(serverCookiePlayer, shelfPos, bookshelfBlockState, level);
    }

    private void spawnEnchantedBook(ServerCookiePlayer serverCookiePlayer, BlockPos shelfPos, BlockState blockState, Level level) {
        ItemStack inHand = serverCookiePlayer.getPlayer().getMainHandItem();

        double x = shelfPos.getX() + 0.5;
        double y = shelfPos.getY() + 0.5;
        double z = shelfPos.getZ() + 0.5;

        // facing вроде бы всегда первый в списке
        EnumProperty<?> facingProperty = (EnumProperty<?>) level.getBlockState(shelfPos).getProperties().toArray()[0];

        Optional<?> facing = blockState.getOptionalValue(facingProperty);
        if (facing.isEmpty()) return;
        // проверка, куда смотрит блок, чтобы спереди призывать предмет
        switch (facing.get().toString()) {
            case "north" -> z--;
            case "south" -> z++;
            case "west" -> x--;
            case "east" -> x++;
            default -> {
                return;
            }
        }

        Location bookLocation = new Location(x, y, z, 1, 1);
        //spawning item
        serverCookiePlayer.getUser()
                .sendMessage(MiniMessage.miniMessage().deserialize("<#f4a91c>\uD83C\uDF6A <#f7f4b5>Вы купили книгу!"));
        packetUtils.spawnItem(serverCookiePlayer, bookLocation, itemsRegistry.get(ItemTag.BOOK_COOKIE_BOOST));
        inHand.setCount(inHand.getCount() - config.getCookieBoostCost());

        connectedPlayers.save(serverCookiePlayer, true);
    }
}
