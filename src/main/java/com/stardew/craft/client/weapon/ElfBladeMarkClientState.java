package com.stardew.craft.client.weapon;

import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ElfBladeMarkClientState {

    public static final class MarkInfo {
        public final long endTick;
        public final int stacks;

        private MarkInfo(long endTick, int stacks) {
            this.endTick = endTick;
            this.stacks = stacks;
        }
    }

    private static final Map<Integer, MarkInfo> MARKS = new ConcurrentHashMap<>();

    private ElfBladeMarkClientState() {}

    public static void apply(int entityId, int durationTicks, int stacks) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }
        long nowTick = mc.level.getGameTime();
        MARKS.put(entityId, new MarkInfo(nowTick + durationTicks, Math.max(0, stacks)));
    }

    public static MarkInfo getMarkInfo(int entityId, long nowTick) {
        MarkInfo info = MARKS.get(entityId);
        if (info == null) {
            return null;
        }
        if (nowTick >= info.endTick) {
            MARKS.remove(entityId);
            return null;
        }
        return info;
    }

    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            MARKS.clear();
            return;
        }
        long nowTick = mc.level.getGameTime();
        Iterator<Map.Entry<Integer, MarkInfo>> it = MARKS.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, MarkInfo> entry = it.next();
            if (nowTick >= entry.getValue().endTick) {
                it.remove();
            }
        }
    }
}
