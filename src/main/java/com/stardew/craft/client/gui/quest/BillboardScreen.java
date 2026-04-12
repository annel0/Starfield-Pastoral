package com.stardew.craft.client.gui.quest;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.client.gui.overnight.StardewGuiUtil;
import com.stardew.craft.client.hud.StardewTimeHud;
import com.stardew.craft.npc.data.NpcDataRegistry;
import com.stardew.craft.quest.StardewQuest;
import com.stardew.craft.quest.network.AcceptQuestPayload;
import com.stardew.craft.quest.network.ClientQuestData;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * SDV Billboard UI — 公告栏界面（日历 + 每日任务 双Tab）
 * 严格复刻 SDV Billboard.cs 布局
 */
@SuppressWarnings("null")
public class BillboardScreen extends Screen {

    // ─── 纹理 ───
    private static final ResourceLocation BILLBOARD_TEX =
        ResourceLocation.fromNamespaceAndPath("stardewcraft", "textures/gui/billboard.png");
    private static final int BILLBOARD_TEX_W = 338;
    private static final int BILLBOARD_TEX_H = 512;

    // ─── 日历背景 UV ───
    private static final int CAL_U = 0, CAL_V = 198, CAL_W = 301, CAL_H = 198;
    // ─── 每日任务背景 UV ───
    private static final int QUEST_U = 0, QUEST_V = 0, QUEST_W = 338, QUEST_H = 198;

    // ─── 日历格子 ───
    @SuppressWarnings("unused")
    private static final int GRID_COLS = 7, GRID_ROWS = 4;
    private static final int CELL_SDV = 32; // SDV pixel size per cell

    // ─── 今日高亮 ───
    private static final int TODAY_U = 379, TODAY_V = 357, TODAY_W = 3, TODAY_H = 3;

    // ─── 每日任务完成星标 (billboard.png) ───
    private static final int STAR_U = 140, STAR_V = 397, STAR_W = 10, STAR_H = 11;

    // ─── 关闭按钮 ───
    private static final int CLOSE_U = 337, CLOSE_V = 494, CLOSE_W = 12, CLOSE_H = 12;

    // ─── 接受按钮 9-slice ───
    private static final int ACCEPT_U = 403, ACCEPT_V = 373, ACCEPT_W = 9, ACCEPT_H = 9;

    // ─── 颜色 ───
    private static final int TEXT_COLOR = 0xFF404040;
    private static final int MONEY_COLOR = 0xFF2C6E0F;

    // ─── 状态 ───
    private int currentTab = 0; // 0 = 日历, 1 = 每日任务
    private int windowX, windowY, windowW, windowH;
    private float s4;
    private int closeX, closeY, closeW2, closeH2;
    private float closeScale = 1.0f;

    // Accept button bounds
    private int acceptX, acceptY, acceptW2, acceptH2;

    public BillboardScreen() {
        super(Component.translatable("gui.stardewcraft.billboard"));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        super.init();
        float guiScale = (float) minecraft.getWindow().getGuiScale();
        s4 = 4.0f / guiScale;

        // Clamp: largest tab is QUEST_W×QUEST_H (338×198), ensure it fits
        float maxTabW = Math.max(CAL_W, QUEST_W);
        float maxTabH = Math.max(CAL_H, QUEST_H);
        float marginH = 48; // for tab labels above + close button
        s4 = Math.min(s4, Math.min((float) width / maxTabW, (float)(height - marginH) / maxTabH));

        recalcLayout();
    }

    private void recalcLayout() {
        if (currentTab == 0) {
            // Calendar: 301×198 → 4×
            windowW = Math.round(CAL_W * s4);
            windowH = Math.round(CAL_H * s4);
        } else {
            // Daily quest: 338×198 → 4×
            windowW = Math.round(QUEST_W * s4);
            windowH = Math.round(QUEST_H * s4);
        }
        windowX = width / 2 - windowW / 2;
        windowY = height / 2 - windowH / 2;

        closeW2 = Math.round(CLOSE_W * s4);
        closeH2 = Math.round(CLOSE_H * s4);
        closeX = windowX + windowW - Math.round(20 * s4);
        closeY = windowY - Math.round(8 * s4);

        // Accept button
        acceptW2 = Math.round(128 * s4);
        acceptH2 = Math.round(32 * s4);
        acceptX = windowX + windowW / 2 - acceptW2 / 2;
        acceptY = windowY + windowH - Math.round(32 * s4);
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        g.fill(0, 0, width, height, 0xBF000000);
    }

