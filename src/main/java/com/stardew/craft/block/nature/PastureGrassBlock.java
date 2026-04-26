package com.stardew.craft.block.nature;

import com.stardew.craft.animal.data.AnimalWorldData;
import com.stardew.craft.block.ModBlocks;
import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.item.tool.ScytheItem;
import com.stardew.craft.network.HayHarvestHudMessagePacket;
import com.stardew.craft.time.StardewTimeManager;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import java.util.ArrayList;
import java.util.List;

public class PastureGrassBlock extends BushBlock {
    public static final MapCodec<PastureGrassBlock> CODEC = simpleCodec(PastureGrassBlock::new);
    public static final IntegerProperty VARIANT = IntegerProperty.create("variant", 0, 2);

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    @SuppressWarnings("null")
    public PastureGrassBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(VARIANT, 0));
    }

    @Override
    protected void createBlockStateDefinition(@SuppressWarnings("null") StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(VARIANT);
    }

    @SuppressWarnings("null")
    @Override
    protected boolean mayPlaceOn(@SuppressWarnings("null") BlockState state, @SuppressWarnings("null") BlockGetter level, @SuppressWarnings("null") BlockPos pos) {
        return state.is(BlockTags.DIRT) || state.is(BlockTags.SAND) || state.is(BlockTags.BASE_STONE_OVERWORLD) || state.isFaceSturdy(level, pos, Direction.UP);
    }

    @SuppressWarnings("null")
    @Override
    public BlockState getStateForPlacement(@SuppressWarnings("null") net.minecraft.world.item.context.BlockPlaceContext context) {
        int variant = context.getLevel().getRandom().nextInt(3);
        return defaultBlockState().setValue(VARIANT, variant);
    }

    @SuppressWarnings("null")
    @Override
    protected void randomTick(@SuppressWarnings("null") BlockState state, @SuppressWarnings("null") ServerLevel level, @SuppressWarnings("null") BlockPos pos, @SuppressWarnings("null") RandomSource random) {
        // 仅在 Stardew Valley 维度内生效（冬季消失 + 扩散），其他维度完全不处理。
        if (level.dimension() != ModDimensions.STARDEW_VALLEY) {
            return;
        }
        // 冬季在 SDV 维度内自动消失（与 SDV 一致）
        if (StardewTimeManager.get().getCurrentSeason() == 3) {
            level.removeBlock(pos, false);
            return;
        }

        // 扩散：极低概率 + 单方向尝试 + 全 O(1) 检查，零额外扫描，对服务器最友好。
        // 参考 SDV growWeedGrass：原版每天结算一次、每株 65% 尝试、4 邻 25% 落地。
        // MC randomTick 触发频率高得多（默认 ~3/section/tick），所以基础概率必须压到极低。
        // 1/24 + 单邻 = 期望每 24 次 randomTick 才放一格，长草节奏接近 SDV 的"几天才铺一片"。
        if (random.nextInt(24) != 0) {
            return;
        }
        Direction dir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        BlockPos targetPos = pos.relative(dir);
        // 必须已加载，避免触发邻区块加载
        if (!level.isLoaded(targetPos)) {
            return;
        }
        if (!level.getBlockState(targetPos).isAir()) {
            return;
        }
        BlockPos belowTarget = targetPos.below();
        BlockState belowState = level.getBlockState(belowTarget);
        if (!mayPlaceOn(belowState, level, belowTarget)) {
            return;
        }
        // VARIANT 仅材质差异（0/1/2 三种贴图），随机一个即可。
        int variant = random.nextInt(3);
        level.setBlock(targetPos,
            this.defaultBlockState().setValue(VARIANT, variant),
            net.minecraft.world.level.block.Block.UPDATE_ALL);
    }

    @SuppressWarnings("null")
    public static boolean cutWithScythe(ServerLevel level, BlockPos pos, ServerPlayer player, ScytheItem scythe) {
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof PastureGrassBlock)) {
            return false;
        }
        boolean blueGrass = state.is(ModBlocks.BLUE_PASTURE_GRASS.get());

        level.removeBlock(pos, false);

        int hayCount = rollHayCount(level, scythe, level.getRandom());
        if (blueGrass && hayCount > 0) {
            hayCount = 2;
        }
        if (hayCount <= 0) {
            return true;
        }

        AnimalWorldData data = AnimalWorldData.get(level);
        int stored = data.storeHay(player.getUUID(), hayCount);
        if (stored > 0) {
            HayHarvestHudMessagePacket.sendTo(player, stored, false);
        }
        // No silo or silo full: hay is simply lost (SDV parity — no drop, no message)
        return true;
    }

    private static int rollHayCount(ServerLevel level, ScytheItem scythe, RandomSource random) {
        double chance = switch (scythe.getTier()) {
            case NORMAL -> 0.50;
            case GOLD -> 0.75;
            case IRIDIUM -> 1.00;
        };

        if (level.dimension() == ModDimensions.STARDEW_VALLEY && StardewTimeManager.get().getCurrentSeason() == 3) {
            chance *= 0.33;
        }

        int count = random.nextDouble() < chance ? 1 : 0;
        if (count > 0 && random.nextDouble() < 0.10) {
            count++;
        }
        return count;
    }

    @SuppressWarnings("null")
    public static boolean consumeForAnimal(ServerLevel level, BlockPos pos) {
        return consumeForAnimal(level, pos, 1);
    }

    @SuppressWarnings("null")
    public static boolean consumeForAnimal(ServerLevel level, BlockPos pos, int clumpsNeeded) {
        if (clumpsNeeded <= 0) {
            return true;
        }

        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof PastureGrassBlock)) {
            return false;
        }

        List<BlockPos> clumps = collectNearbySameTypeGrass(level, pos, state, clumpsNeeded);
        if (clumps.size() < clumpsNeeded) {
            return false;
        }

        for (int i = 0; i < clumpsNeeded; i++) {
            level.removeBlock(clumps.get(i), false);
        }
        return true;
    }

    private static List<BlockPos> collectNearbySameTypeGrass(ServerLevel level, BlockPos origin, BlockState targetState, int clumpsNeeded) {
        List<BlockPos> result = new ArrayList<>(clumpsNeeded);
        result.add(origin.immutable());
        if (clumpsNeeded == 1) {
            return result;
        }

        int radius = 5;
        for (int dist = 1; dist <= radius && result.size() < clumpsNeeded; dist++) {
            for (int x = origin.getX() - dist; x <= origin.getX() + dist && result.size() < clumpsNeeded; x++) {
                for (int y = origin.getY() - 1; y <= origin.getY() + 1 && result.size() < clumpsNeeded; y++) {
                    for (int z = origin.getZ() - dist; z <= origin.getZ() + dist && result.size() < clumpsNeeded; z++) {
                        BlockPos candidate = new BlockPos(x, y, z);
                        if (candidate.equals(origin)) {
                            continue;
                        }
                        BlockState candidateState = level.getBlockState(candidate);
                        if (!(candidateState.getBlock() instanceof PastureGrassBlock)) {
                            continue;
                        }
                        if (candidateState.getBlock() != targetState.getBlock()) {
                            continue;
                        }
                        result.add(candidate.immutable());
                    }
                }
            }
        }

        return result;
    }

    @SuppressWarnings("null")
    @Override
    protected boolean canSurvive(@SuppressWarnings("null") BlockState state, @SuppressWarnings("null") LevelReader level, @SuppressWarnings("null") BlockPos pos) {
        BlockPos below = pos.below();
        return mayPlaceOn(level.getBlockState(below), level, below);
    }
}
