package net.flectone.cookieclicker.utility.CCobjects;

import net.flectone.cookieclicker.CookieClicker;
import net.minecraft.world.item.ItemStack;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;

public interface ClickerItems {
    public ItemStack toItemStack();
    AttributeModifier modifier = new AttributeModifier(new NamespacedKey(CookieClicker.getPlugin(CookieClicker.class),
            "generic.armor_toughness"),
            0.0,
            AttributeModifier.Operation.ADD_SCALAR,
            EquipmentSlotGroup.HAND);
    String fortuneTag = "ff";
    NamespacedKey itemTagKey = new NamespacedKey("cc2", "custom");
    NamespacedKey fortuneKey = new NamespacedKey("cc2", "ff");
    NamespacedKey abilityKey = new NamespacedKey("cc2", "ability");
    NamespacedKey damageKey = new NamespacedKey("cc2", "dmg");
    NamespacedKey miningFortuneKey = new NamespacedKey("cc2", "mf");


}
