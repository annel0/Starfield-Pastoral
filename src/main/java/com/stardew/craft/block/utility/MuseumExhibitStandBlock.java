package com.stardew.craft.block.utility;

import com.stardew.craft.block.ModBlocks;
import com.stardew.craft.blockentity.MuseumExhibitStandBlockEntity;
import com.stardew.craft.item.IStardewItem;
import com.stardew.craft.museum.MuseumDonationData;
import com.stardew.craft.network.MuseumDonationSyncPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.List;

public class MuseumExhibitStandBlock extends MapUtilityStaticBlock implements EntityBlock {

    @SuppressWarnings("null")
    public MuseumExhibitStandBlock(Properties properties) {
        super(properties, "stardewcraft:block/utility/museum_exhibit_stand");
    }

    @SuppressWarnings("null")
    @Override
    public RenderShape getRenderShape(@SuppressWarnings("null") BlockState state) {
        return RenderShape.MODEL;
    }

    @SuppressWarnings("null")
    @Override
    protected List<ItemStack> getDrops(@SuppressWarnings("null") BlockState state, @SuppressWarnings("null") LootParams.Builder params) {
        if (state.getValue(PART) == Part.EXTENSION) {
            return List.of();
        }
        return List.of(new ItemStack(ModBlocks.MUSEUM_EXHIBIT_STAND.get()));
    }

    @SuppressWarnings("null")
    @Override
    public void onRemove(@SuppressWarnings("null") BlockState state, @SuppressWarnings("null") Level level, @SuppressWarnings("null") BlockPos pos, @SuppressWarnings("null") BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock()) && !isMoving && state.getValue(PART) == Part.MAIN) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof MuseumExhibitStandBlockEntity stand && stand.hasDisplayItem()) {
                popResource(level, pos, stand.removeDisplayItem());
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @SuppressWarnings("null")
    @Override
    @Nullable
    public BlockEntity newBlockEntity(@SuppressWarnings("null") BlockPos pos, @SuppressWarnings("null") BlockState state) {
        if (state.getValue(PART) == Part.EXTENSION) {
            return null;
        }
        return new MuseumExhibitStandBlockEntity(pos, state);
    }

    @SuppressWarnings("null")
    @Override
    protected InteractionResult useWithoutItem(@SuppressWarnings("null") BlockState state, @SuppressWarnings("null") Level level, @SuppressWarnings("null") BlockPos pos, @SuppressWarnings("null") Player player, @SuppressWarnings("null") BlockHitResult hit) {
        if (state.getValue(PART) == Part.EXTENSION) {
            BlockPos mainPos = getMainPos(level, pos, state);
            if (mainPos == null) {
                return InteractionResult.PASS;
            }
            return useWithoutItem(level.getBlockState(mainPos), level, mainPos, player, hit);
        }

        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof MuseumExhibitStandBlockEntity stand)) {
            return InteractionResult.PASS;
        }

        MuseumDonationData data = MuseumDonationData.get(serverLevel);
        if (!data.isDonationModeActive()) {
            return InteractionResult.PASS;
        }

        if (player.isShiftKeyDown() && stand.hasDisplayItem()) {
            ItemStack removed = stand.removeDisplayItem();
            if (!removed.isEmpty()) {
                if (!player.addItem(removed)) {
                    player.drop(removed, false);
                }
                syncDonation(serverLevel, data);
                level.playSound(null, pos, net.minecraft.sounds.SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.7f, 1.0f);
                return InteractionResult.CONSUME;
            }
        }

        return InteractionResult.PASS;
    }

    @SuppressWarnings("null")
    @Override
    protected net.minecraft.world.ItemInteractionResult useItemOn(@SuppressWarnings("null") ItemStack stack, @SuppressWarnings("null") BlockState state, @SuppressWarnings("null") Level level, @SuppressWarnings("null") BlockPos pos, @SuppressWarnings("null") Player player, @SuppressWarnings("null") InteractionHand hand, @SuppressWarnings("null") BlockHitResult hit) {
        if (state.getValue(PART) == Part.EXTENSION) {
            BlockPos mainPos = getMainPos(level, pos, state);
            if (mainPos == null) {
                return net.minecraft.world.ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
            return useItemOn(stack, level.getBlockState(mainPos), level, mainPos, player, hand, hit);
        }

        if (!(level instanceof ServerLevel serverLevel)) {
            return net.minecraft.world.ItemInteractionResult.sidedSuccess(true);
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof MuseumExhibitStandBlockEntity stand)) {
            return net.minecraft.world.ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        MuseumDonationData data = MuseumDonationData.get(serverLevel);
        if (!data.isDonationModeActive()) {
            return net.minecraft.world.ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (player.isShiftKeyDown()) {
            if (stand.hasDisplayItem()) {
                ItemStack removed = stand.removeDisplayItem();
                if (!removed.isEmpty()) {
                    if (!player.addItem(removed)) {
                        player.drop(removed, false);
                    }
                    syncDonation(serverLevel, data);
                    level.playSound(null, pos, net.minecraft.sounds.SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.7f, 1.0f);
                    return net.minecraft.world.ItemInteractionResult.sidedSuccess(false);
                }
            }
            return net.minecraft.world.ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (stack.isEmpty() || stand.hasDisplayItem()) {
            return net.minecraft.world.ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (!(stack.getItem() instanceof IStardewItem stardewItem)) {
            return net.minecraft.world.ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        String typeKey = stardewItem.getItemTypeKey();
        if (!"stardewcraft.type.mineral".equals(typeKey) && !"stardewcraft.type.artifact".equals(typeKey)) {
            return net.minecraft.world.ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        if (!data.canDonateItem(itemId)) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.translatable("stardewcraft.command.museum.donate.already", stack.getHoverName()));
            return net.minecraft.world.ItemInteractionResult.sidedSuccess(false);
        }

        ItemStack toDisplay = stack.copy();
        toDisplay.setCount(1);
        stand.setDisplayItem(toDisplay);
        data.markSessionPendingItem(itemId);

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        syncDonation(serverLevel, data);
        level.playSound(null, pos, net.minecraft.sounds.SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.8f, 1.0f);
        return net.minecraft.world.ItemInteractionResult.sidedSuccess(false);
    }

    @Nullable
    private BlockPos getMainPos(BlockGetter level, BlockPos pos, BlockState state) {
        return findMainPos(level, pos, state);
    }

    private static void syncDonation(ServerLevel serverLevel, MuseumDonationData data) {
        PacketDistributor.sendToAllPlayers(new MuseumDonationSyncPacket(List.copyOf(data.getDonatedItems())));
    }
}
