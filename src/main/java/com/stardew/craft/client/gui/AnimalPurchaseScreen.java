package com.stardew.craft.client.gui;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.network.payload.AnimalPurchaseSubmitPayload;
import com.stardew.craft.network.payload.OpenAnimalPurchaseScreenPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

@SuppressWarnings("null")
public class AnimalPurchaseScreen extends Screen {

    private enum Stage {
        ANIMAL_SELECT,
        BUILDING_AND_NAME
    }

    private static final int BASE_WIDTH = 668;
    private static final int BASE_HEIGHT = 394;

    // Keep visual language aligned with AnimalQueryScreen.
    private static final int COLOR_OVERLAY = 0xA5000000;
    private static final int COLOR_PANEL = 0xEF182332;
    private static final int COLOR_PANEL_DARK = 0xE8162130;
    private static final int COLOR_SECTION = 0xD9243346;
    private static final int COLOR_ACCENT = 0xFFE0B464;
    private static final int COLOR_TEXT_MAIN = 0xFFF5EBD8;
    private static final int COLOR_TEXT_SUB = 0xFFB8C5D1;
    private static final int COLOR_TEXT_MUTED = 0xFF8FA3B8;
    private static final int COLOR_BORDER = 0xFF3C5368;

    private static final ResourceLocation GOLD_ICON = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/gold_icon.png");
    private static final ResourceLocation DICE_ICON = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/animal_query/dice_icon.png");
    private static final ResourceLocation OK_ICON = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/animal_query/ok_yes_tile46.png");
    private static final ResourceLocation NO_ICON = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/animal_query/cancel_no_tile47.png");

    private static final int GRID_COLS = 4;
    private static final int GRID_ROWS = 2;
    private static final int SLOT_W = 132;
    private static final int SLOT_H = 120;

    private final OpenAnimalPurchaseScreenPayload payload;
    private final Random random = new Random();

    private Stage stage = Stage.ANIMAL_SELECT;

    private int panelX;
    private int panelY;
    private int panelW;
    private int panelH;

    private int selectedAnimal = -1;
    private int selectedBuilding = -1;
    private int buildingScroll = 0;

    private int gridX;
    private int gridY;

    private int leftX;
    private int leftY;
    private int leftW;
    private int leftH;

    private int rightX;
    private int rightY;
    private int rightW;
    private int rightH;

    private int listX;
    private int listY;
    private int listW;
    private final int listRowH = 40;
    private final int visibleRows = 6;

    private int confirmIconX;
    private int confirmIconY;
    private int cancelIconX;
    private int cancelIconY;
    private int diceIconX;
    private int diceIconY;

    private EditBox nameEditBox;

    private static final Map<String, String[]> NAME_POOLS = Map.of(
        "white_chicken", new String[]{"Nugget", "Peep", "Dot", "Poppy", "Clover", "Sunny"},
        "duck", new String[]{"Quackie", "Ripple", "Mochi", "Puddle", "Flipper", "Bubbles"},
        "rabbit", new String[]{"Cocoa", "Hop", "Mimi", "Cotton", "Bean", "Sprout"},
        "cow", new String[]{"MooMoo", "Daisy", "Bessie", "Toffee", "Maple", "Butter"},
        "goat", new String[]{"Pepper", "Ivy", "Ginger", "Cliff", "Bramble", "Taro"},
        "sheep", new String[]{"Cloud", "Fleece", "Marsh", "Fluff", "Wooly", "Snow"},
        "pig", new String[]{"Truffle", "Rosie", "Bacon", "Porky", "Nori", "Acorn"}
    );

    public AnimalPurchaseScreen(OpenAnimalPurchaseScreenPayload payload) {
        super(Component.translatable("container.stardew_craft.animal_purchase"));
        this.payload = payload;
    }

