package net.flectone.cookieclicker;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.flectone.cookieclicker.events.PacketInteractAtEntityEvent;
import net.flectone.cookieclicker.events.Packets;
import net.flectone.cookieclicker.events.core.CookieEventManager;
import net.flectone.cookieclicker.events.test.PacketEventsImpl;
import net.flectone.cookieclicker.events.test.PacketInteractListener;
import net.flectone.cookieclicker.items.ItemsRegistry;
import net.flectone.cookieclicker.items.RecipesRegistry;
import net.flectone.cookieclicker.items.VillagerTradesRegistry;
import net.flectone.cookieclicker.utility.config.RegisteredEntitiesConfig;
import net.flectone.cookieclicker.utility.database.Database;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;

@Singleton
public final class CookieClicker extends JavaPlugin {
    private Injector injector;

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        PacketEvents.getAPI().init();

        injector = Guice.createInjector(new CookieClickerInject(this, getLogger()));


        PacketEvents.getAPI().getEventManager().registerListener(injector.getInstance(Packets.class), PacketListenerPriority.NORMAL);
        injector.getInstance(ItemsRegistry.class).load(getLogger());
        injector.getInstance(VillagerTradesRegistry.class).loadSellingItems(this.getLogger());
        injector.getInstance(RecipesRegistry.class).addRecipes();

        Path projectPath = this.getDataFolder().toPath();
        Path configPath = projectPath.resolve("config.yml");

        RegisteredEntitiesConfig config = new RegisteredEntitiesConfig(configPath);
        config.reload();

        injector.getInstance(PacketInteractAtEntityEvent.class).setRegisteredEntitiesConfig(config);

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(injector.getInstance(RegisteredCommands.class).createCookieClickerCommand());
            commands.registrar().register(injector.getInstance(RegisteredCommands.class).createOpenMenuCommand());
        });

        try {
            injector.getInstance(Database.class).connect(projectPath);
        } catch (Exception e) {
            getLogger().warning("Failed to connect database");
            return;
        }

        injector.getInstance(PacketInteractAtEntityEvent.class).loadAllEntities();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        PacketEvents.getAPI().terminate();

        injector.getInstance(Database.class).disconnect();
    }
}
