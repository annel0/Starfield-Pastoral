package com.stardew.craft.event;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.block.ModBlocks;
import com.stardew.craft.core.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.FarmBlock;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber(modid = StardewCraft.MODID)
public class FarmlandEventHandler {

    /**
     * 防止星露谷维度的耕地被踩坏
     */
    @SubscribeEvent
    public static void onFarmlandTrample(BlockEvent.FarmlandTrampleEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }
        
        // 检查维度是否为星露谷
        if (event.getLevel() instanceof net.minecraft.world.level.Level level 
                && level.dimension() == ModDimensions.STARDEW_VALLEY) {
            event.setCanceled(true);
        }
    }

    /**
     * 星露谷维度破坏耕地 → 掉落黄土而非原版泥土。
     * SDV parity: 锄过的地撸掉后回到原始土壤状态。
     */
    @SubscribeEvent
    public static void onFarmlandBreak(BlockEvent.BreakEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (!(event.getState().getBlock() instanceof FarmBlock)) return;
        if (!(event.getLevel() instanceof net.minecraft.world.level.Level level)) return;
        if (level.dimension() != ModDimensions.STARDEW_VALLEY) return;

        BlockPos pos = event.getPos();
        // 取消原版破坏，手动放置黄土并掉落
        event.setCanceled(true);
        level.setBlock(pos, ModBlocks.YELLOW_DIRT.get().defaultBlockState(),
                net.minecraft.world.level.block.Block.UPDATE_ALL);
        net.minecraft.world.entity.item.ItemEntity drop = new net.minecraft.world.entity.item.ItemEntity(
                level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                new ItemStack(ModBlocks.YELLOW_DIRT.get()));
        level.addFreshEntity(drop);
    }
}
