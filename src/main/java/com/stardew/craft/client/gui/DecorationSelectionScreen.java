package com.stardew.craft.client.gui;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.network.payload.ApplyDecorationStylePayload;
import com.stardew.craft.network.payload.OpenDecorationScreenPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("null")
public class DecorationSelectionScreen extends Screen {
    private static final ResourceLocation LOCKED_ICON = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/locked.png");
    private static final ResourceLocation MOUSE_CURSORS_2 = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/mouse_cursors2.png");
    private static final int BG = 0xE21A1A1A;
    private static final int PANEL = 0xEE2A2A2A;
    private static final int TEXT = 0xFFF3F3F3;
    private static final int FRAME_SRC_Y = 31;
    private static final int WALLPAPER_FRAME_SRC_X = 39;
    private static final int FLOORING_FRAME_SRC_X = 55;
    private static final int FRAME_SRC_SIZE = 16;
    private static final int CURSORS2_W = 256;
    private static final int CURSORS2_H = 320;

    private final OpenDecorationScreenPayload payload;
    private final List<OpenDecorationScreenPayload.DecorationOption> options;
    private int selectedIndex = 0;
    private int scrollRow = 0;

    private int panelX;
    private int panelY;
    private boolean isWallpaper;

    public DecorationSelectionScreen(OpenDecorationScreenPayload payload) {
        super(Component.translatable("stardewcraft.deco.screen.title"));
        this.payload = payload;
        this.options = new ArrayList<>(payload.options());
        this.isWallpaper = "WALLPAPER".equals(payload.decorationType());
    }

    @Override
    protected void init() {
        super.init();
        panelX = (width - 360) / 2;
        panelY = (height - 220) / 2;

        int initial = 0;
        for (int i = 0; i < options.size(); i++) {
            if (options.get(i).styleId().equals(payload.currentStyleId())) {
                initial = i;
                break;
            }
        }
        selectedIndex = initial;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        graphics.fill(panelX, panelY, panelX + 360, panelY + 220, PANEL);
        graphics.fill(panelX + 1, panelY + 1, panelX + 359, panelY + 24, BG);
        graphics.drawString(font, title, panelX + 8, panelY + 8, TEXT, false);

        drawGrid(graphics, mouseX, mouseY);
        drawPreview(graphics);
        drawButtons(graphics, mouseX, mouseY);

    }

    private void drawGrid(GuiGraphics graphics, int mouseX, int mouseY) {
        int gridX = panelX + 10;
        int gridY = panelY + 34;
        int cols = 7;
        int rows = 5;
        int cell = 24;

        graphics.fill(gridX - 2, gridY - 2, gridX + cols * cell + 2, gridY + rows * cell + 2, 0xCC111111);

        for (int i = 0; i < cols * rows; i++) {
            int styleIndex = scrollRow * cols + i;
            int col = i % cols;
            int row = i / cols;
            int x = gridX + col * cell;
            int y = gridY + row * cell;

            graphics.fill(x, y, x + 22, y + 22, 0xAA2E2E2E);
            if (styleIndex >= options.size()) {
                continue;
            }

            OpenDecorationScreenPayload.DecorationOption option = options.get(styleIndex);
            drawDecorationIcon(graphics, option, x + 3, y + 3, 16, 16);

            if (!option.unlocked()) {
                graphics.fill(x + 1, y + 1, x + 21, y + 21, 0xAA000000);
                graphics.blit(LOCKED_ICON, x + 3, y + 3, 0, 0, 16, 16, 16, 16);
            }

            if (styleIndex == selectedIndex) {
                graphics.fill(x, y, x + 22, y + 1, 0xFFE6C35B);
                graphics.fill(x, y + 21, x + 22, y + 22, 0xFFE6C35B);
                graphics.fill(x, y, x + 1, y + 22, 0xFFE6C35B);
                graphics.fill(x + 21, y, x + 22, y + 22, 0xFFE6C35B);
            }

            if (mouseX >= x && mouseX < x + 22 && mouseY >= y && mouseY < y + 22) {
                graphics.renderTooltip(font, Component.literal(option.styleId()), mouseX, mouseY);
            }
        }
    }

    private void drawPreview(GuiGraphics graphics) {
        int previewX = panelX + 190;
        int previewY = panelY + 40;
        int previewW = 154;
        int previewH = 118;

        graphics.fill(previewX, previewY, previewX + previewW, previewY + previewH, 0xCC151515);
        if (selectedIndex < 0 || selectedIndex >= options.size()) {
            return;
        }

        OpenDecorationScreenPayload.DecorationOption option = options.get(selectedIndex);
        ResourceLocation tex = ResourceLocation.parse(option.texture());
        graphics.drawString(font, Component.literal("ID: " + option.styleId()), previewX + 6, previewY + 6, TEXT, false);

        int areaX = previewX + 8;
        int areaY = previewY + 20;
        int areaW = 128;
        int areaH = 72;
        areaX = previewX + (previewW - areaW) / 2;
        graphics.fill(areaX, areaY, areaX + areaW, areaY + areaH, 0xFF101010);

        if (isWallpaper) {
            int idx = parseStyleIndex(option.styleId());
            int baseX = (idx % 16) * 16;
            int srcY = (idx / 16) * 48 + 8;
            int sampleW = 16;
            int sampleH = 28;
            int sampleY = areaY + (areaH - sampleH) / 2;
            for (int px = 0; px < areaW; px += sampleW) {
                graphics.blit(tex, areaX + px, sampleY, sampleW, sampleH, baseX, srcY, 16, 28, option.texWidth(), option.texHeight());
            }
        } else {
            int idx = parseStyleIndex(option.styleId());
            int bx = (idx % 8) * 32;
            int by = option.styleId().startsWith("MoreFloors:") ? (idx / 8) * 32 : 336 + (idx / 8) * 32;
            int tile = 16;
            for (int py = 0; py < areaH; py += tile) {
                int pz = (py / tile) % 2;
                for (int px = 0; px < areaW; px += tile) {
                    int px2 = (px / tile) % 2;
                    int srcX = bx + px2 * 16;
                    int srcY = by + pz * 16;
                    graphics.blit(tex, areaX + px, areaY + py, tile, tile, srcX, srcY, 16, 16, option.texWidth(), option.texHeight());
                }
            }
        }

        if (!option.unlocked()) {
            graphics.fill(areaX, areaY, areaX + areaW, areaY + areaH, 0x99000000);
            graphics.blit(LOCKED_ICON, areaX + areaW - 18, areaY + 2, 0, 0, 16, 16, 16, 16);
        }

        if (!option.unlocked()) {
            List<net.minecraft.util.FormattedCharSequence> lines = font.split(Component.translatable(option.unlockHintKey()), previewW - 12);
            int lineY = previewY + 96;
            int maxLines = 2;
            for (int i = 0; i < lines.size() && i < maxLines; i++) {
                graphics.drawString(font, lines.get(i), previewX + 6, lineY + i * 10, 0xFFF38D8D);
            }
        }
    }

