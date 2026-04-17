package com.stardew.craft.farm;

import com.stardew.craft.StardewCraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * 核心 SavedData：管理所有玩家的农场实例。
 * 存储在 Overworld 的 DataStorage 中，全局唯一。
 */
@SuppressWarnings("null")
public class FarmInstanceRegistry extends SavedData {

    private static final String DATA_NAME = "stardew_farm_instances";

    /** 玩家UUID → 农场实例 */
    private final Map<UUID, FarmInstance> instances = new HashMap<>();
    /** 槽位序号 → 玩家UUID（用于反查） */
    private final Map<Integer, UUID> slotToOwner = new HashMap<>();
    /** 下一个可分配的槽位序号 */
    private int nextSlotIndex = 0;

    public FarmInstanceRegistry() {}

    // ── 访问方法 ──

    /**
     * 获取全局唯一实例。
     */
    public static FarmInstanceRegistry get() {
        var server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return new FarmInstanceRegistry();
        return server.overworld().getDataStorage().computeIfAbsent(factory(), DATA_NAME);
    }

    /**
     * 获取玩家的农场实例，没有则返回 null。
     */
    @Nullable
    public FarmInstance getFarm(UUID playerUUID) {
        return instances.get(playerUUID);
    }

    /**
     * 玩家是否已有农场。
     */
    public boolean hasFarm(UUID playerUUID) {
        return instances.containsKey(playerUUID);
    }

    /**
     * 获取玩家的农场出生点，没有农场则返回 null。
     */
    @Nullable
    public BlockPos getFarmSpawnPoint(UUID playerUUID) {
        FarmInstance farm = instances.get(playerUUID);
        return farm != null ? farm.getSpawnPoint() : null;
    }

    /**
     * 根据坐标反查归属玩家。O(1) 计算。
     */
    @Nullable
    public UUID getOwnerAt(BlockPos pos) {
        int slotIndex = FarmInstanceAllocator.getSlotIndexAt(pos);
        if (slotIndex < 0) return null;
        return slotToOwner.get(slotIndex);
    }

    /**
     * 根据槽位序号查找归属玩家。
     */
    @Nullable
    public UUID getOwnerBySlot(int slotIndex) {
        return slotToOwner.get(slotIndex);
    }

    /**
     * 获取所有农场实例（只读）。
     */
    public Collection<FarmInstance> getAllFarms() {
        return Collections.unmodifiableCollection(instances.values());
    }

    /**
     * 获取农场总数。
     */
    public int getFarmCount() {
        return instances.size();
    }

    /**
     * 获取下一个槽位序号。
     */
    public int getNextSlotIndex() {
        return nextSlotIndex;
    }

    // ── 管理方法（OP 专用） ──

    /** 已回收的可复用槽位 */
    private final Queue<Integer> recycledSlots = new ArrayDeque<>();

    /**
     * 删除玩家的农场实例。
     * 注意：不会清理方块，需调用者另行处理。
     * @return 被删除的农场实例，如果不存在返回 null
     */
    @Nullable
    public FarmInstance deleteFarm(UUID playerUUID) {
        FarmInstance farm = instances.remove(playerUUID);
        if (farm == null) return null;
        slotToOwner.remove(farm.getSlotIndex());
        recycledSlots.add(farm.getSlotIndex());
        setDirty();
        StardewCraft.LOGGER.info("[FARM_REGISTRY] Deleted farm for {} (slot={})", playerUUID, farm.getSlotIndex());
        return farm;
    }

