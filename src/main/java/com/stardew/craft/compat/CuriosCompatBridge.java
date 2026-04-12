package com.stardew.craft.compat;

import net.minecraft.world.item.Item;
import net.neoforged.fml.ModList;

/**
 * Curios 安全加载桥接器。
 * 所有对 Curios API 的引用集中在 {@link CuriosCompat}，
 * 本类只在 ModList 检测通过后才加载 CuriosCompat，
 * 避免 Curios 不存在时 class-loading 报错。
 */
public final class CuriosCompatBridge {

    private static final String CURIOS_MODID = "curios";
    private static Boolean loaded = null;

    private CuriosCompatBridge() {}

    public static boolean isCuriosLoaded() {
        if (loaded == null) {
            loaded = ModList.get().isLoaded(CURIOS_MODID);
        }
        return loaded;
    }

    /**
     * 在 FMLCommonSetupEvent 中调用，将所有戒指/靴子注册为 Curios 兼容物品。
     */
    public static void registerItem(Item item) {
        if (!isCuriosLoaded()) return;
        CuriosCompat.registerCurioItem(item);
    }
}
