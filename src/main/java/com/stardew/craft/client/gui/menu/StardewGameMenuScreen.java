package com.stardew.craft.client.gui.menu;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.client.ClientPlayerDataCache;
import com.stardew.craft.client.gui.common.StardewRenderMapping;
import com.stardew.craft.client.gui.overnight.StardewGuiUtil;
import com.stardew.craft.network.payload.CraftingMenuCraftSubmitPayload;
import com.stardew.craft.network.payload.CraftingMenuInventoryActionPayload;
import com.stardew.craft.player.RecipeCatalogData;
import com.stardew.craft.player.StardewCraftingRecipeData;
import com.stardew.craft.sound.ModSounds;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"null", "unused"})
public class StardewGameMenuScreen extends Screen {
    private static final int STD_TILE_SIZE = 16;
    private static final ResourceLocation VANILLA_PAGE_UP = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/crafting/vanilla_page_up.png");
    private static final ResourceLocation VANILLA_PAGE_DOWN = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/crafting/vanilla_page_down.png");
    private static final ResourceLocation VANILLA_TRASH_BODY = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/crafting/vanilla_trashcan_body.png");
    private static final ResourceLocation VANILLA_TRASH_LID = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/crafting/vanilla_trashcan_lid.png");
    private static final int TRASH_W = 18;
    private static final int TRASH_BODY_H = 26;
    private static final int TRASH_LID_H = 10;
    private static final float ITEM_VISUAL_SCALE = 1.0f;
    private static final int CARRIED_ITEM_OFFSET_SDV = 16;
    private static final int INVENTORY_COLS = 9;
    private static final int INVENTORY_ROWS = 4;
    private static final int INVENTORY_SLOT_SDV = 64;
    private static final int INVENTORY_GAP_SDV = 4;
    private static final int INVENTORY_TOP_SDV = 368;

    private static final int BORDER_WIDTH = 32;
    private static final int MENU_WIDTH_SDV = 800 + BORDER_WIDTH * 2;
    private static final int MENU_HEIGHT_SDV = 600 + BORDER_WIDTH * 2;
    private static final int TAB_Y_OFFSET_SDV = -56;
    private static final int TAB_START_X_SDV = 64;
    private static final int TAB_STEP_SDV = 64;
    private static final int TAB_SIZE_SDV = 64;

    private static final int CLOSE_X_OFFSET_SDV = 36;
    private static final int CLOSE_Y_OFFSET_SDV = 8;
    private static final int CLOSE_SIZE_SDV = 48;

    private static final int[] TAB_SHEET_INDEX = new int[] {0, 1, 2, 3, 4, -1, -1, 5, 6, 7};
    private static final int TAB_COUNT = 10;

    private static final String[] TAB_KEYS = new String[] {
            "stardewcraft.game_menu.tab.inventory",
            "stardewcraft.game_menu.tab.skills",
            "stardewcraft.game_menu.tab.social",
            "stardewcraft.game_menu.tab.map",
            "stardewcraft.game_menu.tab.crafting",
            "stardewcraft.game_menu.tab.animals",
            "stardewcraft.game_menu.tab.powers",
            "stardewcraft.game_menu.tab.collections",
            "stardewcraft.game_menu.tab.options",
            "stardewcraft.game_menu.tab.exit"
    };

    private StardewRenderMapping mapping;
    private int menuX;
    private int menuY;
    private int menuWidth;
    private int menuHeight;
    private int currentTab;

    private int currentCraftingPage;
    private int selectedCraftingIndex = -1;
    private List<ItemStack> craftingRecipeStacks = List.of();
    private List<String> craftingRecipeIds = List.of();
    private List<List<RecipeCell>> craftingPages = List.of();
    private final float[] recipeHoverScale = new float[40];
    private int hoveredCraftingIndex = -1;
    private float upButtonScale = 1.0f;
    private float downButtonScale = 1.0f;
    private float trashCanLidRotation;
    private boolean trashCanLidSoundPlayed;
    
    private int currentFocusIndex = -1;
    private float showcaseAlpha = 0.0f;

    private record RecipeRequirement(Ingredient ingredient, ItemStack icon, int need) {
    }

    private record RecipeCell(int recipeIndex, int x, int y, boolean bigCraftable) {
    }

    private record RecipeEntry(String id, ItemStack stack, int rank) {
    }

    public StardewGameMenuScreen() {
        super(Component.translatable("stardewcraft.game_menu.title"));
    }

    @Override
    protected void init() {
        super.init();
        recalcLayout();
        rebuildCraftingEntries();
        playUiSound(ModSounds.BIG_SELECT.get(), 1.0f, 1.0f);
    }

    private float guiScale() {
        return this.minecraft == null ? 1.0f : (float) this.minecraft.getWindow().getGuiScale();
    }

    private int ui(int stardewPixels) {
        return mapping == null ? Math.round(stardewPixels / guiScale()) : mapping.ui(stardewPixels);
    }

    private void recalcLayout() {
        this.mapping = new StardewRenderMapping(this.width, this.height, guiScale());
        this.menuWidth = ui(MENU_WIDTH_SDV);
        this.menuHeight = ui(MENU_HEIGHT_SDV);
        this.menuX = mapping.centerX(this.menuWidth);
        this.menuY = this.height / 2 - this.menuHeight / 2;
    }

