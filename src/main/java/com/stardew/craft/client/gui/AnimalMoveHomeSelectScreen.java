package com.stardew.craft.client.gui;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.network.payload.AnimalMoveHomeSelectPayload;
import com.stardew.craft.network.payload.OpenAnimalMoveHomeScreenPayload;
import com.stardew.craft.sound.ModSounds;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("null")
public class AnimalMoveHomeSelectScreen extends Screen {

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

    private static final int UI_WIDTH = 340;
    private static final int UI_HEIGHT = 200;

    private static final int BG_OVERLAY = 0xD8111116;
    private static final int TITLE_COLOR = 0xFFF7F2DB; 
    private static final int TEXT_MUTED = 0xFFA0A0A0;

    private final OpenAnimalMoveHomeScreenPayload payload;
    private final List<OpenAnimalMoveHomeScreenPayload.BuildingOption> options;
    private final List<String> cachedOccupancyStrings = new ArrayList<>();

    private int scrollRow = 0;
    private float[] itemHoverScales;
    
    // Smooth scroll variables
    private float visualScroll = 0;

    private static final int VISIBLE_ROWS = 5;
    private static final int ITEM_HEIGHT = 34;

    public AnimalMoveHomeSelectScreen(OpenAnimalMoveHomeScreenPayload payload) {
        super(Component.translatable("container.stardew_craft.animal_move_home"));
        this.payload = payload;
        this.options = payload.options();
        this.itemHoverScales = new float[options.size()];
        for (int i = 0; i < itemHoverScales.length; i++) {
            itemHoverScales[i] = 1.0f;
            cachedOccupancyStrings.add(options.get(i).animalCount() + " / " + options.get(i).capacity());
        }
    }

    @Override
    protected void init() {
        super.init();
        scrollRow = 0;
        visualScroll = 0;
    }

    @Override
    public void tick() {
        super.tick();
        visualScroll += (scrollRow - visualScroll) * 0.3f;
    }

