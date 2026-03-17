package com.stardew.craft.client.gui.overnight;

import com.stardew.craft.StardewCraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StardewGuiUtil {

    public static final ResourceLocation CURSORS = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/cursors.png");
    public static final int CURSORS_WIDTH = 704;
    public static final int CURSORS_HEIGHT = 2256;

    public static void drawFromCursors(GuiGraphics graphics, int x, int y, int u, int v, int width, int height, float scale) {
        drawFromCursors(graphics, x, y, u, v, width, height, scale, 1.0f);
    }
    
    public static void drawFromCursors(GuiGraphics graphics, int x, int y, int u, int v, int width, int height, float scale, float alpha) {
        int scaledWidth = (int) (width * scale);
        int scaledHeight = (int) (height * scale);
        
        // We can use pose stack scaling and exact blit
        if (alpha < 1.0f) {
            graphics.setColor(1.0F, 1.0F, 1.0F, alpha);
        }
        
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        graphics.pose().scale(scale, scale, 1.0f);
        
        graphics.blit(CURSORS, 0, 0, u, v, width, height, CURSORS_WIDTH, CURSORS_HEIGHT);
        
        graphics.pose().popPose();
        
        if (alpha < 1.0f) {
            graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    public static void drawFromCursors(GuiGraphics graphics, int x, int y, int width, int height, int u, int v, int texWidth, int texHeight) {
        graphics.blit(CURSORS, x, y, width, height, u, v, texWidth, texHeight, CURSORS_WIDTH, CURSORS_HEIGHT);
    }

    public static void drawTextureBox(GuiGraphics graphics, int x, int y, int width, int height) {
        float scale = 4f;
        int cornerSize = 4;
        int scaledCorner = (int)(cornerSize * scale);
        int cw = 18;
        int ch = 18;
        // source coords: 384, 373, 18, 18
        int rU = 384;
        int rV = 373;
        
        // This simulates IClickableMenu.drawTextureBox
        
        // Top-left
        drawFromCursors(graphics, x, y, rU, rV, cornerSize, cornerSize, scale);
        // Top-right
        drawFromCursors(graphics, x + width - scaledCorner, y, rU + cw - cornerSize, rV, cornerSize, cornerSize, scale);
        // Bottom-left
        drawFromCursors(graphics, x, y + height - scaledCorner, rU, rV + ch - cornerSize, cornerSize, cornerSize, scale);
        // Bottom-right
        drawFromCursors(graphics, x + width - scaledCorner, y + height - scaledCorner, rU + cw - cornerSize, rV + ch - cornerSize, cornerSize, cornerSize, scale);
        
        // Top
        drawFromCursors2(graphics, x + scaledCorner, y, width - scaledCorner * 2, scaledCorner, rU + cornerSize, rV, cw - cornerSize * 2, cornerSize);
        // Bottom
        drawFromCursors2(graphics, x + scaledCorner, y + height - scaledCorner, width - scaledCorner * 2, scaledCorner, rU + cornerSize, rV + ch - cornerSize, cw - cornerSize * 2, cornerSize);
        // Left
        drawFromCursors2(graphics, x, y + scaledCorner, scaledCorner, height - scaledCorner * 2, rU, rV + cornerSize, cornerSize, ch - cornerSize * 2);
        // Right
        drawFromCursors2(graphics, x + width - scaledCorner, y + scaledCorner, scaledCorner, height - scaledCorner * 2, rU + cw - cornerSize, rV + cornerSize, cornerSize, ch - cornerSize * 2);
        // Center
        drawFromCursors2(graphics, x + scaledCorner, y + scaledCorner, width - scaledCorner * 2, height - scaledCorner * 2, rU + cornerSize, rV + cornerSize, cw - cornerSize * 2, ch - cornerSize * 2);
    }
    
    private static void drawFromCursors2(GuiGraphics graphics, int x, int y, int width, int height, int u, int v, int texW, int texH) {
        graphics.blit(CURSORS, x, y, width, height, u, v, texW, texH, CURSORS_WIDTH, CURSORS_HEIGHT);
    }
}
