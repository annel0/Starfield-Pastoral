package com.stardew.craft.client;

import com.stardew.craft.StardewCraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/**
 * 驱动鱼竿第一人称动画状态机的客户端 tick。
 */
@EventBusSubscriber(modid = StardewCraft.MODID, value = Dist.CLIENT)
public final class FishingRodClientAnimationTicker {
	private FishingRodClientAnimationTicker() {
	}

	@SubscribeEvent
	public static void onClientTick(ClientTickEvent.Post event) {
		FishingRodCastAnimationState.tick();
	}
}
