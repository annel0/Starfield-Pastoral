package com.stardew.craft.client.gui.quest;

import com.stardew.craft.client.gui.common.StardewRenderMapping;
import com.stardew.craft.client.gui.overnight.StardewGuiUtil;
import com.stardew.craft.client.hud.QuestIconHud;
import com.stardew.craft.quest.StardewQuest;
import com.stardew.craft.quest.network.ClientQuestData;
import com.stardew.craft.quest.network.CancelQuestPayload;
import com.stardew.craft.quest.network.ClaimRewardPayload;
import com.stardew.craft.quest.network.MarkQuestViewedPayload;
import com.stardew.craft.sound.ModSounds;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * SDV QuestLog.cs 的 1:1 复刻。
 *
 * 核心映射规则（与项目 StardewRenderMapping 一致）:
 *   SDV 屏幕像素 → MC GUI 坐标: mapping.ui(N) = round(N / guiScale)
 *   SDV 4× atlas 缩放 → MC:      s4 = mapping.s4() = 4.0f / guiScale
 *
 * s4 仅用于 drawFromCursors() 的缩放参数（将 atlas 源像素放大到 GUI 空间）。
 * 所有位置、尺寸偏移量统一使用 mapping.ui()。
 */
@SuppressWarnings("null")
public class QuestLogScreen extends Screen {

    private static final int SDV_WIDTH = 832;
    private static final int SDV_HEIGHT = 576;
    private static final int QUESTS_PER_PAGE = 6;

    // ─── Cursors UV（与 SDV 完全一致） ───
    private static final int BOX_U = 384, BOX_V = 373, BOX_W = 18, BOX_H = 18;
    private static final int ENTRY_U = 384, ENTRY_V = 396, ENTRY_W = 15, ENTRY_H = 15;
    private static final int CLOSE_U = 337, CLOSE_V = 494, CLOSE_W = 12, CLOSE_H = 12;
    private static final int BACK_U = 352, BACK_V = 495, BACK_W = 12, BACK_H = 11;
    private static final int FWD_U = 365, FWD_V = 495, FWD_W = 12, FWD_H = 11;
    private static final int CANCEL_U = 322, CANCEL_V = 498, CANCEL_W = 12, CANCEL_H = 12;
    private static final int REWARD_U = 293, REWARD_V = 360, REWARD_W = 24, REWARD_H = 24;
    private static final int COIN_U = 280, COIN_V = 410, COIN_W = 16, COIN_H = 16;
    private static final int NEW_U = 317, NEW_V = 410, NEW_W = 23, NEW_H = 9;
    private static final int DONE_U = 341, DONE_V = 410, DONE_W = 23, DONE_H = 9;
    private static final int DOT_U = 395, DOT_V = 497, DOT_W = 3, DOT_H = 8;
    private static final int TIMED_U = 410, TIMED_V = 501, TIMED_W = 9, TIMED_H = 9;
    private static final int OBJ_ARROW_U = 412, OBJ_ARROW_V = 495, OBJ_ARROW_W = 5, OBJ_ARROW_H = 4;
    private static final int UP_U = 421, UP_V = 459, UP_W = 11, UP_H = 12;
    private static final int DN_U = 421, DN_V = 472, DN_W = 11, DN_H = 12;
    private static final int SBAR_U = 435, SBAR_V = 463, SBAR_W = 6, SBAR_H = 10;

    private static final int TEXT_COLOR = 0xFF222222;
    private static final int DARK_BLUE = 0xFF00008B;

    // ─── 布局（由 init() 计算） ───
    private StardewRenderMapping mapping;
    private float s4;
    private int winX, winY, winW, winH;
    private int entryX, entryW;
    private final int[] entryY = new int[QUESTS_PER_PAGE];
    private int entryH;
    private int closeX, closeY, closeSz;
    private int backX, backY, backSzW, backSzH;
    private int fwdX, fwdY, fwdSzW, fwdSzH;
    private int cancelX, cancelY, cancelSz;
    private int scrollBarOuterX;
    private int upArrowY, dnArrowY;
    private int scrollTrackTop, scrollTrackBottom;

