package com.stardew.craft.client;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.item.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;

@EventBusSubscriber(modid = StardewCraft.MODID, value = Dist.CLIENT)
public final class SpecialConsumableClientFx {
    private static final int STARDROP_TOP = 0xFFEFC7FF;
    private static final int STARDROP_MID = 0xFF9A5CFF;
    private static final int STARDROP_BOTTOM = 0xFF2A124F;
    private static final int STARDROP_BG_TOP = 0xF0180928;
    private static final int STARDROP_BG_BOTTOM = 0xF0060312;

    private static final int IRIDIUM_TOP = 0xFFE6FAFF;
    private static final int IRIDIUM_MID = 0xFF68C8FF;
    private static final int IRIDIUM_BOTTOM = 0xFF3F4A7A;
    private static final int IRIDIUM_BG_TOP = 0xF0061620;
    private static final int IRIDIUM_BG_BOTTOM = 0xF0020810;

    private SpecialConsumableClientFx() {}

    @SubscribeEvent
    public static void onTooltipColor(RenderTooltipEvent.Color event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) {
            return;
        }

        if (stack.is(ModItems.STARDROP.get()) || stack.is(ModItems.STARDROP_TEA.get())) {
            applyPalette(event, STARDROP_TOP, STARDROP_MID, STARDROP_BOTTOM, STARDROP_BG_TOP, STARDROP_BG_BOTTOM);
        } else if (stack.is(ModItems.IRIDIUM_MILK.get())) {
            applyPalette(event, IRIDIUM_TOP, IRIDIUM_MID, IRIDIUM_BOTTOM, IRIDIUM_BG_TOP, IRIDIUM_BG_BOTTOM);
        }
    }

    public static MutableComponent stardropTypeLabel(String raw) {
        return flowingLabel(raw, 0x8E4CFF, 0xFFD56D, 0.40F, 0.34F);
    }

    public static MutableComponent iridiumMilkTypeLabel(String raw) {
        return flowingLabel(raw, 0x5BBEEA, 0xF4FDFF, 0.44F, 0.30F);
    }

    private static void applyPalette(RenderTooltipEvent.Color event, int top, int mid, int bottom, int bgTop, int bgBottom) {
        long ms = System.currentTimeMillis();
        float phase = (float) (((ms % 60_000L) / 1000.0F / 2.4F) * Math.PI * 2.0D);
        float k = 0.5F + 0.5F * (float) Math.sin(phase);
        float shifted = 0.5F + 0.5F * (float) Math.sin(phase + Math.PI * 0.58D);
        event.setBorderStart(lerpArgb(mid, top, k));
        event.setBorderEnd(lerpArgb(bottom, mid, shifted));
        event.setBackgroundStart(bgTop);
        event.setBackgroundEnd(bgBottom);
    }

    private static MutableComponent flowingLabel(String raw, int baseRgb, int highlightRgb, float speed, float halfWidth) {
        long ms = System.currentTimeMillis();
        float span = 1.0F + halfWidth * 2.0F;
        float pos = (((ms % 60_000L) / 1000.0F * speed) % span) - halfWidth;
        int length = raw.length();
        MutableComponent out = Component.empty();
        for (int i = 0; i < length; i++) {
            float u = length > 1 ? (float) i / (length - 1) : 0.5F;
            float k = Math.max(0.0F, 1.0F - Math.abs(u - pos) / halfWidth);
            k = k * k * (3.0F - 2.0F * k);
            out.append(Component.literal(String.valueOf(raw.charAt(i)))
                    .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(lerpRgb(baseRgb, highlightRgb, k))).withBold(true)));
        }
        return out;
    }

    private static int lerpArgb(int a, int b, float k) {
        k = Mth.clamp(k, 0.0F, 1.0F);
        int aa = (a >>> 24) & 0xFF, ar = (a >> 16) & 0xFF, ag = (a >> 8) & 0xFF, ab = a & 0xFF;
        int ba = (b >>> 24) & 0xFF, br = (b >> 16) & 0xFF, bg = (b >> 8) & 0xFF, bb = b & 0xFF;
        int alpha = Math.round(aa + (ba - aa) * k);
        int red = Math.round(ar + (br - ar) * k);
        int green = Math.round(ag + (bg - ag) * k);
        int blue = Math.round(ab + (bb - ab) * k);
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    private static int lerpRgb(int a, int b, float k) {
        k = Mth.clamp(k, 0.0F, 1.0F);
        int ar = (a >> 16) & 0xFF, ag = (a >> 8) & 0xFF, ab = a & 0xFF;
        int br = (b >> 16) & 0xFF, bg = (b >> 8) & 0xFF, bb = b & 0xFF;
        int red = Math.round(ar + (br - ar) * k);
        int green = Math.round(ag + (bg - ag) * k);
        int blue = Math.round(ab + (bb - ab) * k);
        return (red << 16) | (green << 8) | blue;
    }
}