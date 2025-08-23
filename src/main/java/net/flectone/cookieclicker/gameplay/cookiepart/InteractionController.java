package net.flectone.cookieclicker.gameplay.cookiepart;

import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import net.flectone.cookieclicker.entities.playerdata.ServerCookiePlayer;
import net.flectone.cookieclicker.inventories.Shops;
import net.flectone.cookieclicker.utility.config.RegisteredEntitiesConfig;
import net.minecraft.server.MinecraftServer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

@Singleton
@Getter
public class InteractionController {

    private static final int CLICK_DELAY = 145;
    private final HashMap<UUID, Long> lastClick = new HashMap<>();

    private final Set<Integer> tradersFarmers = new HashSet<>();
    private final Set<Integer> tradersArmorers = new HashSet<>();
    private final HashMap<Integer, Location> itemFrames = new HashMap<>();

    private final RegisteredEntitiesConfig registeredEntities;
    private final CookieBonusSpawn cookieBonusSpawn;
    private final CookieSpawningLogic cookieSpawningLogic;
    private final Shops shops;
    private final Logger logger;

    @Inject
    public InteractionController(Shops shops, CookieBonusSpawn cookieBonusSpawn,
                                 RegisteredEntitiesConfig registeredEntities, Logger logger,
                                 CookieSpawningLogic cookieSpawningLogic) {
        this.shops = shops;
        this.logger = logger;
        this.cookieBonusSpawn = cookieBonusSpawn;
        this.registeredEntities = registeredEntities;
        this.cookieSpawningLogic = cookieSpawningLogic;
    }

    public void loadAllEntities() {
        MinecraftServer.getServer().getAllLevels().forEach(serverLevel -> {
            serverLevel.getAllEntities().forEach(entity -> {
                UUID entityUUID = entity.getUUID();
                int entityId = entity.getId();

                if (registeredEntities.getVillagers().getArmorers().contains(entityUUID.toString())) {
                    tradersArmorers.add(entityId);
                }
                if (registeredEntities.getVillagers().getFarmers().contains(entityUUID.toString())) {
                    tradersFarmers.add(entityId);
                }
                if (registeredEntities.getItemFrames().contains(entityUUID.toString())) {
                    itemFrames.put(entityId, new Location(entity.getX(), entity.getY(), entity.getZ(), 0f, 0f));
                }
            });
        });

        logger.info("loaded " + tradersFarmers.size() + " farmers");
        logger.info("loaded " + tradersArmorers.size() + " armorers");
        logger.info("loaded " + itemFrames.size() + " item frames with cookie");
    }

    public void reloadEntitiesFromConfig() {
        tradersFarmers.clear();
        tradersArmorers.clear();
        itemFrames.clear();

        loadAllEntities();
    }

    private boolean checkCooldown(ServerCookiePlayer serverCookiePlayer) {
        UUID uuid = serverCookiePlayer.getUuid();
        long currentTime = System.currentTimeMillis();

        if (lastClick.isEmpty() || !lastClick.containsKey(uuid) || lastClick.get(uuid) < currentTime) {
            lastClick.put(uuid, currentTime + CLICK_DELAY);
            return true;
        }
        return false;
    }

    @Deprecated
    public boolean checkEntity(WrapperPlayClientInteractEntity interactPacket, ServerCookiePlayer serverCookiePlayer) {
        if (interactPacket.getAction() != WrapperPlayClientInteractEntity.InteractAction.INTERACT) return false;
        return checkEntity(interactPacket.getEntityId(), serverCookiePlayer);
    }

    public boolean checkEntity(int entityId, ServerCookiePlayer serverCookiePlayer) {
        cookieBonusSpawn.checkForBonusClick(serverCookiePlayer, entityId);

        if (tradersFarmers.contains(entityId)) {
            shops.openAnyShop(serverCookiePlayer, "trading_farm");
            return true;
        }
        if (tradersArmorers.contains(entityId)) {
            shops.openAnyShop(serverCookiePlayer, "trading_armorer");
            return true;
        }
        if (itemFrames.containsKey(entityId)) {
            if (!checkCooldown(serverCookiePlayer))
                return true;

            Location itemFrameLocation = itemFrames.get(entityId);

            cookieSpawningLogic.onCookieClick(serverCookiePlayer, itemFrameLocation);
            cookieBonusSpawn.checkBonusChance(serverCookiePlayer, itemFrameLocation);
            return true;
        }
        return false;
    }

}
