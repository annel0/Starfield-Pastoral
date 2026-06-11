package com.stardew.craft.item.catalog;

import com.stardew.craft.fishing.data.SpawnFishRule;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Comparator;

public final class StardewItemComparator {
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

        return path(left).compareTo(path(right));
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
