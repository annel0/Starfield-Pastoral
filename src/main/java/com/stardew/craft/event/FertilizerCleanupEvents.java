package com.stardew.craft.event;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.manager.FertilizerManager;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * 肥料清理事件：
 * - 耕地被破坏时立即移除肥料
 * - 定期清理所有不在耕地上的肥料残留
 */
@SuppressWarnings("removal")
@EventBusSubscriber(modid = StardewCraft.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class FertilizerCleanupEvents {
	private FertilizerCleanupEvents() {
	}

	@SubscribeEvent
	public static void onBlockBreak(BlockEvent.BreakEvent event) {
		if (!(event.getLevel() instanceof ServerLevel serverLevel)) {
			return;
		}
		var pos = event.getPos();
		var manager = FertilizerManager.get(serverLevel);
		if (manager.hasFertilizer(serverLevel, pos)) {
			manager.removeFertilizer(serverLevel, pos);
		}
	}

	@SubscribeEvent
	public static void onServerTick(ServerTickEvent.Post event) {
		// 每5秒（100 ticks）清理一次无效肥料，避免每tick都清理
		if (tickCounter++ >= 100) {
			tickCounter = 0;
			var overworld = event.getServer().overworld();
			var manager = FertilizerManager.get(overworld);
			manager.cleanupInvalidEntries(event.getServer());
		}
	}
	
	private static int tickCounter = 0;
}
