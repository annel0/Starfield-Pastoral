package com.stardew.craft.client.gui.auction;

import com.stardew.craft.client.gui.common.CommonGuiTextures;
import com.stardew.craft.client.gui.overnight.StardewGuiUtil;
import com.stardew.craft.item.ModItems;
import com.stardew.craft.network.payload.AuctionEntryChoicePayload;
import com.stardew.craft.sound.ModSounds;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nonnull;

@SuppressWarnings("null")
public class AuctionEntryChoiceScreen extends Screen {
    private int panelX, panelY, panelW, panelH;
    private int contentX, contentY, contentW, contentH;
    private int stageX, stageY, stageW, stageH;
    private int auctionX, auctionY, choiceW, choiceH;
    private int houseX, houseY;
    private boolean selectedAuction = true;
    private boolean opened;

    public AuctionEntryChoiceScreen() {
        super(Component.translatable("stardewcraft.auction.entry.title"));
    }

    @Override
    protected void init() {
        panelW = fitSize(width - 48, 340, 640);
        panelH = fitSize(height - 72, 248, 360);
        panelX = (width - panelW) / 2;
        panelY = (height - panelH) / 2;
        int pad = Math.max(14, Math.min(24, panelW / 22));
        contentX = panelX + pad;
        contentY = panelY + pad;
        contentW = panelW - pad * 2;
        contentH = panelH - pad * 2;

        int gap = Math.max(12, contentW / 34);
        boolean narrow = contentW < 500;
        int bodyY = contentY + 58;
        int bodyH = contentH - 66;
        if (narrow) {
            stageX = contentX + 8;
            stageY = bodyY;
            stageW = contentW - 16;
            stageH = Math.max(54, bodyH / 3);
            choiceW = stageW;
            choiceH = Math.max(58, (bodyH - stageH - gap * 2) / 2);
            auctionX = stageX;
            auctionY = stageY + stageH + gap;
            houseX = stageX;
            houseY = auctionY + choiceH + gap;
        } else {
            stageX = contentX + 8;
            stageY = bodyY;
            stageW = Math.min(214, Math.max(176, (contentW - gap - 16) * 38 / 100));
            stageH = bodyH;
            choiceW = contentW - 16 - stageW - gap;
            choiceH = Math.max(72, (bodyH - gap) / 2);
            auctionX = stageX + stageW + gap;
            auctionY = bodyY;
            houseX = auctionX;
            houseY = auctionY + choiceH + gap;
        }
        if (!opened) {
            opened = true;
            playOpen();
        }
    }

    @Override
    public void render(@Nonnull GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        renderTransparentBackground(g);
        StardewGuiUtil.drawDialogueBoxFrame(g, panelX, panelY, panelW, panelH);
        AuctionUi.ledgerPanel(g, contentX, contentY, contentW, contentH);
        AuctionUi.title(g, font, Component.translatable("stardewcraft.auction.entry.title"),
            Component.translatable("stardewcraft.auction.entry.question"), contentX + 8, contentY + 7, contentW - 16);
        drawStage(g);
        drawChoice(g, mouseX, mouseY, auctionX, auctionY, true);
        drawChoice(g, mouseX, mouseY, houseX, houseY, false);
    }

    private void drawStage(GuiGraphics g) {
        AuctionUi.card(g, stageX, stageY, stageW, stageH, false, false);
        int iconBox = Math.min(54, Math.max(38, stageH - 34));
        int iconX = stageX + 16;
        int iconY = stageY + 16;
        AuctionUi.slot(g, font, new ItemStack(ModItems.AUCTION_PADDLE.get()), iconX, iconY, iconBox, true, false);
        int textX = iconX + iconBox + 14;
        int textW = stageW - (textX - stageX) - 16;
        if (textW < 80) {
            textX = stageX + 15;
            textW = stageW - 30;
            iconX = stageX + (stageW - iconBox) / 2;
            iconY = stageY + 16;
            AuctionUi.slot(g, font, new ItemStack(ModItems.AUCTION_PADDLE.get()), iconX, iconY, iconBox, true, false);
            AuctionUi.drawClamped(g, font, Component.translatable("stardewcraft.auction.entry.stage_title"),
                textX, iconY + iconBox + 10, textW, AuctionUi.INK);
            AuctionUi.drawClamped(g, font, Component.translatable("stardewcraft.auction.entry.stage_hint"),
                textX, iconY + iconBox + 28, textW, AuctionUi.MUTED);
            return;
        }
        AuctionUi.drawClamped(g, font, Component.translatable("stardewcraft.auction.entry.stage_title"),
            textX, stageY + 22, textW, AuctionUi.INK);
        drawWrapped(g, Component.translatable("stardewcraft.auction.entry.stage_hint").getString(),
            textX, stageY + 42, textW, Math.max(20, stageH - 52), AuctionUi.MUTED);
    }

