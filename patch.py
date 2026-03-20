import os

path = "src/main/java/com/stardew/craft/client/gui/menu/StardewGameMenuScreen.java"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

# Replace drawInventorySlot to include the box
old_slot = """    private void drawInventorySlot(GuiGraphics graphics, int x, int y, ItemStack stack, boolean hovered) {
        if (hovered) {
            graphics.fill(x, y, x + ui(INVENTORY_SLOT_SDV), y + ui(INVENTORY_SLOT_SDV), 0x35FFFFFF);
        }
        if (!stack.isEmpty()) {
            drawScaledItem(graphics, stack, x, y, mapping.s4() * ITEM_VISUAL_SCALE, false);
            drawItemCount(graphics, stack, x + ui(62), y + ui(62), 0xFFFFFFFF);
        }
    }"""
new_slot = """    private void drawInventorySlot(GuiGraphics graphics, int x, int y, ItemStack stack, boolean hovered) {
        StardewGuiUtil.drawMenuTileIndex(graphics, x, y, ui(INVENTORY_SLOT_SDV), ui(INVENTORY_SLOT_SDV), 10);
        if (hovered) {
            graphics.fill(x, y, x + ui(INVENTORY_SLOT_SDV), y + ui(INVENTORY_SLOT_SDV), 0x35FFFFFF);
        }
        if (!stack.isEmpty()) {
            drawScaledItem(graphics, stack, x, y, mapping.s4() * ITEM_VISUAL_SCALE, false);
            drawItemCount(graphics, stack, x + ui(62), y + ui(62), 0xFFFFFFFF);
        }
    }"""
text = text.replace(old_slot, new_slot)

# Fix buildCraftingPages (Strict Queue)
old_build = """    private List<List<RecipeCell>> buildCraftingPages() {
        List<List<RecipeCell>> pages = new ArrayList<>();
        if (craftingRecipeIds.isEmpty()) {
            pages.add(List.of());
            return pages;
        }

        List<RecipeCell> currentPage = new ArrayList<>();
        boolean[][] occupied = new boolean[10][4];

        for (int recipeIndex = 0; recipeIndex < craftingRecipeIds.size(); recipeIndex++) {
            ItemStack stack = craftingRecipeStacks.get(recipeIndex);
            boolean big = isBigCraftable(stack);

            boolean placed = false;
            while (!placed) {
                for (int y = 0; y < 4 && !placed; y++) {
                    for (int x = 0; x < 10; x++) {
                        if (occupied[x][y]) {
                            continue;
                        }
                        if (big) {
                            if (y + 1 >= 4 || occupied[x][y + 1]) {
                                continue;
                            }
                            occupied[x][y] = true;
                            occupied[x][y + 1] = true;
                            currentPage.add(new RecipeCell(recipeIndex, x, y, true));
                            placed = true;
                        } else {
                            occupied[x][y] = true;
                            currentPage.add(new RecipeCell(recipeIndex, x, y, false));
                            placed = true;
                        }
                        if (placed) {
                            break;
                        }
                    }
                }

                if (!placed) {
                    pages.add(currentPage);
                    currentPage = new ArrayList<>();
                    occupied = new boolean[10][4];
                }
            }
        }

        pages.add(currentPage);
        return pages;
    }"""
new_build = """    private List<List<RecipeCell>> buildCraftingPages() {
        List<List<RecipeCell>> pages = new ArrayList<>();
        if (craftingRecipeIds.isEmpty()) {
            pages.add(List.of());
            return pages;
        }

        List<RecipeCell> currentPage = new ArrayList<>();
        int capacity = 40;
        int currentCount = 0;

        for (int recipeIndex = 0; recipeIndex < craftingRecipeIds.size(); recipeIndex++) {
            if (currentCount >= capacity) {
                pages.add(currentPage);
                currentPage = new ArrayList<>();
                currentCount = 0;
            }
            int x = currentCount % 10;
            int y = currentCount / 10;
            currentPage.add(new RecipeCell(recipeIndex, x, y, false));
            currentCount++;
        }
        if (!currentPage.isEmpty()) {
            pages.add(currentPage);
        }

        return pages;
    }"""
text = text.replace(old_build, new_build)

# Fix Carried Drag Offset
old_drag = """            ItemStack carried = currentCarriedItem();
            if (!carried.isEmpty()) {
                int drawX = mouseX + ui(CARRIED_ITEM_OFFSET_SDV);
                int drawY = mouseY + ui(CARRIED_ITEM_OFFSET_SDV);
                drawScaledItem(graphics, carried, drawX, drawY, mapping.s4() * ITEM_VISUAL_SCALE, true);
            }"""
