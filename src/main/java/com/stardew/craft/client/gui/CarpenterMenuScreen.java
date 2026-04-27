package com.stardew.craft.client.gui;

import com.stardew.craft.client.gui.overnight.StardewGuiUtil;
import com.stardew.craft.network.payload.CarpenterPurchasePayload;
import com.stardew.craft.network.payload.CarpenterPurchaseResultPayload;
import com.stardew.craft.network.payload.OpenCarpenterMenuPayload;
import com.stardew.craft.shop.CarpenterBlueprint;
import com.stardew.craft.sound.ModSounds;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

/**
 * Client-side screen replicating SDV's CarpenterMenu.
 * Displays building blueprints with preview, description, cost, materials, and build time.
 * Instead of on-farm placement, purchasing gives the player a manager item.
 */
@OnlyIn(Dist.CLIENT)
@SuppressWarnings({"null", "unused"})
public class CarpenterMenuScreen extends Screen {

    // =========================================================================
    // SDV layout constants (in SDV screen pixels = sprite_px × 4)
    // =========================================================================
    private static final int MAX_VIEWER_W = 448;
    private static final int MAX_VIEWER_H = 512;
    private static final int MAX_DESC_W   = 416;
    // SDV IClickableMenu exact values (from IClickableMenu.cs)
    private static final int SPACE_SIDE   = 16;
    private static final int SPACE_TOP    = 96;
    private static final int BORDER_W     = 40;

    // Cursors UV coords (sprite pixels, NOT ×4)
    // OK button (green checkmark)
    private static final int OK_U = 366, OK_V = 373, OK_W = 16, OK_H = 16;
    // Cancel button (red X) - standard tile 47: column 47%4=3, row 47/4=11 → u=192, v=704 in 64×64 tiles
    // Actually SDV uses getSourceRectForStandardTileSheet(mouseCursors, 47) which maps to:
    // tile 47 at the mouseCursors spritesheet. The standard tile sheet has 16-wide tiles.
    // SDV: tileX = 47 % (mouseCursors.Width / tileSize) where tileSize=16
    // mouseCursors width = 704, so cols = 704/16 = 44
    // 47 % 44 = 3, 47 / 44 = 1 → u = 3*16 = 48, v = 1*16 = 16? No...
    // Actually SDV getSourceRectForStandardTileSheet default tileSize=64:
    // cols = 704 / 64 = 11; row = 47/11=4, col = 47%11=3 → u=192, v=256
    private static final int CANCEL_U = 192, CANCEL_V = 256, CANCEL_W = 64, CANCEL_H = 64;
    // Back arrow
    private static final int BACK_U = 352, BACK_V = 495, BACK_W = 12, BACK_H = 11;
    // Forward arrow
    private static final int FWD_U = 365, FWD_V = 495, FWD_W = 12, FWD_H = 11;
    // Upgrade icon
    private static final int UPG_U = 402, UPG_V = 328, UPG_W = 9, UPG_H = 13;
    // Gold coin icon (from cursors_1_6)
    private static final int GOLD_U = 241, GOLD_V = 303, GOLD_W = 14, GOLD_H = 13;
    // Texture box border (from IClickableMenu.drawTextureBox)
    private static final int BOX_U = 384, BOX_V = 373, BOX_W = 18, BOX_H = 18;
    // Scroll banner background (SpriteText scroll: 325,318,11,18 in mouseCursors)
    private static final int SCROLL_U = 325, SCROLL_V = 318, SCROLL_W = 11, SCROLL_H = 18;

    // =========================================================================
    // Colors
    // =========================================================================
    private static final int BG_TINT          = 0x99000000;
    private static final int TEXT_COLOR        = 0x5C2B00;   // SDV Game1.textColor (brown)
    private static final int TEXT_COLOR_SHADOW = 0x404040;
    private static final int RED_COLOR         = 0xFF0000;   // SDV Color.Red

    // =========================================================================
    // State
    // =========================================================================
    private final List<CarpenterBlueprint> blueprints;
    private int playerMoney;
    private int currentIndex = 0;
    private boolean purchasePending = false;

