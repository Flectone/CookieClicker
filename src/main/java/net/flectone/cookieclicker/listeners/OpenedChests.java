package net.flectone.cookieclicker.listeners;

import com.comphenix.protocol.wrappers.BlockPosition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class OpenedChests {
    public static HashMap<UUID, ArrayList<BlockPosition>> playersOpenedChest = new HashMap<>();

    public static void addChestToPlayer (UUID id, BlockPosition chestPos) {
        if (playersOpenedChest.get(id) == null) {
            ArrayList<BlockPosition> chests = new ArrayList<>();
            chests.add(chestPos);
            playersOpenedChest.put(id, chests);
        } else {
            ArrayList<BlockPosition> chests = playersOpenedChest.get(id);
            chests.add(chestPos);
            playersOpenedChest.put(id, chests);
        }
    }

    public static boolean isPlayerOpenedChest(UUID id, BlockPosition pos) {
        if (playersOpenedChest.get(id) ==  null)
            return false;
        ArrayList<BlockPosition> openedChests = playersOpenedChest.get(id);
        if (openedChests.contains(pos))
            return true;
        else
            return false;
    }

    public static void removeOpenedChest (UUID id, BlockPosition chestPos) {
        ArrayList<BlockPosition> chests = playersOpenedChest.get(id);
        chests.remove(chestPos);
        playersOpenedChest.replace(id, chests);
    }
}