    @Override
    protected void init() {
        super.init();
        computeLayout();

        nameEditBox = new EditBox(this.font, 0, 0, 218, 18, Component.translatable("stardewcraft.animal.purchase.name_hint"));
        nameEditBox.setMaxLength(48);
        nameEditBox.setResponder(v -> {});
        addRenderableWidget(nameEditBox);

        selectedAnimal = firstUnlockedAnimal();
        if (selectedAnimal >= 0) {
            resetBuildingSelection();
            nameEditBox.setValue(defaultRolledName());
        }

        applyStageVisibility();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (stage == Stage.BUILDING_AND_NAME && isInsideBuildingList((int) mouseX, (int) mouseY) && scrollY != 0.0) {
            int max = Math.max(0, filteredBuildings().size() - visibleRows);
            buildingScroll = Math.max(0, Math.min(max, buildingScroll + (scrollY > 0 ? -1 : 1)));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        int mx = (int) mouseX;
        int my = (int) mouseY;

        if (inside(mx, my, confirmIconX - 22, confirmIconY - 22, 44, 44)) {
            if (stage == Stage.ANIMAL_SELECT) {
                OpenAnimalPurchaseScreenPayload.AnimalOption option = selectedAnimalOption();
                if (option != null && option.unlocked()) {
                    stage = Stage.BUILDING_AND_NAME;
                    resetBuildingSelection();
                    if (nameEditBox.getValue().isBlank()) {
                        nameEditBox.setValue(defaultRolledName());
                    }
                    applyStageVisibility();
                }
            } else {
                submitPurchase();
            }
            return true;
        }

        if (inside(mx, my, cancelIconX - 22, cancelIconY - 22, 44, 44)) {
            if (stage == Stage.BUILDING_AND_NAME) {
                stage = Stage.ANIMAL_SELECT;
                applyStageVisibility();
            } else {
                onClose();
            }
            return true;
        }

        if (stage == Stage.ANIMAL_SELECT) {
            int idx = hoveredAnimalIndex(mx, my);
            if (idx >= 0 && idx < payload.animalOptions().size()) {
                OpenAnimalPurchaseScreenPayload.AnimalOption option = payload.animalOptions().get(idx);
                if (option.unlocked()) {
                    selectedAnimal = idx;
                }
                return true;
            }
        } else {
            if (inside(mx, my, diceIconX - 12, diceIconY - 12, 24, 24)) {
                rollRandomName();
                return true;
            }
            if (nameEditBox.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
            if (isInsideBuildingList(mx, my)) {
                int row = (my - listY) / listRowH;
                int idx = buildingScroll + row;
                List<OpenAnimalPurchaseScreenPayload.BuildingOption> filtered = filteredBuildings();
                if (idx >= 0 && idx < filtered.size()) {
                    selectedBuilding = findBuildingIndexById(filtered.get(idx).buildingId());
                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (stage == Stage.BUILDING_AND_NAME && nameEditBox.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (stage == Stage.BUILDING_AND_NAME && nameEditBox.charTyped(codePoint, modifiers)) {
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        computeLayout();
        positionDynamicControls();

        renderTransparentBackground(graphics);
        graphics.fill(0, 0, width, height, COLOR_OVERLAY);

        drawWindow(graphics, panelX, panelY, panelW, panelH);
        drawHeader(graphics);

        if (stage == Stage.ANIMAL_SELECT) {
            drawAnimalGrid(graphics, mouseX, mouseY);
        } else {
            drawFinalizeStage(graphics, mouseX, mouseY);
        }

        super.render(graphics, mouseX, mouseY, partialTick);

        drawActionIcons(graphics, mouseX, mouseY);
        if (stage == Stage.BUILDING_AND_NAME) {
            drawDiceIcon(graphics);
        }

        drawHoverInfo(graphics, mouseX, mouseY);
    }

    private void drawHeader(GuiGraphics graphics) {
        graphics.drawString(this.font, Component.translatable("container.stardew_craft.animal_purchase"), panelX + 14, panelY + 10, COLOR_ACCENT, false);

        int money = payload.playerMoney();
        String moneyText = String.valueOf(money);
        int textW = this.font.width(moneyText);
        int x = panelX + panelW - 18 - textW - 18;
        graphics.blit(GOLD_ICON, x, panelY + 8, 0, 0, 16, 16, 16, 16);
        graphics.drawString(this.font, moneyText, x + 18, panelY + 12, COLOR_TEXT_MAIN, false);
    }

    private void drawAnimalGrid(GuiGraphics graphics, int mouseX, int mouseY) {
        for (int i = 0; i < payload.animalOptions().size(); i++) {
            OpenAnimalPurchaseScreenPayload.AnimalOption option = payload.animalOptions().get(i);
            int col = i % GRID_COLS;
            int row = i / GRID_COLS;
            if (row >= GRID_ROWS) {
                break;
            }

            int cx = gridX + col * SLOT_W + SLOT_W / 2;
            int cy = gridY + row * SLOT_H + SLOT_H / 2;
            boolean hovered = hoveredAnimalIndex(mouseX, mouseY) == i;
            boolean selected = selectedAnimal == i;

            // Subtle shadow and selection halo without card/frame boxes.
            int shadowColor = hovered ? 0x66243E58 : 0x50121A24;
            graphics.fill(cx - 38, cy + 18, cx + 38, cy + 24, shadowColor);
            if (selected) {
                graphics.fill(cx - 40, cy - 36, cx + 40, cy - 34, COLOR_ACCENT);
                graphics.fill(cx - 40, cy + 34, cx + 40, cy + 36, COLOR_ACCENT);
                graphics.fill(cx - 40, cy - 36, cx - 38, cy + 36, COLOR_ACCENT);
                graphics.fill(cx + 38, cy - 36, cx + 40, cy + 36, COLOR_ACCENT);
            }

            if (option.unlocked()) {
                drawAnimalIcon(graphics, option.animalTypeId(), cx, cy - 4, 2.0f);
            } else {
                drawAnimalSilhouette(graphics, option.animalTypeId(), cx, cy - 4, 2.0f);
            }
        }
    }

    private void drawFinalizeStage(GuiGraphics graphics, int mouseX, int mouseY) {
        OpenAnimalPurchaseScreenPayload.AnimalOption animal = selectedAnimalOption();
        if (animal == null) {
            return;
        }

        drawSection(graphics, leftX, leftY, leftW, leftH);
        drawSection(graphics, rightX, rightY, rightW, rightH);

        drawAnimalIcon(graphics, animal.animalTypeId(), leftX + 56, leftY + 56, 2.2f);

        String animalName = resolveDisplayName(animal.displayName(), animal.animalTypeId());
        graphics.drawString(this.font, Component.literal(animalName), leftX + 104, leftY + 24, COLOR_TEXT_MAIN, false);
        graphics.drawString(this.font, Component.translatable("stardewcraft.animal.shop.require_tier", animal.requiredTier()), leftX + 104, leftY + 40, COLOR_TEXT_MUTED, false);

        drawMoneyLine(graphics, leftX + 16, leftY + 68, animal.price());

        Component desc = Component.translatable(animal.descriptionKey());
        List<net.minecraft.util.FormattedCharSequence> lines = this.font.split(desc, leftW - 24);
        int ly = leftY + 96;
        for (net.minecraft.util.FormattedCharSequence line : lines) {
            graphics.drawString(this.font, line, leftX + 12, ly, COLOR_TEXT_SUB, false);
            ly += this.font.lineHeight + 1;
            if (ly > leftY + leftH - 12) {
                break;
            }
        }

        graphics.drawString(this.font, Component.translatable("stardewcraft.animal.purchase.buildings"), rightX + 10, rightY + 8, COLOR_TEXT_MAIN, false);

        listX = rightX + 10;
        listY = rightY + 26;
        listW = rightW - 20;

        List<OpenAnimalPurchaseScreenPayload.BuildingOption> filtered = filteredBuildings();
        for (int row = 0; row < visibleRows; row++) {
            int idx = buildingScroll + row;
            if (idx >= filtered.size()) {
                break;
            }

            OpenAnimalPurchaseScreenPayload.BuildingOption option = filtered.get(idx);
            int y = listY + row * listRowH;
            boolean selected = isSelectedBuilding(option.buildingId());
            boolean hovered = inside(mouseX, mouseY, listX, y, listW, listRowH - 2);
            int rowBg = selected ? 0x9A35506A : (hovered ? 0x6A2A3E56 : 0x4B1E2E40);
            graphics.fill(listX, y, listX + listW, y + listRowH - 2, rowBg);

            graphics.drawString(this.font, option.displayName(), listX + 8, y + 6, COLOR_TEXT_MAIN, false);
            drawSmallText(graphics, Component.literal(option.buildingId()), listX + 8, y + 20, COLOR_TEXT_MUTED, 0.85f);

            String occupancy = option.animalCount() + "/" + option.capacity();
            drawSmallText(graphics, Component.literal(occupancy), listX + listW - 44, y + 20, COLOR_TEXT_MUTED, 0.85f);
        }

        if (filtered.isEmpty()) {
            graphics.drawString(this.font, Component.translatable("stardewcraft.animal.purchase.no_building"), listX + 8, listY + 8, 0xFFE39191, false);
        }
    }

    private void drawActionIcons(GuiGraphics graphics, int mouseX, int mouseY) {
        boolean canConfirm = stage == Stage.ANIMAL_SELECT
            ? selectedAnimalOption() != null && selectedAnimalOption().unlocked()
            : selectedAnimalOption() != null && selectedBuildingOption() != null && payload.playerMoney() >= selectedAnimalOption().price();

        float confirmScale = inside(mouseX, mouseY, confirmIconX - 22, confirmIconY - 22, 44, 44) ? 2.85f : 2.6f;
        float cancelScale = inside(mouseX, mouseY, cancelIconX - 22, cancelIconY - 22, 44, 44) ? 2.85f : 2.6f;

        drawScaledIcon(graphics, OK_ICON, confirmIconX, confirmIconY, confirmScale, canConfirm ? 0xFFFFFFFF : 0x77FFFFFF);
        drawScaledIcon(graphics, NO_ICON, cancelIconX, cancelIconY, cancelScale, 0xFFFFFFFF);
    }

    private void drawDiceIcon(GuiGraphics graphics) {
        // Fixed-size dice icon, no hover scaling per request.
        graphics.pose().pushPose();
        graphics.pose().translate(diceIconX - 8, diceIconY - 8, 0);
        graphics.pose().scale(1.6f, 1.6f, 1.0f);
        graphics.blit(DICE_ICON, 0, 0, 0, 0, 10, 10, 10, 10);
        graphics.pose().popPose();
    }

    private void drawHoverInfo(GuiGraphics graphics, int mouseX, int mouseY) {
        if (stage != Stage.ANIMAL_SELECT) {
            return;
        }

        int idx = hoveredAnimalIndex(mouseX, mouseY);
        if (idx < 0 || idx >= payload.animalOptions().size()) {
            return;
        }

        OpenAnimalPurchaseScreenPayload.AnimalOption option = payload.animalOptions().get(idx);
        int cardW = 220;
        int cardH = option.unlocked() ? 70 : 86;
        int x = Math.min(mouseX + 14, this.width - cardW - 8);
        int y = Math.min(mouseY + 14, this.height - cardH - 8);

        graphics.fillGradient(x, y, x + cardW, y + cardH, 0xF0192431, 0xF0131D28);
        graphics.fill(x, y, x + cardW, y + 1, COLOR_BORDER);
        graphics.fill(x, y + cardH - 1, x + cardW, y + cardH, COLOR_BORDER);
        graphics.fill(x, y, x + 1, y + cardH, COLOR_BORDER);
        graphics.fill(x + cardW - 1, y, x + cardW, y + cardH, COLOR_BORDER);

        String name = resolveDisplayName(option.displayName(), option.animalTypeId());
        graphics.drawString(this.font, name, x + 8, y + 8, COLOR_TEXT_MAIN, false);

        graphics.blit(GOLD_ICON, x + 8, y + 24, 0, 0, 16, 16, 16, 16);
        graphics.drawString(this.font, String.valueOf(option.price()), x + 26, y + 28, COLOR_ACCENT, false);

        Component desc = Component.translatable(option.descriptionKey());
        graphics.drawString(this.font, this.font.split(desc, cardW - 16).get(0), x + 8, y + 46, COLOR_TEXT_SUB, false);

        if (!option.unlocked()) {
            Component lock = Component.translatable(option.lockReasonKey());
            graphics.drawString(this.font, this.font.split(lock, cardW - 16).get(0), x + 8, y + 60, 0xFFE39E9E, false);
        }
    }

    private void drawMoneyLine(GuiGraphics graphics, int x, int y, int amount) {
        graphics.blit(GOLD_ICON, x, y - 2, 0, 0, 16, 16, 16, 16);
        graphics.drawString(this.font, String.valueOf(amount), x + 18, y + 2, COLOR_ACCENT, false);
    }

    private void drawAnimalIcon(GuiGraphics graphics, String animalTypeId, int centerX, int centerY, float scale) {
        ResourceLocation icon = resolveAnimalIcon(animalTypeId);
        int src = 32;
        int drawX = centerX - Math.round(src * scale / 2f);
        int drawY = centerY - Math.round(src * scale / 2f);

        graphics.pose().pushPose();
        graphics.pose().translate(drawX, drawY, 0);
        graphics.pose().scale(scale, scale, 1.0f);
        graphics.blit(icon, 0, 0, 0, 0, src, src, src, src);
        graphics.pose().popPose();
    }

    private void drawAnimalSilhouette(GuiGraphics graphics, String animalTypeId, int centerX, int centerY, float scale) {
        ResourceLocation icon = resolveAnimalIcon(animalTypeId);
        int src = 32;
        int drawX = centerX - Math.round(src * scale / 2f);
        int drawY = centerY - Math.round(src * scale / 2f);

        graphics.setColor(0f, 0f, 0f, 0.96f);
        graphics.pose().pushPose();
        graphics.pose().translate(drawX, drawY, 0);
        graphics.pose().scale(scale, scale, 1.0f);
        graphics.blit(icon, 0, 0, 0, 0, src, src, src, src);
        graphics.pose().popPose();
        graphics.setColor(1f, 1f, 1f, 1f);
    }

    private void drawScaledIcon(GuiGraphics graphics, ResourceLocation icon, int centerX, int centerY, float scale, int tint) {
        int a = (tint >>> 24) & 0xFF;
        int r = (tint >>> 16) & 0xFF;
        int g = (tint >>> 8) & 0xFF;
        int b = tint & 0xFF;
        graphics.setColor(r / 255f, g / 255f, b / 255f, a / 255f);

        graphics.pose().pushPose();
        graphics.pose().translate(centerX, centerY, 0);
        graphics.pose().scale(scale, scale, 1.0f);
        graphics.blit(icon, -8, -8, 0, 0, 16, 16, 16, 16);
        graphics.pose().popPose();

        graphics.setColor(1f, 1f, 1f, 1f);
    }

    private int hoveredAnimalIndex(int mouseX, int mouseY) {
        for (int i = 0; i < payload.animalOptions().size(); i++) {
            int col = i % GRID_COLS;
            int row = i / GRID_COLS;
            if (row >= GRID_ROWS) {
                break;
            }

            int x = gridX + col * SLOT_W;
            int y = gridY + row * SLOT_H;
            if (inside(mouseX, mouseY, x, y, SLOT_W, SLOT_H)) {
                return i;
            }
        }
        return -1;
    }

    private boolean isInsideBuildingList(int mouseX, int mouseY) {
        return inside(mouseX, mouseY, listX, listY, listW, listRowH * visibleRows);
    }

    private void submitPurchase() {
        OpenAnimalPurchaseScreenPayload.AnimalOption animal = selectedAnimalOption();
        OpenAnimalPurchaseScreenPayload.BuildingOption building = selectedBuildingOption();
        if (animal == null || building == null) {
            return;
        }

        String customName = nameEditBox.getValue() == null ? "" : nameEditBox.getValue().trim();
        PacketDistributor.sendToServer(new AnimalPurchaseSubmitPayload(animal.animalTypeId(), building.buildingId(), customName));
        onClose();
    }

    private void rollRandomName() {
        nameEditBox.setValue(defaultRolledName());
    }

    private String defaultRolledName() {
        OpenAnimalPurchaseScreenPayload.AnimalOption option = selectedAnimalOption();
        if (option == null) {
            return "";
        }
        String[] pool = NAME_POOLS.get(option.animalTypeId());
        if (pool == null || pool.length == 0) {
            return resolveDisplayName(option.displayName(), option.animalTypeId());
        }
        return pool[random.nextInt(pool.length)];
    }

    private List<OpenAnimalPurchaseScreenPayload.BuildingOption> filteredBuildings() {
        OpenAnimalPurchaseScreenPayload.AnimalOption animal = selectedAnimalOption();
        if (animal == null) {
            return List.of();
        }

        List<OpenAnimalPurchaseScreenPayload.BuildingOption> out = new ArrayList<>();
        for (OpenAnimalPurchaseScreenPayload.BuildingOption building : payload.buildingOptions()) {
            if (!animal.family().equalsIgnoreCase(building.family())) {
                continue;
            }
            if (building.tier() < animal.requiredTier()) {
                continue;
            }
            if (building.animalCount() >= building.capacity()) {
                continue;
            }
            out.add(building);
        }
        return out;
    }

    private void resetBuildingSelection() {
        selectedBuilding = -1;
        buildingScroll = 0;
        List<OpenAnimalPurchaseScreenPayload.BuildingOption> filtered = filteredBuildings();
        if (!filtered.isEmpty()) {
            selectedBuilding = findBuildingIndexById(filtered.get(0).buildingId());
        }
    }

    private int firstUnlockedAnimal() {
        for (int i = 0; i < payload.animalOptions().size(); i++) {
            if (payload.animalOptions().get(i).unlocked()) {
                return i;
            }
        }
        return payload.animalOptions().isEmpty() ? -1 : 0;
    }

    private OpenAnimalPurchaseScreenPayload.AnimalOption selectedAnimalOption() {
        if (selectedAnimal < 0 || selectedAnimal >= payload.animalOptions().size()) {
            return null;
        }
        return payload.animalOptions().get(selectedAnimal);
    }

    private OpenAnimalPurchaseScreenPayload.BuildingOption selectedBuildingOption() {
        if (selectedBuilding < 0 || selectedBuilding >= payload.buildingOptions().size()) {
            return null;
        }
        return payload.buildingOptions().get(selectedBuilding);
    }

    private int findBuildingIndexById(String buildingId) {
        for (int i = 0; i < payload.buildingOptions().size(); i++) {
            if (payload.buildingOptions().get(i).buildingId().equals(buildingId)) {
                return i;
            }
        }
        return -1;
    }

    private boolean isSelectedBuilding(String buildingId) {
        OpenAnimalPurchaseScreenPayload.BuildingOption selected = selectedBuildingOption();
        return selected != null && selected.buildingId().equals(buildingId);
    }

    private ResourceLocation resolveAnimalIcon(String animalTypeId) {
        String key = animalTypeId == null ? "" : animalTypeId.toLowerCase(Locale.ROOT);
        return switch (key) {
            case "white_chicken" -> icon("icon_white_chicken.png");
            case "duck" -> icon("icon_duck.png");
            case "rabbit" -> icon("icon_rabbit.png");
            case "cow" -> icon("icon_cow.png");
            case "goat" -> icon("icon_goat.png");
            case "sheep" -> icon("icon_sheep.png");
            case "pig" -> icon("icon_pig.png");
            default -> icon("icon_white_chicken.png");
        };
    }

    private ResourceLocation icon(String fileName) {
        return ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/animal_query/" + fileName);
    }

    private String resolveDisplayName(String rawName, String animalTypeId) {
        if (rawName != null && !rawName.isBlank() && !rawName.startsWith("entity.")) {
            return rawName;
        }
        String key = animalTypeId == null ? "animal" : animalTypeId.replace('_', ' ');
        String[] parts = key.split(" ");
        StringBuilder out = new StringBuilder();
        for (String part : parts) {
            if (part.isBlank()) {
                continue;
            }
            if (!out.isEmpty()) {
                out.append(' ');
            }
            out.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        return out.isEmpty() ? "Animal" : out.toString();
    }

    private void drawWindow(GuiGraphics graphics, int x, int y, int w, int h) {
        graphics.fillGradient(x, y, x + w, y + h, COLOR_PANEL, 0xEC111B28);
        graphics.fill(x + 2, y + 2, x + w - 2, y + 30, COLOR_PANEL_DARK);
        drawBorder(graphics, x, y, w, h, COLOR_BORDER);
        graphics.fill(x + 1, y + 1, x + w - 1, y + 2, COLOR_ACCENT);
    }

    private void drawSection(GuiGraphics graphics, int x, int y, int w, int h) {
        graphics.fillGradient(x, y, x + w, y + h, COLOR_SECTION, 0xCB172433);
        drawBorder(graphics, x, y, w, h, 0xFF31475D);
    }

    private void drawBorder(GuiGraphics graphics, int x, int y, int w, int h, int color) {
        graphics.fill(x, y, x + w, y + 1, color);
        graphics.fill(x, y + h - 1, x + w, y + h, color);
        graphics.fill(x, y, x + 1, y + h, color);
        graphics.fill(x + w - 1, y, x + w, y + h, color);
    }

    private void drawSmallText(GuiGraphics graphics, Component text, int x, int y, int color, float scale) {
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        graphics.pose().scale(scale, scale, 1.0f);
        graphics.drawString(this.font, text, 0, 0, color, false);
        graphics.pose().popPose();
    }

    private boolean inside(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    private void computeLayout() {
        panelW = Math.min(BASE_WIDTH, this.width - 18);
        panelH = Math.min(BASE_HEIGHT, this.height - 18);
        panelX = (this.width - panelW) / 2;
        panelY = (this.height - panelH) / 2;

        gridX = panelX + 48;
        gridY = panelY + 62;

        leftX = panelX + 16;
        leftY = panelY + 44;
        leftW = 230;
        leftH = panelH - 72;

        rightX = leftX + leftW + 12;
        rightY = leftY;
        rightW = panelX + panelW - 16 - rightX;
        rightH = leftH;
    }

    private void positionDynamicControls() {
        confirmIconX = panelX + panelW - 34;
        confirmIconY = panelY + panelH - 34;
        cancelIconX = panelX + panelW - 88;
        cancelIconY = panelY + panelH - 34;

        nameEditBox.setX(rightX + 10);
        nameEditBox.setY(panelY + panelH - 48);
        nameEditBox.setWidth(220);

        diceIconX = nameEditBox.getX() + nameEditBox.getWidth() + 20;
        diceIconY = nameEditBox.getY() + 9;
    }

    private void applyStageVisibility() {
        boolean finalize = stage == Stage.BUILDING_AND_NAME;
        nameEditBox.visible = finalize;
        nameEditBox.active = finalize;
        if (!finalize) {
            nameEditBox.setFocused(false);
            setFocused(null);
        }
    }
}
