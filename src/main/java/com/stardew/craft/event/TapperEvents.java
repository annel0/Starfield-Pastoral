package com.stardew.craft.event;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.block.ModBlocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = StardewCraft.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class TapperEvents {
	private TapperEvents() {
	}

	@SuppressWarnings("null")
	@SubscribeEvent
	public static void onTapperBreak(BlockEvent.BreakEvent event) {
		if (event.getPlayer() == null || event.getPlayer().isCreative()) {
			return;
		}
		var level = event.getLevel();
		var pos = event.getPos();
		@SuppressWarnings("null")
		var state = level.getBlockState(pos);
		if (!state.is(ModBlocks.TAPPER.get())) {
			return;
		}
		// Treat tapper like a normal wooden block: do not cancel breaking.
	}
}
