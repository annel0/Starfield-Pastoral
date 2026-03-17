with open('src/main/java/com/stardew/craft/client/gui/overnight/ShippingMenuScreen.java', 'r', encoding='utf-8') as f:
    text = f.read()

# We want to keep everything up to getCategoryName.
# Then we provide our specific getCategoryName, onClose, mouseClicked, and isPauseScreen.

start = text.find('    private Component getCategoryName(int id)')

bottom_code = '''    private Component getCategoryName(int id) {
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
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int centerX = this.width / 2;
            int centerY = this.height / 2;

            if (currentPage == -1) {
                int okWidth = 16;
                int okX = centerX + totalWidth / 2 - itemAndPlusButtonWidth + 8;
                int okY = centerY + 75 - 16;
                if (mouseX >= okX && mouseX <= okX + okWidth && mouseY >= okY && mouseY <= okY + okWidth) {
                    this.onClose();
                    return true;
                }

                for (int i = 0; i < 5; i++) {
                    if (!categoryItems.get(i).isEmpty()) {
                        int plusButtonX = centerX + totalWidth / 2 - plusButtonWidth;
                        int plusButtonY = centerY - 75 + i * 27;

                        if (mouseX >= plusButtonX && mouseX <= plusButtonX + plusButtonWidth && mouseY >= plusButtonY && mouseY <= plusButtonY + 11) {
                            this.currentPage = i;
                            this.currentTab = 0;
                            return true;
                        }
                    }
                }
            } else {
                int backWidth = 12;
                int backX = this.width / 2 - 100;
                int backY = this.height / 2 + 100;
                
                if (mouseX >= backX && mouseX <= backX + backWidth && mouseY >= backY && mouseY <= backY + 11) {
                    this.currentPage = -1;
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
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
    public boolean isPauseScreen() {
        return true;
    }
}
'''

new_text = text[:start] + bottom_code

with open('src/main/java/com/stardew/craft/client/gui/overnight/ShippingMenuScreen.java', 'w', encoding='utf-8') as f:
    f.write(new_text)

