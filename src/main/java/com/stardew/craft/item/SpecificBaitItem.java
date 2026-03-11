package com.stardew.craft.item;

import com.stardew.craft.item.artisan.PreservesIngredientDataManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;

import java.util.List;

@SuppressWarnings("null")
public class SpecificBaitItem extends Item implements IStardewItem {
    private static final String TAG_TARGET_FISH_ID = "TargetFishId";
    private static final String TAG_TARGET_FISH_COLOR = "TargetFishColor";
    private static final int BASE_SELL_PRICE = 5;

    public SpecificBaitItem(Properties properties) {
        super(properties);
    }

    @Override
    public String getItemTypeKey() {
        return "stardewcraft.type.fishing";
    }

    @Override
    public int getSellPrice(ItemStack stack) {
        return BASE_SELL_PRICE;
    }

    @SuppressWarnings("null")
    @Override
    public Component getName(@SuppressWarnings("null") ItemStack stack) {
        ItemStack fishStack = getTargetFishStack(stack);
        if (!fishStack.isEmpty()) {
            return Component.translatable("item.stardewcraft.targeted_bait.flavored_name", fishStack.getHoverName())
                    .withStyle(ChatFormatting.WHITE);
        }
        return Component.translatable(this.getDescriptionId(stack)).withStyle(ChatFormatting.WHITE);
    }

    @SuppressWarnings("null")
    @Override
    public void appendHoverText(@SuppressWarnings("null") ItemStack stack,
                                @SuppressWarnings("null") TooltipContext context,
                                @SuppressWarnings("null") List<Component> tooltipComponents,
                                @SuppressWarnings("null") TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        ItemStack fishStack = getTargetFishStack(stack);
        if (!fishStack.isEmpty()) {
            tooltipComponents.add(Component.translatable("item.stardewcraft.targeted_bait.target_desc", fishStack.getHoverName())
                    .withStyle(ChatFormatting.GRAY));
        }
    }

    public int getColor(ItemStack stack) {
        return getIntTag(stack, TAG_TARGET_FISH_COLOR, -1);
    }

    public static ItemStack createForFish(ItemStack fishStack, int count) {
        ItemStack stack = new ItemStack(ModItems.TARGETED_BAIT.get(), Math.max(1, count));
        setTargetFish(stack, fishStack);
        return stack;
    }

    public static void setTargetFish(ItemStack baitStack, ItemStack fishStack) {
        if (baitStack == null || baitStack.isEmpty() || fishStack == null || fishStack.isEmpty()) {
            return;
        }
        ResourceLocation fishId = BuiltInRegistries.ITEM.getKey(fishStack.getItem());
        CompoundTag tag = getOrCreateTag(baitStack);
        tag.putString(TAG_TARGET_FISH_ID, fishId.toString());

        int color = PreservesIngredientDataManager.getData(fishId)
                .map(PreservesIngredientDataManager.IngredientData::getColorRgb)
                .orElse(-1);
        if (color >= 0) {
            tag.putInt(TAG_TARGET_FISH_COLOR, color);
        }
        applyTag(baitStack, tag);
    }

    public static String getTargetFishId(ItemStack stack) {
        return getStringTag(stack, TAG_TARGET_FISH_ID, null);
    }

    @SuppressWarnings("null")
    public static ItemStack getTargetFishStack(ItemStack stack) {
        String raw = getTargetFishId(stack);
        if (raw == null || raw.isBlank()) {
            return ItemStack.EMPTY;
        }
        try {
            ResourceLocation fishId = ResourceLocation.parse(raw);
            Item item = BuiltInRegistries.ITEM.get(fishId);
            ItemStack fish = new ItemStack(item);
            return fish.isEmpty() ? ItemStack.EMPTY : fish;
        } catch (Exception ignored) {
            return ItemStack.EMPTY;
        }
    }

    private static CompoundTag getOrCreateTag(ItemStack stack) {
        CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        return data.copyTag();
    }

    private static void applyTag(ItemStack stack, CompoundTag tag) {
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    private static int getIntTag(ItemStack stack, String key, int fallback) {
        CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = data.copyTag();
        if (tag.contains(key)) {
            return tag.getInt(key);
        }
        return fallback;
    }

    private static String getStringTag(ItemStack stack, String key, String fallback) {
        CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = data.copyTag();
        if (tag.contains(key)) {
            return tag.getString(key);
        }
        return fallback;
    }
}
