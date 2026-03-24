package com.stardew.craft.blockentity;

import com.stardew.craft.museum.MuseumDonationData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

import javax.annotation.Nonnull;
import java.util.Objects;

public class MuseumExhibitStandBlockEntity extends BlockEntity {
    private static final String TAG_DISPLAY_ITEM = "DisplayItem";

    private ItemStack displayItem = ItemStack.EMPTY;

    public MuseumExhibitStandBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MUSEUM_EXHIBIT_STAND.get(), pos, state);
    }

    public ItemStack getDisplayItem() {
        return displayItem;
    }

    public boolean hasDisplayItem() {
        return !displayItem.isEmpty();
    }

    public void setDisplayItem(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            displayItem = ItemStack.EMPTY;
        } else {
            ItemStack one = stack.copy();
            one.setCount(1);
            displayItem = one;
        }

        setChanged();
        if (level instanceof ServerLevel serverLevel) {
            String standKey = MuseumDonationData.standKey(serverLevel, worldPosition);
            MuseumDonationData data = MuseumDonationData.get(serverLevel);
            if (displayItem.isEmpty()) {
                data.setStandDisplayedItem(standKey, null);
            } else {
                net.minecraft.resources.ResourceLocation itemKey = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(Objects.requireNonNull(displayItem.getItem()));
                if (itemKey != null) {
                    String itemId = itemKey.toString();
                    data.setStandDisplayedItem(standKey, itemId);
                }
            }
            serverLevel.sendBlockUpdated(Objects.requireNonNull(worldPosition), Objects.requireNonNull(getBlockState()), Objects.requireNonNull(getBlockState()), 3);
        }
    }

    public ItemStack removeDisplayItem() {
        ItemStack out = displayItem;
        displayItem = ItemStack.EMPTY;
        setChanged();

        if (level instanceof ServerLevel serverLevel) {
            String standKey = MuseumDonationData.standKey(serverLevel, worldPosition);
            MuseumDonationData data = MuseumDonationData.get(serverLevel);
            data.setStandDisplayedItem(standKey, null);
            serverLevel.sendBlockUpdated(Objects.requireNonNull(worldPosition), Objects.requireNonNull(getBlockState()), Objects.requireNonNull(getBlockState()), 3);
        }

        return out;
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag tag, @Nonnull net.minecraft.core.HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        if (!displayItem.isEmpty()) {
            net.minecraft.nbt.Tag saved = displayItem.save(provider);
            if (saved != null) {
                tag.put(TAG_DISPLAY_ITEM, saved);
            }
        } else {
            tag.put(TAG_DISPLAY_ITEM, new CompoundTag());
        }
    }

    @Override
    protected void loadAdditional(@Nonnull CompoundTag tag, @Nonnull net.minecraft.core.HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains(TAG_DISPLAY_ITEM, CompoundTag.TAG_COMPOUND)) {
            displayItem = ItemStack.parse(provider, Objects.requireNonNull(tag.getCompound(TAG_DISPLAY_ITEM))).orElse(ItemStack.EMPTY);
        } else {
            displayItem = ItemStack.EMPTY;
        }
    }

    @Override
    public CompoundTag getUpdateTag(@Nonnull net.minecraft.core.HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, provider);
        return tag;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
