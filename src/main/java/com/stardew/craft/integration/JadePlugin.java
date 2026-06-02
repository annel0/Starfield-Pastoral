package com.stardew.craft.integration;

import com.stardew.craft.block.crop.StardewCropBlock;
import com.stardew.craft.block.animal.AnimalProduceSpotBlock;
import com.stardew.craft.block.utility.BeeHouseBlock;
import com.stardew.craft.block.utility.BaitMakerBlock;
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
import com.stardew.craft.integration.jade.StardewCropFertilizerJadeProvider;
import com.stardew.craft.manager.CropGrowthManager;
import com.stardew.craft.block.tree.WildOakBranchBlock;
import com.stardew.craft.block.tree.WildOakLeavesBlock;
import com.stardew.craft.block.tree.WildOakTrunkBlock;
import com.stardew.craft.block.tree.WildTreeSaplingBlock;
import com.stardew.craft.manager.TreeGrowthManager;
import com.stardew.craft.tree.WildTrees;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.Objects;

@WailaPlugin
public class JadePlugin implements IWailaPlugin {

    private static final String NBT_TOTAL_DAYS = "Stardew_TotalDays";
    private static final String NBT_DAYS_GROWN = "Stardew_DaysGrown";
    private static final String NBT_MATURE = "Stardew_Mature";
	private static final String NBT_BLOCKED = "Stardew_Blocked";
    private static final String NBT_TREE_STAGE = "Stardew_TreeStage";
    private static final String NBT_TREE_MAX_STAGE = "Stardew_TreeMaxStage";
    private static final String NBT_TREE_FERTILIZED = "Stardew_TreeFertilized";
    private static final String NBT_TREE_GROWTH_CHANCE = "Stardew_TreeGrowthChance";
    private static final String NBT_TREE_FERTILIZED_CHANCE = "Stardew_TreeFertilizedChance";
    private static final String NBT_TREE_SEED_SHAKE_CHANCE = "Stardew_TreeSeedShakeChance";
    private static final String NBT_TREE_SEED_SPREAD_CHANCE = "Stardew_TreeSeedSpreadChance";
    private static final String NBT_TREE_SEED_CHOP_CHANCE = "Stardew_TreeSeedChopChance";
    private static final String NBT_DECORATIVE = "Stardew_Decorative";

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(new CropComponentProvider(), StardewCropBlock.class);
		registration.registerBlockDataProvider(new TreeSaplingComponentProvider(), WildTreeSaplingBlock.class);
        registration.registerBlockDataProvider(new TreeSaplingComponentProvider(), WildOakTrunkBlock.class);
        registration.registerBlockDataProvider(new TreeSaplingComponentProvider(), WildOakBranchBlock.class);
        registration.registerBlockDataProvider(new TreeSaplingComponentProvider(), WildOakLeavesBlock.class);
        registration.registerBlockDataProvider(AnimalProduceSpotJadeProvider.INSTANCE, AnimalProduceSpotBlock.class);
        registration.registerBlockDataProvider(FarmlandFertilizerJadeProvider.INSTANCE, FarmBlock.class);
        registration.registerBlockDataProvider(CropFertilizerJadeProvider.INSTANCE, CropBlock.class);
        registration.registerBlockDataProvider(StardewCropFertilizerJadeProvider.INSTANCE, StardewCropBlock.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(new CropComponentProvider(), StardewCropBlock.class);
        registration.registerBlockComponent(new TreeSaplingComponentProvider(), WildTreeSaplingBlock.class);
        registration.registerBlockComponent(new TreeSaplingComponentProvider(), WildOakTrunkBlock.class);
        registration.registerBlockComponent(new TreeSaplingComponentProvider(), WildOakBranchBlock.class);
        registration.registerBlockComponent(new TreeSaplingComponentProvider(), WildOakLeavesBlock.class);
        registration.registerBlockComponent(AnimalProduceSpotJadeProvider.INSTANCE, AnimalProduceSpotBlock.class);
        // 注册耕地肥料显示
        registration.registerBlockComponent(FarmlandFertilizerJadeProvider.INSTANCE, FarmBlock.class);
        // 注册作物肥料显示（原版作物 + 本模组作物）
        registration.registerBlockComponent(CropFertilizerJadeProvider.INSTANCE, CropBlock.class);
        registration.registerBlockComponent(StardewCropFertilizerJadeProvider.INSTANCE, StardewCropBlock.class);
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
            || block instanceof BaitMakerBlock
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
            WildTrees.Def def = WildTrees.findByAnyPart(state);
            boolean isSapling = state.getBlock() instanceof WildTreeSaplingBlock;
            if (def == null && !isSapling) {
                return;
            }
            if (isSapling) {
                def = ((WildTreeSaplingBlock) state.getBlock()).getDef();
            }
            if (def == null) {
                return;
            }

            TreeGrowthManager mgr = TreeGrowthManager.get(serverLevel);
            BlockPos pos = Objects.requireNonNull(accessor.getPosition(), "position");
            tag.putFloat(NBT_TREE_GROWTH_CHANCE, def.growthChance());
            tag.putFloat(NBT_TREE_FERTILIZED_CHANCE, def.fertilizedGrowthChance());
            tag.putFloat(NBT_TREE_SEED_SHAKE_CHANCE, def.seedOnShakeChance());
            tag.putFloat(NBT_TREE_SEED_SPREAD_CHANCE, def.seedSpreadChance());
            tag.putFloat(NBT_TREE_SEED_CHOP_CHANCE, def.seedOnChopChance());
            if (isSapling) {
                int grown = mgr.getDaysGrown(serverLevel, pos);
                grown = Math.max(0, Math.min(grown, TOTAL_DAYS));
                tag.putInt(NBT_TOTAL_DAYS, TOTAL_DAYS);
                tag.putInt(NBT_DAYS_GROWN, grown);
                tag.putInt(NBT_TREE_STAGE, mgr.getGrowthStage(serverLevel, pos));
                tag.putInt(NBT_TREE_MAX_STAGE, TreeGrowthManager.matureGrowthStage());
                tag.putBoolean(NBT_TREE_FERTILIZED, mgr.isFertilized(serverLevel, pos));
                tag.putBoolean(NBT_BLOCKED, mgr.isBlockedNow(serverLevel, pos));
            } else {
                tag.putBoolean(NBT_MATURE, true);
            }
        }

        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            BlockState state = accessor.getBlockState();
            if (!(state.getBlock() instanceof WildTreeSaplingBlock) && WildTrees.findByAnyPart(state) == null) {
                return;
            }

