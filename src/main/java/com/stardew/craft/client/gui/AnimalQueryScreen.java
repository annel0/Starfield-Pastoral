package com.stardew.craft.client.gui;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.menu.AnimalQueryMenu;
import com.stardew.craft.network.payload.AnimalQueryActionPayload;
import com.stardew.craft.sound.ModSounds;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nonnull;

@SuppressWarnings("null")
public class AnimalQueryScreen extends AbstractContainerScreen<AnimalQueryMenu> {

    private static final int VANILLA_WIDTH = 384;
    private static final int VANILLA_HEIGHT = 512;
    private static final int VIRTUAL_TOTAL_WIDTH = 452;
    private static final int VIRTUAL_TOTAL_HEIGHT = 512;
    private static final int VANILLA_X = (VIRTUAL_TOTAL_WIDTH - VANILLA_WIDTH) / 2;
    private static final int VANILLA_Y = 0;

    private static final int BORDER_WIDTH = 64;
    private static final int SPACE_SIDE = 64;
    private static final int SPACE_TOP = 64;
    private static final int TILE = 64;
    private static final int MENU_TEX_W = 256;
    private static final int MENU_TEX_H = 1152;

    private static final ResourceLocation MENU_TILES = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/animal_query/menu_tiles.png");
    private static final ResourceLocation HEART_EMPTY = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/animal_query/heart_empty.png");
    private static final ResourceLocation HEART_HALF_BASE = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/animal_query/heart_half_base.png");
    private static final ResourceLocation HEART_HALF_FILL = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/animal_query/heart_half_fill.png");
    private static final ResourceLocation SELL_ICON = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/animal_query/sell_icon.png");
    private static final ResourceLocation MOVE_ICON = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/animal_query/move_icon.png");
    private static final ResourceLocation OK_ICON = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/animal_query/ok_yes_tile46.png");
    private static final ResourceLocation NO_ICON = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/animal_query/cancel_no_tile47.png");
    private static final ResourceLocation ICON_WHITE_CHICKEN = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/animal_query/icon_white_chicken.png");
    private static final ResourceLocation ICON_DUCK = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/animal_query/icon_duck.png");
    private static final ResourceLocation ICON_RABBIT = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/animal_query/icon_rabbit.png");
    private static final ResourceLocation ICON_OSTRICH = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/animal_query/icon_ostrich.png");
    private static final ResourceLocation TEX_GOLDEN_CHICKEN = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/entity/animal/golden_chicken.png");
    private static final ResourceLocation TEX_VOID_CHICKEN = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/entity/animal/void_chicken.png");
    private static final ResourceLocation TEX_DINOSAUR = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/entity/animal/dinosaur.png");
    private static final ResourceLocation ICON_COW = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/animal_query/icon_cow.png");
    private static final ResourceLocation ICON_GOAT = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/animal_query/icon_goat.png");
    private static final ResourceLocation ICON_SHEEP = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/animal_query/icon_sheep.png");
    private static final ResourceLocation ICON_PIG = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/animal_query/icon_pig.png");

    private float uiScale = 1.0f;
    private int uiOriginX = 0;
    private int uiOriginY = 0;
    private static final float BASE_UI_SCALE = 0.70f;
    private static final float TEXT_SCALE = 2.0f;
    private static final int BASE_UI_Y_OFFSET = -18;

    private boolean confirmingSell = false;
    private float okScale = 0.6f;
    private float sellScale = 2.4f;
    private float moveScale = 2.4f;
    private float reproScale = 2.4f;
    private float yesScale = 0.6f;
    private float noScale = 0.6f;
    private String hoverText = "";

