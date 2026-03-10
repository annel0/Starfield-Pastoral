package com.stardew.craft.manager;

import com.stardew.craft.block.FertilizerType;
import com.stardew.craft.network.FertilizerSyncPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 肥料管理器
 * 记录耕地上施加的肥料类型，允许农作物与肥料共存
 */
public class FertilizerManager extends SavedData {
    private static final String DATA_NAME = "stardew_fertilizer_manager";

    // 存储每个位置的肥料类型
    private final Map<GlobalPos, FertilizerType> fertilizerMap = new ConcurrentHashMap<>();

    public FertilizerManager() {}

    /**
     * 获取服务器级别的肥料管理器实例
     */
    public static FertilizerManager get(ServerLevel level) {
        return level.getServer()
                .overworld()
                .getDataStorage()
                .computeIfAbsent(
                        new SavedData.Factory<>(
                                FertilizerManager::new,
                                FertilizerManager::load
                        ),
                        DATA_NAME
                );
    }

    /**
     * 设置某个位置的肥料
     */
    @SuppressWarnings("null")
    public void setFertilizer(ServerLevel level, BlockPos pos, FertilizerType type) {
        // 只允许对耕地施肥；如果不再是耕地，立刻拒绝并确保清理
        if (!(level.getBlockState(pos).getBlock() instanceof FarmBlock)) {
            if (hasFertilizer(level, pos)) {
                removeFertilizer(level, pos);
            }
            return;
        }
        ResourceKey<Level> dimKey = level.dimension();
        @SuppressWarnings("null")
        GlobalPos globalPos = GlobalPos.of(dimKey, pos);
        fertilizerMap.put(globalPos, type);
        setDirty();
        
        // 同步到附近的客户端
        FertilizerSyncPacket packet = new FertilizerSyncPacket(pos, type.getSerializedName());
        for (ServerPlayer player : level.players()) {
            if (player.blockPosition().distSqr(pos) < 64 * 64) { // 64格内的玩家
                PacketDistributor.sendToPlayer(player, packet);
            }
        }
    }

    /**
     * 获取某个位置的肥料类型
     */
    @SuppressWarnings("null")
    @Nullable
    public FertilizerType getFertilizer(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return null;
        }
        ResourceKey<Level> dimKey = serverLevel.dimension();
        @SuppressWarnings("null")
        GlobalPos globalPos = GlobalPos.of(dimKey, pos);

        FertilizerType type = fertilizerMap.get(globalPos);
        if (type == null) {
            return null;
        }

        // 如果该位置已经不再是耕地：立即清理数据 + 同步客户端，并视为“没有肥料”
        if (!(serverLevel.getBlockState(pos).getBlock() instanceof FarmBlock)) {
            removeFertilizer(serverLevel, pos);
            return null;
        }

