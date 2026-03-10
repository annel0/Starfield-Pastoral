package com.stardew.craft.manager;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.block.crop.StardewCropBlock;
import com.stardew.craft.manager.FertilizerManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 作物生长管理器
 * 记录所有星露谷作物的位置，并在每天换日时统一处理生长
 */
@SuppressWarnings("unused")
public class CropGrowthManager extends SavedData {
    private static final String DATA_NAME = "stardew_crop_manager";
    
    // 存储所有作物的位置 (使用GlobalPos以支持多维度，虽然目前只限星露谷维度)
    private final Set<GlobalPos> cropPositions = new HashSet<>();

    /**
     * 每株作物的生长状态：当前阶段已过天数 + 是否处于“再生长倒计时”状态。
     * 这是实现 Stardew Valley 原版 Crop.newDay() 的关键状态。
     */
    private final Map<GlobalPos, CropGrowthState> cropStates = new ConcurrentHashMap<>();

    // 防止在遍历时被 onRemove/onPlace 修改导致 ConcurrentModificationException
    private boolean isProcessing = false;
    private final Set<GlobalPos> pendingAdds = new HashSet<>();
    private final Set<GlobalPos> pendingRemoves = new HashSet<>();

    public CropGrowthManager() {}

    public static class CropGrowthState {
        public int dayInPhase;
        /**
         * 当前处于哪一个“星露谷 phase”（0-3）。
         * 注意：我们的方块 AGE 只是 0-3 的渲染阶段，其中 AGE=3 需要只在成熟时出现，
         * 因此不能再用 AGE 直接当 phase。
         */
        public int phase;
        public boolean regrowing;

        public CropGrowthState() {
            this(0, 0, false);
        }

        public CropGrowthState(int dayInPhase, int phase, boolean regrowing) {
            this.dayInPhase = dayInPhase;
            this.phase = phase;
            this.regrowing = regrowing;
        }
    }

    public CropGrowthState getOrCreateState(Level level, BlockPos pos) {
        @SuppressWarnings("null")
        GlobalPos globalPos = GlobalPos.of(level.dimension(), pos.immutable());
        return cropStates.computeIfAbsent(globalPos, (k) -> new CropGrowthState());
    }

    /**
     * 获取作物生长状态（不会创建新状态）。
     * 若未记录，则返回 null。
     */
    public CropGrowthState getState(Level level, BlockPos pos) {
        @SuppressWarnings("null")
        GlobalPos globalPos = GlobalPos.of(level.dimension(), pos.immutable());
        return cropStates.get(globalPos);
    }

    public void setRegrowing(Level level, BlockPos pos, boolean regrowing) {
        if (!(level instanceof ServerLevel)) {
            return;
        }
        CropGrowthState state = getOrCreateState(level, pos);
        state.regrowing = regrowing;
        state.dayInPhase = 0;
        // 再生默认算作进入最后 phase 的倒计时
        state.phase = 3;
        setDirty();
    }

    /**
     * 添加作物位置
     */
    public void addCrop(Level level, BlockPos pos) {
        if (level instanceof ServerLevel) {
            @SuppressWarnings("null")
            GlobalPos globalPos = GlobalPos.of(level.dimension(), pos.immutable());
            if (isProcessing) {
                pendingAdds.add(globalPos);
                pendingRemoves.remove(globalPos);
                cropStates.putIfAbsent(globalPos, new CropGrowthState());
                setDirty();
                return;
            }
            if (cropPositions.add(globalPos)) {
                cropStates.putIfAbsent(globalPos, new CropGrowthState());
                setDirty();
            }
        }
    }

    /**
     * 移除作物位置
     */
    public void removeCrop(Level level, BlockPos pos) {
        if (level instanceof ServerLevel) {
            @SuppressWarnings("null")
            GlobalPos globalPos = GlobalPos.of(level.dimension(), pos.immutable());
            if (isProcessing) {
                pendingRemoves.add(globalPos);
                pendingAdds.remove(globalPos);
                cropStates.remove(globalPos);
                setDirty();
                return;
            }
            if (cropPositions.remove(globalPos)) {
                cropStates.remove(globalPos);
                setDirty();
            }
        }
    }

    private void applyPendingChanges() {
        boolean changed = false;
        if (!pendingRemoves.isEmpty()) {
            changed |= cropPositions.removeAll(pendingRemoves);
            for (GlobalPos p : pendingRemoves) {
                cropStates.remove(p);
            }
            pendingRemoves.clear();
        }
        if (!pendingAdds.isEmpty()) {
            changed |= cropPositions.addAll(pendingAdds);
            for (GlobalPos p : pendingAdds) {
                cropStates.putIfAbsent(p, new CropGrowthState());
            }
            pendingAdds.clear();
        }
        if (changed) {
            setDirty();
        }
    }

