package net.flectone.cookieclicker.cookiePart;

import com.google.inject.Singleton;
import net.flectone.cookieclicker.CookieClicker;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.UUID;

@Singleton
public class EpicHoeUtils {
    public HashMap<UUID, Integer> charge = new HashMap<>();
    public void addCharge(Player player, Integer percentage) {
        UUID plId = player.getUUID();
        charge.put(plId, charge.isEmpty() || !(charge.containsKey(plId)) ? percentage : charge.get(plId) + percentage);
        Bukkit.getScheduler().runTaskLater(CookieClicker.getPlugin(CookieClicker.class), () -> {
            charge.put(plId, charge.get(plId) - percentage);

        }, 400L);
    }

    public Integer getCharge (Player player) {
        UUID uuid = player.getUUID();
        if (charge.isEmpty() || !(charge.containsKey(uuid)))
            return 0;
        return charge.get(uuid);
    }

    public Integer getTier (Player player) {
        int value = getCharge(player);
        if (value <= 0)
            return 0;
        return Math.round((float) value / 33f);
    }
}
