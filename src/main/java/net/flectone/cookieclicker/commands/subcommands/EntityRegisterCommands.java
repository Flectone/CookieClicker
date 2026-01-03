package net.flectone.cookieclicker.commands.subcommands;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.EntitySelectorArgumentResolver;
import net.flectone.cookieclicker.gameplay.cookiepart.InteractionController;
import net.flectone.cookieclicker.items.TradesRegistry;
import net.flectone.cookieclicker.utility.config.RegisteredEntitiesConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Entity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Singleton
public class EntityRegisterCommands {

    private final TradesRegistry villagerTrades;
    private final InteractionController interactionController;

    @Inject
    public EntityRegisterCommands(TradesRegistry villagerTrades,
                                  InteractionController interactionController) {
        this.interactionController = interactionController;
        this.villagerTrades = villagerTrades;
    }

    public LiteralCommandNode<CommandSourceStack> createCookieEntityCommand() {
        LiteralArgumentBuilder<CommandSourceStack> ccEntityCommand = Commands.literal("entities");
        ccEntityCommand.requires(sender -> sender.getSender().isOp());
        ccEntityCommand.then(createEntityCommand("register", true));
        ccEntityCommand.then(createEntityCommand("unregister", false));
        ccEntityCommand.then(createAllEntityCommand());

        return ccEntityCommand.build();
    }

    private LiteralArgumentBuilder<CommandSourceStack> createAllEntityCommand() {
        return Commands.literal("list")
                .executes(ctx -> {
                    ctx.getSource().getSender().sendMessage("trading_farm: " + interactionController.getTradersFarmers());
                    ctx.getSource().getSender().sendMessage("trading_armorer: " + interactionController.getTradersArmorers());
                    ctx.getSource().getSender().sendMessage("item_frames: " + interactionController.getItemFrames());
                    return Command.SINGLE_SUCCESS;
                });
    }

    private LiteralArgumentBuilder<CommandSourceStack> createEntityCommand(String name, boolean register) {
        LiteralArgumentBuilder<CommandSourceStack> villager = Commands.literal("villager");
        RegisteredEntitiesConfig registeredEntitiesConfig = interactionController.getRegisteredEntities();

        villagerTrades.getAllTraders().keySet().forEach(tag -> villager.then(Commands.literal(tag)
                .then(Commands.argument("villagerArgument", ArgumentTypes.entity())
                        .executes(ctx -> {
                            EntitySelectorArgumentResolver entitySelectorArgumentResolver = ctx.getArgument("villagerArgument", EntitySelectorArgumentResolver.class);

                            List<Entity> entities = entitySelectorArgumentResolver.resolve(ctx.getSource());
                            Entity entity = entities.getFirst();

                            Set<String> addedEntities = switch (tag) {
                                case "trading_farm" -> registeredEntitiesConfig.getVillagers().getFarmers();
                                case "trading_armorer" -> registeredEntitiesConfig.getVillagers().getArmorers();
                                default -> new HashSet<>();
                            };

                            if (register) {
                                ctx.getSource().getSender().sendMessage(MiniMessage.miniMessage()
                                        .deserialize("registered entity with id " + entity.getEntityId()));
                                addedEntities.add(entity.getUniqueId().toString());
                            } else {
                                if (!addedEntities.contains(entity.getUniqueId().toString()))
                                    return 0;
                                addedEntities.remove(entity.getUniqueId().toString());
                                ctx.getSource().getSender().sendMessage(MiniMessage.miniMessage()
                                        .deserialize("unregistered entity with id " + entity.getEntityId()));
                            }

                            registeredEntitiesConfig.save();

                            return Command.SINGLE_SUCCESS;
                        }))
        ));

        //Рамка
        LiteralArgumentBuilder<CommandSourceStack> itemFrames = Commands.literal("item_frame");
        itemFrames.then(Commands.argument("itemFrame", ArgumentTypes.entity())
                .executes(ctx -> {
                    EntitySelectorArgumentResolver entitySelectorArgumentResolver = ctx.getArgument("itemFrame", EntitySelectorArgumentResolver.class);

                    List<Entity> entities = entitySelectorArgumentResolver.resolve(ctx.getSource());
                    Entity entity = entities.getFirst();

                    Set<String> registeredFrames = registeredEntitiesConfig.getItemFrames();

                    if (register) {
                        ctx.getSource().getSender().sendMessage(MiniMessage.miniMessage()
                                .deserialize("registered entity with id " + entity.getEntityId()));
                        registeredFrames.add(entity.getUniqueId().toString());
                    } else {
                        if (!registeredFrames.contains(entity.getUniqueId().toString()))
                            return 0;
                        registeredFrames.remove(entity.getUniqueId().toString());
                        ctx.getSource().getSender().sendMessage(MiniMessage.miniMessage()
                                .deserialize("unregistered entity with id " + entity.getEntityId()));
                    }

                    registeredEntitiesConfig.save();

                    return Command.SINGLE_SUCCESS;
                }));

        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(name);
        root.then(villager);
        root.then(itemFrames);

        return root;
    }
}
