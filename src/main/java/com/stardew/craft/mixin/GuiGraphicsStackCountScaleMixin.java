package com.stardew.craft.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsStackCountScaleMixin {

    @Shadow @Final private PoseStack pose;

    @SuppressWarnings("null")
    @Redirect(
            method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V",
            at = @At(
                value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)I"
            ),
            require = 1
    )
    private int stardewcraft$scaleBigStackCount(GuiGraphics guiGraphics, Font font, String text, int x, int y, int color, boolean dropShadow) {
        float scale = getScaleForCountText(text);
        if (scale >= 0.999f) {
            return guiGraphics.drawString(font, text, x, y, color, dropShadow);
        }

        // Keep vanilla alignment by scaling around the text's bottom-right corner.
        // Vanilla positions the count so its right edge hugs the slot's right side.
        @SuppressWarnings("null")
        int textWidth = font.width(text);
        int anchorX = x + textWidth;
        int anchorY = y + font.lineHeight;

        this.pose.pushPose();
        this.pose.translate(anchorX, anchorY, 0.0F);
        this.pose.scale(scale, scale, 1.0F);
        this.pose.translate(-anchorX, -anchorY, 0.0F);

        int result = guiGraphics.drawString(font, text, x, y, color, dropShadow);
        this.pose.popPose();
        return result;
    }

    private static float getScaleForCountText(String text) {
        if (text == null) {
            return 1.0F;
        }

        int len = text.length();
        if (len < 3) {
            return 1.0F;
        }

        // Only shrink when it's a plain numeric stack count (e.g. 100, 999)
        for (int i = 0; i < len; i++) {
            if (!Character.isDigit(text.charAt(i))) {
                return 1.0F;
            }
        }

        // Slightly smaller for 3 digits, progressively smaller for larger numbers.
        if (len == 3) {
            return 0.85F;
        }
        if (len == 4) {
            return 0.7F;
        }
        return 0.6F;
    }
}
