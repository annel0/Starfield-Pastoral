package com.stardew.craft.client;

import net.minecraft.client.resources.language.I18n;

import java.util.Locale;

public final class NpcDisplayNames {
    private static final String KEY_PREFIX = "entity.stardewcraft.npc.";

    private NpcDisplayNames() {
    }

    public static String translated(String npcId) {
        String normalized = normalize(npcId);
        if (normalized.isEmpty()) {
            return "?";
        }
        String key = KEY_PREFIX + normalized;
        return I18n.exists(key) ? I18n.get(key) : fallbackName(normalized);
    }

    public static String sortKey(String npcId) {
        return translated(npcId).toLowerCase(Locale.ROOT);
    }

    private static String normalize(String npcId) {
        return npcId == null ? "" : npcId.trim().toLowerCase(Locale.ROOT);
    }

    private static String fallbackName(String npcId) {
        String[] parts = npcId.split("[_\\s]+");
        StringBuilder result = new StringBuilder();
        for (String part : parts) {
            if (part.isBlank()) {
                continue;
            }
            if (!result.isEmpty()) {
                result.append(' ');
            }
            result.append(part.substring(0, 1).toUpperCase(Locale.ROOT));
            if (part.length() > 1) {
                result.append(part.substring(1));
            }
        }
        return result.isEmpty() ? npcId : result.toString();
    }
}