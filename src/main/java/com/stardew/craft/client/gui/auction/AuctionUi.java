package com.stardew.craft.client.gui.auction;

import com.stardew.craft.client.gui.common.CommonGuiTextures;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

final class AuctionUi {
    static final int INK = 0x4B2A12;
    static final int BODY = 0x633B1B;
    static final int MUTED = 0x76532E;
    static final int GOLD = 0xB87522;
    static final int GOLD_BRIGHT = 0xE3A93F;
    static final int WOOD = 0x9A5B28;
    static final int PAPER = 0xFFF4C979;
    static final int PAPER_STRONG = 0xFFFFD891;
    static final int CARD = 0xFFFFDFA1;
    static final int CARD_STRONG = 0xFFFFE6B4;
    static final int DISABLED = 0x93755B;
    static final int ERROR = 0x9E3326;
    static final int LINE = 0xA66B26;
    static final int LINE_SOFT = 0x88B87938;
    static final int FIELD_FILL = 0xFFFFE7B1;
    static final int FIELD_FOCUS = 0xFFFFF0C9;
    static final int FIELD_HOVER = 0xFFFFEDBF;
    static final int SELECTED_BAND = 0xFFE9B84E;
    static final int SOFT_BAND = 0xFFE8BE68;

    private AuctionUi() {
    }

    static float pulse() {
        return 0.5F + 0.5F * (float) Math.sin(System.currentTimeMillis() / 310.0D);
    }

    static void parchment(GuiGraphics g, int x, int y, int w, int h) {
        g.fill(x, y, x + w, y + h, PAPER);
        g.fill(x + 4, y + 4, x + w - 4, y + h - 4, PAPER_STRONG);
        g.fill(x + 10, y + 10, x + w - 10, y + h - 10, 0x18FFFFFF);
    }

    static void ledgerPanel(GuiGraphics g, int x, int y, int w, int h) {
        parchment(g, x, y, w, h);
        g.fill(x + 8, y + 8, x + w - 8, y + h - 8, 0x28FFFFFF);
        g.fill(x + 8, y + 8, x + w - 8, y + 10, 0x44FFFFFF);
    }

    static void divider(GuiGraphics g, int x, int y, int w) {
        g.fill(x, y, x + w, y + 1, LINE);
        if (w > 44) {
            g.fill(x + 22, y + 3, x + w - 18, y + 4, 0x55B87938);
        }
    }

    static void title(GuiGraphics g, Font font, Component title, Component subtitle, int x, int y, int w) {
        drawClamped(g, font, title, x, y, w, INK);
        if (subtitle != null) {
            drawClamped(g, font, subtitle, x, y + 17, w, MUTED);
        }
        divider(g, x, y + 34, w);
    }

    static void card(GuiGraphics g, int x, int y, int w, int h, boolean selected, boolean hover) {
        CommonGuiTextures.drawEntryBox(g, x, y, w, h, 1.0f, false);
        int fill = selected ? CARD_STRONG : hover ? 0xFFFFE9B9 : CARD;
        g.fill(x + 5, y + 5, x + w - 5, y + h - 5, fill);
        g.fill(x + 7, y + 7, x + w - 7, y + 8, 0x55FFFFFF);
        if (selected || hover) {
            int line = selected ? SELECTED_BAND : SOFT_BAND;
            g.fill(x + 7, y + h - 8, x + w - 7, y + h - 5, line);
        }
    }

    static void noticeSlip(GuiGraphics g, int x, int y, int w, int h, boolean selected, boolean hover) {
        card(g, x, y, w, h, selected, hover);
        g.fill(x + 8, y + 8, x + 11, y + h - 8, selected ? GOLD_BRIGHT : hover ? GOLD : LINE_SOFT);
    }

    static void sectionLabel(GuiGraphics g, Font font, Component label, int x, int y, int w) {
        drawClamped(g, font, label, x, y, w, BODY);
        g.fill(x, y + 12, x + Math.max(30, Math.min(w, font.width(label) + 18)), y + 13, 0x77B87938);
    }

