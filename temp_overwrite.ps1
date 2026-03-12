
$code = @"
package com.stardew.craft.client.gui;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.cooking.service.VanillaCookingRecipeData;
import com.stardew.craft.item.ModItems;
import com.stardew.craft.item.cooking.CookingDishItem;
import com.stardew.craft.menu.CookingPotMenu;
import com.stardew.craft.network.payload.CookingPotCookSubmitPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

@SuppressWarnings("null")
public class CookingPotScreen extends AbstractContainerScreen<CookingPotMenu> {
    private static final int UI_WIDTH = 340;
    private static final int UI_HEIGHT = 260;

    private static final int OVERLAY_BG = 0xE608080A; // Deeper luxury black
    private static final int HIGHLIGHT_COLOR = 0xAAFFD700; // Gold highlight
    private static final int TITLE_COLOR = 0xFFF0D080;
    
    private final List<ItemStack> availableRecipes = new ArrayList<>();
    private final List<ResourceLocation> recipeIds = new ArrayList<>();
    private int hoverRecipeIndex = -1;
    private int recipePage = 0;
    
    // Smooth transition vars
    private int currentFocusIndex = -1;

    public CookingPotScreen(CookingPotMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = UI_WIDTH;
        this.imageHeight = UI_HEIGHT;
    }