new_drag = """            ItemStack carried = currentCarriedItem();
            if (!carried.isEmpty()) {
                float drScale = mapping.s4() * ITEM_VISUAL_SCALE;
                int hw = Math.round(16 * drScale) / 2;
                int drawX = mouseX - hw;
                int drawY = mouseY - hw;
                drawScaledItem(graphics, carried, drawX, drawY, drScale, true);
            }"""
text = text.replace(old_drag, new_drag)

# Adjust coordinates
old_coord1 = "private static final int INVENTORY_TOP_SDV = 400;"
new_coord1 = "private static final int INVENTORY_TOP_SDV = 368;"
old_coord2 = "private static final int INVENTORY_GAP_SDV = 0;"
new_coord2 = "private static final int INVENTORY_GAP_SDV = 4;"
text = text.replace(old_coord1, new_coord1).replace(old_coord2, new_coord2)

old_part = "        StardewGuiUtil.drawHorizontalPartition(graphics, menuX + ui(48), partitionY, menuWidth - ui(96), ui(16));"
new_part = "        StardewGuiUtil.drawHorizontalPartition(graphics, menuX, menuY + ui(312), menuWidth, ui(64));"
text = text.replace(old_part, new_part)

old_partY = "        int partitionY = menuY + ui(352);\n\n"
text = text.replace(old_partY, "")

# Up/Down bounds
old_x = """    private int pageButtonX() {
        return menuX + ui(800);
    }"""
new_x = """    private int pageButtonX() {
        return menuX + menuWidth;
    }"""
text = text.replace(old_x, new_x)

old_uy = """    private int pageUpButtonY() {
        return craftingGridY();
    }"""
new_uy = """    private int pageUpButtonY() {
        return menuY + ui(16);
    }"""
text = text.replace(old_uy, new_uy)

old_dy = """    private int pageDownButtonY() {
        return craftingGridY() + ui(224);
    }"""
new_dy = """    private int pageDownButtonY() {
        return menuY + menuHeight - ui(64);
    }"""
text = text.replace(old_dy, new_dy)

# Arrow Drawing
old_arrow = """    private void drawCenteredArrow(GuiGraphics graphics, int boundX, int boundY, ResourceLocation texture, float scaleMul) {
        float scale = mapping.s4() * 0.5f * scaleMul;
        int drawSize = Math.round(STD_TILE_SIZE * scale);
        int boundSize = ui(64);
        int drawX = boundX + (boundSize - drawSize) / 2;
        int drawY = boundY + (boundSize - drawSize) / 2;
        drawScaledTexture(graphics, texture, drawX, drawY, STD_TILE_SIZE, STD_TILE_SIZE, scale);
    }"""
new_arrow = """    private void drawCenteredArrow(GuiGraphics graphics, int boundX, int boundY, ResourceLocation texture, float scaleMul) {
        float drawScale = mapping.s4() * scaleMul;
        int baseW = 16;
        int baseH = 16;
        int visualW = Math.round(baseW * drawScale);
        int visualH = Math.round(baseH * drawScale);
        int boxW = ui(64);
        int boxH = ui(64);
        int ox = boundX + (boxW - visualW) / 2;
        int oy = boundY + (boxH - visualH) / 2;
        
        graphics.pose().pushPose();
        graphics.pose().translate(ox, oy, 0);
        graphics.pose().scale(drawScale, drawScale, 1.0f);
        graphics.blit(texture, 0, 0, 0, 0, baseW, baseH, baseW, baseH);
        graphics.pose().popPose();
    }"""
text = text.replace(old_arrow, new_arrow)

# Tooltips for icons inline! 
old_tt = """                Component line = Component.literal((have >= need ? "✔ " : "✖ "))
                        .append(requirement.icon().getHoverName().copy().withStyle(ChatFormatting.GRAY))
                        .append(Component.literal(" x" + need).withStyle(countColor));"""
new_tt = """                Component line = Component.literal((have >= need ? "✔   " : "✖   "))
                        .append(requirement.icon().getHoverName().copy().withStyle(ChatFormatting.GRAY))
                        .append(Component.literal(" x" + need).withStyle(countColor));"""
text = text.replace(old_tt, new_tt)

with open(path, "w", encoding="utf-8") as f:
    f.write(text)
