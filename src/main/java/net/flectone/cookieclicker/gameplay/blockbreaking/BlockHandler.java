package net.flectone.cookieclicker.gameplay.blockbreaking;

import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import com.github.retrooper.packetevents.protocol.particle.Particle;
import com.github.retrooper.packetevents.protocol.particle.type.ParticleTypes;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.sound.Sounds;
import com.github.retrooper.packetevents.protocol.world.BlockFace;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import net.flectone.cookieclicker.entities.objects.mineable.ClickableBlock;
import net.flectone.cookieclicker.entities.playerdata.ServerCookiePlayer;
import net.flectone.cookieclicker.gameplay.blockbreaking.data.BlockType;
import net.flectone.cookieclicker.items.attributes.StatType;
import net.flectone.cookieclicker.items.itemstacks.base.data.Features;
import net.flectone.cookieclicker.utility.PacketUtils;
import net.flectone.cookieclicker.utility.StatsUtils;
import net.flectone.cookieclicker.utility.data.Position;
import net.kyori.adventure.text.Component;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @__(@Inject))
public class BlockHandler {

    private final TaskScheduler taskScheduler;
    private final PacketUtils packetUtils;
    private final BlockDropSpawner dropSpawner;
    private final StatsUtils statsUtils;

    private final HashMap<Integer, ClickableBlock> spawnedBlocks = new HashMap<>();

    public void createBlock(ServerCookiePlayer serverCookiePlayer, BlockType blockType, Position blockPos, BlockFace blockFace) {
        ClickableBlock block = new ClickableBlock(blockType);

        block.setLocation(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        block.shiftDropLocation(blockFace);

        // Спавн блока и сохранение его
        block.spawn(serverCookiePlayer);

        spawnedBlocks.put(block.getInteractionId(), block);
        
        // Запуск таймера, как закончится, удалить блок
        taskScheduler.runTaskLater(() -> remove(serverCookiePlayer, block.getInteractionId(), block), 30 * 20);
    }

    public boolean dealDamage(ServerCookiePlayer serverCookiePlayer, Integer clickedId) {
        if (!spawnedBlocks.containsKey(clickedId)) return false;

        User user = serverCookiePlayer.getUser();

        ClickableBlock block = spawnedBlocks.get(clickedId);

        ItemStack mainHandItem = serverCookiePlayer.getPlayer().getMainHandItem();
        Features mainHandFeatures = new Features(mainHandItem);

        if (mainHandFeatures.getCategory() != block.getRequiredTool()) return false;

        int playerPower = mainHandFeatures.getStat(StatType.MINING_POWER);
        int requiredPower = block.getRequiredPower();
        int miningFortune = statsUtils.getMiningFortune(serverCookiePlayer.getPlayer());

        if (playerPower < requiredPower) {
            user.sendMessage(Component.text("Попробуйте использовать инструменты, у которого сила шахтёра больше или равна " + requiredPower));
            return false;
        }

        // Основной урон по блоку идёт от стата BLOCK_DAMAGE
        int attackDamageStat = statsUtils.getBlockDamage(mainHandItem);
        // Но также, если сила шахтёра выше необходимой,
        // то урон увеличивается на 2 за каждую единицу выше необходимого значения
        int attackDamageFromPower = (playerPower - requiredPower) * 2;

        int totalAttackDamage = attackDamageStat + attackDamageFromPower;

        // Если удар окончательно ломает блок
        if (totalAttackDamage >= block.getHealth()) {
            remove(serverCookiePlayer, block.getInteractionId(), block);
            dropSpawner.spawnDrop(serverCookiePlayer, block, miningFortune);
            return true;
        }

        block.dealDamage(totalAttackDamage);
        displayAttack(user, block.getDropPosition());
        return false;
    }

    private void displayAttack(User user, Position position) {
        packetUtils.playSound(user, Sounds.BLOCK_CALCITE_BREAK, 0.5f, 0.6f);
        packetUtils.spawnParticle(user, new Particle<>(ParticleTypes.SWEEP_ATTACK), 1, position.toVector3d(), 0.1f);
    }

    private void remove(ServerCookiePlayer serverCookiePlayer, Integer entityId, ClickableBlock block) {
        if (!spawnedBlocks.containsKey(entityId)) return;

        block.remove(serverCookiePlayer);

        spawnedBlocks.remove(entityId);
    }
}
