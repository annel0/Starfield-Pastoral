package com.stardew.craft.client.gui.menu;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.client.ClientPlayerDataCache;
import com.stardew.craft.client.NpcFriendshipClientCache;
import com.stardew.craft.client.gui.common.StardewRenderMapping;
import com.stardew.craft.client.gui.overnight.StardewGuiUtil;
import com.stardew.craft.player.ProfessionType;
import com.stardew.craft.player.SkillType;
import com.stardew.craft.network.payload.RequestNpcFriendshipOverviewPayload;
import com.stardew.craft.network.payload.CraftingMenuCraftSubmitPayload;
import com.stardew.craft.network.payload.CraftingMenuInventoryActionPayload;
import com.stardew.craft.player.RecipeCatalogData;
import com.stardew.craft.player.StardewCraftingRecipeData;
import com.stardew.craft.sound.ModSounds;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Comparator;

@SuppressWarnings({"null", "unused"})
public class StardewGameMenuScreen extends Screen {
    private static final int STD_TILE_SIZE = 16;
    private static final int TRASH_W = 18;
    private static final int TRASH_BODY_H = 26;
    private static final int TRASH_LID_H = 10;
    private static final int TRASH_U_BASE = 564;
    private static final int TRASH_BODY_V = 102;
    private static final int TRASH_LID_V = 129;
    private static final float ITEM_VISUAL_SCALE = 1.0f;
    private static final int CARRIED_ITEM_OFFSET_SDV = 16;
    private static final int INVENTORY_COLS = 9;
    private static final int INVENTORY_ROWS = 4;
    private static final int INVENTORY_SLOT_SDV = 64;
    private static final int INVENTORY_GAP_SDV = 4;
    private static final int INVENTORY_TOP_SDV = 440;

    private static final int BORDER_WIDTH = 32;
    private static final int MENU_WIDTH_SDV = 800 + BORDER_WIDTH * 2;
    private static final int MENU_HEIGHT_SDV = 700 + BORDER_WIDTH * 2;
    private static final int TAB_Y_OFFSET_SDV = -56;
    private static final int TAB_START_X_SDV = 64;
    private static final int TAB_STEP_SDV = 64;
    private static final int TAB_SIZE_SDV = 64;

    private static final int CLOSE_X_OFFSET_SDV = 36;
    private static final int CLOSE_Y_OFFSET_SDV = 8;
    private static final int CLOSE_SIZE_SDV = 48;

    private static final int[] TAB_SHEET_INDEX = new int[] {0, 1, 2, 3, 4, -1, -1, 5, 6, 7};
    private static final int TAB_COUNT = 10;
    private static final int TAB_SOCIAL = 2;

    private static final int SOCIAL_ROW_HEIGHT_SDV = 112;
    private static final int SOCIAL_MAX_VISIBLE = 5;
    private static final int[][] SOCIAL_HEART_FILL_PATTERN = new int[][] {
        {1, 1, 0, 1, 1},
        {1, 1, 1, 1, 1},
        {0, 1, 1, 1, 0},
        {0, 0, 1, 0, 0}
    };
    private static final Set<String> DATEABLE_NPCS = Set.of(
        "abigail", "alex", "elliott", "emily", "haley", "harvey", "leah", "maru", "penny", "sam", "sebastian", "shane"
    );
    private static final Map<ResourceLocation, PortraitResource> SOCIAL_PORTRAIT_CACHE = new HashMap<>();

    private static final String[] TAB_KEYS = new String[] {
            "stardewcraft.game_menu.tab.inventory",
            "stardewcraft.game_menu.tab.skills",
            "stardewcraft.game_menu.tab.social",
            "stardewcraft.game_menu.tab.map",
            "stardewcraft.game_menu.tab.crafting",
            "stardewcraft.game_menu.tab.animals",
            "stardewcraft.game_menu.tab.powers",
            "stardewcraft.game_menu.tab.collections",
            "stardewcraft.game_menu.tab.options",
            "stardewcraft.game_menu.tab.exit"
    };

    private StardewRenderMapping mapping;
    private int menuX;
    private int menuY;
    private int menuWidth;
    private int menuHeight;
    private int currentTab;

    private int currentCraftingPage;
    private int selectedCraftingIndex = -1;

    // ---- Farm Management Tab (tab 3) ----
    private int farmMgmtScroll = 0;
    private int farmMgmtSelectedPlayer = -1;  // 选中的在线玩家索引
    private int farmMgmtVisibleRows = 5;      // 动态计算的可见行数
    private List<ItemStack> craftingRecipeStacks = List.of();
    private List<String> craftingRecipeIds = List.of();
    private List<List<RecipeCell>> craftingPages = List.of();
    private final float[] recipeHoverScale = new float[40];
    private int hoveredCraftingIndex = -1;
    private float upButtonScale = 1.0f;
    private float downButtonScale = 1.0f;
    private float trashCanLidRotation;
    private boolean trashCanLidSoundPlayed;
    
    private int currentFocusIndex = -1;
    private float showcaseAlpha = 0.0f;
    private int socialScroll;
    private boolean socialScrolling;

    // Social tuning is intentionally disabled for strict vanilla parity.
    private static final boolean socialTuneMode = false;
    private int socialTuneTarget = -1;
    private int socialTuneStep = 1;
    private boolean socialTuneDragging;
    private final int[] socialTuneOffsetX = new int[SOCIAL_TUNE_TARGET_COUNT];
    private final int[] socialTuneOffsetY = new int[SOCIAL_TUNE_TARGET_COUNT];

    private static final int SOCIAL_TUNE_TARGET_VLINE_LEFT = 0;
    private static final int SOCIAL_TUNE_TARGET_VLINE_MIDDLE = 1;
    private static final int SOCIAL_TUNE_TARGET_VLINE_RIGHT = 2;
    private static final int SOCIAL_TUNE_TARGET_HLINE_1 = 3;
    private static final int SOCIAL_TUNE_TARGET_HLINE_2 = 4;
    private static final int SOCIAL_TUNE_TARGET_HLINE_3 = 5;
    private static final int SOCIAL_TUNE_TARGET_HLINE_4 = 6;
    private static final int SOCIAL_TUNE_TARGET_PORTRAIT = 7;
    private static final int SOCIAL_TUNE_TARGET_NAME = 8;
    private static final int SOCIAL_TUNE_TARGET_HEARTS = 9;
    private static final int SOCIAL_TUNE_TARGET_GIFT_ICON = 10;
    private static final int SOCIAL_TUNE_TARGET_GIFT_BOX_1 = 11;
    private static final int SOCIAL_TUNE_TARGET_GIFT_BOX_2 = 12;
    private static final int SOCIAL_TUNE_TARGET_TALK_ICON = 13;
    private static final int SOCIAL_TUNE_TARGET_TALK_BOX = 14;
    private static final int SOCIAL_TUNE_TARGET_COUNT = 15;

    private static final String[] SOCIAL_TUNE_TARGET_LABELS = new String[] {
        "VLINE_LEFT", "VLINE_MIDDLE", "VLINE_RIGHT",
        "HLINE_1", "HLINE_2", "HLINE_3", "HLINE_4",
        "PORTRAIT", "NAME", "HEARTS",
        "GIFT_ICON", "GIFT_BOX_1", "GIFT_BOX_2",
        "TALK_ICON", "TALK_BOX"
    };

    private record RecipeRequirement(Ingredient ingredient, ItemStack icon, int need) {
    }

    private record RecipeCell(int recipeIndex, int x, int y, boolean bigCraftable) {
    }

    private record RecipeEntry(String id, ItemStack stack, int rank) {
    }

    public StardewGameMenuScreen() {
        super(Component.translatable("stardewcraft.game_menu.title"));
    }

    @Override
    protected void init() {
        super.init();
        recalcLayout();
        rebuildCraftingEntries();
        playUiSound(ModSounds.BIG_SELECT.get(), 1.0f, 1.0f);
    }

    private float guiScale() {
        return this.minecraft == null ? 1.0f : (float) this.minecraft.getWindow().getGuiScale();
    }

    private int ui(int stardewPixels) {
        return mapping == null ? Math.round(stardewPixels / guiScale()) : mapping.ui(stardewPixels);
    }

    private void recalcLayout() {
        this.mapping = new StardewRenderMapping(this.width, this.height, guiScale());
        this.menuWidth = ui(MENU_WIDTH_SDV);
        this.menuHeight = ui(MENU_HEIGHT_SDV);
        this.menuX = mapping.centerX(this.menuWidth);
        this.menuY = this.height / 2 - this.menuHeight / 2;
    }

