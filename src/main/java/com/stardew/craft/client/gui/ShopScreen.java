package com.stardew.craft.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import com.stardew.craft.client.gui.overnight.StardewGuiUtil;
import com.stardew.craft.item.IStardewItem;
import com.stardew.craft.network.payload.OpenShopScreenPayload;
import com.stardew.craft.network.payload.ShopPurchasePayload;
import com.stardew.craft.network.payload.ShopPurchaseResultPayload;
import com.stardew.craft.network.payload.ShopSellPayload;
import com.stardew.craft.network.payload.ShopPickupPayload;
import com.stardew.craft.network.payload.ShopSellResultPayload;
import com.stardew.craft.shop.ShopItemEntry;
import com.stardew.craft.sound.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("null")
public class ShopScreen extends Screen {

    // SDV layout (screen-pixels = sprite-px × 4)
    private static final int BORDER   = 40;
    private static final int WIN_W    = 1000 + 2 * BORDER;
    private static final int WIN_H    = 600  + 2 * BORDER;
    private static final int PANEL_H  = WIN_H - 256 + 32 + 4;
    private static final int ROW_H    = (WIN_H - 256) / 4 + 4;
    private static final int ROWS     = 4;
    private static final int PORTRAIT_OFFSET = 320;

    // Cursors UV (sprite coords, NOT ×4)
    private static final int BDR_U=384,BDR_V=373,BDR_W=18,BDR_SH=18;
    private static final int ROW_U=384,ROW_V=396,ROW_W=15,ROW_SH=15;
    private static final int ICO_U=296,ICO_V=363,ICO_W=18,ICO_SH=18;
    private static final int PORT_U=603,PORT_V=414,PORT_W=74,PORT_SH=74;
    private static final int ARR_UP_U=421,ARR_UP_V=459,ARR_UP_W=11,ARR_UP_SH=12;
    private static final int ARR_DN_U=421,ARR_DN_V=472,ARR_DN_W=11,ARR_DN_SH=12;
    private static final int SCR_F_U=435,SCR_F_V=463,SCR_F_W=6,SCR_F_SH=10;
    private static final int SCR_B_U=403,SCR_B_V=383,SCR_B_W=6,SCR_B_SH=6;
    private static final int COIN_U=193,COIN_V=373,COIN_W=9,COIN_SH=10;
    private static final int CLOSE_U=337,CLOSE_V=494,CLOSE_W=12,CLOSE_SH=12;

    // Inventory constants (9-wide × 4-row: hotbar at row-3, main at rows 0-2)
    private static final int INV_COLS  = 9;
    private static final int INV_ROWS  = 4;
    private static final int SLOT_SIZE = 64;  // SDV screen px
    private static final int SLOT_GAP  = 4;   // SDV screen px

    // Colors
    private static final int BG_TINT = 0xBF000000;
    private static final long BUY_HOLD_INITIAL_DELAY_MS = 320;
    private static final long BUY_HOLD_REPEAT_MS = 95;

    // -------------------------------------------------------------------------
    // Runtime state
    // -------------------------------------------------------------------------
    private final String              shopId;
    private final List<ShopItemEntry> forSale;
    private int                       playerMoney;
    private final String              ownerNpcId;
    private final String              ownerDialogue;
    /**
     * IStardewItem.getItemTypeKey() values this shop will buy from player.
     * Sent from server via OpenShopScreenPayload (mirrors SDV SalableItemTags).
     */
    private final Set<String>         acceptedSellTypeKeys;

    // Layout (MC gui coords)
    private int panelX, panelY;
    private int panelWGui, mainHGui;
    private int invBoxXGui, invBoxYGui, invBoxWGui, invBoxHGui;
    private int rowHGui, rowWGui;
    private int invGridX, invGridY, slotSzGui;
    private int upArrowX,upArrowY,upArrowW,upArrowH;
    private int dnArrowX,dnArrowY,dnArrowW,dnArrowH;
    private int scrBarX,scrBarY,scrBarW,scrBarH;
    private int scrRunX,scrRunY,scrRunW,scrRunH;
    private int closeX,closeY,closeW,closeH;
    private float closeScale = 1.0f;
    private int currencyX, currencyY;

    private int   currentIndex = 0;
    private int   hoveredRow   = -1;
    private int   hoveredInvSlot = -1;
    private final float[] rowScales = new float[ROWS];

    private boolean scrolling = false;
    private int     scrollDragOffsetY;

    private float guiScale = 1.0f;
    private long  openedAtMs;
    private static final long SAFETY_MS = 250;

    /** Item currently held on cursor (SDV heldItem equivalent). */
    private ItemStack heldItem = ItemStack.EMPTY;
    private boolean   purchasePending = false;
    private boolean   buyHoldActive = false;
    private int       buyHoldItemIndex = -1;
    private long      buyHoldStartedAtMs = 0L;
    private long      buyHoldLastTickMs = 0L;

