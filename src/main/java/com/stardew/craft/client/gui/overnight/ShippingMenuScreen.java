package com.stardew.craft.client.gui.overnight;

import com.stardew.craft.network.overnight.OvernightSettlementPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
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

    // UI Layout vars
    private int categoryLabelsWidth = 512;
    private int plusButtonWidth = 40;
    private int itemSlotWidth = 96;
    private int itemAndPlusButtonWidth = plusButtonWidth + itemSlotWidth + 8;
    private int totalWidth = categoryLabelsWidth + itemAndPlusButtonWidth;

    private final List<Screen> siblingScreens;

    public ShippingMenuScreen(List<OvernightSettlementPayload.ShippedItem> shippedItems, List<Screen> siblingScreens) {
        super(Component.literal("Shipping Summary"));
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
            categoryTotals[5] += itemTotal; // Total
        }
    }

    @Override
    protected void init() {
        super.init();
        this.lastTime = System.currentTimeMillis();
    }

    @SuppressWarnings("null")
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        long currentTime = System.currentTimeMillis();
        int delta = (int)(currentTime - lastTime);
        this.lastTime = currentTime;

        // Speed up if clicking (left mouse button isn't tracked easily here but we can assume normal speed for now)
        introTimer -= delta;

        float alphaOverlay = 1.0f - (float) Math.max(0, introTimer) / 3500.0f;

        drawBackground(graphics, alphaOverlay);

        if (currentPage == -1) {
            drawSummaryPage(graphics, alphaOverlay, mouseX, mouseY);
        } else {
            drawItemDetail(graphics, mouseX, mouseY);
        }
    }

    private void drawBackground(GuiGraphics graphics, float alpha) {
        int w = this.width;
        int h = this.height;

        // Dark sky color
        graphics.fill(0, 0, w, h, (int)(alpha * 255) << 24 | 0x001428); // RGB 0, 20, 40

        // Stars from cursors (simplification)
        for (int l = 0; l < w; l += 639) {
            StardewGuiUtil.drawFromCursors(graphics, l, 0, 0, 1453, Math.min(639, w - l), 195, 1.0f, alpha); // The stars/sky top
        }

        // The hills at the bottom
        // b.Draw(Game1.mouseCursors, new Vector2(0f, Game1.uiViewport.Height - 192), new Rectangle(0, isWinter ? 1034 : 737, 639, 48), ..., scale: 4f
        // Let's adapt the scale to fit screen
        for (int l = 0; l < w; l += 639 * 4) {
             StardewGuiUtil.drawFromCursors(graphics, l, h - 192, 0, 737, 639, 48, 4.0f, alpha * 0.65f); // Distant hills
             StardewGuiUtil.drawFromCursors(graphics, l, h - 128, 0, 737, 639, 32, 4.0f, alpha); // Closer hills
        }
    }

    @SuppressWarnings("null")
    private void drawSummaryPage(GuiGraphics graphics, float alpha, int mouseX, int mouseY) {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        int scrollDrawY = (centerY - 300) - 128; // approximated dayPlaqueY from ShippingMenu.cs

        // Date scroll - can use a simple text or the scroll graphic
        Component title = Component.literal("Level up & Shipping");
        int textWidth = this.font.width(title);
        graphics.drawString(this.font, title, centerX - textWidth/2, Math.max(10, scrollDrawY + 50), 0xFFFFFF);

        int yOffset = -20;
        
        // Loop through 6 categories
        // Farming, Foraging, Fishing, Mining, Other, Total (Total is 5)
        for (int i = 0; i < 6; i++) {
            if (introTimer < 2500 - i * 500) {
                boolean hasItems = i == 5 || !categoryItems.get(i).isEmpty();
                if (hasItems) {
                    // Coordinates logic
                    int plusButtonX = centerX + totalWidth / 2 - plusButtonWidth;
                    int plusButtonY = centerY - 250 + i * 27 * 4; // spaced out
                    
                    int startX = plusButtonX + 12;
                    int startY = plusButtonY - 8;

                    // Plus button (if applicable)
                    if (i < 5) {
                        boolean hovering = mouseX >= plusButtonX && mouseX <= plusButtonX + plusButtonWidth && mouseY >= plusButtonY && mouseY <= plusButtonY + 44;
                        StardewGuiUtil.drawFromCursors(graphics, plusButtonX, plusButtonY, hovering ? 402 : 392, 361, 10, 11, 4f);
                        // Draw first item mini icon
                        if (!categoryItems.get(i).isEmpty()) {
                            ItemStack firstStack = categoryItems.get(i).get(0).stack();
                            graphics.renderItem(firstStack, startX - 88 + 4, startY + yOffset + 16 + 4);
                        }
                    }

                    // Texture Box
                    int boxX = startX - itemSlotWidth - categoryLabelsWidth - 12;
                    int boxY = startY + yOffset;
                    StardewGuiUtil.drawTextureBox(graphics, boxX, boxY, categoryLabelsWidth, 104);
                    
                    // Name text
                    Component catName = getCategoryName(i);
                    graphics.drawString(this.font, catName, boxX + 24, boxY + 28, 0x663300, false);

                    // Dial setup... Wait, Stardew uses `categoryDials[i2].draw(...)`
                    int dotsX = startX - itemSlotWidth - 192 - 24;
                    for (int m = 0; m < 6; m++) {
                        StardewGuiUtil.drawFromCursors(graphics, dotsX + m * 6 * 4, startY + 12, 355, 476, 7, 11, 4f); // dots
                    }
                    
                    // The dial itself
                    int dialX = startX - itemSlotWidth - 192 - 48 + 4;
                    int dialY = startY + 20;
                    categoryDials[i].draw(graphics, dialX, dialY, categoryTotals[i]);
                    
                    // The Gold coin
                    int coinX = startX - itemSlotWidth - 64 - 4;
                    int coinY = startY + 12;
                    StardewGuiUtil.drawFromCursors(graphics, coinX, coinY, 408, 476, 9, 11, 4f);
                }
            }
        }
        
        if (introTimer <= 0) {
            // Draw OK button
            int okWidth = 64;
            int okX = centerX + totalWidth / 2 - itemAndPlusButtonWidth + 32;
            int okY = centerY + 300 - 64;
            boolean hoveringText = mouseX >= okX && mouseX <= okX + okWidth && mouseY >= okY && mouseY <= okY + okWidth;
            graphics.pose().pushPose();
            if (hoveringText) {
                graphics.pose().translate(okX + okWidth/2f, okY + okWidth/2f, 0);
                graphics.pose().scale(1.1f, 1.1f, 1f);
                graphics.pose().translate(-(okX + okWidth/2f), -(okY + okWidth/2f), 0);
            }
            StardewGuiUtil.drawFromCursors(graphics, okX, okY, 128, 256, okWidth, okWidth, 1.0f);
            graphics.pose().popPose();
        }
    }

    private void drawItemDetail(GuiGraphics graphics, int mouseX, int mouseY) {
        int boxwidth = Math.min(this.width, 1280);
        int boxheight = Math.min(this.height, 920);
        int xPos = this.width / 2 - boxwidth / 2;
        int yPos = this.height / 2 - boxheight / 2;

        StardewGuiUtil.drawTextureBox(graphics, xPos, yPos, boxwidth, boxheight);

        int currentY = yPos + 32;
        int startX = xPos + 32;

        List<OvernightSettlementPayload.ShippedItem> items = categoryItems.get(currentPage);
        int startIndex = currentTab * itemsPerCategoryPage;
        int endIndex = Math.min(startIndex + itemsPerCategoryPage, items.size());

        for (int i = startIndex; i < endIndex; i++) {
            OvernightSettlementPayload.ShippedItem item = items.get(i);
            
            // Draw Item
            graphics.renderItem(item.stack(), startX, currentY);
            
            // Draw Subtotal (Name x Price)
            Component itemName = item.stack().getHoverName();
            String subtotalStr = itemName.getString() + " x" + item.pricePerItem();
            int stackTotal = item.pricePerItem() * item.stack().getCount();
            String totalStr = String.valueOf(stackTotal);
            
            String dotsAndName = subtotalStr;
            int totalPosX = startX + boxwidth - 64 - this.font.width(totalStr) - 32;
            
            while (this.font.width(dotsAndName + totalStr) < boxwidth - 192) {
                dotsAndName += " .";
            }
            if (this.font.width(dotsAndName + totalStr) >= boxwidth) {
                 dotsAndName = dotsAndName.substring(0, dotsAndName.length() - 1);
            }
            
            graphics.drawString(this.font, dotsAndName, startX + 64 + 12, currentY + 4, 0x553311, false);
            graphics.drawString(this.font, totalStr, totalPosX, currentY + 4, 0x553311, false);
            
            currentY += 68;
        }

        // Back button
        int backX = xPos + 16;
        int backY = yPos + boxheight - 64;
        boolean backHover = mouseX >= backX && mouseX <= backX + 48 && mouseY >= backY && mouseY <= backY + 44;
        graphics.pose().pushPose();
        if (backHover) {
             graphics.pose().translate(backX + 24, backY + 22, 0);
             graphics.pose().scale(1.1f, 1.1f, 1f);
             graphics.pose().translate(-(backX + 24), -(backY + 22), 0);
        }
        StardewGuiUtil.drawFromCursors(graphics, backX, backY, 352, 495, 12, 11, 4f);
        graphics.pose().popPose();

        // Forward button
        if (endIndex < items.size()) {
            int fwX = xPos + boxwidth - 32 - 48;
            int fwY = yPos + boxheight - 64;
            boolean fwHover = mouseX >= fwX && mouseX <= fwX + 48 && mouseY >= fwY && mouseY <= fwY + 44;
            graphics.pose().pushPose();
            if (fwHover) {
                 graphics.pose().translate(fwX + 24, fwY + 22, 0);
                 graphics.pose().scale(1.1f, 1.1f, 1f);
                 graphics.pose().translate(-(fwX + 24), -(fwY + 22), 0);
            }
            StardewGuiUtil.drawFromCursors(graphics, fwX, fwY, 365, 495, 12, 11, 4f);
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
        if (this.siblingScreens != null && !this.siblingScreens.isEmpty()) {
            this.minecraft.setScreen(this.siblingScreens.remove(0));
        } else {
            super.onClose();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int centerX = this.width / 2;
            int centerY = this.height / 2;

            if (currentPage == -1) {
                if (introTimer > 0) {
                    introTimer -= 2000; // Fast forward
                    return true;
                } else {
                    // Click OK button
                    int okWidth = 64;
                    int okX = centerX + totalWidth / 2 - itemAndPlusButtonWidth + 32;
                    int okY = centerY + 300 - 64;
                    
                    if (mouseX >= okX && mouseX <= okX + okWidth && mouseY >= okY && mouseY <= okY + okWidth) {
                        this.onClose();
                        return true;
                    }
                }
                
                // Check plus buttons
                for (int i = 0; i < 5; i++) {
                    if (categoryItems.get(i).isEmpty()) continue;
                    int plusButtonX = centerX + totalWidth / 2 - plusButtonWidth;
                    int plusButtonY = centerY - 250 + i * 27 * 4;
                    if (mouseX >= plusButtonX && mouseX <= plusButtonX + plusButtonWidth && mouseY >= plusButtonY && mouseY <= plusButtonY + 44) {
                        currentPage = i;
                        currentTab = 0;
                        return true;
                    }
                }
            } else {
                int boxwidth = Math.min(this.width, 1280);
                int boxheight = Math.min(this.height, 920);
                int xPos = this.width / 2 - boxwidth / 2;
                int yPos = this.height / 2 - boxheight / 2;

                // Back button
                int backX = xPos + 16;
                int backY = yPos + boxheight - 64;
                if (mouseX >= backX && mouseX <= backX + 48 && mouseY >= backY && mouseY <= backY + 44) {
                    if (currentTab == 0) {
                        currentPage = -1;
                    } else {
                        currentTab--;
                    }
                    return true;
                }

                // Forward button
                List<OvernightSettlementPayload.ShippedItem> items = categoryItems.get(currentPage);
                int startIndex = currentTab * itemsPerCategoryPage;
                int endIndex = Math.min(startIndex + itemsPerCategoryPage, items.size());
                if (endIndex < items.size()) {
                    int fwX = xPos + boxwidth - 32 - 48;
                    int fwY = yPos + boxheight - 64;
                    if (mouseX >= fwX && mouseX <= fwX + 48 && mouseY >= fwY && mouseY <= fwY + 44) {
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
}
