package net.flectone.cookieclicker;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class Cooldown {
    public static HashMap<UUID, Double> teleportCD = new HashMap<>();

    public static void setTeleportCD(Player player, int addTime) {
        double time = System.currentTimeMillis() + (addTime);
        teleportCD.put(player.getUniqueId(), time);
    }

    public static boolean checkTeleportCD(Player player) {
        if (teleportCD.get(player.getUniqueId()) == null) return true;
        if (teleportCD.get(player.getUniqueId()) <= System.currentTimeMillis()) {
            teleportCD.remove(player.getUniqueId());
            return true;
        } else {
            return false;
        }
    }

}
