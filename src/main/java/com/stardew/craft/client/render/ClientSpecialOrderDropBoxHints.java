package com.stardew.craft.client.render;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public final class ClientSpecialOrderDropBoxHints {
    private static final Set<String> ACTIVE_DROP_BOX_IDS = new LinkedHashSet<>();
    private static boolean debugShowAll = Boolean.getBoolean("stardewcraft.debug.specialOrderDropBoxes");

    private ClientSpecialOrderDropBoxHints() {
    }

    public static void replace(Collection<String> ids) {
        ACTIVE_DROP_BOX_IDS.clear();
        for (String id : ids) {
            if (id != null && !id.isBlank()) {
                ACTIVE_DROP_BOX_IDS.add(id);
            }
        }
    }

    public static boolean isActive(String dropBoxId) {
        return debugShowAll || ACTIVE_DROP_BOX_IDS.contains(dropBoxId);
    }

    public static void setDebugShowAll(boolean showAll) {
        debugShowAll = showAll;
    }
}
