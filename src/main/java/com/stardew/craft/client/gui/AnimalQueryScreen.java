package com.stardew.craft.client.gui;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.menu.AnimalQueryMenu;
import com.stardew.craft.network.payload.AnimalQueryActionPayload;
import com.stardew.craft.network.payload.AnimalRenamePayload;
import com.stardew.craft.sound.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nonnull;
import java.util.List;

@SuppressWarnings("null")
public class AnimalQueryScreen extends AbstractContainerScreen<AnimalQueryMenu> {

    private static final int BASE_WIDTH = 668;
    private static final int BASE_HEIGHT = 394;
    private static final int HEART_SPACING = 24;

    private static final ResourceLocation HEART_EMPTY = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/animal_query/heart_empty.png");
    private static final ResourceLocation HEART_HALF_BASE = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/animal_query/heart_half_base.png");
    private static final ResourceLocation HEART_HALF_FILL = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/animal_query/heart_half_fill.png");
    private static final ResourceLocation SELL_ICON = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/animal_query/sell_icon.png");
    private static final ResourceLocation MOVE_ICON = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/animal_query/move_icon.png");
    private static final ResourceLocation OK_ICON = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/animal_query/ok_yes_tile46.png");
    private static final ResourceLocation NO_ICON = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/animal_query/cancel_no_tile47.png");
    private static final ResourceLocation REPRO_OFF_ICON = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/animal_query/repro_off.png");
    private static final ResourceLocation REPRO_ON_ICON = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/animal_query/repro_on.png");
    private static final ResourceLocation GOLDEN_CRACKER_ICON = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/item/misc/golden_animal_cracker.png");
    private static final ResourceLocation GOLD_ICON = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/gold_icon.png");

    private static final ResourceLocation ICON_WHITE_CHICKEN = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/animal_query/icon_white_chicken.png");
    private static final ResourceLocation ICON_GOLDEN_CHICKEN = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/animal_query/icon_golden_chicken.png");
    private static final ResourceLocation ICON_DUCK = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/animal_query/icon_duck.png");
    private static final ResourceLocation ICON_VOID_CHICKEN = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/animal_query/icon_void_chicken.png");
    private static final ResourceLocation ICON_RABBIT = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/animal_query/icon_rabbit.png");
    private static final ResourceLocation ICON_OSTRICH = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/animal_query/icon_ostrich.png");
    private static final ResourceLocation ICON_DINOSAUR = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/animal_query/icon_dinosaur.png");
    private static final ResourceLocation ICON_COW = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/animal_query/icon_cow.png");
    private static final ResourceLocation ICON_GOAT = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/animal_query/icon_goat.png");
    private static final ResourceLocation ICON_SHEEP = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/animal_query/icon_sheep.png");
    private static final ResourceLocation ICON_PIG = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/animal_query/icon_pig.png");

    private static final int COLOR_OVERLAY = 0xA5000000;
    private static final int COLOR_PANEL = 0xEF182332;
    private static final int COLOR_PANEL_DARK = 0xE8162130;
    private static final int COLOR_SECTION = 0xD9243346;
    private static final int COLOR_ACCENT = 0xFFE0B464;
    private static final int COLOR_TEXT_MAIN = 0xFFF5EBD8;
    private static final int COLOR_TEXT_SUB = 0xFFB8C5D1;
    private static final int COLOR_BORDER = 0xFF3C5368;

    private int panelX;
    private int panelY;
    private int panelW;
    private int panelH;

    private int profileX;
    private int profileY;
    private int profileW;
    private int profileH;

    private int infoX;
    private int infoY;
    private int infoW;
    private int infoH;

    private int moodX;
    private int moodY;
    private int moodW;
    private int moodH;

    private int actionX;
    private int actionY;

    private int okBtnX;
    private int okBtnY;
    private int sellBtnX;
    private int sellBtnY;
    private int moveBtnX;
    private int moveBtnY;
    private int reproBtnX;
    private int reproBtnY;

    private int nameAreaX;
    private int nameAreaY;
    private int nameAreaW;
    private int nameAreaH;

    private boolean confirmingSell = false;
    private boolean editingName = false;

    private float okScale = 2.6f;
    private float sellScale = 2.6f;
    private float moveScale = 2.6f;
    private float reproScale = 2.6f;
    private float yesScale = 2.6f;
    private float noScale = 2.6f;

