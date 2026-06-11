package com.stardew.craft.client.gui.auction;

import com.stardew.craft.client.gui.common.CommonGuiTextures;
import com.stardew.craft.client.gui.overnight.StardewGuiUtil;
import com.stardew.craft.network.payload.OpenAuctionJoinListPayload;
import com.stardew.craft.sound.ModSounds;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;
import java.util.List;

@SuppressWarnings("null")
public class AuctionJoinListScreen extends Screen {
    private final List<OpenAuctionJoinListPayload.AuctionSummary> auctions;
    private int panelX, panelY, panelW, panelH;
    private int contentX, contentY, contentW, contentH;
    private int listX, listY, listW, listH;
    private int detailX, detailY, detailW, detailH;
    private int joinX, joinY, joinW, joinH;
    private int rowH;
    private int maxVisible;
    private int scrollOffset;
    private int selectedIndex;
    private boolean singleColumn;
    private boolean opened;

    public AuctionJoinListScreen(List<OpenAuctionJoinListPayload.AuctionSummary> auctions) {
        super(Component.translatable("stardewcraft.auction.join_list.title"));
        this.auctions = auctions;
    }

    @Override
    protected void init() {
        panelW = fitSize(width - 46, 360, 700);
        panelH = fitSize(height - 52, 260, 456);
        panelX = (width - panelW) / 2;
        panelY = (height - panelH) / 2;
        int pad = Math.max(14, Math.min(24, panelW / 24));
        contentX = panelX + pad;
        contentY = panelY + pad;
        contentW = panelW - pad * 2;
        contentH = panelH - pad * 2;
        singleColumn = contentW < 540;
        int bodyY = contentY + 50;
        int bodyH = contentH - 50;
        int gap = Math.max(12, contentW / 42);
        if (singleColumn) {
            listX = contentX + 8;
            listY = bodyY;
            listW = contentW - 16;
            listH = Math.max(104, bodyH - 70);
            detailX = listX;
            detailY = listY + listH + gap;
            detailW = listW;
            detailH = Math.max(54, contentY + contentH - detailY);
        } else {
            listX = contentX + 8;
            listY = bodyY;
            listW = Math.max(278, (contentW - gap - 16) * 58 / 100);
            listH = bodyH;
            detailX = listX + listW + gap;
            detailY = bodyY;
            detailW = contentX + contentW - 8 - detailX;
            detailH = bodyH;
        }
        rowH = 58;
        maxVisible = Math.max(1, listH / (rowH + 8));
        scrollOffset = Math.min(scrollOffset, maxScroll());
        selectedIndex = auctions.isEmpty() ? -1 : Math.min(Math.max(0, selectedIndex), auctions.size() - 1);
        if (selectedIndex >= 0) {
            ensureVisible(selectedIndex);
        }
        joinW = Math.min(168, Math.max(132, detailW - 24));
        joinH = 31;
        joinX = detailX + detailW - joinW - 12;
        joinY = detailY + detailH - joinH - 12;
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
        AuctionUi.title(g, font, Component.translatable("stardewcraft.auction.join_list.title"),
            Component.translatable("stardewcraft.auction.join_list.subtitle"),
            contentX + 8, contentY + 7, contentW - 16);
        if (auctions.isEmpty()) {
            drawEmpty(g);
            return;
        }
        drawList(g, mouseX, mouseY);
        drawDetail(g, mouseX, mouseY);
    }

    private void drawEmpty(GuiGraphics g) {
        AuctionUi.card(g, listX, listY + 8, listW, Math.min(98, listH), false, false);
        g.drawString(font, Component.translatable("stardewcraft.auction.join_list.empty"), listX + 14, listY + 30, AuctionUi.BODY, false);
        AuctionUi.drawClamped(g, font, Component.translatable("stardewcraft.auction.join_list.empty_hint"),
            listX + 14, listY + 50, listW - 28, AuctionUi.MUTED);
    }

    private void drawList(GuiGraphics g, int mouseX, int mouseY) {
        for (int i = 0; i < Math.min(maxVisible, auctions.size() - scrollOffset); i++) {
            int y = listY + i * (rowH + 8);
            int index = i + scrollOffset;
            drawRow(g, auctions.get(index), index, listX, y, listW, mouseX, mouseY);
        }
        if (maxScroll() > 0) {
            drawScrollbar(g);
        }
    }

    private void drawRow(GuiGraphics g, OpenAuctionJoinListPayload.AuctionSummary summary, int index, int x, int y, int w, int mouseX, int mouseY) {
        boolean hover = AuctionUi.inside(mouseX, mouseY, x, y, w, rowH);
        boolean selected = selectedIndex == index;
        AuctionUi.noticeSlip(g, x, y, w, rowH, selected, hover);
        CommonGuiTextures.drawQuestTimed(g, x + 17, y + 16, 1.0f);
        AuctionUi.drawClamped(g, font, summary.name(), x + 40, y + 10, w - 52, AuctionUi.INK);
        String meta = Component.translatable("stardewcraft.auction.join_list.meta",
            summary.creatorName(), formatDayTime(summary.scheduledDay(), summary.startMinute()), summary.lotCount()).getString();
        AuctionUi.drawClamped(g, font, meta, x + 40, y + 31, w - 52, selected || hover ? AuctionUi.GOLD : AuctionUi.MUTED);
    }

