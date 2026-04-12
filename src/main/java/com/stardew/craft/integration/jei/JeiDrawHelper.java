package com.stardew.craft.integration.jei;

import com.stardew.craft.StardewCraft;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Shared JEI rendering utilities — Stardew Valley visual style.
 * Provides background panels, arrows, gold/coin display, season indicators,
 * and SDV-themed slot rendering for all JEI categories.
 */
@SuppressWarnings("null")
public final class JeiDrawHelper {

    // ─── SDV Color Palette ─────────────────────────────────────────────
    /** Panel background — warm cream / parchment */
    public static final int PANEL_BG        = 0xFFF5E4C8;
    /** Outer panel border — dark wood brown  */
    public static final int PANEL_BORDER    = 0xFF8B5E34;
    /** Inner panel highlight — warm gold      */
    public static final int PANEL_HIGHLIGHT = 0xFFD4B07A;

    /** Primary label text — dark brown         */
    public static final int TEXT_TITLE      = 0xFF5B3A1F;
    /** Secondary body text — warm brown        */
    public static final int TEXT_BODY       = 0xFF6B5244;
    /** Price / gold text — dark goldenrod      */
    public static final int TEXT_GOLD       = 0xFFB8860B;
    /** Muted / hint text — warm gray           */
    public static final int TEXT_MUTED      = 0xFF9E8E7E;

    /** Arrow & decorative elements — warm brown */
    public static final int ARROW_COLOR     = 0xFFA0784C;
    /** Arrow outline / shadow */
    public static final int ARROW_SHADOW    = 0xFF6B4A2A;

    /** Season colours */
    public static final int SEASON_SPRING = 0xFF4CAF50;
    public static final int SEASON_SUMMER = 0xFFFF9800;
    public static final int SEASON_FALL   = 0xFFE65100;
    public static final int SEASON_WINTER = 0xFF42A5F5;

    // Gold icon texture — 16×16
    private static final ResourceLocation GOLD_ICON =
            ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/gold_icon.png");

    private static IDrawable goldIconDrawable;

    private JeiDrawHelper() {}

    // ─── Gold Icon ─────────────────────────────────────────────────────

    /** Initialise the gold icon drawable (call once during category construction). */
    public static void initGoldIcon(IGuiHelper guiHelper) {
        if (goldIconDrawable == null) {
            goldIconDrawable = guiHelper.drawableBuilder(GOLD_ICON, 0, 0, 16, 16)
                    .setTextureSize(16, 16)
                    .build();
        }
    }

    // ─── Background Panel ──────────────────────────────────────────────

    /**
     * Draw a warm Stardew-style background panel.
     * Features double border (dark outer + gold inner) with parchment fill.
     */
    public static void drawPanel(GuiGraphics gg, int x, int y, int w, int h) {
        // Background fill
        gg.fill(x + 2, y + 2, x + w - 2, y + h - 2, PANEL_BG);

        // Outer border — dark wood
        // Top / bottom
        gg.fill(x + 1, y, x + w - 1, y + 1, PANEL_BORDER);
        gg.fill(x + 1, y + h - 1, x + w - 1, y + h, PANEL_BORDER);
        // Left / right
        gg.fill(x, y + 1, x + 1, y + h - 1, PANEL_BORDER);
        gg.fill(x + w - 1, y + 1, x + w, y + h - 1, PANEL_BORDER);

        // Inner highlight — gold
        // Top / bottom
        gg.fill(x + 2, y + 1, x + w - 2, y + 2, PANEL_HIGHLIGHT);
        gg.fill(x + 2, y + h - 2, x + w - 2, y + h - 1, PANEL_HIGHLIGHT);
        // Left / right
        gg.fill(x + 1, y + 2, x + 2, y + h - 2, PANEL_HIGHLIGHT);
        gg.fill(x + w - 2, y + 2, x + w - 1, y + h - 2, PANEL_HIGHLIGHT);
    }

    // ─── Arrow ─────────────────────────────────────────────────────────

    /**
     * Draw a clean right-pointing arrow at (x, y).
     * Size: 20×9 pixels. Centred vertically around y+4.
     */
    public static void drawArrow(GuiGraphics gg, int x, int y) {
        // Shadow (offset by 1,1)
        drawArrowShape(gg, x + 1, y + 1, ARROW_SHADOW);
        // Main arrow
        drawArrowShape(gg, x, y, ARROW_COLOR);
    }