    // Layout (MC gui coords, recalculated on resize)
    private float guiScale;
    private int menuX, menuY, menuW, menuH;
    private int viewerX, viewerY, viewerW, viewerH;
    private int descX, descY, descW, descH;
    private int okX, okY, okW, okH;
    private int cancelX, cancelY, cancelW, cancelH;
    private int backX, backY, backW, backH;
    private int fwdX, fwdY, fwdW, fwdH;

    public CarpenterMenuScreen(OpenCarpenterMenuPayload payload) {
        super(Component.literal("Carpenter Menu"));
        this.blueprints = payload.blueprints();
        this.playerMoney = payload.playerMoney();
    }

    // =========================================================================
    // Coordinate helpers (same pattern as ShopScreen)
    // =========================================================================
    private int ui(int sdvPx) {
        return Math.round(sdvPx / guiScale);
    }

    private float s4() {
        return 4.0f / guiScale;
    }

    // =========================================================================
    // Lifecycle
    // =========================================================================
    @Override
    protected void init() {
        super.init();
        guiScale = (float) minecraft.getWindow().getGuiScale();
        recalcLayout();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void renderBackground(@javax.annotation.Nonnull GuiGraphics g, int mouseX, int mouseY, float pt) {
        // suppress default MC background — SDV draws its own dim overlay in render()
    }

    private void recalcLayout() {
        int sdvMenuW = MAX_VIEWER_W + MAX_DESC_W + SPACE_SIDE * 2 + 64;
        int sdvMenuH = MAX_VIEWER_H + SPACE_TOP;

        menuW = ui(sdvMenuW);
        menuH = ui(sdvMenuH);
        menuX = (width - menuW) / 2;
        menuY = (height - menuH) / 2;

        // SDV: xPositionOnScreen = viewport.Width/2 - maxWidthOfBuildingViewer - spaceToClearSideBorder
        // We center the whole menu, so adjust viewer relative to menu origin
        int sdvXPos = width / 2 - ui(MAX_VIEWER_W) - ui(SPACE_SIDE);
        int sdvYPos = height / 2 - ui(MAX_VIEWER_H) / 2 - ui(SPACE_TOP) + ui(32);

        // Building viewer box (inflated by 96 left, 16 top, etc.)
        viewerX = sdvXPos - ui(96);
        viewerY = sdvYPos - ui(16);
        viewerW = ui(MAX_VIEWER_W + 64);
        viewerH = ui(MAX_VIEWER_H + 64);

        // Description box
        descX = sdvXPos + ui(MAX_VIEWER_W) - ui(16);
        descY = sdvYPos + ui(80);
        descW = ui(MAX_DESC_W + 64);
        descH = ui(MAX_VIEWER_H - 32);

        // Back button (SDV: xPositionOnScreen + 64, yPositionOnScreen + maxHeightOfBuildingViewer + 64)
        float s4 = s4();
        backX = sdvXPos + ui(64);
        backY = sdvYPos + ui(MAX_VIEWER_H + 64);
        backW = (int)(BACK_W * s4);
        backH = (int)(BACK_H * s4);

        // Forward button (SDV: xPositionOnScreen + maxWidthOfBuildingViewer - 256 + 16)
        fwdX = sdvXPos + ui(MAX_VIEWER_W - 256 + 16);
        fwdY = sdvYPos + ui(MAX_VIEWER_H + 64);
        fwdW = (int)(FWD_W * s4);
        fwdH = (int)(FWD_H * s4);

        // OK button (SDV: xPositionOnScreen + width - borderWidth - spaceToClearSideBorder - 192 - 12)
        int sdvTotalW = ui(sdvMenuW);
        okX = sdvXPos + sdvTotalW - ui(BORDER_W) - ui(SPACE_SIDE) - ui(192) - ui(12);
        okY = sdvYPos + ui(MAX_VIEWER_H + 64);
        okW = (int)(OK_W * s4);
        okH = (int)(OK_H * s4);

        // Cancel button (SDV: xPositionOnScreen + width - borderWidth - spaceToClearSideBorder - 64)
        cancelX = sdvXPos + sdvTotalW - ui(BORDER_W) - ui(SPACE_SIDE) - ui(64);
        cancelY = sdvYPos + ui(MAX_VIEWER_H + 64);
        cancelW = ui(64);
        cancelH = ui(64);
    }

    // =========================================================================
    // Input
    // =========================================================================
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);
        int mx = (int) mouseX, my = (int) mouseY;

