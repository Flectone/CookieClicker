package net.flectone.cookieclicker.items.itemstacks.base;

import com.mojang.serialization.JavaOps;
import io.papermc.paper.adventure.WrapperAwareSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.List;

public interface CookieItems {
    default net.minecraft.network.chat.Component convertToNMSComponent(net.kyori.adventure.text.Component component) {
        ComponentSerializer<Component, Component, net.minecraft.network.chat.Component> componentSerializer;
        componentSerializer = new WrapperAwareSerializer(() -> MinecraftServer.getServer().registryAccess().createSerializationContext(JavaOps.INSTANCE));
        return componentSerializer.serialize(component);
    }

//    default Component convertToAdventure(net.minecraft.network.chat.Component component) {
//        ComponentSerializer<Component, Component, net.minecraft.network.chat.Component> componentSerializer;
//        PaperAdventure.asAdventure(component);
//    }
    MiniMessage miniMessage = MiniMessage.miniMessage();

    Item HIDDEN_ITEM = Items.MOJANG_BANNER_PATTERN;

    String PLUGIN_KEY = "cc2";
    String ITEM_CATEGORY_KEY = "category";
    String ITEM_TAG_KEY = "item_tag";
    String FORTUNE_KEY = "farm_fortune";
    String ABILITY_KEY = "ability";

    String OLD_FORTUNE_KEY = "ff"; //фф

    List<String> LOADED_STATS = List.of(FORTUNE_KEY, OLD_FORTUNE_KEY);

    //когда-нибудь
    //Pair<String, String> DAMAGE_KEY = new Pair<>(PLUGIN_KEY, "dmg");
    //Pair<String, String> MINING_FORTUNE_KEY = new Pair<>(PLUGIN_KEY, "mine_fortune");


}
