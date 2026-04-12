package com.stardew.craft.communitycenter.client;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.client.gui.common.StardewRenderMapping;
import com.stardew.craft.client.gui.overnight.StardewGuiUtil;
import com.stardew.craft.communitycenter.data.BundleDataManager;
import com.stardew.craft.communitycenter.data.BundleDefinition;
import com.stardew.craft.communitycenter.data.BundleIngredient;
import com.stardew.craft.communitycenter.data.BundleItemResolver;
import com.stardew.craft.communitycenter.menu.BundleMenu;
import com.stardew.craft.communitycenter.network.BundleClientData;
import com.stardew.craft.communitycenter.network.BundleClaimRewardPayload;
import com.stardew.craft.communitycenter.network.BundleDepositPayload;
import com.stardew.craft.communitycenter.network.BundlePurchasePayload;
import com.stardew.craft.item.quality.QualityHelper;
import com.stardew.craft.sound.ModSounds;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Client-side GUI for Community Center bundle interface.
 * Pixel-perfect replication of SDV JunimoNoteMenu.cs draw().
 *
 * Two-page mode:
 * 1. Room overview (specificBundlePage = false): shows all bundle orbs
 * 2. Bundle detail (specificBundlePage = true): shows ingredient slots + inventory
 */
@SuppressWarnings("null")
public class BundleScreen extends AbstractContainerScreen<BundleMenu> {

    // ── Texture constants ──
    private static final ResourceLocation JUNIMO_NOTE = ResourceLocation.fromNamespaceAndPath(
            StardewCraft.MODID, "textures/gui/junimo_note.png");
    private static final int TEX_WIDTH = 640;
    private static final int TEX_HEIGHT = 308;

    // SDV menu base dimensions (sprite pixels, before ×4 scale)
    private static final int BASE_W = 320;
    private static final int BASE_H = 180;

    // ── State ──
    private StardewRenderMapping mapping;
    private float s4;
    private int menuX, menuY;

    private boolean specificBundlePage = false;
    private int selectedBundleId = -1;
    private int lastSyncedAreaId = -1;
    private int lastDataVersion = -1;

    // Bundle orbs for room overview
    private final List<BundleOrb> bundleOrbs = new ArrayList<>();

    // Ingredient slots for detail page (submission slots)
    private final List<IngredientSlotVisual> ingredientSlots = new ArrayList<>();
    // Ingredient list for detail page (required items display)
    private final List<IngredientListEntry> ingredientList = new ArrayList<>();

    // Present button state (reward claiming)
    private boolean showPresentButton = false;
    private int presentAnimFrame = 0;
    private float presentAnimTimer = 0;

    // Animation
    private long lastTickMs = 0;

    // ── SDV Bundle orb positions (from getBundleLocationFromNumber) ──
    // These are pixel offsets from (xPositionOnScreen, yPositionOnScreen)
    private static final int[][] BUNDLE_POSITIONS = {
            {592, 136},  // 0
            {392, 384},  // 1
            {784, 388},  // 2
            {304, 252},  // 3
            {892, 252},  // 4
            {588, 276},  // 5
            {588, 380},  // 6
            {440, 164},  // 7
            {776, 164},  // 8
    };

    public BundleScreen(BundleMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();

        float guiScale = (float) this.minecraft.getWindow().getGuiScale();
        this.mapping = new StardewRenderMapping(this.width, this.height, guiScale);
        this.s4 = mapping.s4();

        // SDV: xPositionOnScreen = viewport.Width/2 - 640, yPositionOnScreen = viewport.Height/2 - 360
        this.menuX = (this.width - mapping.ui(1280)) / 2;
        this.menuY = (this.height - mapping.ui(720)) / 2;

        // Anchor inventory slot rendering relative to menu origin
        this.imageWidth = mapping.ui(1280);
        this.imageHeight = mapping.ui(720);
        this.leftPos = menuX;
        this.topPos = menuY;

        // Reposition MC inventory slots to match SDV's detail page layout.
        // SDV: InventoryMenu drawn at (xPos+128, yPos+140) in screen pixels.
        // MC slot.x/y are GUI-pixel offsets from leftPos/topPos (= menuX/menuY).
        // MC slots are 18 GUI px each; we place them so that the grid's top-left
        // aligns with the SDV inventory origin in GUI units.
        int invOffX = mapping.ui(128);
        int invOffY = mapping.ui(140);
        for (int i = 0; i < this.menu.slots.size(); i++) {
            net.minecraft.world.inventory.Slot slot = this.menu.slots.get(i);
            if (i < 27) {
                int col = i % 9;
                int row = i / 9;
                slot.x = invOffX + col * 18;
                slot.y = invOffY + row * 18;
            } else {
                int col = i - 27;
                slot.x = invOffX + col * 18;
                slot.y = invOffY + 3 * 18 + 4; // 4px gap before hotbar
            }
        }

        buildBundleOrbs();

        this.lastTickMs = System.currentTimeMillis();
    }

