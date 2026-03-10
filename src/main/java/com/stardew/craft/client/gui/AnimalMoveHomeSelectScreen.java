package com.stardew.craft.client.gui;

import com.stardew.craft.network.payload.AnimalMoveHomeSelectPayload;
import com.stardew.craft.network.payload.OpenAnimalMoveHomeScreenPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

@SuppressWarnings("null")
public class AnimalMoveHomeSelectScreen extends Screen {

    private static final int BASE_PANEL_WIDTH = 540;
    private static final int BASE_PANEL_HEIGHT = 340;
    private static final int LIST_X = 22;
    private static final int LIST_Y = 68;
    private static final int LIST_ROW_HEIGHT = 40;
    private static final int VISIBLE_ROWS = 7;

    private static final int COLOR_OVERLAY = 0xA3000000;
    private static final int COLOR_PANEL = 0xEE192432;
    private static final int COLOR_PANEL_DARK = 0xE617202C;
    private static final int COLOR_ACCENT = 0xFFE0B464;
    private static final int COLOR_TEXT_MAIN = 0xFFF5EBD8;
    private static final int COLOR_TEXT_SUB = 0xFFB8C5D1;
    private static final int COLOR_BORDER = 0xFF3C5368;
    private static final int COLOR_ROW = 0xC71A2430;
    private static final int COLOR_ROW_HOVER = 0xC7243447;
    private static final int COLOR_ROW_SELECTED = 0xD0563E1D;

    private final OpenAnimalMoveHomeScreenPayload payload;
    private final List<OpenAnimalMoveHomeScreenPayload.BuildingOption> options;

    private int panelWidth;
    private int panelHeight;
    private int panelX;
    private int panelY;
    private int listWidth;
    private int selectedIndex = -1;
    private int scrollOffset = 0;

    private Button moveButton;
    private Button cancelButton;
    private Button upButton;
    private Button downButton;

    public AnimalMoveHomeSelectScreen(OpenAnimalMoveHomeScreenPayload payload) {
        super(Component.translatable("container.stardew_craft.animal_move_home"));
        this.payload = payload;
        this.options = payload.options();
    }

