package com.stardew.craft.client.gui.overnight;

import com.stardew.craft.sound.ModSounds;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("null")
public class MoneyDial {

    public int numDigits;
    public int currentValue;
    public int previousTargetValue;

    private int speed;
    private int soundTimer;
    private int moneyMadeAccumulator;
    private int moneyShineTimer;

    public MoneyDial(int numDigits) {
        this.numDigits = numDigits;
        this.currentValue = 0;
    }

    public void draw(GuiGraphics graphics, int x, int y, int target) {
        float guiScale = (float) net.minecraft.client.Minecraft.getInstance().getWindow().getGuiScale();
        float digitScale = 4.0f / guiScale;
        int digitStep = Math.max(1, Math.round(24.0f / guiScale));

        if (previousTargetValue != target) {
            speed = (target - currentValue) / 100;
            previousTargetValue = target;
            soundTimer = Math.max(6, 100 / (Math.abs(speed) + 1));
        }

        if (moneyShineTimer > 0 && currentValue == target) {
            moneyShineTimer = Math.max(0, moneyShineTimer - 16);
        }

        if (moneyMadeAccumulator > 0) {
            moneyMadeAccumulator -= (Math.abs(speed / 2) + 1) * 100;
            if (moneyMadeAccumulator <= 0) {
                moneyShineTimer = numDigits * 60;
            }
        }
        
        if (currentValue != target) {
            currentValue += speed + ((currentValue < target) ? 1 : -1);

            if (currentValue < target) {
                moneyMadeAccumulator += Math.abs(speed);
            }
            
            soundTimer--;
            
            if (Math.abs(target - currentValue) <= speed + 1 || (speed != 0 && Math.signum(target - currentValue) != Math.signum(speed))) {
                currentValue = target;
            }
            
            if (soundTimer <= 0) {
                if (target > currentValue && net.minecraft.client.Minecraft.getInstance() != null) {
                    net.minecraft.client.Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(ModSounds.MONEY_DIAL.get(), 1.0f, 1.0f));
                }
                soundTimer = Math.max(6, 100 / (Math.abs(speed) + 1));
            }
        }
        
        int xPosition = 0;
        int digitStrip = (int) Math.pow(10.0, numDigits - 1);
        boolean significant = false;
        
        for (int j = 0; j < numDigits; j++) {
            int currentDigit = (currentValue / digitStrip) % 10;
            if (currentDigit > 0 || j == numDigits - 1) {
                significant = true;
            }
            
            if (significant) {
                float yOffset = 0.0f;
                if (net.minecraft.client.Minecraft.getInstance().screen instanceof ShippingMenuScreen && currentValue >= 1_000_000) {
                    yOffset = Mth.sin((float) (System.currentTimeMillis() / 100.53096771240234D + j)) * (currentValue / 1_000_000.0f);
                }
                float scale = digitScale + ((moneyShineTimer / 60 == numDigits - j) ? (0.3f / guiScale) : 0.0f);
                ShippingMenuTextures.drawDigit(graphics, x + xPosition, (int) (y + yOffset), currentDigit, scale,
                        128.0F / 255.0F, 0.0F, 0.0F, 1.0F);
            }
            xPosition += digitStep;
            digitStrip /= 10;
        }
    }
}