    /**
     * 每日生长结算
     * 由 TimeManager 在 advanceDay() 时调用
     */
    @SuppressWarnings("null")
    public void growDaily(ServerLevel serverLevel) {
        StardewCraft.LOGGER.info("Starting daily crop growth update for {} crops...", cropPositions.size());
        isProcessing = true;
        try {
            // 使用快照遍历，避免方块替换触发 add/remove 导致 HashSet 迭代器 CME
            java.util.List<GlobalPos> snapshot = new java.util.ArrayList<>(cropPositions);
            for (GlobalPos globalPos : snapshot) {

                // 确保是当前处理的维度
                if (globalPos.dimension() != serverLevel.dimension()) {
                    continue;
                }

                BlockPos pos = globalPos.pos();

                // 检查区块是否加载 (避免加载未加载的区块造成卡顿)
                if (serverLevel.isLoaded(pos)) {
                    @SuppressWarnings("null")
                    BlockState state = serverLevel.getBlockState(pos);
                    Block block = state.getBlock();

                    // 校验：这还是个作物吗？
                    if (block instanceof StardewCropBlock cropBlock) {
                        CropGrowthState growthState = cropStates.computeIfAbsent(globalPos, (k) -> new CropGrowthState());

                        // 检查水分 (来自下方耕地)
                        BlockPos belowPos = pos.below();
                        @SuppressWarnings("null")
                        BlockState belowState = serverLevel.getBlockState(belowPos);
                        boolean isWatered = false;

                        if (belowState.getBlock() instanceof FarmBlock) {
                            @SuppressWarnings("null")
                            int moisture = belowState.getValue(FarmBlock.MOISTURE);
                            isWatered = moisture > 0;
                        }

                        // growCropOneDay 内部会处理季节判断；若替换为 DEAD_CROP，会触发 onRemove。
                        // onRemove 调用 removeCrop 时会被延迟处理，避免遍历时 CME。
                        cropBlock.growCropOneDay(serverLevel, pos, state, isWatered, growthState);
                        // growthState is mutated in-place

                    } else {
                        // 只要发现位置上不是作物了，就清理掉脏数据
                        removeCrop(serverLevel, pos);
                    }
                }
            }
        } finally {
            isProcessing = false;
            applyPendingChanges();
        }

        // 暴力处理所有加载区块的耕地 (干燥化)
        dryAllFarmland(serverLevel);
    }

    /**
     * 立即检查并枯萎所有“已加载区块里”的非当季作物。
     * 用于调试命令改季节后立刻生效，避免等到第二天。
     */
    @SuppressWarnings("null")
    public void killOutOfSeasonLoaded(ServerLevel serverLevel) {
        StardewCraft.LOGGER.info("Checking out-of-season crops for {} crops...", cropPositions.size());

        isProcessing = true;
        try {
            // 使用快照遍历，避免 setBlock 触发 add/remove 导致 HashSet 迭代器 CME
            java.util.List<GlobalPos> snapshot = new java.util.ArrayList<>(cropPositions);
            for (GlobalPos globalPos : snapshot) {

                if (globalPos.dimension() != serverLevel.dimension()) {
                    continue;
                }

                BlockPos pos = globalPos.pos();
                if (!serverLevel.isLoaded(pos)) {
                    continue;
                }

                @SuppressWarnings("null")
                BlockState state = serverLevel.getBlockState(pos);
                Block block = state.getBlock();
                if (block instanceof StardewCropBlock cropBlock) {
                    CropGrowthState growthState = cropStates.computeIfAbsent(globalPos, (k) -> new CropGrowthState());
                    // 传 watered=false，确保不会推进生长，但仍会触发“不在季节 -> 枯萎”替换。
                    cropBlock.growCropOneDay(serverLevel, pos, state, false, growthState);
                    // growthState is mutated in-place
                } else {
                    removeCrop(serverLevel, pos);
                }
            }
        } finally {
            isProcessing = false;
            applyPendingChanges();
        }
    }
    
