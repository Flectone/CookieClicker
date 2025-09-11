package net.flectone.cookieclicker.commands.subcommands;

import com.google.inject.Singleton;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.flectone.cookieclicker.items.attributes.CookieAbility;
import net.flectone.cookieclicker.items.attributes.StatType;
import net.flectone.cookieclicker.items.attributes.ToolType;
import net.flectone.cookieclicker.items.itemstacks.GeneratedCookieItem;
import net.flectone.cookieclicker.utility.ConversionUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.bukkit.entity.Entity;

import java.util.Arrays;

@Singleton
public class ModifyCommands {

    private static final Component UNABLE_TO_FIND = Component.literal("Такого параметра не существует").withColor(11213338);

    private static final Component SUCCESSFUL_RENAME = Component.literal("Предмет переименован").withColor(16572593);
    private static final Component SUCCESSFUL_STAT_CHANGE = Component.literal("Изменён стат ").withColor(16572593);
    private static final Component SUCCESSFUL_TOOL_CHANGE = Component.literal("Изменён тип предмета").withColor(16572593);
    private static final Component SUCCESSFUL_ABILITY_CHANGE = Component.literal("Изменена способность предмета").withColor(16572593);

    public LiteralCommandNode<CommandSourceStack> createModifyCommand() {
        LiteralArgumentBuilder<CommandSourceStack> modifyCommand = Commands.literal("modify")
                .requires(sender -> sender.getSender().isOp())
                .then(createStatsBranch())
                .then(createAbilityBranch())
                .then(createItemNameCommand())
                .then(createToolTypeBranch());

        return modifyCommand.build();
    }

