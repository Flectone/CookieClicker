package net.flectone.cookieclicker;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.EntitySelectorArgumentResolver;
import net.flectone.cookieclicker.events.PacketInteractEvent;
import net.flectone.cookieclicker.inventories.MainMenu;
import net.flectone.cookieclicker.items.VillagerTrades;
import net.flectone.cookieclicker.utility.CCobjects.CookiePlayer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Entity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Singleton
public class RegisteredCommands {
    private final MainMenu mainMenu;
    private final VillagerTrades villagerTrades;
    private final PacketInteractEvent packetInteractEvent;

    @Inject
    public RegisteredCommands(MainMenu mainMenu, VillagerTrades villagerTrades, PacketInteractEvent packetInteractEvent) {
        this.mainMenu = mainMenu;
        this.villagerTrades = villagerTrades;
        this.packetInteractEvent = packetInteractEvent;
    }

    public LiteralCommandNode<CommandSourceStack> createOpenMenuCommand() {
        LiteralArgumentBuilder<CommandSourceStack> openMenu = Commands.literal("menu")
                .executes(ctx -> {
                    CookiePlayer cookiePlayer = new CookiePlayer(ctx.getSource().getExecutor().getUniqueId());

                    mainMenu.openMainMenu(cookiePlayer);
                    return Command.SINGLE_SUCCESS;
                });
        return openMenu.build();
    }

    public LiteralCommandNode<CommandSourceStack> createCookieEntityCommand() {
        LiteralArgumentBuilder<CommandSourceStack> ccEntityCommand = Commands.literal("cc_entity");
        ccEntityCommand.requires(sender -> sender.getSender().isOp());
        ccEntityCommand.then(createEntityCommand("register", true));
        ccEntityCommand.then(createEntityCommand("unregister", false));
        ccEntityCommand.then(createAllEntityCommand());
        ccEntityCommand.then(Commands.literal("reload")
                .executes(ctx -> {
                    packetInteractEvent.reloadEntitiesFromConfig();
                    return Command.SINGLE_SUCCESS;
                }));

        return ccEntityCommand.build();
    }

    private LiteralArgumentBuilder<CommandSourceStack> createAllEntityCommand() {
        LiteralArgumentBuilder<CommandSourceStack> allEntities = Commands.literal("all_entities")
                .executes(ctx -> {
                    ctx.getSource().getSender().sendMessage("trading_farm: " + packetInteractEvent.getTradingFarm());
                    ctx.getSource().getSender().sendMessage("trading_armorer: " + packetInteractEvent.getTradingArmorer());
                    ctx.getSource().getSender().sendMessage("item_frames: " + packetInteractEvent.getItemFrames());
                    return Command.SINGLE_SUCCESS;
                });
        return allEntities;
    }

    private LiteralArgumentBuilder<CommandSourceStack> createEntityCommand(String name, boolean register) {
        LiteralArgumentBuilder<CommandSourceStack> villager = Commands.literal("villager");
        RegisteredEntitiesConfig registeredEntitiesConfig = packetInteractEvent.getRegisteredEntitiesConfig();

        villagerTrades.getAllTraders().keySet().forEach(tag -> {
            villager.then(Commands.literal(tag)
                            .then(Commands.argument("entity", ArgumentTypes.entity())
                                    .executes(ctx -> {
                                        EntitySelectorArgumentResolver entitySelectorArgumentResolver = ctx.getArgument("entity", EntitySelectorArgumentResolver.class);

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
            );
        });

        //Рамка
        LiteralArgumentBuilder<CommandSourceStack> itemFrames = Commands.literal("item_frame");
        itemFrames.then(Commands.argument("entity", ArgumentTypes.entity())
                .executes(ctx -> {
                    EntitySelectorArgumentResolver entitySelectorArgumentResolver = ctx.getArgument("entity", EntitySelectorArgumentResolver.class);

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
    };

}
