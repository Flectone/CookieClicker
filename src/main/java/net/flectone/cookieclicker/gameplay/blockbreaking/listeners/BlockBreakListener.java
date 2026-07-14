package net.flectone.cookieclicker.gameplay.blockbreaking.listeners;

import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.commands.subcommands.BlockRegisterCommands;
import net.flectone.cookieclicker.entities.playerdata.ServerCookiePlayer;
import net.flectone.cookieclicker.eventdata.CookieEventHandler;
import net.flectone.cookieclicker.eventdata.CookieListener;
import net.flectone.cookieclicker.eventdata.events.ClickerAttackEntity;
import net.flectone.cookieclicker.eventdata.events.ClickerPlayerBreaking;
import net.flectone.cookieclicker.gameplay.blockbreaking.BlockHandler;
import net.flectone.cookieclicker.gameplay.blockbreaking.data.BlockType;
import net.flectone.cookieclicker.gameplay.cookiepart.StatisticDisplay;
import net.flectone.cookieclicker.items.itemstacks.base.CookieItems;
import net.flectone.cookieclicker.utility.config.RegisteredBlocks;
import net.flectone.cookieclicker.utility.data.Position;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

import java.util.Optional;

@Singleton
public class BlockBreakListener implements CookieListener {

    private final BlockHandler blockHandler;
    private final RegisteredBlocks registeredBlocks;
    private final StatisticDisplay statisticDisplay;

    @Inject
    public BlockBreakListener(BlockHandler blockHandler, RegisteredBlocks registeredBlocks, StatisticDisplay statisticDisplay) {
        this.blockHandler = blockHandler;
        this.registeredBlocks = registeredBlocks;
        this.statisticDisplay = statisticDisplay;
    }

    @CookieEventHandler
    public void onPlayerStartMine(ClickerPlayerBreaking event) {
        if (event.getDiggingAction() != DiggingAction.START_DIGGING) return;

        ServerCookiePlayer serverCookiePlayer = event.getCookiePlayer();
        Player player = serverCookiePlayer.getPlayer();

        // Проверка, держит ли игрок в руке инструмент
        // Если нет, то обрабатывать блок нет смысла
        if (!isToolUsed(player)) return;

        Position miningBlockPos = event.getBlockPos();

        Level level = player.level();
        String dimensionKey = level.dimension().identifier().toString();

        Optional<BlockType> blockType = registeredBlocks.getBlockType(miningBlockPos.getX(), miningBlockPos.getY(), miningBlockPos.getZ(), dimensionKey);

        if (blockType.isEmpty()) return;

        handleBlockInteract(serverCookiePlayer, dimensionKey, blockType.get(), event);
    }

    @CookieEventHandler
    public void onPlayerClick(ClickerAttackEntity event) {
        //if (event.getInteractAction() != WrapperPlayClientInteractEntity.InteractAction.ATTACK) return;
        
        blockHandler.dealDamage(event.getCookiePlayer(), event.getInteractedEntityId());
    }

    private void handleBlockInteract(ServerCookiePlayer serverCookiePlayer, String dimensionKey, BlockType blockType, ClickerPlayerBreaking event) {
        Player player = serverCookiePlayer.getPlayer();
        Position pos = event.getBlockPos();

        if (player.isCreative()) {
            registeredBlocks.removeBlock(pos.getX(), pos.getY(), pos.getZ(), dimensionKey);
            serverCookiePlayer.getUser().sendMessage(BlockRegisterCommands.SUCCESSFUL_UNREGISTER);
            return;
        }

        event.setCancelled(true);

        // Отображение статистики перед спавном блока
        statisticDisplay.displayMiningStatsOnStart(serverCookiePlayer, blockType);

        blockHandler.createBlock(serverCookiePlayer, blockType, event.getBlockPos(), event.getBlockFace());
    }

    private boolean isToolUsed(Player player) {
        ItemStack mainHandItem = player.getMainHandItem();

        if (mainHandItem.isEmpty()) return false;

        CustomData customDataTag = mainHandItem.get(DataComponents.CUSTOM_DATA);
        if (customDataTag == null) return false;

        return customDataTag.copyTag().contains(CookieItems.PLUGIN_KEY);
    }
}
