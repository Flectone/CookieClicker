package net.flectone.cookieclicker.utility;

import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.google.inject.Singleton;
import com.mojang.serialization.JavaOps;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.papermc.paper.adventure.WrapperAwareSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

@Singleton
public class CCConversionUtils {

    //конвертация компонентов
    public net.minecraft.network.chat.Component convertToNMSComponent(net.kyori.adventure.text.Component component) {
        ComponentSerializer<Component, Component, net.minecraft.network.chat.Component> componentSerializer;
        componentSerializer = new WrapperAwareSerializer(() -> MinecraftServer.getServer().registryAccess().createSerializationContext(JavaOps.INSTANCE));
        return componentSerializer.serialize(component);
    }

    public Player userToNMS (User user) {
        return getNMSplayerByUUID(user.getUUID());
    }

    public Player getNMSplayerByUUID (UUID uuid) {
        //короче, тут я беру всех игроков на сервере и с помощью UUID нахожу нужного
        Player player = null;
        for (Player i : MinecraftServer.getServer().getPlayerList().players) {
            if (i.getUUID().equals(uuid))
                player = i;
        }
        return player;
    }

    public ItemStack toMinecraftStack(com.github.retrooper.packetevents.protocol.item.ItemStack stack, RegistryAccess registries) {
        ByteBuf buf = PooledByteBufAllocator.DEFAULT.buffer();
        try {
            PacketWrapper.createUniversalPacketWrapper(buf).writeItemStack(stack);
            return net.minecraft.world.item.ItemStack.OPTIONAL_STREAM_CODEC.decode(
                    new RegistryFriendlyByteBuf(buf, registries));
        } finally {
            buf.release();
        }
    }

    public com.github.retrooper.packetevents.protocol.item.ItemStack fromMinecraftStack(ItemStack stack, RegistryAccess registries) {
        ByteBuf buf = PooledByteBufAllocator.DEFAULT.buffer();
        try {
            net.minecraft.world.item.ItemStack.OPTIONAL_STREAM_CODEC.encode(
                    new RegistryFriendlyByteBuf(buf, registries), stack);
            return PacketWrapper.createUniversalPacketWrapper(buf).readItemStack();
        } finally {
            buf.release();
        }
    }
}
