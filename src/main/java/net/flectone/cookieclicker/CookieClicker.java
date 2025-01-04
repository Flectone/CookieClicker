package net.flectone.cookieclicker;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import net.flectone.cookieclicker.cookiePart.BlockInteractEvent;
import net.flectone.cookieclicker.cookiePart.InteractEvent;
import net.flectone.cookieclicker.crafting.CraftingEvent;
import net.flectone.cookieclicker.events.*;
import net.flectone.cookieclicker.items.ItemManager;
import net.flectone.cookieclicker.crafting.Recipes;
import net.flectone.cookieclicker.items.ShopManager;
import net.minecraft.util.profiling.jfr.event.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Iterator;

public final class CookieClicker extends JavaPlugin {
    public Plugin plugin;
    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
        Bukkit.getLogger().info("куки кликер загружается");
        Iterator<Recipe> iterator = Bukkit.getServer().recipeIterator();
        while (iterator.hasNext()) {
            Recipe pizda = iterator.next();
            if (pizda instanceof Keyed keyed && keyed.key().asString().equals("minecraft:bread")) {
                Bukkit.getLogger().info("удаляем вот этот рецепт " + String.valueOf(pizda));
                iterator.remove();
            }
        }
    }

    @Override
    public void onEnable() {
        PacketEvents.getAPI().init();
        this.plugin = getPlugin(CookieClicker.class);
//        Iterator<Recipe> iterator = Bukkit.recipeIterator();
//        while (iterator.hasNext()) {
//            if (iterator.next() instanceof Keyed keyed && keyed.key().asString().equals("minecraft:bread")) {
//                iterator.remove();
//            }
//        }
        Injector injector = Guice.createInjector(new CookieClickerInject(this));
        // Plugin startup logic
//        getLogger().info("я больше не буду нихуя тапать,");
//        getLogger().info("в эту хуйню верить ебаную. Заебала");
//        getLogger().info("меня эта сука наивная дичь, жизнь");
//        getLogger().info("эта долбоёбская блять. Верить в эту сука");
//        getLogger().info("хомячину блять, побрили как лоха...");

        //slozhno.generateText();
        //Bukkit.getPluginManager().registerEvents(injector.getInstance(InteractEvent.class), this);
        Bukkit.getPluginManager().registerEvents(injector.getInstance(PickupEvent.class), this);
        Bukkit.getPluginManager().registerEvents(injector.getInstance(EatingEvent.class), this);
        //Bukkit.getPluginManager().registerEvents(injector.getInstance(BlockInteractEvent.class), this);
        Bukkit.getPluginManager().registerEvents(injector.getInstance(ClickInInvEvent.class), this);
        Bukkit.getPluginManager().registerEvents(injector.getInstance(openAllItems.class), this);
        Bukkit.getPluginManager().registerEvents(injector.getInstance(MenuInventories.class), this);
        Bukkit.getPluginManager().registerEvents(injector.getInstance(CraftingEvent.class), this);
        PacketEvents.getAPI().getEventManager().registerListener(injector.getInstance(Packets.class), PacketListenerPriority.HIGHEST);
        injector.getInstance(ItemManager.class).load();
        injector.getInstance(ShopManager.class).loadSellingItems();
        injector.getInstance(Recipes.class).addRecipes();

        
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
        // Plugin shutdown logic

    }
}
