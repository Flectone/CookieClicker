package net.flectone.cookieclicker.utility.config;

import lombok.Getter;
import net.elytrium.serializer.SerializerConfig;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

@Getter
public class RegisteredEntitiesConfig extends FileSerializable {
    private static final SerializerConfig CONFIG = new SerializerConfig.Builder().build();

    public RegisteredEntitiesConfig(Path path) {
        super(path);
    }

    private Villagers villagers = new Villagers();
    private Set<String> itemFrames = new HashSet<>();

    @Getter
    public static final class Villagers {
        private Set<String> farmers = new HashSet<>();
        private Set<String> armorers = new HashSet<>();

        public Villagers() {}
    }
}
