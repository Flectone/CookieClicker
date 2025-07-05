package net.flectone.cookieclicker.items.itemstacks.base;

import net.flectone.cookieclicker.items.attributes.CookieAbility;
import net.flectone.cookieclicker.items.attributes.ToolType;
import net.flectone.cookieclicker.items.itemstacks.base.data.Features;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.Arrays;

public class BaseCookieItem extends CookieItemStack {

    public BaseCookieItem(Item originalMaterial, Features features) {
        super(originalMaterial, features);

        if (features.getCategory() != ToolType.NONE) {
            removeVisibleAttributes(true);
        }
    }

    public void setItemModel(Item itemModel) {
        StringBuilder stringBuilder = new StringBuilder(itemModel.toString().toLowerCase());
        stringBuilder.delete(0, 10);
        applyComponent(
                DataComponents.ITEM_MODEL,
                ResourceLocation.tryBuild(ResourceLocation.DEFAULT_NAMESPACE, stringBuilder.toString())
        );
    }

    public void hideItem() {
        setItemModel(originalMaterial);
        originalMaterial = HIDDEN_ITEM;
    }

    public void setEnchantmentGlint() {
        applyComponent(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
    }

    public void setName(String text) {
        applyComponent(
                DataComponents.CUSTOM_NAME,
                convertToNMSComponent(miniMessage.deserialize(text))
        );
    }

    public void addLore(String... strings) {
        //если в списке только тег, то пусть после него будет пробел
        lore.addAll(Arrays.asList(strings));
    }

    public void setAbility(CookieAbility cookieAbility) {
        features.ability = cookieAbility;

        if (cookieAbility.getType().equals("infinity")) {
            applyComponent(DataComponents.MAX_DAMAGE, 100);
        }
    }
}