    // =========================================================================
    public ShopScreen(OpenShopScreenPayload payload) {
        super(Component.empty());
        this.shopId             = payload.shopId();
        this.forSale            = new ArrayList<>(payload.items()); // mutable for stock updates
        this.playerMoney        = payload.playerMoney();
        this.ownerNpcId         = payload.ownerNpcId();
        this.ownerDialogue      = payload.ownerDialogue();
        this.acceptedSellTypeKeys = new HashSet<>(payload.acceptedSellTypeKeys());
        for (int i = 0; i < ROWS; i++) rowScales[i] = 1.0f;
    }

    // =========================================================================
    // Item name / description resolution (client-side from MC registry)
    // =========================================================================

    /**
     * Returns the localised display name of the item.
     * Priority: MC ItemStack.getHoverName() (honours resource-pack / lang overrides).
     * Falls back to ShopItemEntry.displayName() if the MC item is unknown.
     */
    private String resolveItemName(ShopItemEntry entry) {
        ItemStack stack = resolveStack(entry.itemId());
        if (!stack.isEmpty()) {
            return stack.getHoverName().getString();
        }
        // fallback to whatever the server sent (may be empty for unknown items)
        return entry.displayName().isEmpty() ? entry.itemId() : entry.displayName();
    }

    /**
     * Returns the item description line.
     * MC items don't all have descriptions; use the server-sent description
     * (which ShopRegistry sets to "") — callers skip empty strings.
     */
    /** Resolves a mod item id to an ItemStack (1 unit), or EMPTY on failure. */
    private static ItemStack resolveStack(String itemId) {
        try {
            ResourceLocation rl = ResourceLocation.parse(itemId);
            Item mcItem = BuiltInRegistries.ITEM.get(rl);
            if (mcItem != null && mcItem != Items.AIR) return new ItemStack(mcItem);
        } catch (Exception ignored) {}
        return ItemStack.EMPTY;
    }

    /**
     * Returns true if the item in the given inventory slot can be sold
     * at this shop (mirrors SDV highlightItemToSell).
     */
    private boolean canSellAt(ItemStack stack) {
        if (!heldItem.isEmpty()) {
            return ItemStack.isSameItemSameComponents(heldItem, stack);
        }
        if (stack.isEmpty()) return false;
        if (acceptedSellTypeKeys.isEmpty()) return false;
        if (!(stack.getItem() instanceof IStardewItem si)) return false;
        if (si.getSellPrice(stack) <= 0) return false;
        return acceptedSellTypeKeys.contains(si.getItemTypeKey());
    }

    // =========================================================================
    // init
    // =========================================================================
    @Override
    protected void init() {
        super.init();
        openedAtMs = System.currentTimeMillis();
        Minecraft mc = Minecraft.getInstance();
        guiScale = (float) mc.getWindow().getGuiScale();
        float s4 = s4();

        panelWGui = ui(WIN_W);
        mainHGui  = ui(PANEL_H);
        panelX    = width  / 2 - ui(880) / 2;
        panelY    = height / 2 - ui(WIN_H) / 2 - ui(32);
        rowHGui   = ui(ROW_H);
        rowWGui   = panelWGui - ui(32);

        // Inventory box
        slotSzGui = ui(SLOT_SIZE);
        int gapGui = ui(SLOT_GAP);
        int gridW  = INV_COLS * slotSzGui + (INV_COLS - 1) * gapGui;
        int gridH  = INV_ROWS * slotSzGui + (INV_ROWS - 1) * gapGui;
        invBoxWGui = gridW + ui(56);
        invBoxHGui = gridH + ui(44);
        invBoxXGui = panelX + panelWGui - invBoxWGui - ui(8);
        invBoxYGui = panelY + mainHGui + ui(8);
        invGridX   = invBoxXGui + ui(20);
        invGridY   = invBoxYGui + ui(16);

        // Scroll arrows
        upArrowW = (int)(ARR_UP_W * s4); upArrowH = (int)(ARR_UP_SH * s4);
        upArrowX = panelX + panelWGui + ui(16); upArrowY = panelY + ui(16);
        dnArrowW = upArrowW; dnArrowH = upArrowH;
        dnArrowX = upArrowX; dnArrowY = panelY + mainHGui - ui(64);

        // Scrollbar
        scrBarW = (int)(SCR_F_W * s4); scrBarH = (int)(SCR_F_SH * s4);
        scrBarX = upArrowX + ui(12);
        scrRunX = scrBarX; scrRunY = upArrowY + upArrowH + ui(4);
        scrRunW = scrBarW; scrRunH = dnArrowY - scrRunY - ui(4);
        updateScrollBarPosition();

        // Close button
        closeW = (int)(CLOSE_W * s4); closeH = (int)(CLOSE_SH * s4);
        closeX = panelX + panelWGui - closeW - ui(4);
        closeY = panelY - closeH / 2;

        // SDV parity: money box = overrideX = xPos-36, overrideY = yPos+height-inv.height-12
        // Our equiv: panelY + mainHGui - 12  =  invBoxYGui - ui(8) - ui(12)  =  invBoxYGui - ui(20)
        currencyX = panelX - ui(36);
        currencyY = invBoxYGui - ui(20);
    }

    // =========================================================================
    // renderBackground — suppress MC dirt/blur (SDV draws its own fadeToBlackRect)
    // =========================================================================
    @Override
    public void renderBackground(@Nonnull GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        // intentionally empty
    }

