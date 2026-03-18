package com.stardew.craft.client.gui.menu;

import com.stardew.craft.client.gui.common.StardewRenderMapping;
import com.stardew.craft.client.gui.overnight.StardewGuiUtil;
import com.stardew.craft.sound.ModSounds;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;

@SuppressWarnings("null")
public class StardewGameMenuScreen extends Screen {

    private static final int BORDER_WIDTH = 40;
    private static final int MENU_WIDTH_SDV = 800 + BORDER_WIDTH * 2;
    private static final int MENU_HEIGHT_SDV = 600 + BORDER_WIDTH * 2;
    private static final int TAB_Y_OFFSET_SDV = 16;
    private static final int TAB_START_X_SDV = 64;
    private static final int TAB_STEP_SDV = 64;
    private static final int TAB_SIZE_SDV = 64;

    private static final int CLOSE_X_OFFSET_SDV = 36;
    private static final int CLOSE_Y_OFFSET_SDV = 8;
    private static final int CLOSE_SIZE_SDV = 48;

    private static final int[] TAB_SHEET_INDEX = new int[] {0, 1, 2, 3, 4, -1, -1, 5, 6, 7};
    private static final int TAB_COUNT = 10;

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

    public StardewGameMenuScreen() {
        super(Component.translatable("stardewcraft.game_menu.title"));
    }

    @Override
    protected void init() {
        super.init();
        recalcLayout();
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
        this.menuX = this.width / 2 - this.menuWidth / 2;
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
        int x = menuX + menuWidth - ui(CLOSE_X_OFFSET_SDV);
        int y = menuY - ui(CLOSE_Y_OFFSET_SDV);
        int s = ui(CLOSE_SIZE_SDV);
        return mouseX >= x && mouseX < x + s && mouseY >= y && mouseY < y + s;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        recalcLayout();
        graphics.fill(0, 0, this.width, this.height, 0x66000000);

        StardewGuiUtil.drawDialogueBoxFrame(graphics, menuX, menuY, menuWidth, menuHeight);
        drawTabs(graphics, mouseX, mouseY);
        drawCloseButton(graphics);
        drawPagePlaceholder(graphics);

        int hoveredTab = hoveredTab(mouseX, mouseY);
        if (hoveredTab >= 0) {
            graphics.renderTooltip(this.font, Component.translatable(TAB_KEYS[hoveredTab]), mouseX, mouseY);
        }
    }

    private int hoveredTab(int mouseX, int mouseY) {
        for (int i = 0; i < TAB_COUNT; i++) {
            if (tabContains(i, mouseX, mouseY)) {
                return i;
            }
        }
        return -1;
    }

    private void drawTabs(GuiGraphics graphics, int mouseX, int mouseY) {
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
        int x = menuX + menuWidth - ui(CLOSE_X_OFFSET_SDV);
        int y = menuY - ui(CLOSE_Y_OFFSET_SDV);
        StardewGuiUtil.drawFromCursors(graphics, x, y, 337, 494, 12, 12, mapping.s4());
    }

    private void drawPagePlaceholder(GuiGraphics graphics) {
        Component tabName = Component.translatable(TAB_KEYS[currentTab]);
        Component title = Component.translatable("stardewcraft.game_menu.placeholder_title", tabName);
        Component line = Component.translatable("stardewcraft.game_menu.placeholder_body");

        int titleX = menuX + menuWidth / 2 - this.font.width(title) / 2;
        int lineX = menuX + menuWidth / 2 - this.font.width(line) / 2;
        int centerY = menuY + menuHeight / 2;

        graphics.drawString(this.font, title, titleX, centerY - 10, 0xFFF3E6C6, false);
        graphics.drawString(this.font, line, lineX, centerY + 8, 0xFFD8C9A8, false);
    }

    private void closeWithSound() {
        playUiSound(ModSounds.BIG_DESELECT.get(), 1.0f, 1.0f);
        if (this.minecraft != null) {
            this.minecraft.setScreen(null);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (closeContains(mouseX, mouseY)) {
                closeWithSound();
                return true;
            }
            for (int i = 0; i < TAB_COUNT; i++) {
                if (tabContains(i, mouseX, mouseY)) {
                    if (currentTab != i) {
                        currentTab = i;
                        playUiSound(ModSounds.SMALL_SELECT.get(), 1.0f, 1.0f);
                    }
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            closeWithSound();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
