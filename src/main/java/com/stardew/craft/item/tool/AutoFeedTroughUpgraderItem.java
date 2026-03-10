package com.stardew.craft.item.tool;

import com.stardew.craft.block.ModBlocks;
import com.stardew.craft.block.utility.FeedTroughBlock;
import com.stardew.craft.block.utility.AutoFeedTroughBlock;
import com.stardew.craft.blockentity.AutoFeedTroughBlockEntity;
import com.stardew.craft.blockentity.FeedTroughBlockEntity;
import com.stardew.craft.item.IStardewItem;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

@SuppressWarnings("null")
public class AutoFeedTroughUpgraderItem extends Item implements IStardewItem {
    public AutoFeedTroughUpgraderItem(Properties properties) {
        super(properties);
    }

    @Override
    public String getItemTypeKey() {
        return "stardewcraft.type.tool";
    }

    @Override
    public int getSellPrice(ItemStack stack) {
        return -1;
    }

    @Override
    public InteractionResult useOn(@SuppressWarnings("null") UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        BlockState clickedState = level.getBlockState(clickedPos);

        if (!(clickedState.getBlock() instanceof FeedTroughBlock)) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        List<BlockPos> network = FeedTroughBlock.collectConnectedTroughs(level, clickedPos);
        for (BlockPos pos : network) {
            BlockState oldState = level.getBlockState(pos);
            if (!(oldState.getBlock() instanceof FeedTroughBlock)) {
                continue;
            }

            boolean hadHay = false;
            if (level.getBlockEntity(pos) instanceof FeedTroughBlockEntity oldBe) {
                hadHay = !oldBe.getHayStack().isEmpty();
            }

            BlockState newState = ModBlocks.AUTOFEED_TROUGH.get().defaultBlockState()
                .setValue(AutoFeedTroughBlock.FACING, oldState.getValue(FeedTroughBlock.FACING))
                .setValue(AutoFeedTroughBlock.LEFT_CONNECTED, oldState.getValue(FeedTroughBlock.LEFT_CONNECTED))
                .setValue(AutoFeedTroughBlock.RIGHT_CONNECTED, oldState.getValue(FeedTroughBlock.RIGHT_CONNECTED));

            level.setBlock(pos, newState, 3);

            if (hadHay && level.getBlockEntity(pos) instanceof AutoFeedTroughBlockEntity newBe) {
                newBe.insertOneHay(false);
            }
        }

        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        if (player == null || !player.isCreative()) {
            stack.shrink(1);
        }

        level.playSound(null, clickedPos, SoundEvents.SMITHING_TABLE_USE, SoundSource.BLOCKS, 0.9f, 1.0f);
        return InteractionResult.CONSUME;
    }
}
