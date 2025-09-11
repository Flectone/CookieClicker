package net.flectone.cookieclicker.utility.config;

import lombok.Getter;
import net.elytrium.serializer.SerializerConfig;
import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

import java.nio.file.Path;

@Getter
public class CookieClickerConfig extends FileSerializable {
    private static final SerializerConfig CONFIG = new SerializerConfig.Builder().build();

    public CookieClickerConfig(Path path) {
        super(path);
    }

    @Comment(@CommentValue("Bonus click reward multiplier (base amount * X)"))
    private int bonusMultiplier = 10;

    @Comment(@CommentValue("Price for Cookie Boost enchantment"))
    private int cookieBoostCost = 30;

    @Comment(@CommentValue("Multiplier for each tier (from Epic Hoe) | amount + amount * (X * tier)"))
    private float tierMultiplier = 0.5f;

    @Comment(@CommentValue("How many items can be spawned. New items will delete existing ones"))
    private int itemsCapacity = 10;

    @Comment(@CommentValue("Controls how many xp required for next lvl"))
    private int lvlRequirement = 90000;

    @Comment(@CommentValue("Controls how many items will be added for enchanted item requirement per lvl"))
    private int scaling = 2;

}
