package com.stardew.craft.client.gui;

import com.stardew.craft.block.ModBlocks;
import com.stardew.craft.block.tv.TVChannelData;
import com.stardew.craft.client.gui.common.StardewConfirmDialogScreen;
import com.stardew.craft.client.gui.common.StardewQuestionDialogSpec;
import com.stardew.craft.client.gui.common.StardewRenderMapping;
import com.stardew.craft.client.gui.overnight.StardewGuiUtil;
import com.stardew.craft.client.render.TVScreenOverlayRenderer;
import com.stardew.craft.network.payload.OpenTVScreenPayload;
import com.stardew.craft.network.payload.TVRecipeUnlockPayload;
import com.stardew.craft.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.IntConsumer;

/**
 * Main TV screen — opens with a channel selection query dialog,
 * then displays content through Stardew-style dialogue boxes
 * while keeping the world running (isPauseScreen = false).
 * <p>
 * Flow mirrors original TV.cs:
 * 1. checkForAction → createQuestionDialogue (channel select)
 * 2. selectChannel → drawObjectDialogue (opening text) + screen animation
 * 3. proceedToNextScene → content text + overlay, then turnOffTV
 */
@OnlyIn(Dist.CLIENT)
@SuppressWarnings("null")
public class TVScreen extends Screen {

    private final OpenTVScreenPayload data;

    /** Which phase we're in. */
    private enum Phase {
        /** Waiting — the query dialog is open as a child screen */
        CHANNEL_SELECT,
        /** Showing opening text for the selected channel */
        OPENING,
        /** Showing main content (e.g. weather forecast, fortune, etc.) */
        CONTENT,
        /** Showing second page of multi-page content */
        CONTENT_PAGE2,
        /** Done — closing */
        CLOSING
    }

    private Phase phase = Phase.CHANNEL_SELECT;
    private int selectedChannel = 0;

    // Dialogue rendering state
    private StardewRenderMapping mapping;
    private String currentText = "";
    private int characterIndex;
    private int characterAdvanceTimer;
    private static final int CHAR_ADVANCE_MS = 30;
    private long lastUpdateMs;

    // Dialogue box layout
    private int boxX, boxY, boxWidth, boxHeight;

    // Transition animation
    private boolean transitioning = true;
    private boolean transitioningBigger = true;
    private boolean transitionInitialized;
    private int transitionX, transitionY, transitionWidth, transitionHeight;
    private boolean openingSoundPlayed;

    // Recipe unlock tracking
    private boolean recipeUnlockSent;

    public TVScreen(OpenTVScreenPayload data) {
        super(Component.literal("TV"));
        this.data = data;
    }

    @Override
    protected void init() {
        super.init();
        mapping = new StardewRenderMapping(this.width, this.height, guiScale());

        // init() is called every time setScreen(this) is invoked (including
        // when we return from the query dialog). Only open channel select
        // on the very first init; subsequent calls just recalc mapping.
        if (phase == Phase.CHANNEL_SELECT) {
            openChannelSelect();
        }
    }

    private float guiScale() {
        return this.minecraft == null ? 1.0f : (float) this.minecraft.getWindow().getGuiScale();
    }

    private int px(int stardewPixels) {
        return mapping == null ? Math.round(stardewPixels / guiScale()) : mapping.ui(stardewPixels);
    }

    // ==================== Channel Selection ====================

    private void openChannelSelect() {
        List<Component> responses = new ArrayList<>();
        List<Integer> channelIds = new ArrayList<>();

        // Weather — always
        responses.add(Component.translatable("stardewcraft.tv.channel.weather"));
        channelIds.add(TVChannelData.CHANNEL_WEATHER);

        // Fortune — always
        responses.add(Component.translatable("stardewcraft.tv.channel.fortune"));
        channelIds.add(TVChannelData.CHANNEL_FORTUNE);

        // Tips — Mon/Thu
        if (data.tipsAvailable()) {
            responses.add(Component.translatable("stardewcraft.tv.channel.tips"));
            channelIds.add(TVChannelData.CHANNEL_TIPS);
        }

        // Cooking — Sun/Wed
        if (data.cookingAvailable()) {
            String key = data.cookingIsRerun()
                    ? "stardewcraft.tv.channel.cooking_rerun"
                    : "stardewcraft.tv.channel.cooking";
            responses.add(Component.translatable(key));
            channelIds.add(TVChannelData.CHANNEL_COOKING);
        }

        // Fishing
        if (data.fishingAvailable()) {
            responses.add(Component.translatable("stardewcraft.tv.channel.fishing"));
            channelIds.add(TVChannelData.CHANNEL_FISHING);
        }

        // Cursed
        if (data.cursedAvailable()) {
            responses.add(Component.literal("???"));
            channelIds.add(TVChannelData.CHANNEL_CURSED);
        }

        // Leave
        responses.add(Component.translatable("stardewcraft.tv.channel.leave"));
        channelIds.add(-1);

        IntConsumer onAnswer = index -> {
            if (index < 0 || index >= channelIds.size()) {
                closeTV();
                return;
            }
            int channel = channelIds.get(index);
            if (channel == -1) {
                closeTV();
            } else {
                // Defer to next tick to avoid StardewConfirmDialogScreen's setScreen(null) override
                Minecraft.getInstance().tell(() -> selectChannel(channel));
            }
        };

        StardewQuestionDialogSpec spec = StardewQuestionDialogSpec.of(
                Component.translatable("stardewcraft.tv.select_channel"),
                responses,
                onAnswer,
                -1
        );

        // Open the query dialog on top of this screen
        Minecraft.getInstance().setScreen(
                StardewConfirmDialogScreen.createQuestionDialog(spec)
        );
    }