    private void drawButtons(GuiGraphics graphics, int mouseX, int mouseY) {
        drawButton(graphics, panelX + 190, panelY + 170, 72, 22, Component.translatable("stardewcraft.deco.screen.ok"), mouseX, mouseY);
        drawButton(graphics, panelX + 272, panelY + 170, 72, 22, Component.translatable("stardewcraft.deco.screen.cancel"), mouseX, mouseY);
    }

    private void drawButton(GuiGraphics graphics, int x, int y, int w, int h, Component text, int mouseX, int mouseY) {
        boolean hover = mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h;
        graphics.fill(x, y, x + w, y + h, hover ? 0xFF71634A : 0xFF4D4333);
        graphics.drawCenteredString(font, text, x + w / 2, y + 7, TEXT);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int cols = 7;
        int totalRows = Math.max(1, (options.size() + cols - 1) / cols);
        int visibleRows = 5;
        int maxScroll = Math.max(0, totalRows - visibleRows);
        if (scrollY > 0) {
            scrollRow = Math.max(0, scrollRow - 1);
        } else if (scrollY < 0) {
            scrollRow = Math.min(maxScroll, scrollRow + 1);
        }
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int gridX = panelX + 10;
        int gridY = panelY + 34;
        int cols = 7;
        int rows = 5;
        int cell = 24;

        if (mouseX >= gridX && mouseX < gridX + cols * cell && mouseY >= gridY && mouseY < gridY + rows * cell) {
            int col = (int) ((mouseX - gridX) / cell);
            int row = (int) ((mouseY - gridY) / cell);
            int idx = scrollRow * cols + row * cols + col;
            if (idx >= 0 && idx < options.size()) {
                selectedIndex = idx;
                return true;
            }
        }

        if (inside(mouseX, mouseY, panelX + 190, panelY + 170, 72, 22)) {
            onConfirm();
            return true;
        }
        if (inside(mouseX, mouseY, panelX + 272, panelY + 170, 72, 22)) {
            onClose();
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean inside(double mouseX, double mouseY, int x, int y, int w, int h) {
        return mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h;
    }

    private void onConfirm() {
        if (selectedIndex < 0 || selectedIndex >= options.size()) {
            return;
        }
        OpenDecorationScreenPayload.DecorationOption option = options.get(selectedIndex);
        if (!option.unlocked()) {
            if (minecraft != null && minecraft.player != null) {
                minecraft.player.displayClientMessage(Component.literal("Style is locked. Use /stardew deco ... unlock first."), true);
            }
            return;
        }
        PacketDistributor.sendToServer(new ApplyDecorationStylePayload(payload.decorationType(), payload.targetPos(), option.styleId()));
        onClose();
    }

    private int parseStyleIndex(String styleId) {
        int split = styleId.indexOf(':');
        try {
            if (split >= 0 && split + 1 < styleId.length()) {
                return Integer.parseInt(styleId.substring(split + 1));
            }
            return Integer.parseInt(styleId);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private void drawDecorationIcon(GuiGraphics graphics,
                                    OpenDecorationScreenPayload.DecorationOption option,
                                    int x,
                                    int y,
                                    int w,
                                    int h) {
        int frameSrcX = isWallpaper ? WALLPAPER_FRAME_SRC_X : FLOORING_FRAME_SRC_X;
        graphics.blit(MOUSE_CURSORS_2, x, y, w, h,
            frameSrcX, FRAME_SRC_Y, FRAME_SRC_SIZE, FRAME_SRC_SIZE,
            CURSORS2_W, CURSORS2_H);

        ResourceLocation tex = ResourceLocation.parse(option.texture());
        int srcW = option.sourceWidth();
        int srcH = option.sourceHeight();
        int innerW;
        int innerH;
        int innerX;
        int innerY;

        if (isWallpaper) {
            innerW = 8;
            innerH = 14;
            innerX = x + (w - innerW) / 2;
            innerY = y + 1;
        } else {
            innerW = 14;
            innerH = 13;
            innerX = x + (w - innerW) / 2;
            innerY = y + 1;
        }

        graphics.blit(tex, innerX, innerY, innerW, innerH,
            option.sourceX(), option.sourceY(), srcW, srcH,
            option.texWidth(), option.texHeight());
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