    private void buildBundleOrbs() {
        bundleOrbs.clear();
        int areaId = this.menu.getAreaId();
        List<BundleDefinition> defs = BundleDataManager.getBundlesForArea(areaId);
        BundleClientData cd = BundleClientData.INSTANCE;

        for (int i = 0; i < defs.size(); i++) {
            BundleDefinition def = defs.get(i);
            int[] pos = (i < BUNDLE_POSITIONS.length) ? BUNDLE_POSITIONS[i] : BUNDLE_POSITIONS[0];

            // SDV positions are screen-pixel offsets from menu origin
            int orbX = menuX + mapping.ui(pos[0]);
            int orbY = menuY + mapping.ui(pos[1]);

            // Sprite source rect: (bundleColor * 256 % 512, 244 + bundleColor * 256 / 512 * 16, 16, 16)
            int color = def.color();
            int srcX = (color * 256) % 512;
            int srcY = 244 + (color * 256 / 512) * 16;

            boolean complete = cd.isBundleComplete(def.bundleId());
            int filledCount = cd.countFilledSlots(def.bundleId());
            bundleOrbs.add(new BundleOrb(def, orbX, orbY, srcX, srcY, complete, filledCount));
        }

        // Check for present button (any bundle in this area has claimable reward)
        showPresentButton = cd.hasAnyRewardForArea(areaId);
    }

    /**
     * Inventory rendering is handled by the parent class.
     * On overview page we skip super.render() to hide inventory.
     * On detail page we call super.render() which draws inventory.
     * Slot positions are fixed in BundleMenu constructor.
     */

    // ═════════════════════════════════════════════════════════════════════════
    //  RENDER — mirrors SDV JunimoNoteMenu.draw() line by line
    // ═════════════════════════════════════════════════════════════════════════

    @Override
    public void renderBackground(@Nonnull GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        // No-op — we handle background dimming in render() to avoid double overlay
        // (super.render() calls renderBackground() internally)
    }

    @Override
    public void render(@Nonnull GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        // Detect areaId sync from DataSlot and rebuild
        int currentArea = this.menu.getAreaId();
        int currentVersion = BundleClientData.INSTANCE.getVersion();
        if (currentArea != lastSyncedAreaId || currentVersion != lastDataVersion) {
            lastSyncedAreaId = currentArea;
            lastDataVersion = currentVersion;
            buildBundleOrbs();
            if (specificBundlePage) {
                refreshDetailPage();
            }
        }

        // Update animations
        long now = System.currentTimeMillis();
        float deltaMs = now - lastTickMs;
        lastTickMs = now;
        for (BundleOrb orb : bundleOrbs) {
            orb.tick(deltaMs);
        }
        // Present button animation: 4 frames, 70ms
        if (showPresentButton) {
            presentAnimTimer += deltaMs;
            if (presentAnimTimer >= 70) {
                presentAnimTimer = 0;
                presentAnimFrame = (presentAnimFrame + 1) % 4;
            }
        }

        // SDV-style flat 50% black overlay (dimming the game world)
        g.fill(0, 0, this.width, this.height, 0x80000000);

        if (!specificBundlePage) {
            renderRoomOverview(g, mouseX, mouseY, partialTick);
        } else {
            renderBundleDetail(g, mouseX, mouseY, partialTick);
        }
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        // Handled in render() directly
    }

    @Override
    protected void renderLabels(@Nonnull GuiGraphics g, int mouseX, int mouseY) {
        // Don't render default labels
    }

    // ── Room Overview Page ──
    // SDV draw() lines: background → room name → bundle orbs → present button → temp sprites → arrows

    private void renderRoomOverview(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        int areaId = this.menu.getAreaId();

        // 1. Room overview background: noteTexture (0, 0, 320, 180) scale ×4
        g.pose().pushPose();
        g.pose().translate(menuX, menuY, 0.1f);
        g.pose().scale(s4, s4, 1.0f);
        g.blit(JUNIMO_NOTE, 0, 0, 0, 0, BASE_W, BASE_H, TEX_WIDTH, TEX_HEIGHT);
        g.pose().popPose();

        // 3. Room name centered
        // SDV: SpriteText.drawStringHorizontallyCenteredAt(b, areaName, xPos + width/2 + 16, yPos + 12, ...)
        // width = 1280, so xPos + 640 + 16 = xPos + 656
        String areaName = BundleDataManager.getAreaName(areaId);
        if (areaName == null) areaName = "???";
        String displayKey = BundleDataManager.getAreaDisplayNameKey(areaId);
        Component displayName = (displayKey != null)
                ? Component.translatable(displayKey)
                : Component.literal(areaName);

        int titleX = menuX + mapping.ui(656);
        int titleY = menuY + mapping.ui(12);
        g.drawCenteredString(this.font, displayName, titleX, titleY, 0xFFFFFFFF);

        // 5. Bundle orbs
        for (BundleOrb orb : bundleOrbs) {
            orb.draw(g, s4, mouseX, mouseY);
        }

        // 6. Present button (gift icon animation) — SDV: (548, 262, 18, 20), 4 frames, 70ms
        // SDV position: (xPos + 592, yPos + 512, 72, 72)
        if (showPresentButton) {
            int presX = menuX + mapping.ui(592);
            int presY = menuY + mapping.ui(512);
            int presU = 548 + presentAnimFrame * 18;
            int presV = 262;
            drawJunimoNote(g, presX, presY, presU, presV, 18, 20, s4);
        }

        // 7. Bottom reward text — SDV: SpriteText.drawStringWithScrollCenteredAt(b, reward, xPos+640, yPos+740)
        String rewardKey = getRewardNameForArea(areaId);
        if (!rewardKey.isEmpty()) {
            int rewardX = menuX + mapping.ui(640);
            int rewardY = Math.min(menuY + mapping.ui(740),
                    this.height - mapping.ui(72));
            g.drawCenteredString(this.font, Component.translatable(rewardKey), rewardX, rewardY, 0xFFFFFFFF);
        }

        // Hover text for bundle orbs
        for (BundleOrb orb : bundleOrbs) {
            if (orb.isHovered(mouseX, mouseY, s4)) {
                String progress = orb.filledCount + "/" + orb.requiredCount;
                Component tooltip = Component.translatable(orb.def.displayNameKey())
                        .append(Component.literal(" (" + progress + ")"));
                g.renderTooltip(this.font, tooltip, mouseX, mouseY);
            }
        }

        // 10. Close button (upper right X) — handled by MC's AbstractContainerScreen
    }