    public AnimalQueryScreen(AnimalQueryMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = VIRTUAL_TOTAL_WIDTH;
        this.imageHeight = VIRTUAL_TOTAL_HEIGHT;
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        updateLayout();
        int vx = toVirtualX(mouseX);
        int vy = toVirtualY(mouseY);

        hoverText = "";
        boolean hoverOk = insideVirtual(vx, vy, VANILLA_X + VANILLA_WIDTH + 4, VANILLA_Y + VANILLA_HEIGHT - 64 - BORDER_WIDTH, 64, 64);
        boolean hoverSell = insideVirtual(vx, vy, VANILLA_X + VANILLA_WIDTH + 4, VANILLA_Y + VANILLA_HEIGHT - 192 - BORDER_WIDTH, 64, 64);
        boolean hoverMove = insideVirtual(vx, vy, VANILLA_X + VANILLA_WIDTH + 4, VANILLA_Y + VANILLA_HEIGHT - 256 - BORDER_WIDTH, 64, 64);
        boolean hoverRepro = insideVirtual(vx, vy, VANILLA_X + VANILLA_WIDTH + 16, VANILLA_Y + VANILLA_HEIGHT - 128 - BORDER_WIDTH + 8, 36, 36);

        if (hoverSell) {
            hoverText = Component.translatable("stardewcraft.animal.query.hover.sell", this.menu.getEstimatedSellPrice()).getString();
        } else if (hoverMove) {
            hoverText = Component.translatable("stardewcraft.animal.query.hover.move").getString();
        } else if (hoverRepro) {
            hoverText = Component.translatable("stardewcraft.animal.query.hover.repro").getString();
        }

        okScale = approach(okScale, hoverOk ? 0.66f : 0.6f);
        sellScale = approach(sellScale, hoverSell ? 2.46f : 2.4f);
        moveScale = approach(moveScale, hoverMove ? 2.46f : 2.4f);
        reproScale = approach(reproScale, hoverRepro ? 2.46f : 2.4f);

        if (confirmingSell) {
            graphics.fill(0, 0, this.width, this.height, 0xBF000000);
        }

        graphics.pose().pushPose();
        graphics.pose().translate(uiOriginX, uiOriginY, 0);
        graphics.pose().scale(uiScale, uiScale, 1.0f);
        drawDialogueBox(graphics, VANILLA_X, VANILLA_Y + 128, VANILLA_WIDTH, VANILLA_HEIGHT - 128);

        drawScaledIcon(graphics, OK_ICON, VANILLA_X + VANILLA_WIDTH + 36, VANILLA_Y + VANILLA_HEIGHT - BORDER_WIDTH - 32, okScale, 64, 64, 64, 64);
        drawScaledIcon(graphics, SELL_ICON, VANILLA_X + VANILLA_WIDTH + 36, VANILLA_Y + VANILLA_HEIGHT - BORDER_WIDTH - 160, sellScale, 16, 16, 16, 16);
        drawScaledIcon(graphics, MOVE_ICON, VANILLA_X + VANILLA_WIDTH + 36, VANILLA_Y + VANILLA_HEIGHT - BORDER_WIDTH - 224, moveScale, 16, 16, 16, 16);

        ResourceLocation reproTex = this.menu.allowReproduction()
            ? ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/animal_query/repro_on.png")
            : ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/animal_query/repro_off.png");
        drawScaledIcon(graphics, reproTex, VANILLA_X + VANILLA_WIDTH + 16 + 18, VANILLA_Y + VANILLA_HEIGHT - 128 - BORDER_WIDTH + 8 + 18, reproScale, 9, 9, 9, 9);

        drawAnimalIcon(graphics, VANILLA_X + VANILLA_WIDTH - 128, VANILLA_Y + 192);

        drawFriendshipRow(graphics);
        drawLabels(graphics);

        if (confirmingSell) {
            int boxX = VIRTUAL_TOTAL_WIDTH / 2 - 160;
            int boxY = VIRTUAL_TOTAL_HEIGHT / 2 - 192;
            drawDialogueBox(graphics, boxX, boxY, 320, 256);

            boolean hoverYes = insideVirtual(vx, vy, VIRTUAL_TOTAL_WIDTH / 2 - 68, VIRTUAL_TOTAL_HEIGHT / 2 - 32, 64, 64);
            boolean hoverNo = insideVirtual(vx, vy, VIRTUAL_TOTAL_WIDTH / 2 + 4, VIRTUAL_TOTAL_HEIGHT / 2 - 32, 64, 64);
            yesScale = approach(yesScale, hoverYes ? 0.66f : 0.6f);
            noScale = approach(noScale, hoverNo ? 0.66f : 0.6f);

            drawScaledIcon(graphics, OK_ICON, VIRTUAL_TOTAL_WIDTH / 2 - 36, VIRTUAL_TOTAL_HEIGHT / 2, yesScale, 64, 64, 64, 64);
            drawScaledIcon(graphics, NO_ICON, VIRTUAL_TOTAL_WIDTH / 2 + 36, VIRTUAL_TOTAL_HEIGHT / 2, noScale, 64, 64, 64, 64);

            Component confirm = Component.translatable("stardewcraft.animal.query.confirm_sell");
            int textX = VIRTUAL_TOTAL_WIDTH / 2 - Math.round(this.font.width(confirm) * TEXT_SCALE / 2.0f);
            drawScaledText(graphics, confirm, textX, VIRTUAL_TOTAL_HEIGHT / 2 - 88, 0xFF3C2F20);
        }

        graphics.pose().popPose();
    }

