package com.stardew.craft.client.gui.overnight;

import com.stardew.craft.client.hud.StardewTimeHud;
import com.stardew.craft.network.overnight.OvernightSettlementPayload;
import com.stardew.craft.sound.ModSounds;
import com.stardew.craft.time.StardewTimeManager;
import com.stardew.craft.weather.ClientWeatherCache;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Locale;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("null")
public class ShippingMenuScreen extends Screen {

    private final List<OvernightSettlementPayload.ShippedItem> shippedItems;

    private int introTimer = 3500;
    private long lastTime;

    private int[] categoryTotals = new int[6];
    private MoneyDial[] categoryDials = new MoneyDial[6];
    
    // Categories: 0: Farming, 1: Foraging, 2: Fishing, 3: Mining, 4: Other, 5: Total
    private List<List<OvernightSettlementPayload.ShippedItem>> categoryItems;

    private int currentPage = -1;
    
    private int currentTab = 0;
    private int itemsPerCategoryPage = 9;
    private boolean outro;
    private int outroFadeTimer;
    private float weatherX;
    private int moonShake = -1;
    private int timesPokedMoon;

    // UI Layout vars
    private int categoryLabelsWidth = 512;
    private int plusButtonWidth = 40;
    private int itemSlotWidth = 96;
    private int itemAndPlusButtonWidth = plusButtonWidth + itemSlotWidth + 8;
    private int totalWidth = categoryLabelsWidth + itemAndPlusButtonWidth;

    private final List<Screen> siblingScreens;

    private static class NightStar {
        int x;
        int y;
        int size;
        float baseAlpha;
        float twinkleAmp;
        float twinkleSpeed;
        float phase;
        int color;
    }

    private final List<NightStar> nightStars = new ArrayList<>();
    private int starsForWidth = -1;
    private int starsForHeight = -1;

    public ShippingMenuScreen(List<OvernightSettlementPayload.ShippedItem> shippedItems, List<Screen> siblingScreens) {
        super(Component.translatable("stardewcraft.shipping.title"));
        this.shippedItems = shippedItems;
        this.siblingScreens = siblingScreens;
        this.categoryItems = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            this.categoryItems.add(new ArrayList<>());
            this.categoryDials[i] = new MoneyDial(7);
        }
        
