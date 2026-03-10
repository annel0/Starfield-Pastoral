package com.stardew.craft.client;

import net.minecraft.client.Minecraft;

/**
 * 客户端第一人称挥舞计时器：让镰刀动画时长可控（0.5s = 10 ticks）。
 *
 * 注意：这是纯客户端视觉，不影响服务端收割判定与冷却。
 */
public final class ScytheSwingAnimationState {
	/** 0.5s at 20 TPS */
	public static final int TOTAL_TICKS = 10;

	private static int ticksRemaining = 0;

	private ScytheSwingAnimationState() {
	}

	public static void start() {
		ticksRemaining = TOTAL_TICKS;
	}

	public static void reset() {
		ticksRemaining = 0;
	}

	public static boolean isActive() {
		return ticksRemaining > 0;
	}

	public static void tick() {
		// 只给本地玩家用；离开世界/切维度时自动清零。
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null || mc.level == null) {
			reset();
			return;
		}
		if (ticksRemaining > 0) {
			ticksRemaining--;
		}
	}

	/**
	 * @param partialTick render partial tick
	 * @return 0..1, where 0 is the first frame, 1 is the last frame.
	 */
	public static float getProgress(float partialTick) {
		if (ticksRemaining <= 0) {
			return 0.0F;
		}
		// remaining: TOTAL..1
		float elapsed = (TOTAL_TICKS - ticksRemaining) + partialTick;
		float progress = elapsed / (float) TOTAL_TICKS;
		if (progress < 0.0F) {
			return 0.0F;
		}
		if (progress > 1.0F) {
			return 1.0F;
		}
		return progress;
	}
}
