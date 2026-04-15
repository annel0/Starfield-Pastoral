package com.stardew.craft.client.gui.overnight;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.client.ClientPlayerDataCache;
import com.stardew.craft.network.overnight.ClientOvernightHandler;
import com.stardew.craft.network.overnight.OvernightSettlementPayload;
import com.stardew.craft.network.payload.OvernightProfessionChoicePayload;
import com.stardew.craft.player.ProfessionType;
import com.stardew.craft.player.SkillLevelRecipeUnlocks;
import com.stardew.craft.player.SkillType;
import com.stardew.craft.player.StardewCraftingRecipeData;
import com.stardew.craft.player.UnlockSourceData;
import com.stardew.craft.sound.ModSounds;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("null")
public class LevelUpMenuScreen extends Screen {

    private static final ResourceLocation TEX_ICON_FARMING = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/overnight/icon_farming.png");
    private static final ResourceLocation TEX_ICON_FISHING = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/overnight/icon_fishing.png");
    private static final ResourceLocation TEX_ICON_FORAGING = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/overnight/icon_foraging.png");
    private static final ResourceLocation TEX_ICON_MINING = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/overnight/icon_mining.png");
    private static final ResourceLocation TEX_ICON_OTHER = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/overnight/icon_other.png");
    private static final ResourceLocation TEX_LITTLE_STAR = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/overnight/little_star.png");

    private final List<Screen> siblingScreens;

    // Stardew variables
    private boolean isProfessionChooser;
    private int currentLevel;
    private int currentSkill;
    private final List<LittleStar> littleStars = new ArrayList<>();

    private static final int BORDER_WIDTH = 40;
    private static final int SPACE_TOP = 96;
    private static final int SPACE_SIDE = 16;

    private float guiScale() {
        return this.minecraft == null ? 1.0f : (float) this.minecraft.getWindow().getGuiScale();
    }

    private int px(int stardewPixels) {
        return Math.round(stardewPixels / guiScale());
    }

    private float s4() {
        return 4.0f / guiScale();
    }

    private int getMenuWidthPx() {
        return px(960);
    }

    private int getMenuHeightPx() {
        return px(512);
    }

    private int[] getOkButtonRect(int xPos, int yPos, int guiWidth, int guiHeight) {
        int okSize = px(64);
        int okX = xPos + guiWidth + px(4);
        int okY = yPos + guiHeight - px(64 + BORDER_WIDTH);
        if (okX + okSize > this.width) {
            okX = this.width - okSize;
        }
        if (okY + okSize > this.height) {
            okY = this.height - okSize;
        }
        return new int[]{okX, okY, okSize};
    }

