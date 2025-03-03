package net.flectone.cookieclicker.cookiePart;

import com.google.inject.Singleton;
import net.flectone.cookieclicker.CookieClicker;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.UUID;

@Singleton
public class EpicHoeUtils {
    public HashMap<UUID, Integer> charge = new HashMap<>();

    public void addCharge(UUID uuid, Integer percentage) {
        charge.put(uuid, charge.isEmpty() || !(charge.containsKey(uuid)) ? percentage : charge.get(uuid) + percentage);
        Bukkit.getScheduler().runTaskLater(CookieClicker.getPlugin(CookieClicker.class), () -> {
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
        return Math.round((float) value / 33f);
    }
}