    private void capScroll() {
        int maxScroll = Math.max(0, options.size() - VISIBLE_ROWS);
        scrollRow = Mth.clamp(scrollRow, 0, maxScroll);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (scrollY > 0) {
            scrollRow -= 1;
            capScroll();
            return true;
        } else if (scrollY < 0) {
            scrollRow += 1;
            capScroll();
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    private int getPanelX() {
        return (width - UI_WIDTH) / 2;
    }

    private int getPanelY() {
        return (height - UI_HEIGHT) / 2;
    }

    private int getListX() {
        return getPanelX() + 150;
    }

    private int getListY() {
        return getPanelY() + 20;
    }

    @Override
    public void render(@Nonnull GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(g, mouseX, mouseY, partialTick);

        int px = getPanelX();
        int py = getPanelY();

        g.fill(px - 16, py - 10, px + UI_WIDTH + 16, py + UI_HEIGHT + 10, BG_OVERLAY);
        g.fillGradient(px + 140, py + 10, px + 141, py + UI_HEIGHT - 10, 0x40FFFFFF, 0x00FFFFFF);

        String titleStr = this.title.getString();
        g.drawString(this.font, titleStr, px + 150, py + 6, TITLE_COLOR, true);

        int totalItems = options.size();
        String pageTxt = totalItems + " 个建筑";
        g.drawString(this.font, pageTxt, px + UI_WIDTH - this.font.width(pageTxt), py + 6, TEXT_MUTED, false);

        updateVisualFocus(mouseX, mouseY);
        drawElegantShowcase(g, px + 10, py + 10);

        int listX = getListX();
        int listY = getListY();

        float currentVisScroll = Mth.lerp(partialTick, visualScroll, visualScroll); // close enough

        g.enableScissor(px + 140, py + 18, px + UI_WIDTH + 16, py + UI_HEIGHT);

        for (int i = 0; i < options.size(); i++) {
            float rowYOffset = (i - currentVisScroll) * ITEM_HEIGHT;
            if (rowYOffset < -ITEM_HEIGHT || rowYOffset > UI_HEIGHT) continue;

            int cx = listX;
            int cy = listY + (int)rowYOffset;

            OpenAnimalMoveHomeScreenPayload.BuildingOption option = options.get(i);
            float scale = itemHoverScales[i];

            g.pose().pushPose();
            g.pose().translate(cx + 80, cy + ITEM_HEIGHT / 2.0f, 0);

            if (scale > 1.0f) {
                g.pose().scale(scale, scale, 1.0f);
            }
            g.pose().translate(-80, -ITEM_HEIGHT / 2.0f, 0);

            // Draw item background
            boolean isCurrent = option.buildingId().equals(payload.currentBuildingId());
            boolean selectable = option.selectable();

            int bgColor = isCurrent ? 0x6A3F3320 : (selectable ? 0x4A000000 : 0x4A301010);
            g.fill(10, 2, 170, ITEM_HEIGHT - 2, bgColor);

            if (isCurrent) {
                int goldC = (0xFF << 24) | (255 << 16) | (200 << 8) | 50;
                g.renderOutline(9, 1, 162, ITEM_HEIGHT - 3, goldC);
            }

            int textColor = isCurrent ? 0xFFE0C080 : (selectable ? 0xFFFFFFFF : 0xFF888888);
            String rawId = option.buildingId();
            String name = rawId; 
            if (name.length() > 16) name = name.substring(0, 15) + "...";

            g.drawString(this.font, name, 18, 6, textColor, true);

            String occupancy = cachedOccupancyStrings.get(i);
            g.drawString(this.font, occupancy, 18, 18, 0xFFAAAAAA, false);

            if (!selectable) {
                String deny = isCurrent ? "当前" : "已满";
                g.drawString(this.font, deny, 140, 12, 0xFFFF6666, true);
            } else {
                g.drawString(this.font, "可用", 140, 12, 0xFF77FF77, true);
            }

            g.pose().popPose();
        }

        g.disableScissor();
    }

    private void updateVisualFocus(int mouseX, int mouseY) {
        int listX = getListX();
        int listY = getListY();

        for (int i = 0; i < options.size(); i++) {
            float rowYOffset = (i - visualScroll) * ITEM_HEIGHT;
            int cx = listX + 10;
            int cy = listY + (int)rowYOffset + 2;

            boolean isHover = mouseX >= cx && mouseX <= cx + 160 &&
                              mouseY >= cy && mouseY <= cy + ITEM_HEIGHT - 4;

            if (isHover && rowYOffset >= -5 && rowYOffset <= UI_HEIGHT - 20) {
                itemHoverScales[i] = Mth.lerp(0.3f, itemHoverScales[i], 1.05f);
            } else {
                itemHoverScales[i] = Mth.lerp(0.15f, itemHoverScales[i], 1.0f);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int listX = getListX();
            int listY = getListY();

            for (int i = 0; i < options.size(); i++) {
                float rowYOffset = (i - visualScroll) * ITEM_HEIGHT;
                if (rowYOffset < -ITEM_HEIGHT || rowYOffset > UI_HEIGHT) continue;

                int cx = listX + 10;
                int cy = listY + (int)rowYOffset + 2;

                if (mouseX >= cx && mouseX <= cx + 160 && mouseY >= cy && mouseY <= cy + ITEM_HEIGHT - 4) {
                    onConfirm(i);
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void onConfirm(int index) {
        OpenAnimalMoveHomeScreenPayload.BuildingOption selected = options.get(index);
        if (!selected.selectable()) {
            playUi(ModSounds.SMALL_SELECT.get(), 0.72f, 0.84f);
            return;
        }
        PacketDistributor.sendToServer(new AnimalMoveHomeSelectPayload(payload.animalId(), selected.buildingId()));
        playUi(ModSounds.NEW_RECIPE.get(), 0.88f, 1.0f);
        onClose();
    }

    private void drawElegantShowcase(GuiGraphics g, int px, int py) {
        int width = 126; 

        g.pose().pushPose();
        float titleScale = 1.3f;
        String printTitle = "动物搬迁";
        int tx = px + (width - (int)(this.font.width(printTitle) * titleScale)) / 2;
        g.pose().translate(tx, py, 0);
        g.pose().scale(titleScale, titleScale, 1.0f);
        
        int shimmerR = 255;
        int shimmerG = 200 + (int)(30 * Math.sin(System.currentTimeMillis() / 250.0));
        int shimmerB = 100;
        int shimmerColor = (shimmerR << 16) | (shimmerG << 8) | shimmerB;
        g.drawString(this.font, printTitle, 0, 0, 0xFF000000 | shimmerColor, true);
        g.pose().popPose();

        int areaY = py + 30;
        int areaH = 80;
        
        float floatY = (float)Math.sin((System.currentTimeMillis() % 6000) / 6000.0f * Math.PI * 2) * 5.0f;
        float shadowScale = 1.0f - (floatY + 5.0f) / 20.0f;
        int shadowWidth = (int)(30 * shadowScale);
        g.fillGradient(px + width / 2 - shadowWidth, areaY + areaH + 16, px + width / 2 + shadowWidth, areaY + areaH + 22, 0xAA000000, 0x00000000);

        g.pose().pushPose();
        g.pose().translate(px + width / 2 - 16, areaY + areaH / 2 - 16 + floatY, 150); 
        g.pose().scale(2.0f, 2.0f, 1.0f);
        ResourceLocation icon = resolveAnimalIcon();
        g.blit(icon, 0, 0, 0, 0, 32, 32, 32, 32);
        g.pose().popPose();

        String hint = "点击右侧建筑立即完成搬迁";
        List<net.minecraft.util.FormattedCharSequence> descLines = this.font.split(Component.literal(hint), width - 4);
        int descY = areaY + areaH + 34;
        for (int i = 0; i < descLines.size(); i++) {
            int dx = px + (width - this.font.width(descLines.get(i))) / 2;
            g.drawString(this.font, descLines.get(i), dx, descY, 0xFFAAAAAA, true);
            descY += 10;
        }
    }

    private ResourceLocation resolveAnimalIcon() {
        return switch (payload.animalTypeId()) {
            case "white_chicken" -> ICON_WHITE_CHICKEN;
            case "golden_chicken" -> ICON_GOLDEN_CHICKEN;
            case "duck" -> ICON_DUCK;
            case "void_chicken" -> ICON_VOID_CHICKEN;
            case "rabbit" -> ICON_RABBIT;
            case "ostrich" -> ICON_OSTRICH;
            case "dinosaur" -> ICON_DINOSAUR;
            case "cow" -> ICON_COW;
            case "goat" -> ICON_GOAT;
            case "sheep" -> ICON_SHEEP;
            case "pig" -> ICON_PIG;
            default -> ICON_COW;
        };
    }

    private void playUi(SoundEvent event, float volume, float pitch) {
        if (this.minecraft != null && this.minecraft.player != null) {
            this.minecraft.player.playSound(event, volume, pitch);
        }
    }
}