    private String hoverText = "";
    private EditBox nameEditBox;
    private String lastSubmittedName = "";

    public AnimalQueryScreen(AnimalQueryMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = BASE_WIDTH;
        this.imageHeight = BASE_HEIGHT;
        this.lastSubmittedName = title.getString();
    }

    @Override
    protected void init() {
        super.init();
        computeLayout();
        this.nameEditBox = new EditBox(this.font, 0, 0, 160, 18, Component.translatable("stardewcraft.animal.query.rename_hint"));
        this.nameEditBox.setMaxLength(48);
        this.nameEditBox.setBordered(true);
        this.nameEditBox.setValue(this.lastSubmittedName);
        this.addRenderableWidget(this.nameEditBox);
        setEditingName(false);
        updateNameBoxLayout();
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        computeLayout();

        hoverText = "";
        boolean hoverOk = inside(mouseX, mouseY, okBtnX - 22, okBtnY - 22, 44, 44);
        boolean hoverSell = inside(mouseX, mouseY, sellBtnX - 22, sellBtnY - 22, 44, 44);
        boolean hoverMove = inside(mouseX, mouseY, moveBtnX - 22, moveBtnY - 22, 44, 44);
        boolean hoverRepro = this.menu.canToggleReproduction() && inside(mouseX, mouseY, reproBtnX - 22, reproBtnY - 22, 44, 44);
        boolean hoverName = !editingName && inside(mouseX, mouseY, nameAreaX, nameAreaY, nameAreaW, nameAreaH);

        if (hoverSell) {
            hoverText = Component.translatable("stardewcraft.animal.query.hover.sell", this.menu.getEstimatedSellPrice()).getString();
        } else if (hoverMove) {
            hoverText = Component.translatable("stardewcraft.animal.query.hover.move").getString();
        } else if (hoverRepro) {
            hoverText = Component.translatable("stardewcraft.animal.query.hover.repro").getString();
        } else if (hoverOk) {
            hoverText = Component.translatable("gui.done").getString();
        } else if (hoverName) {
            hoverText = "点击以修改名字";
        }

        okScale = approach(okScale, hoverOk ? 2.85f : 2.6f);
        sellScale = approach(sellScale, hoverSell ? 2.85f : 2.6f);
        moveScale = approach(moveScale, hoverMove ? 2.85f : 2.6f);
        reproScale = approach(reproScale, hoverRepro ? 2.85f : 2.6f);

        updateNameBoxLayout();

        graphics.fill(0, 0, this.width, this.height, COLOR_OVERLAY);

        drawWindow(graphics, panelX, panelY, panelW, panelH);
        drawSection(graphics, profileX, profileY, profileW, profileH);
        drawSection(graphics, infoX, infoY, infoW, infoH);
        drawSection(graphics, moodX, moodY, moodW, moodH);

        graphics.drawString(this.font, Component.literal("Animal Query"), panelX + 14, panelY + 10, COLOR_ACCENT, false);
        drawSellPrice(graphics);

        drawAnimalIcon(graphics);
        if (!editingName) {
            drawNameDisplay(graphics, hoverName);
        }

        drawInfoContent(graphics);
        drawMoodContent(graphics);
        drawFriendshipRow(graphics);
        drawGoldenCrackerIndicator(graphics);

        drawActionIcon(graphics, OK_ICON, okBtnX, okBtnY, okScale);
        drawActionIcon(graphics, SELL_ICON, sellBtnX, sellBtnY, sellScale);
        drawActionIcon(graphics, MOVE_ICON, moveBtnX, moveBtnY, moveScale);
        if (this.menu.canToggleReproduction()) {
            drawActionIcon(graphics, this.menu.allowReproduction() ? REPRO_ON_ICON : REPRO_OFF_ICON, reproBtnX, reproBtnY, reproScale);
        }

        if (confirmingSell) {
            drawConfirmSellDialog(graphics, mouseX, mouseY);
        }
    }

    @Override
    protected void renderLabels(@Nonnull GuiGraphics graphics, int mouseX, int mouseY) {
    }

