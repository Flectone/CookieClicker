package net.flectone.cookieclicker.gameplay.blockbreaking;

import com.github.retrooper.packetevents.protocol.sound.Sounds;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import net.flectone.cookieclicker.entities.ConnectedPlayers;
import net.flectone.cookieclicker.entities.objects.mineable.ClickableBlock;
import net.flectone.cookieclicker.entities.playerdata.ServerCookiePlayer;
import net.flectone.cookieclicker.items.ItemsRegistry;
import net.flectone.cookieclicker.utility.PacketUtils;
import net.minecraft.world.item.ItemStack;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @__(@Inject))
public class BlockDropSpawner {

    private final PacketUtils packetUtils;
    private final ItemsRegistry itemsRegistry;
    private final ConnectedPlayers connectedPlayers;

    public void spawnDrop(ServerCookiePlayer serverCookiePlayer, ClickableBlock block, int fortune) {
        ItemStack dropItemStack = itemsRegistry.get(block.getDrop());

        // TODO сделать удачу, чтобы роботала

        packetUtils.playSound(serverCookiePlayer.getUser(), Sounds.ENTITY_ITEM_PICKUP, 0.5f, 0.6f);
        packetUtils.spawnItem(serverCookiePlayer, block.getDropPosition().toLocation(), dropItemStack);
        connectedPlayers.save(serverCookiePlayer, true);
    }
}
