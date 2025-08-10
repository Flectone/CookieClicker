package net.flectone.cookieclicker.entities.objects.item;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityVelocity;
import lombok.Getter;
import net.flectone.cookieclicker.entities.objects.CookieEntity;
import net.flectone.cookieclicker.utility.data.Position;
import net.flectone.cookieclicker.items.itemstacks.base.data.Features;
import net.flectone.cookieclicker.entities.playerdata.ServerCookiePlayer;
import net.minecraft.world.item.ItemStack;

@Getter
public class CookieItemEntity extends CookieEntity {
    private final CookieItemEntityData data;

    public CookieItemEntity(ItemStack itemStack) {
        super(EntityTypes.ITEM);
        data = new CookieItemEntityData(
                new Features(itemStack).getItemTag(), itemStack.getCount(), getEntityId()
        );
    }

    public void setVisual(com.github.retrooper.packetevents.protocol.item.ItemStack itemStackPE) {
        entityData.add(new EntityData<>(8, EntityDataTypes.ITEMSTACK, itemStackPE));
    }

    @Override
    public void spawn(ServerCookiePlayer serverCookiePlayer) {
        super.spawn(serverCookiePlayer);

        WrapperPlayServerEntityVelocity velocityPacket = new WrapperPlayServerEntityVelocity(
                getEntityId(), new Vector3d()
        );
        serverCookiePlayer.sendPEpacket(velocityPacket);

        data.setPosition(new Position(location)
                .withHeight(serverCookiePlayer.getPlayer().getY())); //Некоторые предметы спавнятся в воздухе, но падают
    }
}