    private void playUiSound(SoundEvent sound, float volume, float pitch) {
        if (this.minecraft != null) {
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(sound, volume, pitch));
        }
    }

    private void updateLittleStars(int xPos, int yPos, int guiWidth) {
        long now = System.currentTimeMillis();
        littleStars.removeIf(star -> star.isDone(now));

        if (ThreadLocalRandom.current().nextDouble() < 0.03) {
            int yRand = ThreadLocalRandom.current().nextInt(yPos - px(128), yPos - px(4));
            int y = (int) (Math.floor((double) yRand / 20.0) * 20 + 32);

            int x;
            if (ThreadLocalRandom.current().nextBoolean()) {
                x = ThreadLocalRandom.current().nextInt(xPos + guiWidth / 2 - px(228), xPos + guiWidth / 2 - px(132));
            } else {
                x = ThreadLocalRandom.current().nextInt(xPos + guiWidth / 2 + px(116), xPos + guiWidth - px(160));
            }
            if (y < yPos - px(72)) {
                x = ThreadLocalRandom.current().nextInt(xPos + guiWidth / 2 - px(116), xPos + guiWidth / 2 + px(116));
            }

            littleStars.add(new LittleStar(x, y, now));
        }
    }

    @Override
    protected void init() {
        super.init();
        playUiSound(ModSounds.BIG_SELECT.get(), 1.0f, 1.0f);
    }

    public LevelUpMenuScreen(OvernightSettlementPayload.LevelUpData levelData, List<Screen> siblingScreens) {
        super(Component.translatable("stardewcraft.levelup.title.screen"));
        this.siblingScreens = siblingScreens;

        this.currentSkill = levelData.skillIndex();
        this.currentLevel = levelData.newLevel();
        this.isProfessionChooser = (currentLevel == 5 || currentLevel == 10) && currentSkill != 5;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Match Stardew's dim overlay first
        graphics.fill(0, 0, this.width, this.height, 0x80000000);

        int guiWidth = getMenuWidthPx();
        int guiHeight = getMenuHeightPx();
        int xPos = this.width / 2 - guiWidth / 2;
        int yPos = this.height / 2 - guiHeight / 2;

        updateLittleStars(xPos, yPos, guiWidth);
        long now = System.currentTimeMillis();
        for (LittleStar star : littleStars) {
            star.draw(graphics, now);
        }

        // Header ribbon (mouseCursors 363,87,58,22, scale 4)
        StardewGuiUtil.drawFromCursors(graphics, xPos + guiWidth / 2 - px(116), yPos - px(40), 363, 87, 58, 22, s4());

        StardewGuiUtil.drawDialogueBoxFrame(graphics, xPos, yPos, guiWidth, guiHeight);

        if (isProfessionChooser) {
            drawSkillIcon(graphics, currentSkill, xPos + px(SPACE_SIDE + BORDER_WIDTH), yPos + px(SPACE_TOP + 16));
            drawSkillIcon(graphics, currentSkill, xPos + guiWidth - px(SPACE_SIDE + BORDER_WIDTH + 64), yPos + px(SPACE_TOP + 16));

            Component title = Component.translatable("stardewcraft.levelup.title.level_skill", currentLevel, getSkillName(currentSkill));
            graphics.drawString(this.font, title, xPos + guiWidth / 2 - this.font.width(title) / 2, yPos + px(SPACE_TOP + 16), 0x3A2A1A, false);

            Component chooseText = Component.translatable("stardewcraft.levelup.choose_profession");
            graphics.drawString(this.font, chooseText, xPos + guiWidth / 2 - this.font.width(chooseText) / 2, yPos + px(SPACE_TOP + 64), 0x3A2A1A, false);

            // IClickableMenu.drawHorizontalPartition + drawVerticalIntersectingPartition
            int partitionUnit = px(64);
            StardewGuiUtil.drawHorizontalPartition(graphics, xPos, yPos + px(192), guiWidth, partitionUnit);
            StardewGuiUtil.drawVerticalIntersectingPartition(graphics, xPos + guiWidth / 2 - px(32), yPos + px(192), yPos, guiHeight, partitionUnit);

            int[] professions = getProfessionPair(currentSkill, currentLevel);
            Component leftName = getProfessionName(professions[0]);
            Component rightName = getProfessionName(professions[1]);

            boolean inChoiceRows = mouseY >= yPos + px(192) && mouseY <= yPos + guiHeight;
            boolean hoverLeft = inChoiceRows && mouseX >= xPos && mouseX < xPos + guiWidth / 2;
            boolean hoverRight = inChoiceRows && mouseX >= xPos + guiWidth / 2 && mouseX <= xPos + guiWidth;
            int leftColor = hoverLeft ? 0x00AA00 : 0x3A2A1A;
            int rightColor = hoverRight ? 0x00AA00 : 0x3A2A1A;

            int leftX = xPos + px(SPACE_SIDE + 32);
            int rightX = xPos + px(SPACE_SIDE) + guiWidth / 2;
            int textY = yPos + px(SPACE_TOP + 160);
            int lineY = yPos + px(SPACE_TOP + 128 + 8 + 64 * 2);

            graphics.drawString(this.font, leftName, leftX, textY, leftColor, false);
            StardewGuiUtil.drawFromCursors(graphics, xPos + px(SPACE_SIDE) + guiWidth / 2 - px(112), yPos + px(SPACE_TOP + 144), (professions[0] % 6) * 16, 624 + (professions[0] / 6) * 16, 16, 16, s4());
            drawWrappedText(graphics, getProfessionDesc(professions[0]), leftX + px(-4), lineY, guiWidth / 2 - px(64), leftColor, px(16));

            graphics.drawString(this.font, rightName, rightX, textY, rightColor, false);
            StardewGuiUtil.drawFromCursors(graphics, xPos + px(SPACE_SIDE) + guiWidth - px(128), yPos + px(SPACE_TOP + 144), (professions[1] % 6) * 16, 624 + (professions[1] / 6) * 16, 16, 16, s4());
            drawWrappedText(graphics, getProfessionDesc(professions[1]), rightX + px(-4), lineY, guiWidth / 2 - px(48), rightColor, px(16));
        } else {
            drawSkillIcon(graphics, currentSkill, xPos + px(SPACE_SIDE + BORDER_WIDTH), yPos + px(SPACE_TOP + 16));
            drawSkillIcon(graphics, currentSkill, xPos + guiWidth - px(SPACE_SIDE + BORDER_WIDTH + 64), yPos + px(SPACE_TOP + 16));

            Component title = Component.translatable("stardewcraft.levelup.title.level_skill", currentLevel, getSkillName(currentSkill));
            graphics.drawString(this.font, title, xPos + guiWidth / 2 - this.font.width(title) / 2, yPos + px(SPACE_TOP + 16), 0x3A2A1A, false);

            Component proficiency = Component.translatable("stardewcraft.levelup.proficiency", getSkillName(currentSkill));
            graphics.drawString(this.font, proficiency, xPos + guiWidth / 2 - px(92), yPos + px(SPACE_TOP + 82), 0x3A2A1A, false);
            graphics.drawString(this.font, Component.translatable("stardewcraft.levelup.new_recipes"), xPos + guiWidth / 2 - px(76), yPos + px(SPACE_TOP + 130), 0x3A2A1A, false);

            List<Component> unlockedRecipeLines = getUnlockedRecipeLines();
            int lineY = yPos + px(SPACE_TOP + 160);
            if (unlockedRecipeLines.isEmpty()) {
                graphics.drawString(this.font, Component.translatable("stardewcraft.levelup.new_recipes.none"), xPos + guiWidth / 2 - px(84), lineY, 0x5A4A3A, false);
            } else {
                int maxLines = Math.min(4, unlockedRecipeLines.size());
                for (int i = 0; i < maxLines; i++) {
                    Component line = unlockedRecipeLines.get(i);
                    graphics.drawString(this.font, line, xPos + guiWidth / 2 - px(140), lineY + i * px(20), 0x3A2A1A, false);
                }
            }

            // Draw OK button
            int[] okRect = getOkButtonRect(xPos, yPos, guiWidth, guiHeight);
            int okX = okRect[0];
            int okY = okRect[1];
            int okWidth = okRect[2];
            boolean hovering = mouseX >= okX && mouseX <= okX + okWidth && mouseY >= okY && mouseY <= okY + okWidth;
            graphics.pose().pushPose();
            if (hovering) {
                 graphics.pose().translate(okX + okWidth/2f, okY + okWidth/2f, 0);
                 graphics.pose().scale(1.1f, 1.1f, 1f);
                 graphics.pose().translate(-(okX + okWidth/2f), -(okY + okWidth/2f), 0);
            }
            StardewGuiUtil.drawFromCursors(graphics, okX, okY, 128, 256, 64, 64, 1.0f / guiScale());
            graphics.pose().popPose();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int guiWidth = getMenuWidthPx();
            int guiHeight = getMenuHeightPx();
            int xPos = this.width / 2 - guiWidth / 2;
            int yPos = this.height / 2 - guiHeight / 2;

            if (isProfessionChooser) {
                int splitY = yPos + px(192);
                if (mouseY >= splitY && mouseY <= yPos + guiHeight) {
                    int[] professions = getProfessionPair(currentSkill, currentLevel);
                    if (mouseX >= xPos && mouseX < xPos + guiWidth / 2) {
                        playUiSound(ModSounds.BIG_DESELECT.get(), 1.0f, 1.0f);
                        ClientOvernightHandler.recordLocalProfessionChoice(professions[0]);
                        PacketDistributor.sendToServer(new OvernightProfessionChoicePayload(professions[0]));
                        this.onClose();
                        return true;
                    }
                    if (mouseX >= xPos + guiWidth / 2 && mouseX <= xPos + guiWidth) {
                        playUiSound(ModSounds.BIG_DESELECT.get(), 1.0f, 1.0f);
                        ClientOvernightHandler.recordLocalProfessionChoice(professions[1]);
                        PacketDistributor.sendToServer(new OvernightProfessionChoicePayload(professions[1]));
                        this.onClose();
                        return true;
                    }
                }
            } else {
                int[] okRect = getOkButtonRect(xPos, yPos, guiWidth, guiHeight);
                int okX = okRect[0];
                int okY = okRect[1];
                int okWidth = okRect[2];
                
                if (mouseX >= okX && mouseX <= okX + okWidth && mouseY >= okY && mouseY <= okY + okWidth) {
                    playUiSound(ModSounds.BIG_DESELECT.get(), 1.0f, 1.0f);
                    this.onClose();
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

    private Component getSkillName(int skill) {
        return switch (skill) {
            case 0 -> Component.translatable("stardewcraft.levelup.skill.farming");
            case 1 -> Component.translatable("stardewcraft.levelup.skill.fishing");
            case 2 -> Component.translatable("stardewcraft.levelup.skill.foraging");
            case 3 -> Component.translatable("stardewcraft.levelup.skill.mining");
            case 4 -> Component.translatable("stardewcraft.levelup.skill.combat");
            default -> Component.translatable("stardewcraft.levelup.skill.unknown");
        };
    }

    private void drawSkillIcon(GuiGraphics graphics, int skill, int x, int y) {
        ResourceLocation icon = switch (skill) {
            case 0 -> TEX_ICON_FARMING;
            case 1 -> TEX_ICON_FISHING;
            case 2 -> TEX_ICON_FORAGING;
            case 3 -> TEX_ICON_MINING;
            default -> TEX_ICON_OTHER;
        };
        graphics.setColor(1.0F, 1.0F, 1.0F, 0.88f);
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        graphics.pose().scale(s4(), s4(), 1.0f);
        graphics.blit(icon, 0, 0, 0, 0, 16, 16, 16, 16);
        graphics.pose().popPose();
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private int[] getProfessionPair(int skill, int level) {
        if (level == 5) {
            return switch (skill) {
                case 0 -> new int[]{0, 1};
                case 1 -> new int[]{6, 7};
                case 2 -> new int[]{12, 13};
                case 3 -> new int[]{18, 19};
                case 4 -> new int[]{24, 25};
                default -> new int[]{0, 1};
            };
        }

        SkillType skillType = mapSkill(skill);
        ProfessionType[] level5Choices = ProfessionType.getLevel5Options(skillType);
        if (ClientOvernightHandler.hasLocalProfession(level5Choices[0]) || ClientPlayerDataCache.hasProfession(level5Choices[0])) {
            ProfessionType[] level10 = ProfessionType.getLevel10Options(skillType, level5Choices[0]);
            return new int[]{level10[0].getId(), level10[1].getId()};
        }
        if (ClientOvernightHandler.hasLocalProfession(level5Choices[1]) || ClientPlayerDataCache.hasProfession(level5Choices[1])) {
            ProfessionType[] level10 = ProfessionType.getLevel10Options(skillType, level5Choices[1]);
            return new int[]{level10[0].getId(), level10[1].getId()};
        }

        return switch (skill) {
            case 0 -> new int[]{2, 3};
            case 1 -> new int[]{8, 9};
            case 2 -> new int[]{14, 15};
            case 3 -> new int[]{20, 21};
            case 4 -> new int[]{26, 27};
            default -> new int[]{2, 3};
        };
    }

    private SkillType mapSkill(int skill) {
        return switch (skill) {
            case 0 -> SkillType.FARMING;
            case 1 -> SkillType.FISHING;
            case 2 -> SkillType.FORAGING;
            case 3 -> SkillType.MINING;
            case 4 -> SkillType.COMBAT;
            default -> SkillType.FARMING;
        };
    }

    private Component getProfessionName(int profession) {
        if (profession < 0 || profession > 29) {
            return Component.translatable("stardewcraft.levelup.profession.unknown.name");
        }
        return Component.translatable("stardewcraft.levelup.profession." + profession + ".name");
    }

    private Component getProfessionDesc(int profession) {
        if (profession < 0 || profession > 29) {
            return Component.translatable("stardewcraft.levelup.profession.unknown.desc");
        }
        return Component.translatable("stardewcraft.levelup.profession." + profession + ".desc");
    }

    private List<Component> getUnlockedRecipeLines() {
        List<Component> lines = new ArrayList<>();
        SkillType skillType = mapSkill(currentSkill);
        java.util.Set<String> seen = new java.util.LinkedHashSet<>();

        // 1) unlock_sources.json (新系统)
        UnlockSourceData.UnlockBundle sourceBundle = UnlockSourceData.getSkillLevelUnlocks(skillType, currentLevel);
        seen.addAll(sourceBundle.recipes());

        // 2) skill_level_recipe_unlocks.json (遗留系统)
        for (String id : SkillLevelRecipeUnlocks.getUnlocks(skillType, currentLevel)) {
            seen.add(id);
        }

        // 3) vanilla_crafting_recipes.json — 匹配 "s <skill> <level>" 条件
        String skillName = skillType.getName().toLowerCase(java.util.Locale.ROOT);
        for (StardewCraftingRecipeData.RecipeEntry recipe : StardewCraftingRecipeData.getRecipes()) {
            String cond = recipe.unlockCondition();
            if (cond == null || cond.isBlank()) continue;
            String[] parts = cond.trim().split("\\s+");
            if (parts.length >= 3 && "s".equalsIgnoreCase(parts[0])
                    && skillName.equalsIgnoreCase(parts[1])) {
                try {
                    if (Integer.parseInt(parts[2]) == currentLevel) {
                        seen.add(recipe.id());
                    }
                } catch (NumberFormatException ignored) {}
            }
        }

        for (String recipeId : seen) {
            if (recipeId == null || recipeId.isBlank()) continue;
            lines.add(Component.translatable("recipe.stardewcraft." + recipeId));
        }
        return lines;
    }

    private void drawWrappedText(GuiGraphics graphics, Component text, int x, int y, int maxWidth, int color, int lineStep) {
        List<FormattedCharSequence> lines = this.font.split(text, Math.max(1, maxWidth));
        int lineY = y;
        for (FormattedCharSequence line : lines) {
            graphics.drawString(this.font, line, x, lineY, color, false);
            lineY += lineStep;
        }
    }

    private static class LittleStar {
        private static final int FRAME_MS = 80;
        private static final int FRAME_COUNT = 7;
        private static final int SIZE = 5;

        private final int x;
        private final int y;
        private final long startMs;

        private LittleStar(int x, int y, long startMs) {
            this.x = x;
            this.y = y;
            this.startMs = startMs;
        }

        private boolean isDone(long now) {
            return now - startMs >= (long) FRAME_MS * FRAME_COUNT;
        }

        private void draw(GuiGraphics graphics, long now) {
            long age = now - startMs;
            int frame = (int) (age / FRAME_MS);
            if (frame < 0 || frame >= FRAME_COUNT) {
                return;
            }
            graphics.pose().pushPose();
            graphics.pose().translate(x, y, 0);
            graphics.pose().scale(4.0f, 4.0f, 1.0f);
            graphics.blit(TEX_LITTLE_STAR, 0, 0, frame * SIZE, 0, SIZE, SIZE, 35, 5);
            graphics.pose().popPose();
        }
    }
}
