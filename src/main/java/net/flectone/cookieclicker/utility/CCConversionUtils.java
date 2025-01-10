package net.flectone.cookieclicker.utility;

import com.github.retrooper.packetevents.protocol.player.User;
import com.google.inject.Singleton;
import com.mojang.serialization.JavaOps;
import io.papermc.paper.adventure.WrapperAwareSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;

@Singleton
public class CCConversionUtils {

    //конвертация компонентов
    public net.minecraft.network.chat.Component convertToNMSComponent(net.kyori.adventure.text.Component component) {
        ComponentSerializer<Component, Component, net.minecraft.network.chat.Component> componentSerializer;
        componentSerializer = new WrapperAwareSerializer(() -> MinecraftServer.getServer().registryAccess().createSerializationContext(JavaOps.INSTANCE));
        return componentSerializer.serialize(component);
    }

    public Player userToNMS (User user) {
        //короче, тут я беру всех игроков на сервере и с помощью UUID нахожу нужного
        Player player = null;
        for (Player i : MinecraftServer.getServer().getPlayerList().players) {
            if (i.getUUID().equals(user.getUUID()))
                player = i;
        }
        return player;
    }
}
