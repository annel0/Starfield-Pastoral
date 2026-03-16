package com.stardew.craft.client.gui;

import com.stardew.craft.block.utility.WoodenChestColorPalette;
import com.stardew.craft.menu.StoneChestMenu;
import com.stardew.craft.network.payload.StoneChestColorSelectPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

@SuppressWarnings("null")
public class StoneChestScreen extends AbstractContainerScreen<StoneChestMenu> {
    private static final ResourceLocation CHEST_TEXTURE = ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png");
    private static final int ROWS = 6;

    private static final ResourceLocation COLOR_WHEEL = ResourceLocation.fromNamespaceAndPath("stardewcraft", "textures/gui/color_wheel.png");
    private static final int SWATCH_WIDTH = 8;
    private static final int SWATCH_HEIGHT = 14;
    private static final int GRID_COLS = 21;
    private static final int BUTTON_SIZE = 18;

    private int colorButtonX;
    private int colorButtonY;
    private boolean paletteOpen;

    public StoneChestScreen(StoneChestMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageHeight = 114 + ROWS * 18;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        this.colorButtonX = this.leftPos + this.imageWidth + 6;
        this.colorButtonY = this.topPos + 16;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        graphics.blit(CHEST_TEXTURE, x, y, 0, 0, this.imageWidth, ROWS * 18 + 17);
        graphics.blit(CHEST_TEXTURE, x, y + ROWS * 18 + 17, 0, 126, this.imageWidth, 96);

        boolean hovered = isHoveringColorButton(mouseX, mouseY);
        if (hovered) {
            graphics.fill(colorButtonX - 1, colorButtonY - 1, colorButtonX + BUTTON_SIZE + 1, colorButtonY + BUTTON_SIZE + 1, 0x40FFFFFF);
        }
        graphics.blit(COLOR_WHEEL, colorButtonX, colorButtonY, 0, 0, BUTTON_SIZE, BUTTON_SIZE, BUTTON_SIZE, BUTTON_SIZE);

        int selected = this.menu.getColorSelection();
        if (selected >= 0) {
            int rgb = WoodenChestColorPalette.rgbAt(selected) | 0xFF000000;
            int indW = 10;
            int indH = 3;
            graphics.fill(colorButtonX + BUTTON_SIZE / 2 - indW / 2, colorButtonY + BUTTON_SIZE + 2,
                colorButtonX + BUTTON_SIZE / 2 + indW / 2, colorButtonY + BUTTON_SIZE + 2 + indH, rgb);
            graphics.fill(colorButtonX + BUTTON_SIZE / 2 - indW / 2 - 1, colorButtonY + BUTTON_SIZE + 1,
                colorButtonX + BUTTON_SIZE / 2 + indW / 2 + 1, colorButtonY + BUTTON_SIZE + 2, 0x40000000);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        if (this.paletteOpen) {
            renderPalette(graphics, mouseX, mouseY);
        }

        this.renderTooltip(graphics, mouseX, mouseY);

        if (isHoveringColorButton(mouseX, mouseY) && !this.paletteOpen) {
            graphics.renderTooltip(this.font, Component.translatable("stardewcraft.stone_chest.color_picker"), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isHoveringColorButton(mouseX, mouseY)) {
            if (button == 1) {
                this.menu.setClientPreviewColorSelection(-1);
                PacketDistributor.sendToServer(new StoneChestColorSelectPayload(-1));
                return true;
            }
            if (button == 0) {
                this.paletteOpen = !this.paletteOpen;
                return true;
            }
        }

        if (this.paletteOpen) {
            int hit = getPaletteIndexAt(mouseX, mouseY);
            if (hit >= -1 && isInsidePalette(mouseX, mouseY)) {
                if (hit != -1) {
                    this.menu.setClientPreviewColorSelection(hit);
                    PacketDistributor.sendToServer(new StoneChestColorSelectPayload(hit));
                }
                return true;
            }

            if (!isInsidePalette(mouseX, mouseY) && !isHoveringColorButton(mouseX, mouseY)) {
                this.paletteOpen = false;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean isHoveringColorButton(double mouseX, double mouseY) {
        return mouseX >= colorButtonX && mouseX < colorButtonX + BUTTON_SIZE
            && mouseY >= colorButtonY && mouseY < colorButtonY + BUTTON_SIZE;
    }

    private void renderPalette(GuiGraphics graphics, int mouseX, int mouseY) {
        int panelX = getPaletteX();
        int panelY = getPaletteY();
        int panelW = GRID_COLS * SWATCH_WIDTH;
        int panelH = SWATCH_HEIGHT;

        graphics.fill(panelX - 2, panelY - 2, panelX + panelW + 2, panelY + panelH + 2, 0xD0101010);
        graphics.fill(panelX - 1, panelY - 1, panelX + panelW + 1, panelY + panelH + 1, 0x40FFFFFF);

        int selected = this.menu.getColorSelection();
        for (int i = 0; i < WoodenChestColorPalette.size(); i++) {
            int x = panelX + i * SWATCH_WIDTH;
            int y = panelY;
            boolean hovered = mouseX >= x && mouseX < x + SWATCH_WIDTH && mouseY >= y && mouseY < y + SWATCH_HEIGHT;
            int rgb = WoodenChestColorPalette.rgbAt(i) | 0xFF000000;

            graphics.fill(x, y, x + SWATCH_WIDTH, y + SWATCH_HEIGHT, rgb);

            if (i == selected) {
                graphics.fill(x, y, x + SWATCH_WIDTH, y + 2, 0xFFFFFFFF);
                graphics.fill(x, y + SWATCH_HEIGHT - 2, x + SWATCH_WIDTH, y + SWATCH_HEIGHT, 0xFFFFFFFF);
                graphics.fill(x, y, x + SWATCH_WIDTH, y + SWATCH_HEIGHT, 0x40FFFFFF);
            } else if (hovered) {
                graphics.fill(x, y, x + SWATCH_WIDTH, y + SWATCH_HEIGHT, 0x50FFFFFF);
            }
        }

        int hovered = getPaletteIndexAt(mouseX, mouseY);
        if (hovered >= 0) {
            graphics.renderTooltip(this.font, Component.translatable("stardewcraft.stone_chest.color_tooltip", hovered + 1), mouseX, mouseY);
        }
    }

    private int getPaletteIndexAt(double mouseX, double mouseY) {
        int panelX = getPaletteX();
        int panelY = getPaletteY();
        for (int i = 0; i < WoodenChestColorPalette.size(); i++) {
            int x = panelX + i * SWATCH_WIDTH;
            int y = panelY;
            if (mouseX >= x && mouseX < x + SWATCH_WIDTH && mouseY >= y && mouseY < y + SWATCH_HEIGHT) {
                return i;
            }
        }
        return -1;
    }

    private boolean isInsidePalette(double mouseX, double mouseY) {
        int panelX = getPaletteX();
        int panelY = getPaletteY();
        int panelW = GRID_COLS * SWATCH_WIDTH;
        int panelH = SWATCH_HEIGHT;
        return mouseX >= panelX - 2 && mouseX < panelX + panelW + 2 && mouseY >= panelY - 2 && mouseY < panelY + panelH + 2;
    }

    private int getPaletteX() {
        return this.leftPos + (this.imageWidth - (GRID_COLS * SWATCH_WIDTH)) / 2;
    }

    private int getPaletteY() {
        return this.topPos - SWATCH_HEIGHT - 6;
    }
}
