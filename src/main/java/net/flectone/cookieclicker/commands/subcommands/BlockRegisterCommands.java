package net.flectone.cookieclicker.commands.subcommands;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.BlockPositionResolver;
import io.papermc.paper.math.BlockPosition;
import lombok.RequiredArgsConstructor;
import net.flectone.cookieclicker.gameplay.blockbreaking.data.BlockType;
import net.flectone.cookieclicker.utility.config.RegisteredBlocks;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

import java.util.Arrays;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @__(@Inject))
public class BlockRegisterCommands {

    private static final Component INCORRECT_BLOCK_TYPE = Component.text("Такого блока не существует").color(TextColor.color(11213338));
    private static final Component SUCCESSFUL_REGISTER = Component.text("Блок записан").color(TextColor.color(16572593));
    public static final Component SUCCESSFUL_UNREGISTER = Component.text("Блок удалён").color(TextColor.color(16572593));

    private final RegisteredBlocks registeredBlocks;

    public LiteralCommandNode<CommandSourceStack> createBlocksCommand() {
        LiteralArgumentBuilder<CommandSourceStack> blockRegistry = Commands.literal("blocks");
        blockRegistry.requires(sender -> sender.getSender().isOp());
        blockRegistry.then(createRegisterCommand());
        blockRegistry.then(createUnregisterCommand());

        return blockRegistry.build();
    }

    public LiteralCommandNode<CommandSourceStack> createRegisterCommand() {
        LiteralArgumentBuilder<CommandSourceStack> register = Commands.literal("register");
        register.then(Commands.argument("blockpos", ArgumentTypes.blockPosition())
                .then(Commands.argument("blocktype", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            Arrays.stream(BlockType.values()).forEach(blockType -> builder.suggest(blockType.toString()));
                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            // Для координат
                            BlockPositionResolver blockPositionResolver = ctx.getArgument("blockpos", BlockPositionResolver.class);
                            BlockPosition blockPosition = blockPositionResolver.resolve(ctx.getSource());

                            try {
                                // Тип блока, если правильно указан
                                BlockType blockType = BlockType.valueOf(ctx.getArgument("blocktype", String.class));

                                registeredBlocks.addBlock(blockPosition.x(), blockPosition.y(), blockPosition.z(),
                                        ctx.getSource().getLocation().getWorld().getKey().asString(), blockType);

                                ctx.getSource().getSender().sendMessage(SUCCESSFUL_REGISTER);
                                registeredBlocks.save();
                            } catch (IllegalArgumentException e) {
                                ctx.getSource().getSender().sendMessage(INCORRECT_BLOCK_TYPE);
                                return 0;
                            }
                            return Command.SINGLE_SUCCESS;
                        }))
        );
        return register.build();
    }

    public LiteralCommandNode<CommandSourceStack> createUnregisterCommand() {
        LiteralArgumentBuilder<CommandSourceStack> unregister = Commands.literal("unregister");
        // Удаление с указанием координат
        unregister.then(Commands.literal("position")
                .then(Commands.argument("blockpos", ArgumentTypes.blockPosition())
                        .executes(ctx -> {
                            BlockPositionResolver blockPositionResolver = ctx.getArgument("blockpos", BlockPositionResolver.class);
                            BlockPosition blockPosition = blockPositionResolver.resolve(ctx.getSource());

                            registeredBlocks.removeBlock(blockPosition.x(), blockPosition.y(), blockPosition.z(),
                                    ctx.getSource().getLocation().getWorld().getKey().asString());

                            ctx.getSource().getSender().sendMessage(SUCCESSFUL_UNREGISTER);
                            return Command.SINGLE_SUCCESS;
                        })));
        // Удаление с выбором записанных блоков
        unregister.then(Commands.literal("direct")
                .then(Commands.argument("packed", StringArgumentType.greedyString())
                        .suggests((ctx, builder) -> {
                            registeredBlocks.getAll().forEach(builder::suggest);
                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            registeredBlocks.removeBlock(ctx.getArgument("packed", String.class));

                            ctx.getSource().getSender().sendMessage(SUCCESSFUL_UNREGISTER);
                            return Command.SINGLE_SUCCESS;
                        })));

        return unregister.build();
    }
}