    @Override
    protected void renderLabels(@Nonnull GuiGraphics graphics, int mouseX, int mouseY) {
    }

    @Override
    public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
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
        updateLayout();
        int vx = toVirtualX(mouseX);
        int vy = toVirtualY(mouseY);

        if (button == 1) {
            playUi(ModSounds.SMALL_SELECT.get(), 0.8f, 1.0f);
            PacketDistributor.sendToServer(new AnimalQueryActionPayload(AnimalQueryActionPayload.Action.CLOSE, false));
            this.onClose();
            return true;
        }

        if (confirmingSell) {
            if (insideVirtual(vx, vy, VIRTUAL_TOTAL_WIDTH / 2 - 68, VIRTUAL_TOTAL_HEIGHT / 2 - 32, 64, 64)) {
                playUi(ModSounds.NEW_RECIPE.get(), 0.9f, 1.0f);
                playUi(ModSounds.MONEY.get(), 0.9f, 1.0f);
                PacketDistributor.sendToServer(new AnimalQueryActionPayload(AnimalQueryActionPayload.Action.SELL, false));
                this.onClose();
                return true;
            }
            if (insideVirtual(vx, vy, VIRTUAL_TOTAL_WIDTH / 2 + 4, VIRTUAL_TOTAL_HEIGHT / 2 - 32, 64, 64)) {
                confirmingSell = false;
                playUi(ModSounds.SMALL_SELECT.get(), 0.8f, 1.0f);
                return true;
            }
        }