    /**
     * 将农场从一个玩家转移给另一个玩家。
     * @return true 成功，false 失败（目标已有农场或源无农场）
     */
    public boolean transferFarm(UUID fromUUID, UUID toUUID, String newOwnerName) {
        FarmInstance farm = instances.get(fromUUID);
        if (farm == null) return false;
        if (instances.containsKey(toUUID)) return false;

        instances.remove(fromUUID);
        // 创建新实例保持相同槽位和坐标
        FarmInstance transferred = new FarmInstance(
            toUUID, newOwnerName, farm.getFarmName(),
            farm.getSlotIndex(), farm.getOrigin(), farm.getFarmType()
        );
        if (farm.isInitialized()) transferred.markInitialized();
        transferred.setCreatedTimestamp(farm.getCreatedTimestamp());
        transferred.setLastOnlineDay(farm.getLastOnlineDay());
        transferred.setLastOnlineSeason(farm.getLastOnlineSeason());

        instances.put(toUUID, transferred);
        slotToOwner.put(farm.getSlotIndex(), toUUID);
        setDirty();
        StardewCraft.LOGGER.info("[FARM_REGISTRY] Transferred farm slot={} from {} to {}",
                farm.getSlotIndex(), fromUUID, toUUID);
        return true;
    }

    /**
     * 修改农场名称。
     */
    public boolean renameFarm(UUID playerUUID, String newName) {
        FarmInstance farm = instances.get(playerUUID);
        if (farm == null) return false;
        farm.setFarmName(newName);
        setDirty();
        return true;
    }

    // ── 分配方法 ──

    /**
     * 为玩家创建新农场实例。如果已有农场则返回现有的。
     */
    public FarmInstance createFarm(UUID playerUUID, String playerName, String farmName, FarmType farmType) {
        if (instances.containsKey(playerUUID)) {
            StardewCraft.LOGGER.warn("[FARM_REGISTRY] Player {} already has a farm, returning existing", playerUUID);
            return instances.get(playerUUID);
        }

        int slotIndex = recycledSlots.isEmpty() ? nextSlotIndex++ : recycledSlots.poll();
        BlockPos origin = FarmInstanceAllocator.getFarmOrigin(slotIndex, farmType);
        FarmInstance instance = new FarmInstance(playerUUID, playerName, farmName, slotIndex, origin, farmType);

        instances.put(playerUUID, instance);
        slotToOwner.put(slotIndex, playerUUID);
        setDirty();

        StardewCraft.LOGGER.info("[FARM_REGISTRY] Created farm for {} (slot={}, origin={}, type={})",
                playerName, slotIndex, origin, farmType.getId());
        return instance;
    }

    /**
     * 标记农场为已初始化。
     */
    public void markFarmInitialized(UUID playerUUID) {
        FarmInstance farm = instances.get(playerUUID);
        if (farm != null) {
            farm.markInitialized();
            setDirty();
        }
    }

    // ── NBT 序列化 ──

    @Override
    @Nonnull
    public CompoundTag save(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
        tag.putInt("NextSlotIndex", nextSlotIndex);

        // 保存回收槽位
        int[] recycledArr = recycledSlots.stream().mapToInt(Integer::intValue).toArray();
        tag.putIntArray("RecycledSlots", recycledArr);

        ListTag list = new ListTag();
        for (FarmInstance instance : instances.values()) {
            list.add(instance.save());
        }
        tag.put("Instances", list);
        return tag;
    }

    private static FarmInstanceRegistry load(CompoundTag tag, HolderLookup.Provider provider) {
        FarmInstanceRegistry registry = new FarmInstanceRegistry();
        registry.nextSlotIndex = tag.getInt("NextSlotIndex");

        // 加载回收槽位
        if (tag.contains("RecycledSlots")) {
            int[] recycledArr = tag.getIntArray("RecycledSlots");
            for (int slot : recycledArr) {
                registry.recycledSlots.add(slot);
            }
        }

        ListTag list = tag.getList("Instances", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            FarmInstance instance = FarmInstance.load(list.getCompound(i));
            registry.instances.put(instance.getOwnerUUID(), instance);
            registry.slotToOwner.put(instance.getSlotIndex(), instance.getOwnerUUID());
        }

        StardewCraft.LOGGER.info("[FARM_REGISTRY] Loaded {} farm instances, nextSlot={}",
                registry.instances.size(), registry.nextSlotIndex);
        return registry;
    }

    public static SavedData.Factory<FarmInstanceRegistry> factory() {
        return new SavedData.Factory<>(FarmInstanceRegistry::new, FarmInstanceRegistry::load);
    }
}
