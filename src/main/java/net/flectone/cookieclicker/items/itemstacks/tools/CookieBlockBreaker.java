package net.flectone.cookieclicker.items.itemstacks.tools;

import net.flectone.cookieclicker.items.attributes.ToolType;
import net.flectone.cookieclicker.items.itemstacks.base.data.ItemTag;
import net.minecraft.advancements.criterion.BlockPredicate;
import net.minecraft.advancements.criterion.DataComponentMatchers;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Unit;
import net.minecraft.world.item.AdventureModePredicate;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.*;

public class CookieBlockBreaker extends ToolCookieItem {

    private final List<Item> canBreakList = new ArrayList<>();

    protected CookieBlockBreaker(Item originalMaterial, ItemTag tag, String name, ToolType category) {
        super(originalMaterial, tag, name, category);

        applyComponent(DataComponents.UNBREAKABLE, Unit.INSTANCE);
    }

    public void addBlocks(Item... items) {
        canBreakList.addAll(Arrays.asList(items));
    }

    private void addCanBreak(AdventureModePredicate adventureModePredicate) {
        applyComponent(DataComponents.CAN_BREAK, adventureModePredicate);

        applyComponent(DataComponents.TOOLTIP_DISPLAY,
                new TooltipDisplay(false, new LinkedHashSet<>(List.of(DataComponents.CAN_BREAK, DataComponents.UNBREAKABLE))));
    }

    private AdventureModePredicate getBlocksPredicate() {
        List<Holder<Block>> blockHolders = new ArrayList<>();

        Optional<Registry<Block>> blockRegistry = MinecraftServer.getServer().registryAccess().lookup(Registries.BLOCK);

        blockRegistry.ifPresent(registry -> registry.asHolderIdMap().forEach(blockHolder -> {
            if (canBreakList.contains(blockHolder.value().asItem())) {
                blockHolders.add(blockHolder);
            }
        }));

        BlockPredicate blockPredicate = new BlockPredicate(
                Optional.of(HolderSet.direct(blockHolders)),
                Optional.empty(),
                Optional.empty(),
                DataComponentMatchers.ANY
        );

        return new AdventureModePredicate(List.of(blockPredicate));
    }

    @Override
    public ItemStack toMinecraftStack() {
       AdventureModePredicate predicate = getBlocksPredicate();
       addCanBreak(predicate);

       return super.toMinecraftStack();
    }
}