    private static void drawArrowShape(GuiGraphics gg, int x, int y, int c) {
        // Shaft — 14px long, 3px tall, centred
        gg.fill(x, y + 3, x + 14, y + 6, c);
        // Arrow head — triangle (5 rows getting narrower)
        gg.fill(x + 12, y + 1, x + 14, y + 8, c);
        gg.fill(x + 14, y + 2, x + 16, y + 7, c);
        gg.fill(x + 16, y + 3, x + 18, y + 6, c);
        gg.fill(x + 18, y + 4, x + 19, y + 5, c);
    }

    // ─── Gold Amount ───────────────────────────────────────────────────

    /**
     * Draw a gold amount with coin icon: [🪙 1000g]
     * @return the total width drawn (icon + text)
     */
    public static int drawGoldAmount(GuiGraphics gg, Font font, int x, int y, int amount) {
        // Draw gold icon at half scale (8×8)
        gg.pose().pushPose();
        gg.pose().translate(x, y - 1, 0);
        gg.pose().scale(0.5f, 0.5f, 1.0f);
        if (goldIconDrawable != null) {
            goldIconDrawable.draw(gg, 0, 0);
        }
        gg.pose().popPose();

        // Price text
        String text = amount + "g";
        gg.drawString(font, text, x + 10, y, TEXT_GOLD, false);
        return 10 + font.width(text);
    }

    /**
     * Draw a gold amount with coin icon, with a custom text suffix.
     */
    public static int drawGoldText(GuiGraphics gg, Font font, int x, int y, String text) {
        // Draw gold icon at half scale (8×8)
        gg.pose().pushPose();
        gg.pose().translate(x, y - 1, 0);
        gg.pose().scale(0.5f, 0.5f, 1.0f);
        if (goldIconDrawable != null) {
            goldIconDrawable.draw(gg, 0, 0);
        }
        gg.pose().popPose();

        gg.drawString(font, text, x + 10, y, TEXT_GOLD, false);
        return 10 + font.width(text);
    }

    // ─── Season Indicators ─────────────────────────────────────────────

    /**
     * Draw season indicator dots for the given season set.
     * @param seasons set of season indices (0=spring .. 3=winter)
     * @return total width drawn
     */
    public static int drawSeasonDots(GuiGraphics gg, int x, int y, java.util.Set<Integer> seasons) {
        if (seasons == null || seasons.isEmpty()) return 0;
        int dx = 0;
        for (int s = 0; s < 4; s++) {
            if (seasons.contains(s)) {
                int color = getSeasonColor(s);
                // 4×4 dot with 1px dark border
                gg.fill(x + dx, y, x + dx + 6, y + 6, 0xFF3B2412); // border
                gg.fill(x + dx + 1, y + 1, x + dx + 5, y + 5, color); // fill
                dx += 8;
            }
        }
        return dx;
    }

    /**
     * Draw season label dots with text abbreviation.
     * @param seasons set of season indices
     * @return total width drawn
     */
    public static int drawSeasonLabels(GuiGraphics gg, Font font, int x, int y,
                                       java.util.Set<Integer> seasons) {
        if (seasons == null || seasons.isEmpty()) return 0;
        int dx = 0;
        for (int s = 0; s < 4; s++) {
            if (seasons.contains(s)) {
                int color = getSeasonColor(s);
                String label = getSeasonAbbrev(s);
                gg.drawString(font, label, x + dx, y, color, false);
                dx += font.width(label) + 3;
            }
        }
        return dx;
    }

    public static int getSeasonColor(int season) {
        return switch (season) {
            case 0 -> SEASON_SPRING;
            case 1 -> SEASON_SUMMER;
            case 2 -> SEASON_FALL;
            case 3 -> SEASON_WINTER;
            default -> TEXT_MUTED;
        };
    }

    @SuppressWarnings("null")
    private static String getSeasonAbbrev(int season) {
        return switch (season) {
            case 0 -> Component.translatable("stardewcraft.jei.season.spring.abbr").getString();
            case 1 -> Component.translatable("stardewcraft.jei.season.summer.abbr").getString();
            case 2 -> Component.translatable("stardewcraft.jei.season.fall.abbr").getString();
            case 3 -> Component.translatable("stardewcraft.jei.season.winter.abbr").getString();
            default -> "??";
        };
    }

    // ─── Slot Background ───────────────────────────────────────────────