    // ─── 状态 ───
    private final List<List<StardewQuest>> pages = new ArrayList<>();
    private int currentPage;
    private int questPage = -1;
    private StardewQuest shownQuest;
    private List<String> objectiveText;
    private float scrollAmount;
    private float contentHeight;
    private float scissorRectHeight;
    private int lastRewardBoxY = -1;

    public QuestLogScreen() {
        super(Component.translatable("gui.stardewcraft.quest_log"));
    }

    @Override
    public boolean isPauseScreen() { return false; }

    @Override
    protected void init() {
        super.init();
        QuestIconHud.dismissQuestPing();

        float guiScale = (float) minecraft.getWindow().getGuiScale();
        mapping = new StardewRenderMapping(width, height, guiScale);
        s4 = mapping.s4();

        // SDV: base(viewport.W/2 - 832/2, viewport.H/2 - 576/2 + 32, 832, 576)
        winW = mapping.ui(SDV_WIDTH);
        winH = mapping.ui(SDV_HEIGHT);
        winX = (width - winW) / 2;
        winY = (height - winH) / 2 + mapping.ui(32);

        // SDV: questLogButtons[i] = Rectangle(xPos+16, yPos+16+i*((h-32)/6), w-32, (h-32)/6+4)
        entryX = winX + mapping.ui(16);
        entryW = winW - mapping.ui(32);
        int entrySlotH = (winH - mapping.ui(32)) / QUESTS_PER_PAGE;
        entryH = entrySlotH + mapping.ui(4);
        for (int i = 0; i < QUESTS_PER_PAGE; i++) {
            entryY[i] = winY + mapping.ui(16) + i * entrySlotH;
        }

        // SDV: upperRightCloseButton = Rectangle(xPos+w-20, yPos-8, 48, 48)
        closeSz = mapping.ui(48);
        closeX = winX + winW - mapping.ui(20);
        closeY = winY - mapping.ui(8);

        // SDV: backButton = Rectangle(xPos-64, yPos+8, 48, 44)
        backSzW = mapping.ui(48);
        backSzH = mapping.ui(44);
        backX = winX - mapping.ui(64);
        backY = winY + mapping.ui(8);

        // SDV: forwardButton = Rectangle(xPos+w+64-48, yPos+h-48, 48, 44)
        fwdSzW = mapping.ui(48);
        fwdSzH = mapping.ui(44);
        fwdX = winX + winW + mapping.ui(64) - fwdSzW;
        fwdY = winY + winH - mapping.ui(48);

        // SDV: cancelQuestButton = Rectangle(xPos+4, yPos+h+4, 48, 48)
        cancelSz = mapping.ui(48);
        cancelX = winX + mapping.ui(4);
        cancelY = winY + winH + mapping.ui(4);

        // SDV: scrollbar_x = xPos+w+16
        // upArrow = Rectangle(scrollbar_x, yPos+96, 44, 48)
        // downArrow = Rectangle(scrollbar_x, yPos+h-64, 44, 48)
        scrollBarOuterX = winX + winW + mapping.ui(16);
        upArrowY = winY + mapping.ui(96);
        dnArrowY = winY + winH - mapping.ui(64);
        // SDV: scrollBar.Y = upArrow.Y + upArrow.H + 4 = upArrowY + 48 + 4 = upArrowY + 52
        scrollTrackTop = upArrowY + mapping.ui(52);
        scrollTrackBottom = dnArrowY - mapping.ui(4);

        playSound(ModSounds.BIG_SELECT);
        paginateQuests();
    }

