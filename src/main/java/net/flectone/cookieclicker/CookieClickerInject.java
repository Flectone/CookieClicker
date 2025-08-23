package net.flectone.cookieclicker;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import net.flectone.cookieclicker.utility.config.RegisteredEntitiesConfig;
import net.flectone.cookieclicker.utility.logging.CustomLoggerProvider;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

public class CookieClickerInject extends AbstractModule {
    private final Plugin plugin;
    private final Logger logger;
    private final RegisteredEntitiesConfig registeredEntities;

    public CookieClickerInject(Plugin plugin, Logger logger, RegisteredEntitiesConfig registeredEntities) {
        this.plugin = plugin;
        this.logger = logger;
        this.registeredEntities = registeredEntities;
    }
    @Override
    protected void configure() {
        bind(Plugin.class).toInstance(plugin);
        bind(RegisteredEntitiesConfig.class).toInstance(registeredEntities);

        bindListener(Matchers.any(), new CustomLoggerProvider(logger));
    }
}