            CompoundTag serverData = accessor.getServerData();
            if (serverData == null) {
                return;
            }
            if (serverData.contains(NBT_TREE_STAGE) && serverData.contains(NBT_TREE_MAX_STAGE)) {
                int stage = serverData.getInt(NBT_TREE_STAGE);
                int maxStage = serverData.getInt(NBT_TREE_MAX_STAGE);
                tooltip.add(Component.translatable("stardewcraft.tooltip.tree_stage", stage, maxStage));
                tooltip.add(Component.translatable("stardewcraft.tooltip.tree_growth_chance", formatChance(serverData.getFloat(NBT_TREE_GROWTH_CHANCE))));
                if (serverData.getBoolean(NBT_TREE_FERTILIZED)) {
                    tooltip.add(Component.translatable("stardewcraft.tooltip.tree_fertilized",
                            formatChance(serverData.getFloat(NBT_TREE_FERTILIZED_CHANCE)))
                            .withStyle(net.minecraft.ChatFormatting.GREEN));
                }
                if (serverData.getBoolean(NBT_BLOCKED)) {
                    tooltip.add(Component.translatable("stardewcraft.tooltip.tree_blocked")
                            .withStyle(net.minecraft.ChatFormatting.RED));
                }
            } else if (serverData.getBoolean(NBT_MATURE)) {
                tooltip.add(Component.translatable("stardewcraft.tooltip.tree_mature"));
                tooltip.add(Component.translatable("stardewcraft.tooltip.tree_seed_shake_chance", formatChance(serverData.getFloat(NBT_TREE_SEED_SHAKE_CHANCE))));
                tooltip.add(Component.translatable("stardewcraft.tooltip.tree_seed_spread_chance", formatChance(serverData.getFloat(NBT_TREE_SEED_SPREAD_CHANCE))));
                tooltip.add(Component.translatable("stardewcraft.tooltip.tree_seed_chop_chance", formatChance(serverData.getFloat(NBT_TREE_SEED_CHOP_CHANCE))));
            }
        }

        private static String formatChance(float chance) {
            float percent = chance * 100.0f;
            if (Math.abs(percent - Math.round(percent)) < 0.01f) {
                return Integer.toString(Math.round(percent)) + "%";
            }
            return String.format(java.util.Locale.ROOT, "%.1f%%", percent);
        }

        @Override
        public ResourceLocation getUid() {
            return ResourceLocation.fromNamespaceAndPath("stardewcraft", "tree_sapling_info");
        }
    }

    public static class CropComponentProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

        private static BlockPos resolveCropRootPos(BlockAccessor accessor) {
            BlockPos pos = Objects.requireNonNull(accessor.getPosition(), "position");
            BlockState state = accessor.getBlockState();
            if (!state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)) {
                return pos;
            }
            if (state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) != DoubleBlockHalf.UPPER) {
                return pos;
            }

            BlockPos belowPos = pos.below();
            BlockState belowState = accessor.getLevel().getBlockState(belowPos);
            if (belowState.getBlock() == state.getBlock()
                    && belowState.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)
                    && belowState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER) {
                return belowPos;
            }
            return pos;
        }

        @Override
        public void appendServerData(CompoundTag tag, BlockAccessor accessor) {
            if (!(accessor.getLevel() instanceof ServerLevel serverLevel)) {
                return;
            }

            BlockPos rootPos = resolveCropRootPos(accessor);
            BlockState state = accessor.getLevel().getBlockState(rootPos);
            if (!(state.getBlock() instanceof StardewCropBlock cropBlock)) {
                return;
            }

            int age = state.getValue(Objects.requireNonNull(StardewCropBlock.AGE, "AGE"));
            CropGrowthManager.CropGrowthState gs = CropGrowthManager.get(serverLevel).getState(serverLevel, rootPos);
            if (StardewCropBlock.isPlayerPlacedDecorative(serverLevel, rootPos, state)) {
                tag.putBoolean(NBT_DECORATIVE, true);
                return;
            }
            int dayInPhase = gs != null ? gs.dayInPhase : 0;
            int phase = gs != null ? gs.phase : 0;
            boolean regrowing = gs != null && gs.regrowing;

            boolean mature;
            
            int totalDays = 0;
            int daysGrown = 0;

            int[] phaseDays = cropBlock.getPhaseDaysForDisplay();
            if (phaseDays == null || phaseDays.length == 0) {
                mature = age >= StardewCropBlock.MAX_AGE;
                totalDays = StardewCropBlock.MAX_AGE;
                daysGrown = age;
            } else {
                int effectivePhase = phase;
                if (effectivePhase <= 0 && gs == null) { // Fallback if no crop state is found
                    effectivePhase = age >= StardewCropBlock.MAX_AGE ? StardewCropBlock.MAX_AGE : Math.min(age, StardewCropBlock.MAX_AGE - 1);
                }
                
                int harvestPhase = phaseDays.length;
                int lastReq = phaseDays.length > StardewCropBlock.MAX_AGE ? phaseDays[StardewCropBlock.MAX_AGE] : 1;
                lastReq = Math.max(1, lastReq);
                mature = age >= StardewCropBlock.MAX_AGE
                    && (effectivePhase >= harvestPhase
                    || (effectivePhase >= StardewCropBlock.MAX_AGE && dayInPhase >= lastReq));
                
                if (regrowing) {
                    totalDays = cropBlock.getRegrowDaysForDisplay();
                    daysGrown = totalDays - dayInPhase;
                    mature = dayInPhase <= 0;
                } else {
                    for (int pd : phaseDays) {
                        totalDays += pd;
                    }
                    
                    for (int i = 0; i < effectivePhase && i < phaseDays.length; i++) {
                        daysGrown += phaseDays[i];
                    }
                    
                    if (effectivePhase < phaseDays.length) {
                        daysGrown += Math.min(dayInPhase, phaseDays[effectivePhase]);
                    } else {
                        daysGrown += dayInPhase;
                    }
                }
                
                if (mature) {
                    daysGrown = totalDays; // Cap it properly at visual maturity
                }
            }

            // Jade 显示“已生长的天数 / 总需天数”
            tag.putInt(NBT_TOTAL_DAYS, totalDays);
            tag.putInt(NBT_DAYS_GROWN, daysGrown);
            tag.putBoolean(NBT_MATURE, mature);
        }

        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            BlockPos rootPos = resolveCropRootPos(accessor);
            BlockState state = accessor.getLevel().getBlockState(rootPos);
            if (state.getBlock() instanceof StardewCropBlock cropBlock) {
                int age = state.getValue(Objects.requireNonNull(StardewCropBlock.AGE, "AGE"));

                CompoundTag serverData = accessor.getServerData();
                if (serverData != null && serverData.getBoolean(NBT_DECORATIVE)) {
                    tooltip.add(Component.translatable("stardewcraft.tooltip.decorative_flower")
                            .withStyle(net.minecraft.ChatFormatting.GRAY));
                    return;
                }
                if ((serverData == null || !serverData.contains(NBT_TOTAL_DAYS))
                        && StardewCropBlock.isDecorativeFlowerState(state)) {
                    tooltip.add(Component.translatable("stardewcraft.tooltip.decorative_flower")
                            .withStyle(net.minecraft.ChatFormatting.GRAY));
                    return;
                }
                if (serverData != null && serverData.contains(NBT_TOTAL_DAYS) && serverData.contains(NBT_DAYS_GROWN)) {
                    int grown = serverData.getInt(NBT_DAYS_GROWN);
                    int total = serverData.getInt(NBT_TOTAL_DAYS);
                    String progressText = grown + "/" + total;
                    tooltip.add(Component.translatable("stardewcraft.tooltip.growth_stage", progressText));
                } else {
                    // 回退：使用方块的阶段天数估算
                    int[] phaseDays = cropBlock.getPhaseDaysForDisplay();
                    if (phaseDays != null && phaseDays.length > 0) {
                        int totalDays = 0;
                        for (int pd : phaseDays) totalDays += pd;
                        int daysGrown = 0;
                        for (int i = 0; i < age && i < phaseDays.length; i++) {
                            daysGrown += phaseDays[i];
                        }
                        if (age >= StardewCropBlock.MAX_AGE) daysGrown = totalDays;
                        tooltip.add(Component.translatable("stardewcraft.tooltip.growth_stage", daysGrown + "/" + totalDays));
                    } else {
                        tooltip.add(Component.translatable("stardewcraft.tooltip.growth_stage", age + "/" + StardewCropBlock.MAX_AGE));
                    }
                }
                
                // 检查是否浇水 (检查下方耕地湿润度)
                BlockPos belowPos = rootPos.below();
                BlockState belowState = accessor.getLevel().getBlockState(belowPos);
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
                } else {
                    // 回退：AGE 达到最大值视为成熟
                    mature = age >= StardewCropBlock.MAX_AGE;
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
