package net.flectone.cookieclicker.listeners;

import net.flectone.cookieclicker.Cooldown;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.RayTraceResult;

public class TeleportRod implements Listener {
    @EventHandler
    public void onRightClick (PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!Cooldown.checkTeleportCD(player)) return;
        Cooldown.setTeleportCD(player, 200);
        if (!player.getInventory().getItemInMainHand().getType().equals(Material.ECHO_SHARD)) return;
        if (event.getPlayer().rayTraceBlocks(60) == null) return;
        Particle particle = Particle.SPELL_WITCH;
        event.setCancelled(true);
        Location loc = player.getLocation();
        Location location = player.getWorld().rayTraceBlocks(player.getEyeLocation(), loc.getDirection(), 60).getHitBlock().getLocation();
        location.setY(location.getY() + 1);
        if (!location.getBlock().getType().equals(Material.AIR)) return;
        location.setX(location.getX() + 0.5);
        location.setZ(location.getZ() + 0.5);
        float yaw = player.getLocation().getYaw();
        float pitch = player.getLocation().getPitch();
        location.setYaw(yaw);
        location.setPitch(pitch);
        player.getLocation().getWorld().spawnParticle(particle, player.getLocation(), 10);
        player.teleport(location);
        player.getWorld().playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
        location.getWorld().spawnParticle(particle, location, 10);

    }
}