        return type;
    }

    /**
     * 移除某个位置的肥料记录
     */
    @SuppressWarnings("null")
    public void removeFertilizer(ServerLevel level, BlockPos pos) {
        ResourceKey<Level> dimKey = level.dimension();
        @SuppressWarnings("null")
        GlobalPos globalPos = GlobalPos.of(dimKey, pos);
        
        // 先检查是否真的存在肥料
        if (!fertilizerMap.containsKey(globalPos)) {
            return;
        }
        
        fertilizerMap.remove(globalPos);
        setDirty();
        
        // 同步到附近的客户端
        FertilizerSyncPacket packet = new FertilizerSyncPacket(pos, null);
        for (ServerPlayer player : level.players()) {
            if (player.blockPosition().distSqr(pos) < 128 * 128) { // 增大同步范围到128
                PacketDistributor.sendToPlayer(player, packet);
            }
        }
        
        com.stardew.craft.StardewCraft.LOGGER.debug("Removed fertilizer at {} in dimension {}", pos, dimKey.location());
    }

    /**
     * 检查某个位置是否有肥料
     */
    public boolean hasFertilizer(Level level, BlockPos pos) {
        return getFertilizer(level, pos) != null;
    }
    
    /**
     * 同步玩家附近所有肥料（玩家登录/维度切换时调用）
     */
    @SuppressWarnings("null")
    public void syncAllFertilizersToPlayer(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        ResourceKey<Level> dimKey = level.dimension();
        BlockPos playerPos = player.blockPosition();
        int syncRadius = 256; // 增加同步范围到256格
        
        for (Map.Entry<GlobalPos, FertilizerType> entry : fertilizerMap.entrySet()) {
            GlobalPos gpos = entry.getKey();
            // 只同步相同维度的肥料
            if (!gpos.dimension().equals(dimKey)) {
                continue;
            }
            
            // 只同步范围内的肥料
            if (playerPos.distSqr(gpos.pos()) > syncRadius * syncRadius) {
                continue;
            }
            
            // 直接同步，不检查区块是否加载（玩家登录时区块可能还没加载）
            FertilizerSyncPacket packet = new FertilizerSyncPacket(gpos.pos(), entry.getValue().getSerializedName());
            PacketDistributor.sendToPlayer(player, packet);
        }
    }

    /**
     * 清理无效肥料：当对应方块不再是耕地时，移除肥料记录并同步客户端。
     * 这是为了解决耕地被破坏/踩坏后，肥料残留导致无法再次施肥的问题。
     */
    @SuppressWarnings("null")
    public void cleanupInvalidEntries(MinecraftServer server) {
        List<GlobalPos> toRemove = new ArrayList<>();
        for (Map.Entry<GlobalPos, FertilizerType> entry : fertilizerMap.entrySet()) {
            GlobalPos gpos = entry.getKey();
            @SuppressWarnings("null")
            ServerLevel level = server.getLevel(gpos.dimension());
            if (level == null) {
                continue;
            }

            // 不要因为清理逻辑去强制加载区块
            if (!level.isLoaded(gpos.pos())) {
                continue;
            }

            if (!(level.getBlockState(gpos.pos()).getBlock() instanceof FarmBlock)) {
                toRemove.add(gpos);
            }
        }
        for (GlobalPos gpos : toRemove) {
            @SuppressWarnings("null")
            ServerLevel level = server.getLevel(gpos.dimension());
            if (level != null) {
                removeFertilizer(level, gpos.pos());
            }
        }
    }

    /**
     * 获取肥料的生长速度加成
     */
    public float getSpeedBoost(Level level, BlockPos pos) {
        FertilizerType type = getFertilizer(level, pos);
        return type != null ? type.getSpeedBoost() : 0f;
    }

    /**
     * 获取肥料的保湿概率
     */
    public float getWaterRetention(Level level, BlockPos pos) {
        FertilizerType type = getFertilizer(level, pos);
        return type != null ? type.getWaterRetention() : 0f;
    }

    /**
     * 获取肥料的品质提升等级
     */
    public int getQualityLevel(Level level, BlockPos pos) {
        FertilizerType type = getFertilizer(level, pos);
        return type != null ? type.getQualityLevel() : 0;
    }

    @SuppressWarnings("null")
    @Override
    public CompoundTag save(@SuppressWarnings("null") CompoundTag tag, @SuppressWarnings("null") net.minecraft.core.HolderLookup.Provider provider) {
        ListTag listTag = new ListTag();
        
        for (Map.Entry<GlobalPos, FertilizerType> entry : fertilizerMap.entrySet()) {
            CompoundTag entryTag = new CompoundTag();
            
            GlobalPos gPos = entry.getKey();
            entryTag.putString("dimension", gPos.dimension().location().toString());
            entryTag.put("pos", NbtUtils.writeBlockPos(gPos.pos()));
            entryTag.putString("type", entry.getValue().getSerializedName());
            
            listTag.add(entryTag);
        }
        
        tag.put("fertilizers", listTag);
        return tag;
    }

    public static FertilizerManager load(CompoundTag tag, net.minecraft.core.HolderLookup.Provider provider) {
        FertilizerManager manager = new FertilizerManager();
        
        if (tag.contains("fertilizers", Tag.TAG_LIST)) {
            ListTag listTag = tag.getList("fertilizers", Tag.TAG_COMPOUND);
            
            for (int i = 0; i < listTag.size(); i++) {
                CompoundTag entryTag = listTag.getCompound(i);
                
                try {
                    @SuppressWarnings("null")
                    ResourceLocation dimLoc = ResourceLocation.parse(entryTag.getString("dimension"));
                    @SuppressWarnings("null")
                    ResourceKey<Level> dimKey = ResourceKey.create(
                            net.minecraft.core.registries.Registries.DIMENSION,
                            dimLoc
                    );
                    BlockPos pos = NbtUtils.readBlockPos(entryTag, "pos").orElseThrow();
                    String typeName = entryTag.getString("type");
                    
                    FertilizerType type = FertilizerType.valueOf(
                            typeName.toUpperCase()
                    );
                    
                    @SuppressWarnings("null")
                    GlobalPos globalPos = GlobalPos.of(dimKey, pos);
                    manager.fertilizerMap.put(globalPos, type);
                } catch (Exception e) {
                    // 跳过损坏的数据
                }
            }
        }
        
        return manager;
    }
}