    private void playUiSound(SoundEvent sound, float volume, float pitch) {
        if (this.minecraft != null) {
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(sound, volume, pitch));
        }
    }

    private int tabX(int index) {
        return menuX + ui(TAB_START_X_SDV + index * TAB_STEP_SDV);
    }

    private int tabY() {
        return menuY + ui(TAB_Y_OFFSET_SDV);
    }

    private int tabSize() {
        return ui(TAB_SIZE_SDV);
    }

    private boolean tabContains(int index, double mouseX, double mouseY) {
        int x = tabX(index);
        int y = tabY();
        int s = tabSize();
        return mouseX >= x && mouseX < x + s && mouseY >= y && mouseY < y + s;
    }

    private boolean closeContains(double mouseX, double mouseY) {
        int x = menuX + menuWidth - ui(CLOSE_X_OFFSET_SDV);
        int y = menuY - ui(CLOSE_Y_OFFSET_SDV);
        int s = ui(CLOSE_SIZE_SDV);
        return mouseX >= x && mouseX < x + s && mouseY >= y && mouseY < y + s;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        recalcLayout();
        graphics.fill(0, 0, this.width, this.height, 0x66000000);

        if (currentTab == 4) {
            updateCraftingHoverState(mouseX, mouseY);
            updateVisualFocus();
        }

        StardewGuiUtil.drawDialogueBoxFrame(graphics, menuX, menuY, menuWidth, menuHeight);
        drawTabs(graphics);
        drawCloseButton(graphics);
        drawCurrentPage(graphics, mouseX, mouseY);

        if (currentTab == 4) {
            if (craftingPages.size() > 1) {
                drawPageArrows(graphics, mouseX, mouseY);
            }
            drawCraftingTooltips(graphics, mouseX, mouseY);
            ItemStack carried = currentCarriedItem();
            if (!carried.isEmpty()) {
                int drawX = mouseX - 8;
                int drawY = mouseY - 8;
                graphics.pose().pushPose();
                graphics.pose().translate(0, 0, 500);
                graphics.renderItem(carried, drawX, drawY);
                graphics.renderItemDecorations(this.font, carried, drawX, drawY);
                graphics.pose().popPose();
            }
        }

        int hoveredTab = hoveredTab(mouseX, mouseY);
        if (hoveredTab >= 0) {
            graphics.renderTooltip(this.font, Component.translatable(TAB_KEYS[hoveredTab]), mouseX, mouseY);
        }
    }

    private int hoveredTab(int mouseX, int mouseY) {
        for (int i = 0; i < TAB_COUNT; i++) {
            if (tabContains(i, mouseX, mouseY)) {
                return i;
            }
        }
        return -1;
    }

    private void drawTabs(GuiGraphics graphics) {
        int yBase = tabY();
        int selectedOffsetY = ui(8);
        float scale = mapping.s4();

        for (int i = 0; i < TAB_COUNT; i++) {
            int x = tabX(i);
            int y = yBase + (currentTab == i ? selectedOffsetY : 0);

            if (i == 5) {
                StardewGuiUtil.drawFromCursors16(graphics, x, y, 257, 246, 16, 16, scale);
                continue;
            }
            if (i == 6) {
                StardewGuiUtil.drawFromCursors16(graphics, x, y, 216, 494, 16, 16, scale);
                continue;
            }

            int sheetIndex = TAB_SHEET_INDEX[i];
            StardewGuiUtil.drawFromCursors(graphics, x, y, sheetIndex * 16, 368, 16, 16, scale);
        }
    }

    private void drawCloseButton(GuiGraphics graphics) {
        int x = menuX + menuWidth - ui(CLOSE_X_OFFSET_SDV);
        int y = menuY - ui(CLOSE_Y_OFFSET_SDV);
        StardewGuiUtil.drawFromCursors(graphics, x, y, 337, 494, 12, 12, mapping.s4());
    }

    private void drawCurrentPage(GuiGraphics graphics, int mouseX, int mouseY) {
        if (currentTab == 4) {
            drawCraftingPage(graphics, mouseX, mouseY);
            return;
        }

        Component tabName = Component.translatable(TAB_KEYS[currentTab]);
        Component title = Component.translatable("stardewcraft.game_menu.placeholder_title", tabName);
        Component line = Component.translatable("stardewcraft.game_menu.placeholder_body");

        int titleX = menuX + menuWidth / 2 - this.font.width(title) / 2;
        int lineX = menuX + menuWidth / 2 - this.font.width(line) / 2;
        int centerY = menuY + menuHeight / 2;

        graphics.drawString(this.font, title, titleX, centerY - 10, 0xFFF3E6C6, false);
        graphics.drawString(this.font, line, lineX, centerY + 8, 0xFFD8C9A8, false);
    }

    private void rebuildCraftingEntries() {
        List<String> recipeIds = new ArrayList<>(RecipeCatalogData.getCraftingRecipeIds());

        List<RecipeEntry> entries = new ArrayList<>();
        for (String recipeId : recipeIds) {
            ItemStack stack = resolveRecipeResultStack(recipeId);
            if (stack.isEmpty()) {
                continue;
            }

            boolean unlocked = ClientPlayerDataCache.hasRecipe(recipeId);
            int rank;
            if (!unlocked) {
                rank = 2;
            } else {
                boolean craftable = computeMaxCraftsClient(getRecipeIngredients(recipeId), 1) > 0;
                rank = craftable ? 0 : 1;
            }
            entries.add(new RecipeEntry(recipeId, stack, rank));
        }

        entries.sort((a, b) -> {
            int rankCmp = Integer.compare(a.rank(), b.rank());
            if (rankCmp != 0) {
                return rankCmp;
            }
            return a.id().compareTo(b.id());
        });

        List<String> validIds = new ArrayList<>(entries.size());
        List<ItemStack> stacks = new ArrayList<>(entries.size());
        for (RecipeEntry entry : entries) {
            validIds.add(entry.id());
            stacks.add(entry.stack());
        }

        this.craftingRecipeIds = validIds;
        this.craftingRecipeStacks = stacks;
        this.craftingPages = buildCraftingPages();

        if (craftingRecipeIds.isEmpty()) {
            selectedCraftingIndex = -1;
        } else if (selectedCraftingIndex < 0 || selectedCraftingIndex >= craftingRecipeIds.size()) {
            selectedCraftingIndex = 0;
        }

        clampCraftingPage();
    }

    private ItemStack resolveRecipeResultStack(String recipeId) {
        return StardewCraftingRecipeData.getOutputStack(recipeId);
    }

    private void clampCraftingPage() {
        int maxPage = Math.max(0, craftingPages.size() - 1);
        if (currentCraftingPage < 0) {
            currentCraftingPage = 0;
        }
        if (currentCraftingPage > maxPage) {
            currentCraftingPage = maxPage;
        }
    }

    private List<List<RecipeCell>> buildCraftingPages() {
        List<List<RecipeCell>> pages = new ArrayList<>();
        if (craftingRecipeIds.isEmpty()) {
            pages.add(List.of());
            return pages;
        }

        List<RecipeCell> currentPage = new ArrayList<>();
        int capacity = 30;
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
    }

    private boolean isBigCraftable(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof BlockItem;
    }

    private void updateVisualFocus() {
        int targetIndex = hoveredCraftingIndex;
        if (targetIndex != currentFocusIndex) {
            float lerpFactor = 0.2f;
            this.showcaseAlpha = Mth.lerp(lerpFactor, this.showcaseAlpha, 0.0f);
            if (this.showcaseAlpha < 0.05f) {
                this.currentFocusIndex = targetIndex;
                if (targetIndex != -1) {
                    this.showcaseAlpha = 0.05f; 
                }
            }
        } else if (targetIndex != -1) {
            if (this.showcaseAlpha < 1.0f) {
                this.showcaseAlpha = Mth.lerp(0.15f, this.showcaseAlpha, 1.0f);
            }
        } else {
            this.showcaseAlpha = 0.0f;
        }
    }

    private void drawElegantShowcase(GuiGraphics g, int px, int py, int focusIdx) {
        if (focusIdx < 0 || focusIdx >= craftingRecipeStacks.size()) return;

        ItemStack stack = craftingRecipeStacks.get(focusIdx);
        if (stack.isEmpty()) return;

        String recipeId = craftingRecipeIds.get(focusIdx);
        boolean unlocked = ClientPlayerDataCache.hasRecipe(recipeId);

        int alphaBits = Math.max(0, Math.min(255, (int) (this.showcaseAlpha * 255))) << 24;
        if ((alphaBits & 0xFF000000) == 0) return;

        g.pose().pushPose();
        g.pose().translate(px, py, 300);

        Component title = unlocked ? stack.getHoverName().copy().withStyle(net.minecraft.ChatFormatting.BOLD)
                : Component.literal("???").withStyle(net.minecraft.ChatFormatting.BOLD);

        float titleScale = 1.5f;
        int tw = this.font.width(title);
        
        int tx = (ui(240) - (int)(tw * titleScale)) / 2;
        g.pose().pushPose();
        g.pose().translate(tx, ui(20), 0);
        g.pose().scale(titleScale, titleScale, 1.0f);
        g.drawString(this.font, title, 1, 1, alphaBits | 0x2d170a, false);
        g.drawString(this.font, title, 0, 0, alphaBits | 0xFFFFFF, false);
        g.pose().popPose();

        int dropY = ui(120);
        float floatY = (float)Math.sin((System.currentTimeMillis() % 6000) / 6000.0f * Math.PI * 2) * 5.0f;

        g.pose().pushPose();
        g.pose().translate(ui(120), dropY + ui(30), 0);
        g.pose().scale(4.0f, 4.0f, 1.0f);
        g.fill(-8, -4, 8, 4, 0x44000000 | (alphaBits & 0xFF000000));
        g.pose().popPose();

        g.pose().pushPose();
        g.pose().translate(ui(120), dropY + floatY, 0);
        g.pose().scale(6.0f, 6.0f, 1.0f);
        
        if (!unlocked) {
            com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
            g.renderItem(stack, -8, -8);
            com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        } else {
            g.renderItem(stack, -8, -8);
        }
        
        g.pose().popPose();

        g.pose().popPose();
    }

    private void drawCraftingPage(GuiGraphics graphics, int mouseX, int mouseY) {
        StardewGuiUtil.drawHorizontalPartition(graphics, menuX, menuY + ui(312), menuWidth, ui(64));

        drawPlayerInventory(graphics, mouseX, mouseY);
        drawTrashCan(graphics, mouseX, mouseY);
        drawRecipeGrid(graphics, mouseX, mouseY);

        if (this.showcaseAlpha > 0.01f && this.currentFocusIndex >= 0 && this.currentFocusIndex < craftingRecipeIds.size()) {
            int px = menuX - ui(260);
            int py = menuY + ui(32);
            drawElegantShowcase(graphics, px, py, currentFocusIndex);
        }
    }

    private void drawRecipeGrid(GuiGraphics graphics, int mouseX, int mouseY) {
        if (currentCraftingPage < 0 || currentCraftingPage >= craftingPages.size()) {
            return;
        }

        List<RecipeCell> page = craftingPages.get(currentCraftingPage);
        for (int local = 0; local < page.size(); local++) {
            RecipeCell cellData = page.get(local);
            int index = cellData.recipeIndex();
            if (index < 0 || index >= craftingRecipeIds.size()) {
                continue;
            }

            int x = menuX + ui(80 + cellData.x() * 72);
            int y = menuY + ui(80 + cellData.y() * 72);
            int h = cellData.bigCraftable() ? ui(128) : ui(64);

            ItemStack stack = craftingRecipeStacks.get(index);
            String recipeId = craftingRecipeIds.get(index);
            boolean unlocked = ClientPlayerDataCache.hasRecipe(recipeId);
            boolean craftable = computeMaxCraftsClient(getRecipeIngredients(recipeId), 1) > 0;
            boolean hovered = recipeContains(mouseX, mouseY, x, y, h, ui(4));

            if (local >= 0 && local < recipeHoverScale.length) {
                recipeHoverScale[local] = stepScale(recipeHoverScale[local], hovered ? 1.1f : 1.0f, 0.02f);
            }
            float scale = (local >= 0 && local < recipeHoverScale.length && recipeHoverScale[local] > 0.001f)
                    ? recipeHoverScale[local]
                    : 1.0f;

            int baseSlot = ui(64);
            int mcSize = 16;
            int itemX = x + (baseSlot - mcSize) / 2;
            int itemY = y + (baseSlot - mcSize) / 2;

            graphics.pose().pushPose();
            graphics.pose().translate(itemX + 8, itemY + 8, 0);
            graphics.pose().scale(scale, scale, 1.0f);
            
            if (!unlocked) {
                com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
                graphics.renderItem(stack, -8, -8);
                com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            } else if (!craftable) {
                com.mojang.blaze3d.systems.RenderSystem.setShaderColor(0.35F, 0.35F, 0.35F, 1.0F);
                graphics.renderItem(stack, -8, -8);
                com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            } else {
                graphics.renderItem(stack, -8, -8);
            }
            
            graphics.pose().popPose();

            if (unlocked && hasShiftDown()) {
                int maxCrafts = computeMaxCraftsClient(getRecipeIngredients(recipeId), 999);
                if (maxCrafts > 0) {
                    ItemStack countStack = stack.copy();
                    countStack.setCount(maxCrafts);
                    graphics.renderItemDecorations(this.font, countStack, itemX, itemY);
                } else if (stack.getCount() > 1) {
                    graphics.renderItemDecorations(this.font, stack, itemX, itemY);
                }
            } else if (stack.getCount() > 1 && unlocked) {
                graphics.renderItemDecorations(this.font, stack, itemX, itemY);
            }
        }
    }

    // drawMCStyleCount removed

    private void drawPageArrows(GuiGraphics graphics, int mouseX, int mouseY) {
        int upX = pageButtonX();
        int upY = pageUpButtonY();
        int downX = pageButtonX();
        int downY = pageDownButtonY();

        boolean upHovered = upButtonContains(mouseX, mouseY);
        boolean downHovered = downButtonContains(mouseX, mouseY);
        upButtonScale = stepScale(upButtonScale, upHovered ? 0.9f : 0.8f, 0.025f);
        downButtonScale = stepScale(downButtonScale, downHovered ? 0.9f : 0.8f, 0.025f);

        if (currentCraftingPage > 0) {
            drawArrowFromCursors(graphics, upX, upY, 12, upButtonScale);
        }
        if (currentCraftingPage < Math.max(0, craftingPages.size() - 1)) {
            drawArrowFromCursors(graphics, downX, downY, 11, downButtonScale);
        }
    }

    private void drawArrowFromCursors(GuiGraphics graphics, int boundX, int boundY, int tilePosition, float vanillaScale) {
        float drawScale = mapping.s4() * vanillaScale;
        // 11 is down arrow, 12 is up arrow in standard Stardew logic (from Game1.mouseCursors)
        // Up arrow: u=421, v=459, w=11, h=12
        // Down arrow: u=421, v=472, w=11, h=12
        int u = 421;
        int v = (tilePosition == 12) ? 459 : 472;
        int w = 11;
        int h = 12;

        int offsetX = (ui(64) - Math.round(w * drawScale)) / 2;
        int offsetY = (ui(64) - Math.round(h * drawScale)) / 2;

        graphics.pose().pushPose();
        graphics.pose().translate(boundX + offsetX, boundY + offsetY, 0);
        graphics.pose().scale(drawScale, drawScale, 1.0f);
        graphics.blit(StardewGuiUtil.CURSORS, 0, 0, u, v, w, h, StardewGuiUtil.CURSORS_WIDTH, StardewGuiUtil.CURSORS_HEIGHT);
        graphics.pose().popPose();
    }

    private void drawTrashCan(GuiGraphics graphics, int mouseX, int mouseY) {
        int bodyX = menuX + menuWidth + ui(4);
        int bodyY = menuY + menuHeight - ui(360);
        boolean hovered = trashCanContains(mouseX, mouseY);
        if (hovered && !trashCanLidSoundPlayed) {
            playUiSound(ModSounds.TRASHCANLID.get(), 1.0f, 1.0f);
            trashCanLidSoundPlayed = true;
        }
        if (!hovered) {
            trashCanLidSoundPlayed = false;
        }
        float step = (float) Math.PI / 48.0f;
        if (hovered) {
            trashCanLidRotation = Math.min(trashCanLidRotation + step, (float) Math.PI / 2.0f);
        } else {
            trashCanLidRotation = Math.max(trashCanLidRotation - step, 0.0f);
        }

        drawScaledTexture(graphics, VANILLA_TRASH_BODY, bodyX, bodyY, TRASH_W, TRASH_BODY_H, mapping.s4());

        int lidDrawX = bodyX + ui(60);
        int lidDrawY = bodyY + ui(40);
        drawRotatedScaledTexture(graphics, VANILLA_TRASH_LID, lidDrawX, lidDrawY, TRASH_W, TRASH_LID_H, mapping.s4(), trashCanLidRotation, 16, 10, 0, 0, TRASH_W, TRASH_LID_H);
    }

    private void drawPlayerInventory(GuiGraphics graphics, int mouseX, int mouseY) {
        Minecraft mc = this.minecraft;
        if (mc == null || mc.player == null) {
            return;
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < INVENTORY_COLS; col++) {
                int invIndex = col + row * 9 + 9;
                int x = inventorySlotX(col);
                int y = inventorySlotY(row);
                boolean hovered = mouseX >= x && mouseX < x + ui(INVENTORY_SLOT_SDV) && mouseY >= y && mouseY < y + ui(INVENTORY_SLOT_SDV);
                drawInventorySlot(graphics, x, y, mc.player.getInventory().getItem(invIndex), hovered);
            }
        }

        int hotbarY = inventoryHotbarY();
        for (int col = 0; col < INVENTORY_COLS; col++) {
            int x = inventorySlotX(col);
            boolean hovered = mouseX >= x && mouseX < x + ui(INVENTORY_SLOT_SDV) && mouseY >= hotbarY && mouseY < hotbarY + ui(INVENTORY_SLOT_SDV);
            drawInventorySlot(graphics, x, hotbarY, mc.player.getInventory().getItem(col), hovered);
        }
    }

    private void drawInventorySlot(GuiGraphics graphics, int x, int y, ItemStack stack, boolean hovered) {
        StardewGuiUtil.drawMenuTileIndex(graphics, x, y, ui(INVENTORY_SLOT_SDV), ui(INVENTORY_SLOT_SDV), 10);
        if (hovered) {
            graphics.pose().pushPose();
            graphics.pose().translate(0, 0, 100);
            graphics.fill(x, y, x + ui(INVENTORY_SLOT_SDV), y + ui(INVENTORY_SLOT_SDV), 0x35FFFFFF);
            graphics.pose().popPose();
        }
        if (!stack.isEmpty()) {
            int boxW = ui(INVENTORY_SLOT_SDV);
            int mcSize = 16;
            int offsetX = (boxW - mcSize) / 2;
            int offsetY = (boxW - mcSize) / 2;

            graphics.renderItem(stack, x + offsetX, y + offsetY);
            graphics.renderItemDecorations(this.font, stack, x + offsetX, y + offsetY);
        }
    }

    private int inventoryGridWidth() {
        return ui(INVENTORY_COLS * INVENTORY_SLOT_SDV + (INVENTORY_COLS - 1) * INVENTORY_GAP_SDV);
    }

    private int inventorySlotX(int col) {
        int startX = menuX + (menuWidth - inventoryGridWidth()) / 2;
        return startX + col * ui(INVENTORY_SLOT_SDV + INVENTORY_GAP_SDV);
    }

    private int inventorySlotY(int row) {
        return menuY + ui(INVENTORY_TOP_SDV + row * (INVENTORY_SLOT_SDV + INVENTORY_GAP_SDV));
    }

    private int inventoryHotbarY() {
        return menuY + ui(INVENTORY_TOP_SDV + 3 * (INVENTORY_SLOT_SDV + INVENTORY_GAP_SDV));
    }

