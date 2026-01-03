package net.flectone.cookieclicker.gameplay.cookiepart;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.entities.playerdata.ServerCookiePlayer;
import net.flectone.cookieclicker.inventories.Shops;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

@Singleton
public class BookshelfClickLogic {

    private final Shops shops;

    @Inject
    public BookshelfClickLogic(Shops shops) {
        this.shops = shops;
    }

    public void bookShelfClick(ServerCookiePlayer serverCookiePlayer) {
        Player player = serverCookiePlayer.getPlayer();

        //ItemStack enchantedCookiesInHand = player.getItemInHand(InteractionHand.MAIN_HAND);
        //if (new Features(enchantedCookiesInHand).getItemTag() != ItemTag.ENCHANTED_COOKIE) return;
        //if (enchantedCookiesInHand.getCount() < config.getCookieBoostCost()) return;

        HitResult hitResult = player.getRayTrace(5, ClipContext.Fluid.NONE);
        if (!hitResult.getType().equals(HitResult.Type.BLOCK)) return;
        BlockHitResult blockHitResult = (BlockHitResult) hitResult;
        //книжная полка, координаты и blockstate
        BlockPos shelfPos = blockHitResult.getBlockPos();

        Level level = player.level();

        BlockState bookshelfBlockState = level.getBlockState(shelfPos);

        if (!bookshelfBlockState.getBlock().asItem().equals(Items.CHISELED_BOOKSHELF)) return;

        serverCookiePlayer.swingArm();
        shops.openSpecialShop(serverCookiePlayer, "bookshelf");


        //spawnEnchantedBook(serverCookiePlayer, shelfPos, bookshelfBlockState, level);
    }
}
