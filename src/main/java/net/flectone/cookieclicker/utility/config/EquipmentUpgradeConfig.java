package net.flectone.cookieclicker.utility.config;

import lombok.Getter;
import net.elytrium.serializer.SerializerConfig;
import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
import net.flectone.cookieclicker.items.itemstacks.base.data.ItemTag;

import java.nio.file.Path;
import java.util.*;

@Getter
public class EquipmentUpgradeConfig extends FileSerializable {
    private static final SerializerConfig CONFIG = new SerializerConfig.Builder().build();

    public EquipmentUpgradeConfig(Path path) {
        super(path);
    }

    private float multiplicationValue = 1.5f;

    @Comment(@CommentValue("Here you can change star symbol. Minimessage format is supported"))
    private String upgradeSymbol = "<#6800C7>+";

    @Comment(@CommentValue("Armor trim that will be apllied to armor. Without minecraft:"))
    private String armorTrim = "sentry";

    @Comment(@CommentValue("Required item for each level"))
    private List<ItemTag> upgradeItems = new ArrayList<>() {{
        add(ItemTag.CAKE_UPGRADE_ITEM);
        add(ItemTag.CAKE_UPGRADE_ITEM);
        add(ItemTag.CAKE_UPGRADE_ITEM);
        add(ItemTag.CAKE_UPGRADE_ITEM);
        add(ItemTag.CAKE_UPGRADE_ITEM);
        add(ItemTag.COOKIE);
    }};

    @Comment(@CommentValue("Trim material for each level. Default - copper"))
    private List<String> upgradeMaterials = new ArrayList<>() {{
        add("quartz");
        add("emerald");
        add("diamond");
        add("amethyst");
        add("gold");
        add("netherite");
    }};

    public int getMaxLvl() {
        return Math.min(upgradeItems.size(), upgradeMaterials.size());
    }

    public Optional<ItemTag> getRequiredItem(int lvl) {
        if (upgradeItems.size() <= lvl) {
            return Optional.empty();
        }

        return Optional.of(upgradeItems.get(lvl));
    }

    public Optional<String> getLvlMaterial(int lvl) {
        if (upgradeMaterials.size() <= lvl) {
            return Optional.empty();
        }

        return Optional.of(upgradeMaterials.get(lvl));
    }
}
