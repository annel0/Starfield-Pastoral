package com.stardew.craft.communitycenter;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.block.ModBlocks;
import com.stardew.craft.communitycenter.block.JunimoNoteBlock;
import com.stardew.craft.communitycenter.restore.CCAreaRegistry;
import com.stardew.craft.communitycenter.state.CommunityCenterProgress;
import com.stardew.craft.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

/**
 * 负责在 CC 结构加载后放置 / 移除 JunimoNote 方块。
 * <p>
 * SDV parity: JunimoNote 按区域解锁顺序分批出现，由
 * {@link CommunityCenterProgress#shouldNoteAppearInArea(int)} 控制。
 * 未解锁的区域不会放置 note 方块。
 */
@SuppressWarnings("null")
public final class JunimoNotePlacer {

    private JunimoNotePlacer() {}

    /**
     * 为应该出现（但未完成）的区域放置 JunimoNote，移除已完成或不应出现的。
     * 在 CC 结构加载后调用，也应在 bundle 完成后调用刷新。
     * <p>
     * SDV parity: doCheckForNewJunimoNotes() → addJunimoNote() + sound "reward" + burst particles
     *
     * @return list of area IDs for newly placed notes (empty if none)
     */
    public static List<Integer> ensureJunimoNotes(ServerLevel level) {
        List<Integer> newlyPlaced = new ArrayList<>();

        for (var entry : CCAreaRegistry.ALL_AREAS.entrySet()) {
            int areaId = entry.getKey();
            CCAreaRegistry.AreaBounds bounds = entry.getValue();
            BlockPos worldPos = bounds.noteWorldPos();

            boolean shouldExist = CommunityCenterProgress.shouldNoteAppearInArea(areaId);

            if (shouldExist) {
                // 未完成且已解锁：放置 note 方块
                BlockState noteState = ModBlocks.JUNIMO_NOTE.get()
                        .defaultBlockState()
                        .setValue(JunimoNoteBlock.AREA, areaId);
                BlockState existing = level.getBlockState(worldPos);
                if (!(existing.getBlock() instanceof JunimoNoteBlock)) {
                    level.setBlock(worldPos, noteState, 2);
                    newlyPlaced.add(areaId);
                    StardewCraft.LOGGER.debug("[CC] Placed JunimoNote for area {} at {}", areaId, worldPos);

                    // SDV parity: addJunimoNote() plays "reward" sound + burst particles (4 sparkles)
                    level.playSound(null, worldPos, ModSounds.REWARD.get(),
                            SoundSource.NEUTRAL, 1.0f, 1.0f);
                    level.sendParticles(ParticleTypes.END_ROD,
                            worldPos.getX() + 0.5, worldPos.getY() + 0.5, worldPos.getZ() + 0.5,
                            8, 0.3, 0.3, 0.3, 0.05);
                }
            } else {
                // 已完成或未解锁：如果 note 方块还在，移除它
                BlockState existing = level.getBlockState(worldPos);
                if (existing.getBlock() instanceof JunimoNoteBlock) {
                    level.removeBlock(worldPos, false);
                    StardewCraft.LOGGER.debug("[CC] Removed JunimoNote for area {} at {}", areaId, worldPos);
                }
            }
        }

        return newlyPlaced;
    }
}