    /**
     * Draw a styled slot background (18×18) with SDV color theme.
     * Slightly tinted compared to vanilla JEI slots.
     */
    public static void drawSlotBg(GuiGraphics gg, int x, int y) {
        // Outer border
        gg.fill(x, y, x + 18, y + 18, 0xFF8B7355);
        // Inner recess
        gg.fill(x + 1, y + 1, x + 17, y + 17, 0xFF373737);
        // Slot interior
        gg.fill(x + 1, y + 1, x + 17, y + 17, 0xFF8B8B8B);
        // Highlight edge (top-left)
        gg.fill(x + 1, y + 1, x + 17, y + 2, 0xFFB0A080);
        gg.fill(x + 1, y + 1, x + 2, y + 17, 0xFFB0A080);
        // Shadow edge (bottom-right)
        gg.fill(x + 1, y + 16, x + 17, y + 17, 0xFF555555);
        gg.fill(x + 16, y + 1, x + 17, y + 17, 0xFF555555);
    }

    /**
     * Draw a highlighted output slot (golden glow effect).
     */
    public static void drawOutputSlotBg(GuiGraphics gg, int x, int y) {
        // Outer golden glow (24×24 centred on 18×18)
        gg.fill(x - 3, y - 3, x + 21, y + 21, 0x30D4A853);
        gg.fill(x - 2, y - 2, x + 20, y + 20, 0x40D4A853);
        gg.fill(x - 1, y - 1, x + 19, y + 19, 0x50D4A853);
        // Standard slot on top
        drawSlotBg(gg, x, y);
    }

    // ─── Processing Time ───────────────────────────────────────────────

    /**
     * Format SDV processing time (in minutes) to a human-readable string.
     * Uses translation keys for time units.
     */
    @SuppressWarnings("null")
    public static String formatTime(int minutes) {
        String dUnit = Component.translatable("stardewcraft.jei.time.day_unit").getString();
        String hUnit = Component.translatable("stardewcraft.jei.time.hour_unit").getString();
        String mUnit = Component.translatable("stardewcraft.jei.time.minute_unit").getString();
        if (minutes >= 1440) {
            int days = minutes / 1440;
            int remainHours = (minutes % 1440) / 60;
            if (remainHours > 0) return days + dUnit + " " + remainHours + hUnit;
            return days + dUnit;
        }
        if (minutes >= 60) {
            int hours = minutes / 60;
            int remainMin = minutes % 60;
            if (remainMin > 0) return hours + hUnit + " " + remainMin + mUnit;
            return hours + hUnit;
        }
        return minutes + mUnit;
    }

    // ─── Divider Lines ─────────────────────────────────────────────────

    /**
     * Draw a horizontal decorative divider (thin dashed style).
     */
    public static void drawDivider(GuiGraphics gg, int x, int y, int width) {
        for (int i = 0; i < width; i += 3) {
            int segW = Math.min(2, width - i);
            gg.fill(x + i, y, x + i + segW, y + 1, 0x40806040);
        }
    }

    // ─── NPC Portrait ──────────────────────────────────────────────────

    /**
     * Draw an NPC portrait cropped from the portrait sprite sheet.
     * Portraits are stored as 2×N grids of 64×64 faces.
     * This draws the first face (top-left 64×64) scaled to size×size.
     *
     * @param npcId lowercase NPC id matching the portrait filename (e.g. "pierre")
     * @param size  rendered size (e.g. 28 for 28×28 display)
     */
    public static void drawNpcPortrait(GuiGraphics gg, String npcId, int x, int y, int size) {
        ResourceLocation portraitTex = ResourceLocation.fromNamespaceAndPath(
                StardewCraft.MODID, "textures/portraits/" + npcId.toLowerCase() + ".png");
        int[] texSize = getPortraitTextureSize(npcId);
        int texW = texSize[0];
        int texH = texSize[1];
        // Face region: top-left 64×64 for normal sheets, full image for single-face textures
        int faceW = Math.min(64, texW);
        int faceH = Math.min(64, texH);
        float scale = (float) size / faceW;
        gg.pose().pushPose();
        gg.pose().translate(x, y, 0);
        gg.pose().scale(scale, scale, 1.0f);
        gg.blit(portraitTex, 0, 0, 0, 0, faceW, faceH, texW, texH);
        gg.pose().popPose();
    }

    /**
     * Return the actual pixel dimensions {width, height} for a portrait texture.
     * Falls back to 128×128 for unknown NPCs.
     */
    private static int[] getPortraitTextureSize(String npcId) {
        return PORTRAIT_TEX_SIZES.getOrDefault(npcId.toLowerCase(), new int[]{128, 128});
    }