    @Override
    public void render(@NotNull GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);

        if (currentTab == 0) {
            renderCalendar(g, mouseX, mouseY);
        } else {
            renderDailyQuest(g, mouseX, mouseY);
        }

        // Close button
        boolean closeHov = isIn(mouseX, mouseY, closeX, closeY, closeW2, closeH2);
        closeScale = closeHov ? Math.min(closeScale + 0.04f, 1.2f) : Math.max(1.0f, closeScale - 0.04f);
        float cs = s4 * closeScale;
        int cdx = closeX + closeW2 / 2 - Math.round(CLOSE_W * cs / 2);
        int cdy = closeY + closeH2 / 2 - Math.round(CLOSE_H * cs / 2);
        StardewGuiUtil.drawFromCursors(g, cdx, cdy, CLOSE_U, CLOSE_V, CLOSE_W, CLOSE_H, cs);

        // Tab indicators
        int tabY = windowY - Math.round(32 * s4);
        String calLabel = Component.translatable("gui.stardewcraft.billboard.calendar").getString();
        String questLabel = Component.translatable("gui.stardewcraft.billboard.daily_quest").getString();
        int calLabelX = windowX + Math.round(32 * s4);
        int questLabelX = windowX + windowW - font.width(questLabel) - Math.round(32 * s4);

