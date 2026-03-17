import re

with open('src/main/java/com/stardew/craft/client/gui/overnight/ShippingMenuScreen.java', 'r', encoding='utf-8') as f:
    text = f.read()

# Replace hardcoded sizes at the class level
text = text.replace('private int categoryLabelsWidth = 512;', 'private int categoryLabelsWidth = 128;')
text = text.replace('private int plusButtonWidth = 40;', 'private int plusButtonWidth = 10;')
text = text.replace('private int itemSlotWidth = 96;', 'private int itemSlotWidth = 24;')
text = text.replace('private int itemAndPlusButtonWidth = plusButtonWidth + itemSlotWidth + 8;', 'private int itemAndPlusButtonWidth = plusButtonWidth + itemSlotWidth + 2;')

new_render = '''
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        long currentTime = net.minecraft.Util.getMillis();
        if (lastTime == 0) lastTime = currentTime;
        int delta = (int)(currentTime - lastTime);
        lastTime = currentTime;

        if (introTimer > 0) {
            introTimer -= delta;
            // return; // We let it draw instantly for testing or do intro animation. For now, zero it out.
            introTimer = 0;
        }

        graphics.fill(0, 0, this.width, this.height, 0x88000000);

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        int scrollDrawY = (centerY - 75) - 32;

        Component title = Component.literal("Level up & Shipping");
        int textWidth = this.font.width(title);
        graphics.drawString(this.font, title, centerX - textWidth/2, Math.max(5, scrollDrawY + 12), 0xFFFFFF);

        int yOffset = -5;

        for (int i = 0; i < 6; i++) {
            boolean hasItems = i == 5 || !categoryItems.get(i).isEmpty();
            if (hasItems) {
                int plusButtonX = centerX + totalWidth / 2 - plusButtonWidth;
                int plusButtonY = centerY - 75 + i * 27;

                int startX = plusButtonX + 3;
                int startY = plusButtonY - 2;

                if (i < 5) {
                    boolean hovering = mouseX >= plusButtonX && mouseX <= plusButtonX + plusButtonWidth && mouseY >= plusButtonY && mouseY <= plusButtonY + 11;
                    StardewGuiUtil.drawFromCursors(graphics, plusButtonX, plusButtonY, hovering ? 402 : 392, 361, 10, 11, 1.0f);

                    if (!categoryItems.get(i).isEmpty()) {
                        ItemStack firstStack = categoryItems.get(i).get(0).stack();
                        graphics.renderItem(firstStack, startX - 22 + 1, startY + yOffset + 4 + 1);
                    }
                }

                int boxX = startX - itemSlotWidth - categoryLabelsWidth - 3;
                int boxY = startY + yOffset;
                StardewGuiUtil.drawTextureBox(graphics, boxX, boxY, categoryLabelsWidth, 26);

                Component catName = getCategoryName(i);
                graphics.drawString(this.font, catName, boxX + 6, boxY + 7, 0x663300, false);

                int dotsX = startX - itemSlotWidth - 48 - 6;
                if (i < 5) {
                    for (int m = 0; m < 6; m++) {
                        StardewGuiUtil.drawFromCursors(graphics, dotsX + m * 6, startY + 3, 355, 476, 7, 11, 1.0f);
                    }
                }

                categoryDials[i].draw(graphics, startX - itemSlotWidth - 48 - 12 + 1, startY + 5, categoryTotals[i]);
                
                StardewGuiUtil.drawFromCursors(graphics, startX - itemSlotWidth - 16 - 1, startY + 3, 408, 476, 9, 11, 1.0f);
            }
        }

        int okWidth = 16;
        int okX = centerX + totalWidth / 2 - itemAndPlusButtonWidth + 8;
        int okY = centerY + 75 - 16;
        
        boolean okHover = mouseX >= okX && mouseX <= okX + okWidth && mouseY >= okY && mouseY <= okY + okWidth;
        graphics.pose().pushPose();
        if (okHover) {
             graphics.pose().translate(okX + 8, okY + 8, 0);
             graphics.pose().scale(1.1f, 1.1f, 1f);
             graphics.pose().translate(-(okX + 8), -(okY + 8), 0);
        }
        StardewGuiUtil.drawFromCursors(graphics, okX, okY, 128, 256, 64, 64, 0.25f);
        graphics.pose().popPose();
    }
'''

start = text.find('    @Override\n    public void render(GuiGraphics')
# Instead of finding mouseClicked, we find getCategoryName to ensure we don't delete everything
end = text.find('    private Component getCategoryName')
text = text[:start] + new_render + '\n' + text[end:]

with open('src/main/java/com/stardew/craft/client/gui/overnight/ShippingMenuScreen.java', 'w', encoding='utf-8') as f:
    f.write(text)