    private void paginateQuests() {
        pages.clear();
        List<StardewQuest> all = ClientQuestData.getQuestLog();
        int idx = 0;
        while (idx < all.size()) {
            List<StardewQuest> page = new ArrayList<>();
            for (int i = 0; i < QUESTS_PER_PAGE && idx < all.size(); i++, idx++) {
                StardewQuest q = all.get(idx);
                if (!q.isDestroy()) page.add(q);
            }
            pages.add(page);
        }
        if (pages.isEmpty()) pages.add(new ArrayList<>());
        currentPage = Math.min(currentPage, pages.size() - 1);
        questPage = -1;
        shownQuest = null;
    }

    // ─── 渲染 ───

    @Override
    public void renderBackground(@NotNull GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        g.fill(0, 0, width, height, 0xBF000000);
    }

    @Override
    public void render(@NotNull GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);

        if (questPage == -1) {
            renderListPage(g, mouseX, mouseY);
        } else {
            renderDetailPage(g, mouseX, mouseY);
        }

        // 滚动条（详情页需滚动时）
        if (needsScroll()) {
            StardewGuiUtil.drawFromCursors(g, scrollBarOuterX, upArrowY, UP_U, UP_V, UP_W, UP_H, s4);
            StardewGuiUtil.drawFromCursors(g, scrollBarOuterX, dnArrowY, DN_U, DN_V, DN_W, DN_H, s4);
            int barH = Math.round(SBAR_H * s4);
            float maxScroll = Math.max(1, contentHeight - scissorRectHeight);
            float frac = scrollAmount / maxScroll;
            int barY = scrollTrackTop + Math.round((scrollTrackBottom - scrollTrackTop - barH) * frac);
            // SDV: scrollBar.X = upArrow.X + 12
            int barX = scrollBarOuterX + mapping.ui(12);
            StardewGuiUtil.drawFromCursors(g, barX, barY, SBAR_U, SBAR_V, SBAR_W, SBAR_H, s4);
        }

        // 前进/后退箭头
        if (questPage == -1 && currentPage < pages.size() - 1) {
            StardewGuiUtil.drawFromCursors(g, fwdX, fwdY, FWD_U, FWD_V, FWD_W, FWD_H, s4);
        }
        if (currentPage > 0 || questPage != -1) {
            StardewGuiUtil.drawFromCursors(g, backX, backY, BACK_U, BACK_V, BACK_W, BACK_H, s4);
        }