    // ── Bundle Detail Page ──
    // SDV draw() lines: detail bg → big icon → name label → back button → purchase button →
    //   temp sprites → ingredient slots → ingredient list → inventory → reward text → held item

    private void renderBundleDetail(GuiGraphics g, int mouseX, int mouseY, float partialTick) {

        // 1. Detail page background: noteTexture (320, 0, 320, 180) scale ×4
        g.pose().pushPose();
        g.pose().translate(menuX, menuY, 0.1f);
        g.pose().scale(s4, s4, 1.0f);
        g.blit(JUNIMO_NOTE, 0, 0, 320, 0, BASE_W, BASE_H, TEX_WIDTH, TEX_HEIGHT);
        g.pose().popPose();

        BundleDefinition def = BundleDataManager.getBundle(selectedBundleId);
        if (def == null) return;

        // 2. Bundle detail icon (32×32)
        // SDV: b.Draw(noteTexture, new Vector2(xPos + 872, yPos + 88),
        //      new Rectangle(bundleIndex*16*2 % texWidth, 180 + 32*(bundleIndex*16*2/texWidth), 32, 32),
        //      Color.White, 0, Zero, 4f, None, 0.15f)
        int iconIdx = def.bundleId();
        int iconU = (iconIdx * 32) % TEX_WIDTH;
        int iconV = 180 + 32 * ((iconIdx * 32) / TEX_WIDTH);
        int iconX = menuX + mapping.ui(872);
        int iconY = menuY + mapping.ui(88);

        g.pose().pushPose();
        g.pose().translate(iconX, iconY, 0.15f);
        g.pose().scale(s4, s4, 1.0f);
        g.blit(JUNIMO_NOTE, 0, 0, iconU, iconV, 32, 32, TEX_WIDTH, TEX_HEIGHT);
        g.pose().popPose();

        // 2b. Reward item preview (SDV: shown to the right of bundle icon)
        ItemStack rewardStack = BundleClaimRewardPayload.parseRewardString(def.rewardString());
        if (!rewardStack.isEmpty()) {
            int rewardIconX = menuX + mapping.ui(1000);
            int rewardIconY = menuY + mapping.ui(88);
            g.pose().pushPose();
            g.pose().translate(rewardIconX, rewardIconY, 0.15f);
            g.pose().scale(s4 * 0.75f, s4 * 0.75f, 1.0f);
            g.renderItem(rewardStack, 0, 0);
            g.renderItemDecorations(this.font, rewardStack, 0, 0);
            g.pose().popPose();
        }

        // 3. Bundle name label (three-part horizontal bar + text with 3-layer shadow)
        // SDV: text measured with dialogueFont, bar centered at xPos + 936
        Component bundleName = Component.translatable(def.displayNameKey());
        int textWidth = this.font.width(bundleName);

        int barCenterX = menuX + mapping.ui(936);
        int barY = menuY + mapping.ui(228);

        // Left end: (517, 266, 4, 17) ×4
        int barLeftX = barCenterX - textWidth / 2 - mapping.ui(16);
        drawJunimoNote(g, barLeftX, barY, 517, 266, 4, 17, s4);

        // Middle stretch: (520, 266, 1, 17) stretched to text width
        int barMiddleX = barLeftX + mapping.ui(16);
        g.pose().pushPose();
        g.pose().translate(barMiddleX, barY, 0.1f);
        g.pose().scale(s4, s4, 1.0f);
        // Stretch 1px wide to cover text width
        int stretchPx = Math.max(1, (int) (textWidth / s4));
        g.blit(JUNIMO_NOTE, 0, 0, stretchPx, 17, 520, 266, 1, 17, TEX_WIDTH, TEX_HEIGHT);
        g.pose().popPose();

        // Right end: (524, 266, 4, 17) ×4
        int barRightX = barCenterX + textWidth / 2;
        drawJunimoNote(g, barRightX, barY, 524, 266, 4, 17, s4);

        // Text with 3-layer shadow (SDV: textShadowColor = 0x3B3022, textColor * 0.9f = ~0x5B5045)
        int textX = barCenterX - textWidth / 2;
        int textY = barY + mapping.ui(8);
        int shadowColor = 0xFF3B3022;
        int textColor = 0xFF5B5045;
        int shadowOff = Math.max(1, mapping.ui(2));

        // SDV shadow offsets: (2,2), (0,2), (2,0) in screen pixels
        g.drawString(this.font, bundleName, textX + shadowOff, textY + shadowOff, shadowColor, false);
        g.drawString(this.font, bundleName, textX, textY + shadowOff, shadowColor, false);
        g.drawString(this.font, bundleName, textX + shadowOff, textY, shadowColor, false);
        g.drawString(this.font, bundleName, textX, textY, textColor, false);

        // 4. Back button (Cursors tile #44)
        // SDV: xPos + borderWidth*2 + 8, yPos + borderWidth*2 + 4 (borderWidth=32)
        // getSourceRectForStandardTileSheet(mouseCursors, 44): 44*64%704=0, 44*64/704*64=256 → (0,256,64,64)
        // SDV scale = 0.75f → 64*0.75=48 screen px; MC: poseScale = 0.75/guiScale = s4*3/16
        int backX = menuX + mapping.ui(72);
        int backY = menuY + mapping.ui(68);
        StardewGuiUtil.drawFromCursors(g, backX, backY, 0, 256, 64, 64, s4 * 3.0f / 16.0f);

        // 5. Purchase button (Vault only)
        if (def.isVaultBundle() && !BundleClientData.INSTANCE.isBundleComplete(def.bundleId())) {
            int purchX = menuX + mapping.ui(800);
            int purchY = menuY + mapping.ui(504);
            drawJunimoNote(g, purchX, purchY, 517, 286, 65, 20, s4);
        }

        // 7. Ingredient slots (submission slots)
        for (IngredientSlotVisual slot : ingredientSlots) {
            slot.draw(g, s4, mouseX, mouseY, this);
        }

        // 8. Ingredient list (required items display)
        for (int i = 0; i < ingredientList.size(); i++) {
            IngredientListEntry entry = ingredientList.get(i);
            entry.draw(g, s4, this.font, this);
        }

        // 9. Player inventory (MC handles via super)
        super.render(g, mouseX, mouseY, partialTick);

        // 10. Bottom reward text
        String rewardKey = getRewardNameForArea(this.menu.getAreaId());
        if (!rewardKey.isEmpty()) {
            int rewardX = menuX + mapping.ui(640);
            int rewardY = Math.min(menuY + mapping.ui(740),
                    this.height - mapping.ui(72));
            g.drawCenteredString(this.font, Component.translatable(rewardKey), rewardX, rewardY, 0xFFFFFFFF);
        }

        // 12. Held item follows mouse — MC handles this automatically

        // 13. Tooltip
        // Reward item hover tooltip
        if (!rewardStack.isEmpty()) {
            int rewardIconX = menuX + mapping.ui(1000);
            int rewardIconY = menuY + mapping.ui(88);
            int rewardSize = (int)(16 * s4 * 0.75f);
            if (mouseX >= rewardIconX && mouseX <= rewardIconX + rewardSize
                    && mouseY >= rewardIconY && mouseY <= rewardIconY + rewardSize) {
                Component rewardLabel = Component.translatable("stardewcraft.cc.reward")
                        .append(Component.literal(": "))
                        .append(rewardStack.getHoverName())
                        .append(Component.literal(" ×" + rewardStack.getCount()));
                g.renderTooltip(this.font, rewardLabel, mouseX, mouseY);
            }
        }
        // Ingredient list hover
        for (IngredientListEntry entry : ingredientList) {
            if (entry.isHovered(mouseX, mouseY)) {
                g.renderTooltip(this.font, entry.displayName, mouseX, mouseY);
            }
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  CLICK HANDLING
    // ═════════════════════════════════════════════════════════════════════════

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (!specificBundlePage) {
                // Room overview: click bundle orb → open detail
                for (BundleOrb orb : bundleOrbs) {
                    if (orb.isHovered((int) mouseX, (int) mouseY, s4) && !orb.complete) {
                        openBundleDetail(orb.def);
                        return true;
                    }
                }

                // Present button click → claim rewards
                if (showPresentButton) {
                    int presX = menuX + mapping.ui(592);
                    int presY = menuY + mapping.ui(512);
                    int presSize = mapping.ui(72);
                    if (mouseX >= presX && mouseX <= presX + presSize
                            && mouseY >= presY && mouseY <= presY + presSize) {
                        // Claim rewards for all completed bundles in this area
                        BundleClientData cd = BundleClientData.INSTANCE;
                        for (BundleDefinition bd : BundleDataManager.getBundlesForArea(this.menu.getAreaId())) {
                            if (cd.isRewardAvailable(bd.bundleId())) {
                                PacketDistributor.sendToServer(new BundleClaimRewardPayload(bd.bundleId()));
                            }
                        }
                        playSound(ModSounds.PURCHASE_CLICK);
                        return true;
                    }
                }
            } else {
                BundleDefinition def = BundleDataManager.getBundle(selectedBundleId);
                if (def == null) return false;

                // Detail page: click back button → return to overview
                // SDV: 64x64 source at 0.75 scale = 48x48 screen px hit area
                int backX = menuX + mapping.ui(72);
                int backY = menuY + mapping.ui(68);
                int backSize = mapping.ui(48);
                if (mouseX >= backX && mouseX <= backX + backSize
                        && mouseY >= backY && mouseY <= backY + backSize) {
                    closeBundleDetail();
                    return true;
                }

                // Purchase button click (Vault only)
                if (def.isVaultBundle() && !BundleClientData.INSTANCE.isBundleComplete(def.bundleId())) {
                    int purchX = menuX + mapping.ui(800);
                    int purchY = menuY + mapping.ui(504);
                    int purchW = mapping.ui(260);
                    int purchH = mapping.ui(80);  // 20 sprite px * 4x = 80 screen px
                    if (mouseX >= purchX && mouseX <= purchX + purchW
                            && mouseY >= purchY && mouseY <= purchY + purchH) {
                        PacketDistributor.sendToServer(new BundlePurchasePayload(def.bundleId()));
                        playSound(ModSounds.PURCHASE_CLICK);
                        return true;
                    }
                }

                // Ingredient slot click — deposit held item
                ItemStack carried = this.menu.getCarried();
                if (!carried.isEmpty() && !def.isVaultBundle()) {
                    for (int i = 0; i < ingredientSlots.size(); i++) {
                        IngredientSlotVisual slot = ingredientSlots.get(i);
                        if (slot.isHovered((int) mouseX, (int) mouseY)) {
                            if (!slot.filled) {
                                int matchIdx = findMatchingIngredientIndex(def, carried);
                                if (matchIdx >= 0) {
                                    PacketDistributor.sendToServer(
                                            new BundleDepositPayload(def.bundleId(), matchIdx));
                                    playSound(ModSounds.COIN);
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }

        if (button == 1 && !specificBundlePage) {
            // Right-click on overview → close menu (SDV behavior)
            this.onClose();
            return true;
        }

        // Only pass clicks to AbstractContainerScreen on detail page (inventory interaction)
        return specificBundlePage ? super.mouseClicked(mouseX, mouseY, button) : false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // ESC on detail page → back to overview (not close)
        if (keyCode == 256 && specificBundlePage) { // 256 = GLFW_KEY_ESCAPE
            closeBundleDetail();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void openBundleDetail(BundleDefinition def) {
        this.specificBundlePage = true;
        this.selectedBundleId = def.bundleId();
        buildIngredientSlots(def);
        buildIngredientList(def);
        playSound(ModSounds.SHWIP);
    }

    private void closeBundleDetail() {
        this.specificBundlePage = false;
        this.selectedBundleId = -1;
        this.ingredientSlots.clear();
        this.ingredientList.clear();
        // Rebuild orbs to refresh completion state
        buildBundleOrbs();
        playSound(ModSounds.SHWIP);
    }

    private void refreshDetailPage() {
        if (!specificBundlePage || selectedBundleId < 0) return;
        BundleDefinition def = BundleDataManager.getBundle(selectedBundleId);
        if (def == null) return;
        buildIngredientSlots(def);
        buildIngredientList(def);
    }

    /**
     * Find the ingredient index that matches the given item.
     */
    private int findMatchingIngredientIndex(BundleDefinition def, ItemStack stack) {
        BundleClientData cd = BundleClientData.INSTANCE;
        List<BundleIngredient> ingredients = def.ingredients();
        for (int i = 0; i < ingredients.size(); i++) {
            if (cd.isSlotComplete(def.bundleId(), i)) continue;
            if (BundleMenu.isValidItem(stack, ingredients.get(i))) {
                return i;
            }
        }
        return -1;
    }

    private String getRewardNameForArea(int areaId) {
        return switch (areaId) {
            case 0 -> "stardewcraft.cc.reward.pantry";
            case 1 -> "stardewcraft.cc.reward.crafts_room";
            case 2 -> "stardewcraft.cc.reward.fish_tank";
            case 3 -> "stardewcraft.cc.reward.boiler_room";
            case 4 -> "stardewcraft.cc.reward.vault";
            case 5 -> "stardewcraft.cc.reward.bulletin_board";
            default -> "";
        };
    }

    private void playSound(net.neoforged.neoforge.registries.DeferredHolder<net.minecraft.sounds.SoundEvent, net.minecraft.sounds.SoundEvent> sound) {
        if (this.minecraft != null && this.minecraft.getSoundManager() != null) {
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(sound.get(), 1.0f));
        }
    }

    /**
     * Check if the carried item can be deposited into any empty slot of the current bundle.
     * Used by IngredientSlotVisual to decide hover highlight.
     */
    boolean canAcceptDeposit() {
        ItemStack carried = this.menu.getCarried();
        if (carried.isEmpty() || selectedBundleId < 0) return false;
        BundleDefinition def = BundleDataManager.getBundle(selectedBundleId);
        if (def == null || def.isVaultBundle()) return false;
        return findMatchingIngredientIndex(def, carried) >= 0;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  LAYOUT — mirrors SDV addRectangleRowsToList / createRowOfBoxesCenteredAt
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Build ingredient submission slots.
     * SDV: addRectangleRowsToList(slots, numberOfIngredientSlots, 932, 540)
     * Each slot is 72×72 with 12px gap.
     */
    private void buildIngredientSlots(BundleDefinition def) {
        ingredientSlots.clear();
        if (def.isVaultBundle()) return;

        int numSlots = def.requiredCount();
        List<int[]> positions = layoutRows(numSlots, 932, 540);
        BundleClientData cd = BundleClientData.INSTANCE;

        for (int i = 0; i < positions.size(); i++) {
            int[] pos = positions.get(i);
            boolean filled = cd.isSlotComplete(def.bundleId(), i);

            // SDV: filled slots show the deposited item icon on top
            ItemStack depositedItem = ItemStack.EMPTY;
            if (filled && i < def.ingredients().size()) {
                BundleIngredient ing = def.ingredients().get(i);
                if (ing.itemId() != null) {
                    depositedItem = BundleItemResolver.resolveItemStack(ing.itemId());
                    if (!depositedItem.isEmpty()) {
                        depositedItem.setCount(ing.stack());
                    }
                }
            }

            ingredientSlots.add(new IngredientSlotVisual(
                    menuX + mapping.ui(pos[0]),
                    menuY + mapping.ui(pos[1]),
                    mapping.ui(72),
                    filled,
                    depositedItem
            ));
        }
    }

    /**
     * Build ingredient requirement display list.
     * SDV: addRectangleRowsToList(list, ingredients.Count, 932, 364)
     */
    private void buildIngredientList(BundleDefinition def) {
        ingredientList.clear();
        if (def.isVaultBundle()) return;

        List<BundleIngredient> ingredients = def.ingredients();
        List<int[]> positions = layoutRows(ingredients.size(), 932, 364);
        BundleClientData cd = BundleClientData.INSTANCE;

        for (int i = 0; i < ingredients.size() && i < positions.size(); i++) {
            BundleIngredient ing = ingredients.get(i);
            int[] pos = positions.get(i);
            boolean completed = cd.isSlotComplete(def.bundleId(), i);

            ItemStack displayStack = ItemStack.EMPTY;
            if (ing.itemId() != null) {
                displayStack = BundleItemResolver.resolveItemStack(ing.itemId());
                if (!displayStack.isEmpty()) {
                    displayStack.setCount(ing.stack());
                }
            }

            Component name = displayStack.isEmpty()
                    ? Component.literal(ing.sdvId())
                    : displayStack.getHoverName();

            // Build rich tooltip: "Name ×count" + quality suffix if > 0
            Component tooltip = name.copy();
            if (ing.stack() > 1) {
                tooltip = tooltip.copy().append(Component.literal(" ×" + ing.stack()));
            }
            if (ing.quality() > 0) {
                tooltip = tooltip.copy().append(Component.literal(" ("))
                        .append(QualityHelper.getQualityName(ing.quality()))
                        .append(Component.literal("+)"));
            }

            ingredientList.add(new IngredientListEntry(
                    menuX + mapping.ui(pos[0]),
                    menuY + mapping.ui(pos[1]),
                    mapping.ui(72),
                    displayStack,
                    tooltip,
                    completed,
                    ing.quality()
            ));
        }
    }

    /**
     * Mirrors SDV addRectangleRowsToList + createRowOfBoxesCenteredAt.
     * Returns SDV screen-pixel positions relative to (0,0).
     */
    private List<int[]> layoutRows(int numItems, int centerX, int centerY) {
        List<int[]> result = new ArrayList<>();
        int boxW = 72, gap = 12;

        switch (numItems) {
            case 1 -> addRow(result, centerX, centerY, 1, boxW, gap);
            case 2 -> addRow(result, centerX, centerY, 2, boxW, gap);
            case 3 -> addRow(result, centerX, centerY, 3, boxW, gap);
            case 4 -> addRow(result, centerX, centerY, 4, boxW, gap);
            case 5 -> {
                addRow(result, centerX, centerY - 36, 3, boxW, gap);
                addRow(result, centerX, centerY + 40, 2, boxW, gap);
            }
            case 6 -> {
                addRow(result, centerX, centerY - 36, 3, boxW, gap);
                addRow(result, centerX, centerY + 40, 3, boxW, gap);
            }
            case 7 -> {
                addRow(result, centerX, centerY - 36, 4, boxW, gap);
                addRow(result, centerX, centerY + 40, 3, boxW, gap);
            }
            case 8 -> {
                addRow(result, centerX, centerY - 36, 4, boxW, gap);
                addRow(result, centerX, centerY + 40, 4, boxW, gap);
            }
            case 9 -> {
                addRow(result, centerX, centerY - 36, 5, boxW, gap);
                addRow(result, centerX, centerY + 40, 4, boxW, gap);
            }
            case 10 -> {
                addRow(result, centerX, centerY - 36, 5, boxW, gap);
                addRow(result, centerX, centerY + 40, 5, boxW, gap);
            }
            case 11 -> {
                addRow(result, centerX, centerY - 36, 6, boxW, gap);
                addRow(result, centerX, centerY + 40, 5, boxW, gap);
            }
            case 12 -> {
                addRow(result, centerX, centerY - 36, 6, boxW, gap);
                addRow(result, centerX, centerY + 40, 6, boxW, gap);
            }
        }
        return result;
    }

    private void addRow(List<int[]> result, int centerX, int centerY, int count, int boxW, int gap) {
        // SDV: createRowOfBoxesCenteredAt
        int totalW = count * (boxW + gap);
        int startX = centerX - totalW / 2;
        int startY = centerY - boxW / 2;
        for (int i = 0; i < count; i++) {
            result.add(new int[]{startX + i * (boxW + gap), startY});
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  HELPER — draw from JunimoNote texture
    // ═════════════════════════════════════════════════════════════════════════

    private void drawJunimoNote(GuiGraphics g, int x, int y, int u, int v, int w, int h, float scale) {
        g.pose().pushPose();
        g.pose().translate(x, y, 0);
        g.pose().scale(scale, scale, 1.0f);
        g.blit(JUNIMO_NOTE, 0, 0, u, v, w, h, TEX_WIDTH, TEX_HEIGHT);
        g.pose().popPose();
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  INNER CLASSES — Bundle orb, ingredient slot, ingredient list entry
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * A bundle orb on the room overview page.
     * Mirrors SDV Bundle's TemporaryAnimatedSprite for the colored ball.
     */
    private static class BundleOrb {
        final BundleDefinition def;
        final int x, y;       // GUI coords
        final int srcX, srcY; // texture source origin
        boolean complete;
        final int filledCount;
        final int requiredCount;

        // Animation state
        int animFrame = 1;     // SDV starts at frame 1 (paused after reset)
        float animTimer = 0;
        boolean pingPong = true;
        boolean animForward = true;
        boolean animPaused = true;
        boolean hovered = false;

        // Shake state
        float rotation = 0;
        float maxShake = 0;
        boolean shakeLeft = false;

        BundleOrb(BundleDefinition def, int x, int y, int srcX, int srcY, boolean complete, int filledCount) {
            this.def = def;
            this.x = x;
            this.y = y;
            this.srcX = srcX;
            this.srcY = srcY;
            this.complete = complete;
            this.filledCount = filledCount;
            this.requiredCount = def.requiredCount();

            if (complete) {
                // Show completion frame (last frame of 15-frame animation)
                animFrame = 14;
                animPaused = true;
            }
        }

        void tick(float deltaMs) {
            // Animate when not paused (hovered)
            if (!animPaused) {
                animTimer += deltaMs;
                if (animTimer >= 70) { // 70ms per frame (SDV)
                    animTimer = 0;
                    if (pingPong) {
                        if (animForward) {
                            animFrame++;
                            if (animFrame >= 2) { animForward = false; }
                        } else {
                            animFrame--;
                            if (animFrame <= 0) { animForward = true; }
                        }
                    }
                }
            }

            // Shake decay
            if (maxShake > 0) {
                float shakeRate = (float) Math.PI / 200f;
                rotation += (shakeLeft ? -1 : 1) * shakeRate;
                if (Math.abs(rotation) >= maxShake) {
                    shakeLeft = !shakeLeft;
                }
                maxShake = Math.max(0, maxShake - 0.0007669904f * deltaMs / 16.67f);
                if (maxShake <= 0) {
                    rotation = 0;
                }
            }

            // Random shake (0.5% chance per tick) — SDV: unconditional
            if (Math.random() < 0.005) {
                maxShake = (float) Math.PI * 3f / 128f;
            }
        }

        void draw(GuiGraphics g, float s4, int mouseX, int mouseY) {
            // Check hover
            boolean wasHovered = hovered;
            hovered = isHovered(mouseX, mouseY, s4) && !complete;

            if (hovered && !wasHovered) {
                animPaused = false;
                animForward = true;
            } else if (!hovered && wasHovered && !complete) {
                // Reset to idle
                animFrame = 1;
                animPaused = true;
            }

            // Source rect: base + frame * 16px
            int frameU = srcX + animFrame * 16;
            int frameV = srcY;

            g.pose().pushPose();
            g.pose().translate(x + 8 * s4, y + 8 * s4, 0.8f);
            if (rotation != 0) {
                g.pose().mulPose(com.mojang.math.Axis.ZP.rotation(rotation));
            }
            g.pose().scale(s4, s4, 1.0f);
            g.blit(JUNIMO_NOTE, -8, -8, frameU, frameV, 16, 16, TEX_WIDTH, TEX_HEIGHT);
            g.pose().popPose();
        }

        boolean isHovered(int mouseX, int mouseY, float s4) {
            // SDV bounds: (position.X, position.Y, 64, 64) in screen pixels
            float hitSize = 16 * s4; // 64 screen px → GUI units
            return mouseX >= x && mouseX <= x + hitSize
                    && mouseY >= y && mouseY <= y + hitSize;
        }
    }

    /**
     * Visual representation of an ingredient submission slot.
     * Mirrors SDV's ClickableTextureComponent for ingredient slots.
     *
     * SDV behavior:
     * - Empty unfocused: (512, 244, 18, 18) at alpha 0.5
     * - Empty hovered + holding matching item: (512, 244, 18, 18) at alpha 1.0
     * - Filled: (620, 244, 18, 18) at alpha 1.0 + item icon on top offset (1,1) sprite px
     */
    private static class IngredientSlotVisual {
        final int x, y, size;
        final boolean filled;
        @javax.annotation.Nullable final ItemStack depositedItem;

        IngredientSlotVisual(int x, int y, int size, boolean filled,
                             @javax.annotation.Nullable ItemStack depositedItem) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.filled = filled;
            this.depositedItem = depositedItem;
        }

        void draw(GuiGraphics g, float s4, int mouseX, int mouseY, BundleScreen screen) {
            int u, v;
            float alpha = 1.0f;

            if (filled) {
                // SDV: filled terminal slot — last frame of 6-frame deposit animation
                u = 530 + 5 * 18;  // = 620
                v = 244;
            } else {
                // SDV: empty slot (512, 244, 18, 18)
                u = 512;
                v = 244;
                // SDV: unfocused empty slots drawn at 0.5 alpha;
                // hovered + holding matching item → full alpha
                if (isHovered(mouseX, mouseY) && screen.canAcceptDeposit()) {
                    alpha = 1.0f;
                } else {
                    alpha = 0.5f;
                }
            }

            if (alpha < 1.0f) g.setColor(1, 1, 1, alpha);

            g.pose().pushPose();
            g.pose().translate(x, y, 0.89f);
            g.pose().scale(s4, s4, 1.0f);
            g.blit(JUNIMO_NOTE, 0, 0, u, v, 18, 18, TEX_WIDTH, TEX_HEIGHT);
            g.pose().popPose();

            if (alpha < 1.0f) g.setColor(1, 1, 1, 1);

            // SDV: draw deposited item icon on top of filled slot
            // Offset by (1, 1) sprite px = 4 screen px to center 16px item in 18px slot
            if (filled && depositedItem != null && !depositedItem.isEmpty()) {
                g.pose().pushPose();
                g.pose().translate(x, y, 0.9f);
                g.pose().scale(s4, s4, 1.0f);
                g.renderItem(depositedItem, 1, 1);
                g.renderItemDecorations(screen.font, depositedItem, 1, 1);
                g.pose().popPose();
            }
        }

        boolean isHovered(int mouseX, int mouseY) {
            return mouseX >= x && mouseX <= x + size
                    && mouseY >= y && mouseY <= y + size;
        }
    }

    /**
     * Visual representation of a required ingredient in the list.
     * Shows the item icon + name tooltip + completion state.
     */
    private static class IngredientListEntry {
        final int x, y, size;
        final ItemStack displayStack;
        final Component displayName;
        final boolean completed;
        final int requiredQuality;

        IngredientListEntry(int x, int y, int size, ItemStack displayStack, Component displayName,
                            boolean completed, int requiredQuality) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.displayStack = displayStack;
            this.displayName = displayName;
            this.completed = completed;
            this.requiredQuality = requiredQuality;
        }

        void draw(GuiGraphics g, float s4, net.minecraft.client.gui.Font font, BundleScreen screen) {
            if (displayStack.isEmpty()) return;

            // SDV: completed items drawn at alpha 0.25
            if (completed) {
                g.setColor(1, 1, 1, 0.25f);
            }

            // SDV: item drawn at slot position + (4,4) screen px offset = (1,1) sprite px centering
            // 16px item centered in 18px display box
            g.pose().pushPose();
            g.pose().translate(x, y, 0.9f);
            g.pose().scale(s4, s4, 1.0f);
            g.renderItem(displayStack, 1, 1);
            g.renderItemDecorations(font, displayStack, 1, 1);
            g.pose().popPose();

            // Quality star indicator (SDV: small colored star below item icon)
            if (requiredQuality > 0 && !completed) {
                int starColor = switch (requiredQuality) {
                    case 1 -> 0xFFC0C0C0; // Silver
                    case 2 -> 0xFFFFD700; // Gold
                    case 3 -> 0xFFBF5FFF; // Iridium purple
                    default -> 0xFFFFFFFF;
                };
                String starStr = "★";
                int starX = x + (int)(12 * s4);
                int starY = y + (int)(14 * s4);
                g.drawString(font, starStr, starX, starY, starColor, false);
            }

            if (completed) {
                g.setColor(1, 1, 1, 1);
            }
        }

        boolean isHovered(int mouseX, int mouseY) {
            return mouseX >= x && mouseX <= x + size
                    && mouseY >= y && mouseY <= y + size;
        }
    }
}
