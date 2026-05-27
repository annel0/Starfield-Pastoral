package com.stardew.craft.client;

import com.stardew.craft.block.FertilizerType;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 客户端肥料数据缓存
 * 存储从服务端同步的肥料信息
 */
public class ClientFertilizerCache {
    private static final Map<GlobalPos, FertilizerType> cache = new ConcurrentHashMap<>();

    /**
     * 设置某个位置的肥料
     */
    public static void setFertilizer(BlockPos pos, FertilizerType type) {
        ResourceKey<Level> dimension = currentDimension();
        if (dimension != null) {
            setFertilizer(dimension, pos, type);
        }
    }

    public static void setFertilizer(Level level, BlockPos pos, FertilizerType type) {
        setFertilizer(level.dimension(), pos, type);
    }

    public static void setFertilizer(ResourceKey<Level> dimension, BlockPos pos, FertilizerType type) {
        cache.put(GlobalPos.of(dimension, pos.immutable()), type);
    }

    /**
     * 获取某个位置的肥料
     */
    @Nullable
    public static FertilizerType getFertilizer(BlockPos pos) {
        ResourceKey<Level> dimension = currentDimension();
        return dimension == null ? null : getFertilizer(dimension, pos);
    }

    @Nullable
    public static FertilizerType getFertilizer(Level level, BlockPos pos) {
        return getFertilizer(level.dimension(), pos);
    }

    @Nullable
    public static FertilizerType getFertilizer(ResourceKey<Level> dimension, BlockPos pos) {
        return cache.get(GlobalPos.of(dimension, pos.immutable()));
    }

    /**
     * 移除某个位置的肥料
     */
    public static void removeFertilizer(BlockPos pos) {
        ResourceKey<Level> dimension = currentDimension();
        if (dimension != null) {
            removeFertilizer(dimension, pos);
        }
    }

    public static void removeFertilizer(Level level, BlockPos pos) {
        removeFertilizer(level.dimension(), pos);
    }

    public static void removeFertilizer(ResourceKey<Level> dimension, BlockPos pos) {
        cache.remove(GlobalPos.of(dimension, pos.immutable()));
    }

    /**
     * 清空所有缓存（切换世界时调用）
     */
    public static void clear() {
        cache.clear();
    }

    /**
     * 检查是否有肥料
     */
    public static boolean hasFertilizer(BlockPos pos) {
        return getFertilizer(pos) != null;
    }

    /**
     * 获取当前缓存快照，供渲染层安全遍历。
     */
    public static Map<BlockPos, FertilizerType> snapshot() {
        ResourceKey<Level> dimension = currentDimension();
        return dimension == null ? Map.of() : snapshot(dimension);
    }

    public static Map<BlockPos, FertilizerType> snapshot(Level level) {
        return snapshot(level.dimension());
    }

    public static Map<BlockPos, FertilizerType> snapshot(ResourceKey<Level> dimension) {
        Map<BlockPos, FertilizerType> snapshot = new HashMap<>();
        for (Map.Entry<GlobalPos, FertilizerType> entry : cache.entrySet()) {
            if (entry.getKey().dimension().equals(dimension)) {
                snapshot.put(entry.getKey().pos(), entry.getValue());
            }
        }
        return Map.copyOf(snapshot);
    }

    @Nullable
    private static ResourceKey<Level> currentDimension() {
        Level level = Minecraft.getInstance().level;
        return level == null ? null : level.dimension();
    }
}
