package net.flectone.cookieclicker.eventdata.events;

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityStatus;
import lombok.Getter;
import net.flectone.cookieclicker.entities.playerdata.ServerCookiePlayer;
import net.flectone.cookieclicker.eventdata.events.base.TypedPlayerEvent;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@Getter
public class ClickerPlayerEat extends TypedPlayerEvent<WrapperPlayServerEntityStatus> {
    private final ItemStack eatenItem;

    public ClickerPlayerEat(ServerCookiePlayer cookiePlayer, WrapperPlayServerEntityStatus packetWrapper) {
        super(cookiePlayer, packetWrapper);

        Player player = cookiePlayer.getPlayer();
        eatenItem = player.getItemInHand(InteractionHand.MAIN_HAND).has(DataComponents.CONSUMABLE)
                ? player.getItemInHand(InteractionHand.MAIN_HAND)
                : player.getItemInHand(InteractionHand.OFF_HAND);
    }
}
