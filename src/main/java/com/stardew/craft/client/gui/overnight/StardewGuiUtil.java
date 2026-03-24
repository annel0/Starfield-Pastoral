package com.stardew.craft.client.gui.overnight;

import com.stardew.craft.StardewCraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("null")
public class StardewGuiUtil {

    public static final ResourceLocation CURSORS = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/cursors.png");
    public static final int CURSORS_WIDTH = 704;
    public static final int CURSORS_HEIGHT = 2256;
    public static final ResourceLocation CURSORS2 = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/mouse_cursors2.png");
    public static final int CURSORS2_WIDTH = 256;
    public static final int CURSORS2_HEIGHT = 320;
    public static final ResourceLocation CURSORS_1_6 = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/cursors_1_6.png");
    public static final int CURSORS_1_6_WIDTH = 512;
    public static final int CURSORS_1_6_HEIGHT = 512;
    public static final ResourceLocation MENU_TILES = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/animal_query/menu_tiles.png");
    public static final int MENU_TILES_WIDTH = 256;
    public static final int MENU_TILES_HEIGHT = 1152;

    public static void drawFromCursors(GuiGraphics graphics, int x, int y, int u, int v, int width, int height, float scale) {
        drawFromCursors(graphics, x, y, u, v, width, height, scale, 1.0f);
    }
    
    public static void drawFromCursors(GuiGraphics graphics, int x, int y, int u, int v, int width, int height, float scale, float alpha) {
        drawFromCursorsTint(graphics, x, y, u, v, width, height, scale, 1.0F, 1.0F, 1.0F, alpha);
    }

