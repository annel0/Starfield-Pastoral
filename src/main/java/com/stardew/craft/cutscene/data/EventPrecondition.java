package com.stardew.craft.cutscene.data;

import com.google.gson.JsonObject;

/**
 * A single precondition for an event trigger.
 * All preconditions in an event must pass for it to fire.
 */
public record EventPrecondition(String type, JsonObject raw) {

    public String getString(String key) {
        return raw.has(key) ? raw.get(key).getAsString() : null;
    }

    public int getInt(String key, int fallback) {
        return raw.has(key) ? raw.get(key).getAsInt() : fallback;
    }

    public static EventPrecondition fromJson(JsonObject obj) {
        return new EventPrecondition(obj.get("type").getAsString(), obj);
    }
}