    // ==================== Channel Selection Handler ====================

    private void selectChannel(int channel) {
        this.selectedChannel = channel;
        this.phase = Phase.OPENING;
        this.transitioning = true;
        this.transitioningBigger = true;
        this.transitionInitialized = false;
        this.openingSoundPlayed = false;
        this.characterIndex = 0;
        this.characterAdvanceTimer = 0;
        this.lastUpdateMs = System.currentTimeMillis();
        this.recipeUnlockSent = false;

        switch (channel) {
            case TVChannelData.CHANNEL_WEATHER:
                setCurrentText(Component.translatable("stardewcraft.tv.weather.opening"));
                break;
            case TVChannelData.CHANNEL_FORTUNE:
                setCurrentText(getFortuneOpening());
                break;
            case TVChannelData.CHANNEL_TIPS:
                setCurrentText(Component.translatable("stardewcraft.tv.tips.opening"));
                break;
            case TVChannelData.CHANNEL_COOKING:
                setCurrentText(Component.translatable("stardewcraft.tv.cooking.opening"));
                break;
            case TVChannelData.CHANNEL_FISHING:
                setCurrentText(Component.translatable("stardewcraft.tv.fishing.opening"));
                break;
            case TVChannelData.CHANNEL_CURSED:
                setCurrentText(Component.literal("............................"));
                break;
            default:
                closeTV();
                return;
        }

        // Tell overlay renderer about this TV and selected channel
        BlockPos tvPos = new BlockPos(data.blockX(), data.blockY(), data.blockZ());
        boolean bigTV = false;
        if (Minecraft.getInstance().level != null) {
            Block block = Minecraft.getInstance().level.getBlockState(tvPos).getBlock();
            bigTV = (block == ModBlocks.TV_2.get());
        }
        TVScreenOverlayRenderer.setActiveTV(tvPos, bigTV);
        TVScreenOverlayRenderer.setChannel(channel, data.tomorrowWeather(), data.dailyLuck());

        // Re-set this screen as the active one (coming back from query dialog)
        Minecraft.getInstance().setScreen(this);
    }

    // ==================== Text Resolution ====================

    private void setCurrentText(Component component) {
        this.currentText = component.getString();
        this.characterIndex = 0;
        this.characterAdvanceTimer = 0;
    }

    private Component getFortuneOpening() {
        return switch (data.fortuneOpeningVariant()) {
            case 0 -> Component.translatable("stardewcraft.tv.fortune.opening_0");
            case 1 -> Component.translatable("stardewcraft.tv.fortune.opening_1");
            case 2 -> Component.translatable("stardewcraft.tv.fortune.opening_2");
            case 3 -> Component.translatable("stardewcraft.tv.fortune.opening_3");
            default -> Component.translatable("stardewcraft.tv.fortune.opening_4");
        };
    }

    private Component getWeatherForecast() {
        String weather = data.tomorrowWeather();
        Random rand = new Random();
        return switch (weather) {
            case "Snow" -> rand.nextBoolean()
                    ? Component.translatable("stardewcraft.tv.weather.snow_1")
                    : Component.translatable("stardewcraft.tv.weather.snow_2");
            case "Rain" -> Component.translatable("stardewcraft.tv.weather.rain");
            case "Storm" -> Component.translatable("stardewcraft.tv.weather.storm");
            case "WindSpring" -> Component.translatable("stardewcraft.tv.weather.wind_spring");
            case "WindFall" -> Component.translatable("stardewcraft.tv.weather.wind_fall");
            default -> rand.nextBoolean()
                    ? Component.translatable("stardewcraft.tv.weather.sun_1")
                    : Component.translatable("stardewcraft.tv.weather.sun_2");
        };
    }