        parseItems();
    }

    private void parseItems() {
        for (OvernightSettlementPayload.ShippedItem item : shippedItems) {
            int category = item.category();
            if (category < 0 || category > 4) {
                category = 4; // default to Other
            }
            categoryItems.get(category).add(item);
            int itemTotal = item.pricePerItem() * item.stack().getCount();
            categoryTotals[category] += itemTotal;
        }

        // Mirror Stardew: build the Total bucket after the 0..4 categories are finalized.
        for (int i = 0; i < 5; i++) {
            categoryTotals[5] += categoryTotals[i];
            categoryItems.get(5).addAll(categoryItems.get(i));
            categoryDials[i].currentValue = categoryTotals[i];
            categoryDials[i].previousTargetValue = categoryTotals[i];
        }
        categoryDials[5].currentValue = categoryTotals[5];
        categoryDials[5].previousTargetValue = categoryTotals[5];
    }

    @Override
    protected void init() {
        super.init();
        this.lastTime = System.currentTimeMillis();
        refreshScaledLayout();
        com.stardew.craft.StardewCraft.LOGGER.info("[OVERNIGHT_CLIENT] ShippingMenuScreen.init() items={}, introTimer={}", shippedItems.size(), introTimer);
    }

    private float guiScale() {
        return this.minecraft == null ? 1.0f : (float) this.minecraft.getWindow().getGuiScale();
    }

    private int px(int stardewPixels) {
        return Math.round(stardewPixels / guiScale());
    }

    private float s4() {
        return 4.0f / guiScale();
    }

    private void playUiSound(SoundEvent sound, float volume, float pitch) {
        if (this.minecraft != null) {
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(sound, volume, pitch));
        }
    }

    private SoundEvent getCategorySoundEvent(int which) {
        return switch (which) {
            case 0 -> isFarmingAnimalProduct() ? ModSounds.CLUCK.get() : ModSounds.HARVEST.get();
            case 1 -> ModSounds.LEAFRUSTLE.get();
            case 2 -> ModSounds.BUTTON1.get();
            case 3 -> ModSounds.HAMMER.get();
            case 4 -> ModSounds.COIN.get();
            case 5 -> ModSounds.MONEY.get();
            default -> ModSounds.STONE_STEP.get();
        };
    }

    private boolean isFarmingAnimalProduct() {
        if (categoryItems.get(0).isEmpty()) {
            return false;
        }
        ItemStack first = categoryItems.get(0).get(0).stack();
        String path = first.getItemHolder().getRegisteredName();
        return path.contains("egg")
                || path.contains("milk")
                || path.contains("wool")
                || path.contains("cheese")
                || path.contains("mayonnaise")
                || path.contains("truffle");
    }

    private boolean isLeftMousePressed() {
        if (this.minecraft == null) {
            return false;
        }
        long window = this.minecraft.getWindow().getWindow();
        return GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
    }

    private void refreshScaledLayout() {
        this.categoryLabelsWidth = px(512);
        this.plusButtonWidth = px(40);
        this.itemSlotWidth = px(96);
        this.itemAndPlusButtonWidth = px(40 + 96 + 8);
        this.totalWidth = px(512 + 40 + 96 + 8);

        int stardewViewportHeight = Math.round(this.height * guiScale());
        int stardewSpaceHeight = Math.min(stardewViewportHeight, 920);
        float itemSpace = stardewSpaceHeight - 96f;
        this.itemsPerCategoryPage = Math.max(1, (int) (itemSpace / 68f));
        if (currentPage >= 0) {
            int items = categoryItems.get(currentPage).size();
            int maxTab = Math.max(0, (items - 1) / this.itemsPerCategoryPage);
            currentTab = Math.min(currentTab, maxTab);
        }
    }

    @SuppressWarnings("null")
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        refreshScaledLayout();

        long currentTime = System.currentTimeMillis();
        int delta = (int)(currentTime - lastTime);
        this.lastTime = currentTime;

        if (outro) {
            outroFadeTimer -= delta;
            if (outroFadeTimer <= 0) {
                closeToNextScreen();
                return;
            }
        }

        int prevIntro = introTimer;
        int introSpeed = isLeftMousePressed() ? 3 : 1;
        introTimer -= delta * introSpeed;
        weatherX += delta * 0.03f;
        if (moonShake > 0) {
            moonShake -= delta;
        }

        if (prevIntro >= 0 && introTimer >= 0 && prevIntro % 500 < introTimer % 500 && introTimer <= 3000) {
            int categoryThatPoppedUp = 4 - introTimer / 500;
            if (categoryThatPoppedUp > -1 && categoryThatPoppedUp < 6) {
                if (!categoryItems.get(categoryThatPoppedUp).isEmpty()) {
                    playUiSound(getCategorySoundEvent(categoryThatPoppedUp), 1.0f, 1.0f);
                    categoryDials[categoryThatPoppedUp].currentValue = 0;
                    categoryDials[categoryThatPoppedUp].previousTargetValue = 0;
                } else {
                    playUiSound(ModSounds.STONE_STEP.get(), 0.9f, 1.0f);
                }
            }
        }

        if (prevIntro >= 0 && introTimer < 0) {
            playUiSound(ModSounds.MONEY.get(), 1.0f, 1.0f);
            categoryDials[5].currentValue = 0;
            categoryDials[5].previousTargetValue = 0;
        }

        float alphaOverlay = 1.0f - (float) Math.max(0, introTimer) / 3500.0f;

        drawBackground(graphics, alphaOverlay);

        if (currentPage == -1) {
            drawSummaryPage(graphics, alphaOverlay, mouseX, mouseY);
        } else {
            drawItemDetail(graphics, mouseX, mouseY);
        }

        if (outro) {
            float t = 1.0f - (float) Math.max(0, outroFadeTimer) / 800.0f;
            int a = Math.max(0, Math.min(255, (int) (t * 255.0f)));
            graphics.fill(0, 0, this.width, this.height, (a << 24));
        }

        if (isGreenRainWeather()) {
            graphics.fill(0, 0, this.width, this.height, 0x1900FF00);
        }
    }

    private void beginOutroClose() {
        if (outro) {
            return;
        }
        outro = true;
        outroFadeTimer = 800;
        playUiSound(ModSounds.BIG_DESELECT.get(), 1.0f, 1.0f);
    }

    private void closeToNextScreen() {
        com.stardew.craft.StardewCraft.LOGGER.info("[OVERNIGHT_CLIENT] ShippingMenuScreen.closeToNextScreen() siblingCount={}",
            this.siblingScreens != null ? this.siblingScreens.size() : -1);
        if (this.siblingScreens != null && !this.siblingScreens.isEmpty()) {
            this.minecraft.setScreen(this.siblingScreens.remove(0));
        } else {
            // Overnight flow is fully done — tell the server the player just
            // regained control so any queued wake_up cutscenes can dispatch.
            net.neoforged.neoforge.network.PacketDistributor.sendToServer(
                new com.stardew.craft.cutscene.network.PlayerWokeUpPayload());
            super.onClose();
        }
    }

    private boolean canReceiveInput() {
        return introTimer <= 0 && !outro;
    }

    private boolean showForwardButton() {
        if (currentPage < 0 || currentPage >= categoryItems.size()) {
            return false;
        }
        return categoryItems.get(currentPage).size() > itemsPerCategoryPage * (currentTab + 1);
    }

    private void rebuildNightStars(int w, int h) {
        nightStars.clear();
        starsForWidth = w;
        starsForHeight = h;
        Random rng = new Random(0x5A17D3L);

        int count = Math.max(40, (w * h) / 12000);
        int maxY = Math.max(1, h - px(220));
        for (int i = 0; i < count; i++) {
            NightStar star = new NightStar();
            star.x = rng.nextInt(Math.max(1, w));
            star.y = rng.nextInt(maxY);
            star.size = rng.nextDouble() < 0.2 ? 2 : 1;
            star.baseAlpha = 0.45f + rng.nextFloat() * 0.45f;
            star.twinkleAmp = 0.08f + rng.nextFloat() * 0.18f;
            star.twinkleSpeed = 0.7f + rng.nextFloat() * 1.6f;
            star.phase = rng.nextFloat() * (float) (Math.PI * 2.0);
            star.color = rng.nextDouble() < 0.22 ? 0xBFD6FF : 0xF5F8FF;
            nightStars.add(star);
        }
    }

    private void drawNightStars(GuiGraphics graphics, float alpha, int w, int h) {
        if (nightStars.isEmpty() || starsForWidth != w || starsForHeight != h) {
            rebuildNightStars(w, h);
        }
        float t = System.currentTimeMillis() / 1000.0f;
        for (NightStar star : nightStars) {
            float twinkle = (float) Math.sin(t * star.twinkleSpeed + star.phase) * star.twinkleAmp;
            float a = Math.max(0.0f, Math.min(1.0f, (star.baseAlpha + twinkle) * alpha));
            int ai = Math.max(0, Math.min(255, Math.round(a * 255.0f)));
            int c = (ai << 24) | (star.color & 0x00FFFFFF);
            graphics.fill(star.x, star.y, star.x + star.size, star.y + star.size, c);
        }
    }

    private void drawBackground(GuiGraphics graphics, float alpha) {
        int w = this.width;
        int h = this.height;
        int day = getCurrentDay();
        boolean isWinter = getCurrentSeason() == 3;
        boolean rainLike = isRainLikeWeather();
        boolean greenRain = isGreenRainWeather();
        int stardewViewWidth = Math.round(w * guiScale());

        if (rainLike) {
            int skyU = greenRain ? 640 : 639;
            if (isWinter) {
                graphics.setColor(119.0F / 255.0F, 136.0F / 255.0F, 153.0F / 255.0F, alpha);
            } else if (greenRain) {
                graphics.setColor(144.0F / 255.0F, 238.0F / 255.0F, 144.0F / 255.0F, alpha);
            } else {
                graphics.setColor(112.0F / 255.0F, 128.0F / 255.0F, 144.0F / 255.0F, alpha);
            }
            graphics.blit(StardewGuiUtil.CURSORS, 0, 0, w, h, skyU, 858, 1, 184, StardewGuiUtil.CURSORS_WIDTH, StardewGuiUtil.CURSORS_HEIGHT);
            graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

            if (greenRain) {
                graphics.setColor(105.0F / 255.0F, 105.0F / 255.0F, 105.0F / 255.0F, alpha * 0.8f);
                graphics.blit(StardewGuiUtil.CURSORS, 0, 0, w, h, skyU, 858, 1, 184, StardewGuiUtil.CURSORS_WIDTH, StardewGuiUtil.CURSORS_HEIGHT);
                graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            }

            for (int x = -244; x < w + 244; x += 244) {
                StardewGuiUtil.drawFromCursorsTint(graphics, x + px((int) ((weatherX / 2.0f) % 244f)), px(32), 643, 1142, 61, 53, s4(),
                        47.0F / 255.0F, 79.0F / 255.0F, 79.0F / 255.0F, alpha);
            }

            for (int i = 0; i < stardewViewWidth; i += 639) {
                if (isWinter) {
                    StardewGuiUtil.drawFromCursorsTint(graphics, px(i * 4), h - px(192), 0, 1034, 639, 48, s4(), 1.0F, 1.0F, 1.0F, alpha * 0.25f);
                    StardewGuiUtil.drawFromCursorsTint(graphics, px(i * 4), h - px(128), 0, 1034, 639, 32, s4(), 1.0F, 1.0F, 1.0F, alpha * 0.5f);
                } else {
                    float lowerBackAlpha = 0.5f - (float) Math.max(0, introTimer) / 3500.0f;
                    StardewGuiUtil.drawFromCursorsTint(graphics, px(i * 4), h - px(192), 0, 737, 639, 48, s4(), 30.0F / 255.0F, 62.0F / 255.0F, 50.0F / 255.0F, lowerBackAlpha);
                    StardewGuiUtil.drawFromCursorsTint(graphics, px(i * 4), h - px(128), 0, 737, 639, 32, s4(), 30.0F / 255.0F, 62.0F / 255.0F, 50.0F / 255.0F, alpha);
                }
            }

            StardewGuiUtil.drawFromCursors(graphics, px(160), h - px(128) + px(24), 653, 880, 10, 10, s4(), alpha);

            for (int x = -244; x < w + 244; x += 244) {
                StardewGuiUtil.drawFromCursorsTint(graphics, x + px((int) (weatherX % 244f)), px(-32), 643, 1142, 61, 53, s4(),
                        112.0F / 255.0F, 128.0F / 255.0F, 144.0F / 255.0F, alpha * 0.85f);
            }
            for (int x = -244; x < w + 244; x += 244) {
                StardewGuiUtil.drawFromCursorsTint(graphics, x + px((int) ((weatherX * 1.5f) % 244f)), px(-128), 643, 1142, 61, 53, s4(),
                        119.0F / 255.0F, 136.0F / 255.0F, 153.0F / 255.0F, alpha);
            }
            return;
        }

        // ShippingMenu base sky strip (no-rain)
        graphics.setColor(1.0F, 1.0F, 1.0F, alpha);
        graphics.blit(StardewGuiUtil.CURSORS, 0, 0, w, h, 639, 858, 1, 184, StardewGuiUtil.CURSORS_WIDTH, StardewGuiUtil.CURSORS_HEIGHT);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

        if (!rainLike) {
            drawNightStars(graphics, alpha, w, h);

            if (day == 28) {
                int shakeX = 0;
                int shakeY = 0;
                if (moonShake > 0) {
                    shakeX = ThreadLocalRandom.current().nextInt(-1, 2);
                    shakeY = ThreadLocalRandom.current().nextInt(-1, 2);
                }
                StardewGuiUtil.drawFromCursors(graphics, w - px(176) + px(shakeX), px(4 + shakeY), 642, 835, 43, 43, s4(), alpha);
                if (timesPokedMoon > 10) {
                    long ms = System.currentTimeMillis();
                    boolean blink = (ms % 4000L < 200L) || (ms % 8000L > 7600L && ms % 8000L < 7800L);
                    int moonFaceV = 844 + (blink ? 21 : 0);
                    StardewGuiUtil.drawFromCursors(graphics, w - px(136) + px(shakeX), px(48 + shakeY), 685, moonFaceV, 19, 21, s4(), alpha);
                }
            }
        }

        float distantAlpha = Math.max(0.0f, Math.min(1.0f, 0.65f - Math.max(0, introTimer) / 3500.0f));
        if (isWinter) {
            StardewGuiUtil.drawFromCursorsTint(graphics, px(0), h - px(192), 0, 1034, 639, 48, s4(), 1.0F, 1.0F, 1.0F, distantAlpha * 0.25f);
            StardewGuiUtil.drawFromCursorsTint(graphics, px(2556), h - px(192), 0, 1034, 639, 48, s4(), 1.0F, 1.0F, 1.0F, distantAlpha * 0.25f);
        } else {
            StardewGuiUtil.drawFromCursorsTint(graphics, px(0), h - px(192), 0, 737, 639, 48, s4(), 0.0F, 20.0F / 255.0F, 40.0F / 255.0F, distantAlpha);
            StardewGuiUtil.drawFromCursorsTint(graphics, px(2556), h - px(192), 0, 737, 639, 48, s4(), 0.0F, 20.0F / 255.0F, 40.0F / 255.0F, distantAlpha);
        }

        if (isWinter) {
            StardewGuiUtil.drawFromCursorsTint(graphics, px(0), h - px(128), 0, 1034, 639, 32, s4(), 1.0F, 1.0F, 1.0F, alpha * 0.5f);
            StardewGuiUtil.drawFromCursorsTint(graphics, px(2556), h - px(128), 0, 1034, 639, 32, s4(), 1.0F, 1.0F, 1.0F, alpha * 0.5f);
        } else {
            StardewGuiUtil.drawFromCursorsTint(graphics, px(0), h - px(128), 0, 737, 639, 32, s4(), 0.0F, 32.0F / 255.0F, 20.0F / 255.0F, alpha);
            StardewGuiUtil.drawFromCursorsTint(graphics, px(2556), h - px(128), 0, 737, 639, 32, s4(), 0.0F, 32.0F / 255.0F, 20.0F / 255.0F, alpha);
        }

        // Shipping bin icon in background
        StardewGuiUtil.drawFromCursors(graphics, px(160), h - px(128) + px(24), 653, 880, 10, 10, s4(), alpha);
    }

    @SuppressWarnings("null")
    private void drawSummaryPage(GuiGraphics graphics, float alpha, int mouseX, int mouseY) {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        int firstCategoryY = centerY + px(-300);
        int scrollDrawY = firstCategoryY + px(-128);

        if (scrollDrawY >= 0) {
            Component title = getYesterdayLabel();
            int textWidth = this.font.width(title);
            graphics.drawString(this.font, title, centerX - textWidth / 2, scrollDrawY, 0xFFFFFF, false);
        }

        int yOffset = px(-20);
        
        // Loop through 6 categories
        // Farming, Foraging, Fishing, Mining, Other, Total (Total is 5)
        for (int i = 0; i < 6; i++) {
            if (introTimer < 2500 - i * 500) {
                // Coordinates logic
                int plusButtonX = centerX + totalWidth / 2 - plusButtonWidth;
                int plusButtonY = centerY + px(-300 + i * 27 * 4);

                int startX = plusButtonX + px(12);
                int startY = plusButtonY + px(-8);

                // Plus button + slot preview are only visible for non-empty non-total categories.
                if (i < 5 && !categoryItems.get(i).isEmpty()) {
                    boolean hovering = mouseX >= plusButtonX && mouseX <= plusButtonX + plusButtonWidth && mouseY >= plusButtonY && mouseY <= plusButtonY + px(44);
                    StardewGuiUtil.drawFromCursors(graphics, plusButtonX, plusButtonY, hovering ? 402 : 392, 361, 10, 11, s4());

                    // Slot frame behind the preview item
                    StardewGuiUtil.drawFromCursors(graphics, startX + px(-104), startY + yOffset + px(4), 293, 360, 24, 24, s4());

                    ItemStack firstStack = categoryItems.get(i).get(0).stack();
                    graphics.renderItem(firstStack, startX + px(-88), startY + yOffset + px(16));
                }

                // Texture Box
                int boxX = startX - itemSlotWidth - categoryLabelsWidth + px(-12);
                int boxY = startY + yOffset;
                StardewGuiUtil.drawTextureBoxNoShadow(graphics, boxX, boxY, categoryLabelsWidth, px(104));

                // Name text
                Component catName = getCategoryName(i);
                graphics.drawString(this.font, catName, boxX + px(20), boxY + px(24), 0x663300, false);

                int dotsX = startX - itemSlotWidth + px(-192 - 24);
                for (int m = 0; m < 6; m++) {
                    StardewGuiUtil.drawFromCursors(graphics, dotsX + px(m * 6 * 4), startY + px(12), 355, 476, 7, 11, s4());
                }

                // Dial
                int dialX = startX - itemSlotWidth + px(-192 - 48 + 4);
                int dialY = startY + px(20);
                categoryDials[i].draw(graphics, dialX, dialY, categoryTotals[i]);

                // Gold coin icon
                int coinX = startX - itemSlotWidth + px(-64 - 4);
                int coinY = startY + px(12);
                StardewGuiUtil.drawFromCursors(graphics, coinX, coinY, 408, 476, 9, 11, s4());
            }
        }
        
        if (introTimer <= 0) {
            // Draw OK button
            int okWidth = px(64);
            int okX = centerX + totalWidth / 2 - itemAndPlusButtonWidth + px(32);
            int okY = centerY + px(300 - 64);
            boolean hoveringText = mouseX >= okX && mouseX <= okX + okWidth && mouseY >= okY && mouseY <= okY + okWidth;
            graphics.pose().pushPose();
            if (hoveringText) {
                graphics.pose().translate(okX + okWidth/2f, okY + okWidth/2f, 0);
                graphics.pose().scale(1.1f, 1.1f, 1f);
                graphics.pose().translate(-(okX + okWidth/2f), -(okY + okWidth/2f), 0);
            }
            StardewGuiUtil.drawFromCursors(graphics, okX, okY, 128, 256, 64, 64, 1.0f / guiScale());
            graphics.pose().popPose();
        }
    }

    private void drawItemDetail(GuiGraphics graphics, int mouseX, int mouseY) {
        int boxwidth = Math.min(this.width, px(1280));
        int boxheight = Math.min(this.height, px(920));
        int xPos = this.width / 2 - boxwidth / 2;
        int yPos = this.height / 2 - boxheight / 2;

        StardewGuiUtil.drawTextureBox(graphics, xPos, yPos, boxwidth, boxheight);

        int currentY = yPos + px(32);
        int startX = xPos + px(32);

        List<OvernightSettlementPayload.ShippedItem> items = categoryItems.get(currentPage);
        int startIndex = currentTab * itemsPerCategoryPage;
        int endIndex = Math.min(startIndex + itemsPerCategoryPage, items.size());

        for (int i = startIndex; i < endIndex; i++) {
            OvernightSettlementPayload.ShippedItem item = items.get(i);
            
            // Draw Item
            graphics.renderItem(item.stack(), startX, currentY);
            
            // Draw Subtotal (Name x Price)
            Component itemName = item.stack().getHoverName();
            String subtotalStr = itemName.getString() + " x" + String.format(Locale.ROOT, "%,d", item.pricePerItem());
            int stackTotal = item.pricePerItem() * item.stack().getCount();
            String totalStr = String.format(Locale.ROOT, "%,d", stackTotal);
            
            String dotsAndName = subtotalStr;
            int totalPosX = startX + boxwidth - px(64) - this.font.width(totalStr);
            
            while (this.font.width(dotsAndName + totalStr) < boxwidth - px(192)) {
                dotsAndName += " .";
            }
            if (this.font.width(dotsAndName + totalStr) >= boxwidth) {
                 dotsAndName = dotsAndName.substring(0, dotsAndName.length() - 1);
            }
            
            graphics.drawString(this.font, dotsAndName, startX + px(64 + 12), currentY + px(12), 0x553311, false);
            graphics.drawString(this.font, totalStr, totalPosX, currentY + px(12), 0x553311, false);
            
            currentY += px(68);
        }

        // Back button
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        int backX = centerX - boxwidth / 2 - px(64);
        int backY = centerY + boxheight / 2 - px(48);
        if (backX < 0) {
            backX = xPos + px(32);
        }
        if (backY > this.height - px(32)) {
            backY = this.height - px(80);
        }
        boolean backHover = mouseX >= backX && mouseX <= backX + px(48) && mouseY >= backY && mouseY <= backY + px(44);
        graphics.pose().pushPose();
        if (backHover) {
               graphics.pose().translate(backX + px(24), backY + px(22), 0);
                         graphics.pose().scale(1.125f, 1.125f, 1f);
               graphics.pose().translate(-(backX + px(24)), -(backY + px(22)), 0);
        }
           StardewGuiUtil.drawFromCursors(graphics, backX, backY, 352, 495, 12, 11, s4());
        graphics.pose().popPose();

        // Forward button
        if (showForwardButton()) {
            int fwX = centerX + boxwidth / 2 + px(8);
            int fwY = centerY + boxheight / 2 - px(48);
            if (fwX > this.width - px(32)) {
                fwX = xPos + boxwidth - px(32) - px(48);
            }
            if (fwY > this.height - px(32)) {
                fwY = this.height - px(80);
            }
            boolean fwHover = mouseX >= fwX && mouseX <= fwX + px(48) && mouseY >= fwY && mouseY <= fwY + px(44);
            graphics.pose().pushPose();
            if (fwHover) {
                  graphics.pose().translate(fwX + px(24), fwY + px(22), 0);
                 graphics.pose().scale(1.125f, 1.125f, 1f);
                  graphics.pose().translate(-(fwX + px(24)), -(fwY + px(22)), 0);
            }
              StardewGuiUtil.drawFromCursors(graphics, fwX, fwY, 365, 495, 12, 11, s4());
            graphics.pose().popPose();
        }
    }

    private Component getCategoryName(int id) {
        return switch (id) {
            case 0 -> Component.translatable("stardewcraft.shipping.farming");
            case 1 -> Component.translatable("stardewcraft.shipping.foraging");
            case 2 -> Component.translatable("stardewcraft.shipping.fishing");
            case 3 -> Component.translatable("stardewcraft.shipping.mining");
            case 4 -> Component.translatable("stardewcraft.shipping.other");
            case 5 -> Component.translatable("stardewcraft.shipping.total");
            default -> Component.literal("");
        };
    }

    @Override
    public void onClose() {
        beginOutroClose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!canReceiveInput()) {
            return true;
        }
        if (button == 0) {
            int centerX = this.width / 2;
            int centerY = this.height / 2;

            if (currentPage == -1) {
                // Click OK button
                int okWidth = px(64);
                int okX = centerX + totalWidth / 2 - itemAndPlusButtonWidth + px(32);
                int okY = centerY + px(300 - 64);

                if (mouseX >= okX && mouseX <= okX + okWidth && mouseY >= okY && mouseY <= okY + okWidth) {
                    beginOutroClose();
                    return true;
                }
                
                // Check plus buttons
                for (int i = 0; i < 5; i++) {
                    if (categoryItems.get(i).isEmpty()) continue;
                    int plusButtonX = centerX + totalWidth / 2 - plusButtonWidth;
                    int plusButtonY = centerY + px(-300 + i * 27 * 4);
                    if (mouseX >= plusButtonX && mouseX <= plusButtonX + plusButtonWidth && mouseY >= plusButtonY && mouseY <= plusButtonY + px(44)) {
                        playUiSound(ModSounds.SHWIP.get(), 1.0f, 1.0f);
                        currentPage = i;
                        currentTab = 0;
                        return true;
                    }
                }

                if (getCurrentDay() == 28 && timesPokedMoon <= 10) {
                    int moonX = this.width - px(176);
                    int moonY = px(4);
                    int moonW = px(172);
                    int moonH = px(172);
                    if (mouseX >= moonX && mouseX <= moonX + moonW && mouseY >= moonY && mouseY <= moonY + moonH) {
                        moonShake = 100;
                        timesPokedMoon++;
                        if (timesPokedMoon > 10) {
                            playUiSound(ModSounds.SHADOW_DIE.get(), 1.0f, 1.0f);
                        } else {
                            playUiSound(ModSounds.THUD_STEP.get(), 1.0f, 1.0f);
                        }
                        return true;
                    }
                }
            } else {
                int boxwidth = Math.min(this.width, px(1280));
                int boxheight = Math.min(this.height, px(920));
                int xPos = this.width / 2 - boxwidth / 2;

                // Back button
                int backX = centerX - boxwidth / 2 - px(64);
                int backY = centerY + boxheight / 2 - px(48);
                if (backX < 0) {
                    backX = xPos + px(32);
                }
                if (backY > this.height - px(32)) {
                    backY = this.height - px(80);
                }
                if (mouseX >= backX && mouseX <= backX + px(48) && mouseY >= backY && mouseY <= backY + px(44)) {
                    playUiSound(ModSounds.SHWIP.get(), 1.0f, 1.0f);
                    if (currentTab == 0) {
                        currentPage = -1;
                    } else {
                        currentTab--;
                    }
                    return true;
                }

                // Forward button
                if (showForwardButton()) {
                    int fwX = centerX + boxwidth / 2 + px(8);
                    int fwY = centerY + boxheight / 2 - px(48);
                    if (fwX > this.width - px(32)) {
                        fwX = xPos + boxwidth - px(32) - px(48);
                    }
                    if (fwY > this.height - px(32)) {
                        fwY = this.height - px(80);
                    }
                    if (mouseX >= fwX && mouseX <= fwX + px(48) && mouseY >= fwY && mouseY <= fwY + px(44)) {
                        playUiSound(ModSounds.SHWIP.get(), 1.0f, 1.0f);
                        currentTab++;
                        return true;
                    }
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean isPauseScreen() {
        return true;
    }

    private StardewTimeManager getClientTime() {
        return StardewTimeHud.getClientTimeCache();
    }

    private int getCurrentDay() {
        return Math.max(1, getClientTime().getCurrentDay());
    }

    private int getCurrentSeason() {
        return Math.max(0, getClientTime().getCurrentSeason());
    }

    private boolean isRainLikeWeather() {
        if (this.minecraft == null || this.minecraft.level == null) {
            return false;
        }
        String weather = ClientWeatherCache.getCurrentWeather(this.minecraft.level.dimension());
        return "Rain".equals(weather) || "Storm".equals(weather) || "Snow".equals(weather);
    }

    private boolean isGreenRainWeather() {
        if (this.minecraft == null || this.minecraft.level == null) {
            return false;
        }
        String weather = ClientWeatherCache.getCurrentWeather(this.minecraft.level.dimension());
        return "GreenRain".equals(weather);
    }

    private Component getYesterdayLabel() {
        StardewTimeManager t = getClientTime();
        int day = Math.max(1, t.getCurrentDay()) - 1;
        int season = Math.max(0, t.getCurrentSeason());
        int year = Math.max(1, t.getCurrentYear());
        if (day <= 0) {
            day = 28;
            season -= 1;
            if (season < 0) {
                season = 3;
                year -= 1;
                if (year <= 0) {
                    year = 1;
                }
            }
        }
        Component seasonName = switch (season) {
            case 0 -> Component.translatable("stardewcraft.shipping.season.spring");
            case 1 -> Component.translatable("stardewcraft.shipping.season.summer");
            case 2 -> Component.translatable("stardewcraft.shipping.season.fall");
            case 3 -> Component.translatable("stardewcraft.shipping.season.winter");
            default -> Component.translatable("stardewcraft.shipping.season.spring");
        };
        return Component.translatable("stardewcraft.shipping.yesterday_label", seasonName, day, year);
    }
}