    // =========================================================================
    // render
    // =========================================================================
    @Override
    public void render(@Nonnull GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        g.fill(0, 0, width, height, BG_TINT); // SDV: fadeToBlackRect × 0.75

        float s4 = s4();

        // 1. Inventory box
        StardewGuiUtil.drawTextureBox(g,
            StardewGuiUtil.CURSORS, StardewGuiUtil.CURSORS_WIDTH, StardewGuiUtil.CURSORS_HEIGHT,
            BDR_U,BDR_V,BDR_W,BDR_SH, invBoxXGui, invBoxYGui, invBoxWGui, invBoxHGui, s4, false);

        // 2. Main items box (with shadow)
        StardewGuiUtil.drawTextureBox(g,
            StardewGuiUtil.CURSORS, StardewGuiUtil.CURSORS_WIDTH, StardewGuiUtil.CURSORS_HEIGHT,
            BDR_U,BDR_V,BDR_W,BDR_SH, panelX, panelY, panelWGui, mainHGui, s4, true);

        // 3. Currency
        drawCurrency(g, s4);

        // 4. Player inventory grid
        drawInventory(g, mouseX, mouseY, s4);

        // 5. Item rows
        hoveredRow = -1;
        for (int i = 0; i < ROWS; i++) {
            int itemIdx = currentIndex + i;
            if (itemIdx >= forSale.size()) break;
            drawItemRow(g, mouseX, mouseY, s4, i, itemIdx);
        }

        // 6. Out-of-stock message
        if (forSale.isEmpty()) {
            String msg = "Nothing for sale.";
            g.drawString(font, msg,
                panelX + panelWGui/2 - font.width(msg)/2,
                panelY + mainHGui/2 - font.lineHeight/2, 0x404040, false);
        }

        // 7. Scroll arrows
        boolean upOn = currentIndex > 0;
        boolean dnOn = currentIndex < Math.max(0, forSale.size() - ROWS);
        if (!upOn) g.setColor(1f,1f,1f,0.4f);
        StardewGuiUtil.drawFromCursors(g, upArrowX, upArrowY, ARR_UP_U, ARR_UP_V, ARR_UP_W, ARR_UP_SH, s4);
        if (!upOn) g.setColor(1f,1f,1f,1f);
        if (!dnOn) g.setColor(1f,1f,1f,0.4f);
        StardewGuiUtil.drawFromCursors(g, dnArrowX, dnArrowY, ARR_DN_U, ARR_DN_V, ARR_DN_W, ARR_DN_SH, s4);
        if (!dnOn) g.setColor(1f,1f,1f,1f);

        // 8. Scrollbar
        if (forSale.size() > ROWS) {
            StardewGuiUtil.drawTextureBox(g,
                StardewGuiUtil.CURSORS, StardewGuiUtil.CURSORS_WIDTH, StardewGuiUtil.CURSORS_HEIGHT,
                SCR_B_U,SCR_B_V,SCR_B_W,SCR_B_SH, scrRunX, scrRunY, scrRunW, scrRunH, s4, false);
            StardewGuiUtil.drawFromCursors(g, scrBarX, scrBarY, SCR_F_U, SCR_F_V, SCR_F_W, SCR_F_SH, s4);
        }

        // 9. Close button
        boolean closeHov = isIn(mouseX, mouseY, closeX, closeY, closeW, closeH);
        closeScale = closeHov ? Math.min(closeScale+0.04f,1.2f) : Math.max(1.0f,closeScale-0.04f);
        float cs = s4 * closeScale;
        int cdx = closeX + closeW/2 - (int)(CLOSE_W *cs/2);
        int cdy = closeY + closeH/2 - (int)(CLOSE_SH*cs/2);
        StardewGuiUtil.drawFromCursors(g, cdx, cdy, CLOSE_U, CLOSE_V, CLOSE_W, CLOSE_SH, cs);

        // 10. Portrait
        int portX = panelX - ui(PORTRAIT_OFFSET);
        if (portX > 0 && !ownerNpcId.isEmpty()) {
            StardewGuiUtil.drawFromCursors(g, portX, panelY, PORT_U, PORT_V, PORT_W, PORT_SH, s4);
            if (!ownerDialogue.isEmpty()) {
                int dy = panelY + (int)(PORT_SH * s4) + ui(8);
                g.drawWordWrap(font, Component.literal(ownerDialogue),
                    portX, dy, (int)(PORT_W*s4), 0xF5DEB3);
            }
        }

        // 11. Held item on cursor — centered on cursor tip (MC convention: -8,-8)
        if (!heldItem.isEmpty()) {
            g.renderItem(heldItem, mouseX - 8, mouseY - 8);
            g.renderItemDecorations(font, heldItem, mouseX - 8, mouseY - 8);
        }

        // 12. Tooltip
        if (hoveredRow >= 0) {
            int idx = currentIndex + hoveredRow;
            if (idx < forSale.size()) drawBuyTooltip(g, mouseX, mouseY, forSale.get(idx));
        } else if (hoveredInvSlot >= 0 && heldItem.isEmpty()) {
            drawInvTooltip(g, mouseX, mouseY, hoveredInvSlot);
        }
    }