// unused scaling helpers removed

    private void drawScaledTexture(GuiGraphics graphics, ResourceLocation texture, int x, int y, int width, int height, float scale) {
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        graphics.pose().scale(scale, scale, 1.0f);
        graphics.blit(texture, 0, 0, width, height, 0, 0, width, height, width, height);
        graphics.pose().popPose();
    }

    private void drawRotatedScaledTexture(GuiGraphics graphics, ResourceLocation texture, int x, int y, int width, int height, float scale, float rotation, int originX, int originY, int u, int v, int texWidth, int texHeight) {
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        graphics.pose().mulPose(Axis.ZP.rotation(rotation));
        graphics.pose().scale(scale, scale, 1.0f);
        graphics.blit(texture, -originX, -originY, width, height, u, v, width, height, texWidth, texHeight);
        graphics.pose().popPose();
    }

    private int craftingGridIndexAt(double mouseX, double mouseY) {
        return craftingGridIndexAt(mouseX, mouseY, 0);
    }

    private int craftingGridIndexAt(double mouseX, double mouseY, int pad) {
        if (currentTab != 4) {
            return -1;
        }

        if (currentCraftingPage < 0 || currentCraftingPage >= craftingPages.size()) {
            return -1;
        }

        List<RecipeCell> page = craftingPages.get(currentCraftingPage);
        for (RecipeCell cellData : page) {
            int x = menuX + ui(80 + cellData.x() * 72);
            int y = menuY + ui(80 + cellData.y() * 72);
            int h = cellData.bigCraftable() ? ui(128) : ui(64);
            if (recipeContains(mouseX, mouseY, x, y, h, pad)) {
                return cellData.recipeIndex();
            }
        }

        return -1;
    }

    private int pageButtonX() {
        return menuX + ui(800);
    }

    private int pageUpButtonY() {
        return craftingGridY() - ui(16);
    }

    private int pageDownButtonY() {
        return craftingGridY() + ui(224);
    }

    private int craftingGridX() {
        return menuX + ui(80);
    }

    private int craftingGridY() {
        return menuY + ui(80);
    }

    private int inventoryStartX() {
        return menuX + ui(BORDER_WIDTH + 16);
    }

    private int inventoryStartY() {
        return menuY + ui(INVENTORY_TOP_SDV);
    }

    private boolean upButtonContains(double mouseX, double mouseY) {
        int x = pageButtonX();
        int y = pageUpButtonY();
        int w = ui(64);
        int h = ui(64);
        return mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h;
    }

    private boolean downButtonContains(double mouseX, double mouseY) {
        int x = pageButtonX();
        int y = pageDownButtonY();
        int w = ui(64);
        int h = ui(64);
        return mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h;
    }

    private boolean trashCanContains(double mouseX, double mouseY) {
        int x = menuX + menuWidth + ui(4);
        int y = menuY + menuHeight - ui(360);
        int w = ui(64);
        int h = ui(104);
        return mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h;
    }

    private boolean recipeContains(double mouseX, double mouseY, int x, int y, int h, int pad) {
        return mouseX >= x - pad && mouseX < x + ui(64) + pad && mouseY >= y - pad && mouseY < y + h + pad;
    }

    private boolean isCraftableClient(int recipeIndex) {
        if (recipeIndex < 0 || recipeIndex >= craftingRecipeIds.size()) {
            return false;
        }
        return computeMaxCraftsClient(getRecipeIngredients(craftingRecipeIds.get(recipeIndex)), 1) > 0;
    }

    private float stepScale(float current, float target, float step) {
        if (current < target) {
            return Math.min(current + step, target);
        }
        return Math.max(current - step, target);
    }

    private void submitCraftRequest(int requestedCount) {
        if (selectedCraftingIndex < 0 || selectedCraftingIndex >= craftingRecipeIds.size()) {
            return;
        }
        String recipeId = craftingRecipeIds.get(selectedCraftingIndex);
        PacketDistributor.sendToServer(new CraftingMenuCraftSubmitPayload(recipeId, requestedCount));
    }

    private void submitInventoryClickRequest(int slotIndex, boolean rightClick) {
        PacketDistributor.sendToServer(new CraftingMenuInventoryActionPayload(CraftingMenuInventoryActionPayload.ACTION_CLICK_SLOT, slotIndex, rightClick));
    }

    private void submitTrashCarriedRequest() {
        PacketDistributor.sendToServer(new CraftingMenuInventoryActionPayload(CraftingMenuInventoryActionPayload.ACTION_TRASH_CARRIED, -1, false));
    }

    private void submitDropCarriedRequest() {
        PacketDistributor.sendToServer(new CraftingMenuInventoryActionPayload(CraftingMenuInventoryActionPayload.ACTION_DROP_CARRIED, -1, false));
    }

    private ItemStack currentCarriedItem() {
        if (this.minecraft == null || this.minecraft.player == null || this.minecraft.player.containerMenu == null) {
            return ItemStack.EMPTY;
        }
        ItemStack carried = this.minecraft.player.containerMenu.getCarried();
        return carried == null ? ItemStack.EMPTY : carried;
    }

    private boolean hasCarriedItem() {
        return !currentCarriedItem().isEmpty();
    }

    private boolean pointInsideMainMenu(double mouseX, double mouseY) {
        return mouseX >= menuX && mouseX < menuX + menuWidth && mouseY >= menuY && mouseY < menuY + menuHeight;
    }

    private int vanillaLikeCraftAmountOnLeftClick() {
        if (hasShiftDown()) {
            if (hasControlDown()) {
                return 25;
            }
            return 5;
        }
        return 1;
    }

    private List<Ingredient> getRecipeIngredients(String recipePath) {
        return StardewCraftingRecipeData.toExpandedIngredients(recipePath);
    }

    private List<RecipeRequirement> getRecipeRequirements(String recipePath) {
        List<Ingredient> ingredients = getRecipeIngredients(recipePath);
        if (ingredients.isEmpty()) {
            return List.of();
        }

        Map<String, RecipeRequirement> grouped = new LinkedHashMap<>();
        for (Ingredient ingredient : ingredients) {
            String key = ingredientSignature(ingredient);
            RecipeRequirement existing = grouped.get(key);
            if (existing == null) {
                grouped.put(key, new RecipeRequirement(ingredient, resolveIngredientIcon(ingredient), 1));
            } else {
                grouped.put(key, new RecipeRequirement(existing.ingredient(), existing.icon(), existing.need() + 1));
            }
        }
        return new ArrayList<>(grouped.values());
    }

    private String ingredientSignature(Ingredient ingredient) {
        if (ingredient == null || ingredient.isEmpty()) {
            return "empty";
        }

        List<String> keys = new ArrayList<>();
        for (ItemStack stack : ingredient.getItems()) {
            if (stack.isEmpty()) {
                continue;
            }
            keys.add(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
        }
        keys.sort(String::compareTo);
        return String.join("|", keys);
    }

    private ItemStack resolveIngredientIcon(Ingredient ingredient) {
        if (ingredient == null || ingredient.isEmpty()) {
            return new ItemStack(Items.BARRIER);
        }
        ItemStack[] options = ingredient.getItems();
        if (options.length > 0 && !options[0].isEmpty()) {
            return options[0];
        }
        return new ItemStack(Items.BARRIER);
    }

    private int countMatchingClient(Ingredient ingredient) {
        if (this.minecraft == null || this.minecraft.player == null || ingredient == null || ingredient.isEmpty()) {
            return 0;
        }
        int total = 0;
        for (ItemStack stack : this.minecraft.player.getInventory().items) {
            if (!stack.isEmpty() && ingredient.test(stack)) {
                total += stack.getCount();
            }
        }
        return total;
    }

    private int computeMaxCraftsClient(List<Ingredient> ingredients, int cap) {
        if (ingredients == null || ingredients.isEmpty() || this.minecraft == null || this.minecraft.player == null) {
            return 0;
        }

        List<ItemStack> stacks = this.minecraft.player.getInventory().items;
        int[] remain = new int[stacks.size()];
        for (int i = 0; i < stacks.size(); i++) {
            remain[i] = stacks.get(i).getCount();
        }

        int crafted = 0;
        while (crafted < cap && tryConsumeOneCraftClient(remain, stacks, ingredients)) {
            crafted++;
        }
        return crafted;
    }

    private boolean tryConsumeOneCraftClient(int[] remain, List<ItemStack> stacks, List<Ingredient> ingredients) {
        for (Ingredient ingredient : ingredients) {
            int chosen = -1;
            for (int slot = 0; slot < stacks.size(); slot++) {
                if (remain[slot] <= 0) {
                    continue;
                }
                ItemStack stack = stacks.get(slot);
                if (!stack.isEmpty() && ingredient.test(stack)) {
                    chosen = slot;
                    break;
                }
            }

            if (chosen < 0) {
                return false;
            }

            remain[chosen]--;
        }
        return true;
    }

    private void drawCraftingTooltips(GuiGraphics graphics, int mouseX, int mouseY) {
        int invSlot = hoveredInventorySlot(mouseX, mouseY);
        if (invSlot >= 0 && this.minecraft != null && this.minecraft.player != null) {
            ItemStack stack = this.minecraft.player.getInventory().getItem(invSlot);
            if (!stack.isEmpty()) {
                graphics.renderTooltip(this.font, stack, mouseX, mouseY);
                return;
            }
        }

        if (hoveredCraftingIndex >= 0 && hoveredCraftingIndex < craftingRecipeStacks.size()) {
            String recipeId = craftingRecipeIds.get(hoveredCraftingIndex);
            boolean unlocked = ClientPlayerDataCache.hasRecipe(recipeId);

            if (!unlocked) {
                graphics.renderTooltip(this.font, Component.literal("???").withStyle(ChatFormatting.BOLD), mouseX, mouseY);
                return;
            }

            ItemStack output = craftingRecipeStacks.get(hoveredCraftingIndex);
            List<RecipeRequirement> requirements = getRecipeRequirements(recipeId);
            List<Component> lines = new ArrayList<>();
            lines.add(Component.literal("需要").withStyle(ChatFormatting.GOLD));
            for (RecipeRequirement requirement : requirements) {
                int have = countMatchingClient(requirement.ingredient());
                int need = requirement.need();
                ChatFormatting countColor = have >= need ? ChatFormatting.GREEN : ChatFormatting.RED;
                Component line = Component.literal((have >= need ? "✔   " : "✖   "))
                        .append(Component.literal("   ")) // Extra space for icon
                        .append(requirement.icon().getHoverName().copy().withStyle(ChatFormatting.GRAY))
                        .append(Component.literal(" x" + need).withStyle(countColor));
                lines.add(line);
            }
            lines.add(Component.literal(" "));
            if (this.minecraft != null && this.minecraft.player != null) {
                Item.TooltipContext context = Item.TooltipContext.of(this.minecraft.level);
                TooltipFlag flag = this.minecraft.options.advancedItemTooltips ? TooltipFlag.ADVANCED : TooltipFlag.NORMAL;
                lines.addAll(output.getTooltipLines(context, this.minecraft.player, flag));
            } else {
                lines.add(output.getHoverName().copy());
            }
            graphics.renderTooltip(this.font, lines, java.util.Optional.empty(), mouseX, mouseY);

            // Now draw the icons on top!
            // Tooltip box calculation based on DefaultTooltipPositioner:
            int boxX = mouseX + 12;
            int boxY = mouseY - 12;
            int tooltipWidth = 0;
            for (Component line : lines) {
                int w = this.font.width(line);
                if (w > tooltipWidth) tooltipWidth = w;
            }
            int tooltipHeight = lines.size() * 10;
            if (boxX + tooltipWidth > this.width) {
                boxX = mouseX - 16 - tooltipWidth;
            }
            if (boxY + tooltipHeight + 24 > this.height) {
                boxY = this.height - tooltipHeight - 24;
            }
            if (boxY < 0) {
                boxY = 0;
            }
            
            for (int i = 0; i < requirements.size(); i++) {
                RecipeRequirement req = requirements.get(i);
                float s = 0.65f; // Similar height to text
                int iconW = Math.round(16 * s);
                int iconX = boxX + this.font.width("✔ ");
                int iconY = boxY + 10 * (i + 1) + (10 - iconW) / 2 - 1; // Center with the line
                graphics.pose().pushPose();
                graphics.pose().translate(iconX, iconY, 800.0f);
                graphics.pose().scale(s, s, 1.0f);
                graphics.renderItem(req.icon(), 0, 0);
                graphics.pose().popPose();
            }
            
            return;
        }

        if (trashCanContains(mouseX, mouseY)) {
            ItemStack carried = currentCarriedItem();
            if (!carried.isEmpty()) {
                List<Component> lines = new ArrayList<>();
                lines.add(Component.translatable("stardewcraft.game_menu.crafting.trash_can").withStyle(ChatFormatting.WHITE));
                lines.add(carried.getHoverName().copy().withStyle(ChatFormatting.GRAY));
                graphics.renderTooltip(this.font, lines, java.util.Optional.empty(), mouseX, mouseY);
                return;
            }
            graphics.renderTooltip(this.font, Component.translatable("stardewcraft.game_menu.crafting.trash_can"), mouseX, mouseY);
        }
    }

    private void updateCraftingHoverState(int mouseX, int mouseY) {
        hoveredCraftingIndex = -1;

        int hoverGridIndex = craftingGridIndexAt(mouseX, mouseY, ui(4));
        if (hoverGridIndex >= 0 && hoverGridIndex < craftingRecipeIds.size()) {
            hoveredCraftingIndex = hoverGridIndex;
        }
    }

    private int hoveredInventorySlot(double mouseX, double mouseY) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < INVENTORY_COLS; col++) {
                int x = inventorySlotX(col);
                int y = inventorySlotY(row);
                if (mouseX >= x && mouseX < x + ui(INVENTORY_SLOT_SDV) && mouseY >= y && mouseY < y + ui(INVENTORY_SLOT_SDV)) {
                    return col + row * 9 + 9;
                }
            }
        }

        int hotbarY = inventoryHotbarY();
        for (int col = 0; col < INVENTORY_COLS; col++) {
            int x = inventorySlotX(col);
            if (mouseX >= x && mouseX < x + ui(INVENTORY_SLOT_SDV) && mouseY >= hotbarY && mouseY < hotbarY + ui(INVENTORY_SLOT_SDV)) {
                return col;
            }
        }

        return -1;
    }

    private void closeWithSound() {
        if (hasCarriedItem()) {
            playUiSound(ModSounds.CANCEL.get(), 1.0f, 1.0f);
            return;
        }
        playUiSound(ModSounds.BIG_DESELECT.get(), 1.0f, 1.0f);
        if (this.minecraft != null) {
            this.minecraft.setScreen(null);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (closeContains(mouseX, mouseY)) {
                closeWithSound();
                return true;
            }
            for (int i = 0; i < TAB_COUNT; i++) {
                if (tabContains(i, mouseX, mouseY)) {
                    if (currentTab != i) {
                        currentTab = i;
                        playUiSound(ModSounds.SMALL_SELECT.get(), 1.0f, 1.0f);
                    }
                    return true;
                }
            }

            if (currentTab == 4) {
                int invSlot = hoveredInventorySlot(mouseX, mouseY);
                if (invSlot >= 0) {
                    submitInventoryClickRequest(invSlot, false);
                    playUiSound(ModSounds.SMALL_SELECT.get(), 1.0f, 1.0f);
                    return true;
                }
            }

            if (currentTab == 4 && upButtonContains(mouseX, mouseY) && currentCraftingPage > 0) {
                currentCraftingPage--;
                clampCraftingPage();
                playUiSound(ModSounds.SHWIP.get(), 1.0f, 1.0f);
                return true;
            }

            if (currentTab == 4 && downButtonContains(mouseX, mouseY) && currentCraftingPage < Math.max(0, craftingPages.size() - 1)) {
                currentCraftingPage++;
                clampCraftingPage();
                playUiSound(ModSounds.SHWIP.get(), 1.0f, 1.0f);
                return true;
            }

            if (currentTab == 4 && trashCanContains(mouseX, mouseY) && hasCarriedItem()) {
                submitTrashCarriedRequest();
                playUiSound(ModSounds.THROW_DOWN_ITEM.get(), 1.0f, 1.0f);
                return true;
            }

            if (currentTab == 4 && !pointInsideMainMenu(mouseX, mouseY) && hasCarriedItem()) {
                submitDropCarriedRequest();
                playUiSound(ModSounds.THROW_DOWN_ITEM.get(), 1.0f, 1.0f);
                return true;
            }

            if (!pointInsideMainMenu(mouseX, mouseY)) {
                return super.mouseClicked(mouseX, mouseY, button);
            }

            int gridIndex = craftingGridIndexAt(mouseX, mouseY, ui(4));
            if (gridIndex >= 0 && gridIndex < craftingRecipeIds.size()) {
                if (selectedCraftingIndex != gridIndex) {
                    selectedCraftingIndex = gridIndex;
                }

                if (currentTab == 4 && isCraftableClient(gridIndex)) {
                    submitCraftRequest(vanillaLikeCraftAmountOnLeftClick());
                    playUiSound(ModSounds.COIN.get(), 1.0f, 1.0f);
                }
                return true;
            }
        }

        if (button == 1 && currentTab == 4) {
            int invSlot = hoveredInventorySlot(mouseX, mouseY);
            if (invSlot >= 0) {
                submitInventoryClickRequest(invSlot, true);
                playUiSound(ModSounds.SMALL_SELECT.get(), 1.0f, 1.0f);
                return true;
            }

            int gridIndex = craftingGridIndexAt(mouseX, mouseY, ui(4));
            if (gridIndex >= 0 && gridIndex < craftingRecipeIds.size()) {
                selectedCraftingIndex = gridIndex;
                if (isCraftableClient(gridIndex)) {
                    submitCraftRequest(1);
                    playUiSound(ModSounds.COIN.get(), 1.0f, 1.0f);
                }
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (currentTab != 4 || craftingRecipeIds.isEmpty()) {
            return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        }

        int oldPage = currentCraftingPage;
        if (scrollY > 0) {
            currentCraftingPage--;
        } else if (scrollY < 0) {
            currentCraftingPage++;
        }
        clampCraftingPage();

        if (oldPage != currentCraftingPage) {
            playUiSound(ModSounds.SHWIP.get(), 1.0f, 1.0f);
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            closeWithSound();
            return true;
        }
        if (keyCode == 265 && currentTab == 4) {
            int old = currentCraftingPage;
            currentCraftingPage--;
            clampCraftingPage();
            if (old != currentCraftingPage) {
                playUiSound(ModSounds.SHWIP.get(), 1.0f, 1.0f);
                return true;
            }
        }
        if (keyCode == 264 && currentTab == 4) {
            int old = currentCraftingPage;
            currentCraftingPage++;
            clampCraftingPage();
            if (old != currentCraftingPage) {
                playUiSound(ModSounds.SHWIP.get(), 1.0f, 1.0f);
                return true;
            }
        }

        if (keyCode == 261 && currentTab == 4 && hasCarriedItem()) {
            submitTrashCarriedRequest();
            playUiSound(ModSounds.THROW_DOWN_ITEM.get(), 1.0f, 1.0f);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void tick() {
        super.tick();
        Minecraft mc = this.minecraft;
        if (mc != null && mc.player != null && currentTab == 4) {
            rebuildCraftingEntries();
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
