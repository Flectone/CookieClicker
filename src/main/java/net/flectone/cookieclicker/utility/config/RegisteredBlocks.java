package net.flectone.cookieclicker.utility.config;

import net.elytrium.serializer.SerializerConfig;
import net.flectone.cookieclicker.gameplay.blockbreaking.data.BlockType;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;


public class RegisteredBlocks extends FileSerializable {
    private static final SerializerConfig CONFIG = new SerializerConfig.Builder().build();

    public RegisteredBlocks(Path path) {
        super(path);
    }

    private HashMap<String, BlockType> blocks = new HashMap<>();

    public void addBlock(double x, double y, double z, String dimension, BlockType blockType) {
        blocks.put(asPackedPos(x, y, z, dimension), blockType);
    }

    public void removeBlock(double x, double y, double z, String dimension) {
        blocks.remove(asPackedPos(x, y, z, dimension));
    }

    public void removeBlock(String packed) {
        blocks.remove(packed);
    }

    public Optional<BlockType> getBlockType(double x, double y, double z, String dimension) {
        return Optional.ofNullable(blocks.get(asPackedPos(x, y, z, dimension)));
    }

    public Set<String> getAll() {
        return blocks.keySet();
    }

    private String asPackedPos(double x, double y, double z, String dimensionName) {
        return dimensionName + (int) x + "_" + (int) y + "_" + (int) z;
    }
}