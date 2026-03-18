package com.stardew.craft.blockentity;

import com.stardew.craft.deco.DecorAnchorStyleRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

@SuppressWarnings("null")
public class DecorAnchorBlockEntity extends net.minecraft.world.level.block.entity.BlockEntity {
    private static final String TAG_STYLE_ID = "StyleId";
    private static final String TAG_OFFSET_X = "OffsetX";
    private static final String TAG_OFFSET_Y = "OffsetY";
    private static final String TAG_OFFSET_Z = "OffsetZ";
    private static final String TAG_ROT_X = "RotX";
    private static final String TAG_ROT_Y = "RotY";
    private static final String TAG_ROT_Z = "RotZ";
    private static final String TAG_SCALE_X = "ScaleX";
    private static final String TAG_SCALE_Y = "ScaleY";
    private static final String TAG_SCALE_Z = "ScaleZ";

    private String styleId = DecorAnchorStyleRegistry.defaultStyleId();
    private float offsetX;
    private float offsetY;
    private float offsetZ;
    private float rotX;
    private float rotY;
    private float rotZ;
    private float scaleX = 1.0f;
    private float scaleY = 1.0f;
    private float scaleZ = 1.0f;

    public DecorAnchorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DECOR_ANCHOR.get(), pos, state);
    }

    public String getStyleId() {
        return styleId;
    }

    public float getOffsetX() {
        return offsetX;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public float getOffsetZ() {
        return offsetZ;
    }

    public float getRotX() {
        return rotX;
    }

    public float getRotY() {
        return rotY;
    }

    public float getRotZ() {
        return rotZ;
    }

    public float getScaleX() {
        return scaleX;
    }

    public float getScaleY() {
        return scaleY;
    }

    public float getScaleZ() {
        return scaleZ;
    }

    public void setEditorState(String styleId, float offsetX, float offsetY, float offsetZ, float rotX, float rotY, float rotZ, float scaleX, float scaleY, float scaleZ) {
        if (styleId == null || styleId.isBlank()) {
            return;
        }
        this.styleId = styleId;
        this.offsetX = Mth.clamp(offsetX, -1.5f, 1.5f);
        this.offsetY = Mth.clamp(offsetY, -1.5f, 1.5f);
        this.offsetZ = Mth.clamp(offsetZ, -1.5f, 1.5f);
        this.rotX = normalizeAngle(rotX);
        this.rotY = normalizeAngle(rotY);
        this.rotZ = normalizeAngle(rotZ);
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.scaleZ = scaleZ;
        setChanged();
        if (level != null) {
            syncBlockStateStyle();
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        syncBlockStateStyle();
    }

    private void syncBlockStateStyle() {
        if (level == null) {
            return;
        }
        BlockState state = getBlockState();
        if (!state.hasProperty(com.stardew.craft.block.utility.DecorAnchorBlock.STYLE)) {
            return;
        }
        int styleIdx = DecorAnchorStyleRegistry.toIndex(styleId);
        if (state.getValue(com.stardew.craft.block.utility.DecorAnchorBlock.STYLE) != styleIdx) {
            level.setBlock(getBlockPos(), state.setValue(com.stardew.craft.block.utility.DecorAnchorBlock.STYLE, styleIdx), 3);
        }
    }

    private static float normalizeAngle(float value) {
        float wrapped = value % 360.0f;
        if (wrapped > 180.0f) {
            wrapped -= 360.0f;
        } else if (wrapped < -180.0f) {
            wrapped += 360.0f;
        }
        return wrapped;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putString(TAG_STYLE_ID, styleId);
        tag.putFloat(TAG_OFFSET_X, offsetX);
        tag.putFloat(TAG_OFFSET_Y, offsetY);
        tag.putFloat(TAG_OFFSET_Z, offsetZ);
        tag.putFloat(TAG_ROT_X, rotX);
        tag.putFloat(TAG_ROT_Y, rotY);
        tag.putFloat(TAG_ROT_Z, rotZ);
        tag.putFloat(TAG_SCALE_X, scaleX);
        tag.putFloat(TAG_SCALE_Y, scaleY);
        tag.putFloat(TAG_SCALE_Z, scaleZ);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        styleId = tag.contains(TAG_STYLE_ID) ? tag.getString(TAG_STYLE_ID) : DecorAnchorStyleRegistry.defaultStyleId();
        offsetX = tag.getFloat(TAG_OFFSET_X);
        offsetY = tag.getFloat(TAG_OFFSET_Y);
        offsetZ = tag.getFloat(TAG_OFFSET_Z);
        rotX = normalizeAngle(tag.getFloat(TAG_ROT_X));
        rotY = normalizeAngle(tag.getFloat(TAG_ROT_Y));
        rotZ = normalizeAngle(tag.getFloat(TAG_ROT_Z));
        scaleX = tag.contains(TAG_SCALE_X) ? tag.getFloat(TAG_SCALE_X) : 1.0f;
        scaleY = tag.contains(TAG_SCALE_Y) ? tag.getFloat(TAG_SCALE_Y) : 1.0f;
        scaleZ = tag.contains(TAG_SCALE_Z) ? tag.getFloat(TAG_SCALE_Z) : 1.0f;
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
}
