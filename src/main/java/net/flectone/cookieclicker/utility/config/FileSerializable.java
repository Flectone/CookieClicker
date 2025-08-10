package net.flectone.cookieclicker.utility.config;

import net.elytrium.serializer.LoadResult;
import net.elytrium.serializer.SerializerConfig;
import net.elytrium.serializer.language.object.YamlSerializable;

import java.nio.file.Path;

//о нет, скопировал код фасэра, ужас
public class FileSerializable extends YamlSerializable {
    private final Path path;
    private static final SerializerConfig CONFIG = new SerializerConfig.Builder().build();

    public FileSerializable(Path path) {
        super(CONFIG);
        this.path = path;
    }

    @Override
    public LoadResult reload() {
        return super.reload(path);
    }

    @Override
    public void save() {
        super.save(path);
    }
}
