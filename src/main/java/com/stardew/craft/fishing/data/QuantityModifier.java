package com.stardew.craft.fishing.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * SDV {@code QuantityModifier} parity. Used by {@link SpawnFishRule#chanceModifiers()}
 * and applied via {@link #apply(float, List, Mode, RandomSource)}.
 * <p>
 * Mirrors {@code Utility.ApplyQuantityModifiers} (Utility.cs:4335) and
 * {@code QuantityModifier.Apply}. Conditions ({@code GameStateQuery}) are evaluated by
 * the caller — pass {@code null} to skip condition gating.
 */
public final class QuantityModifier {

	public enum Type { Add, Subtract, Multiply, Divide, Set }

	/** SDV {@code QuantityModifierMode}: how multiple modifiers in one list combine. */
	public enum Mode { Stack, Minimum, Maximum }

	public record Entry(Type modification, float amount, List<Float> randomAmount, String condition) {
		public float pickAmount(RandomSource rng) {
			if (randomAmount != null && !randomAmount.isEmpty()) {
				return randomAmount.get(rng.nextInt(randomAmount.size()));
			}
			return amount;
		}
	}

	private QuantityModifier() {}

	public static float singleStep(float value, Type type, float amount) {
		return switch (type) {
			case Add -> value + amount;
			case Subtract -> value - amount;
			case Multiply -> value * amount;
			case Divide -> amount == 0f ? value : value / amount;
			case Set -> amount;
		};
	}

	/**
	 * Apply a list of modifiers. {@code conditionEvaluator} returns {@code true} to keep an entry,
	 * {@code false} to skip it. Pass {@code null} to accept all entries unconditionally.
	 */
	public static float apply(float baseValue, List<Entry> modifiers, Mode mode,
							  RandomSource rng,
							  java.util.function.Predicate<String> conditionEvaluator) {
		if (modifiers == null || modifiers.isEmpty()) return baseValue;
		Float newValue = null;
		for (Entry m : modifiers) {
			if (conditionEvaluator != null && m.condition() != null && !m.condition().isBlank()
					&& !conditionEvaluator.test(m.condition())) {
				continue;
			}
			float amt = m.pickAmount(rng);
			switch (mode) {
				case Minimum -> {
					float applied = singleStep(baseValue, m.modification(), amt);
					if (newValue == null || applied < newValue) newValue = applied;
				}
				case Maximum -> {
					float applied = singleStep(baseValue, m.modification(), amt);
					if (newValue == null || applied > newValue) newValue = applied;
				}
				default -> newValue = singleStep(newValue == null ? baseValue : newValue, m.modification(), amt);
			}
		}
		return newValue == null ? baseValue : newValue;
	}

	// ─── JSON parsing ────────────────────────────────────────────────────

	public static List<Entry> parseList(JsonObject parent, String key) {
		if (parent == null || !parent.has(key) || !parent.get(key).isJsonArray()) {
			return Collections.emptyList();
		}
		JsonArray arr = parent.getAsJsonArray(key);
		List<Entry> out = new ArrayList<>(arr.size());
		for (JsonElement el : arr) {
			if (!el.isJsonObject()) continue;
			JsonObject o = el.getAsJsonObject();
			Type type = parseType(o.has("modification") ? o.get("modification").getAsString() : "Add");
			float amount = o.has("amount") ? o.get("amount").getAsFloat() : 0f;
			List<Float> randomAmount = null;
			if (o.has("randomAmount") && o.get("randomAmount").isJsonArray()) {
				JsonArray ra = o.getAsJsonArray("randomAmount");
				randomAmount = new ArrayList<>(ra.size());
				for (JsonElement e2 : ra) randomAmount.add(e2.getAsFloat());
			}
			String condition = o.has("condition") && !o.get("condition").isJsonNull()
					? o.get("condition").getAsString() : null;
			out.add(new Entry(type, amount, randomAmount, condition));
		}
		return out;
	}

	public static Mode parseMode(JsonObject parent, String key) {
		if (parent == null || !parent.has(key) || parent.get(key).isJsonNull()) return Mode.Stack;
		String s = parent.get(key).getAsString();
		try { return Mode.valueOf(s); } catch (Exception e) { return Mode.Stack; }
	}

	private static Type parseType(String s) {
		if (s == null) return Type.Add;
		try { return Type.valueOf(s); } catch (Exception e) { return Type.Add; }
	}
}