    // =========================================================================
    // Sub-render helpers
    // =========================================================================

    // SDV money box sprite: cursors (340,472,65,17) ×4 = background banner
    // SDV digit sprite:    cursors (286, 502-digit*8, 5, 8) ×4, color=Maroon (0x800000)
    // Position: drawMoneyBox called with overrideX = xPos-36, overrideY = yPos+height-inventory.height-12
    // The banner draws at pos + (28,172), digits at pos + (68,196) relative to the
    // "position" field which in ShopMenu equals (overrideX, overrideY-172).
    // So banner absolute Y = overrideY - 172 + 172 = overrideY
    //    digit  absolute Y = overrideY - 172 + 196 = overrideY + 24
    private static final int MONEY_BOX_U = 340, MONEY_BOX_V = 472;
    private static final int MONEY_BOX_W = 65,  MONEY_BOX_H = 17;
    private static final int DIGIT_U = 286,  DIGIT_V_BASE = 502;
    private static final int DIGIT_W = 5,    DIGIT_H = 8;
    private static final int NUM_DIGITS = 8;

    private void drawCurrency(GuiGraphics g, float s4) {
        // ── Banner ──────────────────────────────────────────────────────────
        // SDV DayTimeMoneyBox: banner draws at position + (28, 0) where both
        // values are in SDV *screen* pixels (already ×4 scale).  The correct
        // MC-GUI conversion is ui(sdvScreenPx) = sdvScreenPx / guiScale, NOT
        // sdvScreenPx * s4 (which would be 4× too large).
        int bannerX = currencyX + ui(28);  // ui(28) == 28/guiScale
        int bannerY = currencyY;
        StardewGuiUtil.drawFromCursors(g, bannerX, bannerY,
            MONEY_BOX_U, MONEY_BOX_V, MONEY_BOX_W, MONEY_BOX_H, s4);

        // ── Digits ──────────────────────────────────────────────────────────
        // SDV MoneyDial: digits at position + (68, 24) (SDV screen px),
        // advance per digit = 6 sprite-px × 4 = 24 SDV screen-px.
        // (int)(6*s4) == ui(24) because 6 sprite-px × s4 = 6×4/guiScale ✓
        int digitStartX  = currencyX + ui(68);  // offset is SDV screen-px → ui()
        int digitY       = currencyY + ui(24);   // same
        int digitSpacing = (int)(6 * s4);        // 6 sprite-px × scale per digit

        int val = Math.max(0, playerMoney);
        int[] digits = new int[NUM_DIGITS];
        int temp = val;
        for (int i = NUM_DIGITS - 1; i >= 0; i--) { digits[i] = temp % 10; temp /= 10; }

        boolean significant = false;
        for (int i = 0; i < NUM_DIGITS; i++) {
            int d = digits[i];
            if (d > 0 || i == NUM_DIGITS - 1) significant = true;
            if (significant) {
                int digitV = DIGIT_V_BASE - d * DIGIT_H;
                // g.setColor() before drawFromCursors() would immediately be
                // overridden inside drawFromCursorsTint().  Must pass the tint
                // directly via drawFromCursorsTint (Maroon = #800000 = 0.502,0,0).
                StardewGuiUtil.drawFromCursorsTint(g,
                    digitStartX + i * digitSpacing, digitY,
                    DIGIT_U, digitV, DIGIT_W, DIGIT_H, s4,
                    0.502f, 0f, 0f, 1f);
            }
        }
    }

    private void drawInventory(GuiGraphics g, int mouseX, int mouseY, float s4) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        int sz  = slotSzGui;
        int gap = ui(SLOT_GAP);
        hoveredInvSlot = -1;

