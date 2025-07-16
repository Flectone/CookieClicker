package net.flectone.cookieclicker.events;

import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.Setter;
import net.flectone.cookieclicker.RegisteredEntitiesConfig;
import net.flectone.cookieclicker.inventories.Shops;
import net.flectone.cookieclicker.playerdata.ServerCookiePlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Singleton
@Getter
public class PacketInteractAtEntityEvent {
    private final int clickDelay = 145;

    private final Set<Integer> tradingFarm = new HashSet<>();
    private final Set<Integer> tradingArmorer = new HashSet<>();
    private final HashMap<Integer, Location> itemFrames = new HashMap<>();
    private final HashMap<UUID, Long> lastClick = new HashMap<>();
    @Setter
    private RegisteredEntitiesConfig registeredEntitiesConfig;

    private final PacketCookieClickEvent cookieClickEvent;
    private final Shops shops;

    @Inject
    public PacketInteractAtEntityEvent(Shops shops, PacketCookieClickEvent packetCookieClickEvent) {
        this.shops = shops;
        this.cookieClickEvent = packetCookieClickEvent;
    }

    public void loadAllEntities() {
        MinecraftServer.getServer().getAllLevels().forEach(serverLevel -> {
            serverLevel.getAllEntities().forEach(entity -> {
                UUID entityUUID = entity.getUUID();
                int entityId = entity.getId();

                if (registeredEntitiesConfig.getVillagers().getArmorers().contains(entityUUID.toString())) {
                    tradingArmorer.add(entityId);
                }
                if (registeredEntitiesConfig.getVillagers().getFarmers().contains(entityUUID.toString())) {
                    tradingFarm.add(entityId);
                }
                if (registeredEntitiesConfig.getItemFrames().contains(entityUUID.toString())) {
                    itemFrames.put(entityId, new Location(entity.getX(), entity.getY(), entity.getZ(), 0f, 0f));
                }
            });
        });
        MinecraftServer.getServer().sendSystemMessage(Component.literal("loaded " + tradingFarm.size() + " farmers"));
        MinecraftServer.getServer().sendSystemMessage(Component.literal("loaded " + tradingArmorer.size() + " armorers"));
        MinecraftServer.getServer().sendSystemMessage(Component.literal("loaded " + itemFrames.size() + " item frames with cookie"));
    }

    public void reloadEntitiesFromConfig() {
        tradingFarm.clear();
        tradingArmorer.clear();
        itemFrames.clear();

        loadAllEntities();
    }

    private boolean checkCooldown(ServerCookiePlayer serverCookiePlayer) {
        UUID uuid = serverCookiePlayer.getUuid();
        long currentTime = System.currentTimeMillis();

        if (lastClick.isEmpty() || !lastClick.containsKey(uuid) || lastClick.get(uuid) < currentTime) {
            lastClick.put(uuid, currentTime + clickDelay);
            return true;
        }
        return false;
    }

    public boolean checkEntity(WrapperPlayClientInteractEntity interactPacket, ServerCookiePlayer serverCookiePlayer) {
        int entityId = interactPacket.getEntityId();

        if (interactPacket.getAction() != WrapperPlayClientInteractEntity.InteractAction.INTERACT) return false;
        cookieClickEvent.checkForBonus(serverCookiePlayer, interactPacket.getEntityId());

        if (tradingFarm.contains(entityId)) {
            shops.openAnyShop(serverCookiePlayer, "trading_farm");
            return true;
        }
        if (tradingArmorer.contains(entityId)) {
            shops.openAnyShop(serverCookiePlayer, "trading_armorer");
            return true;
        }
        if (itemFrames.containsKey(entityId)) {
            if (!checkCooldown(serverCookiePlayer))
                return true;
            cookieClickEvent.onCookieClick(serverCookiePlayer, itemFrames.get(entityId));
            return true;
        }
        return false;
    }

}