    private void playUiSound(SoundEvent sound, float volume, float pitch) {
        if (this.minecraft != null) {
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(sound, volume, pitch));
        }
    }

    private int tabX(int index) {
        return menuX + ui(TAB_START_X_SDV + index * TAB_STEP_SDV);
    }

    private int tabY() {
        return menuY + ui(TAB_Y_OFFSET_SDV);
    }

    private int tabSize() {
        return ui(TAB_SIZE_SDV);
    }

    private boolean tabContains(int index, double mouseX, double mouseY) {
        int x = tabX(index);
        int y = tabY();
        int s = tabSize();
        return mouseX >= x && mouseX < x + s && mouseY >= y && mouseY < y + s;
    }

    private boolean closeContains(double mouseX, double mouseY) {
        int x = menuX + activeMenuWidth() - ui(CLOSE_X_OFFSET_SDV);
        int y = menuY - ui(CLOSE_Y_OFFSET_SDV);
        int s = ui(CLOSE_SIZE_SDV);
        return mouseX >= x && mouseX < x + s && mouseY >= y && mouseY < y + s;
    }

    private int activeMenuWidth() {
        return currentTab == TAB_SOCIAL ? socialPageWidth() : menuWidth;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        recalcLayout();
        graphics.fill(0, 0, this.width, this.height, 0x66000000);

        if (currentTab == 4) {
            updateCraftingHoverState(mouseX, mouseY);
        }

        StardewGuiUtil.drawDialogueBoxFrame(graphics, menuX, menuY, activeMenuWidth(), menuHeight);
        drawTabs(graphics);
        drawCloseButton(graphics);
        drawCurrentPage(graphics, mouseX, mouseY);

        if (currentTab == 0) {
            drawInventoryPageTooltips(graphics, mouseX, mouseY);
            ItemStack carried = currentCarriedItem();
            if (!carried.isEmpty()) {
                int drawX = mouseX - 8;
                int drawY = mouseY - 8;
                graphics.pose().pushPose();
                graphics.pose().translate(0, 0, 500);
                graphics.renderItem(carried, drawX, drawY);
                graphics.renderItemDecorations(this.font, carried, drawX, drawY);
                graphics.pose().popPose();
            }
        }

        if (currentTab == 4) {
            if (craftingPages.size() > 1) {
                drawPageArrows(graphics, mouseX, mouseY);
            }
            drawCraftingTooltips(graphics, mouseX, mouseY);
            ItemStack carried = currentCarriedItem();
            if (!carried.isEmpty()) {
                int drawX = mouseX - 8;
                int drawY = mouseY - 8;
                graphics.pose().pushPose();
                graphics.pose().translate(0, 0, 500);
                graphics.renderItem(carried, drawX, drawY);
                graphics.renderItemDecorations(this.font, carried, drawX, drawY);
                graphics.pose().popPose();
            }
        }

        int hoveredTab = hoveredTab(mouseX, mouseY);
        if (hoveredTab >= 0) {
            graphics.renderTooltip(this.font, Component.translatable(TAB_KEYS[hoveredTab]), mouseX, mouseY);
        }

        if (currentTab == TAB_SOCIAL && socialTuneMode) {
            drawSocialTuneOverlay(graphics);
        }
    }

    private int socialTuneUiX(int target) {
        return 0;
    }

    private int socialTuneUiY(int target) {
        return 0;
    }

    private int socialTuneHorizontalLineY(int lineIndex) {
        int target = SOCIAL_TUNE_TARGET_HLINE_1 + lineIndex;
        return socialVerticalStartY() + ui(lineIndex * SOCIAL_ROW_HEIGHT_SDV) + socialTuneUiY(target);
    }

    private int socialTuneHorizontalLineX(int lineIndex) {
        return menuX;
    }

    private int socialPageWidth() {
        return menuWidth + ui(36);
    }

    private int socialPageRightX() {
        return menuX + socialPageWidth();
    }

    private int socialTuneVerticalLineX(int target) {
        return switch (target) {
            case SOCIAL_TUNE_TARGET_VLINE_LEFT -> menuX + ui(268);
            case SOCIAL_TUNE_TARGET_VLINE_MIDDLE -> menuX + ui(620);
            case SOCIAL_TUNE_TARGET_VLINE_RIGHT -> menuX + ui(752);
            default -> menuX;
        };
    }

    private int socialTuneVerticalLineStartY(int target) {
        // Align social columns with the first content row to avoid excess top blank space.
        return menuY + ui(64);
    }

    private int socialTuneTargetAt(double mouseX, double mouseY) {
        for (int target = 0; target < SOCIAL_TUNE_TARGET_COUNT; target++) {
            if (socialTuneTargetContains(target, mouseX, mouseY)) {
                return target;
            }
        }

        // Fallback: when clicking in social table area, snap to nearest separator target.
        int contentTop = menuY;
        int contentBottom = menuY + menuHeight;
        if (mouseX >= menuX && mouseX <= socialPageRightX() && mouseY >= contentTop && mouseY <= contentBottom) {
            int nearest = nearestSeparatorTarget(mouseX, mouseY);
            if (nearest >= 0) {
                return nearest;
            }
        }

        return -1;
    }

    private int nearestSeparatorTarget(double mouseX, double mouseY) {
        int bestTarget = SOCIAL_TUNE_TARGET_VLINE_LEFT;
        double bestDistance = Double.MAX_VALUE;

        int[] vTargets = {SOCIAL_TUNE_TARGET_VLINE_LEFT, SOCIAL_TUNE_TARGET_VLINE_MIDDLE, SOCIAL_TUNE_TARGET_VLINE_RIGHT};
        for (int target : vTargets) {
            double d = Math.abs(mouseX - socialTuneVerticalLineX(target));
            if (d < bestDistance) {
                bestDistance = d;
                bestTarget = target;
            }
        }

        for (int i = 0; i < 4; i++) {
            int target = SOCIAL_TUNE_TARGET_HLINE_1 + i;
            double d = Math.abs(mouseY - socialTuneHorizontalLineY(i));
            if (d < bestDistance) {
                bestDistance = d;
                bestTarget = target;
            }
        }

        double threshold = ui(24);
        return bestDistance <= threshold ? bestTarget : -1;
    }

    private boolean socialTuneTargetContains(int target, double mouseX, double mouseY) {
        int unit16 = ui(16);

        int x;
        int y;
        int w;
        int h;

        switch (target) {
            case SOCIAL_TUNE_TARGET_VLINE_LEFT, SOCIAL_TUNE_TARGET_VLINE_MIDDLE, SOCIAL_TUNE_TARGET_VLINE_RIGHT -> {
                int pad = ui(4);
                x = socialTuneVerticalLineX(target) - pad;
                y = socialTuneVerticalLineStartY(target);
                w = unit16 + pad * 2;
                h = Math.max(0, menuHeight - ui(128));
            }
            case SOCIAL_TUNE_TARGET_HLINE_1, SOCIAL_TUNE_TARGET_HLINE_2, SOCIAL_TUNE_TARGET_HLINE_3, SOCIAL_TUNE_TARGET_HLINE_4 -> {
                int lineIndex = target - SOCIAL_TUNE_TARGET_HLINE_1;
                int pad = ui(4);
                x = socialTuneHorizontalLineX(lineIndex);
                y = socialTuneHorizontalLineY(lineIndex) - pad;
                w = socialPageWidth();
                h = unit16 + pad * 2;
            }
            case SOCIAL_TUNE_TARGET_PORTRAIT -> {
                x = socialPortraitX();
                w = ui(64);
                h = ui(96);
                for (int row = 0; row < SOCIAL_MAX_VISIBLE; row++) {
                    y = socialRowPosition(row) + socialTuneUiY(SOCIAL_TUNE_TARGET_PORTRAIT);
                    if (mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h) {
                        return true;
                    }
                }
                return false;
            }
            case SOCIAL_TUNE_TARGET_NAME -> {
                x = socialNameCenterX() - ui(72);
                w = ui(144);
                h = ui(36);
                for (int row = 0; row < SOCIAL_MAX_VISIBLE; row++) {
                    y = socialRowPosition(row) + ui(16) + socialTuneUiY(SOCIAL_TUNE_TARGET_NAME);
                    if (mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h) {
                        return true;
                    }
                }
                return false;
            }
            case SOCIAL_TUNE_TARGET_HEARTS -> {
                x = socialHeartsBaseX();
                w = ui(320);
                h = ui(48);
                for (int row = 0; row < SOCIAL_MAX_VISIBLE; row++) {
                    y = socialRowPosition(row) + socialHeartsTopRowOffsetY();
                    if (mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h) {
                        return true;
                    }
                }
                return false;
            }
            case SOCIAL_TUNE_TARGET_GIFT_ICON -> {
                x = socialGiftIconX();
                w = ui(56);
                h = ui(48);
                for (int row = 0; row < SOCIAL_MAX_VISIBLE; row++) {
                    y = socialRowPosition(row) + socialGiftIconOffsetY();
                    if (mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h) {
                        return true;
                    }
                }
                return false;
            }
            case SOCIAL_TUNE_TARGET_GIFT_BOX_1 -> {
                x = socialGiftFirstBoxX();
                w = ui(36);
                h = ui(36);
                for (int row = 0; row < SOCIAL_MAX_VISIBLE; row++) {
                    y = socialRowPosition(row) + socialGiftBoxesOffsetY();
                    if (mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h) {
                        return true;
                    }
                }
                return false;
            }
            case SOCIAL_TUNE_TARGET_GIFT_BOX_2 -> {
                x = socialGiftSecondBoxX();
                w = ui(36);
                h = ui(36);
                for (int row = 0; row < SOCIAL_MAX_VISIBLE; row++) {
                    y = socialRowPosition(row) + socialGiftBoxesOffsetY();
                    if (mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h) {
                        return true;
                    }
                }
                return false;
            }
            case SOCIAL_TUNE_TARGET_TALK_ICON -> {
                x = socialTalkIconX();
                w = ui(52);
                h = ui(44);
                for (int row = 0; row < SOCIAL_MAX_VISIBLE; row++) {
                    y = socialRowPosition(row) + socialTalkIconOffsetY();
                    if (mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h) {
                        return true;
                    }
                }
                return false;
            }
            case SOCIAL_TUNE_TARGET_TALK_BOX -> {
                x = socialTalkBoxX();
                w = ui(36);
                h = ui(36);
                for (int row = 0; row < SOCIAL_MAX_VISIBLE; row++) {
                    y = socialRowPosition(row) + socialTalkBoxOffsetY();
                    if (mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h) {
                        return true;
                    }
                }
                return false;
            }
            default -> {
                return false;
            }
        }

        return mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h;
    }

    private void drawSocialTuneOverlay(GuiGraphics graphics) {
        int x = menuX + ui(16);
        int y = menuY - ui(22);
        String label = socialTuneTarget >= 0 && socialTuneTarget < SOCIAL_TUNE_TARGET_LABELS.length
            ? SOCIAL_TUNE_TARGET_LABELS[socialTuneTarget]
            : "NONE";
        String line = "SocialTune[F6] LeftClick=select Drag=move Wheel=step(" + socialTuneStep + ") R=reset selected Shift+R=reset all target=" + label;
        graphics.drawString(this.font, line, x, y, 0xFFEEE2C2, false);
    }

    private boolean isShiftPressed(int modifiers) {
        return (modifiers & GLFW.GLFW_MOD_SHIFT) != 0;
    }

    private boolean handleSocialTuneKey(int keyCode, int modifiers) {
        return false;
    }

    private void applySocialTuneDelta(int dx, int dy) {
        if (socialTuneTarget < 0 || socialTuneTarget >= SOCIAL_TUNE_TARGET_COUNT) {
            return;
        }
        socialTuneOffsetX[socialTuneTarget] += dx;
        socialTuneOffsetY[socialTuneTarget] += dy;
    }

    private void resetAllSocialTuneOffsets() {
        for (int i = 0; i < SOCIAL_TUNE_TARGET_COUNT; i++) {
            socialTuneOffsetX[i] = 0;
            socialTuneOffsetY[i] = 0;
        }
    }

    private void resetSocialTuneTarget(int target) {
        if (target < 0 || target >= SOCIAL_TUNE_TARGET_COUNT) {
            return;
        }
        socialTuneOffsetX[target] = 0;
        socialTuneOffsetY[target] = 0;
    }

    private int hoveredTab(int mouseX, int mouseY) {
        for (int i = 0; i < TAB_COUNT; i++) {
            if (tabContains(i, mouseX, mouseY)) {
                return i;
            }
        }
        return -1;
    }

    private void drawTabs(GuiGraphics graphics) {
        int yBase = tabY();
        int selectedOffsetY = ui(8);
        float scale = mapping.s4();

        for (int i = 0; i < TAB_COUNT; i++) {
            int x = tabX(i);
            int y = yBase + (currentTab == i ? selectedOffsetY : 0);

            if (i == 5) {
                StardewGuiUtil.drawFromCursors16(graphics, x, y, 257, 246, 16, 16, scale);
                continue;
            }
            if (i == 6) {
                StardewGuiUtil.drawFromCursors16(graphics, x, y, 216, 494, 16, 16, scale);
                continue;
            }

            int sheetIndex = TAB_SHEET_INDEX[i];
            StardewGuiUtil.drawFromCursors(graphics, x, y, sheetIndex * 16, 368, 16, 16, scale);
        }
    }

    private void drawCloseButton(GuiGraphics graphics) {
        int x = menuX + activeMenuWidth() - ui(CLOSE_X_OFFSET_SDV);
        int y = menuY - ui(CLOSE_Y_OFFSET_SDV);
        StardewGuiUtil.drawFromCursors(graphics, x, y, 337, 494, 12, 12, mapping.s4());
    }

    private void drawCurrentPage(GuiGraphics graphics, int mouseX, int mouseY) {
        if (currentTab == TAB_SOCIAL) {
            drawSocialPage(graphics, mouseX, mouseY);
            return;
        }

        if (currentTab == 0) {
            drawInventoryPage(graphics, mouseX, mouseY);
            return;
        }

        if (currentTab == 1) {
            drawSkillsPage(graphics, mouseX, mouseY);
            return;
        }

        if (currentTab == 4) {
            drawCraftingPage(graphics, mouseX, mouseY);
            return;
        }

        if (currentTab == 3) {
            drawFarmManagementPage(graphics, mouseX, mouseY);
            return;
        }

        Component tabName = Component.translatable(TAB_KEYS[currentTab]);
        Component title = Component.translatable("stardewcraft.game_menu.placeholder_title", tabName);
        Component line = Component.translatable("stardewcraft.game_menu.placeholder_body");

        int titleX = menuX + menuWidth / 2 - this.font.width(title) / 2;
        int lineX = menuX + menuWidth / 2 - this.font.width(line) / 2;
        int centerY = menuY + menuHeight / 2;

        graphics.drawString(this.font, title, titleX, centerY - 10, 0xFFF3E6C6, false);
        graphics.drawString(this.font, line, lineX, centerY + 8, 0xFFD8C9A8, false);
    }

    // ============ Tab 3: Farm Management Page (农场管理) ============

    private static final int FARM_MGMT_ROW_HEIGHT_SDV = 84;
    private static final int FARM_MGMT_VISIBLE_ROWS = 5;
    private static final String[] PERM_KEYS = {
        "gui.stardewcraft.farm_mgmt.perm_0",
        "gui.stardewcraft.farm_mgmt.perm_1",
        "gui.stardewcraft.farm_mgmt.perm_2"
    };

    private void drawFarmManagementPage(GuiGraphics graphics, int mouseX, int mouseY) {
        if (!com.stardew.craft.client.gui.FarmPermissionClientCache.hasData()) {
            Component loading = Component.translatable("gui.stardewcraft.farm_mgmt.loading");
            int lx = menuX + menuWidth / 2 - this.font.width(loading) / 2;
            graphics.drawString(this.font, loading, lx, menuY + menuHeight / 2, 0x582A11, false);
            return;
        }

        int spaceSide = ui(64);
        int spaceTop = ui(64);
        int contentX = menuX + spaceSide;
        int contentW = menuWidth - spaceSide * 2;
        int y = menuY + spaceTop;

        int rowHGui = ui(FARM_MGMT_ROW_HEIGHT_SDV);

        // 标题
        Component title = Component.translatable("gui.stardewcraft.farm_mgmt.title")
                .withStyle(ChatFormatting.BOLD);
        int titleX = menuX + menuWidth / 2 - this.font.width(title) / 2;
        graphics.drawString(this.font, title, titleX, y, 0x582A11, false);
        y += this.font.lineHeight + ui(12);

        // 默认权限标签
        int defaultPerm = com.stardew.craft.client.gui.FarmPermissionClientCache.getDefaultPerm();
        Component defaultLabel = Component.translatable("gui.stardewcraft.farm_mgmt.default_perm");
        graphics.drawString(this.font, defaultLabel, contentX, y, 0x582A11, false);
        y += this.font.lineHeight + ui(4);

        // 默认权限描述
        Component defaultDesc = Component.translatable("gui.stardewcraft.farm_mgmt.default_perm.desc");
        graphics.drawString(this.font, defaultDesc, contentX, y, 0x8D6E63, false);
        y += this.font.lineHeight + ui(8);

        // 默认权限按钮组 — 等宽按钮，居中排列
        int btnH = this.font.lineHeight + ui(8);
        int btnGap = ui(8);
        // 计算按钮统一宽度（取最宽 + padding）
        int maxLblW = 0;
        for (int lvl = 0; lvl <= 2; lvl++) {
            maxLblW = Math.max(maxLblW, this.font.width(Component.translatable(PERM_KEYS[lvl])));
        }
        int defBtnW = maxLblW + ui(20);
        int totalBtnsW = defBtnW * 3 + btnGap * 2;
        int defBtnStartX = contentX + (contentW - totalBtnsW) / 2;
        for (int lvl = 0; lvl <= 2; lvl++) {
            Component lbl = Component.translatable(PERM_KEYS[lvl]);
            int btnX = defBtnStartX + lvl * (defBtnW + btnGap);
            boolean active = (lvl == defaultPerm);
            int btnColor = active ? 0xFF4CAF50 : 0xFF757575;
            graphics.fill(btnX + 1, y, btnX + defBtnW - 1, y + btnH, btnColor);
            graphics.fill(btnX, y + 1, btnX + defBtnW, y + btnH - 1, btnColor);
            int textX = btnX + (defBtnW - this.font.width(lbl)) / 2;
            int textY = y + (btnH - this.font.lineHeight) / 2;
            graphics.drawString(this.font, lbl, textX, textY, 0xFFFFFFFF, false);
        }
        farmMgmtDefaultPermY = y;
        farmMgmtDefBtnW = defBtnW;
        farmMgmtDefBtnStartX = defBtnStartX;
        y += btnH + ui(16);

        // 分隔线
        StardewGuiUtil.drawHorizontalPartitionSmall(graphics, contentX, y, contentW, mapping.s4());
        y += ui(36);

        // 在线玩家列表标题
        Component playerTitle = Component.translatable("gui.stardewcraft.farm_mgmt.online_players")
                .withStyle(ChatFormatting.BOLD);
        graphics.drawString(this.font, playerTitle, contentX, y, 0x582A11, false);
        y += this.font.lineHeight + ui(8);

        var players = com.stardew.craft.client.gui.FarmPermissionClientCache.getPlayers();
        if (players.isEmpty()) {
            Component noPlayers = Component.translatable("gui.stardewcraft.farm_mgmt.no_players");
            graphics.drawString(this.font, noPlayers, contentX + ui(16), y, 0x8D6E63, false);
            return;
        }

        farmMgmtPlayerListY = y;
        // 动态计算可见行数：剩余空间 / 行高，至少 1 行
        int availableH = (menuY + menuHeight - ui(32)) - y;
        farmMgmtVisibleRows = Math.max(1, availableH / rowHGui);
        // clamp scroll 防止 resize 后越界
        int maxScrollClamp = Math.max(0, players.size() - farmMgmtVisibleRows);
        farmMgmtScroll = Math.min(farmMgmtScroll, maxScrollClamp);
        int maxVisible = Math.min(farmMgmtVisibleRows, players.size() - farmMgmtScroll);

        // 玩家行按钮统一宽度
        int maxPermW = 0;
        for (int lvl = 0; lvl <= 2; lvl++) {
            maxPermW = Math.max(maxPermW, this.font.width(Component.translatable(PERM_KEYS[lvl])));
        }
        int playerBtnW = maxPermW + ui(16);
        int playerBtnGap = ui(6);

        // scissor 裁剪列表区域（clamp 到面板底部）
        int listBottom = Math.min(y + farmMgmtVisibleRows * rowHGui, menuY + menuHeight - ui(16));
        graphics.enableScissor(contentX, y, contentX + contentW, listBottom);

        for (int i = 0; i < maxVisible; i++) {
            int idx = farmMgmtScroll + i;
            var entry = players.get(idx);
            int rowY = y + i * rowHGui;

            // 选中行高亮
            if (idx == farmMgmtSelectedPlayer) {
                graphics.fill(contentX, rowY, contentX + contentW, rowY + rowHGui - 2, 0x33EADB8C);
            }

            // 第一行：玩家名 + "(使用默认)" 标注
            int currentPerm = entry.permission();
            boolean isDefault = (currentPerm == -1);
            int nameY = rowY + ui(6);
            graphics.drawString(this.font, Component.literal(entry.name()),
                    contentX + ui(8), nameY, 0x582A11, false);
            if (isDefault) {
                Component usingDefault = Component.translatable("gui.stardewcraft.farm_mgmt.using_default");
                int udX = contentX + ui(8) + this.font.width(entry.name()) + ui(12);
                graphics.drawString(this.font, usingDefault, udX, nameY, 0x8D6E63, false);
            }

            // 第二行：3 个权限按钮（左对齐，在名字下方）
            int effectivePerm = isDefault ? defaultPerm : currentPerm;
            int btnRowH = this.font.lineHeight + ui(6);
            int btnRowY = nameY + this.font.lineHeight + ui(6);
            for (int lvl = 0; lvl <= 2; lvl++) {
                Component lbl = Component.translatable(PERM_KEYS[lvl]);
                int bX = contentX + ui(8) + lvl * (playerBtnW + playerBtnGap);
                int bColor;
                if (lvl == effectivePerm) {
                    bColor = isDefault ? 0xFF5588AA : 0xFF4CAF50;
                } else {
                    bColor = 0xFF616161;
                }
                graphics.fill(bX + 1, btnRowY, bX + playerBtnW - 1, btnRowY + btnRowH, bColor);
                graphics.fill(bX, btnRowY + 1, bX + playerBtnW, btnRowY + btnRowH - 1, bColor);
                int tX = bX + (playerBtnW - this.font.width(lbl)) / 2;
                int tY = btnRowY + (btnRowH - this.font.lineHeight) / 2;
                graphics.drawString(this.font, lbl, tX, tY, 0xFFFFFFFF, false);
            }
        }

        graphics.disableScissor();

        // 滚动条
        if (players.size() > farmMgmtVisibleRows) {
            int barX = contentX + contentW - ui(6);
            int barTotalH = farmMgmtVisibleRows * rowHGui;
            int thumbH = Math.max(ui(16), barTotalH * farmMgmtVisibleRows / players.size());
            int maxScroll = Math.max(1, players.size() - farmMgmtVisibleRows);
            int thumbY = y + (barTotalH - thumbH) * farmMgmtScroll / maxScroll;
            graphics.fill(barX, y, barX + ui(3), y + barTotalH, 0x22000000);
            graphics.fill(barX, thumbY, barX + ui(3), thumbY + thumbH, 0x66582A11);
        }
    }

    /** 用于 mouseClicked 时定位默认权限按钮的 Y 坐标 */
    private int farmMgmtDefaultPermY = 0;
    /** 默认权限按钮统一宽度和起始 X */
    private int farmMgmtDefBtnW = 0;
    private int farmMgmtDefBtnStartX = 0;
    /** 在线玩家列表起始 Y 坐标 */
    private int farmMgmtPlayerListY = 0;

    private boolean handleFarmMgmtClick(int mouseX, int mouseY) {
        int spaceSide = ui(64);
        int contentX = menuX + spaceSide;
        int contentW = menuWidth - spaceSide * 2;
        int btnH = this.font.lineHeight + ui(8);
        int btnGap = ui(8);
        int rowHGui = ui(FARM_MGMT_ROW_HEIGHT_SDV);
        int defaultPerm = com.stardew.craft.client.gui.FarmPermissionClientCache.getDefaultPerm();
        var players = com.stardew.craft.client.gui.FarmPermissionClientCache.getPlayers();

        // 默认权限按钮点击（等宽按钮，居中排列）
        if (mouseY >= farmMgmtDefaultPermY && mouseY < farmMgmtDefaultPermY + btnH) {
            for (int lvl = 0; lvl <= 2; lvl++) {
                int btnX = farmMgmtDefBtnStartX + lvl * (farmMgmtDefBtnW + btnGap);
                if (mouseX >= btnX && mouseX < btnX + farmMgmtDefBtnW) {
                    if (lvl != defaultPerm) {
                        PacketDistributor.sendToServer(
                            new com.stardew.craft.network.payload.FarmPermissionUpdatePayload(
                                2, new java.util.UUID(0, 0), lvl));
                        com.stardew.craft.client.gui.FarmPermissionClientCache.update(
                            lvl, players);
                        playUiSound(ModSounds.SMALL_SELECT.get(), 1.0f, 1.0f);
                    }
                    return true;
                }
            }
        }

        // 在线玩家权限按钮点击（2-line layout）
        if (!players.isEmpty() && mouseY >= farmMgmtPlayerListY) {
            int maxVisible = Math.min(farmMgmtVisibleRows, players.size() - farmMgmtScroll);
            // 玩家行按钮统一宽度
            int maxPermW = 0;
            for (int lvl = 0; lvl <= 2; lvl++) {
                maxPermW = Math.max(maxPermW, this.font.width(Component.translatable(PERM_KEYS[lvl])));
            }
            int playerBtnW = maxPermW + ui(16);
            int playerBtnGap = ui(6);
            int btnRowH = this.font.lineHeight + ui(6);

            for (int i = 0; i < maxVisible; i++) {
                int idx = farmMgmtScroll + i;
                var entry = players.get(idx);
                int rowY = farmMgmtPlayerListY + i * rowHGui;

                if (mouseY < rowY || mouseY >= rowY + rowHGui) continue;

                // 第二行按钮的 Y 坐标
                int nameY = rowY + ui(6);
                int btnRowY = nameY + this.font.lineHeight + ui(6);
                if (mouseY >= btnRowY && mouseY < btnRowY + btnRowH) {
                    for (int lvl = 0; lvl <= 2; lvl++) {
                        int bX = contentX + ui(8) + lvl * (playerBtnW + playerBtnGap);
                        if (mouseX >= bX && mouseX < bX + playerBtnW) {
                            int currentPerm = entry.permission();
                            int effectivePerm = (currentPerm == -1) ? defaultPerm : currentPerm;
                            if (lvl != effectivePerm || currentPerm == -1) {
                                PacketDistributor.sendToServer(
                                    new com.stardew.craft.network.payload.FarmPermissionUpdatePayload(
                                        0, entry.uuid(), lvl));
                                var newPlayers = new java.util.ArrayList<>(players);
                                newPlayers.set(idx, new com.stardew.craft.network.payload.FarmPermSyncPayload.PlayerPermEntry(
                                    entry.uuid(), entry.name(), lvl));
                                com.stardew.craft.client.gui.FarmPermissionClientCache.update(
                                    com.stardew.craft.client.gui.FarmPermissionClientCache.getDefaultPerm(), newPlayers);
                                playUiSound(ModSounds.SMALL_SELECT.get(), 1.0f, 1.0f);
                            }
                            return true;
                        }
                    }
                }

                // 点击行选中
                farmMgmtSelectedPlayer = idx;
                return true;
            }
        }
        return false;
    }

    // ============ Tab 1: Skills Page (SDV SkillsPage 1:1 parity) ============

    // SDV skill row order: Farming(0), Mining(3), Foraging(2), Fishing(1), Combat(4)
    private static final SkillType[] SKILLS_PAGE_ROW_ORDER = {
        SkillType.FARMING, SkillType.MINING, SkillType.FORAGING, SkillType.FISHING, SkillType.COMBAT
    };

    // SDV skill icon source rects from cursors.png (10x10 each, y=428)
    private static final int[][] SKILL_ICON_SOURCES = {
        {10, 428, 10, 10},   // Farming
        {30, 428, 10, 10},   // Mining
        {60, 428, 10, 10},   // Foraging
        {20, 428, 10, 10},   // Fishing
        {120, 428, 10, 10},  // Combat
    };

    // SDV skill name i18n keys (matching SDV row order)
    private static final String[] SKILL_NAME_KEYS = {
        "stardewcraft.skills_page.farming",
        "stardewcraft.skills_page.mining",
        "stardewcraft.skills_page.foraging",
        "stardewcraft.skills_page.fishing",
        "stardewcraft.skills_page.combat",
    };

    // SDV skill hover description i18n keys
    private static final String[][] SKILL_HOVER_KEYS = {
        {"stardewcraft.skills_page.farming_hover1", "stardewcraft.skills_page.farming_hover2"}, // Farming: hoe + watercan
        {"stardewcraft.skills_page.mining_hover"},   // Mining: pickaxe
        {"stardewcraft.skills_page.foraging_hover"},  // Foraging: axe
        {"stardewcraft.skills_page.fishing_hover"},   // Fishing: rod
        {"stardewcraft.skills_page.combat_hover"},    // Combat: health
    };

    // Profession IDs at level 5 and 10 per skill row (SDV order: farming, mining, foraging, fishing, combat)
    private static final ProfessionType[][] SKILL_ROW_LV5_PROFS = {
        {ProfessionType.RANCHER, ProfessionType.TILLER},
        {ProfessionType.MINER, ProfessionType.GEOLOGIST},
        {ProfessionType.FORESTER, ProfessionType.GATHERER},
        {ProfessionType.FISHER, ProfessionType.TRAPPER},
        {ProfessionType.FIGHTER, ProfessionType.SCOUT},
    };

    private static final int SKILLS_VERTICAL_SPACING = 68; // SDV px between rows

    // SDV Farmer title level thresholds (based on sum of all 5 skill levels / 2... actually just sum)
    // SDV: Farmer.Level = (farming + mining + foraging + fishing + combat) / 2
    private static final String[] FARMER_TITLE_KEYS = {
        "stardewcraft.farmer_title.farm_king",      // 30+
        "stardewcraft.farmer_title.cropmaster",      // 29
        "stardewcraft.farmer_title.agriculturist",   // 27-28
        "stardewcraft.farmer_title.farmer",          // 25-26
        "stardewcraft.farmer_title.rancher",         // 23-24
        "stardewcraft.farmer_title.planter",         // 21-22
        "stardewcraft.farmer_title.granger",         // 19-20
        "stardewcraft.farmer_title.farmboy",         // 17-18 (male)
        "stardewcraft.farmer_title.sodbuster",       // 15-16
        "stardewcraft.farmer_title.smallholder",     // 13-14
        "stardewcraft.farmer_title.tiller",          // 11-12
        "stardewcraft.farmer_title.farmhand",        // 9-10
        "stardewcraft.farmer_title.cowpoke",         // 7-8
        "stardewcraft.farmer_title.bumpkin",         // 5-6
        "stardewcraft.farmer_title.greenhorn",       // 3-4
        "stardewcraft.farmer_title.newcomer",        // 0-2
    };
    private static final int[] FARMER_TITLE_MIN_LEVELS = {
        30, 29, 27, 25, 23, 21, 19, 17, 15, 13, 11, 9, 7, 5, 3, 0
    };

    private String getFarmerTitle() {
        int totalLevel = 0;
        for (SkillType skill : SkillType.values()) {
            totalLevel += ClientPlayerDataCache.getSkillLevel(skill);
        }
        // SDV: Farmer.Level = totalLevel / 2
        int farmerLevel = totalLevel / 2;
        for (int i = 0; i < FARMER_TITLE_MIN_LEVELS.length; i++) {
            if (farmerLevel >= FARMER_TITLE_MIN_LEVELS[i]) {
                return Component.translatable(FARMER_TITLE_KEYS[i]).getString();
            }
        }
        return Component.translatable("stardewcraft.farmer_title.newcomer").getString();
    }

    // Hover state for skills page
    private String skillsHoverText = "";
    private String skillsHoverTitle = "";
    private int skillsHoveredProfessionId = -1;
    private int skillsHoveredBarX = 0;
    private int skillsHoveredBarY = 0;

    /**
     * SDV NumberSprite.draw equivalent.
     * Draws a number using digit sprites from cursors.png at (512,128), 8x8 per digit, 6 per row (48px wide).
     */
    private void drawNumberSprite(GuiGraphics graphics, int number, int posX, int posY, int color, float alpha) {
        float scale = mapping.s4();
        int digitCount = 0;
        int n = number;
        // Count digits
        do { digitCount++; n /= 10; } while (n > 0);

        // Draw from right to left (least significant digit first)
        int drawX = posX;
        n = number;
        // SDV draws right-to-left from the position, so we need to adjust
        // Actually SDV uses position as the rightmost digit's center, let's replicate exactly
        int tempNumber = number;
        do {
            int currentDigit = tempNumber % 10;
            tempNumber /= 10;
            int texU = 512 + (currentDigit * 8) % 48;
            int texV = 128 + (currentDigit * 8) / 48 * 8;

            // Draw the digit - SDV draws centered on (4,4) origin
            graphics.pose().pushPose();
            graphics.pose().translate(drawX, posY, 0);
            graphics.pose().scale(scale, scale, 1.0f);
            // Apply color tint
            float r = ((color >> 16) & 0xFF) / 255.0f;
            float g = ((color >> 8) & 0xFF) / 255.0f;
            float b = (color & 0xFF) / 255.0f;
            graphics.setColor(r, g, b, alpha);
            graphics.blit(StardewGuiUtil.CURSORS, -4, -4, texU, texV, 8, 8,
                StardewGuiUtil.CURSORS_WIDTH, StardewGuiUtil.CURSORS_HEIGHT);
            graphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
            graphics.pose().popPose();

            // Move left for next digit: SDV spacing = 8*1*4 - 4 = 28 SDV px
            drawX -= ui(28);
        } while (tempNumber > 0);
    }

    private void drawSkillsPage(GuiGraphics graphics, int mouseX, int mouseY) {
        float s4 = mapping.s4();
        int borderWidth = ui(BORDER_WIDTH);
        int spaceSide = ui(32);   // IClickableMenu.spaceToClearSideBorder
        int spaceTop = ui(96);    // IClickableMenu.spaceToClearTopBorder

        // --- Player Panel (left side) ---
        int playerPanelX = menuX + ui(64);
        int playerPanelY = menuY + borderWidth + spaceTop;
        // Draw day/night background (same as inventory page)
        int bgX = playerPanelX - ui(8);
        int bgY = playerPanelY - ui(20);
        Minecraft mc = this.minecraft;
        if (mc != null && mc.player != null) {
            long dayTime = mc.level != null ? mc.level.getDayTime() % 24000 : 0;
            boolean isNight = dayTime >= 13000;
            ResourceLocation bgTex = isNight ? NIGHTBG : DAYBG;
            int bgW = ui(131);
            int bgH = ui(190);
            graphics.blit(bgTex, bgX, bgY, 0, 0, bgW, bgH, bgW, bgH);

            // Draw MC player entity
            int margin = ui(8);
            net.minecraft.client.gui.screens.inventory.InventoryScreen
                .renderEntityInInventoryFollowsMouse(
                    graphics,
                    bgX + margin, bgY + margin,
                    bgX + bgW - margin, bgY + bgH - margin,
                    ui(30), 0.0625F,
                    (float) mouseX, (float) mouseY,
                    mc.player
                );

            // Player name (centered under the panel)
            String playerName = mc.player.getName().getString();
            Component boldPlayerName = Component.literal(playerName).withStyle(ChatFormatting.BOLD);
            float nameScale = sdvTextScale();
            int nameRawW = this.font.width(boldPlayerName);
            float nameEffScale = nameScale;
            int namePanelW = ui(128);
            if (nameRawW * nameEffScale > namePanelW) {
                nameEffScale = (float) namePanelW / nameRawW;
            }
            int nameScaledW = Math.round(nameRawW * nameEffScale);
            int nameX = playerPanelX + ui(64) - nameScaledW / 2;
            int nameY = playerPanelY + ui(192 - 17);
            graphics.pose().pushPose();
            graphics.pose().translate(nameX, nameY, 0);
            graphics.pose().scale(nameEffScale, nameEffScale, 1.0f);
            graphics.drawString(this.font, boldPlayerName, 0, 0, 0xFF5B3A1A, false);
            graphics.pose().popPose();

            // Player title (centered below name)
            String playerTitle = getFarmerTitle();
            float titleScale = sdvTextScale() * 0.85f;
            Component boldTitle = Component.literal(playerTitle).withStyle(ChatFormatting.BOLD);
            int titleRawW = this.font.width(boldTitle);
            float titleEffScale = titleScale;
            if (titleRawW * titleEffScale > namePanelW) {
                titleEffScale = (float) namePanelW / titleRawW;
            }
            int titleScaledW = Math.round(titleRawW * titleEffScale);
            int titleX = playerPanelX + ui(64) - titleScaledW / 2;
            int titleY = playerPanelY + ui(256 - 32 - 19);
            graphics.pose().pushPose();
            graphics.pose().translate(titleX, titleY, 0);
            graphics.pose().scale(titleEffScale, titleEffScale, 1.0f);
            graphics.drawString(this.font, boldTitle, 0, 0, 0xFF5B3A1A, false);
            graphics.pose().popPose();
        }

        // --- Horizontal separator ---
        int sepY = menuY + spaceTop + ui((int)(menuHeight * guiScale() / 2f) + 21);
        int sepX = menuX + spaceSide * 2;
        int sepW = menuWidth - spaceSide * 4 - ui(8);
        graphics.fill(sepX, sepY, sepX + sepW, sepY + ui(4), 0xFFD68F54);

        // --- Skill bars ---
        int drawX = menuX + borderWidth + spaceTop + ui(256 - 8);
        int drawY = menuY + spaceTop + borderWidth - ui(8);
        int verticalSpacing = ui(SKILLS_VERTICAL_SPACING);
        int addedX = 0;

        // Reset hover state
        skillsHoverText = "";
        skillsHoverTitle = "";
        skillsHoveredProfessionId = -1;

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 5; j++) {
                SkillType skill = SKILLS_PAGE_ROW_ORDER[j];
                int skillLevel = ClientPlayerDataCache.getSkillLevel(skill);
                boolean drawRed = skillLevel > i;

                // Draw skill name + icon on the first column only
                if (i == 0) {
                    String skillName = Component.translatable(SKILL_NAME_KEYS[j]).getString();
                    Component boldSkillName = Component.literal(skillName).withStyle(ChatFormatting.BOLD);
                    float skillNameScale = sdvTextScale() * 0.9f;
                    int skillNameRawW = this.font.width(boldSkillName);
                    int skillNameMaxW = ui(100); // max width for skill name area
                    float skillNameEffScale = skillNameScale;
                    if (skillNameRawW * skillNameEffScale > skillNameMaxW) {
                        skillNameEffScale = (float) skillNameMaxW / skillNameRawW;
                    }
                    int skillNameScaledW = Math.round(skillNameRawW * skillNameEffScale);
                    int nameX = drawX - skillNameScaledW + ui(4) - ui(64);
                    int nameY = drawY + ui(4) + j * verticalSpacing;
                    graphics.pose().pushPose();
                    graphics.pose().translate(nameX, nameY, 0);
                    graphics.pose().scale(skillNameEffScale, skillNameEffScale, 1.0f);
                    graphics.drawString(this.font, boldSkillName, 0, 0, 0xFF5B3A1A, false);
                    graphics.pose().popPose();

                    // Skill icon - shadow first, then normal
                    int[] iconSrc = SKILL_ICON_SOURCES[j];
                    int iconShadowX = drawX - ui(56);
                    int iconShadowY = drawY + j * verticalSpacing;
                    StardewGuiUtil.drawFromCursorsTint(graphics, iconShadowX, iconShadowY,
                        iconSrc[0], iconSrc[1], iconSrc[2], iconSrc[3], s4,
                        0.0f, 0.0f, 0.0f, 0.3f);
                    int iconX = drawX - ui(52);
                    int iconY = drawY - ui(4) + j * verticalSpacing;
                    StardewGuiUtil.drawFromCursors(graphics, iconX, iconY,
                        iconSrc[0], iconSrc[1], iconSrc[2], iconSrc[3], s4);

                    // Hover detection for skill name area
                    int areaX = drawX - ui(128) - ui(48);
                    int areaY = drawY + j * verticalSpacing;
                    int areaW = ui(148);
                    int areaH = ui(36);
                    if (mouseX >= areaX && mouseX < areaX + areaW && mouseY >= areaY && mouseY < areaY + areaH) {
                        if (skillLevel > 0) {
                            skillsHoverTitle = skillName;
                            String[] hoverKeys = SKILL_HOVER_KEYS[j];
                            StringBuilder sb = new StringBuilder();
                            for (int h = 0; h < hoverKeys.length; h++) {
                                if (h > 0) sb.append("\n");
                                sb.append(Component.translatable(hoverKeys[h], skillLevel).getString());
                            }
                            skillsHoverText = sb.toString();
                        }
                    }
                }

                // Draw bar
                if ((i + 1) % 5 != 0) {
                    // Small bar (non-5th): unlit=(129,338,8,9), lit=(137,338,8,9)
                    // Shadow
                    StardewGuiUtil.drawFromCursorsTint(graphics,
                        addedX + drawX - ui(4) + i * ui(36),
                        drawY + j * verticalSpacing,
                        129, 338, 8, 9, s4, 0.0f, 0.0f, 0.0f, 0.35f);
                    // Bar
                    StardewGuiUtil.drawFromCursors(graphics,
                        addedX + drawX + i * ui(36),
                        drawY - ui(4) + j * verticalSpacing,
                        drawRed ? 137 : 129, 338, 8, 9, s4,
                        drawRed ? 1.0f : 0.65f);
                } else {
                    // Big bar (every 5th): unlit=(145,338,14,9), lit=(159,338,14,9)
                    if (!drawRed) {
                        // Shadow for unlit big bar
                        StardewGuiUtil.drawFromCursorsTint(graphics,
                            addedX + drawX - ui(4) + i * ui(36),
                            drawY + j * verticalSpacing,
                            145, 338, 14, 9, s4, 0.0f, 0.0f, 0.0f, 0.35f);
                        // Unlit big bar
                        StardewGuiUtil.drawFromCursors(graphics,
                            addedX + drawX + i * ui(36),
                            drawY - ui(4) + j * verticalSpacing,
                            145, 338, 14, 9, s4, 0.65f);
                    } else {
                        // Lit big bar (profession unlocked) - shadow first (SDV skillBars drawShadow:true)
                        StardewGuiUtil.drawFromCursorsTint(graphics,
                            addedX + drawX - ui(4) + i * ui(36),
                            drawY + j * verticalSpacing,
                            145, 338, 14, 9, s4, 0.0f, 0.0f, 0.0f, 0.35f);
                        StardewGuiUtil.drawFromCursors(graphics,
                            addedX + drawX + i * ui(36),
                            drawY - ui(4) + j * verticalSpacing,
                            159, 338, 14, 9, s4);

                        // Profession hover detection for boxes at level 5 and 10
                        int boxX = addedX + drawX - ui(4) + i * ui(36);
                        int boxY = drawY + j * verticalSpacing;
                        int boxW = ui(56);
                        int boxH = ui(36);
                        if (mouseX >= boxX && mouseX < boxX + boxW && mouseY >= boxY && mouseY < boxY + boxH) {
                            // Find which profession the player chose at this level
                            int profLevel = i + 1; // 5 or 10
                            ProfessionType chosenProf = getChosenProfessionForRow(j, profLevel);
                            if (chosenProf != null) {
                                skillsHoverTitle = chosenProf.getDisplayName();
                                skillsHoverText = Component.translatable(
                                    "stardewcraft.profession." + chosenProf.getName() + ".desc").getString();
                                skillsHoveredProfessionId = chosenProf.getId();
                                skillsHoveredBarX = boxX;
                                skillsHoveredBarY = boxY;
                            }
                        }
                    }
                }

                // Draw level number after the last bar (i==9)
                if (i == 9) {
                    int numX = addedX + drawX + (i + 2) * ui(36) + ui(12) + (skillLevel >= 10 ? ui(12) : 0);
                    int numY = drawY + ui(16) + j * verticalSpacing;
                    // Shadow (offset: 0, +4 relative)
                    drawNumberSprite(graphics, skillLevel, numX, numY, 0x000000, 0.35f);
                    // Number (offset: +4, 0 relative to shadow base)
                    int numColor = 0xF4A460; // SandyBrown
                    float numAlpha = (skillLevel == 0) ? 0.75f : 1.0f;
                    drawNumberSprite(graphics, skillLevel,
                        numX + ui(4), numY - ui(4), numColor, numAlpha);
                }
            }
            if ((i + 1) % 5 == 0) {
                addedX += ui(24);
            }
        }

        // --- Profession icon popup on hover ---
        if (skillsHoveredProfessionId >= 0) {
            // SDV: IClickableMenu.drawTextureBox at (c.bounds.X - 16 - 8, c.bounds.Y - 16 - 16, 96, 96)
            int popupX = skillsHoveredBarX - ui(16) - ui(8);
            int popupY = skillsHoveredBarY - ui(16) - ui(16);
            StardewGuiUtil.drawTextureBox(graphics, popupX, popupY, ui(96), ui(96));
            // SDV: profession icon at (c.bounds.X - 8, c.bounds.Y - 32 + 16)
            int profIconU = (skillsHoveredProfessionId % 6) * 16;
            int profIconV = 624 + (skillsHoveredProfessionId / 6) * 16;
            StardewGuiUtil.drawFromCursors(graphics,
                skillsHoveredBarX - ui(8),
                skillsHoveredBarY - ui(32) + ui(16),
                profIconU, profIconV, 16, 16, s4);
        }

        // --- Lower section: horizontal separator already drawn above ---

        // --- Hover tooltip ---
        if (!skillsHoverText.isEmpty()) {
            drawSkillsTooltip(graphics, mouseX, mouseY, skillsHoverTitle, skillsHoverText);
        }
    }

    /**
     * Get the profession a player chose for a given skill row and level.
     * Row order: 0=Farming, 1=Mining, 2=Foraging, 3=Fishing, 4=Combat
     */
    private ProfessionType getChosenProfessionForRow(int rowIndex, int level) {
        SkillType skill = SKILLS_PAGE_ROW_ORDER[rowIndex];
        if (level == 5) {
            ProfessionType[] options = ProfessionType.getLevel5Options(skill);
            for (ProfessionType opt : options) {
                if (ClientPlayerDataCache.hasProfession(opt)) return opt;
            }
        } else if (level == 10) {
            // Need to know the lv5 choice to get lv10 options
            ProfessionType[] lv5Options = ProfessionType.getLevel5Options(skill);
            for (ProfessionType lv5 : lv5Options) {
                if (ClientPlayerDataCache.hasProfession(lv5)) {
                    ProfessionType[] lv10Options = ProfessionType.getLevel10Options(skill, lv5);
                    for (ProfessionType opt : lv10Options) {
                        if (ClientPlayerDataCache.hasProfession(opt)) return opt;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Draw SDV-style hover tooltip (matching IClickableMenu.drawHoverText)
     */
    private void drawSkillsTooltip(GuiGraphics graphics, int mouseX, int mouseY, String title, String text) {
        if (text.isEmpty() && title.isEmpty()) return;

        float ttScale = sdvTextScale();
        int padding = ui(16);
        int maxWidth = ui(300);
        int scaledLineH = Math.round(this.font.lineHeight * ttScale);

        // Calculate text dimensions (at scaled size)
        List<String> textLines = new ArrayList<>();
        if (!text.isEmpty()) {
            for (String line : text.split("\n")) {
                textLines.add(line);
            }
        }

        int textWidth = 0;
        for (String line : textLines) {
            textWidth = Math.max(textWidth, Math.round(this.font.width(line) * ttScale));
        }
        if (!title.isEmpty()) {
            textWidth = Math.max(textWidth, Math.round(this.font.width(title) * ttScale));
        }
        textWidth = Math.min(textWidth, maxWidth);

        int textHeight = textLines.size() * (scaledLineH + 2);
        if (!title.isEmpty()) {
            textHeight += scaledLineH + 4;
        }

        int boxW = textWidth + padding * 2;
        int boxH = textHeight + padding * 2;
        int boxX = mouseX + ui(32);
        int boxY = mouseY + ui(32);

        // Keep on screen
        if (boxX + boxW > this.width) boxX = mouseX - boxW;
        if (boxY + boxH > this.height) boxY = mouseY - boxH;
        if (boxX < 0) boxX = 0;
        if (boxY < 0) boxY = 0;

        // Draw tooltip background using SDV textureBox
        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 400);
        StardewGuiUtil.drawTextureBox(graphics, boxX, boxY, boxW, boxH);

        int contentX = boxX + padding;
        int contentY = boxY + padding;

        if (!title.isEmpty()) {
            graphics.pose().pushPose();
            graphics.pose().translate(contentX, contentY, 0);
            graphics.pose().scale(ttScale, ttScale, 1.0f);
            graphics.drawString(this.font, Component.literal(title).withStyle(ChatFormatting.BOLD), 0, 0, 0xFF5B3A1A, false);
            graphics.pose().popPose();
            contentY += scaledLineH + 4;
        }

        for (String line : textLines) {
            graphics.pose().pushPose();
            graphics.pose().translate(contentX, contentY, 0);
            graphics.pose().scale(ttScale, ttScale, 1.0f);
            graphics.drawString(this.font, Component.literal(line).withStyle(ChatFormatting.BOLD), 0, 0, 0xFF5B3A1A, false);
            graphics.pose().popPose();
            contentY += scaledLineH + 2;
        }
        graphics.pose().popPose();
    }

    // ============ Tab 0: Inventory Page (SDV InventoryPage 1:1 parity) ============

    // --- Grid layout (top section, SDV pixels from menu origin) ---
    private static final int INV_PAGE_ROW0_Y = 36;      // First main-inventory row
    private static final int INV_PAGE_ROW_STEP = 68;     // 64 slot + 4 gap
    private static final int INV_PAGE_HOTBAR_Y = 252;    // Hotbar row (extra gap above)
    private static final int INV_PAGE_PARTITION_Y = 324;  // Horizontal partition

    // --- Equipment slots (lower-left) ---
    private static final int INV_PAGE_EQUIP_X = 48;
    private static final int INV_PAGE_EQUIP_SIZE = 64;
    private static final int INV_PAGE_EQUIP_Y0 = 356;    // Left Ring
    private static final int INV_PAGE_EQUIP_Y1 = 420;    // Right Ring
    private static final int INV_PAGE_EQUIP_Y2 = 484;    // Boots

    // Empty-slot placeholder tiles (from menu_tiles.png, SDV: getSourceRectForStandardTileSheet)
    private static final int EMPTY_RING_TILE = 41;
    private static final int EMPTY_BOOTS_TILE = 40;

    // --- Player model area (lower-center, SDV: x=120, y=296 from menu origin) ---
    private static final int INV_PAGE_PLAYER_BG_X = 120;
    private static final int INV_PAGE_PLAYER_BG_Y = 356;
    private static final int PLAYER_BG_TEX_W = 128;
    private static final int PLAYER_BG_TEX_H = 192;
    private static final int INV_PAGE_PLAYER_NAME_Y = 556;

    // --- Right info panel (lower-right, text centered at this X) ---
    private static final int INV_PAGE_INFO_CENTER_X = 576;
    private static final int INV_PAGE_INFO_Y0 = 364;     // Farm name
    private static final int INV_PAGE_INFO_Y1 = 428;     // Current funds
    private static final int INV_PAGE_INFO_Y2 = 492;     // Season / day

    // SDV text color: new Color(86, 22, 12)
    private static final int SDV_TEXT_COLOR = 0xFF56160C;
    private static final int SDV_TEXT_SHADOW = 0xFF2D0B06;
    private static final int SDV_TEXT_COLOR_DIM = 0xFF45120A; // textColor * 0.8

    // Day/night player backgrounds (extracted from SDV LooseSprites)
    private static final ResourceLocation DAYBG =
            ResourceLocation.fromNamespaceAndPath("stardewcraft", "textures/gui/daybg.png");
    private static final ResourceLocation NIGHTBG =
            ResourceLocation.fromNamespaceAndPath("stardewcraft", "textures/gui/nightbg.png");

    // Organize button (cursors.png UV)
    private static final int ORGANIZE_U = 162;
    private static final int ORGANIZE_V = 440;
    private static final int ORGANIZE_ICON_SIZE = 16;

    // ────────────────────────────────────────────────────────────────

    private void drawInventoryPage(GuiGraphics graphics, int mouseX, int mouseY) {
        Minecraft mc = this.minecraft;
        if (mc == null || mc.player == null) return;

        // ── 1. Inventory grid (top section, SDV-like) ──
        drawInvPageGrid(graphics, mouseX, mouseY);

        // ── 2. Horizontal partition (SDV: drawHorizontalPartition) ──
        StardewGuiUtil.drawHorizontalPartition(graphics, menuX, menuY + ui(INV_PAGE_PARTITION_Y),
                menuWidth, mapping.s4());

        // ── 3. Equipment slots (lower-left, SDV placeholder icons) ──
        String leftRingId = ClientPlayerDataCache.getEquippedLeftRing();
        String rightRingId = ClientPlayerDataCache.getEquippedRightRing();
        String bootsId = ClientPlayerDataCache.getEquippedBoots();
        drawInvEquipSlot(graphics, 0, leftRingId, mouseX, mouseY);
        drawInvEquipSlot(graphics, 1, rightRingId, mouseX, mouseY);
        drawInvEquipSlot(graphics, 2, bootsId, mouseX, mouseY);

        // ── 4. Player model with day/night background ──
        drawPlayerModelArea(graphics, mouseX, mouseY);

        // ── 5. Right info panel (farm name / money / date) ──
        drawInfoPanel(graphics);

        // ── 6. Organize button (SDV: organizeButton) ──
        drawOrganizeButton(graphics, mouseX, mouseY);

        // ── 7. Trash can ──
        drawTrashCan(graphics, mouseX, mouseY);
    }

    // ------------- Inventory grid (top section) -------------

    private void drawInvPageGrid(GuiGraphics graphics, int mouseX, int mouseY) {
        Minecraft mc = this.minecraft;
        if (mc == null || mc.player == null) return;

        // 3 main rows (MC slots 9-35)
        for (int row = 0; row < 3; row++) {
            int rowY = menuY + ui(INV_PAGE_ROW0_Y + row * INV_PAGE_ROW_STEP);
            for (int col = 0; col < INVENTORY_COLS; col++) {
                int invIndex = col + row * 9 + 9;
                int x = invPageSlotX(col);
                boolean hovered = mouseX >= x && mouseX < x + ui(INVENTORY_SLOT_SDV)
                        && mouseY >= rowY && mouseY < rowY + ui(INVENTORY_SLOT_SDV);
                drawInventorySlot(graphics, x, rowY,
                        mc.player.getInventory().getItem(invIndex), hovered);
            }
        }

        // Hotbar row (MC slots 0-8, with extra gap — matches SDV's separated toolbar)
        int hotbarY = menuY + ui(INV_PAGE_HOTBAR_Y);
        for (int col = 0; col < INVENTORY_COLS; col++) {
            int x = invPageSlotX(col);
            boolean hovered = mouseX >= x && mouseX < x + ui(INVENTORY_SLOT_SDV)
                    && mouseY >= hotbarY && mouseY < hotbarY + ui(INVENTORY_SLOT_SDV);
            drawInventorySlot(graphics, x, hotbarY,
                    mc.player.getInventory().getItem(col), hovered);
        }
    }

    private int invPageSlotX(int col) {
        int startX = menuX + (menuWidth - inventoryGridWidth()) / 2;
        return startX + col * ui(INVENTORY_SLOT_SDV + INVENTORY_GAP_SDV);
    }

    private int invPageHoveredSlot(double mouseX, double mouseY) {
        for (int row = 0; row < 3; row++) {
            int rowY = menuY + ui(INV_PAGE_ROW0_Y + row * INV_PAGE_ROW_STEP);
            for (int col = 0; col < INVENTORY_COLS; col++) {
                int x = invPageSlotX(col);
                if (mouseX >= x && mouseX < x + ui(INVENTORY_SLOT_SDV)
                        && mouseY >= rowY && mouseY < rowY + ui(INVENTORY_SLOT_SDV)) {
                    return col + row * 9 + 9;
                }
            }
        }
        int hotbarY = menuY + ui(INV_PAGE_HOTBAR_Y);
        for (int col = 0; col < INVENTORY_COLS; col++) {
            int x = invPageSlotX(col);
            if (mouseX >= x && mouseX < x + ui(INVENTORY_SLOT_SDV)
                    && mouseY >= hotbarY && mouseY < hotbarY + ui(INVENTORY_SLOT_SDV)) {
                return col;
            }
        }
        return -1;
    }

    // ------------- Equipment slots (lower-left) -------------

    private int invEquipSlotY(int index) {
        return menuY + ui(index == 0 ? INV_PAGE_EQUIP_Y0
                : index == 1 ? INV_PAGE_EQUIP_Y1 : INV_PAGE_EQUIP_Y2);
    }

    private void drawInvEquipSlot(GuiGraphics graphics, int index, String itemId,
                                   int mouseX, int mouseY) {
        int x = menuX + ui(INV_PAGE_EQUIP_X);
        int y = invEquipSlotY(index);
        int size = ui(INV_PAGE_EQUIP_SIZE);
        boolean hasItem = !itemId.isEmpty();
        boolean hovered = mouseX >= x && mouseX < x + size
                && mouseY >= y && mouseY < y + size;

        if (hasItem) {
            // SDV: filled slot uses tile 10 + item drawn on top
            StardewGuiUtil.drawMenuTileIndex(graphics, x, y, size, size, 10);
            ResourceLocation rl = ResourceLocation.tryParse(itemId);
            if (rl != null) {
                Item item = BuiltInRegistries.ITEM.get(rl);
                ItemStack stack = new ItemStack(item);
                if (!stack.isEmpty()) {
                    int mcSize = 16;
                    int ox = (size - mcSize) / 2;
                    int oy = (size - mcSize) / 2;
                    graphics.renderItem(stack, x + ox, y + oy);
                }
            }
        } else {
            // SDV: empty ring → tile 41, empty boots → tile 40
            int placeholderTile = (index <= 1) ? EMPTY_RING_TILE : EMPTY_BOOTS_TILE;
            StardewGuiUtil.drawMenuTileIndex(graphics, x, y, size, size, placeholderTile);
        }

        if (hovered) {
            graphics.pose().pushPose();
            graphics.pose().translate(0, 0, 100);
            graphics.fill(x, y, x + size, y + size, 0x35FFFFFF);
            graphics.pose().popPose();
        }
    }

    private int invPageHoveredEquip(double mouseX, double mouseY) {
        int size = ui(INV_PAGE_EQUIP_SIZE);
        int x = menuX + ui(INV_PAGE_EQUIP_X);
        for (int i = 0; i < 3; i++) {
            int y = invEquipSlotY(i);
            if (mouseX >= x && mouseX < x + size && mouseY >= y && mouseY < y + size) {
                return i;
            }
        }
        return -1;
    }

    // ------------- Player model area (lower-center) -------------

    private void drawPlayerModelArea(GuiGraphics graphics, int mouseX, int mouseY) {
        Minecraft mc = this.minecraft;
        if (mc == null || mc.player == null) return;

        int bgX = menuX + ui(INV_PAGE_PLAYER_BG_X);
        int bgY = menuY + ui(INV_PAGE_PLAYER_BG_Y);
        int bgW = ui(PLAYER_BG_TEX_W);
        int bgH = ui(PLAYER_BG_TEX_H);

        // SDV: Game1.timeOfDay >= 1900 → nightbg, else daybg
        long dayTime = mc.level != null ? mc.level.getDayTime() % 24000 : 0;
        boolean isNight = dayTime >= 13000;
        ResourceLocation bgTex = isNight ? NIGHTBG : DAYBG;
        graphics.blit(bgTex, bgX, bgY, 0, 0, bgW, bgH, bgW, bgH);

        // Render MC player entity inside the background frame
        int margin = ui(8);
        net.minecraft.client.gui.screens.inventory.InventoryScreen
                .renderEntityInInventoryFollowsMouse(
                        graphics,
                        bgX + margin, bgY + margin,
                        bgX + bgW - margin, bgY + bgH - margin,
                        ui(30), 0.0625F,
                        (float) mouseX, (float) mouseY,
                        mc.player
                );

        // SDV: player name centered below
        String playerName = mc.player.getName().getString();
        Component boldName = Component.literal(playerName).withStyle(ChatFormatting.BOLD);
        float nameScale = sdvTextScale();
        int nameRawW = this.font.width(boldName);
        float nameEffScale = nameScale;
        if (nameRawW * nameEffScale > bgW) {
            nameEffScale = (float) bgW / nameRawW;
        }
        int nameScaledW = Math.round(nameRawW * nameEffScale);
        int nameX = bgX + (bgW - nameScaledW) / 2;
        int nameY = menuY + ui(INV_PAGE_PLAYER_NAME_Y);
        graphics.pose().pushPose();
        graphics.pose().translate(nameX, nameY, 0);
        graphics.pose().scale(nameEffScale, nameEffScale, 1.0f);
        graphics.drawString(this.font, boldName, 0, 0, SDV_TEXT_COLOR, false);
        graphics.pose().popPose();
    }

    // ------------- Right info panel (lower-right) -------------

    /** Compute text scale that makes MC font (9px) match SDV SpriteText proportions. */
    private float sdvTextScale() {
        return Math.max(1.0f, (float) ui(24) / 9.0f);
    }

    private void drawInfoPanel(GuiGraphics graphics) {
        Minecraft mc = this.minecraft;
        if (mc == null || mc.player == null) return;

        int centerX = menuX + ui(INV_PAGE_INFO_CENTER_X);
        float textScale = sdvTextScale();
        // Available width: from player model right edge to menu right border
        int maxHalfWidth = menuX + menuWidth - ui(BORDER_WIDTH) - centerX;
        int maxWidth = maxHalfWidth * 2;

        // SDV: "{farmName} Farm"
        String farmName = net.minecraft.client.resources.language.I18n.get(
                "stardewcraft.game_menu.inventory.farm_name", mc.player.getName().getString());
        drawScaledCenteredSdvText(graphics, farmName, centerX,
                menuY + ui(INV_PAGE_INFO_Y0), textScale, maxWidth, SDV_TEXT_COLOR);

        // SDV: "Current Funds: {amount}g"
        int money = ClientPlayerDataCache.getMoney();
        String fundsStr = net.minecraft.client.resources.language.I18n.get(
                "stardewcraft.game_menu.inventory.current_funds",
                String.format("%,d", money));
        drawScaledCenteredSdvText(graphics, fundsStr, centerX,
                menuY + ui(INV_PAGE_INFO_Y1), textScale, maxWidth, SDV_TEXT_COLOR);

        // SDV: date string
        com.stardew.craft.time.StardewTimeManager time =
                com.stardew.craft.client.hud.StardewTimeHud.getClientTimeCache();
        String dateStr = net.minecraft.client.resources.language.I18n.get(
                "stardewcraft.game_menu.inventory.date",
                time.getSeasonName(), time.getCurrentDay(), time.getCurrentYear());
        drawScaledCenteredSdvText(graphics, dateStr, centerX,
                menuY + ui(INV_PAGE_INFO_Y2), textScale, maxWidth, SDV_TEXT_COLOR_DIM);
    }

    /**
     * Draw text centered at {@code centerX}, scaled up to SDV proportions.
     * Auto-shrinks if the text would exceed {@code maxWidth} pixels.
     */
    private void drawScaledCenteredSdvText(GuiGraphics graphics, String text,
                                            int centerX, int y, float scale,
                                            int maxWidth, int color) {
        Component bold = Component.literal(text).withStyle(ChatFormatting.BOLD);
        int rawWidth = this.font.width(bold);
        float effectiveScale = scale;
        if (rawWidth * effectiveScale > maxWidth) {
            effectiveScale = (float) maxWidth / rawWidth;
        }
        int scaledWidth = Math.round(rawWidth * effectiveScale);
        int x = centerX - scaledWidth / 2;
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        graphics.pose().scale(effectiveScale, effectiveScale, 1.0f);
        graphics.drawString(this.font, bold, 0, 0, color, false);
        graphics.pose().popPose();
    }

    // ------------- Organize button -------------

    private int organizeButtonX() {
        return menuX + menuWidth;
    }

    private int organizeButtonY() {
        return menuY + ui(INV_PAGE_PARTITION_Y) + ui(20);
    }

    private void drawOrganizeButton(GuiGraphics graphics, int mouseX, int mouseY) {
        int x = organizeButtonX();
        int y = organizeButtonY();
        // SDV: ClickableTextureComponent draws at scale 4, icon 16×16
        StardewGuiUtil.drawFromCursors(graphics, x, y,
                ORGANIZE_U, ORGANIZE_V, ORGANIZE_ICON_SIZE, ORGANIZE_ICON_SIZE,
                mapping.s4());
    }

    private boolean organizeButtonContains(double mouseX, double mouseY) {
        int x = organizeButtonX();
        int y = organizeButtonY();
        int size = ui(64); // 16 * 4 = 64 SDV pixels
        return mouseX >= x && mouseX < x + size && mouseY >= y && mouseY < y + size;
    }

    // ------------- Inventory Page Tooltips -------------

    private void drawInventoryPageTooltips(GuiGraphics graphics, int mouseX, int mouseY) {
        // Equipment slot tooltips
        int equipSlot = invPageHoveredEquip(mouseX, mouseY);
        if (equipSlot >= 0) {
            String itemId = switch (equipSlot) {
                case 0 -> ClientPlayerDataCache.getEquippedLeftRing();
                case 1 -> ClientPlayerDataCache.getEquippedRightRing();
                case 2 -> ClientPlayerDataCache.getEquippedBoots();
                default -> "";
            };
            if (!itemId.isEmpty()) {
                ResourceLocation rl = ResourceLocation.tryParse(itemId);
                if (rl != null) {
                    Item item = BuiltInRegistries.ITEM.get(rl);
                    ItemStack stack = new ItemStack(item);
                    if (!stack.isEmpty()) {
                        graphics.renderTooltip(this.font, stack, mouseX, mouseY);
                        return;
                    }
                }
            }
            Component label = switch (equipSlot) {
                case 0 -> Component.translatable("stardewcraft.equipment.slot.left_ring");
                case 1 -> Component.translatable("stardewcraft.equipment.slot.right_ring");
                case 2 -> Component.translatable("stardewcraft.equipment.slot.boots");
                default -> Component.empty();
            };
            graphics.renderTooltip(this.font, label, mouseX, mouseY);
            return;
        }

        // Inventory item tooltips (use invPage positions)
        int invSlot = invPageHoveredSlot(mouseX, mouseY);
        if (invSlot >= 0 && this.minecraft != null && this.minecraft.player != null) {
            ItemStack stack = this.minecraft.player.getInventory().getItem(invSlot);
            if (!stack.isEmpty()) {
                graphics.renderTooltip(this.font, stack, mouseX, mouseY);
                return;
            }
        }

        // Organize button tooltip
        if (organizeButtonContains(mouseX, mouseY)) {
            graphics.renderTooltip(this.font,
                    Component.translatable("stardewcraft.game_menu.inventory.organize"),
                    mouseX, mouseY);
            return;
        }

        // Trash can tooltip
        if (trashCanContains(mouseX, mouseY)) {
            ItemStack carried = currentCarriedItem();
            if (!carried.isEmpty()) {
                List<Component> lines = new ArrayList<>();
                lines.add(Component.translatable("stardewcraft.game_menu.crafting.trash_can").withStyle(ChatFormatting.WHITE));
                lines.add(carried.getHoverName().copy().withStyle(ChatFormatting.GRAY));
                graphics.renderTooltip(this.font, lines, java.util.Optional.empty(), mouseX, mouseY);
                return;
            }
            graphics.renderTooltip(this.font,
                    Component.translatable("stardewcraft.game_menu.crafting.trash_can"),
                    mouseX, mouseY);
        }
    }

    private void drawSocialPage(GuiGraphics graphics, int mouseX, int mouseY) {
        List<NpcFriendshipClientCache.Entry> all = visibleSocialEntries();
        int total = all.size();
        socialScroll = Mth.clamp(socialScroll, 0, socialMaxScroll(total));

        // Exact vanilla separator style for SocialPage: small horizontal + small vertical partitions.
        for (int i = 0; i < 4; i++) {
            StardewGuiUtil.drawHorizontalPartitionSmall(
                graphics,
                socialTuneHorizontalLineX(i),
                socialTuneHorizontalLineY(i),
                socialPageWidth(),
                mapping.s4()
            );
        }

        drawSocialVerticalPartitions(graphics);

        if (!all.isEmpty()) {
            int toIndex = Math.min(total, socialScroll + SOCIAL_MAX_VISIBLE);
            for (int i = socialScroll; i < toIndex; i++) {
                int visibleRow = i - socialScroll;
                drawSocialRow(graphics, all.get(i), visibleRow);
            }
        }

        drawSocialScrollControls(graphics, total);
    }

    private int socialVerticalPartitionClipTopY() {
        // Vanilla rowPosition(numFarmers - 1) resolves to y + 92 when no farmers are listed.
        return Math.max(0, menuY + ui(92));
    }

    private void drawSocialVerticalPartitions(GuiGraphics graphics) {
        int clipTop = socialVerticalPartitionClipTopY();
        int clipBottom = Math.min(this.height, menuY + menuHeight);
        if (clipBottom <= clipTop) {
            return;
        }

        graphics.enableScissor(0, clipTop, this.width, clipBottom);
        try {
            StardewGuiUtil.drawVerticalPartitionSmall(graphics, socialVerticalLeftX(), menuY, menuHeight, mapping.s4());
            StardewGuiUtil.drawVerticalPartitionSmall(graphics, socialVerticalMiddleX(), menuY, menuHeight, mapping.s4());
            StardewGuiUtil.drawVerticalPartitionSmall(graphics, socialVerticalRightX(), menuY, menuHeight, mapping.s4());
        } finally {
            graphics.disableScissor();
        }
    }

    private void drawSocialRow(GuiGraphics graphics,
                               NpcFriendshipClientCache.Entry entry,
                               int visibleRow) {
        int y = socialRowPosition(visibleRow);
        boolean datable = DATEABLE_NPCS.contains(normalizeNpcId(entry.npcId()));

        String name = formatNpcName(entry.npcId());
        drawSocialPortrait(graphics, entry.npcId(), socialPortraitX(), y);
        // Name centered horizontally between portrait right edge and left partition line
        int nameX = socialNameCenterX() - this.font.width(name) / 2;
        int nameY = y + ui(datable ? 24 : 28);
        graphics.drawString(this.font, name, nameX, nameY, 0xFF7B5A2F, false);

        drawSocialHearts(graphics, entry, y);
        drawSocialGiftAndTalkMarkers(graphics, entry, y);
    }

    private int socialLeftColumnStartX() {
        return menuX + ui(BORDER_WIDTH);
    }

    private int socialLeftColumnEndX() {
        return socialVerticalLeftX();
    }

    private int socialHeartsColumnStartX() {
        return socialVerticalLeftX() + ui(64);
    }

    private int socialHeartsColumnEndX() {
        return socialVerticalMiddleX();
    }

    private int socialGiftColumnStartX() {
        return socialVerticalMiddleX() + ui(64);
    }

    private int socialGiftColumnEndX() {
        return socialVerticalRightX();
    }

    private int socialTalkColumnStartX() {
        return socialVerticalRightX() + ui(64);
    }

    private int socialTalkColumnEndX() {
        return socialPageRightX() - ui(BORDER_WIDTH);
    }

    private int socialRowPosition(int index) {
        return menuY + ui(64 + index * SOCIAL_ROW_HEIGHT_SDV);
    }

    private int socialContentTopY() {
        return socialVerticalStartY();
    }

    private void drawSocialHearts(GuiGraphics graphics, NpcFriendshipClientCache.Entry entry, int rowY) {
        int points = Math.max(0, entry.points());
        int fullHearts = Math.max(0, Math.min(14, points / 250));
        int maxHearts = Math.max(10, fullHearts);
        boolean datableLocked = isDatableHeartLocked(entry);
        for (int hearts = 0; hearts < maxHearts; hearts++) {
            boolean isLockedHeart = datableLocked && hearts >= 8;
            int u = (hearts < fullHearts || isLockedHeart) ? 211 : 218;
            int drawX;
            int drawY;
            if (hearts < 10) {
                drawX = socialHeartsBaseX() + ui(hearts * 32);
                drawY = rowY + socialHeartsTopRowOffsetY();
            } else {
                drawX = socialHeartsBaseX() + ui((hearts - 10) * 32);
                drawY = rowY + socialHeartsBottomRowOffsetY();
            }
            if (isLockedHeart && hearts < 10) {
                StardewGuiUtil.drawFromCursorsTint(graphics, drawX, drawY, u, 428, 7, 6, mapping.s4(), 0.0f, 0.0f, 0.0f, 0.35f);
            } else {
                StardewGuiUtil.drawFromCursors(graphics, drawX, drawY, u, 428, 7, 6, mapping.s4(), 0.88f);
            }
        }

        drawSocialHeartPartialFill(graphics, rowY, points, fullHearts, maxHearts);
    }

    private void drawSocialHeartPartialFill(GuiGraphics graphics, int rowY, int points, int fullHearts, int maxHearts) {
        if (points <= 0 || fullHearts >= maxHearts) {
            return;
        }

        int pointsToNextHeart = points % 250;
        if (pointsToNextHeart <= 0) {
            return;
        }

        int activeHeart = fullHearts;
        int drawX;
        int drawY;
        if (activeHeart < 10) {
            drawX = socialHeartsBaseX() + ui(activeHeart * 32);
            drawY = rowY + socialHeartsTopRowOffsetY();
        } else {
            drawX = socialHeartsBaseX() + ui((activeHeart - 10) * 32);
            drawY = rowY + socialHeartsBottomRowOffsetY();
        }

        int filledCells = Math.max(0, Math.min(20, (int) (pointsToNextHeart / 12.5f)));
        if (filledCells <= 0) {
            return;
        }

        int cell = Math.max(1, ui(4));
        int offsetX = ui(2);
        int offsetY = ui(2);
        int remaining = filledCells;

        for (int row = 3; row >= 0 && remaining > 0; row--) {
            for (int col = 0; col < 5 && remaining > 0; col++) {
                if (SOCIAL_HEART_FILL_PATTERN[row][col] != 1) {
                    continue;
                }
                int x = drawX + offsetX + col * cell;
                int y = drawY + offsetY + row * cell;
                graphics.fill(x, y, x + cell, y + cell, 0xFFDC143C);
                remaining--;
            }
        }
    }

    private void drawSocialGiftAndTalkMarkers(GuiGraphics graphics, NpcFriendshipClientCache.Entry entry, int rowY) {
        StardewGuiUtil.drawFromCursors2(graphics, socialGiftIconX(), rowY + socialGiftIconOffsetY(), 166, 174, 14, 12, mapping.s4(), 0.88f);
        StardewGuiUtil.drawFromCursors(graphics, socialGiftSecondBoxX(), rowY + socialGiftBoxesOffsetY(), 227 + (entry.giftsThisWeek() >= 2 ? 9 : 0), 425, 9, 9, mapping.s4(), 0.88f);
        StardewGuiUtil.drawFromCursors(graphics, socialGiftFirstBoxX(), rowY + socialGiftBoxesOffsetY(), 227 + (entry.giftsThisWeek() >= 1 ? 9 : 0), 425, 9, 9, mapping.s4(), 0.88f);

        StardewGuiUtil.drawFromCursors2(graphics, socialTalkIconX(), rowY + socialTalkIconOffsetY(), 180, 175, 13, 11, mapping.s4(), 0.88f);
        StardewGuiUtil.drawFromCursors(graphics, socialTalkBoxX(), rowY + socialTalkBoxOffsetY(), 227 + (entry.talkedToday() ? 9 : 0), 425, 9, 9, mapping.s4(), 0.88f);
    }

    // ─── Vanilla-derived absolute positions (relative to menuX/menuY) ───
    // Match original SocialPage column cuts directly: 268, 620, 752.
    private int socialPortraitX() { return menuX + ui(BORDER_WIDTH + 4); }

    private int socialNameCenterX() { return menuX + ui(188); }

    private int socialVerticalLeftX() { return socialTuneVerticalLineX(SOCIAL_TUNE_TARGET_VLINE_LEFT); }
    private int socialVerticalMiddleX() { return socialTuneVerticalLineX(SOCIAL_TUNE_TARGET_VLINE_MIDDLE); }
    private int socialVerticalRightX() { return socialTuneVerticalLineX(SOCIAL_TUNE_TARGET_VLINE_RIGHT); }

    private int socialHeartsBaseX() { return menuX + ui(316); }

    private int socialHeartsTopRowOffsetY() { return ui(36); }
    private int socialHeartsBottomRowOffsetY() { return ui(64); }

    private int socialGiftIconX() { return menuX + ui(688); }

    private int socialGiftIconOffsetY() { return -ui(4); }

    private int socialGiftFirstBoxX() { return menuX + ui(680); }

    private int socialGiftSecondBoxX() { return menuX + ui(720); }

    private int socialGiftBoxesOffsetY() { return ui(52); }

    private int socialTalkIconX() { return menuX + ui(808); }

    private int socialTalkIconOffsetY() { return 0; }

    private int socialTalkBoxX() { return menuX + ui(816); }

    private int socialTalkBoxOffsetY() { return ui(52); }

    private int socialVerticalStartY() { return menuY + ui(BORDER_WIDTH + 100); }

    private int socialContentLeftX() {
        return menuX + ui(BORDER_WIDTH);
    }

    private int socialContentRightX() {
        return socialPageRightX() - ui(BORDER_WIDTH);
    }

    private int socialContentWidth() {
        return Math.max(1, socialContentRightX() - socialContentLeftX());
    }

    private void drawSocialScrollControls(GuiGraphics graphics, int total) {
        int upX = socialUpButtonX();
        int upY = socialUpButtonY();
        int downX = socialDownButtonX();
        int downY = socialDownButtonY();
        int maxScroll = socialMaxScroll(total);

        if (socialScroll > 0) {
            drawArrowFromCursors(graphics, upX, upY, 12, 0.8f);
        }
        if (socialScroll < maxScroll) {
            drawArrowFromCursors(graphics, downX, downY, 11, 0.8f);
        }

        StardewGuiUtil.drawTextureBox(
            graphics,
            StardewGuiUtil.CURSORS,
            StardewGuiUtil.CURSORS_WIDTH,
            StardewGuiUtil.CURSORS_HEIGHT,
            403,
            383,
            6,
            6,
            socialScrollRunnerX(),
            socialScrollRunnerY(),
            socialScrollRunnerWidth(),
            socialScrollRunnerHeight(),
            mapping.s4(),
            false
        );
        StardewGuiUtil.drawFromCursors(graphics, socialScrollBarX(), currentSocialScrollBarY(total), 435, 463, 6, 10, mapping.s4());
    }

    private int socialMaxScroll(int total) {
        return Math.max(0, total - SOCIAL_MAX_VISIBLE);
    }

    private int socialUpButtonX() {
        return socialPageRightX() + ui(16);
    }

    private int socialUpButtonY() {
        return menuY + ui(64);
    }

    private int socialDownButtonX() {
        return socialUpButtonX();
    }

    private int socialDownButtonY() {
        return menuY + menuHeight - ui(64);
    }

    private int socialArrowBoundWidth() {
        return ui(44);
    }

    private int socialArrowBoundHeight() {
        return ui(48);
    }

    private int socialScrollBarX() {
        return socialUpButtonX() + ui(12);
    }

    private int socialScrollBarY() {
        return socialUpButtonY() + socialArrowBoundHeight() + ui(4);
    }

    private int socialScrollBarWidth() {
        return ui(24);
    }

    private int socialScrollBarHeight() {
        return ui(40);
    }

    private int socialScrollRunnerX() {
        return socialScrollBarX();
    }

    private int socialScrollRunnerY() {
        return socialScrollBarY();
    }

    private int socialScrollRunnerWidth() {
        return socialScrollBarWidth();
    }

    private int socialScrollRunnerHeight() {
        return menuHeight - ui(128) - socialArrowBoundHeight() - ui(8);
    }

    private int currentSocialScrollBarY(int total) {
        if (total <= 0) {
            return socialScrollRunnerY();
        }
        int maxScroll = socialMaxScroll(total);
        int y = socialScrollRunnerHeight() / Math.max(1, total - SOCIAL_MAX_VISIBLE + 1) * socialScroll + socialUpButtonY() + socialArrowBoundHeight() + ui(4);
        if (maxScroll > 0 && socialScroll == maxScroll) {
            y = socialDownButtonY() - socialScrollBarHeight() - ui(4);
        }
        return y;
    }

    private boolean socialUpButtonContains(double mouseX, double mouseY) {
        return mouseX >= socialUpButtonX() && mouseX < socialUpButtonX() + socialArrowBoundWidth()
            && mouseY >= socialUpButtonY() && mouseY < socialUpButtonY() + socialArrowBoundHeight();
    }

    private boolean socialDownButtonContains(double mouseX, double mouseY) {
        return mouseX >= socialDownButtonX() && mouseX < socialDownButtonX() + socialArrowBoundWidth()
            && mouseY >= socialDownButtonY() && mouseY < socialDownButtonY() + socialArrowBoundHeight();
    }

    private boolean socialScrollBarContains(double mouseX, double mouseY, int total) {
        int y = currentSocialScrollBarY(total);
        return mouseX >= socialScrollBarX() && mouseX < socialScrollBarX() + socialScrollBarWidth()
            && mouseY >= y && mouseY < y + socialScrollBarHeight();
    }

    private boolean socialScrollRunnerContains(double mouseX, double mouseY) {
        return mouseX > socialPageRightX() && mouseX < socialPageRightX() + ui(128)
            && mouseY > menuY && mouseY < menuY + menuHeight
            && !socialDownButtonContains(mouseX, mouseY);
    }

    private void setSocialScrollFromMouse(double mouseY, int total) {
        int maxScroll = socialMaxScroll(total);
        if (maxScroll <= 0) {
            socialScroll = 0;
            return;
        }
        int minY = menuY + ui(68);
        int maxY = menuY + menuHeight - ui(64) - ui(12) - socialScrollBarHeight();
        int clampedY = Mth.clamp((int) Math.round(mouseY), minY, maxY);
        float percentage = (clampedY - socialScrollRunnerY()) / (float) Math.max(1, socialScrollRunnerHeight());
        socialScroll = Mth.clamp((int) (total * percentage), 0, maxScroll);
    }

    private String formatNpcName(String npcId) {
        if (npcId == null || npcId.isBlank()) {
            return "?";
        }
        String[] parts = npcId.trim().split("[_\\s]+");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part == null || part.isBlank()) {
                continue;
            }
            if (!sb.isEmpty()) {
                sb.append(' ');
            }
            sb.append(part.substring(0, 1).toUpperCase(Locale.ROOT));
            if (part.length() > 1) {
                sb.append(part.substring(1).toLowerCase(Locale.ROOT));
            }
        }
        return sb.isEmpty() ? npcId : sb.toString();
    }

    private void drawSocialPortrait(GuiGraphics graphics, String npcId, int x, int y) {
        PortraitResource portrait = resolveSocialMugshot(npcId);
        graphics.blit(portrait.texture(), x, y, ui(64), ui(96), 0, 0, 16, 24, portrait.sheetWidth(), portrait.sheetHeight());
    }

    private PortraitResource resolveSocialMugshot(String npcId) {
        String normalized = normalizeNpcId(npcId);
        ResourceLocation mugshotLocation = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/mugshots/" + normalized + ".png");
        return loadPortrait(mugshotLocation, 16, 24);
    }

    private List<NpcFriendshipClientCache.Entry> visibleSocialEntries() {
        List<NpcFriendshipClientCache.Entry> all = NpcFriendshipClientCache.entries();
        if (all.isEmpty()) {
            return all;
        }

        List<NpcFriendshipClientCache.Entry> filtered = new ArrayList<>(all.size());
        for (NpcFriendshipClientCache.Entry entry : all) {
            if (entry == null) {
                continue;
            }
            String npcId = normalizeNpcId(entry.npcId());
            if (hasSocialPortraitAndCharacter(npcId)) {
                filtered.add(entry);
            }
        }

        filtered.sort(Comparator
            .comparingInt(NpcFriendshipClientCache.Entry::points).reversed()
            .thenComparingInt(NpcFriendshipClientCache.Entry::metOrder)
            .thenComparing(entry -> displayNameForSort(entry.npcId()))
            .thenComparing(NpcFriendshipClientCache.Entry::npcId));
        return filtered;
    }

    private boolean hasSocialPortraitAndCharacter(String normalizedNpcId) {
        ResourceLocation portraitLocation = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/portraits/" + normalizedNpcId + ".png");
        if (!hasResource(portraitLocation)) {
            return false;
        }
        ResourceLocation mugshotLocation = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/mugshots/" + normalizedNpcId + ".png");
        return hasResource(mugshotLocation);
    }

    private String displayNameForSort(String npcId) {
        return formatNpcName(npcId).toLowerCase(Locale.ROOT);
    }

    private boolean isDatableHeartLocked(NpcFriendshipClientCache.Entry entry) {
        return DATEABLE_NPCS.contains(normalizeNpcId(entry.npcId()));
    }

    private String normalizeNpcId(String npcId) {
        if (npcId == null || npcId.isBlank()) {
            return "lewis";
        }
        return npcId.trim().toLowerCase(Locale.ROOT);
    }

    private boolean hasResource(ResourceLocation location) {
        ResourceManager resourceManager = this.minecraft == null ? null : this.minecraft.getResourceManager();
        return resourceManager != null && resourceManager.getResource(location).isPresent();
    }

    private PortraitResource loadPortrait(ResourceLocation location, int fallbackW, int fallbackH) {
        PortraitResource cached = SOCIAL_PORTRAIT_CACHE.get(location);
        if (cached != null) {
            return cached;
        }

        ResourceManager resourceManager = this.minecraft == null ? null : this.minecraft.getResourceManager();
        if (resourceManager == null) {
            return new PortraitResource(location, fallbackW, fallbackH);
        }

        int width = fallbackW;
        int height = fallbackH;
        try {
            var resource = resourceManager.getResource(location).orElse(null);
            if (resource != null) {
                try (var stream = resource.open(); NativeImage image = NativeImage.read(stream)) {
                    width = image.getWidth();
                    height = image.getHeight();
                }
            }
        } catch (IOException ignored) {
        }

        PortraitResource resolved = new PortraitResource(location, Math.max(1, width), Math.max(1, height));
        SOCIAL_PORTRAIT_CACHE.put(location, resolved);
        return resolved;
    }

    private record PortraitResource(ResourceLocation texture, int sheetWidth, int sheetHeight) {
    }

    private void rebuildCraftingEntries() {
        List<String> recipeIds = new ArrayList<>(RecipeCatalogData.getCraftingRecipeIds());

        List<RecipeEntry> entries = new ArrayList<>();
        for (String recipeId : recipeIds) {
            ItemStack stack = resolveRecipeResultStack(recipeId);
            if (stack.isEmpty()) {
                continue;
            }

            boolean unlocked = ClientPlayerDataCache.hasRecipe(recipeId);
            int rank;
            if (!unlocked) {
                rank = 2;
            } else {
                boolean craftable = computeMaxCraftsClient(getRecipeIngredients(recipeId), 1) > 0;
                rank = craftable ? 0 : 1;
            }
            entries.add(new RecipeEntry(recipeId, stack, rank));
        }

        entries.sort((a, b) -> {
            int rankCmp = Integer.compare(a.rank(), b.rank());
            if (rankCmp != 0) {
                return rankCmp;
            }
            return a.id().compareTo(b.id());
        });

        List<String> validIds = new ArrayList<>(entries.size());
        List<ItemStack> stacks = new ArrayList<>(entries.size());
        for (RecipeEntry entry : entries) {
            validIds.add(entry.id());
            stacks.add(entry.stack());
        }

        this.craftingRecipeIds = validIds;
        this.craftingRecipeStacks = stacks;
        this.craftingPages = buildCraftingPages();

        if (craftingRecipeIds.isEmpty()) {
            selectedCraftingIndex = -1;
        } else if (selectedCraftingIndex < 0 || selectedCraftingIndex >= craftingRecipeIds.size()) {
            selectedCraftingIndex = 0;
        }

        clampCraftingPage();
    }

    private ItemStack resolveRecipeResultStack(String recipeId) {
        return StardewCraftingRecipeData.getOutputStack(recipeId);
    }

    private void clampCraftingPage() {
        int maxPage = Math.max(0, craftingPages.size() - 1);
        if (currentCraftingPage < 0) {
            currentCraftingPage = 0;
        }
        if (currentCraftingPage > maxPage) {
            currentCraftingPage = maxPage;
        }
    }

    private List<List<RecipeCell>> buildCraftingPages() {
        List<List<RecipeCell>> pages = new ArrayList<>();
        if (craftingRecipeIds.isEmpty()) {
            pages.add(List.of());
            return pages;
        }

        List<RecipeCell> currentPage = new ArrayList<>();
        int capacity = 40;
        int currentCount = 0;

        for (int recipeIndex = 0; recipeIndex < craftingRecipeIds.size(); recipeIndex++) {
            if (currentCount >= capacity) {
                pages.add(currentPage);
                currentPage = new ArrayList<>();
                currentCount = 0;
            }
            int x = currentCount % 10;
            int y = currentCount / 10;
            currentPage.add(new RecipeCell(recipeIndex, x, y, false));
            currentCount++;
        }
        if (!currentPage.isEmpty()) {
            pages.add(currentPage);
        }

        return pages;
    }

    private boolean isBigCraftable(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof BlockItem;
    }

    private void updateVisualFocus() {
        int targetIndex = hoveredCraftingIndex;
        if (targetIndex != currentFocusIndex) {
            float lerpFactor = 0.2f;
            this.showcaseAlpha = Mth.lerp(lerpFactor, this.showcaseAlpha, 0.0f);
            if (this.showcaseAlpha < 0.05f) {
                this.currentFocusIndex = targetIndex;
                if (targetIndex != -1) {
                    this.showcaseAlpha = 0.05f; 
                }
            }
        } else if (targetIndex != -1) {
            if (this.showcaseAlpha < 1.0f) {
                this.showcaseAlpha = Mth.lerp(0.15f, this.showcaseAlpha, 1.0f);
            }
        } else {
            this.showcaseAlpha = 0.0f;
        }
    }

    private void drawElegantShowcase(GuiGraphics g, int px, int py, int focusIdx) {
        if (focusIdx < 0 || focusIdx >= craftingRecipeStacks.size()) return;

        ItemStack stack = craftingRecipeStacks.get(focusIdx);
        if (stack.isEmpty()) return;

        String recipeId = craftingRecipeIds.get(focusIdx);
        boolean unlocked = ClientPlayerDataCache.hasRecipe(recipeId);

        int alphaBits = Math.max(0, Math.min(255, (int) (this.showcaseAlpha * 255))) << 24;
        if ((alphaBits & 0xFF000000) == 0) return;

        g.pose().pushPose();
        g.pose().translate(px, py, 300);

        Component title = unlocked ? stack.getHoverName().copy().withStyle(net.minecraft.ChatFormatting.BOLD)
                : Component.literal("???").withStyle(net.minecraft.ChatFormatting.BOLD);

        float titleScale = 1.5f;
        int tw = this.font.width(title);
        
        int tx = (ui(240) - (int)(tw * titleScale)) / 2;
        g.pose().pushPose();
        g.pose().translate(tx, ui(20), 0);
        g.pose().scale(titleScale, titleScale, 1.0f);
        g.drawString(this.font, title, 1, 1, alphaBits | 0x2d170a, false);
        g.drawString(this.font, title, 0, 0, alphaBits | 0xFFFFFF, false);
        g.pose().popPose();

        int dropY = ui(120);
        float floatY = (float)Math.sin((System.currentTimeMillis() % 6000) / 6000.0f * Math.PI * 2) * 5.0f;

        g.pose().pushPose();
        g.pose().translate(ui(120), dropY + ui(30), 0);
        g.pose().scale(4.0f, 4.0f, 1.0f);
        g.fill(-8, -4, 8, 4, 0x44000000 | (alphaBits & 0xFF000000));
        g.pose().popPose();

        g.pose().pushPose();
        g.pose().translate(ui(120), dropY + floatY, 0);
        g.pose().scale(6.0f, 6.0f, 1.0f);
        
        if (!unlocked) {
            com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
            g.renderItem(stack, -8, -8);
            com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        } else {
            g.renderItem(stack, -8, -8);
        }
        
        g.pose().popPose();

        g.pose().popPose();
    }

    private void drawCraftingPage(GuiGraphics graphics, int mouseX, int mouseY) {
        StardewGuiUtil.drawHorizontalPartition(graphics, menuX, menuY + ui(400), menuWidth, mapping.s4());

        drawPlayerInventory(graphics, mouseX, mouseY);
        drawTrashCan(graphics, mouseX, mouseY);
        drawRecipeGrid(graphics, mouseX, mouseY);
    }

    private void drawRecipeGrid(GuiGraphics graphics, int mouseX, int mouseY) {
        if (currentCraftingPage < 0 || currentCraftingPage >= craftingPages.size()) {
            return;
        }

        List<RecipeCell> page = craftingPages.get(currentCraftingPage);
        for (int local = 0; local < page.size(); local++) {
            RecipeCell cellData = page.get(local);
            int index = cellData.recipeIndex();
            if (index < 0 || index >= craftingRecipeIds.size()) {
                continue;
            }

            int x = menuX + ui(80 + cellData.x() * 72);
            int y = menuY + ui(80 + cellData.y() * 72);
            int h = cellData.bigCraftable() ? ui(128) : ui(64);

            ItemStack stack = craftingRecipeStacks.get(index);
            String recipeId = craftingRecipeIds.get(index);
            boolean unlocked = ClientPlayerDataCache.hasRecipe(recipeId);
            boolean craftable = computeMaxCraftsClient(getRecipeIngredients(recipeId), 1) > 0;
            boolean hovered = recipeContains(mouseX, mouseY, x, y, h, ui(4));

            if (local >= 0 && local < recipeHoverScale.length) {
                recipeHoverScale[local] = stepScale(recipeHoverScale[local], hovered ? 1.1f : 1.0f, 0.02f);
            }
            float scale = (local >= 0 && local < recipeHoverScale.length && recipeHoverScale[local] > 0.001f)
                    ? recipeHoverScale[local]
                    : 1.0f;

            int baseSlot = ui(64);
            int mcSize = 16;
            int itemX = x + (baseSlot - mcSize) / 2;
            int itemY = y + (baseSlot - mcSize) / 2;

            graphics.pose().pushPose();
            graphics.pose().translate(itemX + 8, itemY + 8, 0);
            graphics.pose().scale(scale, scale, 1.0f);
            
            if (!unlocked) {
                com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
                graphics.renderItem(stack, -8, -8);
                com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            } else if (!craftable) {
                com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.35F, 0.35F, 0.35F, 1.0F);
                graphics.renderItem(stack, -8, -8);
                com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            } else {
                graphics.renderItem(stack, -8, -8);
            }
            
            graphics.pose().popPose();

            if (unlocked && hasShiftDown()) {
                int maxCrafts = computeMaxCraftsClient(getRecipeIngredients(recipeId), 999);
                if (maxCrafts > 0) {
                    ItemStack countStack = stack.copy();
                    countStack.setCount(maxCrafts);
                    graphics.renderItemDecorations(this.font, countStack, itemX, itemY);
                } else if (stack.getCount() > 1) {
                    graphics.renderItemDecorations(this.font, stack, itemX, itemY);
                }
            } else if (stack.getCount() > 1 && unlocked) {
                graphics.renderItemDecorations(this.font, stack, itemX, itemY);
            }
        }
    }

    // drawMCStyleCount removed

    private void drawPageArrows(GuiGraphics graphics, int mouseX, int mouseY) {
        int upX = pageButtonX();
        int upY = pageUpButtonY();
        int downX = pageButtonX();
        int downY = pageDownButtonY();

        boolean upHovered = upButtonContains(mouseX, mouseY);
        boolean downHovered = downButtonContains(mouseX, mouseY);
        upButtonScale = stepScale(upButtonScale, upHovered ? 0.9f : 0.8f, 0.025f);
        downButtonScale = stepScale(downButtonScale, downHovered ? 0.9f : 0.8f, 0.025f);

        if (currentCraftingPage > 0) {
            drawArrowFromCursors(graphics, upX, upY, 12, upButtonScale);
        }
        if (currentCraftingPage < Math.max(0, craftingPages.size() - 1)) {
            drawArrowFromCursors(graphics, downX, downY, 11, downButtonScale);
        }
    }

    private void drawArrowFromCursors(GuiGraphics graphics, int boundX, int boundY, int tilePosition, float vanillaScale) {
        float drawScale = mapping.s4() * vanillaScale;
        // 11 is down arrow, 12 is up arrow in standard Stardew logic (from Game1.mouseCursors)
        // Up arrow: u=421, v=459, w=11, h=12
        // Down arrow: u=421, v=472, w=11, h=12
        int u = 421;
        int v = (tilePosition == 12) ? 459 : 472;
        int w = 11;
        int h = 12;

        int offsetX = (ui(64) - Math.round(w * drawScale)) / 2;
        int offsetY = (ui(64) - Math.round(h * drawScale)) / 2;

        graphics.pose().pushPose();
        graphics.pose().translate(boundX + offsetX, boundY + offsetY, 0);
        graphics.pose().scale(drawScale, drawScale, 1.0f);
        graphics.blit(StardewGuiUtil.CURSORS, 0, 0, u, v, w, h, StardewGuiUtil.CURSORS_WIDTH, StardewGuiUtil.CURSORS_HEIGHT);
        graphics.pose().popPose();
    }

    private void drawTrashCan(GuiGraphics graphics, int mouseX, int mouseY) {
        int bodyX = menuX + menuWidth + ui(4);
        int bodyY = menuY + menuHeight - ui(360);
        int trashCanLevel = 0;
        int trashU = TRASH_U_BASE + trashCanLevel * TRASH_W;
        boolean hovered = trashCanContains(mouseX, mouseY);
        if (hovered && !trashCanLidSoundPlayed) {
            playUiSound(ModSounds.TRASHCANLID.get(), 1.0f, 1.0f);
            trashCanLidSoundPlayed = true;
        }
        if (!hovered) {
            trashCanLidSoundPlayed = false;
        }
        float step = (float) Math.PI / 48.0f;
        if (hovered) {
            trashCanLidRotation = Math.min(trashCanLidRotation + step, (float) Math.PI / 2.0f);
        } else {
            trashCanLidRotation = Math.max(trashCanLidRotation - step, 0.0f);
        }

        StardewGuiUtil.drawFromCursors(graphics, bodyX, bodyY, trashU, TRASH_BODY_V, TRASH_W, TRASH_BODY_H, mapping.s4());

        float lidScale = mapping.s4();
        int lidDrawX = bodyX + ui(60);
        int lidDrawY = bodyY + ui(40);
        graphics.pose().pushPose();
        graphics.pose().translate(lidDrawX, lidDrawY, 0);
        graphics.pose().mulPose(com.mojang.math.Axis.ZP.rotation(trashCanLidRotation));
        graphics.pose().scale(lidScale, lidScale, 1.0f);
        graphics.blit(StardewGuiUtil.CURSORS, -16, -10, trashU, TRASH_LID_V, TRASH_W, TRASH_LID_H, StardewGuiUtil.CURSORS_WIDTH, StardewGuiUtil.CURSORS_HEIGHT);
        graphics.pose().popPose();
    }

    private void drawPlayerInventory(GuiGraphics graphics, int mouseX, int mouseY) {
        Minecraft mc = this.minecraft;
        if (mc == null || mc.player == null) {
            return;
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < INVENTORY_COLS; col++) {
                int invIndex = col + row * 9 + 9;
                int x = inventorySlotX(col);
                int y = inventorySlotY(row);
                boolean hovered = mouseX >= x && mouseX < x + ui(INVENTORY_SLOT_SDV) && mouseY >= y && mouseY < y + ui(INVENTORY_SLOT_SDV);
                drawInventorySlot(graphics, x, y, mc.player.getInventory().getItem(invIndex), hovered);
            }
        }

        int hotbarY = inventoryHotbarY();
        for (int col = 0; col < INVENTORY_COLS; col++) {
            int x = inventorySlotX(col);
            boolean hovered = mouseX >= x && mouseX < x + ui(INVENTORY_SLOT_SDV) && mouseY >= hotbarY && mouseY < hotbarY + ui(INVENTORY_SLOT_SDV);
            drawInventorySlot(graphics, x, hotbarY, mc.player.getInventory().getItem(col), hovered);
        }
    }

    private void drawInventorySlot(GuiGraphics graphics, int x, int y, ItemStack stack, boolean hovered) {
        StardewGuiUtil.drawMenuTileIndex(graphics, x, y, ui(INVENTORY_SLOT_SDV), ui(INVENTORY_SLOT_SDV), 10);
        if (hovered) {
            graphics.pose().pushPose();
            graphics.pose().translate(0, 0, 100);
            graphics.fill(x, y, x + ui(INVENTORY_SLOT_SDV), y + ui(INVENTORY_SLOT_SDV), 0x35FFFFFF);
            graphics.pose().popPose();
        }
        if (!stack.isEmpty()) {
            int boxW = ui(INVENTORY_SLOT_SDV);
            int mcSize = 16;
            int offsetX = (boxW - mcSize) / 2;
            int offsetY = (boxW - mcSize) / 2;

            graphics.renderItem(stack, x + offsetX, y + offsetY);
            graphics.renderItemDecorations(this.font, stack, x + offsetX, y + offsetY);
        }
    }

    private int inventoryGridWidth() {
        return ui(INVENTORY_COLS * INVENTORY_SLOT_SDV + (INVENTORY_COLS - 1) * INVENTORY_GAP_SDV);
    }

    private int inventorySlotX(int col) {
        int startX = menuX + (menuWidth - inventoryGridWidth()) / 2;
        return startX + col * ui(INVENTORY_SLOT_SDV + INVENTORY_GAP_SDV);
    }

    private int inventorySlotY(int row) {
        return menuY + ui(INVENTORY_TOP_SDV + row * (INVENTORY_SLOT_SDV + INVENTORY_GAP_SDV));
    }

    private int inventoryHotbarY() {
        return menuY + ui(INVENTORY_TOP_SDV + 3 * (INVENTORY_SLOT_SDV + INVENTORY_GAP_SDV));
    }

