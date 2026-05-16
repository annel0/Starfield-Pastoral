package com.stardew.craft.desert;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.block.ModBlocks;
import com.stardew.craft.block.decor.MapDecorStaticBlock;
import com.stardew.craft.core.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nonnull;

public final class DesertGalaxyPillarBootstrap {

    public static final BlockPos NORTH_PILLAR_POS = new BlockPos(-201, 64, -200);
    public static final BlockPos EAST_PILLAR_POS = new BlockPos(-198, 64, -205);
    public static final BlockPos WEST_PILLAR_POS = new BlockPos(-195, 64, -200);
    public static final BlockPos RITUAL_TRIGGER_POS = new BlockPos(-198, 64, -202);

    private static final int PILLAR_LAYOUT_VERSION = 2;
    private static final int CHECK_INTERVAL_TICKS = 20;
    private static int tickCounter;

    private DesertGalaxyPillarBootstrap() {}

    public static void ensurePlaced(ServerLevel level, String reason) {
        if (!ModDimensions.STARDEW_VALLEY.equals(level.dimension())) {
            return;
        }

        PillarSavedData data = PillarSavedData.get(level);
        if (data.version == PILLAR_LAYOUT_VERSION && data.placed) {
            return;
        }
        if (DesertMapBootstrap.isPlacementInProgress()) {
            return;
        }

        placePillar(level, NORTH_PILLAR_POS, Direction.SOUTH);
        placePillar(level, EAST_PILLAR_POS, Direction.WEST);
        placePillar(level, WEST_PILLAR_POS, Direction.EAST);

        data.version = PILLAR_LAYOUT_VERSION;
        data.placed = true;
        data.setDirty();

        StardewCraft.LOGGER.info("[DESERT] Galaxy pillars placed. reason={}, trigger={}", reason, RITUAL_TRIGGER_POS);
    }

    public static void tick(ServerLevel level) {
        tickCounter++;
        if (tickCounter < CHECK_INTERVAL_TICKS) {
            return;
        }
        tickCounter = 0;
        ensurePlaced(level, "level_tick");
    }

    private static void placePillar(ServerLevel level, BlockPos pos, Direction facing) {
        level.getChunkAt(pos);

        Block block = ModBlocks.GALAXY_PILLAR.get();
        BlockState state = block.defaultBlockState()
                .setValue(MapDecorStaticBlock.PART, MapDecorStaticBlock.Part.MAIN)
                .setValue(MapDecorStaticBlock.FACING, facing);

        level.setBlock(pos, state, 3);
        block.setPlacedBy(level, pos, state, null, ItemStack.EMPTY);
    }

    static final class PillarSavedData extends SavedData {
        private static final String DATA_NAME = "stardew_desert_galaxy_pillars";

        int version;
        boolean placed;

        static PillarSavedData get(ServerLevel level) {
            return level.getDataStorage().computeIfAbsent(
                    new SavedData.Factory<>(PillarSavedData::new, PillarSavedData::load),
                    DATA_NAME
            );
        }

        static PillarSavedData load(CompoundTag tag, HolderLookup.Provider provider) {
            PillarSavedData data = new PillarSavedData();
            data.version = tag.getInt("version");
            data.placed = tag.getBoolean("placed");
            return data;
        }

        @Override
        public @Nonnull CompoundTag save(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider provider) {
            tag.putInt("version", version);
            tag.putBoolean("placed", placed);
            return tag;
        }
    }
}