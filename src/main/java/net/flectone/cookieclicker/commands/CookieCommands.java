package net.flectone.cookieclicker.commands;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import lombok.RequiredArgsConstructor;
import net.flectone.cookieclicker.commands.subcommands.*;
import net.flectone.cookieclicker.items.ItemsRegistry;
import net.flectone.cookieclicker.items.itemstacks.base.data.ItemTag;
import net.minecraft.world.entity.player.Player;

import java.util.Arrays;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @__(@Inject))
public class CookieCommands {

    private final ItemsRegistry itemsRegistry;

    private final ModifyCommands modifyCommands;
    private final UtilityCommands utilityCommands;
    private final EntityRegisterCommands entityRegisterCommands;
    private final BlockRegisterCommands blockRegisterCommands;
    private final ConvertCommands convertCommands;

    public LiteralCommandNode<CommandSourceStack> createCookieClickerCommand() {
        LiteralArgumentBuilder<CommandSourceStack> cookieClicker = Commands.literal("cookieclicker")
                .then(convertCommands.createCookieClickerConvert())
                .then(createGiveCommand())
                .then(modifyCommands.createModifyCommand())
                .then(utilityCommands.createReloadCommand())
                .then(Commands.literal("registry")
                        .requires(sender -> sender.getSender().isOp())
                        .then(entityRegisterCommands.createCookieEntityCommand())
                        .then(blockRegisterCommands.createBlocksCommand()));
        return cookieClicker.build();
    }

    public LiteralCommandNode<CommandSourceStack> createGiveCommand() {
        LiteralArgumentBuilder<CommandSourceStack> giveCommand = Commands.literal("give")
                .requires(sender -> sender.getSender().isOp())
                .then(Commands.argument("item_tag", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            Arrays.asList(ItemTag.values()).forEach(tag -> builder.suggest(tag.getRealTag()));
                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            Player nmsExecutor = convertCommands.getPlayerExecutor(ctx.getSource().getExecutor());
                            if (nmsExecutor == null)
                                return 0;

                            nmsExecutor.addItem(itemsRegistry.get(ItemTag.fromString(ctx.getArgument("item_tag", String.class))));
                            return Command.SINGLE_SUCCESS;
                        }));
        return giveCommand.build();
    }

}
