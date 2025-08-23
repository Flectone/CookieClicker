package net.flectone.cookieclicker.inventories.containers;

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import lombok.Getter;
import lombok.Setter;
import net.flectone.cookieclicker.entities.playerdata.ServerCookiePlayer;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class MenuContainer extends ClickerContainer {

    private final HashMap<Integer, BiConsumer<ServerCookiePlayer, WrapperPlayClientClickWindow.WindowClickType>> actions = new HashMap<>();
    @Getter
    @Setter
    private Consumer<ServerCookiePlayer> defaultAction;

    public MenuContainer(int windowId, int windowType, String customData) {
        super(windowId, windowType, customData);
    }

    public void setAction(Integer slot, BiConsumer<ServerCookiePlayer, WrapperPlayClientClickWindow.WindowClickType> action) {
        actions.put(slot, action);
    }

    @Nullable
    public BiConsumer<ServerCookiePlayer, WrapperPlayClientClickWindow.WindowClickType> getAction(Integer slot) {
        if (actions.isEmpty() || !actions.containsKey(slot)) return null;

        return actions.get(slot);
    }
}
