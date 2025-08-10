package net.flectone.cookieclicker.utility.hoes;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
public class EpicHoeUtils {

    private final Map<UUID, Integer> charge = new HashMap<>();
    @Inject
    Plugin plugin;

    public void addCharge(UUID uuid, Integer percentage) {
        charge.put(uuid, charge.isEmpty() || !(charge.containsKey(uuid)) ? percentage : charge.get(uuid) + percentage);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            charge.put(uuid, charge.get(uuid) - percentage);

        }, 400L);
    }

    public Integer getCharge (UUID uuid) {
        if (charge.isEmpty() || !(charge.containsKey(uuid)))
            return 0;
        return charge.get(uuid);
    }

    public Integer getTier (UUID uuid) {
        int value = getCharge(uuid);
        if (value <= 0)
            return 0;
        return Math.round(value / 33f);
    }
}
