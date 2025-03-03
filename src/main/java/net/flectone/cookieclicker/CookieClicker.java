package net.flectone.cookieclicker;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.flectone.cookieclicker.events.Packets;
import net.flectone.cookieclicker.events.PacketInteractEvent;
import net.flectone.cookieclicker.items.ItemManager;
import net.flectone.cookieclicker.items.Recipes;
import net.flectone.cookieclicker.items.ShopManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;

@Singleton
public final class CookieClicker extends JavaPlugin {
    public Plugin plugin;

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
        Bukkit.getLogger().info("куки кликер загружается");
    }

    @Override
    public void onEnable() {
        PacketEvents.getAPI().init();
        this.plugin = getPlugin(CookieClicker.class);

        Injector injector = Guice.createInjector(new CookieClickerInject(this));

        PacketEvents.getAPI().getEventManager().registerListener(injector.getInstance(Packets.class), PacketListenerPriority.NORMAL);
        injector.getInstance(ItemManager.class).load();
        injector.getInstance(ShopManager.class).loadSellingItems();
        injector.getInstance(Recipes.class).addRecipes();

        Path projectPath = plugin.getDataFolder().toPath();
        Path configPath = projectPath.resolve("config.yml");

        RegisteredEntitiesConfig config = new RegisteredEntitiesConfig(configPath);
        config.reload();

        injector.getInstance(PacketInteractEvent.class).setRegisteredEntitiesConfig(config);

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(injector.getInstance(RegisteredCommands.class).createOpenMenuCommand());
            commands.registrar().register(injector.getInstance(RegisteredCommands.class).createCookieEntityCommand());
        });

        injector.getInstance(PacketInteractEvent.class).loadAllEntities();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        PacketEvents.getAPI().terminate();

    }
}
