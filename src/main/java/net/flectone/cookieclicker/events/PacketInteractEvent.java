package net.flectone.cookieclicker.events;

import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.inventories.ContainerManager;
import net.flectone.cookieclicker.inventories.containers.ItemStorage;
import net.flectone.cookieclicker.inventories.MainMenu;
import net.flectone.cookieclicker.items.attributes.StatType;
import net.flectone.cookieclicker.items.attributes.ToolType;
import net.flectone.cookieclicker.items.itemstacks.base.data.Features;
import net.flectone.cookieclicker.entities.playerdata.ServerCookiePlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;

@Singleton
public class PacketInteractEvent {
    private final MainMenu mainMenu;
    private final ContainerManager containerManager;

    @Inject
    public PacketInteractEvent(MainMenu mainMenu, ContainerManager containerManager) {
        this.mainMenu = mainMenu;
        this.containerManager = containerManager;
    }

    public void onRightClick(ServerCookiePlayer serverCookiePlayer) {
        Player player = serverCookiePlayer.getPlayer();
        ItemStack itemInHand = player.getMainHandItem();
        if (itemInHand.getItem() == Items.AIR)
            return;

        if (itemInHand.getItem() == Items.JIGSAW) {
            mainMenu.openMainMenu(serverCookiePlayer);
            return;
        }

        if (new Features(itemInHand).getCategory() != ToolType.BACKPACK)
            return;

        ItemContainerContents containerContents = itemInHand.getComponents().getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        ItemStorage itemStorage = new ItemStorage(containerContents,
                (new Features(itemInHand).getStat(StatType.ADDITIONAL_SLOT) / 9) - 1, itemInHand);

        containerManager.openContainer(serverCookiePlayer, itemStorage);
        serverCookiePlayer.swingArm();
    }

    public boolean checkForDropAction(ServerCookiePlayer serverCookiePlayer, DiggingAction diggingAction) {
        if (diggingAction != DiggingAction.DROP_ITEM && diggingAction != DiggingAction.DROP_ITEM_STACK)
            return false;

        Player player = serverCookiePlayer.getPlayer();
        ItemStack inHand = player.getMainHandItem();

        return new Features(inHand).getCategory() == ToolType.BACKPACK
                && containerManager.getOpenedContainer(serverCookiePlayer) instanceof ItemStorage;
    }
}
