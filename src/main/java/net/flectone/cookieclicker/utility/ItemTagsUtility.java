package net.flectone.cookieclicker.utility;

import com.google.inject.Singleton;
import net.flectone.cookieclicker.utility.CCobjects.ClickerItems;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.CustomData;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

@Singleton
public class ItemTagsUtility {

    //тут получаем CompoundTag, откуда потом будем брать все параметры
    public CompoundTag getCookiesTags(net.minecraft.world.item.ItemStack itemNMS) {
        if (itemNMS == null) return new CompoundTag();
        CustomData customData = itemNMS.get(DataComponents.CUSTOM_DATA);
        if (customData == null || !customData.contains("cookies")) return new CompoundTag();
        return (CompoundTag) customData.copyTag().get("cookies");
    }
    public CompoundTag getCookiesTags(ItemStack itemNotNMS) {
        if (itemNotNMS == null) return new CompoundTag();
        return getCookiesTags(CraftItemStack.asNMSCopy(itemNotNMS));
    }

    public Integer getStat(ItemStack itemNotNMS, String statName) {
        return getCookiesTags(itemNotNMS).getInt(statName);
    }
    //nms version
    public Integer getStat(net.minecraft.world.item.ItemStack itemNMS, String statName) {
        return getCookiesTags(itemNMS).getInt(statName);
    }


    //Получить фарминг фортуну только с данных, чары не учитываются
    public Integer getBaseFortune(ItemStack item) {
        return getStat(item, ClickerItems.fortuneTag);
    }
    //nms version
    public Integer getBaseFortune(net.minecraft.world.item.ItemStack item) {
        return getStat(item, ClickerItems.fortuneTag);
    }

    public String getAbility(ItemStack itemNotNMS) {
        return getCookiesTags(itemNotNMS).getString("ability");
    }
    //nms version
    public String getAbility(net.minecraft.world.item.ItemStack itemNMS) {
        return getCookiesTags(itemNMS).getString("ability");
    }

    public void setAbility(net.minecraft.world.item.ItemStack itemNMS, String ability) {
        CompoundTag cookiesTag = getCookiesTags(itemNMS);
        cookiesTag.putString("ability", ability);
        CompoundTag baseTag = new CompoundTag();
        baseTag.put("cookies", cookiesTag);
        itemNMS.applyComponents(DataComponentPatch.builder()
                .set(DataComponents.CUSTOM_DATA, CustomData.of(baseTag))
                .build()
        );
    }

    public String getItemTag(ItemStack itemNotNMS) {
        return getCookiesTags(itemNotNMS).getString("item_tag");
    }
    public String getItemTag(net.minecraft.world.item.ItemStack itemNMS) {
        return getCookiesTags(itemNMS).getString("item_tag");
    }
    public void setStat(net.minecraft.world.item.ItemStack itemNMS, String statName, Integer value) {
        CompoundTag cookiesTag = getCookiesTags(itemNMS);
        cookiesTag.putInt(statName, value);
        CompoundTag baseTag = new CompoundTag();
        baseTag.put("cookies", cookiesTag);
        itemNMS.applyComponents(DataComponentPatch.builder()
                        .set(DataComponents.CUSTOM_DATA, CustomData.of(baseTag))
                .build()
        );

    }
}
