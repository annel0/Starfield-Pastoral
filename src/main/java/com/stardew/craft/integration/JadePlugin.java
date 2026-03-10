package com.stardew.craft.integration;

import com.stardew.craft.block.crop.StardewCropBlock;
import com.stardew.craft.block.animal.AnimalProduceSpotBlock;
import com.stardew.craft.block.utility.BeeHouseBlock;
import com.stardew.craft.block.utility.CaskBlock;
import com.stardew.craft.block.utility.CharcoalKilnBlock;
import com.stardew.craft.block.utility.CheesePressBlock;
import com.stardew.craft.block.utility.CrystalariumBlock;
import com.stardew.craft.block.utility.DehydratorBlock;
import com.stardew.craft.block.utility.DeluxeWormBinBlock;
import com.stardew.craft.block.utility.FishSmokerBlock;
import com.stardew.craft.block.utility.FurnaceBlock;
import com.stardew.craft.block.utility.KegBlock;
import com.stardew.craft.block.utility.LightningRodBlock;
import com.stardew.craft.block.utility.LoomBlock;
import com.stardew.craft.block.utility.MayonnaiseMachineBlock;
import com.stardew.craft.block.utility.OilMakerBlock;
import com.stardew.craft.block.utility.PreservesJarBlock;
import com.stardew.craft.block.utility.SeedMakerBlock;
import com.stardew.craft.block.utility.SolarPanelBlock;
import com.stardew.craft.block.utility.TapperBlock;
import com.stardew.craft.block.utility.WormBinBlock;
import com.stardew.craft.integration.jade.CropFertilizerJadeProvider;
import com.stardew.craft.integration.jade.FarmlandFertilizerJadeProvider;
import com.stardew.craft.integration.jade.AnimalProduceSpotJadeProvider;
import com.stardew.craft.manager.CropGrowthManager;
import com.stardew.craft.block.tree.WildTreeSaplingBlock;
import com.stardew.craft.manager.TreeGrowthManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.JadeIds;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.IServerDataProvider;


import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.Objects;

@WailaPlugin
public class JadePlugin implements IWailaPlugin {

    private static final String NBT_TOTAL_DAYS = "Stardew_TotalDays";
    private static final String NBT_DAYS_GROWN = "Stardew_DaysGrown";
    private static final String NBT_REGROWING = "Stardew_Regrowing";
    private static final String NBT_REGROW_DAYS = "Stardew_RegrowDays";
    private static final String NBT_MATURE = "Stardew_Mature";
	private static final String NBT_BLOCKED = "Stardew_Blocked";

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(new CropComponentProvider(), StardewCropBlock.class);
		registration.registerBlockDataProvider(new TreeSaplingComponentProvider(), WildTreeSaplingBlock.class);
        registration.registerBlockDataProvider(AnimalProduceSpotJadeProvider.INSTANCE, AnimalProduceSpotBlock.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(new CropComponentProvider(), StardewCropBlock.class);
        registration.registerBlockComponent(new TreeSaplingComponentProvider(), WildTreeSaplingBlock.class);
        registration.registerBlockComponent(AnimalProduceSpotJadeProvider.INSTANCE, AnimalProduceSpotBlock.class);
        // 注册耕地肥料显示
        registration.registerBlockComponent(FarmlandFertilizerJadeProvider.INSTANCE, FarmBlock.class);
        // 注册作物肥料显示（原版作物 + 本模组作物）
        registration.registerBlockComponent(CropFertilizerJadeProvider.INSTANCE, CropBlock.class);
        registration.registerBlockComponent(CropFertilizerJadeProvider.INSTANCE, StardewCropBlock.class);
        registration.addTooltipCollectedCallback((box, accessor) -> {
            if (accessor instanceof BlockAccessor blockAccessor) {
                if (shouldHideItemStorage(blockAccessor.getBlockState().getBlock())) {
                    box.getTooltip().remove(JadeIds.UNIVERSAL_ITEM_STORAGE);
                }
            }
        });
    }

    private static boolean shouldHideItemStorage(Block block) {
        return block instanceof BeeHouseBlock
            || block instanceof CaskBlock
            || block instanceof CharcoalKilnBlock
            || block instanceof CheesePressBlock
            || block instanceof CrystalariumBlock
            || block instanceof DehydratorBlock
            || block instanceof DeluxeWormBinBlock
            || block instanceof FishSmokerBlock
            || block instanceof FurnaceBlock
            || block instanceof KegBlock
            || block instanceof LightningRodBlock
            || block instanceof LoomBlock
            || block instanceof MayonnaiseMachineBlock
            || block instanceof OilMakerBlock
            || block instanceof PreservesJarBlock
            || block instanceof SeedMakerBlock
            || block instanceof SolarPanelBlock
            || block instanceof TapperBlock
            || block instanceof WormBinBlock;
    }

