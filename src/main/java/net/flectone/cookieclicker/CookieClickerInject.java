package net.flectone.cookieclicker;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import net.flectone.cookieclicker.utility.config.CookieClickerConfig;
import net.flectone.cookieclicker.utility.config.EquipmentUpgradeConfig;
import net.flectone.cookieclicker.utility.config.ItemsDescription;
import net.flectone.cookieclicker.utility.config.RegisteredEntitiesConfig;
import net.flectone.cookieclicker.utility.logging.CustomLoggerProvider;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

public class CookieClickerInject extends AbstractModule {
    private final Plugin plugin;
    private final Logger logger;

    private final RegisteredEntitiesConfig registeredEntities;
    private final CookieClickerConfig config;
    private final ItemsDescription itemsDescription;
    private final EquipmentUpgradeConfig upgradeConfig;

    public CookieClickerInject(Plugin plugin, Logger logger, RegisteredEntitiesConfig registeredEntities,
                               CookieClickerConfig config, ItemsDescription itemsDescription,
                               EquipmentUpgradeConfig upgradeConfig) {
        this.plugin = plugin;
        this.logger = logger;
        this.registeredEntities = registeredEntities;
        this.config = config;
        this.itemsDescription = itemsDescription;
        this.upgradeConfig = upgradeConfig;
    }
    @Override
    protected void configure() {
        bind(Plugin.class).toInstance(plugin);
        bind(RegisteredEntitiesConfig.class).toInstance(registeredEntities);
        bind(ItemsDescription.class).toInstance(itemsDescription);
        bind(CookieClickerConfig.class).toInstance(config);
        bind(EquipmentUpgradeConfig.class).toInstance(upgradeConfig);

        bindListener(Matchers.any(), new CustomLoggerProvider(logger));
    }
}