    @SuppressWarnings("null")
    private void dryAllFarmland(ServerLevel level) {
            FertilizerManager fertilizerManager = FertilizerManager.get(level);
         // 使用 Set 去重
         java.util.Set<net.minecraft.world.level.chunk.LevelChunk> chunksToProcess = new java.util.HashSet<>();
         
         // 1. 获取玩家周围的区块
         int radius = level.getServer().getPlayerList().getViewDistance();
         for (net.minecraft.server.level.ServerPlayer player : level.players()) {
             net.minecraft.world.level.ChunkPos center = player.chunkPosition();
             for (int dx = -radius; dx <= radius; dx++) {
                 for (int dz = -radius; dz <= radius; dz++) {
                     // getChunk with false returns null if not loaded
                     net.minecraft.world.level.chunk.LevelChunk chunk = level.getChunkSource().getChunk(center.x + dx, center.z + dz, false);
                     if (chunk != null) {
                         chunksToProcess.add(chunk);
                     }
                 }
             }
         }
         
         // 2. 获取强制加载的区块 (Forced Chunks)
         for (long chunkLong : level.getForcedChunks()) {
             net.minecraft.world.level.ChunkPos pos = new net.minecraft.world.level.ChunkPos(chunkLong);
             net.minecraft.world.level.chunk.LevelChunk chunk = level.getChunkSource().getChunk(pos.x, pos.z, false);
             if (chunk != null) {
                 chunksToProcess.add(chunk);
             }
         }

         // 3. 遍历并干燥
         for (net.minecraft.world.level.chunk.LevelChunk chunk : chunksToProcess) {
            net.minecraft.world.level.chunk.LevelChunkSection[] sections = chunk.getSections();
            for (int i = 0; i < sections.length; i++) {
                net.minecraft.world.level.chunk.LevelChunkSection section = sections[i];
                if (section.hasOnlyAir()) continue;
                if (!section.getStates().maybeHas((state) -> state.getBlock() instanceof net.minecraft.world.level.block.FarmBlock)) {
                    continue;
                }

                int bottomY = chunk.getSectionYFromSectionIndex(i) << 4;

                for (int x = 0; x < 16; x++) {
                    for (int y = 0; y < 16; y++) {
                        for (int z = 0; z < 16; z++) {
                            BlockState state = section.getBlockState(x, y, z);
                            if (state.getBlock() instanceof net.minecraft.world.level.block.FarmBlock) {
                                @SuppressWarnings("null")
                                int moisture = state.getValue(net.minecraft.world.level.block.FarmBlock.MOISTURE);
                                if (moisture > 0) {
                                    BlockPos realPos = new BlockPos(
                                            chunk.getPos().getMinBlockX() + x,
                                            bottomY + y,
                                            chunk.getPos().getMinBlockZ() + z
                                    );
                                    // 保水土壤：按概率过夜保留水分（对齐 Stardew 的 retaining soil 概念）
                                    float retain = fertilizerManager.getWaterRetention(level, realPos);
                                    if (retain > 0f && level.random.nextFloat() < retain) {
                                        continue;
                                    }

                                    // 默认：设为干燥
                                    level.setBlock(realPos, state.setValue(net.minecraft.world.level.block.FarmBlock.MOISTURE, 0), 2);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings("null")
    @Override
    public CompoundTag save(@SuppressWarnings("null") CompoundTag tag, @SuppressWarnings("null") net.minecraft.core.HolderLookup.Provider provider) {
        ListTag list = new ListTag();
        for (GlobalPos pos : cropPositions) {
            // GlobalPos 没有内置codec直接转tag的方法比较方便，我们手动存一下或者用NbtUtils存BlockPos
            CompoundTag posTag = new CompoundTag();
            posTag.putString("Dimension", pos.dimension().location().toString());
            posTag.put("Pos", NbtUtils.writeBlockPos(pos.pos()));

            CropGrowthState state = cropStates.get(pos);
            if (state != null) {
                posTag.putInt("DayInPhase", state.dayInPhase);
                posTag.putInt("Phase", state.phase);
                posTag.putBoolean("Regrowing", state.regrowing);
            }

            list.add(posTag);
        }
        tag.put("Crops", list);
        return tag;
    }

    public static CropGrowthManager load(CompoundTag tag, net.minecraft.core.HolderLookup.Provider provider) {
        CropGrowthManager manager = new CropGrowthManager();
        if (tag.contains("Crops", Tag.TAG_LIST)) {
            ListTag list = tag.getList("Crops", Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag posTag = list.getCompound(i);
                @SuppressWarnings("null")
                ResourceKey<Level> dim = ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, 
                        net.minecraft.resources.ResourceLocation.parse(posTag.getString("Dimension")));
                BlockPos pos = NbtUtils.readBlockPos(posTag, "Pos").orElse(BlockPos.ZERO);
                @SuppressWarnings("null")
                GlobalPos gp = GlobalPos.of(dim, pos);
                manager.cropPositions.add(gp);

                int dayInPhase = posTag.contains("DayInPhase", Tag.TAG_INT) ? posTag.getInt("DayInPhase") : 0;
                int phase = posTag.contains("Phase", Tag.TAG_INT) ? posTag.getInt("Phase") : 0;
                boolean regrowing = posTag.contains("Regrowing", Tag.TAG_BYTE) && posTag.getBoolean("Regrowing");
                manager.cropStates.put(gp, new CropGrowthState(dayInPhase, phase, regrowing));
            }
        }
        return manager;
    }

    @SuppressWarnings("null")
    public static CropGrowthManager get(ServerLevel level) {
        // 数据保存在主世界(Overworld)的存储中，全局共享
        ServerLevel overworld = level.getServer().overworld();
        return overworld.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(
                        CropGrowthManager::new,
                        CropGrowthManager::load,
                        null
                ),
                DATA_NAME
        );
    }
}
