package com.stardew.craft.item.catalog;

import com.stardew.craft.fishing.data.SpawnFishRule;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Comparator;
import java.util.Map;

public final class StardewItemComparator {
    private static final Map<String, String> PATH_SORT_OVERRIDES = Map.ofEntries(
            Map.entry("dresser_1", "dresser_1_00_birch_bedside_cabinet"),
            Map.entry("redwood_bedside_cabinet", "dresser_1_01_redwood_bedside_cabinet"),
            Map.entry("walnut_bedside_cabinet", "dresser_1_02_walnut_bedside_cabinet"),
            Map.entry("oak_bedside_cabinet", "dresser_1_03_oak_bedside_cabinet"),
            Map.entry("dresser_2", "dresser_1_10_birch_dresser"),
            Map.entry("redwood_dresser", "dresser_1_11_redwood_dresser"),
            Map.entry("walnut_dresser", "dresser_1_12_walnut_dresser"),
            Map.entry("oak_dresser", "dresser_1_13_oak_dresser"),
            Map.entry("dresser_3", "dresser_1_20_birch_wardrobe"),
            Map.entry("redwood_wardrobe", "dresser_1_21_redwood_wardrobe"),
            Map.entry("walnut_wardrobe", "dresser_1_22_walnut_wardrobe"),
            Map.entry("oak_wardrobe", "dresser_1_23_oak_wardrobe")
    );

    private StardewItemComparator() {
    }

    public static final Comparator<Item> ITEM = StardewItemComparator::compareItems;
    public static final Comparator<ItemStack> STACK = StardewItemComparator::compareStacks;
    public static final Comparator<SpawnFishRule> FISH_RULE = StardewItemComparator::compareFishRules;

    public static int compareItems(Item left, Item right) {
        if (left == right) {
            return 0;
        }
        StardewCatalogTab leftTab = StardewItemCatalog.tabForItem(left);
        StardewCatalogTab rightTab = StardewItemCatalog.tabForItem(right);
        int tabCompare = Integer.compare(leftTab.ordinal(), rightTab.ordinal());
        if (tabCompare != 0) {
            return tabCompare;
        }

        int sectionCompare = Integer.compare(
                StardewItemCatalog.sectionOrder(leftTab, left),
                StardewItemCatalog.sectionOrder(rightTab, right));
        if (sectionCompare != 0) {
            return sectionCompare;
        }

        return sortPath(left).compareTo(sortPath(right));
    }

    public static int compareStacks(ItemStack left, ItemStack right) {
        int itemCompare = compareItems(left.getItem(), right.getItem());
        if (itemCompare != 0) {
            return itemCompare;
        }
        return Integer.compare(variantOrder(left), variantOrder(right));
    }

    public static int compareFishRules(SpawnFishRule left, SpawnFishRule right) {
        Item leftItem = itemFromId(left.itemId());
        Item rightItem = itemFromId(right.itemId());
        int itemCompare = compareItems(leftItem, rightItem);
        if (itemCompare != 0) {
            return itemCompare;
        }
        return left.itemId().compareTo(right.itemId());
    }

    public static String path(Item item) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
        return id == null ? "" : id.getPath();
    }

    private static String sortPath(Item item) {
        String path = path(item);
        return PATH_SORT_OVERRIDES.getOrDefault(path, path);
    }

    private static Item itemFromId(String rawId) {
        try {
            return BuiltInRegistries.ITEM.get(ResourceLocation.parse(rawId));
        } catch (Exception ignored) {
            return net.minecraft.world.item.Items.AIR;
        }
    }

    private static int variantOrder(ItemStack stack) {
        int quality = com.stardew.craft.item.quality.QualityHelper.getQuality(stack);
        Integer flowerColor = StardewItemDisplayStacks.getFlowerColor(stack);
        if (flowerColor != null) {
            return flowerColor * 10 + quality;
        }
        return quality;
    }
}
