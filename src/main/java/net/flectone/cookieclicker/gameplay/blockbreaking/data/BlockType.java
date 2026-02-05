package net.flectone.cookieclicker.gameplay.blockbreaking.data;

import lombok.Getter;
import net.flectone.cookieclicker.items.attributes.ToolType;
import net.flectone.cookieclicker.items.itemstacks.base.data.ItemTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import javax.annotation.Nullable;

@Getter
public enum BlockType {

    MELON_BLOCK (Items.MELON, ItemTag.MELON, 3, 1, ToolType.AXE);

    private final Item mineableBlock;
    private final ItemTag dropType;
    private final int health;

    private final int requiredPower;
    private final ToolType requiredTool;

    BlockType(Item blockType, ItemTag dropType, int health, int requiredPower, ToolType requiredTool) {
        this.mineableBlock = blockType;
        this.dropType = dropType;
        this.health = health;

        this.requiredPower = requiredPower;
        this.requiredTool = requiredTool;
    }

    @Nullable
    public static BlockType fromBlock(Item block) {
        for (BlockType blockType : values()) {
            if (blockType.getMineableBlock() == block) return blockType;
        }
        return null;
    }
}
