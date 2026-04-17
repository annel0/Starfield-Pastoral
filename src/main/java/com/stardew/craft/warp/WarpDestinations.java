package com.stardew.craft.warp;

import com.stardew.craft.core.ModDimensions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 传送魔杖目的地注册表。
 * <p>
 * 所有目的地在此集中注册，添加新地点只需在 {@link #init()} 中追加即可。
 * 轮盘 UI 根据此列表动态计算布局，不需要硬编码数量。
 */
public final class WarpDestinations {

    private static final List<WarpDestination> DESTINATIONS = new ArrayList<>();

    private WarpDestinations() {}

    static {
        init();
    }

    private static void init() {
        register(new WarpDestination(
                "farm", "stardewcraft.warp.farm", "stardewcraft.warp.farm.desc",
                0, 150.5, -12.0, 119.5, ModDimensions.STARDEW_VALLEY));

        register(new WarpDestination(
                "town", "stardewcraft.warp.town", "stardewcraft.warp.town.desc",
                100_000, -159.5, -18.0, 54.5, ModDimensions.STARDEW_VALLEY));

        register(new WarpDestination(
                "mountain", "stardewcraft.warp.mountain", "stardewcraft.warp.mountain.desc",
                300_000, -290.5, -14.0, 256.5, ModDimensions.STARDEW_VALLEY));

        register(new WarpDestination(
                "beach", "stardewcraft.warp.beach", "stardewcraft.warp.beach.desc",
                500_000, -189.5, -14.0, -142.5, ModDimensions.STARDEW_VALLEY));
    }

    private static void register(WarpDestination dest) {
        DESTINATIONS.add(dest);
    }

    /** 获取所有注册的目的地（不可变视图） */
    public static List<WarpDestination> getAll() {
        return Collections.unmodifiableList(DESTINATIONS);
    }

    /** 按 ID 查找目的地 */
    public static WarpDestination getById(String id) {
        for (WarpDestination d : DESTINATIONS) {
            if (d.id().equals(id)) return d;
        }
        return null;
    }

    /** 目的地数量 */
    public static int size() {
        return DESTINATIONS.size();
    }
}