        for (int row = 0; row < INV_ROWS; row++) {
            for (int col = 0; col < INV_COLS; col++) {
                // row 3 = hotbar (slots 0-8), rows 0-2 = main inventory (9-35)
                int mcSlot = (row == 3) ? col : (9 + row * 9 + col);
                int sx = invGridX + col * (sz + gap);
                int sy = invGridY + row * (sz + gap);
                boolean hov = isIn(mouseX, mouseY, sx, sy, sz, sz);
                if (hov) hoveredInvSlot = mcSlot;

                ItemStack stack = mc.player.getInventory().getItem(mcSlot);
                boolean sellable = canSellAt(stack);

                StardewGuiUtil.drawMenuTileIndex(g, sx, sy, sz, sz, 10);
                if (hov) {
                    g.fill(sx, sy, sx + sz, sy + sz, 0x35FFFFFF);
                }

                if (!stack.isEmpty()) {
                    int ix = sx + (sz-16)/2;
                    int iy = sy + (sz-16)/2;
                    // Only dim the item icon (not the slot background).
                    if (!sellable) g.setColor(0.62f, 0.62f, 0.62f, 1f);
                    g.renderItem(stack, ix, iy);
                    g.renderItemDecorations(font, stack, ix, iy);
                    if (!sellable) g.setColor(1f, 1f, 1f, 1f);
                }
            }
        }
    }

    private void drawItemRow(GuiGraphics g, int mouseX, int mouseY, float s4, int i, int itemIdx) {
        ShopItemEntry item = forSale.get(itemIdx);
        boolean canAfford = item.price() <= 0 || playerMoney >= item.price();
        boolean hasTrade = hasTradeItem(item, 1);
        boolean outOfStock = item.stock() == 0;
        boolean canBuy = canAfford && hasTrade && !outOfStock;

        int rowX = panelX + ui(16);
        int rowY = panelY + ui(16) + i * rowHGui;
        boolean hov = isIn(mouseX, mouseY, rowX, rowY, rowWGui, rowHGui) && !scrolling;
        if (hov) hoveredRow = i;

        // Scale animation
        rowScales[i] = hov && canBuy
            ? Math.min(rowScales[i]+0.03f, 1.1f)
            : Math.max(1.0f, rowScales[i]-0.03f);

        // Row background tint
        if      (hov && canBuy)    g.setColor(0.961f, 0.871f, 0.702f, 1.0f); // Wheat
        else if (!canBuy)          g.setColor(0.6f,   0.6f,   0.6f,   1.0f); // grey
        StardewGuiUtil.drawTextureBox(g,
            StardewGuiUtil.CURSORS, StardewGuiUtil.CURSORS_WIDTH, StardewGuiUtil.CURSORS_HEIGHT,
            ROW_U,ROW_V,ROW_W,ROW_SH, rowX, rowY, rowWGui, rowHGui, s4, false);
        g.setColor(1f,1f,1f,1f);

        // Icon background (SDV: button.X+32-12, button.Y+24-4)
        int icoX = rowX + ui(20);
        int icoY = rowY + ui(20);
        StardewGuiUtil.drawFromCursors(g, icoX, icoY, ICO_U, ICO_V, ICO_W, ICO_SH, s4);

        // Item icon
        int iconX = rowX + ui(24);
        int iconY = rowY + ui(24);
        float alpha = canBuy ? 1.0f : 0.25f; // SDV: failedCanPurchaseCheck → 0.25
        drawItemIconAt(g, item.itemId(), iconX, iconY, s4, alpha);

        // Name — resolved from MC registry (localised), not hardcoded
        String name = truncateName(resolveItemName(item), item.price() > 0);
        // SDV SpriteText ItemRowTextColor = Color.Black (0x000000), we approximate with 0x1a1a1a
        g.drawString(font, name, rowX + ui(104), rowY + ui(28),
            canBuy ? 0x1a1a1a : 0x888888, false);

        // Price + coin (SDV: right-60 text, right-52 coin at Y+40-4)
        if (item.price() > 0) {
            String prStr = item.price() + " ";
            int prW   = font.width(prStr);
            int prX   = rowX + rowWGui - ui(60) - prW;
            int prY   = rowY + ui(28);
            g.drawString(font, prStr, prX, prY,
                canAfford ? 0x404040 : 0x992222, false);

            float coinA = canBuy ? 1.0f : 0.25f;
            if (coinA < 1f) g.setColor(1f,1f,1f,coinA);
            StardewGuiUtil.drawFromCursors(g,
                rowX + rowWGui - ui(52), rowY + ui(36),
                COIN_U, COIN_V, COIN_W, COIN_SH, s4);
            if (coinA < 1f) g.setColor(1f,1f,1f,1f);
        }

        // Trade requirement (SDV: stock.TradeItem)
        if (item.requiresTrade()) {
            ItemStack trade = resolveStack(item.tradeItemId());
            int req = Math.max(1, item.tradeItemCount());
            boolean enough = hasTrade;
            int tx = rowX + rowWGui - ui(item.price() > 0 ? 140 : 88);
            int ty = rowY + ui(item.price() > 0 ? 20 : 28);

            if (!trade.isEmpty()) {
                if (!enough) g.setColor(1f, 1f, 1f, 0.25f);
                g.renderItem(trade, tx, ty);
                if (!enough) g.setColor(1f, 1f, 1f, 1f);
            }

            String reqText = "x" + req;
            g.drawString(font, reqText, tx + ui(20), ty + ui(8),
                enough ? 0x404040 : 0x992222, false);
        }

        // Limited stock counter
        if (item.stock() != Integer.MAX_VALUE && item.stock() > 0) {
            String sc = "(" + item.stock() + ")";
            int prW = item.price() > 0 ? font.width(item.price() + " ") + ui(96) : ui(16);
            g.drawString(font, sc, rowX + rowWGui - prW - font.width(sc) - ui(4),
                rowY + ui(28), 0x888888, false);
        }
    }

    private void drawItemIconAt(GuiGraphics g, String itemId,
                                 int x, int y, float s4, float alpha) {
        try {
            ResourceLocation rl = ResourceLocation.parse(itemId);
            Item mcItem = BuiltInRegistries.ITEM.get(rl);
            if (mcItem == null || mcItem == Items.AIR) return;
            if (alpha < 1f) g.setColor(1f,1f,1f,alpha);
            // Render at native MC 16x16 size; avoid SDV-space over-scaling.
            g.renderItem(new ItemStack(mcItem), x, y);
            if (alpha < 1f) g.setColor(1f,1f,1f,1f);
        } catch (Exception ignored) {}
    }

    private void drawBuyTooltip(GuiGraphics g, int mx, int my, ShopItemEntry item) {
        Minecraft mc = Minecraft.getInstance();
        ItemStack stack = resolveStack(item.itemId());

        List<Component> lines = new ArrayList<>();

        // --- MC 原生 tooltip 内容（名称、模组说明、耦魔等）---
        if (!stack.isEmpty()) {
            List<Component> vanillaLines = stack.getTooltipLines(
                net.minecraft.world.item.Item.TooltipContext.EMPTY,
                mc.player,
                net.minecraft.world.item.TooltipFlag.Default.NORMAL);
            lines.addAll(vanillaLines);
        } else {
            // 未知物品，至少显示名义
            lines.add(Component.literal(resolveItemName(item)).withStyle(ChatFormatting.BOLD));
        }

        // --- 商店自定义信息（空行分隔）---
        lines.add(Component.empty());
        if (item.price() > 0) {
            boolean ok = playerMoney >= item.price();
            lines.add(Component.literal("购入：" + item.price() + "g")
                .withStyle(ok ? ChatFormatting.GOLD : ChatFormatting.DARK_RED));
        } else {
            lines.add(Component.literal("免费").withStyle(ChatFormatting.GREEN));
        }
        lines.add(Component.literal("[Shift]×5  [Ctrl+Shift]×25").withStyle(ChatFormatting.DARK_GRAY));
        if (item.stock() != Integer.MAX_VALUE)
            lines.add(Component.literal("剩余库存：" + item.stock()).withStyle(ChatFormatting.AQUA));
        if (item.requiresTrade()) {
            ItemStack trade = resolveStack(item.tradeItemId());
            String tradeName = trade.isEmpty() ? item.tradeItemId() : trade.getHoverName().getString();
            int reqCount = Math.max(1, item.tradeItemCount());
            boolean enough = hasTradeItem(item, 1);
            lines.add(Component.literal("交易需求：" + tradeName + " x" + reqCount)
                .withStyle(enough ? ChatFormatting.GRAY : ChatFormatting.DARK_RED));
        }
        if (!stack.isEmpty() && stack.getItem() instanceof IStardewItem si) {
            int sell = si.getSellPrice(stack);
            if (sell > 0)
                lines.add(Component.literal("基础出售价：" + sell + "g").withStyle(ChatFormatting.GRAY));
        }
        g.renderTooltip(font, lines, java.util.Optional.empty(), mx, my);
    }

    private void drawInvTooltip(GuiGraphics g, int mx, int my, int slot) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        ItemStack stack = mc.player.getInventory().getItem(slot);
        if (stack.isEmpty()) return;
        List<Component> lines = new ArrayList<>(stack.getTooltipLines(
            net.minecraft.world.item.Item.TooltipContext.EMPTY, mc.player,
            net.minecraft.world.item.TooltipFlag.Default.NORMAL));
        boolean sellable = canSellAt(stack);
        if (sellable && stack.getItem() instanceof IStardewItem si) {
            // Show sell price (what the shop pays = IStardewItem.getSellPrice, the item's sell value)
            int sellUnit = si.getSellPrice(stack);
            if (sellUnit > 0) {
                lines.add(Component.literal("出售：" + (sellUnit * stack.getCount()) + "g")
                    .withStyle(ChatFormatting.GOLD));
                lines.add(Component.literal("[Click]全部出售  [Right-Click]出售1个")
                    .withStyle(ChatFormatting.DARK_GRAY));
            }
        } else if (!sellable && !stack.isEmpty()) {
            lines.add(Component.literal("此商店不收购此物品").withStyle(ChatFormatting.DARK_GRAY));
        }
        g.renderTooltip(font, lines, java.util.Optional.empty(), mx, my);
    }

    // =========================================================================
    // Input
    // =========================================================================
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int mx=(int)mouseX, my=(int)mouseY;

        // Close
        if (isIn(mx,my,closeX,closeY,closeW,closeH)) {
            if (!heldItem.isEmpty()) placeHeldItemInInventory();
            playSound(ModSounds.DWOP.get());
            onClose();
            return true;
        }
        // Up arrow
        if (isIn(mx,my,upArrowX,upArrowY,upArrowW,upArrowH)) {
            if (currentIndex > 0) { currentIndex--; updateScrollBarPosition(); playSound(ModSounds.SHWIP.get()); }
            return true;
        }
        // Down arrow
        if (isIn(mx,my,dnArrowX,dnArrowY,dnArrowW,dnArrowH)) {
            if (currentIndex < Math.max(0,forSale.size()-ROWS)) { currentIndex++; updateScrollBarPosition(); playSound(ModSounds.SHWIP.get()); }
            return true;
        }
        // Scrollbar drag
        if (isIn(mx,my,scrBarX,scrBarY,scrBarW,scrBarH)) {
            scrolling=true; scrollDragOffsetY=my-scrBarY; return true;
        }
        // Inventory slot
        int inv = getInvSlotAt(mx,my);
        if (inv >= 0) { onInventorySlotClicked(inv,button); return true; }
        // Item row
        for (int i=0; i<ROWS; i++) {
            int idx=currentIndex+i;
            if (idx>=forSale.size()) break;
            int rx=panelX+ui(16), ry=panelY+ui(16)+i*rowHGui;
            if (isIn(mx,my,rx,ry,rowWGui,rowHGui)) {
                if (button == 0 || button == 1) {
                    tryPurchase(idx, false);
                    buyHoldActive = true;
                    buyHoldItemIndex = idx;
                    buyHoldStartedAtMs = System.currentTimeMillis();
                    buyHoldLastTickMs = buyHoldStartedAtMs;
                }
                return true;
            }
        }
        return super.mouseClicked(mouseX,mouseY,button);
    }

    @Override
    public boolean mouseDragged(double mouseX,double mouseY,int button,double dx,double dy){
        if (scrolling && forSale.size()>ROWS) {
            int newY=(int)mouseY-scrollDragOffsetY;
            newY=Math.max(scrRunY,Math.min(scrRunY+scrRunH-scrBarH,newY));
            float pct=(float)(newY-scrRunY)/Math.max(1,scrRunH-scrBarH);
            currentIndex=Math.max(0,Math.min(forSale.size()-ROWS,Math.round(pct*(forSale.size()-ROWS))));
            updateScrollBarPosition();
            return true;
        }
        return super.mouseDragged(mouseX,mouseY,button,dx,dy);
    }

    @Override
    public boolean mouseReleased(double mouseX,double mouseY,int button){
        scrolling=false;
        if (button == 0 || button == 1) {
            buyHoldActive = false;
            buyHoldItemIndex = -1;
        }
        return super.mouseReleased(mouseX,mouseY,button);
    }

    @Override
    public void tick() {
        super.tick();
        if (!buyHoldActive) return;
        if (buyHoldItemIndex < 0 || buyHoldItemIndex >= forSale.size()) return;

        long now = System.currentTimeMillis();
        if (now - buyHoldStartedAtMs < BUY_HOLD_INITIAL_DELAY_MS) return;
        if (now - buyHoldLastTickMs < BUY_HOLD_REPEAT_MS) return;

        buyHoldLastTickMs = now;

        // SDV parity: play the repeat-purchase sound on every hold tick,
        // independently of purchasePending (which only gates the network packet).
        ShopItemEntry holdItem = forSale.get(buyHoldItemIndex);
        boolean canAfford = holdItem.price() <= 0 || playerMoney >= holdItem.price();
        boolean inStock   = holdItem.stock() != 0;
        if (canAfford && inStock && hasTradeItem(holdItem, 1)) {
            playSound(ModSounds.PURCHASE_REPEAT.get());
        }

        tryPurchase(buyHoldItemIndex, true);
    }

    @Override
    public boolean mouseScrolled(double mouseX,double mouseY,double hScroll,double vScroll){
        int dir=vScroll>0?-1:1;
        int ni=Math.max(0,Math.min(forSale.size()-ROWS,currentIndex+dir));
        if (ni!=currentIndex) { currentIndex=ni; updateScrollBarPosition(); playSound(ModSounds.SHINY4.get()); }
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode,int scan,int mods){
        if (keyCode==InputConstants.KEY_ESCAPE) {
            if (!heldItem.isEmpty()) { placeHeldItemInInventory(); return true; }
            onClose(); return true;
        }
        return super.keyPressed(keyCode,scan,mods);
    }

    // =========================================================================
    // Purchase (SDV: tryToPurchaseItem)
    // =========================================================================
    private boolean hasTradeItem(ShopItemEntry entry, int qty) {
        if (!entry.requiresTrade()) return true;
        ItemStack req = resolveStack(entry.tradeItemId());
        if (req.isEmpty()) return false;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return false;
        int need = Math.max(1, entry.tradeItemCount()) * Math.max(1, qty);
        return mc.player.getInventory().countItem(req.getItem()) >= need;
    }

    private void tryPurchase(int itemIdx, boolean repeating) {
        if (System.currentTimeMillis()-openedAtMs < SAFETY_MS) return;
        if (purchasePending) return;

        ShopItemEntry item = forSale.get(itemIdx);
        boolean shift=hasShiftDown(), ctrl=hasControlDown();
        int qty = (shift&&ctrl) ? 25 : shift ? 5 : 1;

        // SDV parity: never buy more than this salable's max stack size.
        ItemStack salable = resolveStack(item.itemId());
        if (!salable.isEmpty()) {
            qty = Math.min(qty, Math.max(1, salable.getMaxStackSize()));
        }

        if (item.stock()!=Integer.MAX_VALUE) qty=Math.min(qty,item.stock());
        if (qty<=0) { playSound(ModSounds.CANCEL.get()); return; }

        int cost=item.price()*qty;
        if (cost>playerMoney || !hasTradeItem(item, qty)) { playSound(ModSounds.CANCEL.get()); return; }

        playerMoney -= cost; // optimistic
        purchasePending = true;
        if (!repeating) {
            playSound(ModSounds.PURCHASE_CLICK.get());
        }
        PacketDistributor.sendToServer(new ShopPurchasePayload(shopId,itemIdx,qty));
    }

    // =========================================================================
    // Sell (SDV: receiveLeftClick on inventory component)
    // =========================================================================
    private void onInventorySlotClicked(int slot,int button) {
        Minecraft mc=Minecraft.getInstance();
        if (mc.player==null) return;
        if (!heldItem.isEmpty()) { placeHeldItemToSlot(slot); return; }
        ItemStack stack=mc.player.getInventory().getItem(slot);
        if (stack.isEmpty()) return;
        // SDV highlightItemToSell: only allow selling what this shop accepts
        if (!canSellAt(stack)) return;
        int qty=(button==1)?1:stack.getCount();
        playSound(ModSounds.PURCHASE_CLICK.get());
        PacketDistributor.sendToServer(new ShopSellPayload(shopId,slot,qty));
    }

    // =========================================================================
    // heldItem placement
    // =========================================================================
    private void placeHeldItemToSlot(int slot) {
        if (heldItem.isEmpty()) return;
        sendPickupPayload(slot); // tell server: place in THIS specific slot
        heldItem = ItemStack.EMPTY;
        playSound(ModSounds.SHINY4.get());
    }

    private void placeHeldItemInInventory() {
        if (!heldItem.isEmpty()) {
            sendPickupPayload(-1); // -1 = auto-place in first available slot
            heldItem = ItemStack.EMPTY;
        }
    }

    /** Send ShopPickupPayload to server. targetSlot >= 0 places in that specific slot; -1 = auto. */
    private void sendPickupPayload(int targetSlot) {
        if (heldItem.isEmpty()) return;
        String id = BuiltInRegistries.ITEM.getKey(heldItem.getItem()).toString();
        PacketDistributor.sendToServer(new ShopPickupPayload(id, heldItem.getCount(), targetSlot));
    }

    // =========================================================================
    // Network callbacks
    // =========================================================================
    public void onPurchaseResult(ShopPurchaseResultPayload r) {
        purchasePending=false;
        playerMoney=r.newMoney();
        if (!r.success()) { playSound(ModSounds.CANCEL.get()); return; }
        // Update stock
        int idx=r.itemIndex();
        if (idx>=0&&idx<forSale.size()) {
            ShopItemEntry old=forSale.get(idx);
            if (old.stock()!=Integer.MAX_VALUE) {
                forSale.set(idx,new ShopItemEntry(old.itemId(),old.displayName(),old.description(),
                    old.price(),Math.max(0,old.stock()-r.quantity()),old.tradeItemId(),old.tradeItemCount(),
                    old.seasons(),old.minYear()));
            }
        }
        if (!r.itemId().isEmpty() && r.quantity() > 0) {
            ItemStack bought = resolveStack(r.itemId());
            if (!bought.isEmpty()) {
                bought.setCount(r.quantity());
                if (heldItem.isEmpty()) {
                    heldItem = bought;
                } else if (ItemStack.isSameItemSameComponents(heldItem, bought)) {
                    heldItem.grow(bought.getCount());
                }
            }
        }
        try { if (ModSounds.COIN!=null) playSound(ModSounds.COIN.get()); } catch(Exception ignored){}
    }

    public void onSellResult(ShopSellResultPayload r) {
        if (!r.success()) return;
        playerMoney=r.newMoney();
        playSound(ModSounds.PURCHASE_CLICK.get());
        // Inventory will sync via vanilla; refresh local view
        Minecraft mc=Minecraft.getInstance();
        if (mc.player!=null) mc.player.getInventory().setChanged();
    }

    // =========================================================================
    // Scrollbar
    // =========================================================================
    private void updateScrollBarPosition() {
        if (forSale.size()<=ROWS) { scrBarY=scrRunY; return; }
        int maxIdx=Math.max(1,forSale.size()-ROWS);
        float pct=(float)currentIndex/maxIdx;
        scrBarY=scrRunY+Math.round(pct*Math.max(0,scrRunH-scrBarH));
        if (currentIndex>=forSale.size()-ROWS) scrBarY=scrRunY+scrRunH-scrBarH;
    }

    // =========================================================================
    // Helpers
    // =========================================================================
    private int getInvSlotAt(int mx,int my) {
        int sz=slotSzGui, gap=ui(SLOT_GAP);
        for (int r=0;r<INV_ROWS;r++) for (int c=0;c<INV_COLS;c++) {
            int sx=invGridX+c*(sz+gap), sy=invGridY+r*(sz+gap);
            if (isIn(mx,my,sx,sy,sz,sz)) return (r==3)?c:(9+r*9+c);
        }
        return -1;
    }

    private String truncateName(String name, boolean hasPrice) {
        int maxLen=hasPrice?27:37;
        if (name.length()>maxLen) return name.substring(0,maxLen)+"...";
        return name;
    }

    private int ui(int sdvPx) { return Math.round(sdvPx/guiScale); }
    private float s4()        { return 4.0f/guiScale; }
    private static boolean isIn(int mx,int my,int x,int y,int w,int h){
        return mx>=x&&mx<x+w&&my>=y&&my<y+h;
    }
    private void playSound(net.minecraft.sounds.SoundEvent ev){
        if (minecraft!=null&&minecraft.player!=null) minecraft.player.playSound(ev,1f,1f);
    }

    @Override public boolean isPauseScreen(){ return false; }
}
