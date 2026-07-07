package com.stardew.craft.wardrobe;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.item.equipment.CombinedRingItem;
import com.stardew.craft.item.equipment.StardewBootsItem;
import com.stardew.craft.item.equipment.StardewRingItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public enum WardrobeCategory {
    HATS("stardewcraft.wardrobe.category.hats"),
    SHIRTS("stardewcraft.wardrobe.category.shirts"),
    PANTS("stardewcraft.wardrobe.category.pants"),
    SHOES("stardewcraft.wardrobe.category.shoes"),
    RINGS("stardewcraft.wardrobe.category.rings");

    private static final TagKey<Item> WARDROBE_ACCEPTED = itemTag(StardewCraft.MODID, "wardrobe_accepted");
    private static final TagKey<Item> CURIOS_RING = itemTag("curios", "ring");
    private static final TagKey<Item> CURIOS_FEET = itemTag("curios", "feet");
    private static final TagKey<Item> CURIOS_HEAD = itemTag("curios", "head");
    private static final TagKey<Item> CURIOS_BODY = itemTag("curios", "body");
    private static final TagKey<Item> CURIOS_BACK = itemTag("curios", "back");
    private static final TagKey<Item> CURIOS_HANDS = itemTag("curios", "hands");
    private static final TagKey<Item> CURIOS_NECKLACE = itemTag("curios", "necklace");
    private static final TagKey<Item> CURIOS_CHARM = itemTag("curios", "charm");
    private static final TagKey<Item> CURIOS_BELT = itemTag("curios", "belt");
    private static final TagKey<Item> CURIOS_BRACELET = itemTag("curios", "bracelet");
    private static final TagKey<Item> CURIOS_CURIO = itemTag("curios", "curio");

    private final String translationKey;

    WardrobeCategory(String translationKey) {
        this.translationKey = translationKey;
    }

    public String translationKey() {
        return translationKey;
    }

    public static boolean isAccepted(ItemStack stack) {
        return categoryFor(stack) != null || stack.is(WARDROBE_ACCEPTED);
    }

    public static WardrobeCategory categoryFor(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        Item item = stack.getItem();
        if (item instanceof StardewRingItem || item instanceof CombinedRingItem || stack.is(CURIOS_RING)) {
            return RINGS;
        }
        if (item instanceof StardewBootsItem || stack.is(CURIOS_FEET)) {
            return SHOES;
        }
        if (stack.is(CURIOS_HEAD)) {
            return HATS;
        }
        if (stack.is(CURIOS_BODY) || stack.is(CURIOS_BACK) || stack.is(CURIOS_HANDS)) {
            return SHIRTS;
        }
        if (stack.is(CURIOS_NECKLACE) || stack.is(CURIOS_CHARM) || stack.is(CURIOS_BELT)
            || stack.is(CURIOS_BRACELET) || stack.is(CURIOS_CURIO)) {
            return RINGS;
        }
        if (item instanceof ArmorItem armor) {
            EquipmentSlot slot = armor.getEquipmentSlot();
            if (slot == EquipmentSlot.HEAD) {
                return HATS;
            }
            if (slot == EquipmentSlot.CHEST) {
                return SHIRTS;
            }
            if (slot == EquipmentSlot.LEGS) {
                return PANTS;
            }
            if (slot == EquipmentSlot.FEET) {
                return SHOES;
            }
        }
        return null;
    }

    private static TagKey<Item> itemTag(String namespace, String path) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(namespace, path));
    }
}
