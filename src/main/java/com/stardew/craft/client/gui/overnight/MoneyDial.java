package com.stardew.craft.client.gui.overnight;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MoneyDial {

    public int numDigits;
    public int currentValue;
    public int previousTargetValue;

    private int speed;
    private int soundTimer;

    public MoneyDial(int numDigits) {
        this.numDigits = numDigits;
        this.currentValue = 0;
    }

    public void draw(GuiGraphics graphics, int x, int y, int target) {
        if (previousTargetValue != target) {
            speed = (target - currentValue) / 100;
            previousTargetValue = target;
            soundTimer = Math.max(6, 100 / (Math.abs(speed) + 1));
        }
        
        if (currentValue != target) {
            currentValue += speed + ((currentValue < target) ? 1 : -1);
            
            soundTimer--;
            
            if (Math.abs(target - currentValue) <= speed + 1 || (speed != 0 && Math.signum(target - currentValue) != Math.signum(speed))) {
                currentValue = target;
            }
            
            if (soundTimer <= 0) {
                // Play money sound - Minecraft equivalent
                // e.g. Minecraft.getInstance().player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.5f, 1.0f);
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
                // b.Draw(..., position + new Vector2(xPosition, ...), new Rectangle(286, 502 - currentDigit * 8, 5, 8), ..., scale: 4f)
                StardewGuiUtil.drawFromCursors(graphics, x + xPosition, y, 286, 502 - currentDigit * 8, 5, 8, 4f);
            }
            xPosition += 24; // 24 is 6 * 4
            digitStrip /= 10;
        }
    }
}
