package net.flectone.cookieclicker;

import com.google.inject.AbstractModule;
import org.bukkit.plugin.Plugin;

public class CookieClickerInject extends AbstractModule {
    private final Plugin plugin;
    public CookieClickerInject(Plugin plugin) {
        this.plugin = plugin;
    }
    @Override
    protected void configure() {
        bind(Plugin.class).toInstance(plugin);
    }
}
