package net.flectone.cookieclicker.gameplay.cookiepart.listeners;

import com.github.retrooper.packetevents.protocol.player.InteractionHand;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.entities.playerdata.ServerCookiePlayer;
import net.flectone.cookieclicker.eventdata.CookieEventHandler;
import net.flectone.cookieclicker.eventdata.CookieListener;
import net.flectone.cookieclicker.eventdata.events.ClickerDropItem;
import net.flectone.cookieclicker.eventdata.events.ClickerInteract;
import net.flectone.cookieclicker.eventdata.events.ClickerPlayerSwingArm;
import net.flectone.cookieclicker.inventories.ContainerManager;
import net.flectone.cookieclicker.inventories.containers.ItemStorage;
import net.flectone.cookieclicker.items.attributes.StatType;
import net.flectone.cookieclicker.items.attributes.ToolType;
import net.flectone.cookieclicker.items.itemstacks.base.data.Features;
import net.flectone.cookieclicker.items.itemstacks.base.data.ItemTag;
import net.flectone.cookieclicker.utility.hoes.LegendaryHoeUpgrade;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;

@Singleton
public class PlayerInteractItemListener implements CookieListener {

    private final ContainerManager containerManager;
    private final LegendaryHoeUpgrade legendaryHoeUpgrade;

    @Inject
    public PlayerInteractItemListener(ContainerManager containerManager, LegendaryHoeUpgrade legendaryHoeUpgrade) {
        this.containerManager = containerManager;
        this.legendaryHoeUpgrade = legendaryHoeUpgrade;
    }

    @CookieEventHandler
    public void onUseMainhand(ClickerInteract event) {
        if (event.getInteractionHand() != InteractionHand.MAIN_HAND) return;

        ServerCookiePlayer serverCookiePlayer = event.getCookiePlayer();
        Player player = serverCookiePlayer.getPlayer();
        ItemStack itemInHand = player.getMainHandItem();

        if (itemInHand.getItem() == Items.AIR)
            return;

        if (new Features(itemInHand).getCategory() != ToolType.BACKPACK)
            return;

        ItemContainerContents containerContents = itemInHand.getComponents().getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        ItemStorage itemStorage = new ItemStorage(containerContents,
                (new Features(itemInHand).getStat(StatType.ADDITIONAL_SLOT) / 9) - 1, itemInHand);

        containerManager.openContainer(serverCookiePlayer, itemStorage);
        serverCookiePlayer.swingArm();
    }

    @CookieEventHandler
    public void onItemDrop(ClickerDropItem event) {
        ItemStack inHand = event.getCookiePlayer().getPlayer().getMainHandItem();

        boolean isStorageOpened = new Features(inHand).getCategory() == ToolType.BACKPACK
                && containerManager.getOpenedContainer(event.getCookiePlayer()) instanceof ItemStorage;

        event.setCancelled(isStorageOpened);
    }

    @CookieEventHandler
    public void onArmAnimation(ClickerPlayerSwingArm event) {
        Player player = event.getCookiePlayer().getPlayer();

        //проверка, клик в воздух или нет
        HitResult hitResult = player.getRayTrace(7, ClipContext.Fluid.NONE);
        if (!hitResult.getType().equals(HitResult.Type.MISS)) return;

        //предмет в руке
        ItemStack itemInHand = player.getItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND);
        if (new Features(itemInHand).getItemTag() == ItemTag.LEGENDARY_HOE) {
            legendaryHoeUpgrade.legHoeChange(event.getCookiePlayer());
        }
    }
}
