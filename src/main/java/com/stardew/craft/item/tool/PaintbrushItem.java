package com.stardew.craft.item.tool;

import com.stardew.craft.block.ModBlocks;
import com.stardew.craft.deco.DecorationService;
import com.stardew.craft.deco.DecorationType;
import com.stardew.craft.item.IStardewItem;
import com.stardew.craft.network.payload.OpenSofaColorScreenPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;

public class PaintbrushItem extends Item implements IStardewItem {
    public PaintbrushItem(Properties properties) {
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
    @SuppressWarnings("null")
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState clicked = level.getBlockState(pos);

        DecorationType type = null;
        if (clicked.getBlock() == ModBlocks.WALLPAPER_BLOCK.get()) {
            type = DecorationType.WALLPAPER;
        } else if (clicked.getBlock() == ModBlocks.FLOORING_BLOCK.get()) {
            type = DecorationType.FLOORING;
        }

        if (type == null) {
            if (clicked.getBlock() == ModBlocks.SOFA.get()) {
                if (level.isClientSide) {
                    return InteractionResult.SUCCESS;
                }
                if (context.getPlayer() instanceof ServerPlayer serverPlayer) {
                    int current = clicked.getValue(com.stardew.craft.block.utility.SofaBlock.COLOR);
                    PacketDistributor.sendToPlayer(serverPlayer, new OpenSofaColorScreenPayload(pos, current));
                    return InteractionResult.CONSUME;
                }
            }
            if (clicked.getBlock() == ModBlocks.CUSHION.get()) {
                if (level.isClientSide) {
                    return InteractionResult.SUCCESS;
                }
                if (context.getPlayer() instanceof ServerPlayer serverPlayer
                    && clicked.hasProperty(com.stardew.craft.block.utility.CushionBlock.COLOR)) {
                    int current = clicked.getValue(com.stardew.craft.block.utility.CushionBlock.COLOR);
                    PacketDistributor.sendToPlayer(serverPlayer, new OpenSofaColorScreenPayload(pos, current));
                    return InteractionResult.CONSUME;
                }
            }
            if (clicked.getBlock() == ModBlocks.OFFICE_STOOL.get()) {
                if (level.isClientSide) {
                    return InteractionResult.SUCCESS;
                }
                if (context.getPlayer() instanceof ServerPlayer serverPlayer
                    && clicked.hasProperty(com.stardew.craft.block.utility.OfficeStoolBlock.COLOR)) {
                    int current = clicked.getValue(com.stardew.craft.block.utility.OfficeStoolBlock.COLOR);
                    PacketDistributor.sendToPlayer(serverPlayer, new OpenSofaColorScreenPayload(pos, current));
                    return InteractionResult.CONSUME;
                }
            }
            if (clicked.getBlock() == ModBlocks.OFFICE_CHAIR_2.get()) {
                if (level.isClientSide) {
                    return InteractionResult.SUCCESS;
                }
                if (context.getPlayer() instanceof ServerPlayer serverPlayer
                    && clicked.hasProperty(com.stardew.craft.block.utility.OfficeChair2Block.COLOR)) {
                    int current = clicked.getValue(com.stardew.craft.block.utility.OfficeChair2Block.COLOR);
                    PacketDistributor.sendToPlayer(serverPlayer, new OpenSofaColorScreenPayload(pos, current));
                    return InteractionResult.CONSUME;
                }
            }
            return InteractionResult.PASS;
        }

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (context.getPlayer() instanceof ServerPlayer serverPlayer) {
            DecorationService.openSelection(serverPlayer, pos, type);
            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }
}