    @Override
    protected void init() {
        super.init();
        computeLayout();

        this.upButton = this.addRenderableWidget(Button.builder(Component.literal("^"), b -> scrollBy(-1))
            .bounds(0, 0, 24, 20)
            .build());

        this.downButton = this.addRenderableWidget(Button.builder(Component.literal("v"), b -> scrollBy(1))
            .bounds(0, 0, 24, 20)
            .build());

        this.moveButton = this.addRenderableWidget(Button.builder(Component.translatable("stardewcraft.animal.query.move_confirm"), b -> confirmMove())
            .bounds(0, 0, 170, 20)
            .build());

        this.cancelButton = this.addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), b -> onClose())
            .bounds(0, 0, 170, 20)
            .build());

        positionWidgets();
        updateButtonStates();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (scrollY > 0) {
            scrollBy(-1);
            return true;
        }
        if (scrollY < 0) {
            scrollBy(1);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int localX = (int) mouseX - panelX;
            int localY = (int) mouseY - panelY;

            if (localX >= LIST_X && localX < LIST_X + listWidth && localY >= LIST_Y && localY < LIST_Y + LIST_ROW_HEIGHT * VISIBLE_ROWS) {
                int row = (localY - LIST_Y) / LIST_ROW_HEIGHT;
                int index = scrollOffset + row;
                if (index >= 0 && index < options.size()) {
                    this.selectedIndex = index;
                    updateButtonStates();
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        computeLayout();
        positionWidgets();

        this.renderTransparentBackground(graphics);
        graphics.fill(0, 0, this.width, this.height, COLOR_OVERLAY);

        drawWindow(graphics, panelX, panelY, panelWidth, panelHeight);
        drawSection(graphics, panelX + 14, panelY + 52, panelWidth - 28, LIST_ROW_HEIGHT * VISIBLE_ROWS + 16);
        drawSection(graphics, panelX + 14, panelY + panelHeight - 74, panelWidth - 28, 56);

        graphics.drawString(this.font, Component.translatable("container.stardew_craft.animal_move_home"), panelX + 14, panelY + 10, COLOR_ACCENT, false);
        graphics.drawString(this.font, Component.translatable("stardewcraft.animal.query.move_target", payload.animalName()), panelX + 14, panelY + 26, COLOR_TEXT_MAIN, false);
        graphics.drawString(this.font, Component.translatable("stardewcraft.animal.query.move_status.available"), panelX + panelWidth - 84, panelY + 26, 0xFF8FEA8F, false);

        for (int row = 0; row < VISIBLE_ROWS; row++) {
            int index = scrollOffset + row;
            if (index >= options.size()) {
                break;
            }
            OpenAnimalMoveHomeScreenPayload.BuildingOption option = options.get(index);
            int rowX = panelX + LIST_X;
            int rowY = panelY + LIST_Y + row * LIST_ROW_HEIGHT;

            boolean selected = selectedIndex == index;
            boolean hover = mouseX >= rowX && mouseX < rowX + listWidth && mouseY >= rowY && mouseY < rowY + LIST_ROW_HEIGHT - 2;
            int bg = selected ? COLOR_ROW_SELECTED : (hover ? COLOR_ROW_HOVER : COLOR_ROW);
            graphics.fill(rowX, rowY, rowX + listWidth, rowY + LIST_ROW_HEIGHT - 2, bg);

            String occupancy = option.animalCount() + "/" + option.capacity();
            int statusColor = option.selectable() ? 0xFF8FEA8F : 0xFFE68B8B;
            Component statusText;
            if (option.buildingId().equals(payload.currentBuildingId())) {
                statusText = Component.translatable("stardewcraft.animal.query.move_status.current");
            } else if (!option.selectable()) {
                statusText = Component.translatable("stardewcraft.animal.query.move_status.full");
            } else {
                statusText = Component.translatable("stardewcraft.animal.query.move_status.available");
            }

            graphics.drawString(this.font, option.displayName(), rowX + 8, rowY + 6, COLOR_TEXT_MAIN, false);
            drawSmallText(graphics, Component.literal(option.buildingId()), rowX + 8, rowY + 19, COLOR_TEXT_SUB, 0.85f);
            graphics.drawString(this.font, statusText, rowX + listWidth - 8 - this.font.width(statusText), rowY + 4, statusColor, false);
            drawSmallText(graphics, Component.literal(occupancy), rowX + listWidth - 42, rowY + 19, COLOR_TEXT_SUB, 0.85f);
        }

        if (options.isEmpty()) {
            graphics.drawString(this.font, Component.translatable("stardewcraft.animal.query.move_empty"), panelX + LIST_X, panelY + LIST_Y + 8, 0xFFE68B8B, false);
        }

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void scrollBy(int delta) {
        int maxOffset = Math.max(0, options.size() - VISIBLE_ROWS);
        this.scrollOffset = Math.max(0, Math.min(maxOffset, this.scrollOffset + delta));
        updateButtonStates();
    }

    private void confirmMove() {
        if (selectedIndex < 0 || selectedIndex >= options.size()) {
            return;
        }
        OpenAnimalMoveHomeScreenPayload.BuildingOption selected = options.get(selectedIndex);
        if (!selected.selectable()) {
            return;
        }
        PacketDistributor.sendToServer(new AnimalMoveHomeSelectPayload(payload.animalId(), selected.buildingId()));
        onClose();
    }

    private void updateButtonStates() {
        int maxOffset = Math.max(0, options.size() - VISIBLE_ROWS);
        if (upButton != null) {
            upButton.active = scrollOffset > 0;
        }
        if (downButton != null) {
            downButton.active = scrollOffset < maxOffset;
        }
        if (moveButton != null) {
            boolean canMove = false;
            if (selectedIndex >= 0 && selectedIndex < options.size()) {
                OpenAnimalMoveHomeScreenPayload.BuildingOption selected = options.get(selectedIndex);
                canMove = selected.selectable();
            }
            moveButton.active = canMove;
        }
    }

    private void computeLayout() {
        this.panelWidth = Math.min(BASE_PANEL_WIDTH, this.width - 20);
        this.panelHeight = Math.min(BASE_PANEL_HEIGHT, this.height - 20);
        this.panelX = (this.width - panelWidth) / 2;
        this.panelY = (this.height - panelHeight) / 2;
        this.listWidth = panelWidth - 2 * LIST_X - 30;
    }

    private void positionWidgets() {
        if (this.upButton != null) {
            this.upButton.setX(panelX + panelWidth - 32);
            this.upButton.setY(panelY + LIST_Y);
        }
        if (this.downButton != null) {
            this.downButton.setX(panelX + panelWidth - 32);
            this.downButton.setY(panelY + LIST_Y + LIST_ROW_HEIGHT * (VISIBLE_ROWS - 1));
        }
        if (this.moveButton != null) {
            this.moveButton.setX(panelX + 20);
            this.moveButton.setY(panelY + panelHeight - 44);
        }
        if (this.cancelButton != null) {
            this.cancelButton.setX(panelX + panelWidth - 190);
            this.cancelButton.setY(panelY + panelHeight - 44);
        }
    }

    private void drawWindow(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.fillGradient(x, y, x + width, y + height, COLOR_PANEL, 0xEA121B26);
        graphics.fill(x + 2, y + 2, x + width - 2, y + 30, COLOR_PANEL_DARK);
        drawBorder(graphics, x, y, width, height, COLOR_BORDER);
        graphics.fill(x + 1, y + 1, x + width - 1, y + 2, COLOR_ACCENT);
    }

    private void drawSection(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.fillGradient(x, y, x + width, y + height, 0xD9222F3D, 0xD5172230);
        drawBorder(graphics, x, y, width, height, 0xFF2F465D);
    }

    private void drawBorder(GuiGraphics graphics, int x, int y, int width, int height, int color) {
        graphics.fill(x, y, x + width, y + 1, color);
        graphics.fill(x, y + height - 1, x + width, y + height, color);
        graphics.fill(x, y, x + 1, y + height, color);
        graphics.fill(x + width - 1, y, x + width, y + height, color);
    }

    private void drawSmallText(GuiGraphics graphics, Component text, int x, int y, int color, float scale) {
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        graphics.pose().scale(scale, scale, 1.0f);
        graphics.drawString(this.font, text, 0, 0, color, false);
        graphics.pose().popPose();
    }
}
