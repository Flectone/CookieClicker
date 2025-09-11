package net.flectone.cookieclicker;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.flectone.cookieclicker.commands.RegisteredCommands;
import net.flectone.cookieclicker.eventdata.CookieEventManager;
import net.flectone.cookieclicker.eventdata.listener.PacketDispatcher;
import net.flectone.cookieclicker.gameplay.cookiepart.InteractionController;
import net.flectone.cookieclicker.gameplay.cookiepart.listeners.*;
import net.flectone.cookieclicker.gameplay.crafting.anvil.listeners.AnvilListener;
import net.flectone.cookieclicker.gameplay.crafting.craftingtable.listeners.CraftingListener;
import net.flectone.cookieclicker.gameplay.itempickup.listeners.ItemPickupListener;
import net.flectone.cookieclicker.gameplay.itempickup.listeners.PlayerMoveListener;
import net.flectone.cookieclicker.gameplay.window.listeners.WindowListener;
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
        Path projectPath = this.getDataFolder().toPath();

        PacketEvents.getAPI().init();

        Path configPath = projectPath.resolve("entities.yml");
        RegisteredEntitiesConfig config = new RegisteredEntitiesConfig(configPath);
        config.reload();

        injector = Guice.createInjector(new CookieClickerInject(this, getLogger(), config));

        CookieEventManager cookieEventManager = injector.getInstance(CookieEventManager.class);

        cookieEventManager.register(injector.getInstance(PlayerInteractEntityListener.class));
        cookieEventManager.register(injector.getInstance(PlayerEatListener.class));
        cookieEventManager.register(injector.getInstance(PlayerMoveListener.class));
        cookieEventManager.register(injector.getInstance(ItemPickupListener.class));
        cookieEventManager.register(injector.getInstance(AnvilListener.class));
        cookieEventManager.register(injector.getInstance(InventoryClickListener.class));
        cookieEventManager.register(injector.getInstance(PlayerInteractItemListener.class));
        cookieEventManager.register(injector.getInstance(PlayerInteractBlockListener.class));

        cookieEventManager.register(injector.getInstance(WindowListener.class));
        cookieEventManager.register(injector.getInstance(CraftingListener.class));

        PacketEvents.getAPI().getEventManager().registerListener(injector.getInstance(PacketDispatcher.class), PacketListenerPriority.NORMAL);

        injector.getInstance(ItemsRegistry.class).load(getLogger());
        injector.getInstance(VillagerTradesRegistry.class).loadSellingItems(this.getLogger());
        injector.getInstance(RecipesRegistry.class).addRecipes();

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(injector.getInstance(CookieCommands.class).createCookieClickerCommand());
            commands.registrar().register(injector.getInstance(UtilityCommands.class).createOpenMenuCommand());
        });

        try {
            injector.getInstance(Database.class).connect(projectPath);
        } catch (Exception e) {
            getLogger().warning("Failed to connect database");
            return;
        }

        injector.getInstance(InteractionController.class).loadAllEntities();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        PacketEvents.getAPI().terminate();

        injector.getInstance(Database.class).disconnect();
    }
}
