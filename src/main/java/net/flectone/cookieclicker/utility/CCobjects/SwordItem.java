package net.flectone.cookieclicker.utility.CCobjects;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.flectone.cookieclicker.CookieClicker;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;

public class SwordItem extends ItemBase{
    Double damage = 1.0;

    public SwordItem(Material itemType, String displayName, String itemTag, Double damage) {
        this.itemTag = itemTag;
        this.itemType = itemType;
        this.displayName = displayName;
        this.firstMeta = (new ItemStack(itemType)).getItemMeta();
        fullLore.add(miniMessage.deserialize("<dark_gray> #cookieclicker"));
    }
    public void setDamage(Double dmg) {
        AttributeModifier modifier = new AttributeModifier(new NamespacedKey(CookieClicker.getPlugin(CookieClicker.class),
                "generic.attack_damage"),
                dmg,
                AttributeModifier.Operation.ADD_SCALAR,
                EquipmentSlotGroup.HAND);
        firstMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE, modifier);
    }
}
