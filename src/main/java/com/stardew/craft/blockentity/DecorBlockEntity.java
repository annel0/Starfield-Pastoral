package com.stardew.craft.blockentity;

import com.stardew.craft.block.utility.FlooringBlock;
import com.stardew.craft.block.utility.WallpaperBlock;
import com.stardew.craft.deco.DecorationStyleRegistry;
import com.stardew.craft.deco.DecorationType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

@SuppressWarnings("null")
public class DecorBlockEntity extends net.minecraft.world.level.block.entity.BlockEntity {
    private static final String TAG_STYLE_ID = "StyleId";

    private String styleId;

    public DecorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DECOR_BLOCK.get(), pos, state);
        this.styleId = DecorationStyleRegistry.getDefaultStyleId(resolveType(state));
    }

    public String getStyleId() {
        return styleId;
    }

    @SuppressWarnings("null")
    public void setStyleId(String styleId) {
        if (styleId == null || styleId.isBlank()) {
            return;
        }
        this.styleId = styleId;
        setChanged();
        if (level != null) {
            syncVisualState();
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    private DecorationType resolveType(BlockState state) {
        if (state.getBlock() == com.stardew.craft.block.ModBlocks.WALLPAPER_BLOCK.get()) {
            return DecorationType.WALLPAPER;
        }
        return DecorationType.FLOORING;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putString(TAG_STYLE_ID, styleId);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        styleId = tag.contains(TAG_STYLE_ID)
            ? tag.getString(TAG_STYLE_ID)
            : resolveStyleIdFromBlockState(getBlockState());
    }

    @Override
    public void onLoad() {
        super.onLoad();
        syncVisualState();
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return saveWithoutMetadata(provider);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider provider) {
        loadAdditional(tag, provider);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private void syncVisualState() {
        if (level == null) {
            return;
        }
        BlockState current = getBlockState();
        DecorationType type = resolveType(current);
        int visual = DecorationStyleRegistry.getVisualIndex(type, styleId);
        BlockState updated = current;
        if (type == DecorationType.WALLPAPER && current.hasProperty(WallpaperBlock.STYLE)) {
            if (current.getValue(WallpaperBlock.STYLE) != visual) {
                updated = current.setValue(WallpaperBlock.STYLE, visual);
            }
            if (updated.hasProperty(WallpaperBlock.SEGMENT)) {
                int segment = resolveWallpaperSegment();
                if (updated.getValue(WallpaperBlock.SEGMENT) != segment) {
                    updated = updated.setValue(WallpaperBlock.SEGMENT, segment);
                }
            }
        } else if (type == DecorationType.FLOORING && current.hasProperty(FlooringBlock.STYLE)) {
            if (current.getValue(FlooringBlock.STYLE) != visual) {
                updated = current.setValue(FlooringBlock.STYLE, visual);
            }
            if (updated.hasProperty(FlooringBlock.PART)) {
                int px = Math.floorMod(getBlockPos().getX(), 2);
                int pz = Math.floorMod(getBlockPos().getZ(), 2);
                int part = pz * 2 + px;
                if (updated.getValue(FlooringBlock.PART) != part) {
                    updated = updated.setValue(FlooringBlock.PART, part);
                }
            }
        }
        if (updated != current) {
            level.setBlock(getBlockPos(), updated, 3);
        }
    }

    private String resolveStyleIdFromBlockState(BlockState state) {
        DecorationType type = resolveType(state);
        int visual = 0;
        if (type == DecorationType.WALLPAPER && state.hasProperty(WallpaperBlock.STYLE)) {
            visual = state.getValue(WallpaperBlock.STYLE);
        } else if (type == DecorationType.FLOORING && state.hasProperty(FlooringBlock.STYLE)) {
            visual = state.getValue(FlooringBlock.STYLE);
        }

        List<com.stardew.craft.deco.DecorationStyle> styles = DecorationStyleRegistry.getStyles(type);
        if (visual >= 0 && visual < styles.size()) {
            return styles.get(visual).id();
        }

        return DecorationStyleRegistry.getDefaultStyleId(type);
    }

    private int resolveWallpaperSegment() {
        if (level == null) {
            return 0;
        }
        int bottomY = getBlockPos().getY();
        BlockPos.MutableBlockPos cursor = getBlockPos().mutable();
        while (true) {
            cursor.set(getBlockPos().getX(), bottomY - 1, getBlockPos().getZ());
            if (!level.getBlockState(cursor).is(com.stardew.craft.block.ModBlocks.WALLPAPER_BLOCK.get())) {
                break;
            }
            bottomY--;
        }
        int offset = getBlockPos().getY() - bottomY;
        return Math.floorMod(offset, 3);
    }
}
