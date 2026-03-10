package com.stardew.craft.integration.jade;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.Objects;

public final class RemainingTimeTooltip {
	private static final String MINUTES_SUFFIX_KEY = "stardewcraft.tooltip.remaining.minutes_suffix";

	private RemainingTimeTooltip() {
	}

	@SuppressWarnings("null")
	public static MutableComponent build(String baseKey, int days, int hours, int minutes) {
		MutableComponent base = Component.translatable(Objects.requireNonNull(baseKey, "baseKey"), days, hours);
		if (minutes <= 0) {
			return base;
		}
		int roundedMinutes = ((minutes + 9) / 10) * 10;
		if (roundedMinutes <= 0) {
			return base;
		}
		return base.append(Component.translatable(MINUTES_SUFFIX_KEY, roundedMinutes));
	}
}
