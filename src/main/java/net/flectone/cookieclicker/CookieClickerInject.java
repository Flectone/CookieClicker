package net.flectone.cookieclicker;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import net.flectone.cookieclicker.utility.logging.CustomLoggerProvider;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

public class CookieClickerInject extends AbstractModule {
    private final Plugin plugin;
    private final Logger logger;

    public CookieClickerInject(Plugin plugin, Logger logger) {
        this.plugin = plugin;
        this.logger = logger;
    }
    @Override
    protected void configure() {
        bind(Plugin.class).toInstance(plugin);
        bindListener(Matchers.any(), new CustomLoggerProvider(logger));
    }
}