// unused scaling helpers removed

    private int craftingGridIndexAt(double mouseX, double mouseY) {
        return craftingGridIndexAt(mouseX, mouseY, 0);
    }

    private int craftingGridIndexAt(double mouseX, double mouseY, int pad) {
        if (currentTab != 4) {
            return -1;
        }

        if (currentCraftingPage < 0 || currentCraftingPage >= craftingPages.size()) {
            return -1;
        }

        List<RecipeCell> page = craftingPages.get(currentCraftingPage);
        for (RecipeCell cellData : page) {
            int x = menuX + ui(80 + cellData.x() * 72);
            int y = menuY + ui(80 + cellData.y() * 72);
            int h = cellData.bigCraftable() ? ui(128) : ui(64);
            if (recipeContains(mouseX, mouseY, x, y, h, pad)) {
                return cellData.recipeIndex();
            }
        }

        return -1;
    }

    private int pageButtonX() {
        return menuX + ui(800);
    }

    private int pageUpButtonY() {
        return craftingGridY() - ui(16);
    }

    private int pageDownButtonY() {
        return craftingGridY() + ui(280);
    }

    private int craftingGridX() {
        return menuX + ui(80);
    }

    private int craftingGridY() {
        return menuY + ui(80);
    }

    private int inventoryStartX() {
        return menuX + ui(BORDER_WIDTH + 16);
    }

    private int inventoryStartY() {
        return menuY + ui(INVENTORY_TOP_SDV);
    }

    private boolean upButtonContains(double mouseX, double mouseY) {
        int x = pageButtonX();
        int y = pageUpButtonY();
        int w = ui(64);
        int h = ui(64);
        return mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h;
    }

    private boolean downButtonContains(double mouseX, double mouseY) {
        int x = pageButtonX();
        int y = pageDownButtonY();
        int w = ui(64);
        int h = ui(64);
        return mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h;
    }

    private boolean trashCanContains(double mouseX, double mouseY) {
        int x = menuX + menuWidth + ui(4);
        int y = menuY + menuHeight - ui(360);
        int w = ui(64);
        int h = ui(104);
        return mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h;
    }

    private boolean recipeContains(double mouseX, double mouseY, int x, int y, int h, int pad) {
        return mouseX >= x - pad && mouseX < x + ui(64) + pad && mouseY >= y - pad && mouseY < y + h + pad;
    }

    private boolean isCraftableClient(int recipeIndex) {
        if (recipeIndex < 0 || recipeIndex >= craftingRecipeIds.size()) {
            return false;
        }
        return computeMaxCraftsClient(getRecipeIngredients(craftingRecipeIds.get(recipeIndex)), 1) > 0;
    }

    private float stepScale(float current, float target, float step) {
        if (current < target) {
            return Math.min(current + step, target);
        }
        return Math.max(current - step, target);
    }

    private void submitCraftRequest(int requestedCount) {
        if (selectedCraftingIndex < 0 || selectedCraftingIndex >= craftingRecipeIds.size()) {
            return;
        }
        String recipeId = craftingRecipeIds.get(selectedCraftingIndex);
        PacketDistributor.sendToServer(new CraftingMenuCraftSubmitPayload(recipeId, requestedCount));
    }

    private void submitInventoryClickRequest(int slotIndex, boolean rightClick) {
        PacketDistributor.sendToServer(new CraftingMenuInventoryActionPayload(CraftingMenuInventoryActionPayload.ACTION_CLICK_SLOT, slotIndex, rightClick));
    }

    private void submitTrashCarriedRequest() {
        PacketDistributor.sendToServer(new CraftingMenuInventoryActionPayload(CraftingMenuInventoryActionPayload.ACTION_TRASH_CARRIED, -1, false));
    }

    private void submitDropCarriedRequest() {
        PacketDistributor.sendToServer(new CraftingMenuInventoryActionPayload(CraftingMenuInventoryActionPayload.ACTION_DROP_CARRIED, -1, false));
    }

    private ItemStack currentCarriedItem() {
        if (this.minecraft == null || this.minecraft.player == null || this.minecraft.player.containerMenu == null) {
            return ItemStack.EMPTY;
        }
        ItemStack carried = this.minecraft.player.containerMenu.getCarried();
        return carried == null ? ItemStack.EMPTY : carried;
    }

    private boolean hasCarriedItem() {
        return !currentCarriedItem().isEmpty();
    }

    private boolean pointInsideMainMenu(double mouseX, double mouseY) {
        return mouseX >= menuX && mouseX < menuX + activeMenuWidth() && mouseY >= menuY && mouseY < menuY + menuHeight;
    }

    private int vanillaLikeCraftAmountOnLeftClick() {
        if (hasShiftDown()) {
            if (hasControlDown()) {
                return 25;
            }
            return 5;
        }
        return 1;
    }

    private List<Ingredient> getRecipeIngredients(String recipePath) {
        return StardewCraftingRecipeData.toExpandedIngredients(recipePath,
            ClientPlayerDataCache.hasProfession(ProfessionType.TRAPPER));
    }

    private List<RecipeRequirement> getRecipeRequirements(String recipePath) {
        List<Ingredient> ingredients = getRecipeIngredients(recipePath);
        if (ingredients.isEmpty()) {
            return List.of();
        }

        Map<String, RecipeRequirement> grouped = new LinkedHashMap<>();
        for (Ingredient ingredient : ingredients) {
            String key = ingredientSignature(ingredient);
            RecipeRequirement existing = grouped.get(key);
            if (existing == null) {
                grouped.put(key, new RecipeRequirement(ingredient, resolveIngredientIcon(ingredient), 1));
            } else {
                grouped.put(key, new RecipeRequirement(existing.ingredient(), existing.icon(), existing.need() + 1));
            }
        }
        return new ArrayList<>(grouped.values());
    }

    private String ingredientSignature(Ingredient ingredient) {
        if (ingredient == null || ingredient.isEmpty()) {
            return "empty";
        }

        List<String> keys = new ArrayList<>();
        for (ItemStack stack : ingredient.getItems()) {
            if (stack.isEmpty()) {
                continue;
            }
            keys.add(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
        }
        keys.sort(String::compareTo);
        return String.join("|", keys);
    }

    private ItemStack resolveIngredientIcon(Ingredient ingredient) {
        if (ingredient == null || ingredient.isEmpty()) {
            return new ItemStack(Items.BARRIER);
        }
        ItemStack[] options = ingredient.getItems();
        if (options.length > 0 && !options[0].isEmpty()) {
            return options[0];
        }
        return new ItemStack(Items.BARRIER);
    }

    private int countMatchingClient(Ingredient ingredient) {
        if (this.minecraft == null || this.minecraft.player == null || ingredient == null || ingredient.isEmpty()) {
            return 0;
        }
        int total = 0;
        for (ItemStack stack : this.minecraft.player.getInventory().items) {
            if (!stack.isEmpty() && ingredient.test(stack)) {
                total += stack.getCount();
            }
        }
        return total;
    }

    private int computeMaxCraftsClient(List<Ingredient> ingredients, int cap) {
        if (ingredients == null || ingredients.isEmpty() || this.minecraft == null || this.minecraft.player == null) {
            return 0;
        }

        List<ItemStack> stacks = this.minecraft.player.getInventory().items;
        int[] remain = new int[stacks.size()];
        for (int i = 0; i < stacks.size(); i++) {
            remain[i] = stacks.get(i).getCount();
        }

        int crafted = 0;
        while (crafted < cap && tryConsumeOneCraftClient(remain, stacks, ingredients)) {
            crafted++;
        }
        return crafted;
    }

    private boolean tryConsumeOneCraftClient(int[] remain, List<ItemStack> stacks, List<Ingredient> ingredients) {
        for (Ingredient ingredient : ingredients) {
            int chosen = -1;
            for (int slot = 0; slot < stacks.size(); slot++) {
                if (remain[slot] <= 0) {
                    continue;
                }
                ItemStack stack = stacks.get(slot);
                if (!stack.isEmpty() && ingredient.test(stack)) {
                    chosen = slot;
                    break;
                }
            }

            if (chosen < 0) {
                return false;
            }

            remain[chosen]--;
        }
        return true;
    }

    private void drawCraftingTooltips(GuiGraphics graphics, int mouseX, int mouseY) {
        int invSlot = hoveredInventorySlot(mouseX, mouseY);
        if (invSlot >= 0 && this.minecraft != null && this.minecraft.player != null) {
            ItemStack stack = this.minecraft.player.getInventory().getItem(invSlot);
            if (!stack.isEmpty()) {
                graphics.renderTooltip(this.font, stack, mouseX, mouseY);
                return;
            }
        }

        if (hoveredCraftingIndex >= 0 && hoveredCraftingIndex < craftingRecipeStacks.size()) {
            String recipeId = craftingRecipeIds.get(hoveredCraftingIndex);
            boolean unlocked = ClientPlayerDataCache.hasRecipe(recipeId);

            if (!unlocked) {
                graphics.renderTooltip(this.font, Component.literal("???").withStyle(ChatFormatting.BOLD), mouseX, mouseY);
                return;
            }

            ItemStack output = craftingRecipeStacks.get(hoveredCraftingIndex);
            List<RecipeRequirement> requirements = getRecipeRequirements(recipeId);

            // Build tooltip lines using MC's native renderTooltip
            List<Component> lines = new ArrayList<>();
            // Item tooltip lines (name, category, description, etc.)
            if (this.minecraft != null && this.minecraft.player != null) {
                Item.TooltipContext context = Item.TooltipContext.of(this.minecraft.level);
                TooltipFlag flag = this.minecraft.options.advancedItemTooltips ? TooltipFlag.ADVANCED : TooltipFlag.NORMAL;
                lines.addAll(output.getTooltipLines(context, this.minecraft.player, flag));
            } else {
                lines.add(output.getHoverName().copy());
            }
            // Separator + Ingredients section
            lines.add(Component.empty());
            lines.add(Component.literal("原料:").withStyle(ChatFormatting.GOLD));
            int ingredientStartLine = lines.size();
            for (RecipeRequirement requirement : requirements) {
                int have = countMatchingClient(requirement.ingredient());
                int need = requirement.need();
                boolean enough = have >= need;
                ChatFormatting color = enough ? ChatFormatting.WHITE : ChatFormatting.RED;
                // "  " prefix = space for icon overlay (about 10px)
                Component line = Component.literal("  ")
                        .append(Component.literal(" " + need + "× ").withStyle(color))
                        .append(requirement.icon().getHoverName().copy().withStyle(color));
                lines.add(line);
            }

            // Render tooltip using MC's native method
            graphics.renderTooltip(this.font, lines, java.util.Optional.empty(), mouseX, mouseY);

            // --- Overlay ingredient icons ---
            // MC tooltip positioning: starts at (mouseX+12, mouseY-12), then clamped
            int tooltipWidth = 0;
            for (Component line : lines) {
                int w = this.font.width(line);
                if (w > tooltipWidth) tooltipWidth = w;
            }
            // MC adds 4px padding on each side inside the tooltip
            int frameWidth = tooltipWidth + 8;
            // Height: first line is 10px tall (lineH + 2px gap), rest are lineH(9) each.
            // But empty lines are only 2px + lineH. MC uses: if line index == 0, height += 2 extra.
            // Actually MC tooltip: first line adds (lineH+2), subsequent lines add lineH each,
            // except if a line is empty it adds lineH/2 instead.
            int tooltipHeight = 8; // top+bottom padding
            for (int i = 0; i < lines.size(); i++) {
                if (i == 0) {
                    tooltipHeight += this.font.lineHeight + 2;
                } else if (lines.get(i).getString().isEmpty()) {
                    tooltipHeight += this.font.lineHeight / 2;
                } else {
                    tooltipHeight += this.font.lineHeight + 1;
                }
            }

            int boxX = mouseX + 12;
            int boxY = mouseY - 12;
            // MC clamping logic
            if (boxX + frameWidth > this.width) {
                boxX -= 28 + frameWidth;
            }
            if (boxY + tooltipHeight + 6 > this.height) {
                boxY = this.height - tooltipHeight - 6;
            }
            if (boxY < 4) {
                boxY = 4;
            }

            // Calculate text start position (4px padding inside box)
            int textX = boxX + 4;
            int textY = boxY + 4;
            // Advance past rendered lines to reach ingredient lines
            for (int i = 0; i < ingredientStartLine; i++) {
                if (i == 0) {
                    textY += this.font.lineHeight + 2;
                } else if (lines.get(i).getString().isEmpty()) {
                    textY += this.font.lineHeight / 2;
                } else {
                    textY += this.font.lineHeight + 1;
                }
            }

            // Draw icons at each ingredient line
            for (int i = 0; i < requirements.size(); i++) {
                RecipeRequirement req = requirements.get(i);
                int lineY = textY;
                // Advance for previous ingredient lines
                for (int j = 0; j < i; j++) {
                    lineY += this.font.lineHeight + 1;
                }
                float s = 0.5f; // 16*0.5=8px, fits in line height of 9
                int iconDrawY = lineY + (this.font.lineHeight - 8) / 2;
                graphics.pose().pushPose();
                graphics.pose().translate(textX, iconDrawY, 400.0f);
                graphics.pose().scale(s, s, 1.0f);
                graphics.renderItem(req.icon(), 0, 0);
                graphics.pose().popPose();
            }

            return;
        }

        if (trashCanContains(mouseX, mouseY)) {
            ItemStack carried = currentCarriedItem();
            if (!carried.isEmpty()) {
                List<Component> lines = new ArrayList<>();
                lines.add(Component.translatable("stardewcraft.game_menu.crafting.trash_can").withStyle(ChatFormatting.WHITE));
                lines.add(carried.getHoverName().copy().withStyle(ChatFormatting.GRAY));
                graphics.renderTooltip(this.font, lines, java.util.Optional.empty(), mouseX, mouseY);
                return;
            }
            graphics.renderTooltip(this.font, Component.translatable("stardewcraft.game_menu.crafting.trash_can"), mouseX, mouseY);
        }
    }

    private void updateCraftingHoverState(int mouseX, int mouseY) {
        hoveredCraftingIndex = -1;

        int hoverGridIndex = craftingGridIndexAt(mouseX, mouseY, ui(4));
        if (hoverGridIndex >= 0 && hoverGridIndex < craftingRecipeIds.size()) {
            hoveredCraftingIndex = hoverGridIndex;
        }
    }

    private int hoveredInventorySlot(double mouseX, double mouseY) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < INVENTORY_COLS; col++) {
                int x = inventorySlotX(col);
                int y = inventorySlotY(row);
                if (mouseX >= x && mouseX < x + ui(INVENTORY_SLOT_SDV) && mouseY >= y && mouseY < y + ui(INVENTORY_SLOT_SDV)) {
                    return col + row * 9 + 9;
                }
            }
        }

        int hotbarY = inventoryHotbarY();
        for (int col = 0; col < INVENTORY_COLS; col++) {
            int x = inventorySlotX(col);
            if (mouseX >= x && mouseX < x + ui(INVENTORY_SLOT_SDV) && mouseY >= hotbarY && mouseY < hotbarY + ui(INVENTORY_SLOT_SDV)) {
                return col;
            }
        }

        return -1;
    }

    private void closeWithSound() {
        if (hasCarriedItem()) {
            playUiSound(ModSounds.CANCEL.get(), 1.0f, 1.0f);
            return;
        }
        playUiSound(ModSounds.BIG_DESELECT.get(), 1.0f, 1.0f);
        if (this.minecraft != null) {
            this.minecraft.setScreen(null);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (currentTab == TAB_SOCIAL && socialTuneMode) {
                int picked = socialTuneTargetAt(mouseX, mouseY);
                if (picked >= 0) {
                    socialTuneTarget = picked;
                    socialTuneDragging = true;
                    playUiSound(ModSounds.SMALL_SELECT.get(), 1.0f, 1.0f);
                    return true;
                }
            }

            if (closeContains(mouseX, mouseY)) {
                closeWithSound();
                return true;
            }
            for (int i = 0; i < TAB_COUNT; i++) {
                if (tabContains(i, mouseX, mouseY)) {
                    if (currentTab != i) {
                        currentTab = i;
                        if (currentTab == TAB_SOCIAL) {
                            socialScroll = 0;
                            PacketDistributor.sendToServer(new RequestNpcFriendshipOverviewPayload());
                        }
                        if (currentTab == 3) {
                            farmMgmtScroll = 0;
                            PacketDistributor.sendToServer(new com.stardew.craft.network.payload.RequestFarmPermPayload());
                        }
                        playUiSound(ModSounds.SMALL_SELECT.get(), 1.0f, 1.0f);
                    }
                    return true;
                }
            }

            if (currentTab == TAB_SOCIAL) {
                int total = visibleSocialEntries().size();
                int maxScroll = socialMaxScroll(total);

                if (socialUpButtonContains(mouseX, mouseY) && socialScroll > 0) {
                    socialScroll--;
                    playUiSound(ModSounds.SHWIP.get(), 1.0f, 1.0f);
                    return true;
                }
                if (socialDownButtonContains(mouseX, mouseY) && socialScroll < maxScroll) {
                    socialScroll++;
                    playUiSound(ModSounds.SHWIP.get(), 1.0f, 1.0f);
                    return true;
                }
                if (socialScrollBarContains(mouseX, mouseY, total)) {
                    socialScrolling = true;
                    return true;
                }
                if (socialScrollRunnerContains(mouseX, mouseY)) {
                    int before = socialScroll;
                    setSocialScrollFromMouse(mouseY, total);
                    if (before != socialScroll) {
                        playUiSound(ModSounds.SHWIP.get(), 1.0f, 1.0f);
                    }
                    return true;
                }
            }

            // Farm management tab click handling
            if (currentTab == 3 && com.stardew.craft.client.gui.FarmPermissionClientCache.hasData()) {
                if (handleFarmMgmtClick((int) mouseX, (int) mouseY)) {
                    return true;
                }
            }

            if (currentTab == 0) {
                // Equipment slot click
                int equipSlot = invPageHoveredEquip(mouseX, mouseY);
                if (equipSlot >= 0) {
                    PacketDistributor.sendToServer(new com.stardew.craft.network.payload.EquipmentActionPayload(equipSlot));
                    playUiSound(ModSounds.SMALL_SELECT.get(), 1.0f, 1.0f);
                    return true;
                }
                // Inventory slot click
                int invSlot = invPageHoveredSlot(mouseX, mouseY);
                if (invSlot >= 0) {
                    submitInventoryClickRequest(invSlot, false);
                    playUiSound(ModSounds.SMALL_SELECT.get(), 1.0f, 1.0f);
                    return true;
                }
                // Organize button
                if (organizeButtonContains(mouseX, mouseY)) {
                    // SDV: ItemGrabMenu.organizeItemsInList
                    playUiSound(ModSounds.SHIP.get(), 1.0f, 1.0f);
                    return true;
                }
                // Trash can click
                if (trashCanContains(mouseX, mouseY) && hasCarriedItem()) {
                    submitTrashCarriedRequest();
                    playUiSound(ModSounds.THROW_DOWN_ITEM.get(), 1.0f, 1.0f);
                    return true;
                }
                // Drop outside
                if (!pointInsideMainMenu(mouseX, mouseY) && hasCarriedItem()) {
                    submitDropCarriedRequest();
                    playUiSound(ModSounds.THROW_DOWN_ITEM.get(), 1.0f, 1.0f);
                    return true;
                }
            }

            if (currentTab == 4) {
                int invSlot = hoveredInventorySlot(mouseX, mouseY);
                if (invSlot >= 0) {
                    submitInventoryClickRequest(invSlot, false);
                    playUiSound(ModSounds.SMALL_SELECT.get(), 1.0f, 1.0f);
                    return true;
                }
            }

            if (currentTab == 4 && upButtonContains(mouseX, mouseY) && currentCraftingPage > 0) {
                currentCraftingPage--;
                clampCraftingPage();
                playUiSound(ModSounds.SHWIP.get(), 1.0f, 1.0f);
                return true;
            }

            if (currentTab == 4 && downButtonContains(mouseX, mouseY) && currentCraftingPage < Math.max(0, craftingPages.size() - 1)) {
                currentCraftingPage++;
                clampCraftingPage();
                playUiSound(ModSounds.SHWIP.get(), 1.0f, 1.0f);
                return true;
            }

            if (currentTab == 4 && trashCanContains(mouseX, mouseY) && hasCarriedItem()) {
                submitTrashCarriedRequest();
                playUiSound(ModSounds.THROW_DOWN_ITEM.get(), 1.0f, 1.0f);
                return true;
            }

            if (currentTab == 4 && !pointInsideMainMenu(mouseX, mouseY) && hasCarriedItem()) {
                submitDropCarriedRequest();
                playUiSound(ModSounds.THROW_DOWN_ITEM.get(), 1.0f, 1.0f);
                return true;
            }

            if (!pointInsideMainMenu(mouseX, mouseY)) {
                return super.mouseClicked(mouseX, mouseY, button);
            }

            int gridIndex = craftingGridIndexAt(mouseX, mouseY, ui(4));
            if (gridIndex >= 0 && gridIndex < craftingRecipeIds.size()) {
                if (selectedCraftingIndex != gridIndex) {
                    selectedCraftingIndex = gridIndex;
                }

                if (currentTab == 4 && isCraftableClient(gridIndex)) {
                    submitCraftRequest(vanillaLikeCraftAmountOnLeftClick());
                    playUiSound(ModSounds.COIN.get(), 1.0f, 1.0f);
                }
                return true;
            }
        }

        if (button == 1 && currentTab == 0) {
            int invSlot = invPageHoveredSlot(mouseX, mouseY);
            if (invSlot >= 0) {
                submitInventoryClickRequest(invSlot, true);
                playUiSound(ModSounds.SMALL_SELECT.get(), 1.0f, 1.0f);
                return true;
            }
        }

        if (button == 1 && currentTab == 4) {
            int invSlot = hoveredInventorySlot(mouseX, mouseY);
            if (invSlot >= 0) {
                submitInventoryClickRequest(invSlot, true);
                playUiSound(ModSounds.SMALL_SELECT.get(), 1.0f, 1.0f);
                return true;
            }

            int gridIndex = craftingGridIndexAt(mouseX, mouseY, ui(4));
            if (gridIndex >= 0 && gridIndex < craftingRecipeIds.size()) {
                selectedCraftingIndex = gridIndex;
                if (isCraftableClient(gridIndex)) {
                    submitCraftRequest(1);
                    playUiSound(ModSounds.COIN.get(), 1.0f, 1.0f);
                }
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (currentTab == TAB_SOCIAL && socialTuneMode) {
            if (scrollY > 0) {
                socialTuneStep = Math.min(16, socialTuneStep + 1);
            } else if (scrollY < 0) {
                socialTuneStep = Math.max(1, socialTuneStep - 1);
            }
            return true;
        }

        if (currentTab == TAB_SOCIAL) {
            int total = visibleSocialEntries().size();
            int maxScroll = Math.max(0, total - SOCIAL_MAX_VISIBLE);
            if (maxScroll <= 0) {
                return true;
            }

            int before = socialScroll;
            if (scrollY > 0) {
                socialScroll = Math.max(0, socialScroll - 1);
            } else if (scrollY < 0) {
                socialScroll = Math.min(maxScroll, socialScroll + 1);
            }

            if (before != socialScroll) {
                playUiSound(ModSounds.SHWIP.get(), 1.0f, 1.0f);
            }
            return true;
        }

        if (currentTab == 3) {
            var players = com.stardew.craft.client.gui.FarmPermissionClientCache.getPlayers();
            int maxScroll = Math.max(0, players.size() - farmMgmtVisibleRows);
            if (maxScroll > 0) {
                int before = farmMgmtScroll;
                if (scrollY > 0) farmMgmtScroll = Math.max(0, farmMgmtScroll - 1);
                else if (scrollY < 0) farmMgmtScroll = Math.min(maxScroll, farmMgmtScroll + 1);
                if (before != farmMgmtScroll) playUiSound(ModSounds.SHWIP.get(), 1.0f, 1.0f);
            }
            return true;
        }

        if (currentTab != 4 || craftingRecipeIds.isEmpty()) {
            return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        }

        int oldPage = currentCraftingPage;
        if (scrollY > 0) {
            currentCraftingPage--;
        } else if (scrollY < 0) {
            currentCraftingPage++;
        }
        clampCraftingPage();

        if (oldPage != currentCraftingPage) {
            playUiSound(ModSounds.SHWIP.get(), 1.0f, 1.0f);
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (currentTab == TAB_SOCIAL && socialTuneMode && socialTuneDragging && button == 0 && socialTuneTarget >= 0) {
            int dx = Math.round((float) dragX * guiScale());
            int dy = Math.round((float) dragY * guiScale());
            if (dx != 0 || dy != 0) {
                socialTuneOffsetX[socialTuneTarget] += dx;
                socialTuneOffsetY[socialTuneTarget] += dy;
            }
            return true;
        }

        if (currentTab == TAB_SOCIAL && socialScrolling && button == 0) {
            int before = socialScroll;
            setSocialScrollFromMouse(mouseY, visibleSocialEntries().size());
            if (before != socialScroll) {
                playUiSound(ModSounds.SHWIP.get(), 1.0f, 1.0f);
            }
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            socialScrolling = false;
            socialTuneDragging = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            closeWithSound();
            return true;
        }

        if (handleSocialTuneKey(keyCode, modifiers)) {
            return true;
        }

        if (currentTab == TAB_SOCIAL) {
            int total = visibleSocialEntries().size();
            int maxScroll = Math.max(0, total - SOCIAL_MAX_VISIBLE);
            if (keyCode == 265) {
                int before = socialScroll;
                socialScroll = Math.max(0, socialScroll - 1);
                if (before != socialScroll) {
                    playUiSound(ModSounds.SHWIP.get(), 1.0f, 1.0f);
                }
                return true;
            }
            if (keyCode == 264) {
                int before = socialScroll;
                socialScroll = Math.min(maxScroll, socialScroll + 1);
                if (before != socialScroll) {
                    playUiSound(ModSounds.SHWIP.get(), 1.0f, 1.0f);
                }
                return true;
            }
        }
        if (keyCode == 265 && currentTab == 4) {
            int old = currentCraftingPage;
            currentCraftingPage--;
            clampCraftingPage();
            if (old != currentCraftingPage) {
                playUiSound(ModSounds.SHWIP.get(), 1.0f, 1.0f);
                return true;
            }
        }
        if (keyCode == 264 && currentTab == 4) {
            int old = currentCraftingPage;
            currentCraftingPage++;
            clampCraftingPage();
            if (old != currentCraftingPage) {
                playUiSound(ModSounds.SHWIP.get(), 1.0f, 1.0f);
                return true;
            }
        }

        if (keyCode == 261 && currentTab == 4 && hasCarriedItem()) {
            submitTrashCarriedRequest();
            playUiSound(ModSounds.THROW_DOWN_ITEM.get(), 1.0f, 1.0f);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void tick() {
        super.tick();
        Minecraft mc = this.minecraft;
        if (mc != null && mc.player != null && currentTab == 4) {
            rebuildCraftingEntries();
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
