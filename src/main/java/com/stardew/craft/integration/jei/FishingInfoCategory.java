package com.stardew.craft.integration.jei;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.fishing.data.SpawnFishRule;
import com.stardew.craft.item.ModItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;

import java.util.ArrayList;
import java.util.List;

public class FishingInfoCategory implements IRecipeCategory<SpawnFishRule> {
    public static final RecipeType<SpawnFishRule> RECIPE_TYPE = RecipeType.create(
            StardewCraft.MODID, "fishing_info", SpawnFishRule.class);
    
    private static final ResourceLocation EMPTY_SLOT_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            StardewCraft.MODID, "textures/gui/jei/empty_slot.png");
    
    private static final int GUI_WIDTH = 160;
    private static final int GUI_HEIGHT = 90;

    // 左侧绿色区域：放大的空白槽位 + 鱼
    private static final int SLOT_FRAME_X = 6;
    private static final int SLOT_FRAME_Y = 8;
    private static final int SLOT_FRAME_SCALE = 2; // 29x29 -> 58x58
    private static final int SLOT_FRAME_SIZE = 29;

    private static final int GUI_PADDING = 8;
    
    private final IDrawable icon;
    private final Component title;
    private final IDrawable emptySlot;
    private final IIngredientRenderer<ItemStack> scaledItemRenderer;
    
    @SuppressWarnings("null")
    private static final List<ItemStack> FISHING_RODS = List.of(
            new ItemStack(ModItems.FISHING_ROD.get()),
            new ItemStack(ModItems.TRAINING_ROD.get()),
            new ItemStack(ModItems.FIBERGLASS_ROD.get()),
            new ItemStack(ModItems.IRIDIUM_ROD.get()),
            new ItemStack(ModItems.ADVANCED_IRIDIUM_ROD.get())
    );
    
    @SuppressWarnings("null")
    public FishingInfoCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, 
                new ItemStack(ModItems.IRIDIUM_ROD.get()));
        this.title = Component.translatable("stardewcraft.jei.fishing_info");
        // 注意：JEI 的 createDrawable 默认按 256x256 纹理采样，小纹理必须显式 setTextureSize
        this.emptySlot = guiHelper.drawableBuilder(EMPTY_SLOT_TEXTURE, 0, 0, SLOT_FRAME_SIZE, SLOT_FRAME_SIZE)
            .setTextureSize(SLOT_FRAME_SIZE, SLOT_FRAME_SIZE)
            .build();
        this.scaledItemRenderer = new ScaledItemStackRenderer(SLOT_FRAME_SCALE);
        JeiDrawHelper.initGoldIcon(guiHelper);
    }
    
    @Override
    public RecipeType<SpawnFishRule> getRecipeType() {
        return RECIPE_TYPE;
    }
    
    @Override
    public Component getTitle() {
        return title;
    }
    
    @Override
    public int getWidth() {
        return GUI_WIDTH;
    }
    
    @Override
    public int getHeight() {
        return GUI_HEIGHT;
    }
    
    @Override
    public IDrawable getIcon() {
        return icon;
    }
    
    @SuppressWarnings("null")
    @Override
    public void setRecipe(@SuppressWarnings("null") IRecipeLayoutBuilder builder, @SuppressWarnings("null") SpawnFishRule recipe, @SuppressWarnings("null") IFocusGroup focuses) {
        // 钓竿槽位 - 右上角
        builder.addSlot(RecipeIngredientRole.CATALYST, 138, 2)
                .addItemStacks(FISHING_RODS);
        
        // 鱼物品槽位 - 在empty_slot框内，框在(5,5)，物品区域从(7,6)开始，所以物品在(5+7, 5+6)=(12,11)
        Item fishItem;
        try {
            fishItem = BuiltInRegistries.ITEM.get(ResourceLocation.parse(recipe.itemId()));
        } catch (Exception e) {
            fishItem = Items.COD;
        }
        if (fishItem == Items.AIR) {
            fishItem = Items.COD;
        }

        // 鱼槽位：物品渲染放大为 32x32，并让点击区域跟随渲染器尺寸
        int scaledFrameSize = SLOT_FRAME_SIZE * SLOT_FRAME_SCALE;
        int scaledItemSize = 16 * SLOT_FRAME_SCALE;
        int slotX = SLOT_FRAME_X + (scaledFrameSize - scaledItemSize) / 2;
        int slotY = SLOT_FRAME_Y + (scaledFrameSize - scaledItemSize) / 2;
        builder.addSlot(RecipeIngredientRole.OUTPUT, slotX, slotY)
            .setCustomRenderer(VanillaTypes.ITEM_STACK, scaledItemRenderer)
            .addItemStack(new ItemStack(fishItem));
    }
    
    @SuppressWarnings("null")
    @Override
    public void draw(@SuppressWarnings("null") SpawnFishRule recipe, @SuppressWarnings("null") IRecipeSlotsView recipeSlotsView, @SuppressWarnings("null") GuiGraphics guiGraphics, double mouseX, double mouseY) {
        // Background panel
        JeiDrawHelper.drawPanel(guiGraphics, 0, 0, GUI_WIDTH, GUI_HEIGHT);

        // 左侧绿色区域：整体缩放绘制（避免 blit 的UV>1 导致纹理平铺成 2x2）
        float scale = (float) SLOT_FRAME_SCALE;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(SLOT_FRAME_X, SLOT_FRAME_Y, 0);
        guiGraphics.pose().scale(scale, scale, 1.0f);
        emptySlot.draw(guiGraphics, 0, 0);
        guiGraphics.pose().popPose();
        
        Font font = Minecraft.getInstance().font;
        int valueColor = 0x404040;
        
        // 右侧蓝色区域：信息区从放大框右侧开始
        int x = SLOT_FRAME_X + (SLOT_FRAME_SIZE * SLOT_FRAME_SCALE) + 10;
        int y = 12;
        int lineHeight = 11;
        int maxWidth = GUI_WIDTH - x - GUI_PADDING;
        int maxY = GUI_HEIGHT - GUI_PADDING;
        
        // 难度 — 星号可视化 ★★★☆☆ + 文字
        int difficulty = recipe.difficulty();
        @SuppressWarnings("null")
        String difficultyStr = Component.translatable(getDifficultyTranslationKey(difficulty)).getString();
        int difficultyColor = getDifficultyColor(difficulty);
        int starCount = getDifficultyStars(difficulty);
        StringBuilder stars = new StringBuilder();
        for (int s = 0; s < 5; s++) {
            stars.append(s < starCount ? "\u2605" : "\u2606");
        }
        String difficultyLine = Component.translatable("stardewcraft.jei.difficulty", difficultyStr, difficulty).getString();
        // Draw stars first, then the text label
        guiGraphics.drawString(font, stars.toString(), x, y, difficultyColor, false);
        int starsWidth = font.width(stars.toString());
        String diffLabel = " " + difficultyLine;
        guiGraphics.drawString(font, diffLabel, x + starsWidth, y, difficultyColor, false);
        y += lineHeight;
        
        // 类型
        @SuppressWarnings("null")
        String motionType = Component.translatable(getMotionTypeTranslationKey(recipe.motionTypeId())).getString();
        y = drawWrappedLine(guiGraphics, font,
            Component.translatable("stardewcraft.jei.motion_type", motionType).getString(),
            x, y, 0x1E88E5, maxWidth, lineHeight, maxY);
        y += lineHeight;
        
        // 位置
        String location = getLocationDescription(recipe);
        y = drawWrappedLine(guiGraphics, font,
            Component.translatable("stardewcraft.jei.location", location).getString(),
            x, y, valueColor, maxWidth, lineHeight, maxY);
        y += lineHeight;
        
        // 时间
        String time = getTimeDescription(recipe);
        y = drawWrappedLine(guiGraphics, font,
            Component.translatable("stardewcraft.jei.time", time).getString(),
            x, y, valueColor, maxWidth, lineHeight, maxY);
        y += lineHeight;
        
        // 季节
        List<String> seasons = recipe.seasons();
        if (seasons != null && !seasons.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < seasons.size(); i++) {
                if (i > 0) sb.append("/");
                sb.append(Component.translatable(getSeasonTranslationKey(seasons.get(i))).getString());
            }
            y = drawWrappedLine(guiGraphics, font,
                    Component.translatable("stardewcraft.jei.season", sb.toString()).getString(),
                    x, y, 0x6D4C41, maxWidth, lineHeight, maxY);
            y += lineHeight;
        }
        
        // 天气
        String weather = recipe.weather();
        if (weather != null && !"any".equalsIgnoreCase(weather)) {
            @SuppressWarnings("null")
            String weatherStr = Component.translatable(getWeatherTranslationKey(weather)).getString();
            y = drawWrappedLine(guiGraphics, font,
                    Component.translatable("stardewcraft.jei.weather", weatherStr).getString(),
                    x, y, 0x2E7D32, maxWidth, lineHeight, maxY);
            y += lineHeight;
        }
        
        // 钓鱼等级
        int minLevel = recipe.minFishingLevel();
        if (minLevel > 0) {
            drawWrappedLine(guiGraphics, font,
                    Component.translatable("stardewcraft.jei.min_level", minLevel).getString(),
                    x, y, valueColor, maxWidth, lineHeight, maxY);
        }
    }

    private static int getDifficultyColor(int difficulty) {
        if (difficulty <= 20) return 0x2E7D32;
        if (difficulty <= 40) return 0x33691E;
        if (difficulty <= 60) return 0xF57C00;
        if (difficulty <= 80) return 0xD32F2F;
        return 0x7B1FA2;
    }

    @SuppressWarnings("null")
    private static int drawWrappedLine(
            GuiGraphics guiGraphics,
            Font font,
            String line,
            int x,
            int y,
            int color,
            int maxWidth,
            int lineHeight,
            int maxY
    ) {
        if (y > maxY) {
            return y;
        }

        int colon = line.indexOf(':');
        String prefix;
        String value;
        if (colon >= 0 && colon + 1 < line.length()) {
            int after = Math.min(colon + 2, line.length());
            prefix = line.substring(0, after);
            value = line.substring(after);
        } else {
            prefix = "";
            value = line;
        }

        int prefixWidth = prefix.isEmpty() ? 0 : font.width(prefix);
        int firstLineMax = Math.max(0, maxWidth - prefixWidth);
        List<String> wrapped = wrapValue(font, value, firstLineMax, maxWidth);
        if (wrapped.isEmpty()) {
            guiGraphics.drawString(font, line, x, y, color, false);
            return y;
        }

        // 第一行带前缀
        String first = prefix + wrapped.get(0);
        guiGraphics.drawString(font, first, x, y, color, false);
        int currentY = y;

        // 后续行缩进对齐
        for (int i = 1; i < wrapped.size(); i++) {
            currentY += lineHeight;
            if (currentY > maxY) break;
            guiGraphics.drawString(font, wrapped.get(i), x + prefixWidth, currentY, color, false);
        }

        return currentY;
    }

    private static List<String> wrapValue(Font font, String value, int firstLineMaxWidth, int otherLineMaxWidth) {
        List<String> out = new ArrayList<>();
        if (value == null) {
            return out;
        }
        String v = value.trim();
        if (v.isEmpty()) {
            out.add("");
            return out;
        }

        // 优先按 "," 分段（时间段/多段信息最常见）
        String[] parts = v.split(",\\s*");
        StringBuilder line = new StringBuilder();
        int max = firstLineMaxWidth;

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i].trim();
            if (part.isEmpty()) continue;

            String candidate = line.isEmpty() ? part : (line + ", " + part);
            if (font.width(candidate) <= max) {
                line.setLength(0);
                line.append(candidate);
                continue;
            }

            if (!line.isEmpty()) {
                out.add(line.toString());
                line.setLength(0);
                max = otherLineMaxWidth;
            }

            if (font.width(part) > max) {
                out.add(truncateToWidth(font, part, max));
            } else {
                line.append(part);
            }
        }

        if (!line.isEmpty()) {
            out.add(line.toString());
        }

        return out;
    }

    @SuppressWarnings("null")
    private static String truncateToWidth(Font font, String text, int maxWidth) {
        if (maxWidth <= 0) return "";
        String ellipsis = "...";
        if (font.width(text) <= maxWidth) return text;
        int available = Math.max(0, maxWidth - font.width(ellipsis));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            sb.append(c);
            if (font.width(sb.toString()) > available) {
                sb.setLength(Math.max(0, sb.length() - 1));
                break;
            }
        }
        return sb + ellipsis;
    }

    private static class ScaledItemStackRenderer implements IIngredientRenderer<ItemStack> {
        private final int scale;

        private ScaledItemStackRenderer(int scale) {
            this.scale = Math.max(1, scale);
        }

        @SuppressWarnings("null")
        @Override
        public void render(@SuppressWarnings("null") GuiGraphics guiGraphics, @SuppressWarnings("null") ItemStack ingredient) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().scale((float) scale, (float) scale, 1.0f);
            guiGraphics.renderItem(ingredient, 0, 0);
            guiGraphics.pose().popPose();
        }

        @SuppressWarnings("null")
        private static List<Component> buildTooltip(ItemStack ingredient, TooltipFlag tooltipFlag) {
            Minecraft minecraft = Minecraft.getInstance();
            try {
                return ingredient.getTooltipLines(Item.TooltipContext.of(minecraft.level), minecraft.player, tooltipFlag);
            } catch (Exception e) {
                return List.of();
            }
        }

        @Override
        @Deprecated(forRemoval = true, since = "19.5.4")
        public List<Component> getTooltip(@SuppressWarnings("null") ItemStack ingredient, @SuppressWarnings("null") TooltipFlag tooltipFlag) {
            return buildTooltip(ingredient, tooltipFlag);
        }

        @SuppressWarnings("null")
        @Override
        public void getTooltip(@SuppressWarnings("null") ITooltipBuilder tooltip, @SuppressWarnings("null") ItemStack ingredient, @SuppressWarnings("null") TooltipFlag tooltipFlag) {
            tooltip.addAll(buildTooltip(ingredient, tooltipFlag));
        }

        @Override
        public int getWidth() {
            return 16 * scale;
        }

        @Override
        public int getHeight() {
            return 16 * scale;
        }
    }
    
    private String getDifficultyTranslationKey(int difficulty) {
        if (difficulty <= 20) return "stardewcraft.jei.difficulty.easy";
        if (difficulty <= 40) return "stardewcraft.jei.difficulty.normal";
        if (difficulty <= 60) return "stardewcraft.jei.difficulty.medium";
        if (difficulty <= 80) return "stardewcraft.jei.difficulty.hard";
        if (difficulty <= 100) return "stardewcraft.jei.difficulty.very_hard";
        return "stardewcraft.jei.difficulty.legendary";
    }

    private int getDifficultyStars(int difficulty) {
        if (difficulty <= 20) return 1;
        if (difficulty <= 40) return 2;
        if (difficulty <= 60) return 3;
        if (difficulty <= 80) return 4;
        return 5;
    }
    
    private String getMotionTypeTranslationKey(int motionType) {
        return switch (motionType) {
            case 0 -> "stardewcraft.jei.motion.mixed";
            case 1 -> "stardewcraft.jei.motion.dart";
            case 2 -> "stardewcraft.jei.motion.smooth";
            case 3 -> "stardewcraft.jei.motion.sinker";
            case 4 -> "stardewcraft.jei.motion.floater";
            default -> "stardewcraft.jei.motion.unknown";
        };
    }
    
    private String getSeasonTranslationKey(String season) {
        return switch (season.toLowerCase()) {
            case "spring" -> "stardewcraft.jei.season.spring";
            case "summer" -> "stardewcraft.jei.season.summer";
            case "fall" -> "stardewcraft.jei.season.fall";
            case "winter" -> "stardewcraft.jei.season.winter";
            default -> "stardewcraft.jei.season.unknown";
        };
    }
    
    private String getWeatherTranslationKey(String weather) {
        return switch (weather.toLowerCase()) {
            case "rainy" -> "stardewcraft.jei.weather.rainy";
            case "sunny" -> "stardewcraft.jei.weather.sunny";
            default -> "stardewcraft.jei.weather.unknown";
        };
    }
    
    @SuppressWarnings("null")
    private String getLocationDescription(SpawnFishRule recipe) {
        List<String> tags = recipe.biomeTags();
        if (tags != null && !tags.isEmpty()) {
            String tag = tags.get(0);
            if (tag.contains("beach")) return Component.translatable("stardewcraft.jei.location.beach").getString();
            if (tag.contains("ocean")) return Component.translatable("stardewcraft.jei.location.ocean").getString();
            if (tag.contains("river")) return Component.translatable("stardewcraft.jei.location.river").getString();
            if (tag.contains("mountain_lake")) return Component.translatable("stardewcraft.jei.location.mountain_lake").getString();
            if (tag.contains("forest_pond")) return Component.translatable("stardewcraft.jei.location.forest_pond").getString();
            if (tag.contains("secret_woods")) return Component.translatable("stardewcraft.jei.location.secret_woods").getString();
            if (tag.contains("sewers")) return Component.translatable("stardewcraft.jei.location.sewers").getString();
            if (tag.contains("mines_20")) return Component.translatable("stardewcraft.jei.location.mines", "20F").getString();
            if (tag.contains("mines_60")) return Component.translatable("stardewcraft.jei.location.mines", "60F").getString();
            if (tag.contains("mines_100")) return Component.translatable("stardewcraft.jei.location.mines", "100F").getString();
            if (tag.contains("desert")) return Component.translatable("stardewcraft.jei.location.desert").getString();
            if (tag.contains("witch_swamp")) return Component.translatable("stardewcraft.jei.location.witch_swamp").getString();
            if (tag.contains("night_market")) return Component.translatable("stardewcraft.jei.location.night_market").getString();
            if (tag.contains("volcano")) return Component.translatable("stardewcraft.jei.location.volcano").getString();
            if (tag.contains("ginger_island")) return Component.translatable("stardewcraft.jei.location.ginger_island").getString();
            if (tag.contains("pirate_cove")) return Component.translatable("stardewcraft.jei.location.pirate_cove").getString();
            return tag.replaceAll("#stardewcraft:is_", "").replace("_", " ");
        }
        
        List<String> biomes = recipe.biomes();
        if (biomes != null && !biomes.isEmpty()) {
            return biomes.get(0).replace("stardewcraft:", "").replace("_", " ");
        }
        
        return Component.translatable("stardewcraft.jei.location.any").getString();
    }
    
    @SuppressWarnings("null")
    private String getTimeDescription(SpawnFishRule recipe) {
        List<int[]> timeRanges = recipe.timeRanges();
        if (timeRanges == null || timeRanges.isEmpty()) {
            return Component.translatable("stardewcraft.jei.time.all_day").getString();
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < timeRanges.size(); i++) {
            int[] range = timeRanges.get(i);
            if (i > 0) sb.append(", ");
            sb.append(formatTime(range[0])).append("-").append(formatTime(range[1]));
        }
        return sb.toString();
    }
    
    private String formatTime(int stardewTime) {
        int hour = stardewTime / 100;
        int minute = stardewTime % 100;
        if (hour >= 24) hour -= 24;
        return String.format("%d:%02d", hour, minute);
    }
}
