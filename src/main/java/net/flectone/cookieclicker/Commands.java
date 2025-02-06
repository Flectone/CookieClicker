package net.flectone.cookieclicker;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.flectone.cookieclicker.inventories.MainMenu;
import net.flectone.cookieclicker.utility.CCobjects.CookiePlayer;

@Singleton
public class Commands {
    private final MainMenu mainMenu;

    @Inject
    public Commands(MainMenu mainMenu) {
        this.mainMenu = mainMenu;
    }

    public LiteralCommandNode<CommandSourceStack> createOpenMenuCommand() {
        LiteralArgumentBuilder<CommandSourceStack> openMenu = io.papermc.paper.command.brigadier.Commands.literal("menu")
                .executes(ctx -> {
                    CookiePlayer cookiePlayer = new CookiePlayer(ctx.getSource().getExecutor().getUniqueId());

                    mainMenu.openMainMenu(cookiePlayer);
                    return Command.SINGLE_SUCCESS;
                });
        return openMenu.build();
    }

}
