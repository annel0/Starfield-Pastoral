package com.stardew.craft.client.emote;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;

@SuppressWarnings("null")
public final class EmoteBubbleClientState {

	private static final int EMOTE_POP_IN_TICKS = 4;
	private static final int EMOTE_FRAME_HOLD_TICKS = 5;
	private static final int EMOTE_STABLE_FRAME_COUNT = 4;
	private static final int EMOTE_FADE_TICKS = 4;
	private static final int EMOTE_TOTAL_TICKS = EMOTE_POP_IN_TICKS + EMOTE_STABLE_FRAME_COUNT * EMOTE_FRAME_HOLD_TICKS + EMOTE_FADE_TICKS;

	private static final Map<Integer, EmoteInstance> ACTIVE = new HashMap<>();

	private EmoteBubbleClientState() {
	}

	public static void trigger(int entityId, int baseIndex) {
		if (baseIndex < 0) {
			return;
		}
		ACTIVE.put(entityId, new EmoteInstance(baseIndex, EMOTE_TOTAL_TICKS));
	}

	public static void tick() {
		Minecraft mc = Minecraft.getInstance();
		ClientLevel level = mc.level;
		if (level == null) {
			ACTIVE.clear();
			return;
		}

		Iterator<Map.Entry<Integer, EmoteInstance>> iterator = ACTIVE.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<Integer, EmoteInstance> entry = iterator.next();
			Entity entity = level.getEntity(entry.getKey());
			if (entity == null || !entity.isAlive()) {
				iterator.remove();
				continue;
			}

			EmoteInstance state = entry.getValue();
			state.ticksLeft--;
			if (state.ticksLeft <= 0) {
				iterator.remove();
			}
		}
	}

	public static int getCurrentFrameIndex(int entityId) {
		EmoteInstance state = ACTIVE.get(entityId);
		if (state == null || state.ticksLeft <= 0 || state.baseIndex < 0) {
			return -1;
		}

		int elapsed = EMOTE_TOTAL_TICKS - state.ticksLeft;
		if (elapsed < EMOTE_POP_IN_TICKS) {
			return elapsed;
		}

		int holdWindow = EMOTE_STABLE_FRAME_COUNT * EMOTE_FRAME_HOLD_TICKS;
		if (elapsed < EMOTE_POP_IN_TICKS + holdWindow) {
			int step = (elapsed - EMOTE_POP_IN_TICKS) / EMOTE_FRAME_HOLD_TICKS;
			return state.baseIndex + Math.min(EMOTE_STABLE_FRAME_COUNT - 1, step);
		}

		int fadeElapsed = elapsed - (EMOTE_POP_IN_TICKS + holdWindow);
		return Math.max(0, EMOTE_FADE_TICKS - 1 - fadeElapsed);
	}

	private static final class EmoteInstance {
		private final int baseIndex;
		private int ticksLeft;

		private EmoteInstance(int baseIndex, int ticksLeft) {
			this.baseIndex = baseIndex;
			this.ticksLeft = ticksLeft;
		}
	}
}
