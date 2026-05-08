package com.stardew.craft.client;

import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class MummyCollapseClientState {

    private static final Map<Integer, Long> COLLAPSED = new ConcurrentHashMap<>();

    private MummyCollapseClientState() {}

    public static void apply(int entityId, int durationTicks) {
        if (durationTicks <= 0) {
            COLLAPSED.remove(entityId);
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }
        long nowTick = mc.level.getGameTime();
        COLLAPSED.put(entityId, nowTick + durationTicks);
    }

    public static boolean isCollapsed(int entityId, long nowTick) {
        Long endTick = COLLAPSED.get(entityId);
        return endTick != null && nowTick < endTick;
    }

    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            COLLAPSED.clear();
            return;
        }

        long nowTick = mc.level.getGameTime();
        Iterator<Map.Entry<Integer, Long>> iterator = COLLAPSED.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Long> entry = iterator.next();
            if (nowTick >= entry.getValue()) {
                iterator.remove();
            }
        }
    }
}