package net.flectone.cookieclicker.events;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.items.ItemManager;
import net.flectone.cookieclicker.utility.CCobjects.CookiePlayer;
import net.flectone.cookieclicker.utility.ItemTagsUtility;
import net.flectone.cookieclicker.utility.Pair;
import net.flectone.cookieclicker.utility.UtilsCookie;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.equipment.trim.ArmorTrim;

import java.util.List;

@Singleton
public class AnvilEvent {
    private final UtilsCookie utilsCookie;
    private final ItemTagsUtility itemTagsUtility;
    private final ItemManager manager;

    @Inject
    public AnvilEvent(UtilsCookie utilsCookie, ItemTagsUtility itemTagsUtility, ItemManager manager) {
        this.utilsCookie = utilsCookie;
        this.itemTagsUtility = itemTagsUtility;
        this.manager = manager;
    }

    public void anvilClick(Player player, Integer slot) {
        //processUpgrade((AnvilMenu) player.containerMenu);
        if (slot != 2) return;

        //в updateStats() есть проверка на воздух, поэтому можно не делать тут проверку
        utilsCookie.updateStats(player.containerMenu.getSlot(2).getItem());
    }

    public void processUpgrade(CookiePlayer cookiePlayer) {
        if (!(cookiePlayer.getPlayer().containerMenu instanceof AnvilMenu anvilMenu))
            return;
        ItemStack firstItem = anvilMenu.getItems().getFirst();
        ItemStack secondItem = anvilMenu.getItems().get(1);
        CompoundTag compoundTag = itemTagsUtility.getCookiesTags(firstItem);

        if (firstItem.getItem().equals(Items.AIR) || secondItem.getItem().equals(Items.AIR))
            return;

        if (!compoundTag.contains("category") || !compoundTag.getString("category").equals("armor"))
            return;

        ItemStack upgraded = updateArmorToNextTier(firstItem, secondItem);
        if (upgraded == null)
            return;

        anvilMenu.setItem(2, 0, upgraded);
        anvilMenu.cost.set(1);
    }

    public ItemStack updateArmorToNextTier(net.minecraft.world.item.ItemStack itemStack, net.minecraft.world.item.ItemStack upgradeItem) {
        ItemStack updatedArmorItemStack = itemStack.copy();
        CompoundTag compoundTag = itemTagsUtility.getCookiesTags(itemStack);
        int currentTier = compoundTag.contains("tier") ? compoundTag.getInt("tier") : 0;

        if (!compoundTag.contains("category") || !compoundTag.getString("category").equals("armor"))
            return itemStack;

        List<Pair<String, String>> upgradeStages = List.of(
                new Pair<>("final_cake", "quartz"),
                new Pair<>("final_cake", "emerald"),
                new Pair<>("final_cake", "diamond"),
                new Pair<>("final_cake", "amethyst"),
                new Pair<>("final_cake", "gold"),
                new Pair<>("cookie", "netherite")
        );

        if (currentTier > upgradeStages.size() || !utilsCookie.compare(upgradeItem, manager.getNMS(upgradeStages.get(currentTier).getKey())))
            return null;


        ArmorTrim armorTrim = new ArmorTrim(utilsCookie.getFromRegistry(Registries.TRIM_MATERIAL, "minecraft:" + upgradeStages.get(currentTier).getValue()),
                utilsCookie.getFromRegistry(Registries.TRIM_PATTERN, "minecraft:sentry"), false);

        compoundTag.putInt("tier", currentTier + 1);
        compoundTag.putInt("ff", compoundTag.getInt("ff") + 30);

        CompoundTag cookieTag = new CompoundTag();
        cookieTag.put("cookies", compoundTag);

        DataComponentPatch.Builder dataComponentBuilder = DataComponentPatch.builder();
        dataComponentBuilder.set(DataComponents.TRIM, armorTrim);
        dataComponentBuilder.set(DataComponents.CUSTOM_DATA, CustomData.of(cookieTag));
        if (itemStack.getCustomName() != null) {
            MutableComponent mutableComponent = MutableComponent.create(PlainTextContents.EMPTY);
            mutableComponent.append(itemStack.getCustomName());
            mutableComponent.append(Component.literal("+").setStyle(Style.EMPTY.withColor(10630898).withItalic(false)));
            dataComponentBuilder.set(DataComponents.CUSTOM_NAME, mutableComponent);
        }

        updatedArmorItemStack.applyComponents(dataComponentBuilder.build());
        return updatedArmorItemStack;
    }
}