    @Override
    protected void init() {
        super.init();
        availableRecipes.clear();
        recipeIds.clear();
        for (String idStr : ModItems.COOKING_DISHES.keySet()) {
            ResourceLocation rl = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, idStr);
            Item item = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(rl);
            if (item != null && item != Items.AIR) {
                availableRecipes.add(new ItemStack(item));
                recipeIds.add(rl);
            }
        }
        if (!availableRecipes.isEmpty()) {
            currentFocusIndex = 0;
        }
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(g, mouseX, mouseY, partialTick); 
        super.render(g, mouseX, mouseY, partialTick);
        this.renderTooltip(g, mouseX, mouseY); 
    }

    @Override
    protected void renderLabels(GuiGraphics g, int mouseX, int mouseY) {
        g.drawString(this.font, Component.translatable("stardewcraft.cooking.title"), 16, 12, TITLE_COLOR, false);
        g.drawString(this.font, "INVENTORY", 89, 158, 0xFF707070, false);
        
        int maxPage = Math.max(1, ((availableRecipes.size() - 1) / 45) + 1);
        String pageTxt = "PAGE " + (recipePage + 1) + " / " + maxPage;
        g.drawString(this.font, pageTxt, 200 - this.font.width(pageTxt), 12, 0xFF707070, false);
    }

    @Override
    protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        // Dynamic focused index updates smoothly when hovering
        if (hoverRecipeIndex != -1) {
            currentFocusIndex = hoverRecipeIndex;
        }

        // Draw Base Panel
        g.fill(x, y, x + imageWidth, y + imageHeight, OVERLAY_BG);
        g.hLine(x, x + imageWidth - 1, y, 0x5CFFFFFF);
        g.hLine(x, x + imageWidth - 1, y + imageHeight - 1, 0x22FFFFFF);
        g.vLine(x, y, y + imageHeight - 1, 0x5CFFFFFF);
        g.vLine(x + imageWidth - 1, y, y + imageHeight - 1, 0x22FFFFFF);

        // Subtly frame inventory
        for (Slot slot : this.menu.slots) {
            int sx = x + slot.x;
            int sy = y + slot.y;
            g.fill(sx, sy, sx + 16, sy + 16, 0x3A000000);
            g.fill(sx, sy + 15, sx + 16, sy + 16, 0x30FFFFFF); 
        }

        hoverRecipeIndex = -1; // reset hover

        // 1. Draw Grid (9x5)
        int gridX = x + 16;
        int gridY = y + 30;
        int startIndex = recipePage * 45;
        
        for (int i = 0; i < 45; i++) {
            int cx = gridX + (i % 9) * 20;
            int cy = gridY + (i / 9) * 20;
            
            // Cell background
            g.fill(cx, cy, cx + 18, cy + 18, 0x22FFFFFF);

            int dataIndex = startIndex + i;
            if (dataIndex < availableRecipes.size()) {
                ItemStack stack = availableRecipes.get(dataIndex);
                
                boolean isHover = mouseX >= cx && mouseX < cx + 18 && mouseY >= cy && mouseY < cy + 18;
                if (isHover) {
                    hoverRecipeIndex = dataIndex;
                    g.fill(cx - 1, cy - 1, cx + 19, cy + 19, HIGHLIGHT_COLOR); // Gold hover frame
                    g.fill(cx, cy, cx + 18, cy + 18, 0x80000000);
                } else if (dataIndex == currentFocusIndex) {
                    g.fill(cx, cy, cx + 18, cy + 18, 0x50FFFFFF); // Soft white for last focus
                }
                
                g.renderItem(stack, cx + 1, cy + 1);
            }
        }

        // Draw Divider
        g.vLine(x + 204, y + 25, y + 145, 0x40FFFFFF);

        // 2. Draw Dynamic Preview Area
        if (currentFocusIndex >= 0 && currentFocusIndex < availableRecipes.size()) {
            drawPreviewPanel(g, x + 212, y + 30, availableRecipes.get(currentFocusIndex), recipeIds.get(currentFocusIndex));
        }
    }

    private void drawPreviewPanel(GuiGraphics g, int px, int py, ItemStack selStack, ResourceLocation recipeId) {
        // High-end showcase box
        g.fill(px, py, px + 115, py + 120, 0x40000000);
        
        // Large Item Model
        g.pose().pushPose();
        g.pose().translate(px + 57, py + 25, 0); // center
        g.pose().scale(2.5f, 2.5f, 1.0f);
        g.renderItem(selStack, -8, -8);
        g.pose().popPose();

        // Title
        Component title = selStack.getHoverName();
        int maxW = 105;
        if (this.font.width(title) > maxW) {
            // Trim
            String trimmed = this.font.plainSubstrByWidth(title.getString(), maxW - 10) + "...";
            g.drawString(this.font, trimmed, px + 5, py + 45, TITLE_COLOR, false);
        } else {
            g.drawString(this.font, title, px + 57 - this.font.width(title)/2, py + 45, TITLE_COLOR, false);
        }

        // Energy & Health if applicable
        Item item = selStack.getItem();
        int yOffset = py + 60;
        if (item instanceof CookingDishItem dish) {
            int e = dish.getEnergy(selStack);
            int h = dish.getHealth(selStack);
            if (e > 0) {
                g.drawString(this.font, "+ " + e + " E", px + 5, yOffset, 0xFF88FF88, false);
                if (h > 0) g.drawString(this.font, "+ " + h + " H", px + 55, yOffset, 0xFFFF8888, false);
                yOffset += 12;
            }
        }

        // Requirements
        List<VanillaCookingRecipeData.IngredientRequirement> reqs = VanillaCookingRecipeData.getRequirements(recipeId.getPath());
        if (!reqs.isEmpty()) {
            g.drawString(this.font, "REQ:", px + 5, yOffset, 0xAAFFFFFF, false);
            
            for (int i = 0; i < Math.min(3, reqs.size()); i++) {
                VanillaCookingRecipeData.IngredientRequirement req = reqs.get(i);
                ItemStack icon = resolveRequirementIcon(req.token());
                int has = countMatching(req.token());
                int need = req.count();
                boolean enough = has >= need;
                
                int rx = px + 30 + (i * 26);
                int ry = yOffset - 2;
                g.renderItem(icon, rx, ry);
                
                g.pose().pushPose();
                g.pose().translate(0, 0, 200); // Front text
                g.drawString(this.font, has + "/" + need, rx + 16 - this.font.width(has+"/"+need)/2, ry + 12, enough ? 0xAAFFAA : 0xFF8888, true);
                g.pose().popPose();
            }
        }
        
        // Fast instructions
        g.drawString(this.font, "LMB: Cook", px + 5, py + 95, 0xFFAAAAAA, false);
        g.drawString(this.font, "Shift+LMB: Cook All", px + 5, py + 107, 0xFFAAAAAA, false);
    }

    private int countMatching(String token) {
        if (this.minecraft == null || this.minecraft.player == null) return 0;
        int count = 0;
        for (int i = 0; i < this.minecraft.player.getInventory().getContainerSize(); i++) {
            ItemStack stack = this.minecraft.player.getInventory().getItem(i);
            if (!stack.isEmpty() && VanillaCookingRecipeData.matchesToken(stack, token)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private ItemStack resolveRequirementIcon(String token) {
        Item item = VanillaCookingRecipeData.resolveTokenItem(token);
        return item == null ? new ItemStack(Items.BARRIER) : new ItemStack(item);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) return true;

        int x = this.leftPos;
        int y = this.topPos;

        if (button == 0 && mouseX >= x + 180 && mouseX <= x + 210 && mouseY >= y + 8 && mouseY <= y + 22) {
            if (hasShiftDown()) recipePage = Math.max(0, recipePage - 1);
            else recipePage = Math.min((availableRecipes.size() - 1) / 45, recipePage + 1);
            return true;
        }

        // Direct craft upon clicking the grid item
        if (hoverRecipeIndex >= 0 && hoverRecipeIndex < availableRecipes.size()) {
            int craftCount = button == 1 ? 5 : 1;
            if (hasShiftDown()) craftCount = -1; 
            
            String itemPath = recipeIds.get(hoverRecipeIndex).getPath();
            PacketDistributor.sendToServer(new CookingPotCookSubmitPayload(itemPath, craftCount));
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double hDelta, double vDelta) {
        if (vDelta > 0 && recipePage > 0) {
            recipePage--;
            return true;
        } else if (vDelta < 0 && recipePage < (availableRecipes.size() - 1) / 45) {
            recipePage++;
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, hDelta, vDelta);
    }
}
"@
Set-Content -Path "src\main\java\com\stardew\craft\client\gui\CookingPotScreen.java" -Value $code