        // Back button
        if (isInside(mx, my, backX, backY, backW, backH)) {
            if (currentIndex > 0) {
                currentIndex--;
                playSound(ModSounds.SHWIP.get());
            }
            return true;
        }

        // Forward button
        if (isInside(mx, my, fwdX, fwdY, fwdW, fwdH)) {
            if (currentIndex < blueprints.size() - 1) {
                currentIndex++;
                playSound(ModSounds.SHWIP.get());
            }
            return true;
        }

        // OK button (purchase)
        if (isInside(mx, my, okX, okY, okW, okH)) {
            tryPurchase();
            return true;
        }

        // Cancel button (close)
        if (isInside(mx, my, cancelX, cancelY, cancelW, cancelH)) {
            playSound(ModSounds.CANCEL.get());
            onClose();
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) { // Escape
            playSound(ModSounds.CANCEL.get());
            onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private boolean isInside(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    // =========================================================================
    // Purchase logic
    // =========================================================================
    private void tryPurchase() {
        if (purchasePending || blueprints.isEmpty()) return;

        CarpenterBlueprint bp = blueprints.get(currentIndex);
        if (!canBuildCurrent()) {
            playSound(ModSounds.CANCEL.get());
            return;
        }

        purchasePending = true;
        playSound(ModSounds.PURCHASE_CLICK.get());
        PacketDistributor.sendToServer(new CarpenterPurchasePayload(currentIndex));
    }

    private boolean canBuildCurrent() {
        if (blueprints.isEmpty()) return false;
        CarpenterBlueprint bp = blueprints.get(currentIndex);

        // Check money
        if (bp.cost() > 0 && playerMoney < bp.cost()) return false;

        // Check materials
        if (minecraft == null || minecraft.player == null) return false;
        for (CarpenterBlueprint.MaterialEntry mat : bp.materials()) {
            try {
                ResourceLocation matId = ResourceLocation.parse(mat.itemId());
                Item matItem = BuiltInRegistries.ITEM.get(matId);
                if (matItem == null || matItem == Items.AIR) return false;
                if (minecraft.player.getInventory().countItem(matItem) < mat.count()) return false;
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    public void onPurchaseResult(CarpenterPurchaseResultPayload result) {
        purchasePending = false;
        playerMoney = result.newMoney();

        if (result.success()) {
            playSound(ModSounds.COIN.get());
        } else {
            playSound(ModSounds.CANCEL.get());
        }
    }

    // =========================================================================
    // Rendering
    // =========================================================================
    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        if (blueprints.isEmpty()) return;

        float s4 = s4();
        CarpenterBlueprint bp = blueprints.get(currentIndex);

        // 1. Dim background
        g.fill(0, 0, width, height, BG_TINT);

        // 2. SDV base.draw(b) only draws background dimming (done above) + close button.
        //    NO encompassing frame - CarpenterMenu only has viewer box + description box.
        int sdvXPos = width / 2 - ui(MAX_VIEWER_W) - ui(SPACE_SIDE);
        int sdvYPos = height / 2 - ui(MAX_VIEWER_H) / 2 - ui(SPACE_TOP) + ui(32);
        int sdvTotalW = ui(MAX_VIEWER_W + MAX_DESC_W + SPACE_SIDE * 2 + 64);

        // 3. Building viewer box — SDV uses default drawTextureBox (menuTexture)
        StardewGuiUtil.drawTextureBox(g, viewerX, viewerY, viewerW, viewerH);

        // 4. Manager block preview (centered in viewer)
        drawManagerPreview(g, bp);

        // 5. Upgrade icon (if applicable)
        if (bp.isUpgrade()) {
            int upgX = sdvXPos + ui(MAX_VIEWER_W - 128 + 32);
            int upgY = sdvYPos + ui(8);
            StardewGuiUtil.drawFromCursors(g, upgX, upgY, UPG_U, UPG_V, UPG_W, UPG_H, s4);
        }

        // 6. Building name with scroll banner
        drawNameWithScroll(g, bp, sdvXPos, sdvYPos, s4);

        // 7. Description box — SDV uses default drawTextureBox (menuTexture)
        StardewGuiUtil.drawTextureBox(g, descX, descY, descW, descH);

        // 8. Description text
        drawDescription(g, bp, sdvXPos, sdvYPos, s4);

        // 9. Price display with gold icon
        drawPrice(g, bp, sdvXPos, sdvYPos, s4);

        // 10. Materials list
        drawMaterials(g, bp, sdvXPos, sdvYPos, s4);

        // 12. Navigation buttons
        drawButton(g, mouseX, mouseY, backX, backY, BACK_U, BACK_V, BACK_W, BACK_H, s4, currentIndex > 0);
        drawButton(g, mouseX, mouseY, fwdX, fwdY, FWD_U, FWD_V, FWD_W, FWD_H, s4, currentIndex < blueprints.size() - 1);

        // 13. OK button (tinted gray if can't build)
        boolean canBuild = canBuildCurrent();
        if (!canBuild) {
            g.setColor(0.5f, 0.5f, 0.5f, 0.8f);
        }
        StardewGuiUtil.drawFromCursors(g, okX, okY, OK_U, OK_V, OK_W, OK_H, s4);
        if (!canBuild) {
            g.setColor(1f, 1f, 1f, 1f);
        }

        // 14. Cancel button (drawn from menu_tiles, standard tile 47)
        // SDV uses getSourceRectForStandardTileSheet(mouseCursors, 47) with tileSize=64
        // This is at u=192, v=256 in cursors, drawn at scale 1 (already 64×64)
        // But actually SDV draws it at scale 1f (not 4f like other buttons)
        float cancelScale = 1.0f / guiScale;
        StardewGuiUtil.drawFromCursors(g, cancelX, cancelY, CANCEL_U, CANCEL_V, CANCEL_W, CANCEL_H, cancelScale);

        // 15. Tooltip / hover text
        drawTooltip(g, mouseX, mouseY);
    }

    private void drawManagerPreview(GuiGraphics g, CarpenterBlueprint bp) {
        // Render the manager block item centered in the viewer area
        ResourceLocation itemRL = ResourceLocation.parse(bp.resultItemId());
        Item item = BuiltInRegistries.ITEM.get(itemRL);
        if (item == Items.AIR) return;

        ItemStack stack = new ItemStack(item);

        // Render at 4× default item size (16px → 64px equivalent), centered in viewer
        float itemScale = 4.0f;
        int renderSize = (int)(16 * itemScale);
        int centerX = viewerX + viewerW / 2;
        int centerY = viewerY + viewerH / 2;
        int imgX = centerX - renderSize / 2;
        int imgY = centerY - renderSize / 2;

        g.pose().pushPose();
        g.pose().translate(imgX, imgY, 0);
        g.pose().scale(itemScale, itemScale, 1.0f);
        g.renderItem(stack, 0, 0);
        g.pose().popPose();
    }

    private void drawNameWithScroll(GuiGraphics g, CarpenterBlueprint bp, int sdvXPos, int sdvYPos, float s4) {
        Component name = bp.displayName();
        // SDV exact formula:
        // x = xPos + viewerW - spaceToClearSideBorder - 16 + 64
        //     + (width - (viewerW + 128)) / 2
        // y = yPositionOnScreen
        int sdvTotalW = ui(MAX_VIEWER_W + MAX_DESC_W + SPACE_SIDE * 2 + 64);
        int nameCenterX = sdvXPos + ui(MAX_VIEWER_W - SPACE_SIDE - 16 + 64)
            + (sdvTotalW - ui(MAX_VIEWER_W + 128)) / 2;

        int nameWidth = font.width(name);
        int scrollPadding = ui(32);
        int scrollWidth = Math.max(nameWidth + scrollPadding * 2, ui(416));
        int scrollHeight = ui(56);

        int scrollX = nameCenterX - scrollWidth / 2;
        int scrollY = sdvYPos;  // SDV exact: yPositionOnScreen

        // Draw scroll background using cursors texture (325, 318, 11, 18)
        StardewGuiUtil.drawTextureBox(g,
            StardewGuiUtil.CURSORS, StardewGuiUtil.CURSORS_WIDTH, StardewGuiUtil.CURSORS_HEIGHT,
            SCROLL_U, SCROLL_V, SCROLL_W, SCROLL_H,
            scrollX, scrollY, scrollWidth, scrollHeight,
            s4, false);

        // Draw name text centered on the scroll
        int textX = nameCenterX - nameWidth / 2;
        int textY = scrollY + (scrollHeight - font.lineHeight) / 2;
        g.drawString(font, name, textX, textY, TEXT_COLOR, false);
    }

    private void drawDescription(GuiGraphics g, CarpenterBlueprint bp, int sdvXPos, int sdvYPos, float s4) {
        // SDV exact: text at (xPositionOnScreen + maxWidthOfBuildingViewer, yPositionOnScreen + 80 + 16)
        int textX = sdvXPos + ui(MAX_VIEWER_W);
        int textY = sdvYPos + ui(80 + 16);
        int maxWidth = descW - ui(32);
        int maxBottom = sdvYPos + ui(256 + 32) - ui(12);

        // Wrap against the actual box width and stop before the price/material area.
        List<FormattedCharSequence> lines = font.split(bp.description(), maxWidth);
        int maxLines = Math.max(1, (maxBottom - textY) / (font.lineHeight + 2));
        boolean truncated = lines.size() > maxLines;
        int drawnLines = Math.min(lines.size(), maxLines);
        for (int index = 0; index < drawnLines; index++) {
            FormattedCharSequence line = lines.get(index);
            if (truncated && index == drawnLines - 1) {
                String clipped = font.plainSubstrByWidth(bp.description().getString(), Math.max(0, maxWidth - font.width("...")));
                line = font.split(Component.literal(clipped + "..."), maxWidth).get(0);
            }
            g.drawString(font, line, textX, textY, TEXT_COLOR, false);
            textY += font.lineHeight + 2;
        }
    }

    private void drawPrice(GuiGraphics g, CarpenterBlueprint bp, int sdvXPos, int sdvYPos, float s4) {
        // SDV: ingredientsPosition = (xPositionOnScreen + maxWidthOfBuildingViewer + 16, yPositionOnScreen + 256 + 32)
        int ingX = sdvXPos + ui(MAX_VIEWER_W + 16);
        int ingY = sdvYPos + ui(256 + 32);

        if (bp.cost() >= 0) {
            // Gold icon (from cursors_1_6)
            int goldX = ingX - ui(8);
            int goldY = ingY - ui(4);
            StardewGuiUtil.drawFromCursors16(g, goldX, goldY, GOLD_U, GOLD_V, GOLD_W, GOLD_H, s4);

            // Price text — SDV: (ingredientsPosition.X + 64, ingredientsPosition.Y + 8)
            String priceStr = formatNumber(bp.cost());
            int priceColor = (playerMoney < bp.cost()) ? RED_COLOR : TEXT_COLOR;
            int textX = ingX + ui(64);
            int textY = ingY + ui(8);
            // Draw with shadow (SDV: Utility.drawTextWithShadow)
            g.drawString(font, priceStr, textX + 1, textY + 1, 0x000000, false);
            g.drawString(font, priceStr, textX, textY, priceColor, false);
        }
    }

    private void drawMaterials(GuiGraphics g, CarpenterBlueprint bp, int sdvXPos, int sdvYPos, float s4) {
        // SDV: ingredientsPosition starts at (xPositionOnScreen + maxWidthOfBuildingViewer + 16, yPositionOnScreen + 256 + 32)
        // then adjusted: X -= 16, Y -= 21
        // Each material: Y += 68, draw icon at ingredientsPosition, text at ingredientsPosition.X + 64 + 16, Y + 20
        int baseX = sdvXPos + ui(MAX_VIEWER_W + 16) - ui(16);
        int baseY = sdvYPos + ui(256 + 32) - ui(21);

        for (CarpenterBlueprint.MaterialEntry mat : bp.materials()) {
            baseY += ui(68);

            // Draw item icon
            try {
                ResourceLocation matId = ResourceLocation.parse(mat.itemId());
                Item matItem = BuiltInRegistries.ITEM.get(matId);
                if (matItem != null && matItem != Items.AIR) {
                    ItemStack stack = new ItemStack(matItem, mat.count());
                    // SDV drawInMenu at scale 1f = 64×64 screen pixels = ui(64) GUI pixels
                    // MC renderItem draws 16×16, so scale = ui(64)/16 = 4/guiScale = s4
                    g.pose().pushPose();
                    g.pose().translate(baseX, baseY, 0);
                    g.pose().scale(s4, s4, 1.0f);
                    g.renderItem(stack, 0, 0);
                    g.pose().popPose();

                    // Material name + count
                    boolean hasEnough = minecraft != null && minecraft.player != null
                        && minecraft.player.getInventory().countItem(matItem) >= mat.count();
                    int textColor = hasEnough ? TEXT_COLOR : RED_COLOR;
                    String materialText = stack.getHoverName().getString() + " (" + mat.count() + ")";
                    int textX = baseX + ui(64 + 16);
                    int textY = baseY + ui(20);
                    // Shadow
                    g.drawString(font, materialText, textX + 1, textY + 1, 0x000000, false);
                    g.drawString(font, materialText, textX, textY, textColor, false);
                }
            } catch (Exception ignored) {}
        }
    }

    private void drawButton(GuiGraphics g, int mouseX, int mouseY, int x, int y,
                            int u, int v, int w, int h, float s4, boolean enabled) {
        if (!enabled) {
            g.setColor(0.5f, 0.5f, 0.5f, 0.5f);
        } else if (isInside(mouseX, mouseY, x, y, (int)(w * s4), (int)(h * s4))) {
            // SDV hover: slight brightness increase
            g.setColor(1.0f, 1.0f, 0.8f, 1.0f);
        }
        StardewGuiUtil.drawFromCursors(g, x, y, u, v, w, h, s4);
        g.setColor(1f, 1f, 1f, 1f);
    }

    private void drawTooltip(GuiGraphics g, int mouseX, int mouseY) {
        // Show tooltip for OK button
        if (isInside(mouseX, mouseY, okX, okY, okW, okH)) {
            CarpenterBlueprint bp = blueprints.get(currentIndex);
            Component tip = canBuildCurrent()
                ? Component.translatable("stardewcraft.carpenter.tooltip.build", bp.displayName())
                : Component.translatable("stardewcraft.carpenter.tooltip.insufficient_resources");
            g.renderTooltip(font, tip, mouseX, mouseY);
        }
        // Cancel button tooltip
        if (isInside(mouseX, mouseY, cancelX, cancelY, cancelW, cancelH)) {
            g.renderTooltip(font, Component.translatable("stardewcraft.carpenter.tooltip.cancel"), mouseX, mouseY);
        }
    }

    // =========================================================================
    // Helpers
    // =========================================================================
    private String formatNumber(int n) {
        if (n < 1000) return String.valueOf(n);
        StringBuilder sb = new StringBuilder();
        String s = String.valueOf(n);
        int len = s.length();
        for (int i = 0; i < len; i++) {
            if (i > 0 && (len - i) % 3 == 0) sb.append(',');
            sb.append(s.charAt(i));
        }
        return sb.toString();
    }

    private void playSound(net.minecraft.sounds.SoundEvent ev) {
        if (minecraft != null && minecraft.player != null) {
            minecraft.player.playSound(ev, 1f, 1f);
        }
    }
}