    private LiteralCommandNode<CommandSourceStack> createStatsBranch() {
        LiteralArgumentBuilder<CommandSourceStack> statCommand = Commands.literal("stat")
                .requires(sender -> sender.getSender().isOp())
                .then(Commands.argument("item_stat", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            Arrays.asList(StatType.values()).forEach(stat -> builder.suggest(stat.toString()));
                            return builder.buildFuture();
                        })
                        .then(Commands.argument("value", IntegerArgumentType.integer())
                                .executes(ctx -> {
                                    Player nmsExecutor = getPlayerExecutor(ctx.getSource().getExecutor());
                                    if (nmsExecutor == null)
                                        return 0;

                                    ItemStack inHandMinecraftStack = nmsExecutor.getMainHandItem();
                                    if (inHandMinecraftStack.isEmpty()) return 0;

                                    try {
                                        StatType statType = StatType.valueOf(ctx.getArgument("item_stat", String.class));
                                        GeneratedCookieItem inHandCookieStack = GeneratedCookieItem.fromItemStack(inHandMinecraftStack);

                                        inHandCookieStack.setStat(statType, ctx.getArgument("value", Integer.class));

                                        nmsExecutor.setItemInHand(InteractionHand.MAIN_HAND, inHandCookieStack.toMinecraftStack());
                                        nmsExecutor.displayClientMessage(SUCCESSFUL_STAT_CHANGE.copy().append(statType.getName()), false);
                                        return Command.SINGLE_SUCCESS;

                                    } catch (IllegalArgumentException e) {
                                        nmsExecutor.displayClientMessage(UNABLE_TO_FIND, false);
                                        return 0;
                                    }
                                })));
        return statCommand.build();
    }

    private LiteralCommandNode<CommandSourceStack> createAbilityBranch() {
        LiteralArgumentBuilder<CommandSourceStack> abilityCommand = Commands.literal("ability")
                .requires(sender -> sender.getSender().isOp())
                .then(Commands.argument("item_ability", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            Arrays.asList(CookieAbility.values()).forEach(stat -> builder.suggest(stat.toString()));
                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            Player nmsExecutor = getPlayerExecutor(ctx.getSource().getExecutor());
                            if (nmsExecutor == null)
                                return 0;

                            ItemStack inHandMinecraftStack = nmsExecutor.getMainHandItem();
                            if (inHandMinecraftStack.isEmpty()) return 0;

                            try {
                                CookieAbility ability = CookieAbility.valueOf(ctx.getArgument("item_ability", String.class));
                                GeneratedCookieItem inHandCookieStack = GeneratedCookieItem.fromItemStack(inHandMinecraftStack);

                                inHandCookieStack.setAbility(ability);

                                nmsExecutor.setItemInHand(InteractionHand.MAIN_HAND, inHandCookieStack.toMinecraftStack());
                                nmsExecutor.displayClientMessage(SUCCESSFUL_ABILITY_CHANGE, false);
                                return Command.SINGLE_SUCCESS;

                            } catch (IllegalArgumentException e) {
                                nmsExecutor.displayClientMessage(UNABLE_TO_FIND, false);
                                return 0;
                            }
                        }));
        return abilityCommand.build();
    }

    private LiteralCommandNode<CommandSourceStack> createToolTypeBranch() {
        LiteralArgumentBuilder<CommandSourceStack> abilityCommand = Commands.literal("tooltype")
                .requires(sender -> sender.getSender().isOp())
                .then(Commands.argument("tool", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            Arrays.asList(ToolType.values()).forEach(stat -> builder.suggest(stat.toString()));
                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            Player nmsExecutor = getPlayerExecutor(ctx.getSource().getExecutor());
                            if (nmsExecutor == null)
                                return 0;

                            ItemStack inHandMinecraftStack = nmsExecutor.getMainHandItem();
                            if (inHandMinecraftStack.isEmpty()) return 0;

                            try {
                                ToolType toolType = ToolType.valueOf(ctx.getArgument("tool", String.class));
                                GeneratedCookieItem inHandCookieStack = GeneratedCookieItem.fromItemStack(inHandMinecraftStack);

                                inHandCookieStack.setToolType(toolType);

                                nmsExecutor.setItemInHand(InteractionHand.MAIN_HAND, inHandCookieStack.toMinecraftStack());
                                nmsExecutor.displayClientMessage(SUCCESSFUL_TOOL_CHANGE, false);
                                return Command.SINGLE_SUCCESS;

                            } catch (IllegalArgumentException e) {
                                nmsExecutor.displayClientMessage(UNABLE_TO_FIND, false);
                                return 0;
                            }
                        }));
        return abilityCommand.build();
    }

    private LiteralCommandNode<CommandSourceStack> createItemNameCommand() {
        LiteralArgumentBuilder<CommandSourceStack> itemNameCommand = Commands.literal("displayname")
                .requires(sender -> sender.getSender().isOp())
                .then(Commands.argument("name", StringArgumentType.greedyString())
                        .suggests((ctx, builder) -> {
                            Arrays.asList(ToolType.values()).forEach(stat -> builder.suggest(stat.toString()));
                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            Player nmsExecutor = getPlayerExecutor(ctx.getSource().getExecutor());
                            if (nmsExecutor == null)
                                return 0;

                            ItemStack inHandMinecraftStack = nmsExecutor.getMainHandItem();
                            if (inHandMinecraftStack.isEmpty()) return 0;

                            GeneratedCookieItem inHandCookieStack = GeneratedCookieItem.fromItemStack(inHandMinecraftStack);

                            inHandCookieStack.setName(ctx.getArgument("name", String.class));

                            nmsExecutor.displayClientMessage(SUCCESSFUL_RENAME, false);
                            nmsExecutor.setItemInHand(InteractionHand.MAIN_HAND, inHandCookieStack.toMinecraftStack());

                            return Command.SINGLE_SUCCESS;
                        }));
        return itemNameCommand.build();
    }

    private Player getPlayerExecutor(Entity bukkitExecutor) {
        if (bukkitExecutor == null)
            return null;

        return ConversionUtils.getNMSplayerByUUID(bukkitExecutor.getUniqueId());
    }
}