    static void field(GuiGraphics g, EditBox box, boolean focus, boolean hover, boolean invalid) {
        int x = box.getX() - 9;
        int y = box.getY() - 6;
        int w = box.getWidth() + 18;
        int h = box.getHeight() + 12;
        inputBox(g, x, y, w, h, focus, hover, invalid);
    }

    static void inputBox(GuiGraphics g, int x, int y, int w, int h, boolean focus, boolean hover, boolean invalid) {
        CommonGuiTextures.drawEntryBox(g, x, y, w, h, 1.0f, false);
        g.fill(x + 5, y + 5, x + w - 5, y + h - 5, focus ? FIELD_FOCUS : hover ? FIELD_HOVER : FIELD_FILL);
        int line;
        if (invalid) {
            line = ERROR;
        } else if (focus) {
            line = blend(GOLD, GOLD_BRIGHT, pulse());
        } else {
            line = hover ? GOLD_BRIGHT : 0xB8B87522;
        }
        g.fill(x + 5, y + h - 6, x + w - 5, y + h - 3, line);
        if (focus) {
            int focusLine = blend(0xD8902A, GOLD_BRIGHT, pulse());
            g.fill(x - 1, y - 1, x + w + 1, y, focusLine);
            g.fill(x - 1, y + h, x + w + 1, y + h + 1, focusLine);
            g.fill(x - 1, y, x, y + h, focusLine);
            g.fill(x + w, y, x + w + 1, y + h, focusLine);
        }
    }

    static void actionButton(GuiGraphics g, Font font, Component label, int x, int y, int w, int h,
                             boolean enabled, boolean hover) {
        CommonGuiTextures.drawBillboardAcceptBox(g, x, y, w, h, 1.0f);
        g.fill(x + 5, y + 5, x + w - 5, y + h - 5, enabled ? (hover ? 0xFFFFDFA1 : 0xFFF0C777) : 0xFFD5B681);
        g.fill(x + 8, y + h - 7, x + w - 8, y + h - 5, enabled ? (hover ? GOLD_BRIGHT : GOLD) : DISABLED);
        CommonGuiTextures.drawGoldCoin16(g, x + 12, y + (h - 16) / 2 + 1, 0.72f);
        drawClamped(g, font, label, x + 36, y + (h - font.lineHeight) / 2, w - 48, enabled ? INK : DISABLED);
    }

    static void plainButton(GuiGraphics g, Font font, Component label, int x, int y, int w, int h,
                            boolean enabled, boolean hover) {
        CommonGuiTextures.drawBillboardAcceptBox(g, x, y, w, h, 1.0f);
        g.fill(x + 5, y + 5, x + w - 5, y + h - 5, enabled ? (hover ? 0xFFFFDFA1 : 0xFFF0C777) : 0xFFD5B681);
        g.fill(x + 8, y + h - 7, x + w - 8, y + h - 5, enabled ? (hover ? GOLD_BRIGHT : GOLD) : DISABLED);
        drawCentered(g, font, label, x + w / 2, y + (h - font.lineHeight) / 2, w - 16, enabled ? INK : DISABLED);
    }

    static void paddleButton(GuiGraphics g, Font font, String label, int x, int y, int w, int h,
                             boolean enabled, boolean hover) {
        CommonGuiTextures.drawEntryBox(g, x, y, w, h, 1.0f, false);
        int fill = enabled ? (hover ? 0xFFFFE2A2 : 0xFFFFD58D) : 0xFFD5B681;
        g.fill(x + 4, y + 4, x + w - 4, y + h - 4, fill);
        g.fill(x + 8, y + h - 7, x + w - 8, y + h - 5, enabled && hover ? GOLD_BRIGHT : GOLD);
        CommonGuiTextures.drawGoldCoin16(g, x + 8, y + (h - 14) / 2, 0.58f);
        drawClamped(g, font, label, x + 28, y + (h - font.lineHeight) / 2, w - 34, enabled ? INK : DISABLED);
    }