    private Component getFortuneForecast() {
        double luck = data.dailyLuck();
        if (luck == -0.12) {
            return Component.translatable("stardewcraft.tv.fortune.worst");
        } else if (luck < -0.07) {
            return Component.translatable("stardewcraft.tv.fortune.bad_2");
        } else if (luck < -0.02) {
            return Component.translatable("stardewcraft.tv.fortune.bad_1");
        } else if (luck < 0.02) {
            // Random among 3 neutral variants — matches original TV.cs
            double d = new Random().nextDouble();
            if (d < 0.33) return Component.translatable("stardewcraft.tv.fortune.neutral_1");
            else if (d < 0.66) return Component.translatable("stardewcraft.tv.fortune.neutral_2");
            else return Component.translatable("stardewcraft.tv.fortune.neutral_3");
        } else if (luck < 0.07) {
            return Component.translatable("stardewcraft.tv.fortune.good_1");
        } else if (luck < 0.12) {
            return Component.translatable("stardewcraft.tv.fortune.good_2");
        } else {
            return Component.translatable("stardewcraft.tv.fortune.best");
        }
    }

    // ==================== Proceed to Next Scene ====================

    private void proceedToNextScene() {
        switch (selectedChannel) {
            case TVChannelData.CHANNEL_WEATHER:
                if (phase == Phase.OPENING) {
                    phase = Phase.CONTENT;
                    TVScreenOverlayRenderer.setContentPhase();
                    setCurrentText(getWeatherForecast());
                } else {
                    closeTV();
                }
                break;

            case TVChannelData.CHANNEL_FORTUNE:
                if (phase == Phase.OPENING) {
                    phase = Phase.CONTENT;
                    TVScreenOverlayRenderer.setContentPhase();
                    setCurrentText(getFortuneForecast());
                } else {
                    closeTV();
                }
                break;

            case TVChannelData.CHANNEL_TIPS:
                if (phase == Phase.OPENING) {
                    phase = Phase.CONTENT;
                    setCurrentText(Component.translatable(
                            "stardewcraft.tv.tip." + data.tipIndex()));
                } else {
                    closeTV();
                }
                break;

            case TVChannelData.CHANNEL_COOKING:
                if (phase == Phase.OPENING) {
                    phase = Phase.CONTENT;
                    setCurrentText(Component.translatable(
                            "stardewcraft.tv.cooking.desc." + data.cookingWeek()));
                } else if (phase == Phase.CONTENT) {
                    phase = Phase.CONTENT_PAGE2;
                    Component recipeName = Component.translatable(
                            "item.stardewcraft." + data.cookingRecipeId());
                    if (data.cookingAlreadyKnown()) {
                        setCurrentText(Component.translatable(
                                "stardewcraft.tv.cooking.already_known", recipeName));
                    } else {
                        setCurrentText(Component.translatable(
                                "stardewcraft.tv.cooking.learned", recipeName));
                        if (!recipeUnlockSent) {
                            recipeUnlockSent = true;
                            PacketDistributor.sendToServer(
                                    new TVRecipeUnlockPayload(data.cookingRecipeId()));
                        }
                    }
                } else {
                    closeTV();
                }
                break;

            case TVChannelData.CHANNEL_FISHING:
                if (phase == Phase.OPENING) {
                    phase = Phase.CONTENT;
                    setCurrentText(Component.translatable("stardewcraft.tv.fishing.content"));
                } else {
                    closeTV();
                }
                break;

            case TVChannelData.CHANNEL_CURSED:
                closeTV();
                break;

            default:
                closeTV();
                break;
        }
    }

    // ==================== Close ====================

    private void closeTV() {
        phase = Phase.CLOSING;
        TVScreenOverlayRenderer.clearActiveTV();
        if (this.minecraft != null) {
            this.minecraft.setScreen(null);
        }
    }

    @Override
    public void removed() {
        super.removed();
        // Only clear the world overlay when we're truly done (not switching to query dialog)
        if (phase == Phase.CLOSING) {
            TVScreenOverlayRenderer.clearActiveTV();
        }
    }

    // ==================== Rendering ====================

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (phase == Phase.CHANNEL_SELECT || phase == Phase.CLOSING) {
            return;
        }

        long now = System.currentTimeMillis();
        long delta = now - lastUpdateMs;
        lastUpdateMs = now;

        // Update character animation
        if (!transitioning) {
            characterAdvanceTimer += (int) delta;
            while (characterAdvanceTimer >= CHAR_ADVANCE_MS && characterIndex < currentText.length()) {
                characterAdvanceTimer -= CHAR_ADVANCE_MS;
                characterIndex++;
                // Play character sound occasionally
                if (characterIndex % 3 == 0) {
                    playUiSound(ModSounds.DIALOGUE_CHARACTER.get(), 0.2f, 1.0f);
                }
            }
        }

        // Update transition
        if (transitioning) {
            updateTransition(delta);
        }

        // Calculate dialogue box dimensions
        computeBoxLayout();

