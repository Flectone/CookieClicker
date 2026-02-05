package net.flectone.cookieclicker;

import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import lombok.RequiredArgsConstructor;
import net.flectone.cookieclicker.utility.config.*;
import net.flectone.cookieclicker.utility.logging.CustomLoggerProvider;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

@RequiredArgsConstructor
public class CookieClickerInject extends AbstractModule {
    private final Plugin plugin;
    private final Logger logger;
    private final TaskScheduler scheduler;

    private final RegisteredEntitiesConfig registeredEntities;
    private final CookieClickerConfig config;
    private final ItemsDescription itemsDescription;
    private final EquipmentUpgradeConfig upgradeConfig;
    private final RegisteredBlocks registeredBlocks;

    @Override
    protected void configure() {
        bind(Plugin.class).toInstance(plugin);
        bind(RegisteredEntitiesConfig.class).toInstance(registeredEntities);
        bind(RegisteredBlocks.class).toInstance(registeredBlocks);
        bind(ItemsDescription.class).toInstance(itemsDescription);
        bind(CookieClickerConfig.class).toInstance(config);
        bind(EquipmentUpgradeConfig.class).toInstance(upgradeConfig);
        bind(TaskScheduler.class).toInstance(scheduler);

        bindListener(Matchers.any(), new CustomLoggerProvider(logger));
    }
}
