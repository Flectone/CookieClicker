package net.flectone.cookieclicker.entities.objects.mineable;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.world.BlockFace;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import lombok.Getter;
import net.flectone.cookieclicker.entities.objects.Spawnable;
import net.flectone.cookieclicker.entities.objects.display.CookieTextDisplay;
import net.flectone.cookieclicker.entities.objects.display.InteractionEntity;
import net.flectone.cookieclicker.entities.playerdata.ServerCookiePlayer;
import net.flectone.cookieclicker.gameplay.blockbreaking.data.BlockType;
import net.flectone.cookieclicker.items.attributes.ToolType;
import net.flectone.cookieclicker.items.itemstacks.base.data.ItemTag;
import net.flectone.cookieclicker.utility.data.Position;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.List;
import java.util.UUID;

@Getter
public class ClickableBlock implements Spawnable {

    private int textDisplayId;
    private int interactionId;

    private final ItemTag drop;

    private Position dropPosition = new Position(0d, 0d, 0d);
    private Position centerPosition = new Position(0d, 0d, 0d);

    private UUID viewerUuid;

    private int health = 10;
    private final ToolType requiredTool;
    private final int requiredPower;

    public ClickableBlock(BlockType blockType) {
        this.drop = blockType.getDropType();
        this.health = blockType.getHealth();
        this.requiredTool = blockType.getRequiredTool();
        this.requiredPower = blockType.getRequiredPower();
    }

    public void dealDamage(int dmg) {
        health = Math.max(0, health - dmg);

        sendTextDisplayChange();
    }

    public void spawn(ServerCookiePlayer serverCookiePlayer) {
        // Создание Interaction, хитбокс которого чуть больше блока
        InteractionEntity interactionEntity = new InteractionEntity();
        interactionEntity.setHeight(1.1f);
        interactionEntity.setWidth(1.1f);
        interactionEntity.setLocation(centerPosition.getX(), centerPosition.getY() - 0.55, centerPosition.getZ());

        CookieTextDisplay textDisplay = new CookieTextDisplay();
        textDisplay.setText(getHealthText());
        textDisplay.setLocation(dropPosition.getX(), dropPosition.getY(), dropPosition.getZ());

        interactionEntity.spawn(serverCookiePlayer);
        textDisplay.spawn(serverCookiePlayer);

        interactionId = interactionEntity.getEntityId();
        textDisplayId = textDisplay.getEntityId();
        viewerUuid = serverCookiePlayer.getUuid();
    }

    private void sendTextDisplayChange() {
        //лучше так не делать, но тут это ни на что не влияет
        ServerCookiePlayer serverCookiePlayer = new ServerCookiePlayer(viewerUuid);

        EntityData<?> textData = new EntityData<>(23, EntityDataTypes.ADV_COMPONENT,
                MiniMessage.miniMessage().deserialize(getHealthText()));

        WrapperPlayServerEntityMetadata metadataPacket = new WrapperPlayServerEntityMetadata(textDisplayId, List.of(textData));

        serverCookiePlayer.sendPEpacket(metadataPacket);
    }

    public void remove(ServerCookiePlayer serverCookiePlayer) {
        WrapperPlayServerDestroyEntities destroyEntitiesPacket = new WrapperPlayServerDestroyEntities(interactionId, textDisplayId);

        serverCookiePlayer.sendPEpacket(destroyEntitiesPacket);
    }

    private Position getFacePos(BlockFace blockFace, Position position) {
        double x = position.getX();
        double y = position.getY();
        double z = position.getZ();

        return switch (blockFace) {
            case DOWN -> new Position(x, y - 0.65, z);
            case UP -> new Position(x, y + 0.65, z);
            case NORTH -> new Position(x, y, z - 0.65);
            case SOUTH -> new Position(x, y, z + 0.65);
            case WEST -> new Position(x - 0.65, y, z);
            case EAST -> new Position(x + 0.65, y, z);
            case OTHER -> new Position(x, y, z);
        };
    }

    private String getHealthText() {
        return String.format("<#FF7400>[<white>%d<#FF7400>]", health);
    }

    public void setLocation(Double x, Double y, Double z) {
        this.centerPosition = new Position(x + 0.5, y + 0.5, z + 0.5);
        this.dropPosition = new Position(x + 0.5, y + 0.5, z + 0.5);
    }

    public void shiftDropLocation(BlockFace blockFace) {
        dropPosition = getFacePos(blockFace, dropPosition);
    }
}
