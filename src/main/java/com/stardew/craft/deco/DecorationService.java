package com.stardew.craft.deco;

import com.stardew.craft.block.ModBlocks;
import com.stardew.craft.block.utility.FlooringBlock;
import com.stardew.craft.block.utility.WallpaperBlock;
import com.stardew.craft.blockentity.DecorBlockEntity;
import com.stardew.craft.network.payload.OpenDecorationScreenPayload;
import com.stardew.craft.player.PlayerStardewData;
import com.stardew.craft.player.PlayerStardewDataAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class DecorationService {
    private DecorationService() {
    }

    @SuppressWarnings("null")
    public static void openSelection(ServerPlayer player, BlockPos pos, DecorationType type) {
        Level level = player.level();
        BlockState state = level.getBlockState(pos);
        if (!isTargetBlock(state.getBlock(), type)) {
            return;
        }

        PlayerStardewData data = PlayerStardewDataAPI.getData(player);
        List<OpenDecorationScreenPayload.DecorationOption> options = new ArrayList<>();
        for (DecorationStyle style : DecorationStyleRegistry.getStyles(type)) {
            boolean unlocked = data.isDecorationUnlocked(type, style.id());
            options.add(new OpenDecorationScreenPayload.DecorationOption(
                style.id(),
                style.texture().toString(),
                style.texWidth(),
                style.texHeight(),
                style.sourceX(),
                style.sourceY(),
                style.sourceWidth(),
                style.sourceHeight(),
                unlocked,
                style.unlockHintKey(),
                style.sortOrder()
            ));
        }

        options.sort(Comparator
            .comparing((OpenDecorationScreenPayload.DecorationOption o) -> !o.unlocked())
            .thenComparingInt(OpenDecorationScreenPayload.DecorationOption::sortOrder));

        String currentStyle = DecorationStyleRegistry.getDefaultStyleId(type);
        if (level.getBlockEntity(pos) instanceof DecorBlockEntity decorBe) {
            currentStyle = decorBe.getStyleId();
        }

        PacketDistributor.sendToPlayer(player, new OpenDecorationScreenPayload(type.name(), pos, currentStyle, options));
    }

    @SuppressWarnings("null")
    public static int applyToConnected(Level level, BlockPos start, DecorationType type, String styleId) {
        Block targetBlock = type == DecorationType.WALLPAPER ? ModBlocks.WALLPAPER_BLOCK.get() : ModBlocks.FLOORING_BLOCK.get();
        if (!level.getBlockState(start).is(targetBlock)) {
            return 0;
        }

        Set<BlockPos> visited = new HashSet<>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        queue.add(start);

        int changed = 0;
        while (!queue.isEmpty()) {
            BlockPos pos = queue.poll();
            if (!visited.add(pos)) {
                continue;
            }
            if (!level.getBlockState(pos).is(targetBlock)) {
                continue;
            }

            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof DecorBlockEntity decorBe) {
                decorBe.setStyleId(styleId);
                applyVisualState(level, pos, type, styleId);
                changed++;
            } else if (level.getBlockState(pos).getBlock() instanceof EntityBlock entityBlock) {
                BlockEntity created = entityBlock.newBlockEntity(pos, level.getBlockState(pos));
                if (created instanceof DecorBlockEntity decorCreated) {
                    level.setBlockEntity(decorCreated);
                    decorCreated.setStyleId(styleId);
                    applyVisualState(level, pos, type, styleId);
                    changed++;
                }
            }

            queue.add(pos.north());
            queue.add(pos.south());
            queue.add(pos.east());
            queue.add(pos.west());
            queue.add(pos.above());
            queue.add(pos.below());
        }

        return changed;
    }

    private static boolean isTargetBlock(Block block, DecorationType type) {
        return (type == DecorationType.WALLPAPER && block == ModBlocks.WALLPAPER_BLOCK.get())
            || (type == DecorationType.FLOORING && block == ModBlocks.FLOORING_BLOCK.get());
    }

    @SuppressWarnings("null")
    private static void applyVisualState(Level level, BlockPos pos, DecorationType type, String styleId) {
        BlockState current = level.getBlockState(pos);
        int visual = DecorationStyleRegistry.getVisualIndex(type, styleId);
        BlockState updated;
        if (type == DecorationType.WALLPAPER && current.hasProperty(WallpaperBlock.STYLE)) {
            int segment = resolveWallpaperSegment(level, pos);
            updated = current.setValue(WallpaperBlock.STYLE, visual);
            if (updated.hasProperty(WallpaperBlock.SEGMENT)) {
                updated = updated.setValue(WallpaperBlock.SEGMENT, segment);
            }
        } else if (type == DecorationType.FLOORING && current.hasProperty(FlooringBlock.STYLE)) {
            int px = Math.floorMod(pos.getX(), 2);
            int pz = Math.floorMod(pos.getZ(), 2);
            int part = pz * 2 + px;
            updated = current.setValue(FlooringBlock.STYLE, visual);
            if (updated.hasProperty(FlooringBlock.PART)) {
                updated = updated.setValue(FlooringBlock.PART, part);
            }
        } else {
            return;
        }
        if (updated != current) {
            level.setBlock(pos, updated, 3);
        }
    }

    @SuppressWarnings("null")
    private static int resolveWallpaperSegment(Level level, BlockPos pos) {
        Block wallpaper = ModBlocks.WALLPAPER_BLOCK.get();
        int bottomY = pos.getY();
        BlockPos.MutableBlockPos cursor = pos.mutable();
        while (true) {
            cursor.set(pos.getX(), bottomY - 1, pos.getZ());
            if (!level.getBlockState(cursor).is(wallpaper)) {
                break;
            }
            bottomY--;
        }

        int offset = pos.getY() - bottomY;
        return Math.floorMod(offset, 3);
    }
}
