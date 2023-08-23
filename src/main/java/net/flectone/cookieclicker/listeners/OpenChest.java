package net.flectone.cookieclicker.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.accessors.Accessors;
import com.comphenix.protocol.reflect.accessors.FieldAccessor;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import jdk.jfr.Event;
import jdk.jfr.consumer.MetadataEvent;
import net.flectone.cookieclicker.CookieClicker;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;


public class OpenChest implements Listener {
    @EventHandler
    public void onChestOpen(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (event.getClickedBlock() == null || !event.getClickedBlock().getType().equals(Material.CHEST) && !event.getClickedBlock().getType().equals(Material.ENDER_CHEST)) return;
        Location location = event.getClickedBlock().getLocation();
        location.setY(location.getY() - 1);
        if (location == null || !location.getBlock().getType().equals(Material.END_STONE_BRICKS)) return;
        event.setCancelled(true);

        Player player = event.getPlayer();

        BlockPosition position = new BlockPosition((int)location.getX(), (int)event.getClickedBlock().getLocation().getY(), (int)location.getZ());
        if (OpenedChests.isPlayerOpenedChest(player.getUniqueId(), position)) {
            player.sendMessage(ChatColor.RED + "Вы уже открывали этот сундук");
            return;
        }
        OpenedChests.addChestToPlayer(player.getUniqueId(), position);

        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        PacketContainer packet = manager.createPacket(PacketType.Play.Server.BLOCK_ACTION);
        packet.getIntegers().write(0, 1);
        packet.getIntegers().write(1, 1);
        packet.getBlockPositionModifier().write(0, position);
        manager.sendServerPacket(player, packet);

        FieldAccessor entityLastId = Accessors.getFieldAccessor(MinecraftReflection.getEntityClass(), AtomicInteger.class, true);
        AtomicInteger entityIdA = (AtomicInteger) entityLastId.get(null);
        int entityId = entityIdA.incrementAndGet();

        Location chestLocation = new Location(event.getClickedBlock().getWorld(), position.getX(), position.getY(), position.getZ());

        float sound1;
        Sound sound;
        if (event.getClickedBlock().getType().equals(Material.ENDER_CHEST)) {
            sound = Sound.BLOCK_ENDER_CHEST_OPEN;
            sound1 = 0.1f;
            OpenChest.DefaultChestLoot(new ItemStack(Material.STRUCTURE_VOID), entityId, manager, player, position, chestLocation);
        } else {
            sound = Sound.BLOCK_CHEST_OPEN;
            sound1 = 1f;
            OpenChest.DefaultChestLoot(new ItemStack(Material.NETHER_STAR),  entityId, manager, player, position, chestLocation);
        }
        player.playSound(event.getPlayer().getLocation(), sound, 0.7f, sound1);



        Bukkit.getScheduler().runTaskLater(CookieClicker.getPlugin(), new Runnable() {

            @Override
            public void run() {
                PacketContainer packet2 = manager.createPacket(PacketType.Play.Server.BLOCK_ACTION);
                packet2.getIntegers().write(0, 1);
                packet2.getIntegers().write(1, 0);
                packet2.getBlockPositionModifier().write(0, position);
                manager.sendServerPacket(player, packet2);

                OpenedChests.removeOpenedChest(player.getUniqueId(), position);
            }
        }, 200L);
    }
    public static void DefaultChestLoot(ItemStack itemStack, int entityId, ProtocolManager manager, Player player, BlockPosition position, Location chestLocation) {
        PacketContainer spawnItem = manager.createPacket(PacketType.Play.Server.SPAWN_ENTITY);
        EntityType item = EntityType.DROPPED_ITEM;
        spawnItem.getIntegers().write(0, entityId);
        spawnItem.getUUIDs().write(0, UUID.randomUUID());
        spawnItem.getEntityTypeModifier().write(0, item);
        spawnItem.getDoubles().write(0, (double) position.getX() + 0.5);
        spawnItem.getDoubles().write(1, (double) position.getY() + 1);
        spawnItem.getDoubles().write(2, (double) position.getZ() + 0.5);

        WrappedDataWatcher.Serializer serializer = WrappedDataWatcher.Registry.getItemStackSerializer(false);
        PacketContainer meta = manager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
        meta.getIntegers().write(0, entityId);
        meta.getDataValueCollectionModifier().write(0, Arrays.asList(new WrappedDataValue(8, serializer, MinecraftReflection.getMinecraftItemStack(itemStack))));

        manager.sendServerPacket(player, spawnItem);
        manager.sendServerPacket(player, meta);

        PickupItem(player, chestLocation, manager, entityId);


    }
    public static void PickupItem(Player player, Location chestLocation, ProtocolManager manager, int entityId) {
        BukkitTask addedItem = new BukkitRunnable() {
            @Override
            public void run() {
                if (player.getLocation().distance(chestLocation) <= 2) {
                    List<Integer> da = new ArrayList<>();
                    da.add(entityId);
                    player.getInventory().addItem(new ItemStack(Material.NETHER_STAR));
                    PacketContainer removeItem = manager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
                    removeItem.getIntLists().write(0, da);
                    manager.sendServerPacket(player, removeItem);
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
                    this.cancel();
                }
            }
        }.runTaskTimerAsynchronously(CookieClicker.getPlugin(), 1L, 10L);
    }
}
