package com.stardew.craft.client;

import com.stardew.craft.block.FertilizerType;
import net.minecraft.core.BlockPos;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 客户端肥料数据缓存
 * 存储从服务端同步的肥料信息
 */
public class ClientFertilizerCache {
    private static final Map<BlockPos, FertilizerType> cache = new ConcurrentHashMap<>();

    /**
     * 设置某个位置的肥料
     */
    public static void setFertilizer(BlockPos pos, FertilizerType type) {
        cache.put(pos.immutable(), type);
    }

    /**
     * 获取某个位置的肥料
     */
    @Nullable
    public static FertilizerType getFertilizer(BlockPos pos) {
        return cache.get(pos);
    }

    /**
     * 移除某个位置的肥料
     */
    public static void removeFertilizer(BlockPos pos) {
        cache.remove(pos);
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
        return cache.containsKey(pos);
    }

    /**
     * 获取当前缓存快照，供渲染层安全遍历。
     */
    public static Map<BlockPos, FertilizerType> snapshot() {
        return Map.copyOf(cache);
    }
}
