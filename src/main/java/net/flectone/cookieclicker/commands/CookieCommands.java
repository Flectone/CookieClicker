package net.flectone.cookieclicker.commands;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.flectone.cookieclicker.commands.subcommands.ConvertCommands;
import net.flectone.cookieclicker.commands.subcommands.EntityRegisterCommands;
import net.flectone.cookieclicker.commands.subcommands.ModifyCommands;
import net.flectone.cookieclicker.commands.subcommands.UtilityCommands;
import net.flectone.cookieclicker.items.ItemsRegistry;
import net.flectone.cookieclicker.items.itemstacks.base.data.ItemTag;
import net.minecraft.world.entity.player.Player;

import java.util.Arrays;

@Singleton
public class CookieCommands {

    private final ItemsRegistry itemsRegistry;

    private final ModifyCommands modifyCommands;
    private final UtilityCommands utilityCommands;
    private final EntityRegisterCommands entityRegisterCommands;
    private final ConvertCommands convertCommands;

    @Inject
    public CookieCommands(ItemsRegistry itemsRegistry,
                          ModifyCommands modifyCommands,
                          EntityRegisterCommands entityRegisterCommands,
                          UtilityCommands utilityCommands,
                          ConvertCommands convertCommands) {
        this.itemsRegistry = itemsRegistry;
        this.modifyCommands = modifyCommands;
        this.utilityCommands = utilityCommands;
        this.entityRegisterCommands = entityRegisterCommands;
        this.convertCommands = convertCommands;
    }

    public LiteralCommandNode<CommandSourceStack> createCookieClickerCommand() {
        LiteralArgumentBuilder<CommandSourceStack> cookieClicker = Commands.literal("cookieclicker")
                .then(convertCommands.createCookieClickerConvert())
                .then(entityRegisterCommands.createCookieEntityCommand())
                .then(createGiveCommand())
                .then(modifyCommands.createModifyCommand())
                .then(utilityCommands.createReloadCommand());
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
