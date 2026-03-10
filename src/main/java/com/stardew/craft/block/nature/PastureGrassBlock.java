package com.stardew.craft.block.nature;

import com.stardew.craft.animal.data.AnimalWorldData;
import com.stardew.craft.block.ModBlocks;
import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.item.ModItems;
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
import net.minecraft.world.item.ItemStack;
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
        if (level.dimension() == ModDimensions.STARDEW_VALLEY && StardewTimeManager.get().getCurrentSeason() == 3) {
            level.removeBlock(pos, false);
        }
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
            return true;
        }

        HayHarvestHudMessagePacket.sendTo(player, hayCount, true);
        popResource(level, pos, new ItemStack(ModItems.HAY.get(), hayCount));
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
