package com.stardew.craft.combat;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.CustomModelData;

public final class WeaponForgeData {
    public static final String TAG_STARDEW_FORGE = "StardewForge";
    public static final String TAG_GEM_FORGES = "GemForges";
    public static final String TAG_GEM_ITEM_ID = "ItemId";
    public static final String TAG_GEM_LEVEL = "Level";
    public static final String TAG_PRISMATIC_ENCHANTMENT = "PrismaticEnchantment";
    public static final String TAG_PREVIOUS_ENCHANTMENTS = "PreviousEnchantments";
    public static final String TAG_GALAXY_SOUL_LEVEL = "GalaxySoulLevel";
    public static final String TAG_APPEARANCE_WEAPON_ID = "AppearanceWeaponId";
    public static final String TAG_DRAGON_TOOTH_ENCHANTMENT = "DragonToothEnchantment";
    public static final String TAG_DIAMOND_FORGE = "DiamondForge";
    public static final int APPEARANCE_CUSTOM_MODEL_DATA_BASE = 700_000;
    private static final int APPEARANCE_CUSTOM_MODEL_DATA_RANGE = 100_000;

    private WeaponForgeData() {
    }

    public record GemForge(String itemId, int level) {
    }

    public record DragonToothBonus(String kind, int level) {
        public DragonToothBonus {
            kind = kind == null ? "" : kind;
            level = Math.max(0, level);
        }
    }

    public record State(
            List<GemForge> gemForges,
            String prismaticEnchantment,
            List<String> previousEnchantments,
            int galaxySoulLevel,
            String appearanceWeaponId,
            String dragonToothEnchantment,
            boolean diamondForge) {
        public State {
            gemForges = List.copyOf(gemForges);
            previousEnchantments = List.copyOf(previousEnchantments);
            prismaticEnchantment = prismaticEnchantment == null ? "" : prismaticEnchantment;
            appearanceWeaponId = appearanceWeaponId == null ? "" : appearanceWeaponId;
            dragonToothEnchantment = dragonToothEnchantment == null ? "" : dragonToothEnchantment;
        }
    }

    public static State empty() {
        return new State(List.of(), "", List.of(), 0, "", "", false);
    }

    public static boolean hasMeaningfulForgeState(ItemStack stack) {
        State state = read(stack);
        return !state.gemForges().isEmpty()
                || !state.prismaticEnchantment().isEmpty()
                || !state.previousEnchantments().isEmpty()
                || state.galaxySoulLevel() > 0
                || !state.appearanceWeaponId().isEmpty()
                || !state.dragonToothEnchantment().isEmpty()
                || state.diamondForge();
    }

    public static List<DragonToothBonus> dragonToothBonuses(String value) {
        if (value == null || value.isEmpty()) {
            return List.of();
        }
        List<DragonToothBonus> bonuses = new ArrayList<>();
        for (String entry : value.split(";")) {
            DragonToothBonus bonus = parseDragonToothBonus(entry);
            if (!bonus.kind().isEmpty() && bonus.level() > 0) {
                bonuses.add(bonus);
            }
        }
        return List.copyOf(bonuses);
    }

    public static String encodeDragonToothBonuses(List<DragonToothBonus> bonuses) {
        if (bonuses == null || bonuses.isEmpty()) {
            return "";
        }
        List<String> entries = new ArrayList<>();
        for (DragonToothBonus bonus : bonuses) {
            if (bonus != null && !bonus.kind().isEmpty() && bonus.level() > 0) {
                entries.add(bonus.kind() + ":" + bonus.level());
            }
        }
        return String.join(";", entries);
    }

    private static DragonToothBonus parseDragonToothBonus(String value) {
        if (value == null || value.isEmpty()) {
            return new DragonToothBonus("", 0);
        }
        String[] parts = value.split(":", 2);
        if (parts.length != 2) {
            return new DragonToothBonus(value, 1);
        }
        try {
            return new DragonToothBonus(parts[0], Integer.parseInt(parts[1]));
        } catch (NumberFormatException exception) {
            return new DragonToothBonus(parts[0], 1);
        }
    }