        if (transitioning) {
            // Draw transitioning box (expanding/shrinking)
            drawTransitionBox(graphics);
        } else {
            // Draw full dialogue box with text
            drawDialogueBox(graphics);
            drawDialogueText(graphics);
            drawContinueIcon(graphics);
        }
    }

    private void computeBoxLayout() {
        boxWidth = Math.min(px(1200), this.width - px(32));
        boxHeight = px(384);
        boxX = (this.width - boxWidth) / 2;
        boxY = this.height - boxHeight - px(64);
    }

    // ==================== Transition Animation ====================

    private void updateTransition(long deltaMs) {
        if (!transitionInitialized) {
            transitionInitialized = true;
            if (transitioningBigger) {
                transitionX = this.width / 2;
                transitionY = this.height - px(64) - px(192);
                transitionWidth = 0;
                transitionHeight = 0;
            }
        }

        if (!openingSoundPlayed && transitioningBigger) {
            openingSoundPlayed = true;
            playUiSound(ModSounds.BREATHIN.get(), 0.5f, 1.0f);
        }

        float speed = 3.0f;
        int expand = (int) (speed * deltaMs);

        if (transitioningBigger) {
            transitionWidth += expand;
            float aspect = boxHeight > 0 ? (float) boxHeight / boxWidth : 0.5f;
            transitionHeight = (int) (transitionWidth * aspect);
            transitionX = (this.width - transitionWidth) / 2;
            transitionY = this.height - transitionHeight - px(64);

            if (transitionWidth >= boxWidth) {
                transitioning = false;
            }
        }
    }

    private void drawTransitionBox(GuiGraphics graphics) {
        if (transitionWidth > 8 && transitionHeight > 8) {
            StardewGuiUtil.drawTextureBox(graphics, transitionX, transitionY, transitionWidth, transitionHeight);
        }
    }

    // ==================== Dialogue Box ====================

    private void drawDialogueBox(GuiGraphics graphics) {
        StardewGuiUtil.drawTextureBox(graphics, boxX, boxY, boxWidth, boxHeight);
    }

    private void drawDialogueText(GuiGraphics graphics) {
        if (currentText == null || currentText.isEmpty()) return;

        String visibleText = currentText.substring(0, Math.min(characterIndex, currentText.length()));
        int textX = boxX + px(32);
        int textY = boxY + px(32);
        int wrapWidth = boxWidth - px(64);

        // Word-wrap and render
        List<net.minecraft.util.FormattedCharSequence> lines =
                this.font.split(Component.literal(visibleText), wrapWidth);
        for (int i = 0; i < lines.size(); i++) {
            graphics.drawString(this.font, lines.get(i), textX, textY + i * (this.font.lineHeight + 2), 0x5B2812, false);
        }
    }

    private void drawContinueIcon(GuiGraphics graphics) {
        // Only show continue icon when text is fully displayed
        if (characterIndex < currentText.length()) return;

        // Draw a small animated arrow at bottom-right of dialogue box
        long time = System.currentTimeMillis();
        int frame = (int) ((time / 100) % 6);
        int iconX = boxX + boxWidth - px(48);
        int iconY = boxY + boxHeight - px(48);
        float s4 = mapping.s4();

        // Triangle arrow from cursors: (232+frame*9, 346, 9, 9)
        StardewGuiUtil.drawFromCursors(graphics, iconX, iconY,
                232 + frame * 9, 346, 9, 9, s4);
    }

    // ==================== Input ====================

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (phase == Phase.CHANNEL_SELECT || phase == Phase.CLOSING) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        if (transitioning) return true;

        if (characterIndex < currentText.length()) {
            // Skip text animation
            characterIndex = currentText.length();
            return true;
        }

        // Text fully shown — proceed
        playUiSound(ModSounds.SMALL_SELECT.get(), 0.5f, 1.0f);
        proceedToNextScene();
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (phase == Phase.CHANNEL_SELECT || phase == Phase.CLOSING) {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        // Escape closes TV
        if (keyCode == 256) { // GLFW_KEY_ESCAPE
            closeTV();
            return true;
        }

        // Enter/Space to proceed
        if (keyCode == 257 || keyCode == 32 || keyCode == 335) {
            if (transitioning) return true;
            if (characterIndex < currentText.length()) {
                characterIndex = currentText.length();
            } else {
                playUiSound(ModSounds.SMALL_SELECT.get(), 0.5f, 1.0f);
                proceedToNextScene();
            }
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    // ==================== Screen Properties ====================

    @Override
    public boolean isPauseScreen() {
        // World keeps running (blocks/animations continue)
        // but the Stardew time system should be paused via gamerule
        return false;
    }

    private void playUiSound(SoundEvent sound, float volume, float pitch) {
        if (this.minecraft != null) {
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(sound, pitch, volume));
        }
    }
}
