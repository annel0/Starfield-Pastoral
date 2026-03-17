package com.stardew.craft.client.gui.overnight;

import com.stardew.craft.network.overnight.OvernightSettlementPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("null")
public class LevelUpMenuScreen extends Screen {

    private final OvernightSettlementPayload.LevelUpData levelData;
    private final List<Screen> siblingScreens;

    // Stardew variables
    private boolean isProfessionChooser;
    private int currentLevel;
    private int currentSkill;

    public LevelUpMenuScreen(OvernightSettlementPayload.LevelUpData levelData, List<Screen> siblingScreens) {
        super(Component.literal("Level Up"));
        this.levelData = levelData;
        this.siblingScreens = siblingScreens;

        this.currentSkill = levelData.skillIndex();
        this.currentLevel = levelData.newLevel();
        this.isProfessionChooser = (currentLevel == 5 || currentLevel == 10) && currentSkill != 5;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        // Draw background overlay
        graphics.fill(0, 0, this.width, this.height, 0x80000000);

        int guiWidth = isProfessionChooser ? 960 : 768;
        int guiHeight = 512;
        int xPos = this.width / 2 - guiWidth / 2;
        int yPos = this.height / 2 - guiHeight / 2;

        if (isProfessionChooser) {
            // LevelUp_ChooseProfession
            StardewGuiUtil.drawTextureBox(graphics, xPos, yPos, guiWidth, guiHeight);
            
            Component title = Component.translatable("stardewcraft.skill.level_up_title", currentLevel);
            graphics.drawString(this.font, title, xPos + guiWidth / 2 - this.font.width(title) / 2, yPos + 32, 0x663300, false);
            
            // Draw horizontal partition
            // For now, simpler partition
            graphics.fill(xPos + 32, yPos + 192, xPos + guiWidth - 32, yPos + 196, 0x665522);
            graphics.fill(xPos + guiWidth / 2 - 2, yPos + 196, xPos + guiWidth / 2 + 2, yPos + guiHeight - 32, 0x665522);
            
            // Draw Left/Right Profession Boxes - simple placeholders
            Component leftTitle = Component.literal("Profession A");
            graphics.drawString(this.font, leftTitle, xPos + guiWidth / 4 - this.font.width(leftTitle) / 2, yPos + 220, 0x663300, false);
            Component rightTitle = Component.literal("Profession B");
            graphics.drawString(this.font, rightTitle, xPos + 3 * guiWidth / 4 - this.font.width(rightTitle) / 2, yPos + 220, 0x663300, false);
        } else {
            StardewGuiUtil.drawTextureBox(graphics, xPos, yPos, guiWidth, guiHeight);
            
            Component title = Component.translatable("stardewcraft.skill.level_up_title", currentLevel);
            graphics.drawString(this.font, title, xPos + guiWidth / 2 - this.font.width(title) / 2, yPos + 32, 0x663300, false);

            graphics.drawString(this.font, Component.literal("New Recipes unlocked... (WIP)"), xPos + 64, yPos + 128, 0x663300, false);

            // Draw OK button
            int okWidth = 64;
            int okX = xPos + guiWidth + 4;
            int okY = yPos + guiHeight - 64 - 8;
            boolean hovering = mouseX >= okX && mouseX <= okX + okWidth && mouseY >= okY && mouseY <= okY + okWidth;
            graphics.pose().pushPose();
            if (hovering) {
                 graphics.pose().translate(okX + okWidth/2f, okY + okWidth/2f, 0);
                 graphics.pose().scale(1.1f, 1.1f, 1f);
                 graphics.pose().translate(-(okX + okWidth/2f), -(okY + okWidth/2f), 0);
            }
            StardewGuiUtil.drawFromCursors(graphics, okX, okY, 128, 256, okWidth, okWidth, 1.0f);
            graphics.pose().popPose();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int guiWidth = isProfessionChooser ? 960 : 768;
            int guiHeight = 512;
            int xPos = this.width / 2 - guiWidth / 2;
            int yPos = this.height / 2 - guiHeight / 2;

            if (isProfessionChooser) {
                // Determine left or right click
                if (mouseY > yPos + 192) {
                    if (mouseX < this.width / 2) {
                        // Pick Left
                        this.onClose();
                        return true;
                    } else if (mouseX > this.width / 2) {
                        // Pick Right
                        this.onClose();
                        return true;
                    }
                }
            } else {
                int okWidth = 64;
                int okX = xPos + guiWidth + 4;
                int okY = yPos + guiHeight - 64 - 8;
                
                if (mouseX >= okX && mouseX <= okX + okWidth && mouseY >= okY && mouseY <= okY + okWidth) {
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
}