    public static void drawFromCursorsTint(GuiGraphics graphics, int x, int y, int u, int v, int width, int height, float scale, float red, float green, float blue, float alpha) {
        graphics.setColor(red, green, blue, alpha);
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        graphics.pose().scale(scale, scale, 1.0f);
        graphics.blit(CURSORS, 0, 0, u, v, width, height, CURSORS_WIDTH, CURSORS_HEIGHT);
        graphics.pose().popPose();
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void drawFromCursors2(GuiGraphics graphics, int x, int y, int u, int v, int width, int height, float scale) {
        drawFromCursors2(graphics, x, y, u, v, width, height, scale, 1.0f);
    }

    public static void drawFromCursors2(GuiGraphics graphics, int x, int y, int u, int v, int width, int height, float scale, float alpha) {
        graphics.setColor(1.0F, 1.0F, 1.0F, alpha);
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        graphics.pose().scale(scale, scale, 1.0f);
        graphics.blit(CURSORS2, 0, 0, u, v, width, height, CURSORS2_WIDTH, CURSORS2_HEIGHT);
        graphics.pose().popPose();
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void drawFromCursors16(GuiGraphics graphics, int x, int y, int u, int v, int width, int height, float scale) {
        drawFromCursors16(graphics, x, y, u, v, width, height, scale, 1.0f);
    }

    public static void drawFromCursors16(GuiGraphics graphics, int x, int y, int u, int v, int width, int height, float scale, float alpha) {
        graphics.setColor(1.0F, 1.0F, 1.0F, alpha);
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        graphics.pose().scale(scale, scale, 1.0f);
        graphics.blit(CURSORS_1_6, 0, 0, u, v, width, height, CURSORS_1_6_WIDTH, CURSORS_1_6_HEIGHT);
        graphics.pose().popPose();
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void drawTextureBox(GuiGraphics graphics, int x, int y, int width, int height) {
        drawTextureBox(graphics, MENU_TILES, MENU_TILES_WIDTH, MENU_TILES_HEIGHT, 0, 256, 60, 60, x, y, width, height, 1.0f / getGuiScale(), true);
    }

    // Equivalent to IClickableMenu.drawHorizontalPartition(..., small=false)
    public static void drawHorizontalPartition(GuiGraphics graphics, int x, int y, int width, float scale) {
        int unit = Math.max(1, Math.round(16.0f * scale));
        drawHorizontalPartition(graphics, x, y, width, unit);
    }

    public static void drawHorizontalPartition(GuiGraphics graphics, int x, int y, int width, int unit) {
        int middleWidth = Math.max(0, width - unit * 2);
        drawMenuTile(graphics, x, y, unit, unit, 4);
        drawMenuTile(graphics, x + unit, y, middleWidth, unit, 6);
        drawMenuTile(graphics, x + width - unit, y, unit, unit, 7);
    }

    // Equivalent to IClickableMenu.drawHorizontalPartition(..., small=true)
    public static void drawHorizontalPartitionSmall(GuiGraphics graphics, int x, int y, int width, float scale) {
        int unit = Math.max(1, Math.round(16.0f * scale));
        int startX = x + unit / 2;
        int middleWidth = Math.max(0, width - unit);
        drawMenuTile(graphics, startX, y, middleWidth, unit, 25);
    }

    // Equivalent to IClickableMenu.drawVerticalPartition(..., small=true)
    public static void drawVerticalPartitionSmall(GuiGraphics graphics, int x, int menuY, int menuHeight, float scale) {
        int unit = Math.max(1, Math.round(16.0f * scale));
        int y = menuY + unit + unit / 2;
        int height = Math.max(0, menuHeight - unit * 2);
        drawMenuTile(graphics, x, y, unit, height, 26);
    }

    // Equivalent to IClickableMenu.drawVerticalIntersectingPartition
    public static void drawVerticalIntersectingPartition(GuiGraphics graphics, int x, int y, int menuY, int menuHeight, float scale) {
        int unit = Math.max(1, Math.round(16.0f * scale));
        drawVerticalIntersectingPartition(graphics, x, y, menuY, menuHeight, unit);
    }

    public static void drawVerticalIntersectingPartition(GuiGraphics graphics, int x, int y, int menuY, int menuHeight, int unit) {
        int middleHeight = Math.max(0, menuY + menuHeight - unit - (y + unit));
        drawMenuTile(graphics, x, y, unit, unit, 59);
        drawMenuTile(graphics, x, y + unit, unit, middleHeight, 63);
        drawMenuTile(graphics, x, menuY + menuHeight - unit, unit, unit, 62);
    }

    public static void drawTextureBoxNoShadow(GuiGraphics graphics, int x, int y, int width, int height) {
        drawTextureBox(graphics, CURSORS, CURSORS_WIDTH, CURSORS_HEIGHT, 384, 373, 18, 18, x, y, width, height, 4.0f / getGuiScale(), false);
    }

    // Equivalent to Game1.drawDialogueBox(..., drawOnlyBox:true)
    public static void drawDialogueBoxFrame(GuiGraphics graphics, int x, int y, int width, int height) {
        int unit = Math.max(1, Math.round(64.0f / getGuiScale()));
        int inset = Math.max(0, Math.round(28.0f / getGuiScale()));

        int centerX = x + inset;
        int centerY = y + inset;
        int centerW = Math.max(0, width - unit);
        int centerH = Math.max(0, height - unit);
        drawMenuRegion(graphics, centerX, centerY, centerW, centerH, 64, 128, 64, 64);

        drawMenuRegion(graphics, x, y, unit, unit, 0, 0, 64, 64);
        drawMenuRegion(graphics, x + width - unit, y, unit, unit, 192, 0, 64, 64);
        drawMenuRegion(graphics, x + width - unit, y + height - unit, unit, unit, 192, 192, 64, 64);
        drawMenuRegion(graphics, x, y + height - unit, unit, unit, 0, 192, 64, 64);

        drawMenuRegion(graphics, x + unit, y, Math.max(0, width - unit * 2), unit, 128, 0, 64, 64);
        drawMenuRegion(graphics, x + unit, y + height - unit, Math.max(0, width - unit * 2), unit, 128, 192, 64, 64);
        drawMenuRegion(graphics, x, y + unit, unit, Math.max(0, height - unit * 2), 0, 128, 64, 64);
        drawMenuRegion(graphics, x + width - unit, y + unit, unit, Math.max(0, height - unit * 2), 192, 128, 64, 64);
    }

    public static void drawTextureBox(GuiGraphics graphics, ResourceLocation texture, int texWidth, int texHeight, int srcX, int srcY, int srcW, int srcH, int x, int y, int width, int height, float scale, boolean drawShadow) {
        int cornerSize = srcW / 3;
        int scaledCorner = Math.max(1, (int)(cornerSize * scale));

        if (drawShadow) {
            graphics.setColor(0.0F, 0.0F, 0.0F, 0.4F);
            drawRegion(graphics, texture, texWidth, texHeight, x + width - scaledCorner - 8, y + 8, scaledCorner, scaledCorner, srcX + cornerSize * 2, srcY, cornerSize, cornerSize);
            drawRegion(graphics, texture, texWidth, texHeight, x - 8, y + height - scaledCorner + 8, scaledCorner, scaledCorner, srcX, srcY + cornerSize * 2, cornerSize, cornerSize);
            drawRegion(graphics, texture, texWidth, texHeight, x + width - scaledCorner - 8, y + height - scaledCorner + 8, scaledCorner, scaledCorner, srcX + cornerSize * 2, srcY + cornerSize * 2, cornerSize, cornerSize);
            drawRegion(graphics, texture, texWidth, texHeight, x + scaledCorner - 8, y + 8, Math.max(0, width - scaledCorner * 2), scaledCorner, srcX + cornerSize, srcY, cornerSize, cornerSize);
            drawRegion(graphics, texture, texWidth, texHeight, x + scaledCorner - 8, y + height - scaledCorner + 8, Math.max(0, width - scaledCorner * 2), scaledCorner, srcX + cornerSize, srcY + cornerSize * 2, cornerSize, cornerSize);
            drawRegion(graphics, texture, texWidth, texHeight, x - 8, y + scaledCorner + 8, scaledCorner, Math.max(0, height - scaledCorner * 2), srcX, srcY + cornerSize, cornerSize, cornerSize);
            drawRegion(graphics, texture, texWidth, texHeight, x + width - scaledCorner - 8, y + scaledCorner + 8, scaledCorner, Math.max(0, height - scaledCorner * 2), srcX + cornerSize * 2, srcY + cornerSize, cornerSize, cornerSize);
            drawRegion(graphics, texture, texWidth, texHeight, x + scaledCorner / 2 - 8, y + scaledCorner / 2 + 8, Math.max(0, width - scaledCorner), Math.max(0, height - scaledCorner), srcX + cornerSize, srcY + cornerSize, cornerSize, cornerSize);
            graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        }

        drawRegion(graphics, texture, texWidth, texHeight, x + scaledCorner, y + scaledCorner, Math.max(0, width - scaledCorner * 2), Math.max(0, height - scaledCorner * 2), srcX + cornerSize, srcY + cornerSize, cornerSize, cornerSize);
        drawRegion(graphics, texture, texWidth, texHeight, x, y, scaledCorner, scaledCorner, srcX, srcY, cornerSize, cornerSize);
        drawRegion(graphics, texture, texWidth, texHeight, x + width - scaledCorner, y, scaledCorner, scaledCorner, srcX + cornerSize * 2, srcY, cornerSize, cornerSize);
        drawRegion(graphics, texture, texWidth, texHeight, x, y + height - scaledCorner, scaledCorner, scaledCorner, srcX, srcY + cornerSize * 2, cornerSize, cornerSize);
        drawRegion(graphics, texture, texWidth, texHeight, x + width - scaledCorner, y + height - scaledCorner, scaledCorner, scaledCorner, srcX + cornerSize * 2, srcY + cornerSize * 2, cornerSize, cornerSize);
        drawRegion(graphics, texture, texWidth, texHeight, x + scaledCorner, y, Math.max(0, width - scaledCorner * 2), scaledCorner, srcX + cornerSize, srcY, cornerSize, cornerSize);
        drawRegion(graphics, texture, texWidth, texHeight, x + scaledCorner, y + height - scaledCorner, Math.max(0, width - scaledCorner * 2), scaledCorner, srcX + cornerSize, srcY + cornerSize * 2, cornerSize, cornerSize);
        drawRegion(graphics, texture, texWidth, texHeight, x, y + scaledCorner, scaledCorner, Math.max(0, height - scaledCorner * 2), srcX, srcY + cornerSize, cornerSize, cornerSize);
        drawRegion(graphics, texture, texWidth, texHeight, x + width - scaledCorner, y + scaledCorner, scaledCorner, Math.max(0, height - scaledCorner * 2), srcX + cornerSize * 2, srcY + cornerSize, cornerSize, cornerSize);
    }

    private static void drawRegion(GuiGraphics graphics, ResourceLocation texture, int texWidth, int texHeight, int x, int y, int width, int height, int u, int v, int srcWidth, int srcHeight) {
        if (width <= 0 || height <= 0) {
            return;
        }
        graphics.blit(texture, x, y, width, height, u, v, srcWidth, srcHeight, texWidth, texHeight);
    }

    private static float getGuiScale() {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.getWindow() == null) {
            return 1.0f;
        }
        return (float) mc.getWindow().getGuiScale();
    }

    private static void drawMenuTile(GuiGraphics graphics, int x, int y, int width, int height, int tileIndex) {
        if (width <= 0 || height <= 0) {
            return;
        }
        // Vanilla Game1.getSourceRectForStandardTileSheet(menuTexture, index) defaults to 64x64 tiles.
        int tileSize = 64;
        int columns = MENU_TILES_WIDTH / tileSize;
        int u = (tileIndex % columns) * tileSize;
        int v = (tileIndex / columns) * tileSize;
        drawRegion(graphics, MENU_TILES, MENU_TILES_WIDTH, MENU_TILES_HEIGHT, x, y, width, height, u, v, tileSize, tileSize);
    }

    public static void drawMenuTileIndex(GuiGraphics graphics, int x, int y, int width, int height, int tileIndex) {
        drawMenuTile(graphics, x, y, width, height, tileIndex);
    }

    private static void drawMenuRegion(GuiGraphics graphics, int x, int y, int width, int height, int u, int v, int srcW, int srcH) {
        drawRegion(graphics, MENU_TILES, MENU_TILES_WIDTH, MENU_TILES_HEIGHT, x, y, width, height, u, v, srcW, srcH);
    }

}
