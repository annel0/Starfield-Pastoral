package com.stardew.craft.player;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

import java.util.Objects;

/**
 * 伤害/倒地钩子：当星露谷生命值被打到 0 时触发。
 *
 * 需求：生命值清零时不要直接死亡，留一个接口做后续“晕倒/结算”等。
 */
public final class StardewDamageHooks {
	private StardewDamageHooks() {
	}

	@FunctionalInterface
	public interface KnockoutHandler {
		void onKnockout(ServerPlayer player, DamageSource source);
	}

	private static volatile KnockoutHandler knockoutHandler = (player, source) -> {
		// no-op by default
	};

	public static void setKnockoutHandler(KnockoutHandler handler) {
		knockoutHandler = Objects.requireNonNull(handler, "handler");
	}

	public static void onHealthDepleted(ServerPlayer player, DamageSource source) {
		knockoutHandler.onKnockout(player, source);
	}
}