        if (insideVirtual(vx, vy, VANILLA_X + VANILLA_WIDTH + 4, VANILLA_Y + VANILLA_HEIGHT - 64 - BORDER_WIDTH, 64, 64)) {
            playUi(ModSounds.SMALL_SELECT.get(), 0.8f, 1.0f);
            PacketDistributor.sendToServer(new AnimalQueryActionPayload(AnimalQueryActionPayload.Action.CLOSE, false));
            this.onClose();
            return true;
        }
        if (insideVirtual(vx, vy, VANILLA_X + VANILLA_WIDTH + 4, VANILLA_Y + VANILLA_HEIGHT - 192 - BORDER_WIDTH, 64, 64)) {
            confirmingSell = true;
            playUi(ModSounds.SMALL_SELECT.get(), 0.8f, 1.0f);
            return true;
        }
        if (insideVirtual(vx, vy, VANILLA_X + VANILLA_WIDTH + 4, VANILLA_Y + VANILLA_HEIGHT - 256 - BORDER_WIDTH, 64, 64)) {
            playUi(ModSounds.SMALL_SELECT.get(), 0.8f, 1.0f);
            PacketDistributor.sendToServer(new AnimalQueryActionPayload(AnimalQueryActionPayload.Action.MOVE_HOME, false));
            return true;
        }
        if (insideVirtual(vx, vy, VANILLA_X + VANILLA_WIDTH + 16, VANILLA_Y + VANILLA_HEIGHT - 128 - BORDER_WIDTH + 8, 36, 36)) {
            boolean next = !this.menu.allowReproduction();
            this.menu.setAllowReproductionValue(next);
            playUi(ModSounds.DRUMKIT6.get(), 0.9f, 1.0f);
            PacketDistributor.sendToServer(new AnimalQueryActionPayload(AnimalQueryActionPayload.Action.TOGGLE_REPRODUCTION, next));
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void drawFriendshipRow(GuiGraphics graphics) {
        int yOffset = 0;
        int heartsY = VANILLA_Y + yOffset + 288;

        int friendship = Math.max(0, Math.min(1000, this.menu.getFriendship()));
        float loveLevel = friendship / 1000.0f;
        int halfHeart = (int) (((friendship % 200) >= 100) ? (friendship / 200) : -100);

        for (int i = 0; i < 5; i++) {
            int hx = VANILLA_X + 96 + (i * 32);
            boolean emptyHeart = loveLevel * 1000.0 <= (double) ((i + 1) * 195);
            drawScaledPatch(graphics, emptyHeart ? HEART_EMPTY : HEART_HALF_BASE, hx, heartsY, 4.0f, 7, 6, 7, 6);
            if (halfHeart == i) {
                drawScaledPatch(graphics, HEART_HALF_FILL, hx, heartsY, 4.0f, 4, 6, 4, 6);
            }
        }

    }

    private void drawLabels(GuiGraphics graphics) {
        int ageYears = (this.menu.getAgeDays() + 1) / 28 + 1;
        int textX = VANILLA_X + SPACE_SIDE + 32;

        Component stageText = Component.translatable(this.menu.isBaby()
            ? "stardewcraft.animal.query.stage.baby"
            : "stardewcraft.animal.query.stage.adult");

        drawScaledText(graphics, Component.translatable("stardewcraft.animal.query.age_years", ageYears), textX, VANILLA_Y + SPACE_TOP + 16 + 128, 0xFF3C2F20);
        drawScaledText(graphics, Component.translatable("stardewcraft.animal.query.stage", stageText), textX, VANILLA_Y + SPACE_TOP + 32 + 128, 0xFF3C2F20);
        drawScaledText(graphics, Component.translatable("stardewcraft.animal.query.growth", this.menu.getAgeDays(), this.menu.getDaysToMature()), textX, VANILLA_Y + SPACE_TOP + 48 + 128, 0xFF3C2F20);
        drawScaledText(graphics, Component.translatable(this.menu.wasPetToday()
            ? "stardewcraft.animal.query.pet_status.done"
            : "stardewcraft.animal.query.pet_status.pending"), textX, VANILLA_Y + SPACE_TOP + 64 + 128, 0xFF3C2F20);

        Component moodText = Component.translatable(this.menu.wasPetToday()
            ? "stardewcraft.animal.query.mood.happy"
            : "stardewcraft.animal.query.mood.neutral");
        int moodY = VANILLA_Y + 384 - 64 + 4;
        int moodWidth = Math.round((VANILLA_WIDTH - SPACE_SIDE * 2 - 64) / TEXT_SCALE);
        int lineY = moodY;
        for (FormattedCharSequence line : this.font.split(moodText, moodWidth)) {
            drawScaledText(graphics, line, textX, lineY, 0xFF5C4A35);
            lineY += Math.round((this.font.lineHeight + 1) * TEXT_SCALE);
        }
    }

    private void drawScaledText(GuiGraphics graphics, Component text, int x, int y, int color) {
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        graphics.pose().scale(TEXT_SCALE, TEXT_SCALE, 1.0f);
        graphics.drawString(this.font, text, 0, 0, color, false);
        graphics.pose().popPose();
    }

    private void drawScaledText(GuiGraphics graphics, FormattedCharSequence text, int x, int y, int color) {
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        graphics.pose().scale(TEXT_SCALE, TEXT_SCALE, 1.0f);
        graphics.drawString(this.font, text, 0, 0, color, false);
        graphics.pose().popPose();
    }

    private void drawScaledIcon(GuiGraphics graphics, ResourceLocation texture, int centerX, int centerY, float scale, int srcW, int srcH, int texW, int texH) {
        graphics.pose().pushPose();
        graphics.pose().translate(centerX, centerY, 0);
        graphics.pose().scale(scale, scale, 1.0f);
        graphics.blit(texture, -srcW / 2, -srcH / 2, 0, 0, srcW, srcH, texW, texH);
        graphics.pose().popPose();
    }

    private void drawDialogueBox(GuiGraphics graphics, int x, int y, int width, int height) {
        int right = x + width - TILE;
        int bottom = y + height - TILE;

        drawPatchScaled(graphics, x + 28, y + 28, width - 64, height - 64, 64, 128);
        drawPatchScaled(graphics, x + TILE, y, width - 128, TILE, 128, 0);
        drawPatchScaled(graphics, x + TILE, bottom, width - 128, TILE, 128, 192);
        drawPatchScaled(graphics, x, y + TILE, TILE, height - 128, 0, 128);
        drawPatchScaled(graphics, right, y + TILE, TILE, height - 128, 192, 128);

        graphics.blit(MENU_TILES, x, y, 0, 0, TILE, TILE, MENU_TEX_W, MENU_TEX_H);
        graphics.blit(MENU_TILES, right, y, 192, 0, TILE, TILE, MENU_TEX_W, MENU_TEX_H);
        graphics.blit(MENU_TILES, x, bottom, 0, 192, TILE, TILE, MENU_TEX_W, MENU_TEX_H);
        graphics.blit(MENU_TILES, right, bottom, 192, 192, TILE, TILE, MENU_TEX_W, MENU_TEX_H);
    }

    private void drawPatchScaled(GuiGraphics graphics, int x, int y, int width, int height, int u, int v) {
        if (width <= 0 || height <= 0) {
            return;
        }
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        graphics.pose().scale(width / (float) TILE, height / (float) TILE, 1.0f);
        graphics.blit(MENU_TILES, 0, 0, u, v, TILE, TILE, MENU_TEX_W, MENU_TEX_H);
        graphics.pose().popPose();
    }

    private void drawScaledPatch(GuiGraphics graphics, ResourceLocation texture, int x, int y, float scale, int srcW, int srcH, int texW, int texH) {
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        graphics.pose().scale(scale, scale, 1.0f);
        graphics.blit(texture, 0, 0, 0, 0, srcW, srcH, texW, texH);
        graphics.pose().popPose();
    }

    private void drawAnimalIcon(GuiGraphics graphics, int x, int y) {
        ResourceLocation tex = switch (this.menu.getVariantIndex()) {
            case 0 -> ICON_WHITE_CHICKEN;
            case 1 -> TEX_GOLDEN_CHICKEN;
            case 2 -> ICON_DUCK;
            case 3 -> TEX_VOID_CHICKEN;
            case 4 -> ICON_RABBIT;
            case 5 -> ICON_OSTRICH;
            case 6 -> TEX_DINOSAUR;
            case 7 -> ICON_COW;
            case 8 -> ICON_GOAT;
            case 9 -> ICON_SHEEP;
            case 10 -> ICON_SHEEP;
            case 11 -> ICON_PIG;
            default -> ICON_WHITE_CHICKEN;
        };

        if (tex == ICON_WHITE_CHICKEN || tex == ICON_DUCK || tex == ICON_RABBIT || tex == ICON_OSTRICH
            || tex == ICON_COW || tex == ICON_GOAT || tex == ICON_SHEEP || tex == ICON_PIG) {
            graphics.blit(tex, x, y, 0, 0, 32, 32, 16, 16, 32, 16);
            return;
        }

        drawScaledPatch(graphics, tex, x, y, 2.0f, 16, 16, 16, 16);
    }

    private float approach(float current, float target) {
        if (current < target) {
            return Math.min(target, current + 0.05f);
        }
        if (current > target) {
            return Math.max(target, current - 0.05f);
        }
        return current;
    }

    private void playUi(SoundEvent event, float volume, float pitch) {
        if (this.minecraft != null && this.minecraft.player != null) {
            this.minecraft.player.playSound(event, volume, pitch);
        }
    }

    private boolean insideVirtual(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    private void updateLayout() {
        float fitX = (this.width - 16f) / VIRTUAL_TOTAL_WIDTH;
        float fitY = (this.height - 16f) / VIRTUAL_TOTAL_HEIGHT;
        this.uiScale = Math.min(1.0f, Math.min(fitX, fitY)) * BASE_UI_SCALE;
        if (this.uiScale < 0.5f) {
            this.uiScale = 0.5f;
        }

        int drawW = Math.round(VIRTUAL_TOTAL_WIDTH * this.uiScale);
        int drawH = Math.round(VIRTUAL_TOTAL_HEIGHT * this.uiScale);
        this.uiOriginX = (this.width - drawW) / 2;
        this.uiOriginY = (this.height - drawH) / 2 + BASE_UI_Y_OFFSET;
    }

    private int toVirtualX(double mouseX) {
        return (int) Math.round((mouseX - this.uiOriginX) / this.uiScale);
    }

    private int toVirtualY(double mouseY) {
        return (int) Math.round((mouseY - this.uiOriginY) / this.uiScale);
    }
}
