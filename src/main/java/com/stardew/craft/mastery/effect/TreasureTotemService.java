package com.stardew.craft.mastery.effect;

import com.stardew.craft.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Treasure Totem 激活 — 按 SDV Object.cs:3246-3275 (treasureTotem) 思路，
 * 适配本 mod 的区域 AABB / 地表方块体系：
 *
 *  1. 确定玩家所在的"采集区域"（依 {@link com.stardew.craft.manager.ArtifactSpotSpawnService}
 *     的 3 个 zone 包围盒），不在任何区域 → 噪音 + 不消耗。
 *  2. 在玩家**脚底那一层 Y** (player.blockPosition().Y - 1)，以玩家为中心，
 *     半径 4 的环（distance == 3）上每格做替换：
 *      - 区域是 MainMap (黄土区) → 把 ModBlocks.YELLOW_DIRT 替换成 ARTIFACT_SPOT_DIRT
 *      - 区域是 Beach (沙滩)     → 把 Blocks.SAND 替换成 BEACH_ARTIFACT_SPOT
 *      - 区域是 Desert (沙漠)    → 把 Blocks.SAND 替换成 DESERT_ARTIFACT_SPOT
 *  3. 仅露天有效（canSeeSky）。
 *  4. 该格不是对应"种床"方块时 → 跳过，不替换其他东西。
 *  5. 内圈 (distance < 3) 仅显示火花粒子，不放方块。
 */
public final class TreasureTotemService {

    private TreasureTotemService() {}

    private static final int RADIUS = 4;
    private static final int RING_DISTANCE = RADIUS - 1; // == 3

    /** 与 ArtifactSpotSpawnService.ZONES 对齐 的 3 个采集区域 AABB（min/max inclusive）。 */
    private enum Zone {
        MAIN_MAP(-151, 63, -237, 200, 91, 79) {
            @Override boolean isSeedBlock(BlockState s) { return s.is(ModBlocks.YELLOW_DIRT.get()); }
            @Override Block artifactSpot() { return ModBlocks.ARTIFACT_SPOT_DIRT.get(); }
        },
        BEACH(-4, 57, 77, 239, 65, 186) {
            @Override boolean isSeedBlock(BlockState s) { return s.is(Blocks.SAND); }
            @Override Block artifactSpot() { return ModBlocks.BEACH_ARTIFACT_SPOT.get(); }
        },
        DESERT(-310, 53, -241, -158, 107, -113) {
            @Override boolean isSeedBlock(BlockState s) { return s.is(Blocks.SAND); }
            @Override Block artifactSpot() { return ModBlocks.DESERT_ARTIFACT_SPOT.get(); }
        };

        final int minX, minY, minZ, maxX, maxY, maxZ;
        Zone(int x1, int y1, int z1, int x2, int y2, int z2) {
            this.minX = Math.min(x1, x2); this.maxX = Math.max(x1, x2);
            this.minY = Math.min(y1, y2); this.maxY = Math.max(y1, y2);
            this.minZ = Math.min(z1, z2); this.maxZ = Math.max(z1, z2);
        }
        boolean contains(int x, int y, int z) {
            return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
        }
        abstract boolean isSeedBlock(BlockState state);
        abstract Block artifactSpot();
    }

    @SuppressWarnings("null")
    public static void activate(Player player) {
        if (!(player.level() instanceof ServerLevel level)) return;

        BlockPos playerPos = player.blockPosition();
        Zone zone = findZone(playerPos);
        if (zone == null) {
            // 不在任何采集区域 — 播放 cancel 音，不消耗物品（调用方需要据此回收）
            level.playSound(null, playerPos, SoundEvents.NOTE_BLOCK_BASS.value(), SoundSource.PLAYERS, 0.6f, 0.6f);
            return;
        }

        level.playSound(null, playerPos,
            SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.PLAYERS, 0.9f, 1.2f);

        int cx = playerPos.getX();
        int cz = playerPos.getZ();
        int footY = playerPos.getY() - 1; // 玩家脚底那层

        for (int dx = -RADIUS; dx < RADIUS; dx++) {
            for (int dz = -RADIUS; dz < RADIUS; dz++) {
                int dist = (int) Math.round(Math.sqrt(dx * dx + dz * dz));
                int x = cx + dx;
                int z = cz + dz;

                if (dist < RING_DISTANCE) {
                    spawnSpiralParticles(level, x, footY, z);
                    continue;
                }
                if (dist != RING_DISTANCE) continue;

                BlockPos foot = new BlockPos(x, footY, z);
                BlockState foundation = level.getBlockState(foot);
                if (!zone.isSeedBlock(foundation)) continue;
                // 仅露天：检查 foot 上方那一格能看天
                if (!level.canSeeSky(foot.above())) continue;

                level.setBlock(foot, zone.artifactSpot().defaultBlockState(),
                    Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);

                spawnReplaceParticles(level, x, footY + 1, z);
            }
        }
    }

    private static Zone findZone(BlockPos pos) {
        for (Zone z : Zone.values()) {
            if (z.contains(pos.getX(), pos.getY(), pos.getZ())) return z;
        }
        return null;
    }

    /** 玩家是否在受支持的采集区域内 — 由 TreasureTotemItem.use 在消耗前检查。 */
    public static boolean canActivateAt(Player player) {
        return findZone(player.blockPosition()) != null;
    }

    private static void spawnReplaceParticles(ServerLevel level, int x, int y, int z) {
        level.sendParticles(ParticleTypes.END_ROD,
            x + 0.5, y + 0.1, z + 0.5,
            6, 0.2, 0.4, 0.2, 0.04);
        level.sendParticles(ParticleTypes.HAPPY_VILLAGER,
            x + 0.5, y + 0.3, z + 0.5,
            3, 0.3, 0.2, 0.3, 0);
    }

    private static void spawnSpiralParticles(ServerLevel level, int x, int y, int z) {
        if (level.getRandom().nextFloat() < 0.5f) {
            level.sendParticles(ParticleTypes.ENCHANT,
                x + 0.5, y + 0.8, z + 0.5,
                2, 0.2, 0.2, 0.2, 0.4);
        }
    }
}
