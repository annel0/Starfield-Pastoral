package com.stardew.craft.fishing.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record FishingLocationData(String locationKey, List<SpawnFishRule> fish) {
	public static FishingLocationData empty(String key) {
		return new FishingLocationData(key, List.of());
	}

	public static FishingLocationData defaultFallback() {
		// 返回空列表，不使用原版鱼作为fallback
		// 如果真的没有匹配的鱼，selectFish会返回垃圾物品
		return new FishingLocationData("Default", List.of());
	}

	public static FishingLocationData fromJson(JsonObject root) {
		String location = root.has("location") ? root.get("location").getAsString() : "Default";
		List<SpawnFishRule> fish = new ArrayList<>();
		if (root.has("fish")) {
			JsonArray arr = root.getAsJsonArray("fish");
			for (JsonElement el : arr) {
				if (!el.isJsonObject()) {
					continue;
				}
				JsonObject obj = el.getAsJsonObject();
				// Allow comment markers like {"comment": "..."} in big JSON files.
				if (!obj.has("item")) {
					continue;
				}
				fish.add(SpawnFishRule.fromJson(obj));
			}
		}
		return new FishingLocationData(location, Collections.unmodifiableList(fish));
	}
}
