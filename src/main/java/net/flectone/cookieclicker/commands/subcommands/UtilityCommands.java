package net.flectone.cookieclicker.commands.subcommands;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.flectone.cookieclicker.entities.ConnectedPlayers;
import net.flectone.cookieclicker.entities.playerdata.ServerCookiePlayer;
import net.flectone.cookieclicker.gameplay.cookiepart.InteractionController;
import net.flectone.cookieclicker.inventories.MainMenu;

@Singleton
public class UtilityCommands {

    private final MainMenu mainMenu;
    private final ConnectedPlayers connectedPlayers;
    private final InteractionController interactionController;

    @Inject
    public UtilityCommands(InteractionController interactionController,
                           ConnectedPlayers connectedPlayers,
                           MainMenu mainMenu) {
        this.interactionController = interactionController;
        this.mainMenu = mainMenu;
        this.connectedPlayers = connectedPlayers;
    }

    public LiteralCommandNode<CommandSourceStack> createReloadCommand() {
        LiteralArgumentBuilder<CommandSourceStack> reloadCommand = Commands.literal("reload")
                .requires(sender -> sender.getSender().isOp())
                .executes(ctx -> {
                    interactionController.reloadEntitiesFromConfig();
                    return Command.SINGLE_SUCCESS;
                });

        return reloadCommand.build();
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
}
