package com.stardew.craft.deco;

import java.util.List;

public final class DecorAnchorStyleRegistry {
    private static final List<String> STYLES = List.of(
        "pierre_shop/window_1",
        "pierre_shop/carpet_1",
        "pierre_shop/carpet_2",
        "pierre_shop/2",
        "pierre_shop/3_1",
        "pierre_shop/3_2",
        "pierre_shop/3_3",
        "pierre_shop/3_4",
        "pierre_shop/3_5",
        "pierre_shop/3_6",
        "pierre_shop/3_7",
        "pierre_shop/3_8",
        "pierre_shop/3_9",
        "pierre_shop/3_10",
        "pierre_shop/4_1",
        "pierre_shop/5_1",
        "pierre_shop/5_2",
        "pierre_shop/5_3",
        "pierre_shop/6_1",
        "pierre_shop/6_2",
        "pierre_shop/7_1",
        "pierre_shop/8_1",
        "pierre_shop/8_2",
        "pierre_shop/8_3",
        "pierre_shop/8_4"
    );

    private DecorAnchorStyleRegistry() {
    }

    public static List<String> all() {
        return STYLES;
    }

    public static int size() {
        return STYLES.size();
    }

    public static String defaultStyleId() {
        return STYLES.get(0);
    }

    public static int toIndex(String styleId) {
        int idx = STYLES.indexOf(styleId);
        return idx >= 0 ? idx : 0;
    }

    public static String fromIndex(int index) {
        if (index < 0 || index >= STYLES.size()) {
            return defaultStyleId();
        }
        return STYLES.get(index);
    }
}