    @Override
    public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderTransparentBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
        if (!hoverText.isBlank() && !confirmingSell) {
            graphics.renderTooltip(this.font, Component.literal(hoverText), mouseX, mouseY);
        }
    }

    @Override
    protected void renderTooltip(@Nonnull GuiGraphics graphics, int x, int y) {
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        computeLayout();
        int mx = (int) mouseX;
        int my = (int) mouseY;

        if (button == 1) {
            playUi(ModSounds.SMALL_SELECT.get(), 0.8f, 1.0f);
            commitNameAndCloseEdit();
            PacketDistributor.sendToServer(new AnimalQueryActionPayload(AnimalQueryActionPayload.Action.CLOSE, false));
            this.onClose();
            return true;
        }

        if (confirmingSell) {
            if (handleConfirmDialogClick(mx, my)) {
                return true;
            }
        }

        if (button != 0) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        if (editingName && this.nameEditBox != null && this.nameEditBox.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        if (!editingName && inside(mx, my, nameAreaX, nameAreaY, nameAreaW, nameAreaH)) {
            setEditingName(true);
            playUi(ModSounds.SMALL_SELECT.get(), 0.7f, 1.1f);
            return true;
        }

        if (inside(mx, my, okBtnX - 22, okBtnY - 22, 44, 44)) {
            playUi(ModSounds.SMALL_SELECT.get(), 0.8f, 1.0f);
            commitNameAndCloseEdit();
            PacketDistributor.sendToServer(new AnimalQueryActionPayload(AnimalQueryActionPayload.Action.CLOSE, false));
            this.onClose();
            return true;
        }

        if (inside(mx, my, sellBtnX - 22, sellBtnY - 22, 44, 44)) {
            confirmingSell = true;
            commitNameAndCloseEdit();
            playUi(ModSounds.SMALL_SELECT.get(), 0.8f, 1.0f);
            return true;
        }

        if (inside(mx, my, moveBtnX - 22, moveBtnY - 22, 44, 44)) {
            playUi(ModSounds.SMALL_SELECT.get(), 0.8f, 1.0f);
            commitNameAndCloseEdit();
            PacketDistributor.sendToServer(new AnimalQueryActionPayload(AnimalQueryActionPayload.Action.MOVE_HOME, false));
            return true;
        }

        if (this.menu.canToggleReproduction() && inside(mx, my, reproBtnX - 22, reproBtnY - 22, 44, 44)) {
            boolean next = !this.menu.allowReproduction();
            this.menu.setAllowReproductionValue(next);
            playUi(ModSounds.DRUMKIT6.get(), 0.9f, 1.0f);
            PacketDistributor.sendToServer(new AnimalQueryActionPayload(AnimalQueryActionPayload.Action.TOGGLE_REPRODUCTION, next));
            return true;
        }

        if (editingName) {
            commitNameAndCloseEdit();
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (editingName && this.nameEditBox != null && this.nameEditBox.keyPressed(keyCode, scanCode, modifiers)) {
            if (keyCode == 257 || keyCode == 335) {
                commitNameAndCloseEdit();
            }
            if (keyCode == 256) {
                setEditingName(false);
            }
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (editingName && this.nameEditBox != null && this.nameEditBox.charTyped(codePoint, modifiers)) {
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public void removed() {
        commitNameAndCloseEdit();
        super.removed();
    }

    private void drawSellPrice(GuiGraphics graphics) {
        int y = panelY + 9;
        int amount = this.menu.getEstimatedSellPrice();
        String text = String.valueOf(amount);
        int textW = this.font.width(text);
        int iconW = 16;
        int gap = 4;
        int totalW = iconW + gap + textW;
        int x = panelX + panelW - 14 - totalW;

        graphics.blit(GOLD_ICON, x, y - 2, 0, 0, 16, 16, 16, 16);
        graphics.drawString(this.font, text, x + iconW + gap, y + 2, COLOR_TEXT_MAIN, false);
    }

    private void drawAnimalIcon(GuiGraphics graphics) {
        ResourceLocation icon = resolveAnimalIcon();
        int iconPixel = 32;
        float scale = 3.85f;
        int drawW = Math.round(iconPixel * scale);
        int drawH = Math.round(iconPixel * scale);

        int cx = profileX + profileW / 2;
        int cy = profileY + (profileH - 74) / 2;
        int drawX = cx - drawW / 2;
        int drawY = cy - drawH / 2;

        graphics.pose().pushPose();
        graphics.pose().translate(drawX, drawY, 0);
        graphics.pose().scale(scale, scale, 1.0f);
        graphics.blit(icon, 0, 0, 0, 0, iconPixel, iconPixel, iconPixel, iconPixel);
        graphics.pose().popPose();
    }

    private void drawNameDisplay(GuiGraphics graphics, boolean hover) {
        String name = getCurrentName();
        float nameScale = 1.65f;
        int textW = Math.round(this.font.width(name) * nameScale);
        int textX = nameAreaX + (nameAreaW - textW) / 2;
        int textY = nameAreaY + 2;

        graphics.pose().pushPose();
        graphics.pose().translate(textX, textY, 0);
        graphics.pose().scale(nameScale, nameScale, 1.0f);
        Component boldName = Component.literal(name).withStyle(ChatFormatting.BOLD);
        graphics.drawString(this.font, boldName, 0, 0, COLOR_TEXT_MAIN, false);
        graphics.pose().popPose();

        int underlineColor = hover ? COLOR_ACCENT : COLOR_BORDER;
        graphics.fill(nameAreaX + 8, nameAreaY + nameAreaH - 2, nameAreaX + nameAreaW - 8, nameAreaY + nameAreaH - 1, underlineColor);
    }

    private void drawInfoContent(GuiGraphics graphics) {
        int textX = infoX + 14;
        int top = infoY + 14;
        int ageWeeks = Math.max(1, this.menu.getAgeDays() / 7 + 1);

        Component stageText = Component.translatable(this.menu.isBaby()
            ? "stardewcraft.animal.query.stage.baby"
            : "stardewcraft.animal.query.stage.adult");

        Component ageText = this.menu.isBaby()
            ? Component.translatable("stardewcraft.animal.query.age_weeks_baby", ageWeeks)
            : Component.translatable("stardewcraft.animal.query.age_weeks", ageWeeks);

        graphics.drawString(this.font, ageText, textX, top, COLOR_TEXT_MAIN, false);
        graphics.drawString(this.font, Component.translatable("stardewcraft.animal.query.stage", stageText), textX, top + 14, COLOR_TEXT_SUB, false);
        graphics.drawString(this.font, Component.translatable("stardewcraft.animal.query.growth", this.menu.getAgeDays(), this.menu.getDaysToMature()), textX, top + 28, COLOR_TEXT_SUB, false);
        graphics.drawString(this.font, Component.translatable(this.menu.wasPetToday()
            ? "stardewcraft.animal.query.pet_status.done"
            : "stardewcraft.animal.query.pet_status.pending"), textX, top + 42, COLOR_TEXT_SUB, false);
    }

    private void drawMoodContent(GuiGraphics graphics) {
        graphics.drawString(this.font, Component.literal("Mood"), moodX + 12, moodY + 10, COLOR_ACCENT, false);
        Component moodText = Component.translatable(this.menu.getMoodTranslationKey());
        List<FormattedCharSequence> lines = this.font.split(moodText, moodW - 24);

        int lineY = moodY + 26;
        for (FormattedCharSequence line : lines) {
            graphics.drawString(this.font, line, moodX + 12, lineY, COLOR_TEXT_SUB, false);
            lineY += this.font.lineHeight + 1;
            if (lineY > moodY + moodH - 10) {
                break;
            }
        }
    }

    private void drawFriendshipRow(GuiGraphics graphics) {
        int friendship = Math.max(0, Math.min(1000, this.menu.getFriendship()));
        int heartPoints = friendship / 100;

        int startX = infoX + 14;
        int y = infoY + infoH - 34;
        float scale = 3.0f;

        for (int i = 0; i < 5; i++) {
            int hx = startX + i * HEART_SPACING;
            drawScaledPatch(graphics, HEART_EMPTY, hx, y, scale, 7, 6, 7, 6);

            int need = (i + 1) * 2;
            if (heartPoints >= need) {
                drawScaledPatch(graphics, HEART_HALF_BASE, hx, y, scale, 7, 6, 7, 6);
            } else if (heartPoints == need - 1) {
                drawScaledPatch(graphics, HEART_HALF_BASE, hx, y, scale, 7, 6, 7, 6);
                drawScaledPatch(graphics, HEART_HALF_FILL, hx, y, scale, 4, 6, 4, 6);
            }
        }
    }

    private void drawGoldenCrackerIndicator(GuiGraphics graphics) {
        if (!this.menu.hasEatenAnimalCracker()) {
            return;
        }
        int x = infoX + 14 + (5 * HEART_SPACING) + 8;
        int y = infoY + infoH - 31;
        drawScaledIcon(graphics, GOLDEN_CRACKER_ICON, x, y, 1.25f, 16, 16, 16, 16);
    }

    private void drawActionIcon(GuiGraphics graphics, ResourceLocation icon, int centerX, int centerY, float scale) {
        drawScaledIcon(graphics, icon, centerX, centerY, scale, 16, 16, 16, 16);
    }

    private void drawConfirmSellDialog(GuiGraphics graphics, int mouseX, int mouseY) {
        int boxW = 340;
        int boxH = 168;
        int boxX = (this.width - boxW) / 2;
        int boxY = (this.height - boxH) / 2;

        drawWindow(graphics, boxX, boxY, boxW, boxH);

        int yesX = boxX + boxW / 2 - 42;
        int noX = boxX + boxW / 2 + 42;
        int y = boxY + 108;

        boolean hoverYes = inside(mouseX, mouseY, yesX - 22, y - 22, 44, 44);
        boolean hoverNo = inside(mouseX, mouseY, noX - 22, y - 22, 44, 44);
        yesScale = approach(yesScale, hoverYes ? 2.85f : 2.6f);
        noScale = approach(noScale, hoverNo ? 2.85f : 2.6f);

        drawActionIcon(graphics, OK_ICON, yesX, y, yesScale);
        drawActionIcon(graphics, NO_ICON, noX, y, noScale);

        Component confirm = Component.translatable("stardewcraft.animal.query.confirm_sell");
        graphics.drawString(this.font, confirm, boxX + (boxW - this.font.width(confirm)) / 2, boxY + 42, COLOR_TEXT_MAIN, false);
    }

    private boolean handleConfirmDialogClick(int mx, int my) {
        int boxW = 340;
        int boxH = 168;
        int boxX = (this.width - boxW) / 2;
        int boxY = (this.height - boxH) / 2;

        int yesX = boxX + boxW / 2 - 42;
        int noX = boxX + boxW / 2 + 42;
        int y = boxY + 108;

        if (inside(mx, my, yesX - 22, y - 22, 44, 44)) {
            playUi(ModSounds.NEW_RECIPE.get(), 0.9f, 1.0f);
            playUi(ModSounds.MONEY.get(), 0.9f, 1.0f);
            PacketDistributor.sendToServer(new AnimalQueryActionPayload(AnimalQueryActionPayload.Action.SELL, false));
            this.onClose();
            return true;
        }
        if (inside(mx, my, noX - 22, y - 22, 44, 44)) {
            confirmingSell = false;
            playUi(ModSounds.SMALL_SELECT.get(), 0.8f, 1.0f);
            return true;
        }
        return false;
    }

    private void computeLayout() {
        this.panelW = Math.min(BASE_WIDTH, this.width - 18);
        this.panelH = Math.min(BASE_HEIGHT, this.height - 18);
        this.panelX = (this.width - panelW) / 2;
        this.panelY = (this.height - panelH) / 2;

        this.profileX = panelX + 14;
        this.profileY = panelY + 40;
        this.profileW = 214;
        this.profileH = panelH - 54;

        this.actionX = panelX + panelW - 38;
        this.actionY = panelY + 86;

        this.infoX = profileX + profileW + 12;
        this.infoY = panelY + 40;
        this.infoW = panelW - (infoX - panelX) - 64;
        this.infoH = 198;

        this.moodX = infoX;
        this.moodY = infoY + infoH + 10;
        this.moodW = infoW;
        this.moodH = panelY + panelH - 14 - moodY;

        this.okBtnX = actionX;
        this.okBtnY = actionY + 198;
        this.sellBtnX = actionX;
        this.sellBtnY = actionY;
        this.moveBtnX = actionX;
        this.moveBtnY = actionY + 66;
        this.reproBtnX = actionX;
        this.reproBtnY = actionY + 132;

        this.nameAreaX = profileX + 10;
        this.nameAreaY = profileY + profileH - 50;
        this.nameAreaW = profileW - 20;
        this.nameAreaH = 30;
    }

    private void updateNameBoxLayout() {
        if (this.nameEditBox == null) {
            return;
        }
        int boxX = nameAreaX + 2;
        int boxY = nameAreaY + 5;
        int boxW = Math.max(120, nameAreaW - 4);

        this.nameEditBox.setX(boxX);
        this.nameEditBox.setY(boxY);
        this.nameEditBox.setWidth(boxW);
        this.nameEditBox.setHeight(18);
    }

    private void setEditingName(boolean editing) {
        this.editingName = editing;
        if (this.nameEditBox == null) {
            return;
        }
        this.nameEditBox.setVisible(editing);
        this.nameEditBox.active = editing;
        this.nameEditBox.setFocused(editing);
        if (editing) {
            this.setFocused(this.nameEditBox);
        } else {
            this.setFocused(null);
        }
    }

    private void commitNameAndCloseEdit() {
        submitRenameIfChanged();
        setEditingName(false);
    }

    private String getCurrentName() {
        if (this.nameEditBox != null && !this.nameEditBox.getValue().isBlank()) {
            return this.nameEditBox.getValue();
        }
        return this.lastSubmittedName;
    }

    private void submitRenameIfChanged() {
        if (this.nameEditBox == null) {
            return;
        }
        String normalized = this.nameEditBox.getValue().trim();
        if (normalized.isBlank()) {
            normalized = this.lastSubmittedName;
            this.nameEditBox.setValue(normalized);
        }
        if (normalized.equals(this.lastSubmittedName)) {
            return;
        }
        this.lastSubmittedName = normalized;
        PacketDistributor.sendToServer(new AnimalRenamePayload(this.menu.getAnimalId(), normalized));
    }

    private ResourceLocation resolveAnimalIcon() {
        return switch (this.menu.getVariantIndex()) {
            case 0 -> ICON_WHITE_CHICKEN;
            case 1 -> ICON_GOLDEN_CHICKEN;
            case 2 -> ICON_DUCK;
            case 3 -> ICON_VOID_CHICKEN;
            case 4 -> ICON_RABBIT;
            case 5 -> ICON_OSTRICH;
            case 6 -> ICON_DINOSAUR;
            case 7 -> ICON_COW;
            case 8 -> ICON_GOAT;
            case 9, 10 -> ICON_SHEEP;
            case 11 -> ICON_PIG;
            default -> ICON_WHITE_CHICKEN;
        };
    }

    private void drawScaledIcon(GuiGraphics graphics, ResourceLocation texture, int centerX, int centerY, float scale, int srcW, int srcH, int texW, int texH) {
        graphics.pose().pushPose();
        graphics.pose().translate(centerX, centerY, 0);
        graphics.pose().scale(scale, scale, 1.0f);
        graphics.blit(texture, -srcW / 2, -srcH / 2, 0, 0, srcW, srcH, texW, texH);
        graphics.pose().popPose();
    }

    private void drawScaledPatch(GuiGraphics graphics, ResourceLocation texture, int x, int y, float scale, int srcW, int srcH, int texW, int texH) {
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        graphics.pose().scale(scale, scale, 1.0f);
        graphics.blit(texture, 0, 0, 0, 0, srcW, srcH, texW, texH);
        graphics.pose().popPose();
    }

    private void drawWindow(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.fillGradient(x, y, x + width, y + height, COLOR_PANEL, 0xEC111B28);
        graphics.fill(x + 2, y + 2, x + width - 2, y + 30, COLOR_PANEL_DARK);
        drawBorder(graphics, x, y, width, height, COLOR_BORDER);
        graphics.fill(x + 1, y + 1, x + width - 1, y + 2, COLOR_ACCENT);
    }

    private void drawSection(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.fillGradient(x, y, x + width, y + height, COLOR_SECTION, 0xCB172433);
        drawBorder(graphics, x, y, width, height, 0xFF31475D);
    }

    private void drawBorder(GuiGraphics graphics, int x, int y, int width, int height, int color) {
        graphics.fill(x, y, x + width, y + 1, color);
        graphics.fill(x, y + height - 1, x + width, y + height, color);
        graphics.fill(x, y, x + 1, y + height, color);
        graphics.fill(x + width - 1, y, x + width, y + height, color);
    }

    private boolean inside(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    private float approach(float current, float target) {
        if (current < target) {
            return Math.min(target, current + 0.06f);
        }
        if (current > target) {
            return Math.max(target, current - 0.06f);
        }
        return current;
    }

    private void playUi(SoundEvent event, float volume, float pitch) {
        if (this.minecraft != null && this.minecraft.player != null) {
            this.minecraft.player.playSound(event, volume, pitch);
        }
    }
}
