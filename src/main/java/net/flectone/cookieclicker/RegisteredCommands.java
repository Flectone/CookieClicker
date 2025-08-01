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
import net.flectone.cookieclicker.events.ConnectedPlayers;
import net.flectone.cookieclicker.events.PacketInteractAtEntityEvent;
import net.flectone.cookieclicker.inventories.MainMenu;
import net.flectone.cookieclicker.items.VillagerTrades;
import net.flectone.cookieclicker.items.itemstacks.GeneratedCookieItem;
import net.flectone.cookieclicker.playerdata.ServerCookiePlayer;
import net.flectone.cookieclicker.utility.CCConversionUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.bukkit.entity.Entity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Singleton
public class RegisteredCommands {
    private final MainMenu mainMenu;
    private final VillagerTrades villagerTrades;
    private final PacketInteractAtEntityEvent packetInteractAtEntityEvent;
    private final CCConversionUtils convertUtils;
    private final ConnectedPlayers connectedPlayers;

    @Inject
    public RegisteredCommands(MainMenu mainMenu, VillagerTrades villagerTrades, PacketInteractAtEntityEvent packetInteractAtEntityEvent,
                              CCConversionUtils convertUtils, ConnectedPlayers connectedPlayers) {
        this.mainMenu = mainMenu;
        this.villagerTrades = villagerTrades;
        this.packetInteractAtEntityEvent = packetInteractAtEntityEvent;
        this.convertUtils = convertUtils;
        this.connectedPlayers = connectedPlayers;
    }

    public LiteralCommandNode<CommandSourceStack> createCookieClickerCommand() {
        LiteralArgumentBuilder<CommandSourceStack> cookieClicker = Commands.literal("cookieclicker2")
                .then(createCookieClickerConvert())
                .then(createCookieEntityCommand());
        return cookieClicker.build();
    }

    private Player getPlayerExecutor(Entity bukkitExecutor) {
        if (bukkitExecutor == null)
            return null;

        return convertUtils.getNMSplayerByUUID(bukkitExecutor.getUniqueId());
    }

    private ItemStack convertItem(ItemStack originalItem, Integer amount) {
        GeneratedCookieItem convertedCookieItem = GeneratedCookieItem.fromItemStack(originalItem);
        return convertedCookieItem.toMinecraftStack().copyWithCount(amount);
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

    public LiteralCommandNode<CommandSourceStack> createOpenMenuCommand() {
        LiteralArgumentBuilder<CommandSourceStack> openMenu = Commands.literal("menu")
                .executes(ctx -> {
                    ServerCookiePlayer serverCookiePlayer = connectedPlayers.getServerCookiePlayer(ctx.getSource().getExecutor().getUniqueId());

                    mainMenu.openMainMenu(serverCookiePlayer);
                    return Command.SINGLE_SUCCESS;
                });
        return openMenu.build();
    }

    public LiteralCommandNode<CommandSourceStack> createCookieEntityCommand() {
        LiteralArgumentBuilder<CommandSourceStack> ccEntityCommand = Commands.literal("entities");
        ccEntityCommand.requires(sender -> sender.getSender().isOp());
        ccEntityCommand.then(createEntityCommand("register", true));
        ccEntityCommand.then(createEntityCommand("unregister", false));
        ccEntityCommand.then(createAllEntityCommand());
        ccEntityCommand.then(Commands.literal("reload")
                .executes(ctx -> {
                    packetInteractAtEntityEvent.reloadEntitiesFromConfig();
                    return Command.SINGLE_SUCCESS;
                }));

        return ccEntityCommand.build();
    }

    private LiteralArgumentBuilder<CommandSourceStack> createAllEntityCommand() {
        return Commands.literal("all_entities")
                .executes(ctx -> {
                    ctx.getSource().getSender().sendMessage("trading_farm: " + packetInteractAtEntityEvent.getTradingFarm());
                    ctx.getSource().getSender().sendMessage("trading_armorer: " + packetInteractAtEntityEvent.getTradingArmorer());
                    ctx.getSource().getSender().sendMessage("item_frames: " + packetInteractAtEntityEvent.getItemFrames());
                    return Command.SINGLE_SUCCESS;
                });
    }

    private LiteralArgumentBuilder<CommandSourceStack> createEntityCommand(String name, boolean register) {
        LiteralArgumentBuilder<CommandSourceStack> villager = Commands.literal("villager");
        RegisteredEntitiesConfig registeredEntitiesConfig = packetInteractAtEntityEvent.getRegisteredEntitiesConfig();

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
