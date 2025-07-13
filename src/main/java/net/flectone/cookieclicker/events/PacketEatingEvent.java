package net.flectone.cookieclicker.events;

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityStatus;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.items.itemstacks.base.data.ItemTag;
import net.flectone.cookieclicker.playerdata.ServerCookiePlayer;
import net.flectone.cookieclicker.utility.StatsUtils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@Singleton
public class PacketEatingEvent {
    private final StatsUtils statsUtils;

    @Inject
    public PacketEatingEvent(StatsUtils statsUtils) {
        this.statsUtils = statsUtils;
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

        if (statsUtils.hasTag(itemStackInHand, ItemTag.ENCHANTED_COOKIE)) {
            player.giveExperiencePoints(160);
        }
    }
}