    static void slot(GuiGraphics g, Font font, ItemStack stack, int x, int y, int size, boolean selected, boolean hover) {
        float scale = Math.max(0.8f, size / 18.0f);
        CommonGuiTextures.drawItemSlot18(g, x, y, scale);
        if (hover || selected) {
            int line = selected ? GOLD_BRIGHT : GOLD;
            g.fill(x + 1, y + size - 3, x + size - 1, y + size - 1, line);
            g.fill(x + 1, y + 1, x + 3, y + size - 1, line);
            g.fill(x + size - 3, y + 1, x + size - 1, y + size - 1, line);
            g.fill(x + 1, y + 1, x + size - 1, y + 3, line);
        }
        if (!stack.isEmpty()) {
            int itemX = x + (size - 16) / 2;
            int itemY = y + (size - 16) / 2;
            g.renderItem(stack, itemX, itemY);
            g.renderItemDecorations(font, stack, itemX, itemY);
        }
    }

    static void drawClamped(GuiGraphics g, Font font, Component text, int x, int y, int maxWidth, int color) {
        drawClamped(g, font, text.getString(), x, y, maxWidth, color);
    }

    static void drawClamped(GuiGraphics g, Font font, String text, int x, int y, int maxWidth, int color) {
        g.drawString(font, fit(font, text, maxWidth), x, y, color, false);
    }

    static void drawCentered(GuiGraphics g, Font font, Component text, int cx, int y, int maxWidth, int color) {
        String fitted = fit(font, text.getString(), maxWidth);
        g.drawString(font, fitted, cx - font.width(fitted) / 2, y, color, false);
    }

    static String fit(Font font, String text, int maxWidth) {
        if (font.width(text) <= maxWidth) {
            return text;
        }
        return font.plainSubstrByWidth(text, Math.max(8, maxWidth - font.width("..."))) + "...";
    }

    static int blend(int a, int b, float t) {
        t = Math.max(0.0F, Math.min(1.0F, t));
        int ar = (a >> 16) & 0xFF;
        int ag = (a >> 8) & 0xFF;
        int ab = a & 0xFF;
        int br = (b >> 16) & 0xFF;
        int bg = (b >> 8) & 0xFF;
        int bb = b & 0xFF;
        int r = Math.round(ar + (br - ar) * t);
        int g = Math.round(ag + (bg - ag) * t);
        int bl = Math.round(ab + (bb - ab) * t);
        return (r << 16) | (g << 8) | bl;
    }

    static boolean inside(double x, double y, int rx, int ry, int rw, int rh) {
        return x >= rx && x < rx + rw && y >= ry && y < ry + rh;
    }

    // ── Layered Stardew surfaces: compose from real nine-slice sprites, never flat fills ──

    /** Framed parchment sub-panel — the container for one decision band. */
    static void band(GuiGraphics g, int x, int y, int w, int h) {
        CommonGuiTextures.drawTextureBox(g, x, y, w, h, 1.0f, false);
    }

    /** Fine inset frame for plates, fields, and the lot pedestal. */
    static void inset(GuiGraphics g, int x, int y, int w, int h) {
        CommonGuiTextures.drawEntryBox(g, x, y, w, h, 1.0f, false);
    }

    /** Wooden hanging title ribbon centered on cx, top edge at y. Returns its drawn height. */
    static int ribbon(GuiGraphics g, Font font, Component title, int cx, int y) {
        String text = fit(font, title.getString(), 240);
        int tw = font.width(text);
        int mid = Math.max(20, tw + 12);
        int textX = cx - mid / 2;
        CommonGuiTextures.drawScrollBanner(g, textX, y, mid, 1.0f);
        g.drawString(font, text, cx - tw / 2, y + 5, INK, false);
        return 18;
    }

    /** A coin-stamped price plate: muted label on top, gold value below. {@code hot} brightens the leading bid. */
    static void pricePlate(GuiGraphics g, Font font, Component label, int amount, int x, int y, int w, int h, boolean hot) {
        inset(g, x, y, w, h);
        drawClamped(g, font, label, x + 11, y + 8, w - 18, MUTED);
        CommonGuiTextures.drawGoldCoin16(g, x + 11, y + h - 20, hot ? 0.8f : 0.66f);
        drawClamped(g, font, amount + "g", x + 29, y + h - 17, w - 38, hot ? GOLD_BRIGHT : GOLD);
    }
}
