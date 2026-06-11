package com.stardew.craft.specialorder;

import net.minecraft.network.chat.Component;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SpecialOrderText {
    private static final Pattern TOKEN = Pattern.compile("\\{([A-Za-z0-9_]+)(?::([A-Za-z0-9_]+))?}");

    private SpecialOrderText() {
    }

    public static Component translatable(String key, SpecialOrderInstance order) {
        String resolved = resolveRaw(key, order);
        if (resolved != null && resolved.startsWith("stardewcraft.")) {
            return Component.translatable(resolved);
        }
        return Component.literal(resolved == null ? "" : resolved);
    }

    public static Component body(String key, SpecialOrderInstance order) {
        String template = Component.translatable(key).getString();
        return Component.literal(resolveRaw(template, order));
    }

    public static String resolveRaw(String template, SpecialOrderInstance order) {
        if (template == null || order == null) {
            return template;
        }
        Matcher matcher = TOKEN.matcher(template);
        StringBuffer out = new StringBuffer();
        while (matcher.find()) {
            String element = matcher.group(1);
            String field = matcher.group(2);
            Map<String, String> values = order.randomValues().get(element);
            if (field == null || field.isBlank()) {
                field = "Text";
            }
            String replacement = values == null ? "" : values.getOrDefault(field, values.getOrDefault(element, ""));
            if (replacement.startsWith("stardewcraft.")) {
                replacement = Component.translatable(replacement).getString();
            }
            matcher.appendReplacement(out, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(out);
        return out.toString();
    }
}