    private void drawDetail(GuiGraphics g, int mouseX, int mouseY) {
        AuctionUi.card(g, detailX, detailY, detailW, detailH, false, false);
        OpenAuctionJoinListPayload.AuctionSummary selected = selectedIndex >= 0 ? auctions.get(selectedIndex) : null;
        if (selected == null) {
            return;
        }
        AuctionUi.sectionLabel(g, font, Component.translatable("stardewcraft.auction.join.target"), detailX + 14, detailY + 13, detailW - 28);
        AuctionUi.drawClamped(g, font, selected.name(), detailX + 16, detailY + 40, detailW - 32, AuctionUi.INK);
        AuctionUi.drawClamped(g, font, Component.translatable("stardewcraft.auction.join_list.detail_host", selected.creatorName()),
            detailX + 16, detailY + 61, detailW - 32, AuctionUi.BODY);
        AuctionUi.drawClamped(g, font, Component.translatable("stardewcraft.auction.join_list.detail_time",
            formatDayTime(selected.scheduledDay(), selected.startMinute())), detailX + 16, detailY + 79, detailW - 32, AuctionUi.MUTED);
        AuctionUi.drawClamped(g, font, Component.translatable("stardewcraft.auction.join_list.detail_lots", selected.lotCount()),
            detailX + 16, detailY + 97, detailW - 32, AuctionUi.MUTED);
        boolean hover = AuctionUi.inside(mouseX, mouseY, joinX, joinY, joinW, joinH);
        AuctionUi.actionButton(g, font, Component.translatable("stardewcraft.auction.join_list.join"), joinX, joinY, joinW, joinH, true, hover);
    }

    private void drawScrollbar(GuiGraphics g) {
        int trackX = listX + listW - 7;
        int trackY = listY;
        int trackH = Math.max(rowH, Math.min(listH, maxVisible * (rowH + 8) - 8));
        CommonGuiTextures.drawScrollTrackBox(g, trackX, trackY, 6, trackH, 1.0f);
        float ratio = scrollOffset / (float) Math.max(1, maxScroll());
        int thumbH = Math.max(18, trackH * maxVisible / auctions.size());
        int thumbY = trackY + Math.round((trackH - thumbH) * ratio);
        CommonGuiTextures.drawScrollBarThumb(g, trackX, thumbY, 1.0f);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (int i = 0; i < Math.min(maxVisible, auctions.size() - scrollOffset); i++) {
            int y = listY + i * (rowH + 8);
            if (AuctionUi.inside(mouseX, mouseY, listX, y, listW, rowH)) {
                selectedIndex = i + scrollOffset;
                playSelect();
                return true;
            }
        }
        if (!auctions.isEmpty() && AuctionUi.inside(mouseX, mouseY, joinX, joinY, joinW, joinH)) {
            openSelected();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (auctions.isEmpty()) {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
        if (keyCode == 257 || keyCode == 335) {
            openSelected();
            return true;
        }
        if (keyCode == 265 && selectedIndex > 0) {
            selectedIndex--;
            ensureVisible(selectedIndex);
            playSelect();
            return true;
        }
        if (keyCode == 264 && selectedIndex < auctions.size() - 1) {
            selectedIndex++;
            ensureVisible(selectedIndex);
            playSelect();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!AuctionUi.inside(mouseX, mouseY, listX, listY, listW, listH)) {
            return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        }
        if (scrollY > 0 && scrollOffset > 0) {
            scrollOffset--;
            selectedIndex = Math.max(0, scrollOffset);
            playSelect();
            return true;
        }
        if (scrollY < 0 && scrollOffset < maxScroll()) {
            scrollOffset++;
            selectedIndex = Math.min(auctions.size() - 1, scrollOffset + maxVisible - 1);
            playSelect();
            return true;
        }
        return true;
    }

    private int maxScroll() {
        return Math.max(0, auctions.size() - maxVisible);
    }

    private String formatDayTime(int day, int minute) {
        return Component.translatable("stardewcraft.auction.join_list.day_time",
            seasonName(day), dayOfSeason(day), String.format(java.util.Locale.ROOT, "%02d:%02d", minute / 60, minute % 60)).getString();
    }

    private static int dayOfSeason(int absoluteDay) {
        return Math.max(0, absoluteDay - 1) % 28 + 1;
    }

    private static Component seasonName(int absoluteDay) {
        int season = (Math.max(0, absoluteDay - 1) / 28) % 4;
        return Component.translatable("stardewcraft.season." + switch (season) {
            case 1 -> "summer";
            case 2 -> "fall";
            case 3 -> "winter";
            default -> "spring";
        });
    }

    private static int fitSize(int available, int min, int max) {
        int usable = Math.max(180, available);
        return Math.max(Math.min(min, usable), Math.min(max, usable));
    }

    private void ensureVisible(int index) {
        if (index < scrollOffset) {
            scrollOffset = index;
        } else if (index >= scrollOffset + maxVisible) {
            scrollOffset = index - maxVisible + 1;
        }
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll()));
    }

    private void openSelected() {
        if (minecraft != null && selectedIndex >= 0 && selectedIndex < auctions.size()) {
            minecraft.setScreen(new AuctionConsignScreen(auctions.get(selectedIndex)));
        }
    }

    private void playSelect() {
        if (minecraft != null) {
            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(ModSounds.BUTTON_PRESS.get(), 0.72f, 0.58f));
        }
    }

    private void playOpen() {
        if (minecraft != null) {
            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(ModSounds.BOOK_READ.get(), 0.82f, 0.70f));
        }
    }
}
