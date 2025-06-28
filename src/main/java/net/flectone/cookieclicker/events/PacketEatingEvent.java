package net.flectone.cookieclicker.events;

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityStatus;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.items.ItemManager;
import net.flectone.cookieclicker.playerdata.ServerCookiePlayer;
import net.flectone.cookieclicker.utility.UtilsCookie;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@Singleton
public class PacketEatingEvent {
    private final ItemManager manager;
    private final UtilsCookie utilsCookie;

    @Inject
    public PacketEatingEvent(ItemManager manager, UtilsCookie utilsCookie) {
        this.manager = manager;
        this.utilsCookie = utilsCookie;
    }

    public void onEat(WrapperPlayServerEntityStatus entityStatusPacket, ServerCookiePlayer serverCookiePlayer) {
        if (entityStatusPacket.getEntityId() != serverCookiePlayer.getId())
            return;
        if (entityStatusPacket.getStatus() != 9)
            return;

        Player player = serverCookiePlayer.getPlayer();

        ItemStack itemStackInHand = player.getItemInHand(InteractionHand.MAIN_HAND).has(DataComponents.CONSUMABLE)
                ? player.getItemInHand(InteractionHand.MAIN_HAND)
                : player.getItemInHand(InteractionHand.OFF_HAND);

        if (utilsCookie.compare(itemStackInHand, manager.getNMS("ench_cookie"))) {
            player.giveExperiencePoints(160);
        }
    }
}
