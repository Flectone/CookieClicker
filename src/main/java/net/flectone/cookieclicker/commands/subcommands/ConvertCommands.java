package net.flectone.cookieclicker.commands.subcommands;

import com.google.inject.Singleton;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.flectone.cookieclicker.items.itemstacks.GeneratedCookieItem;
import net.flectone.cookieclicker.utility.ConversionUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.bukkit.entity.Entity;

import java.util.List;

@Singleton
public class ConvertCommands {

    public LiteralCommandNode<CommandSourceStack> createCookieClickerConvert() {
        LiteralArgumentBuilder<CommandSourceStack> convert = Commands.literal("convert")
                .then(Commands.literal("mainhand")
                        .executes(ctx -> {
                            Player nmsExecutor = getPlayerExecutor(ctx.getSource().getExecutor());
                            if (nmsExecutor == null)
                                return 0;
                            nmsExecutor.displayClientMessage(Component.literal("Предмет в вашей руке был переделан"), false);

                            nmsExecutor.setItemInHand(InteractionHand.MAIN_HAND, convertItem(nmsExecutor.getMainHandItem(), nmsExecutor.getMainHandItem().getCount()));

                            return Command.SINGLE_SUCCESS;
                        }))
                .then(Commands.literal("inventory")
                        .executes(ctx -> {
                            Player nmsExecutor = getPlayerExecutor(ctx.getSource().getExecutor());
                            if (nmsExecutor == null)
                                return 0;

                            Inventory playerInventory = nmsExecutor.getInventory();
                            convertInventory(playerInventory);

                            nmsExecutor.displayClientMessage(Component.literal("Предметы в инвентаре обновлены"), false);
                            return Command.SINGLE_SUCCESS;
                        }));
        return convert.build();
    }

    private void convertInventory(Inventory inventory) {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack singleItem = inventory.getItem(i);
            if (singleItem.getItem() == Items.AIR)
                continue;

            inventory.setItem(i, convertItem(singleItem, singleItem.getCount()));
        }
    }

    private void convertInList(List<ItemStack> items) {
        for (int i = 0; i < items.size(); i++) {
            ItemStack singleItem = items.get(i);
            if (singleItem.getItem() == Items.AIR)
                continue;

            items.set(i, convertItem(singleItem, singleItem.getCount()));
        }
    }

    public Player getPlayerExecutor(Entity bukkitExecutor) {
        if (bukkitExecutor == null)
            return null;

        return ConversionUtils.getNMSplayerByUUID(bukkitExecutor.getUniqueId());
    }

    private ItemStack convertItem(ItemStack originalItem, Integer amount) {
        GeneratedCookieItem convertedCookieItem = GeneratedCookieItem.fromItemStack(originalItem);
        return convertedCookieItem.toMinecraftStack().copyWithCount(amount);
    }
}