    public static State read(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return empty();
        }

        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) {
            return empty();
        }

        CompoundTag root = data.copyTag();
        if (!root.contains(TAG_STARDEW_FORGE, Tag.TAG_COMPOUND)) {
            return empty();
        }

        CompoundTag forgeTag = root.getCompound(TAG_STARDEW_FORGE);
        List<GemForge> gemForges = new ArrayList<>();
        ListTag gemList = forgeTag.getList(TAG_GEM_FORGES, Tag.TAG_COMPOUND);
        for (int i = 0; i < gemList.size(); i++) {
            CompoundTag gemTag = gemList.getCompound(i);
            String itemId = gemTag.getString(TAG_GEM_ITEM_ID);
            if (!itemId.isEmpty()) {
                gemForges.add(new GemForge(itemId, gemTag.getInt(TAG_GEM_LEVEL)));
            }
        }

        List<String> previousEnchantments = new ArrayList<>();
        ListTag historyList = forgeTag.getList(TAG_PREVIOUS_ENCHANTMENTS, Tag.TAG_STRING);
        for (int i = 0; i < historyList.size(); i++) {
            String enchantment = historyList.getString(i);
            if (!enchantment.isEmpty()) {
                previousEnchantments.add(enchantment);
            }
        }

        return new State(
                gemForges,
                forgeTag.getString(TAG_PRISMATIC_ENCHANTMENT),
                previousEnchantments,
                forgeTag.getInt(TAG_GALAXY_SOUL_LEVEL),
                forgeTag.getString(TAG_APPEARANCE_WEAPON_ID),
                forgeTag.getString(TAG_DRAGON_TOOTH_ENCHANTMENT),
                forgeTag.getBoolean(TAG_DIAMOND_FORGE));
    }

    public static void ensure(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        CompoundTag root = data != null ? data.copyTag() : new CompoundTag();
        if (!root.contains(TAG_STARDEW_FORGE, Tag.TAG_COMPOUND)) {
            root.put(TAG_STARDEW_FORGE, toTag(empty()));
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));
        }
        syncAppearanceModelData(stack, read(stack).appearanceWeaponId());
    }

    public static void write(ItemStack stack, State state) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        CompoundTag root = data != null ? data.copyTag() : new CompoundTag();
        root.put(TAG_STARDEW_FORGE, toTag(state));
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));
        syncAppearanceModelData(stack, state.appearanceWeaponId());
    }

    public static void clear(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) {
            return;
        }
        CompoundTag root = data.copyTag();
        root.remove(TAG_STARDEW_FORGE);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));
        syncAppearanceModelData(stack, "");
    }

    public static int appearanceCustomModelData(String itemId) {
        if (itemId == null || itemId.isEmpty()) {
            return 0;
        }
        return APPEARANCE_CUSTOM_MODEL_DATA_BASE + Math.floorMod(itemId.hashCode(), APPEARANCE_CUSTOM_MODEL_DATA_RANGE);
    }

    private static void syncAppearanceModelData(ItemStack stack, String appearanceWeaponId) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        if (appearanceWeaponId != null && !appearanceWeaponId.isEmpty()) {
            stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(appearanceCustomModelData(appearanceWeaponId)));
            return;
        }

        CustomModelData modelData = stack.get(DataComponents.CUSTOM_MODEL_DATA);
        if (modelData != null && isAppearanceCustomModelData(modelData.value())) {
            stack.remove(DataComponents.CUSTOM_MODEL_DATA);
        }
    }

    private static boolean isAppearanceCustomModelData(int value) {
        return value >= APPEARANCE_CUSTOM_MODEL_DATA_BASE
                && value < APPEARANCE_CUSTOM_MODEL_DATA_BASE + APPEARANCE_CUSTOM_MODEL_DATA_RANGE;
    }

    private static CompoundTag toTag(State state) {
        CompoundTag forgeTag = new CompoundTag();
        ListTag gemList = new ListTag();
        for (GemForge gemForge : state.gemForges()) {
            if (gemForge.itemId() == null || gemForge.itemId().isEmpty()) {
                continue;
            }
            CompoundTag gemTag = new CompoundTag();
            gemTag.putString(TAG_GEM_ITEM_ID, gemForge.itemId());
            gemTag.putInt(TAG_GEM_LEVEL, gemForge.level());
            gemList.add(gemTag);
        }
        forgeTag.put(TAG_GEM_FORGES, gemList);
        forgeTag.putString(TAG_PRISMATIC_ENCHANTMENT, state.prismaticEnchantment());

        ListTag historyList = new ListTag();
        for (String enchantment : state.previousEnchantments()) {
            if (enchantment != null && !enchantment.isEmpty()) {
                historyList.add(StringTag.valueOf(enchantment));
            }
        }
        forgeTag.put(TAG_PREVIOUS_ENCHANTMENTS, historyList);
        forgeTag.putInt(TAG_GALAXY_SOUL_LEVEL, state.galaxySoulLevel());
        forgeTag.putString(TAG_APPEARANCE_WEAPON_ID, state.appearanceWeaponId());
        forgeTag.putString(TAG_DRAGON_TOOTH_ENCHANTMENT, state.dragonToothEnchantment());
        forgeTag.putBoolean(TAG_DIAMOND_FORGE, state.diamondForge());
        return forgeTag;
    }
}