        // 关闭按钮
        StardewGuiUtil.drawFromCursors(g, closeX, closeY, CLOSE_U, CLOSE_V, CLOSE_W, CLOSE_H, s4);
    }

    private void renderListPage(GuiGraphics g, int mouseX, int mouseY) {
        // SDV: drawTextureBox (384,373,18,18) 4×
        StardewGuiUtil.drawTextureBox(g,
                StardewGuiUtil.CURSORS, StardewGuiUtil.CURSORS_WIDTH, StardewGuiUtil.CURSORS_HEIGHT,
                BOX_U, BOX_V, BOX_W, BOX_H,
                winX, winY, winW, winH, s4, true);

        // SDV: SpriteText.drawStringWithScrollCenteredAt at (xPos+w/2, yPos-64)
        String title = Component.translatable("gui.stardewcraft.quest_log").getString();
        g.drawCenteredString(font, title, winX + winW / 2, winY - mapping.ui(64), 0xFFFFFFFF);

        if (pages.get(currentPage).isEmpty()) {
            String empty = Component.translatable("gui.stardewcraft.quest_log.empty").getString();
            g.drawCenteredString(font, empty, winX + winW / 2, winY + winH / 2, 0xFFAAAAAA);
            return;
        }

        for (int i = 0; i < QUESTS_PER_PAGE; i++) {
            if (pages.get(currentPage).size() <= i) break;
            StardewQuest q = pages.get(currentPage).get(i);

            int ex = entryX;
            int ey = entryY[i];
            boolean hovered = isIn(mouseX, mouseY, ex, ey, entryW, entryH);

            // SDV: drawTextureBox (384,396,15,15), hover → Color.Wheat
            StardewGuiUtil.drawTextureBox(g,
                    StardewGuiUtil.CURSORS, StardewGuiUtil.CURSORS_WIDTH, StardewGuiUtil.CURSORS_HEIGHT,
                    ENTRY_U, ENTRY_V, ENTRY_W, ENTRY_H,
                    ex, ey, entryW, entryH, s4, false);
            if (hovered) {
                g.fill(ex, ey, ex + entryW, ey + entryH, 0x30F5DEB3);
            }

            // SDV: new/complete marker or dot/timed icon
            if (q.shouldDisplayAsNew() || q.shouldDisplayAsComplete()) {
                int mu = q.shouldDisplayAsComplete() ? DONE_U : NEW_U;
                int mv = q.shouldDisplayAsComplete() ? DONE_V : NEW_V;
                int mw = q.shouldDisplayAsComplete() ? DONE_W : NEW_W;
                int mh = q.shouldDisplayAsComplete() ? DONE_H : NEW_H;
                // SDV: at (bounds.X+68, bounds.Y+44) with origin(11,4) at 4×
                // Visual top-left = (bounds.X+68 - 11*4, bounds.Y+44 - 4*4)
                //                 = (bounds.X+24, bounds.Y+28)
                int markX = ex + mapping.ui(24);
                int markY = ey + mapping.ui(28);
                StardewGuiUtil.drawFromCursors(g, markX, markY, mu, mv, mw, mh, s4);
            } else {
                int du, dv, dw, dh;
                if (q.isTimedQuest()) {
                    du = TIMED_U; dv = TIMED_V; dw = TIMED_W; dh = TIMED_H;
                } else {
                    du = DOT_U; dv = DOT_V; dw = DOT_W; dh = DOT_H;
                }
                // SDV: at (bounds.X+32, bounds.Y+28), origin=Zero, scale=4
                StardewGuiUtil.drawFromCursors(g, ex + mapping.ui(32), ey + mapping.ui(28),
                        du, dv, dw, dh, s4);
            }

            // SDV: SpriteText.drawString at (bounds.X+132, bounds.Y+20)
            g.drawString(font, q.getTitle(),
                    ex + mapping.ui(132), ey + mapping.ui(20), TEXT_COLOR, false);
        }
    }

    private void renderDetailPage(GuiGraphics g, int mouseX, int mouseY) {
        if (shownQuest == null) return;

        // SDV: 背景框
        StardewGuiUtil.drawTextureBox(g,
                StardewGuiUtil.CURSORS, StardewGuiUtil.CURSORS_WIDTH, StardewGuiUtil.CURSORS_HEIGHT,
                BOX_U, BOX_V, BOX_W, BOX_H,
                winX, winY, winW, winH, s4, true);

        // SDV: 标题居中 at (xPos+w/2, yPos+32)
        String questTitle = shownQuest.getTitle();
        g.drawCenteredString(font, questTitle,
                winX + winW / 2, winY + mapping.ui(32), TEXT_COLOR);

        // SDV: 计时任务时钟图标 + 剩余天数
        int extraYOffset = 0;
        if (shownQuest.isTimedQuest() && shownQuest.getDaysLeft() > 0) {
            int titleWidth = font.width(questTitle);
            int xOffset = 0;
            if (titleWidth > winW / 2) {
                xOffset = mapping.ui(28);
                extraYOffset = mapping.ui(48);
            }
            // SDV: clock at (xPos+xOffset+32, yPos+48-8+extraY) = (xPos+xOffset+32, yPos+40+extraY)
            int clockX = winX + xOffset + mapping.ui(32);
            int clockY = winY + mapping.ui(40) + extraYOffset;
            StardewGuiUtil.drawFromCursors(g, clockX, clockY, TIMED_U, TIMED_V, TIMED_W, TIMED_H, s4);
            // SDV: text at (xPos+xOffset+80, yPos+40+extraY)
            String daysText = Component.translatable("gui.stardewcraft.quest_log.days_left",
                    shownQuest.getDaysLeft()).getString();
            g.drawString(font, daysText, winX + xOffset + mapping.ui(80), clockY, TEXT_COLOR, false);
        }

        // ─── Scissor 区域 ───
        // SDV: scissor.X = xPos+32, scissor.Y = yPos+96+extraY
        //      scissor.Height = yPos+h-32 - scissor.Y, scissor.Width = w-64
        int scissorX = winX + mapping.ui(32);
        int scissorY = winY + mapping.ui(96) + extraYOffset;
        int scissorW = winW - mapping.ui(64);
        int scissorBottom = winY + winH - mapping.ui(32);
        scissorRectHeight = scissorBottom - scissorY;

        g.enableScissor(scissorX, scissorY, scissorX + scissorW, scissorBottom);

        // SDV: description at (xPos+64, yPos+96+extraY-scroll)
        int contentX = winX + mapping.ui(64);
        int descWidth = winW - mapping.ui(128);
        float yPos = scissorY - scrollAmount + mapping.ui(4);

        List<FormattedCharSequence> descLines = font.split(
                Component.literal(shownQuest.getDescription()), descWidth);
        for (FormattedCharSequence line : descLines) {
            g.drawString(font, line, contentX, (int) yPos, TEXT_COLOR, false);
            yPos += font.lineHeight + 1;
        }

        yPos += mapping.ui(32);

        // SDV: 完成状态 → 奖励区
        if (shownQuest.shouldDisplayAsComplete()) {
            g.disableScissor();

            if (shownQuest.hasMoneyReward()) {
                // SDV: rewardBox at (xPos+w/2-80, yPos+h-32-96)
                int rboxX = winX + winW / 2 - mapping.ui(80);
                int rboxY = winY + winH - mapping.ui(128);
                lastRewardBoxY = rboxY;
                int rboxSz = mapping.ui(96);

                // SDV: "Reward" label at (xPos+36, rewardBox.Y+25)
                String rewardLabel = Component.translatable("gui.stardewcraft.quest_log.reward").getString();
                g.drawString(font, rewardLabel,
                        winX + mapping.ui(36), rboxY + mapping.ui(25), TEXT_COLOR, false);

                // SDV: rewardBox.draw — (293,360,24,24) at 4×
                StardewGuiUtil.drawFromCursors(g, rboxX, rboxY, REWARD_U, REWARD_V, REWARD_W, REWARD_H, s4);

                // SDV: coin at (rewardBox.X+16, rewardBox.Y+16) at 4×
                StardewGuiUtil.drawFromCursors(g,
                        rboxX + mapping.ui(16), rboxY + mapping.ui(16),
                        COIN_U, COIN_V, COIN_W, COIN_H, s4);
                // SDV: money text at (xPos+448, rewardBox.Y+25)
                g.drawString(font, shownQuest.getMoneyReward() + "g",
                        winX + mapping.ui(448), rboxY + mapping.ui(25), 0xFF2C6E0F, false);

                if (isIn(mouseX, mouseY, rboxX, rboxY, rboxSz, rboxSz)) {
                    g.drawCenteredString(font,
                            Component.translatable("gui.stardewcraft.quest_log.collect").getString(),
                            winX + winW / 2, rboxY + rboxSz + mapping.ui(8), 0xFFFFD700);
                }
            }
        } else {
            // SDV: Objectives
            if (objectiveText != null) {
                for (int j = 0; j < objectiveText.size(); j++) {
                    String objStr = objectiveText.get(j);
                    int objTextWidth = descWidth - mapping.ui(64);
                    List<FormattedCharSequence> objLines = font.split(
                            Component.literal(objStr), objTextWidth);

                    // SDV: arrow at (xPos+96+8*dialogueButtonScale/10, yPos) rotated PI/2
                    // We skip the oscillation; draw rotated arrow
                    int arrowX = winX + mapping.ui(96);
                    g.pose().pushPose();
                    g.pose().translate(arrowX, yPos + 2, 0);
                    g.pose().mulPose(com.mojang.math.Axis.ZP.rotationDegrees(90));
                    g.pose().scale(s4, s4, 1.0f);
                    g.blit(StardewGuiUtil.CURSORS, 0, 0, OBJ_ARROW_U, OBJ_ARROW_V, OBJ_ARROW_W, OBJ_ARROW_H,
                            StardewGuiUtil.CURSORS_WIDTH, StardewGuiUtil.CURSORS_HEIGHT);
                    g.pose().popPose();

                    // SDV: text at (xPos+128, yPos-8)
                    int objTextX = winX + mapping.ui(128);
                    for (FormattedCharSequence objLine : objLines) {
                        g.drawString(font, objLine, objTextX, (int) yPos, DARK_BLUE, false);
                        yPos += font.lineHeight + 1;
                    }
                    yPos += mapping.ui(8);
                }
            }

            // 进度计数
            int curCount = shownQuest.getCurrentObjectiveCount();
            int totalCount = shownQuest.getTotalObjectiveCount();
            if (totalCount > 0 && curCount >= 0) {
                String countStr = curCount + "/" + totalCount;
                int countX = winX + winW - mapping.ui(64) - font.width(countStr);
                g.drawString(font, countStr, countX, (int) yPos, DARK_BLUE, false);
                yPos += font.lineHeight + mapping.ui(8);
            }

            contentHeight = yPos + scrollAmount - scissorY;
            g.disableScissor();

            // SDV: cancelQuestButton
            if (shownQuest.isCanBeCancelled() && !shownQuest.isCompleted()) {
                StardewGuiUtil.drawFromCursors(g, cancelX, cancelY,
                        CANCEL_U, CANCEL_V, CANCEL_W, CANCEL_H, s4);
            }

            // 滚动边缘渐隐
            if (needsScroll()) {
                if (scrollAmount > 0) {
                    g.fill(scissorX, scissorY, scissorX + scissorW, scissorY + 4, 0x26000000);
                }
                if (scrollAmount < contentHeight - scissorRectHeight) {
                    g.fill(scissorX, scissorBottom - 4, scissorX + scissorW, scissorBottom, 0x26000000);
                }
            }
        }
    }

    private boolean needsScroll() {
        if (shownQuest != null && shownQuest.shouldDisplayAsComplete()) return false;
        return questPage != -1 && contentHeight > scissorRectHeight;
    }

    // ─── 输入处理 ───

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);
        int mx = (int) mouseX, my = (int) mouseY;

        // Close button
        if (isIn(mx, my, closeX, closeY, closeSz, closeSz)) {
            playSound(ModSounds.BIG_DESELECT);
            onClose();
            return true;
        }

        if (questPage == -1) {
            // ─── 列表页 ───
            for (int i = 0; i < QUESTS_PER_PAGE; i++) {
                if (pages.get(currentPage).size() <= i) break;
                if (isIn(mx, my, entryX, entryY[i], entryW, entryH)) {
                    playSound(ModSounds.SMALL_SELECT);
                    questPage = i;
                    shownQuest = pages.get(currentPage).get(i);
                    objectiveText = shownQuest.getObjectiveDescriptions();
                    shownQuest.setShowNew(false);
                    // 通知服务端清除 showNew 标记，避免下次同步覆盖
                    PacketDistributor.sendToServer(new MarkQuestViewedPayload(shownQuest.getId()));
                    scrollAmount = 0;
                    return true;
                }
            }

            // SDV: forwardButton
            if (currentPage < pages.size() - 1 && isIn(mx, my, fwdX, fwdY, fwdSzW, fwdSzH)) {
                playSound(ModSounds.SHWIP);
                currentPage++;
                return true;
            }
            // SDV: backButton
            if (currentPage > 0 && isIn(mx, my, backX, backY, backSzW, backSzH)) {
                playSound(ModSounds.SHWIP);
                currentPage--;
                return true;
            }

            // SDV: 点击窗口外关闭 + "bigDeSelect"
            playSound(ModSounds.BIG_DESELECT);
            onClose();
            return true;
        } else {
            // ─── 详情页 ───
            StardewQuest q = shownQuest;
            if (q == null) return false;

            // 奖励领取 — SDV: "purchaseRepeat" + OnMoneyRewardClaimed()
            if (q.shouldDisplayAsComplete() && q.hasMoneyReward() && lastRewardBoxY >= 0) {
                int rboxX = winX + winW / 2 - mapping.ui(80);
                int rboxSz = mapping.ui(96);
                if (isIn(mx, my, rboxX, lastRewardBoxY, rboxSz, rboxSz)) {
                    playSound(ModSounds.PURCHASE);
                    PacketDistributor.sendToServer(new ClaimRewardPayload(q.getId()));
                    // SDV OnMoneyRewardClaimed: 客户端立即更新状态，不等服务端同步
                    q.setMoneyReward(0);
                    q.setDestroy(true);
                    // SDV 不在此处 exitQuestPage，而是等玩家手动返回
                    // 但由于奖励已清零，UI 会自动不再显示奖励框
                    exitQuestPage();
                    return true;
                }
            }

            // 取消任务 — SDV: "trashcan"
            if (q.isCanBeCancelled() && !q.isCompleted()
                    && isIn(mx, my, cancelX, cancelY, cancelSz, cancelSz)) {
                playSound(ModSounds.TRASHCAN);
                PacketDistributor.sendToServer(new CancelQuestPayload(q.getId()));
                exitQuestPage();
                return true;
            }

            // 滚动条
            if (needsScroll()) {
                int arrowW = mapping.ui(44);
                int arrowH = mapping.ui(48);
                float maxScroll = contentHeight - scissorRectHeight;
                if (isIn(mx, my, scrollBarOuterX, upArrowY, arrowW, arrowH) && scrollAmount > 0) {
                    playSound(ModSounds.SHWIP);
                    scrollAmount = Math.max(0, scrollAmount - 64);
                    return true;
                }
                if (isIn(mx, my, scrollBarOuterX, dnArrowY, arrowW, mapping.ui(48))
                        && scrollAmount < maxScroll) {
                    playSound(ModSounds.SHWIP);
                    scrollAmount = Math.min(maxScroll, scrollAmount + 64);
                    return true;
                }
            }

            // 后退按钮或无滚动时点击 → 返回列表
            if (isIn(mx, my, backX, backY, backSzW, backSzH) || !needsScroll()) {
                exitQuestPage();
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void exitQuestPage() {
        playSound(ModSounds.SHWIP);
        questPage = -1;
        shownQuest = null;
        scrollAmount = 0;
        paginateQuests();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) {
            if (questPage >= 0) {
                exitQuestPage();
                return true;
            }
            playSound(ModSounds.BIG_DESELECT);
            onClose();
            return true;
        }
        if (com.stardew.craft.client.ModKeyMappings.QUEST_LOG.matches(keyCode, scanCode)) {
            playSound(ModSounds.BIG_DESELECT);
            onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (needsScroll()) {
            float maxScroll = Math.max(0, contentHeight - scissorRectHeight);
            float delta = (float) (-Math.signum(scrollY) * 32);
            scrollAmount = Math.max(0, Math.min(scrollAmount + delta, maxScroll));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    private void playSound(net.neoforged.neoforge.registries.DeferredHolder<net.minecraft.sounds.SoundEvent, net.minecraft.sounds.SoundEvent> sound) {
        if (minecraft != null && minecraft.getSoundManager() != null) {
            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(sound.get(), 1.0f));
        }
    }

    private boolean isIn(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }
}
