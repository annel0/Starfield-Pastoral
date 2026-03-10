package com.stardew.craft.client.gui;

import com.stardew.craft.menu.ElevatorMenu;
import com.stardew.craft.network.payload.ElevatorActionPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

/**
 * 电梯 GUI - 显示已解锁的整5层按钮
 */
public class ElevatorScreen extends AbstractContainerScreen<ElevatorMenu> {

    private static final int BUTTON_SIZE = 24;
    private static final int BUTTON_GAP = 4;
    private static final int COLUMNS = 5;
    private int lastMaxFloor = Integer.MIN_VALUE;

    public ElevatorScreen(ElevatorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 180;
    }

    @SuppressWarnings("null")
    @Override
    protected void init() {
        super.init();

        List<Integer> floors = getUnlockedFloors();
        int rows = (int) Math.ceil(floors.size() / (double) COLUMNS);

        int gridWidth = COLUMNS * BUTTON_SIZE + (COLUMNS - 1) * BUTTON_GAP;
        int gridHeight = rows * BUTTON_SIZE + (rows - 1) * BUTTON_GAP;

        int startX = (this.width - gridWidth) / 2;
        int startY = (this.height - gridHeight) / 2;

        for (int i = 0; i < floors.size(); i++) {
            int floor = floors.get(i);
            int col = i % COLUMNS;
            int row = i / COLUMNS;

            int x = startX + col * (BUTTON_SIZE + BUTTON_GAP);
            int y = startY + row * (BUTTON_SIZE + BUTTON_GAP);

            this.addRenderableWidget(Button.builder(
                    Component.literal(String.valueOf(floor)),
                    button -> {
                        PacketDistributor.sendToServer(new ElevatorActionPayload(floor));
                        this.onClose();
                    })
                .bounds(x, y, BUTTON_SIZE, BUTTON_SIZE)
                .build());
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        int maxFloor = this.menu.getMaxFloorReached();
        if (maxFloor != lastMaxFloor) {
            lastMaxFloor = maxFloor;
            this.clearWidgets();
            this.init();
        }
    }

    private List<Integer> getUnlockedFloors() {
        int maxFloor = Math.min(120, this.menu.getMaxFloorReached());
        List<Integer> floors = new ArrayList<>();
        for (int floor = 0; floor <= maxFloor; floor += 5) {
            floors.add(floor);
        }
        return floors;
    }

    @Override
    protected void renderBg(@SuppressWarnings("null") GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        graphics.fill(x, y, x + this.imageWidth, y + this.imageHeight, 0xC0101010);
    }

    @SuppressWarnings("null")
    @Override
    protected void renderLabels(@SuppressWarnings("null") GuiGraphics graphics, int mouseX, int mouseY) {
        Component title = Component.translatable("container.stardew_craft.elevator");
        @SuppressWarnings("null")
        int titleWidth = this.font.width(title);
        graphics.drawString(this.font, title, (this.imageWidth - titleWidth) / 2, 8, 0xFFD700, false);

        Component floorText = Component.translatable("gui.stardew_craft.elevator.current_floor", this.menu.getCurrentFloor());
        @SuppressWarnings("null")
        int floorWidth = this.font.width(floorText);
        graphics.drawString(this.font, floorText, (this.imageWidth - floorWidth) / 2, 24, 0xFFFFFF, false);
    }
}