    private static final java.util.Map<String, int[]> PORTRAIT_TEX_SIZES = java.util.Map.ofEntries(
            java.util.Map.entry("pierre",  new int[]{128, 192}),
            java.util.Map.entry("willy",   new int[]{128, 128}),
            java.util.Map.entry("marnie",  new int[]{128, 192}),
            java.util.Map.entry("sandy",   new int[]{128, 128}),
            java.util.Map.entry("clint",   new int[]{128, 256}),
            java.util.Map.entry("gus",     new int[]{128, 128}),
            java.util.Map.entry("harvey",  new int[]{128, 384}),
            java.util.Map.entry("marlon",  new int[]{64, 64}),
            java.util.Map.entry("robin",   new int[]{128, 256})
    );

    // ─── Chance Text ───────────────────────────────────────────────────

    /**
     * Draw a probability percentage with colour coding.
     * 100%=green, 50–99%=yellow-green, 10–49%=orange, <10%=red.
     */
    public static void drawChanceText(GuiGraphics gg, Font font, int x, int y, int chancePercent) {
        int color;
        if (chancePercent >= 100) {
            color = 0xFF2E7D32; // green
        } else if (chancePercent >= 50) {
            color = 0xFF689F38; // yellow-green
        } else if (chancePercent >= 10) {
            color = 0xFFF57C00; // orange
        } else {
            color = 0xFFD32F2F; // red
        }
        String text = chancePercent + "%";
        gg.drawString(font, text, x, y, color, false);
    }

    // ─── Quality Star ──────────────────────────────────────────────────

    /** Quality constants matching QualityHelper */
    public static final int QUALITY_NORMAL  = 0;
    public static final int QUALITY_SILVER  = 1;
    public static final int QUALITY_GOLD    = 2;
    public static final int QUALITY_IRIDIUM = 3;

    /**
     * Draw a coloured quality star character at the given position.
     * Silver=#C0C0C0, Gold=#FFD700, Iridium=#B044DD. Normal draws nothing.
     */
    public static void drawQualityStar(GuiGraphics gg, Font font, int x, int y, int quality) {
        if (quality <= QUALITY_NORMAL) return;
        int color = switch (quality) {
            case QUALITY_SILVER -> 0xFFC0C0C0;
            case QUALITY_GOLD -> 0xFFFFD700;
            default -> 0xFFB044DD; // iridium
        };
        gg.drawString(font, "★", x, y, color, false);
    }

    // ─── Energy & Health ───────────────────────────────────────────────

    private static final ResourceLocation ENERGY_ICON =
            ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/energy.png");
    private static final ResourceLocation HEALTH_ICON =
            ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/health.png");

    private static IDrawable energyIconDrawable;
    private static IDrawable healthIconDrawable;

    /** Initialise the energy/health icon drawables (call once during category construction). */
    public static void initStatIcons(IGuiHelper guiHelper) {
        if (energyIconDrawable == null) {
            energyIconDrawable = guiHelper.drawableBuilder(ENERGY_ICON, 0, 0, 16, 16)
                    .setTextureSize(16, 16)
                    .build();
        }
        if (healthIconDrawable == null) {
            healthIconDrawable = guiHelper.drawableBuilder(HEALTH_ICON, 0, 0, 16, 16)
                    .setTextureSize(16, 16)
                    .build();
        }
    }

    /**
     * Draw energy and health values with their respective icons.
     * Layout: [⚡ energy] [❤ health]
     * @return total width drawn
     */
    public static int drawEnergyHealth(GuiGraphics gg, Font font, int x, int y, int energy, int health) {
        int dx = 0;
        if (energy != 0) {
            // Energy icon at half scale
            gg.pose().pushPose();
            gg.pose().translate(x + dx, y - 1, 0);
            gg.pose().scale(0.5f, 0.5f, 1.0f);
            if (energyIconDrawable != null) {
                energyIconDrawable.draw(gg, 0, 0);
            }
            gg.pose().popPose();
            String eText = String.valueOf(energy);
            gg.drawString(font, eText, x + dx + 10, y, 0xFF4CAF50, false);
            dx += 10 + font.width(eText) + 4;
        }
        if (health != 0) {
            // Health icon at half scale
            gg.pose().pushPose();
            gg.pose().translate(x + dx, y - 1, 0);
            gg.pose().scale(0.5f, 0.5f, 1.0f);
            if (healthIconDrawable != null) {
                healthIconDrawable.draw(gg, 0, 0);
            }
            gg.pose().popPose();
            String hText = String.valueOf(health);
            gg.drawString(font, hText, x + dx + 10, y, 0xFFE53935, false);
            dx += 10 + font.width(hText);
        }
        return dx;
    }
}
