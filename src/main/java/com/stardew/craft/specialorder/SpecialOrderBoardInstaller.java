package com.stardew.craft.specialorder;

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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nonnull;

public final class SpecialOrderBoardInstaller extends SavedData {
    private static final String DATA_NAME = "stardew_special_order_board";
    private static final int SITE_VERSION = 1;
    private static final int FLAGS = Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE;
    public static final BlockPos BOARD_POS = new BlockPos(57, 64, 46);

    private int placedVersion = 0;

    public SpecialOrderBoardInstaller() {
    }

    public static SpecialOrderBoardInstaller get(ServerLevel anyLevelInServer) {
        ServerLevel overworld = anyLevelInServer.getServer().getLevel(net.minecraft.world.level.Level.OVERWORLD);
        if (overworld == null) {
            return new SpecialOrderBoardInstaller();
        }
        return overworld.getDataStorage().computeIfAbsent(factory(), DATA_NAME);
    }

    public void resetForMigration() {
        placedVersion = 0;
        setDirty();
    }

    public void ensurePlaced(ServerLevel stardewLevel) {
        if (placedVersion >= SITE_VERSION) {
            return;
        }
        if (!ModDimensions.STARDEW_VALLEY.equals(stardewLevel.dimension())) {
            return;
        }

        StardewCraft.LOGGER.info("[SPECIAL_ORDERS] Installing special orders board (version {} -> {})", placedVersion, SITE_VERSION);
        placeBoard(stardewLevel);
        placedVersion = SITE_VERSION;
        setDirty();
    }

    private static void placeBoard(ServerLevel level) {
        MapDecorStaticBlock.runWithDropsSuppressed(() -> {
            for (int x = BOARD_POS.getX() - 1; x <= BOARD_POS.getX() + 1; x++) {
                BlockPos clearPos = new BlockPos(x, BOARD_POS.getY(), BOARD_POS.getZ());
                BlockState here = level.getBlockState(clearPos);
                if (here.isAir() || here.is(ModBlocks.SPECIAL_ORDERS_BOARD.get())) {
                    level.setBlock(clearPos, Blocks.AIR.defaultBlockState(), FLAGS);
                }
            }
        });

        BlockState state = ModBlocks.SPECIAL_ORDERS_BOARD.get().defaultBlockState()
            .setValue(MapDecorStaticBlock.PART, MapDecorStaticBlock.Part.MAIN)
            .setValue(MapDecorStaticBlock.FACING, Direction.SOUTH);
        level.setBlock(BOARD_POS, state, FLAGS);
        ModBlocks.SPECIAL_ORDERS_BOARD.get().setPlacedBy(level, BOARD_POS, state, null, ItemStack.EMPTY);
    }

    @Override
    @Nonnull
    public CompoundTag save(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
        tag.putInt("PlacedVersion", placedVersion);
        return tag;
    }

    private static SpecialOrderBoardInstaller load(CompoundTag tag, HolderLookup.Provider provider) {
        SpecialOrderBoardInstaller installer = new SpecialOrderBoardInstaller();
        installer.placedVersion = tag.getInt("PlacedVersion");
        return installer;
    }

    public static SavedData.Factory<SpecialOrderBoardInstaller> factory() {
        return new SavedData.Factory<>(SpecialOrderBoardInstaller::new, SpecialOrderBoardInstaller::load);
    }
}
