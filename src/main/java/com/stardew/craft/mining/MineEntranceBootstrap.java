package com.stardew.craft.mining;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.core.ModMiningDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.AABB;
import javax.annotation.Nonnull;

/**
 * 矿井大厅（0层）管理器
 * - 按照 Implementation Plan 的坐标系统：大厅在 (0, 64, 0)
 * - 每天刷新一次（延迟加载，玩家进入时检查）
 */
@SuppressWarnings("null")
public final class MineEntranceBootstrap {
	private MineEntranceBootstrap() {}

	// NBT 结构文件路径
	private static final String ENTRANCE_STRUCTURE = "data/stardewcraft/structures/mine/mine_entrance.nbt";

	/**
	 * 确保大厅（0层）已生成
	 */
	public static void ensureGenerated(ServerLevel level) {
		if (level.dimension() != ModMiningDimensions.STARDEW_MINING) {
			return;
		}

		MineEntranceSavedData data = MineEntranceSavedData.get(level);
		
		// 检查是否需要生成/刷新
		if (data.needsRegeneration()) {
			regenerateEntrance(level, data, new BlockPos(0, 64, 0));
		}
	}

	/**
	 * 确保大厅（0层）已生成（基于指定中心点）
	 */
	public static void ensureGenerated(ServerLevel level, BlockPos center) {
		if (level.dimension() != ModMiningDimensions.STARDEW_MINING) {
			return;
		}

		MineEntranceSavedData data = MineEntranceSavedData.get(level);
		if (data.needsRegeneration()) {
			regenerateEntrance(level, data, center);
		}
	}

	/**
	 * 生成/刷新大厅结构
	 */
	private static void regenerateEntrance(ServerLevel level, MineEntranceSavedData data, BlockPos center) {
		// 大厅中心点由调用者提供
		// 假设结构大小为 40x40，起始点应该在 (center - 20, 64, center - 20)
		BlockPos structureOrigin = new BlockPos(center.getX() - 20, center.getY(), center.getZ() - 20);
		
		StardewCraft.LOGGER.info("[MINE] Generating hall at {}", structureOrigin);
		
		// 加载并放置 NBT 结构
		StructureLoader.loadAndPlace(level, ENTRANCE_STRUCTURE, structureOrigin);
		
		data.markGenerated();
		data.setDirty();
		
		// 生成矿井出口交互实体
		spawnMineExitPortal(level);
		
		StardewCraft.LOGGER.info("[MINE] Hall generated successfully");
	}

	/** 矿井出口交互实体位置：(0, 66, -7)，1宽 x 1深 x 2高 */
	private static final BlockPos MINE_EXIT_POS = new BlockPos(0, 66, -7);
	private static final String MINE_EXIT_MARKER = "sdv_portal_marker:mine_exit";
	private static final String MINE_EXIT_TARGET = "sdv_portal_target:mine_exit";

	private static void spawnMineExitPortal(ServerLevel level) {
		// 清理已有出口交互实体
		AABB searchBox = new AABB(MINE_EXIT_POS).inflate(6.0D);
		for (Interaction existing : level.getEntitiesOfClass(Interaction.class, searchBox,
				e -> e.getTags().contains(MINE_EXIT_MARKER))) {
			existing.discard();
		}
		// 生成 1x2 交互区域
		for (int dy = 0; dy < 2; dy++) {
			BlockPos pos = MINE_EXIT_POS.above(dy);
			level.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
			Entity entity = EntityType.INTERACTION.create(level);
			if (!(entity instanceof Interaction interaction)) {
				StardewCraft.LOGGER.warn("[MINE] Failed to create mine exit interaction at {}", pos);
				continue;
			}
			interaction.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.0F, 0.0F);
			interaction.addTag(MINE_EXIT_MARKER);
			interaction.addTag(MINE_EXIT_TARGET);
			level.addFreshEntity(interaction);
		}
		StardewCraft.LOGGER.info("[MINE] Mine exit portal spawned at {}", MINE_EXIT_POS);
	}

	/** 保存矿井生成状态的数据 */
	static final class MineEntranceSavedData extends SavedData {
		private static final String NAME = "stardew_mine_entrance";
		
		private boolean generated = false;
		private int lastGeneratedDay = -1;  // 上次生成的游戏天数

		static MineEntranceSavedData get(ServerLevel level) {
			return level.getDataStorage().computeIfAbsent(
				new SavedData.Factory<>(
					MineEntranceSavedData::new,
					MineEntranceSavedData::load
				),
				NAME
			);
		}

		static MineEntranceSavedData load(net.minecraft.nbt.CompoundTag tag, net.minecraft.core.HolderLookup.Provider provider) {
			MineEntranceSavedData d = new MineEntranceSavedData();
			d.generated = tag.getBoolean("generated");
			d.lastGeneratedDay = tag.getInt("lastGeneratedDay");
			return d;
		}

		@Override
		public @Nonnull net.minecraft.nbt.CompoundTag save(@Nonnull net.minecraft.nbt.CompoundTag tag, @Nonnull net.minecraft.core.HolderLookup.Provider provider) {
			tag.putBoolean("generated", generated);
			tag.putInt("lastGeneratedDay", lastGeneratedDay);
			return tag;
		}

		/**
		 * 检查是否需要重新生成
		 */
		boolean needsRegeneration() {
			if (!generated) {
				return true;  // 从未生成过
			}
			
			// 检查是否是新的一天
			int currentDay = com.stardew.craft.time.StardewTimeManager.get().getCurrentDay();
			return currentDay != lastGeneratedDay;
		}

		/**
		 * 标记为已生成
		 */
		void markGenerated() {
			this.generated = true;
			this.lastGeneratedDay = com.stardew.craft.time.StardewTimeManager.get().getCurrentDay();
		}

		/**
		 * 标记需要重新生成（用于强制刷新）
		 */
		void markForRegeneration() {
			this.generated = false;
		}
	}
}
