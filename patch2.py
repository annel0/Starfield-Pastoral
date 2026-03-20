import os

path = "src/main/java/com/stardew/craft/client/gui/menu/StardewGameMenuScreen.java"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

# 1. Grid Capacity Fix
old_cap = "int capacity = 40;"
new_cap = "int capacity = 30;"
text = text.replace(old_cap, new_cap)

# 2. Add Silhouette rendering method and fix visual item rendering for Locked/Uncraftable
# Find drawScaledItemTinted and replace the logic that uses setColor (since it's ignored) 
# and add drawSilhouetteItem.
old_drawScaledItemTinted = """    private void drawScaledItemTinted(GuiGraphics graphics, ItemStack stack, int x, int y, float scale, boolean drawDecorations, float r, float g, float b, float a) {
        if (stack.isEmpty()) {
            return;
        }
        graphics.setColor(r, g, b, a);
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        graphics.pose().scale(scale, scale, 1.0f);
        graphics.renderItem(stack, 0, 0);
        graphics.pose().popPose();
        graphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);

        if (drawDecorations) {
            drawItemCount(graphics, stack, x + ui(62), y + ui(62), 0xFFFFFFFF);
        }
    }"""
new_drawScaledItemTinted = """    private void drawScaledItemTinted(GuiGraphics graphics, ItemStack stack, int x, int y, float scale, boolean drawDecorations, float r, float g, float b, float a) {
        if (stack.isEmpty()) {
            return;
        }
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        graphics.pose().scale(scale, scale, 1.0f);
        graphics.renderItem(stack, 0, 0);
        graphics.pose().popPose();
        
        // Emulate dimming by rendering a semi-transparent black square over the exact slot area
        if (a < 1.0f) {
            int size = Math.round(16 * scale);
            graphics.pose().pushPose();
            graphics.pose().translate(0, 0, 200); // above item
            int alpha = Math.round((1.0f - a) * 255.0f);
            int color = (alpha << 24) | 0x000000;
            graphics.fill(x, y, x + size, y + size, color);
            graphics.pose().popPose();
        }

        if (drawDecorations) {
            drawItemCount(graphics, stack, x + ui(62), y + ui(62), 0xFFFFFFFF);
        }
    }

    private void drawSilhouetteItem(GuiGraphics graphics, ItemStack stack, int x, int y, float scale) {
        if (stack.isEmpty() || this.minecraft == null) return;
        graphics.pose().pushPose();
        graphics.pose().translate(x + 8 * scale, y + 8 * scale, 150.0F); // Center of 16x16, bumped Z
        graphics.pose().scale(16 * scale, -16 * scale, 16 * scale); // GUI standard flip
        this.minecraft.getItemRenderer().renderStatic(
                stack,
                net.minecraft.world.item.ItemDisplayContext.GUI,
                0, // 0 packedLight makes it purely black
                net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY,
                graphics.pose(),
                graphics.bufferSource(),
                this.minecraft.level,
                0
        );
        graphics.bufferSource().endBatch(); // Flush to apply
        graphics.pose().popPose();
    }"""
text = text.replace(old_drawScaledItemTinted, new_drawScaledItemTinted)


# 3. Update Recipe Grid Rendering Logic
old_grid_tint = """            if (!unlocked) {
                drawScaledItemTinted(graphics, stack, itemX, itemY, mapping.s4() * ITEM_VISUAL_SCALE * scale, false, 0.0f, 0.0f, 0.0f, 0.65f);
            } else if (!craftable) {
                drawScaledItemTinted(graphics, stack, itemX, itemY, mapping.s4() * ITEM_VISUAL_SCALE * scale, false, 0.0f, 0.0f, 0.0f, 0.35f);
                drawScaledItemTinted(graphics, stack, itemX, itemY, mapping.s4() * ITEM_VISUAL_SCALE * scale, false, 0.41f, 0.41f, 0.41f, 0.40f);
            }"""
new_grid_tint = """            if (!unlocked) {
                drawSilhouetteItem(graphics, stack, itemX, itemY, mapping.s4() * ITEM_VISUAL_SCALE * scale);
            } else if (!craftable) {
                drawScaledItemTinted(graphics, stack, itemX, itemY, mapping.s4() * ITEM_VISUAL_SCALE * scale, false, 1.0f, 1.0f, 1.0f, 0.4f); // 0.4 meaning 0.6 black overlay
            }"""
text = text.replace(old_grid_tint, new_grid_tint)


# Fix earlier item count code in Recipe Grid
old_count2 = """            if (stack.getCount() > 1) {
                int countColor = (unlocked && craftable) ? 0xFFFFFFFF : 0xBEBEBE;
                drawCraftCount(graphics, stack.getCount(), x + ui(64) - ui(2), y + ui(64) - ui(2), countColor, scale);
            }"""
new_count2 = """            if (stack.getCount() > 1) {
                int countColor = (unlocked && craftable) ? 0xFFFFFFFF : 0xBEBEBE;
                drawMCStyleCount(graphics, stack.getCount(), x, y, ui(64), countColor);
            }"""
text = text.replace(old_count2, new_count2)

# Fix drawCraftCount method to drawMCStyleCount
old_craft_count = """    private void drawCraftCount(GuiGraphics graphics, int count, int anchorX, int anchorY, int color, float recipeScaleMultiplier) {
        if (count <= 1) {
            return;
        }
        String text = Integer.toString(count);
        float numberScale = 0.5f * recipeScaleMultiplier;
        int textWidth = this.font.width(text);

        graphics.pose().pushPose();
        graphics.pose().translate(anchorX, anchorY, 0.0f);
        graphics.pose().scale(numberScale, numberScale, 1.0f);
        int drawX = Math.round(-textWidth / numberScale);
        int drawY = Math.round(-ui(10) / numberScale);
        graphics.drawString(this.font, text, drawX, drawY, color, true);
        graphics.pose().popPose();
    }"""