        g.drawString(font, calLabel, calLabelX, tabY, currentTab == 0 ? 0xFFFFD700 : 0xFFAAAAAA, true);
        g.drawString(font, questLabel, questLabelX, tabY, currentTab == 1 ? 0xFFFFD700 : 0xFFAAAAAA, true);
    }

    private void renderCalendar(GuiGraphics g, int mouseX, int mouseY) {
        // Draw calendar background from billboard.png
        drawBillboard(g, windowX, windowY, CAL_U, CAL_V, CAL_W, CAL_H, s4);

        int currentDay = getCurrentDay();
        String currentSeason = StardewTimeHud.getClientTimeCache().getSeasonName().toLowerCase();
        int cellSize = Math.round(CELL_SDV * s4);
        int cellInner = Math.round(31 * s4);
        int gridOffX = Math.round(38 * s4);
        int gridOffY = Math.round(50 * s4);

        // Pre-build birthday lookup for current season: day → npcId
        Map<Integer, String> birthdaysByDay = buildBirthdayMap(currentSeason);

        for (int day = 1; day <= 28; day++) {
            int col = (day - 1) % GRID_COLS;
            int row = (day - 1) / GRID_COLS;
            int cx = windowX + gridOffX + col * cellSize;
            int cy = windowY + gridOffY + row * cellSize;

            // Day number
            g.drawString(font, String.valueOf(day), cx + Math.round(2 * s4), cy + Math.round(2 * s4), TEXT_COLOR, false);

            // NPC birthday mugshot
            String birthdayNpc = birthdaysByDay.get(day);
            if (birthdayNpc != null) {
                int mugSize = Math.round(12 * s4); // small portrait
                int mugX = cx + Math.round(12 * s4);
                int mugY = cy + Math.round(7 * s4);
                drawNpcMugshot(g, birthdayNpc, mugX, mugY, mugSize);
            }

            // Daily quest completed star marker
            if (ClientQuestData.isDailyQuestCompletedOnDay(day)) {
                int starX = cx + cellSize - Math.round(STAR_W * s4) - Math.round(1 * s4);
                int starY = cy + cellSize - Math.round(STAR_H * s4) - Math.round(1 * s4);
                drawBillboard(g, starX, starY, STAR_U, STAR_V, STAR_W, STAR_H, s4);
            }

            // Today highlight
            if (day == currentDay) {
                StardewGuiUtil.drawFromCursors(g, cx, cy, TODAY_U, TODAY_V, TODAY_W, TODAY_H,
                    (float) cellInner / TODAY_W);
            }
        }

        // NPC birthday tooltip on hover
        for (int day = 1; day <= 28; day++) {
            String bNpc = birthdaysByDay.get(day);
            if (bNpc == null) continue;
            int col = (day - 1) % GRID_COLS;
            int row = (day - 1) / GRID_COLS;
            int cx = windowX + gridOffX + col * cellSize;
            int cy = windowY + gridOffY + row * cellSize;
            if (mouseX >= cx && mouseX < cx + cellSize && mouseY >= cy && mouseY < cy + cellSize) {
                String displayName = bNpc.substring(0, 1).toUpperCase() + bNpc.substring(1);
                g.renderTooltip(font, Component.literal(displayName + "'s Birthday"), mouseX, mouseY);
                break;
            }
        }
    }

    /** Build a map of day → npcId for birthdays in the given season */
    private Map<Integer, String> buildBirthdayMap(String season) {
        Map<Integer, String> result = new java.util.HashMap<>();
        try {
            JsonObject root = NpcDataRegistry.events().get("npc_birthdays");
            if (root == null || !root.has("birthdays")) return result;
            JsonObject birthdays = root.getAsJsonObject("birthdays");
            for (Map.Entry<String, JsonElement> entry : birthdays.entrySet()) {
                if (!entry.getValue().isJsonObject()) continue;
                JsonObject bd = entry.getValue().getAsJsonObject();
                String bdSeason = bd.has("season") ? bd.get("season").getAsString().toLowerCase() : "";
                int bdDay = bd.has("day") ? bd.get("day").getAsInt() : -1;
                if (bdSeason.equals(season) && bdDay >= 1 && bdDay <= 28) {
                    result.put(bdDay, entry.getKey());
                }
            }
        } catch (Exception ignored) {}
        return result;
    }

    /** Draw a small NPC portrait (face only, index 0) */
    private void drawNpcMugshot(GuiGraphics g, String npcId, int x, int y, int size) {
        ResourceLocation portraitTex = ResourceLocation.fromNamespaceAndPath(
            StardewCraft.MODID, "textures/portraits/" + npcId.toLowerCase() + ".png");
        // Portrait sheets are 128×N, face is top-left 64×64
        int texW = 128, texH = 128;
        int faceW = 64, faceH = 64;
        float scale = (float) size / faceW;
        g.pose().pushPose();
        g.pose().translate(x, y, 0);
        g.pose().scale(scale, scale, 1.0f);
        g.blit(portraitTex, 0, 0, 0, 0, faceW, faceH, texW, texH);
        g.pose().popPose();
    }

    private void renderDailyQuest(GuiGraphics g, int mouseX, int mouseY) {
        // Draw quest panel background from billboard.png
        drawBillboard(g, windowX, windowY, QUEST_U, QUEST_V, QUEST_W, QUEST_H, s4);

        StardewQuest daily = ClientQuestData.getDailyQuest();
        if (daily == null) {
            String noQuest = Component.translatable("gui.stardewcraft.billboard.no_quest").getString();
            g.drawCenteredString(font, noQuest,
                windowX + windowW / 2,
                windowY + Math.round(80 * s4),
                0xFFAAAAAA);
            return;
        }

        // Check if already accepted
        boolean alreadyAccepted = ClientQuestData.hasQuest(daily.getId());

        // Quest description
        int textX = windowX + Math.round(80 * s4 + 8 * s4);
        int textY = windowY + Math.round(64 * s4);
        int textW = windowW - Math.round(160 * s4);
        List<FormattedCharSequence> descLines = font.split(Component.literal(daily.getDescription()), textW);
        for (FormattedCharSequence line : descLines) {
            g.drawString(font, line, textX, textY, TEXT_COLOR, false);
            textY += font.lineHeight + 1;
        }

        // Objectives
        textY += Math.round(8 * s4);
        for (String obj : daily.getObjectiveDescriptions()) {
            g.drawString(font, "> " + obj, textX, textY, TEXT_COLOR, false);
            textY += font.lineHeight + 2;
        }

        // Reward
        if (daily.hasMoneyReward()) {
            textY += Math.round(8 * s4);
            String rewardStr = Component.translatable("gui.stardewcraft.quest_log.reward").getString()
                + ": " + daily.getMoneyReward() + "g";
            g.drawString(font, rewardStr, textX, textY, MONEY_COLOR, true);
        }

        // Accept button or "already accepted" text
        if (alreadyAccepted) {
            String accepted = Component.translatable("gui.stardewcraft.billboard.already_accepted").getString();
            g.drawCenteredString(font, accepted,
                windowX + windowW / 2,
                acceptY + Math.round(8 * s4),
                0xFF888888);
        } else {
            // 9-slice accept button
            StardewGuiUtil.drawTextureBox(g,
                StardewGuiUtil.CURSORS, StardewGuiUtil.CURSORS_WIDTH, StardewGuiUtil.CURSORS_HEIGHT,
                ACCEPT_U, ACCEPT_V, ACCEPT_W, ACCEPT_H,
                acceptX, acceptY, acceptW2, acceptH2, s4, false);

            String acceptText = Component.translatable("gui.stardewcraft.billboard.accept").getString();
            boolean hov = isIn(mouseX, mouseY, acceptX, acceptY, acceptW2, acceptH2);
            g.drawCenteredString(font, acceptText,
                acceptX + acceptW2 / 2,
                acceptY + acceptH2 / 2 - font.lineHeight / 2,
                hov ? 0xFFFFD700 : 0xFFFFFFFF);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);
        int mx = (int) mouseX, my = (int) mouseY;

        // Close button
        if (isIn(mx, my, closeX, closeY, closeW2, closeH2)) {
            onClose();
            return true;
        }

        // Tab switching via label click
        int tabY = windowY - Math.round(32 * s4);
        int tabH = font.lineHeight + 4;
        String calLabel = Component.translatable("gui.stardewcraft.billboard.calendar").getString();
        String questLabel = Component.translatable("gui.stardewcraft.billboard.daily_quest").getString();
        int calLabelX = windowX + Math.round(32 * s4);
        int questLabelX = windowX + windowW - font.width(questLabel) - Math.round(32 * s4);

        if (isIn(mx, my, calLabelX, tabY, font.width(calLabel), tabH) && currentTab != 0) {
            currentTab = 0;
            recalcLayout();
            return true;
        }
        if (isIn(mx, my, questLabelX, tabY, font.width(questLabel), tabH) && currentTab != 1) {
            currentTab = 1;
            recalcLayout();
            return true;
        }

        // Accept button (daily quest tab)
        if (currentTab == 1) {
            StardewQuest daily = ClientQuestData.getDailyQuest();
            if (daily != null && !ClientQuestData.hasQuest(daily.getId())) {
                if (isIn(mx, my, acceptX, acceptY, acceptW2, acceptH2)) {
                    PacketDistributor.sendToServer(new AcceptQuestPayload(daily.getId()));
                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_TAB) {
            currentTab = (currentTab + 1) % 2;
            recalcLayout();
            return true;
        }
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) {
            onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    // ─── 工具方法 ───

    private void drawBillboard(GuiGraphics g, int x, int y, int u, int v, int w, int h, float scale) {
        int dw = Math.round(w * scale);
        int dh = Math.round(h * scale);
        g.blit(BILLBOARD_TEX, x, y, dw, dh,
            (float) u, (float) v, w, h,
            BILLBOARD_TEX_W, BILLBOARD_TEX_H);
    }

    private int getCurrentDay() {
        return Math.max(1, com.stardew.craft.client.hud.StardewTimeHud.getClientTimeCache().getCurrentDay());
    }

    private boolean isIn(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }
}
