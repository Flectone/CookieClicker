package net.flectone.cookieclicker.inventories;

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenWindow;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

@Getter
public class ClickerContainer {
    public static final ClickerContainer EMPTY = new ClickerContainer(0, 0, "empty");

    private final int windowId;
    private final int windowType;
    private final String customData;
    protected final NonNullList<ItemStack> containerItems = NonNullList.create();
    @Setter
    private String title = "новый инвентарь";

    public ClickerContainer(int windowId, int windowType, String customData) {
        this.windowId = windowId;
        this.windowType = windowType;
        this.customData = customData;
        int listSize;
        if (!customData.equals("default")) {
            switch (windowType) {
                // 9 * x инвентари
                case 0, 1, 2, 3, 4, 5 -> listSize = 9 * (windowType + 1);
                case 6 ->  listSize = 9;
                default -> listSize = 0;
            }
            containerItems.addAll(new ArrayList<>(Collections.nCopies(listSize, new ItemStack(Items.AIR))));
        }
    }
    public ClickerContainer(WrapperPlayServerOpenWindow packet) {
        windowId = packet.getContainerId();
        windowType = packet.getType();

        title = packet.getTitle().toString();
        customData = "fromPacket";
    }

    //о нееет, это статика
    public static Integer generateId() {
        Random random = new Random();
        return random.nextInt(99, 1000);
    }

    public void setItem(Integer index, ItemStack itemNMS) {
        if (index > containerItems.size()) return;
        containerItems.set(index, itemNMS);
    }

}