new_mc_style_count = """    private void drawMCStyleCount(GuiGraphics graphics, int count, int slotX, int slotY, int slotSize, int color) {
        if (count <= 1) {
            return;
        }
        String text = Integer.toString(count);
        int textWidth = this.font.width(text);
        
        graphics.pose().pushPose();
        graphics.pose().translate(slotX + slotSize - textWidth - 2, slotY + slotSize - 10, 250.0f);
        graphics.drawString(this.font, text, 0, 0, color, true);
        graphics.pose().popPose();
    }"""
text = text.replace(old_craft_count, new_mc_style_count)


# Inventory slot fix: items strictly in bounds and count standard MC
old_inv_slot = """    private void drawInventorySlot(GuiGraphics graphics, int x, int y, ItemStack stack, boolean hovered) {
        StardewGuiUtil.drawMenuTileIndex(graphics, x, y, ui(INVENTORY_SLOT_SDV), ui(INVENTORY_SLOT_SDV), 10);
        if (hovered) {
            graphics.fill(x, y, x + ui(INVENTORY_SLOT_SDV), y + ui(INVENTORY_SLOT_SDV), 0x35FFFFFF);
        }
        if (!stack.isEmpty()) {
            drawScaledItem(graphics, stack, x, y, mapping.s4() * ITEM_VISUAL_SCALE, false);
            drawItemCount(graphics, stack, x + ui(INVENTORY_SLOT_SDV - 2), y + ui(INVENTORY_SLOT_SDV - 2), 0xFFFFFFFF);
        }
    }"""
new_inv_slot = """    private void drawInventorySlot(GuiGraphics graphics, int x, int y, ItemStack stack, boolean hovered) {
        StardewGuiUtil.drawMenuTileIndex(graphics, x, y, ui(INVENTORY_SLOT_SDV), ui(INVENTORY_SLOT_SDV), 10);
        if (hovered) {
            graphics.pose().pushPose();
            graphics.pose().translate(0, 0, 100);
            graphics.fill(x, y, x + ui(INVENTORY_SLOT_SDV), y + ui(INVENTORY_SLOT_SDV), 0x35FFFFFF);
            graphics.pose().popPose();
        }
        if (!stack.isEmpty()) {
            float drawScale = mapping.s4() * 0.8f; // strictly within bounds
            int scaledW = Math.round(16 * drawScale);
            int boxW = ui(INVENTORY_SLOT_SDV);
            int offsetX = (boxW - scaledW) / 2;
            int offsetY = (boxW - scaledW) / 2;
            
            drawScaledItem(graphics, stack, x + offsetX, y + offsetY, drawScale, false);
            drawMCStyleCount(graphics, stack.getCount(), x, y, boxW, 0xFFFFFFFF);
        }
    }"""
text = text.replace(old_inv_slot, new_inv_slot)


# Tooltip adjustments: enlarge icon
old_tooltip = """            for (int i = 0; i < requirements.size(); i++) {
                RecipeRequirement req = requirements.get(i);
                int iconX = boxX + this.font.width("✔  ");
                int iconY = boxY + 10 * (i + 1); // 0 is "需要", so +1
                graphics.pose().pushPose();
                graphics.pose().translate(iconX, iconY - 2, 800); // 800 z-index to overlay tooltip
                graphics.pose().scale(0.5f, 0.5f, 1.0f);
                graphics.renderItem(req.icon(), 0, 0);
                graphics.pose().popPose();
            }"""
new_tooltip = """            for (int i = 0; i < requirements.size(); i++) {
                RecipeRequirement req = requirements.get(i);
                float s = 0.75f; // Similar height to text
                int iconX = boxX + this.font.width("✔ ");
                int iconY = boxY + 10 * (i + 1) - 1; // Center with the line
                graphics.pose().pushPose();
                graphics.pose().translate(iconX, iconY, 800.0f);
                graphics.pose().scale(s, s, 1.0f);
                graphics.renderItem(req.icon(), 0, 0);
                graphics.pose().popPose();
            }"""
text = text.replace(old_tooltip, new_tooltip)


# Drop unused drawItemCount to avoid compiler warnings if it's unused
# Wait, drawItemCount might still be used by drawScaledItemTinted. 
# Let's check drawScaledItemTinted's drawDecorations usage:
# Yes it calls drawItemCount.
# Since we replaced it in grid, what about other callers?
# drawScaledItem calls drawScaledItemTinted(.., false, ..) so no decorations.
# So I should remove drawItemCount entirely and replace it in drawScaledItemTinted if used, 
# but drawScaledItemTinted is called with `drawDecorations=false` everywhere now. 
# Let's just fix drawItemCount just in case.

old_item_count = """    private void drawItemCount(GuiGraphics graphics, ItemStack stack, int x, int y, int color) {
        if (stack.getCount() <= 1) {
            return;
        }
        String count = Integer.toString(stack.getCount());
        int w = this.font.width(count);
        graphics.drawString(this.font, count, x - w, y - ui(10), color, true);
    }"""
new_item_count = """    private void drawItemCount(GuiGraphics graphics, ItemStack stack, int x, int y, int color) {
        if (stack.getCount() <= 1) {
            return;
        }
        String count = Integer.toString(stack.getCount());
        int w = this.font.width(count);
        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 250);
        graphics.drawString(this.font, count, x - w, y - ui(10), color, true);
        graphics.pose().popPose();
    }"""
text = text.replace(old_item_count, new_item_count)

with open(path, "w", encoding="utf-8") as f:
    f.write(text)
