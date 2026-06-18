package com.stardew.craft.client.gui.auction;

import com.stardew.craft.client.gui.overnight.StardewGuiUtil;
import com.stardew.craft.sound.ModSounds;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.function.IntConsumer;

/**
 * Standalone "choose a lot" popup: shows the player's bag in a roomy grid, click one to select it and
 * return to the create/consign screen. Click-to-pick (not drag) so the caller's typed fields survive the round trip.
 */
@SuppressWarnings("null")
public class AuctionItemPickerScreen extends Screen {
    // Fixed design canvas, scaled uniformly to fit the screen so the layout is identical at every GUI scale.
    private static final int DESIGN_W = 620;
    private static final int DESIGN_H = 460;
    private final Screen parent;
    private final IntConsumer onPick;
    private int panelX, panelY, panelW, panelH;
    private int contentX, contentY, contentW, contentH;
    private int gridX, gridY, slotSize, slotGap;
    private int cancelX, cancelY, cancelW, cancelH;
    private int hoveredSlot = -1;
    private boolean opened;
    private float fitScale = 1.0f;
    private int fitOriginX, fitOriginY;

    public AuctionItemPickerScreen(Screen parent, IntConsumer onPick) {
        super(Component.translatable("stardewcraft.auction.picker.title"));
        this.parent = parent;
        this.onPick = onPick;
    }

    private static int slotIndex(int row, int col) {
        return row == 3 ? col : 9 + row * 9 + col;
    }

    @Override
    protected void init() {
        slotSize = 32;
        slotGap = 5;
        int gridW = 9 * slotSize + 8 * slotGap;

        fitScale = Math.min(1.5f, 0.94f * Math.min(width / (float) DESIGN_W, height / (float) DESIGN_H));
        fitOriginX = Math.round((width - DESIGN_W * fitScale) / 2f);
        fitOriginY = Math.round((height - DESIGN_H * fitScale) / 2f);
        panelW = DESIGN_W;
        panelH = DESIGN_H;
        panelX = 0;
        panelY = 0;
        int pad = Math.max(30, Math.min(48, panelW / 14));
        contentX = panelX + pad;
        contentY = panelY + pad;
        contentW = panelW - pad * 2;
        contentH = panelH - pad * 2;

        gridX = contentX + (contentW - gridW) / 2;
        gridY = contentY + 50;

        cancelW = Math.min(150, contentW / 2);
        cancelH = 28;
        cancelX = contentX + (contentW - cancelW) / 2;
        cancelY = contentY + contentH - cancelH - 4;

        if (!opened) {
            opened = true;
            if (minecraft != null) {
                minecraft.getSoundManager().play(SimpleSoundInstance.forUI(ModSounds.BOOK_READ.get(), 0.82f, 0.70f));
            }
        }
    }

    @Override
    public void render(@Nonnull GuiGraphics g, int rawMouseX, int rawMouseY, float partialTick) {
        renderTransparentBackground(g);
        int mouseX = lmx(rawMouseX);
        int mouseY = lmy(rawMouseY);
        g.pose().pushPose();
        g.pose().translate(fitOriginX, fitOriginY, 0);
        g.pose().scale(fitScale, fitScale, 1f);
        StardewGuiUtil.drawDialogueBoxFrame(g, panelX, panelY, panelW, panelH);
        AuctionUi.ribbon(g, font, Component.translatable("stardewcraft.auction.picker.title"), contentX + contentW / 2, contentY);
        AuctionUi.drawCentered(g, font, Component.translatable("stardewcraft.auction.picker.hint"),
            contentX + contentW / 2, contentY + 22, contentW - 12, AuctionUi.MUTED);

        AuctionUi.band(g, gridX - 12, gridY - 12, 9 * slotSize + 8 * slotGap + 24, 4 * slotSize + 3 * slotGap + 32);

        hoveredSlot = -1;
        ItemStack hoveredStack = ItemStack.EMPTY;
        int hx = 0;
        int hy = 0;
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 9; col++) {
                int slot = slotIndex(row, col);
                int x = gridX + col * (slotSize + slotGap);
                int y = gridY + row * (slotSize + slotGap) + (row == 3 ? 8 : 0);
                ItemStack stack = minecraft.player == null ? ItemStack.EMPTY : minecraft.player.getInventory().getItem(slot);
                boolean hover = AuctionUi.inside(mouseX, mouseY, x, y, slotSize, slotSize);
                AuctionUi.slot(g, font, stack, x, y, slotSize, false, hover);
                if (hover && !stack.isEmpty()) {
                    hoveredSlot = slot;
                    hoveredStack = stack;
                    hx = x;
                    hy = y;
                }
            }
        }

        boolean cancelHover = AuctionUi.inside(mouseX, mouseY, cancelX, cancelY, cancelW, cancelH);
        AuctionUi.plainButton(g, font, Component.translatable("stardewcraft.auction.picker.cancel"),
            cancelX, cancelY, cancelW, cancelH, true, cancelHover);

        if (!hoveredStack.isEmpty()) {
            g.renderTooltip(font, hoveredStack, hx, hy);
        }
        g.pose().popPose();
    }

    private int lmx(double mouseX) { return (int) Math.round((mouseX - fitOriginX) / fitScale); }
    private int lmy(double mouseY) { return (int) Math.round((mouseY - fitOriginY) / fitScale); }
    private double ldx(double mouseX) { return (mouseX - fitOriginX) / fitScale; }
    private double ldy(double mouseY) { return (mouseY - fitOriginY) / fitScale; }

    @Override
    public boolean mouseClicked(double rawX, double rawY, int button) {
        double mouseX = ldx(rawX);
        double mouseY = ldy(rawY);
        if (AuctionUi.inside(mouseX, mouseY, cancelX, cancelY, cancelW, cancelH)) {
            onClose();
            return true;
        }
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 9; col++) {
                int slot = slotIndex(row, col);
                int x = gridX + col * (slotSize + slotGap);
                int y = gridY + row * (slotSize + slotGap) + (row == 3 ? 8 : 0);
                if (AuctionUi.inside(mouseX, mouseY, x, y, slotSize, slotSize)) {
                    ItemStack stack = minecraft.player == null ? ItemStack.EMPTY : minecraft.player.getInventory().getItem(slot);
                    if (stack.isEmpty()) {
                        return true;
                    }
                    onPick.accept(slot);
                    if (minecraft != null) {
                        minecraft.getSoundManager().play(SimpleSoundInstance.forUI(ModSounds.BIG_SELECT.get(), 0.9f, 0.95f));
                        minecraft.setScreen(parent);
                    }
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void onClose() {
        if (minecraft != null) {
            minecraft.setScreen(parent);
        }
    }
}