    private void drawChoice(GuiGraphics g, int mouseX, int mouseY, int x, int y, boolean auction) {
        boolean hover = AuctionUi.inside(mouseX, mouseY, x, y, choiceW, choiceH);
        AuctionUi.card(g, x, y, choiceW, choiceH, selectedAuction == auction, hover);
        if (selectedAuction == auction) {
            g.fill(x + 9, y + 9, x + 13, y + choiceH - 9, AuctionUi.SELECTED_BAND);
        }
        ItemStack icon = auction ? new ItemStack(ModItems.AUCTION_PADDLE.get()) : new ItemStack(Items.OAK_DOOR);
        int iconX = x + 20;
        int iconY = y + choiceH / 2 - 13;
        CommonGuiTextures.drawItem(g, icon, iconX, iconY, 1.35f);
        Component title = Component.translatable(auction ? "stardewcraft.auction.entry.auction" : "stardewcraft.auction.entry.house");
        int textX = x + 64;
        int titleY = y + Math.max(12, (choiceH - 38) / 2);
        int textW = choiceW - 82;
        AuctionUi.drawClamped(g, font, title, textX, titleY, textW, auction ? AuctionUi.INK : AuctionUi.BODY);
        Component hint = Component.translatable(auction ? "stardewcraft.auction.entry.auction_hint" : "stardewcraft.auction.entry.house_hint");
        drawWrapped(g, hint.getString(), textX, titleY + 17, textW, choiceH - (titleY - y) - 26, hover ? AuctionUi.GOLD : AuctionUi.MUTED);
    }

    private void drawWrapped(GuiGraphics g, String text, int x, int y, int w, int h, int color) {
        int lineY = y;
        for (net.minecraft.util.FormattedCharSequence line : font.split(Component.literal(text), w)) {
            if (lineY + font.lineHeight > y + h) {
                break;
            }
            g.drawString(font, line, x, lineY, color, false);
            lineY += font.lineHeight + 2;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (AuctionUi.inside(mouseX, mouseY, auctionX, auctionY, choiceW, choiceH)) {
            selectedAuction = true;
            choose(true);
            return true;
        }
        if (AuctionUi.inside(mouseX, mouseY, houseX, houseY, choiceW, choiceH)) {
            selectedAuction = false;
            choose(false);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 257 || keyCode == 335) {
            choose(selectedAuction);
            return true;
        }
        if (keyCode == 263 || keyCode == 262) {
            selectedAuction = !selectedAuction;
            if (minecraft != null) {
                minecraft.getSoundManager().play(SimpleSoundInstance.forUI(ModSounds.BUTTON_PRESS.get(), 0.74f, 0.56f));
            }
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void choose(boolean auction) {
        if (minecraft != null) {
            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(ModSounds.BIG_SELECT.get(), auction ? 1.0f : 0.82f));
        }
        PacketDistributor.sendToServer(new AuctionEntryChoicePayload(auction));
        onClose();
    }

    private void playOpen() {
        if (minecraft != null) {
            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(ModSounds.BOOK_READ.get(), 0.82f, 0.68f));
        }
    }

    private static int fitSize(int available, int min, int max) {
        int usable = Math.max(160, available);
        return Math.max(Math.min(min, usable), Math.min(max, usable));
    }
}