    public static class TreeSaplingComponentProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
        private static final int TOTAL_DAYS = 28;

        @Override
        public void appendServerData(CompoundTag tag, BlockAccessor accessor) {
            if (!(accessor.getLevel() instanceof ServerLevel serverLevel)) {
                return;
            }
            BlockState state = accessor.getBlockState();
            if (!(state.getBlock() instanceof WildTreeSaplingBlock)) {
                return;
            }

            TreeGrowthManager mgr = TreeGrowthManager.get(serverLevel);
            BlockPos pos = Objects.requireNonNull(accessor.getPosition(), "position");
            int grown = mgr.getDaysGrown(serverLevel, pos);
            grown = Math.max(0, Math.min(grown, TOTAL_DAYS));
            tag.putInt(NBT_TOTAL_DAYS, TOTAL_DAYS);
            tag.putInt(NBT_DAYS_GROWN, grown);
            tag.putBoolean(NBT_BLOCKED, mgr.isBlockedNow(serverLevel, pos));
        }

        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            if (!(accessor.getBlockState().getBlock() instanceof WildTreeSaplingBlock)) {
                return;
            }

            CompoundTag serverData = accessor.getServerData();
            if (serverData != null && serverData.contains(NBT_TOTAL_DAYS) && serverData.contains(NBT_DAYS_GROWN)) {
                int grown = serverData.getInt(NBT_DAYS_GROWN);
                int total = serverData.getInt(NBT_TOTAL_DAYS);
                int remaining = Math.max(0, total - grown);
                String progressText = grown + "/" + total;
                tooltip.add(Component.translatable("stardewcraft.tooltip.growth_stage", progressText));
                tooltip.add(Component.translatable("stardewcraft.tooltip.remaining_days", remaining));
                if (serverData.getBoolean(NBT_BLOCKED)) {
                    tooltip.add(Component.translatable("stardewcraft.tooltip.tree_blocked")
                            .withStyle(net.minecraft.ChatFormatting.RED));
                }
            }
        }

        @Override
        public ResourceLocation getUid() {
            return ResourceLocation.fromNamespaceAndPath("stardewcraft", "tree_sapling_info");
        }
    }

    public static class CropComponentProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

        @Override
        public void appendServerData(CompoundTag tag, BlockAccessor accessor) {
            if (!(accessor.getLevel() instanceof ServerLevel serverLevel)) {
                return;
            }

            BlockState state = accessor.getBlockState();
            if (!(state.getBlock() instanceof StardewCropBlock cropBlock)) {
                return;
            }

            int age = state.getValue(Objects.requireNonNull(StardewCropBlock.AGE, "AGE"));
            int[] phaseDays = cropBlock.getPhaseDaysForDisplay();
            if (phaseDays == null || phaseDays.length == 0) {
                return;
            }

            int totalDays = 0;
            for (int d : phaseDays) {
                totalDays += Math.max(0, d);
            }
            if (totalDays <= 0) {
                totalDays = 1;
            }

            CropGrowthManager.CropGrowthState gs = CropGrowthManager.get(serverLevel).getState(serverLevel, accessor.getPosition());
            int dayInPhase = gs != null ? gs.dayInPhase : 0;
            boolean regrowing = gs != null && gs.regrowing;
            int phase = gs != null ? gs.phase : 0;

            boolean mature;
            int displayGrown;
            int displayTotal;
            int regrowDays = cropBlock.getRegrowDaysForDisplay();

            if (regrowing && cropBlock.canRegrowForDisplay()) {
                displayGrown = Math.max(0, dayInPhase);
                displayTotal = Math.max(1, regrowDays);
                mature = false;
            } else {
                int grown = 0;
                // 用真实 phase(0-3) 来计算天数进度；AGE 只是渲染阶段
                int effectivePhase = phase;
                if (effectivePhase <= 0) {
                    effectivePhase = age >= StardewCropBlock.MAX_AGE ? StardewCropBlock.MAX_AGE : Math.min(age, StardewCropBlock.MAX_AGE - 1);
                }

                for (int i = 0; i < effectivePhase && i < phaseDays.length; i++) {
                    grown += Math.max(0, phaseDays[i]);
                }
                grown += Math.max(0, dayInPhase);
                displayGrown = Math.min(grown, totalDays);
                displayTotal = totalDays;

                int lastReq = phaseDays.length > StardewCropBlock.MAX_AGE ? phaseDays[StardewCropBlock.MAX_AGE] : 1;
                lastReq = Math.max(1, lastReq);
                mature = age >= StardewCropBlock.MAX_AGE && effectivePhase >= StardewCropBlock.MAX_AGE && dayInPhase >= lastReq;
            }

            tag.putInt(NBT_TOTAL_DAYS, displayTotal);
            tag.putInt(NBT_DAYS_GROWN, displayGrown);
            tag.putBoolean(NBT_REGROWING, regrowing);
            tag.putInt(NBT_REGROW_DAYS, Math.max(0, regrowDays));
            tag.putBoolean(NBT_MATURE, mature);
        }

        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            if (accessor.getBlockState().getBlock() instanceof StardewCropBlock) {
                BlockState state = accessor.getBlockState();
                int age = state.getValue(Objects.requireNonNull(StardewCropBlock.AGE, "AGE"));
                int maxAge = StardewCropBlock.MAX_AGE;

                CompoundTag serverData = accessor.getServerData();
                if (serverData != null && serverData.contains(NBT_TOTAL_DAYS) && serverData.contains(NBT_DAYS_GROWN)) {
                    int grown = serverData.getInt(NBT_DAYS_GROWN);
                    int total = serverData.getInt(NBT_TOTAL_DAYS);
                    boolean regrowing = serverData.getBoolean(NBT_REGROWING);

                    int remaining = Math.max(0, total - grown);

                    String progressText;
                    if (regrowing) {
                        progressText = "再生 " + grown + "/" + total;
                    } else {
                        progressText = grown + "/" + total;
                    }
                    tooltip.add(Component.translatable("stardewcraft.tooltip.growth_stage", progressText));
                    tooltip.add(Component.translatable("stardewcraft.tooltip.remaining_days", remaining));
                } else {
                    // fallback：没有服务端数据时，至少别误导成“0/3天”，改成“阶段 0/3”
                    String progressText = "阶段 " + age + "/" + maxAge;
                    tooltip.add(Component.translatable("stardewcraft.tooltip.growth_stage", progressText));
                }
                
                // 检查是否浇水 (检查下方耕地湿润度)
                BlockPos belowPos = Objects.requireNonNull(accessor.getPosition(), "belowPos").below();
                BlockState belowState = accessor.getLevel().getBlockState(Objects.requireNonNull(belowPos, "belowPos"));
                boolean isWatered = false;
                if (belowState.getBlock() instanceof FarmBlock) {
                    isWatered = belowState.getValue(Objects.requireNonNull(FarmBlock.MOISTURE, "MOISTURE")) > 0;
                } else {
                     // 兼容其他模组耕地，只要block id包含farmland
                     var block = Objects.requireNonNull(belowState.getBlock(), "block");
                     var key = BuiltInRegistries.BLOCK.getKey(block);
                     String blockId = key == null ? "" : key.toString().toLowerCase();
                     if (blockId.contains("farmland")) {
                         // 尝试获取MOISTURE属性，如果没有则默认不可知或算作wet? 通常模组耕地也会有moisture
                            var moisture = Objects.requireNonNull(FarmBlock.MOISTURE, "MOISTURE");
                            if (belowState.hasProperty(moisture)) {
                                isWatered = belowState.getValue(moisture) > 0;
                         }
                     }
                }
                
                // 只有未成熟时才显示浇水状态
                boolean mature = false;
                if (serverData != null && serverData.contains(NBT_MATURE)) {
                    mature = serverData.getBoolean(NBT_MATURE);
                }

                if (!mature) {
                    if (isWatered) {
                        tooltip.add(Component.translatable("stardewcraft.tooltip.watered.yes").withStyle(net.minecraft.ChatFormatting.AQUA));
                    } else {
                        tooltip.add(Component.translatable("stardewcraft.tooltip.watered.no").withStyle(net.minecraft.ChatFormatting.RED));
                    }
                }

                if (mature) {
                    tooltip.add(Component.translatable("stardewcraft.tooltip.mature"));
                }
            }
        }

        @Override
        public ResourceLocation getUid() {
            return ResourceLocation.fromNamespaceAndPath("stardewcraft", "crop_info");
        }
    }
}
