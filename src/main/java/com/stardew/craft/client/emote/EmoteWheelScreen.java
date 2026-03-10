package com.stardew.craft.client.emote;

import java.util.List;

import com.stardew.craft.emote.EmoteCatalog;
import com.stardew.craft.emote.EmoteType;
import com.stardew.craft.network.payload.EmoteUsePayload;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.network.PacketDistributor;

@SuppressWarnings("null")
public final class EmoteWheelScreen extends Screen {

	private static final ResourceLocation EMOTES_TEXTURE = ResourceLocation.fromNamespaceAndPath("stardewcraft", "textures/gui/emotes.png");
	private static final int EMOTES_TEX_W = 64;
	private static final int EMOTES_TEX_H = 256;
	private static final int ICON_SIZE = 24;
	private static final int INNER_RADIUS = 76;
	private static final int OUTER_RADIUS = 118;
	private static final float SEGMENT_GAP_RAD = 0.12F;
	private static final int OPEN_ANIM_MS = 200;
	private static final double COLLAPSE_ANGLE = -Math.PI / 2D;
	private static final int COLLAPSED_INNER_RADIUS = 24;
	private static final int COLLAPSED_OUTER_RADIUS = 34;

	private int selectedIndex = -1;
	private long openedAtMs;

	public EmoteWheelScreen() {
		super(Component.empty());
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return true;
	}

	@Override
	protected void init() {
		super.init();
		openedAtMs = Util.getMillis();
	}

	@Override
	public void tick() {
		super.tick();
		if (minecraft == null || minecraft.player == null || minecraft.level == null) {
			onClose();
			return;
		}
		if (!EmoteWheelClient.isWheelKeyHeld(minecraft)) {
			confirmAndClose();
		}
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		int screenW = guiGraphics.guiWidth();
		int screenH = guiGraphics.guiHeight();
		int centerX = screenW / 2;
		int centerY = screenH / 2;
		float openProgress = getOpenProgress();
		float eased = easeInOutSmoother(openProgress);
		int animatedInner = Mth.floor(Mth.lerp(eased, COLLAPSED_INNER_RADIUS, INNER_RADIUS));
		int animatedOuter = Mth.floor(Mth.lerp(eased, COLLAPSED_OUTER_RADIUS, OUTER_RADIUS));
		double spanScale = Math.max(0.06D, eased);

		int fadeAlpha = Mth.floor(Mth.lerp(eased, 20.0F, 122.0F));
		guiGraphics.fill(0, 0, screenW, screenH, (fadeAlpha << 24));

		List<EmoteType> wheelItems = EmoteCatalog.WHEEL_ITEMS;
		selectedIndex = computeSelectedIndex(mouseX, mouseY, centerX, centerY, wheelItems.size(), selectedIndex, animatedInner, animatedOuter);
		drawAnimatedSegments(guiGraphics, centerX, centerY, wheelItems.size(), selectedIndex, animatedInner, animatedOuter, spanScale);

		int lineEndX = mouseX;
		int lineEndY = mouseY;
		if (selectedIndex >= 0 && selectedIndex < wheelItems.size()) {
			double selectedAngle = getAnimatedSegmentCenterAngle(selectedIndex, wheelItems.size(), eased);
			lineEndX = centerX + (int) Math.round(Math.cos(selectedAngle) * animatedOuter);
			lineEndY = centerY + (int) Math.round(Math.sin(selectedAngle) * animatedOuter);
		}
		drawLine(guiGraphics, centerX, centerY, lineEndX, lineEndY, 0xFFFFD84A, 2);
		drawCircle(guiGraphics, centerX, centerY, 4, 0xFFFFD84A);

		for (int i = 0; i < wheelItems.size(); i++) {
			double angle = getAnimatedSegmentCenterAngle(i, wheelItems.size(), eased);
			int iconRadius = (animatedInner + animatedOuter) / 2;
			int x = centerX + (int) Math.round(Math.cos(angle) * iconRadius) - ICON_SIZE / 2;
			int y = centerY + (int) Math.round(Math.sin(angle) * iconRadius) - ICON_SIZE / 2;
			drawCircle(guiGraphics, x + ICON_SIZE / 2 + 1, y + ICON_SIZE / 2 + 1, (ICON_SIZE / 2) + 3, 0x50000000);
			drawCircle(guiGraphics, x + ICON_SIZE / 2, y + ICON_SIZE / 2, (ICON_SIZE / 2) + 2, 0xB0000000);

			if (i == selectedIndex) {
				drawCircle(guiGraphics, x + ICON_SIZE / 2, y + ICON_SIZE / 2, (ICON_SIZE / 2) + 3, 0x7FFFD84A);
			}
			drawEmoteIcon(guiGraphics, x, y, ICON_SIZE, wheelItems.get(i));
		}

		if (selectedIndex >= 0 && selectedIndex < wheelItems.size() && minecraft != null) {
			EmoteType selected = wheelItems.get(selectedIndex);
			Component label = Component.translatable("stardewcraft.emote." + selected.id());
			int labelX = centerX - minecraft.font.width(label) / 2;
			guiGraphics.drawString(minecraft.font, label, labelX, centerY + animatedOuter + 24, 0xFFF4EED0, true);
		}
	}

	public void confirmAndClose() {
		if (selectedIndex >= 0 && selectedIndex < EmoteCatalog.WHEEL_ITEMS.size()) {
			EmoteType emote = EmoteCatalog.WHEEL_ITEMS.get(selectedIndex);
			PacketDistributor.sendToServer(new EmoteUsePayload(emote.id()));
		}
		onClose();
	}

	@Override
	public void onClose() {
		Minecraft mc = Minecraft.getInstance();
		if (mc.screen == this) {
			mc.setScreen(null);
		}
	}

	private static int computeSelectedIndex(int mouseX, int mouseY, int centerX, int centerY, int count, int previousSelection,
			int innerRadius, int outerRadius) {
		double dx = mouseX - centerX;
		double dy = mouseY - centerY;
		double length = Math.sqrt(dx * dx + dy * dy);
		if (length < innerRadius - 2) {
			return -1;
		}

		double step = (Math.PI * 2D) / count;
		double normalized = Math.atan2(dy, dx) - (-Math.PI / 2D);
		while (normalized < 0D) {
			normalized += Math.PI * 2D;
		}
		while (normalized >= Math.PI * 2D) {
			normalized -= Math.PI * 2D;
		}

		int sector = Mth.floor(normalized / step);
		double local = normalized - (sector * step);
		double gapHalf = SEGMENT_GAP_RAD * 0.5D;
		if (local < gapHalf || local > step - gapHalf) {
			if (length >= outerRadius && previousSelection >= 0) {
				return previousSelection;
			}
			return -1;
		}
		return Mth.clamp(sector, 0, count - 1);
	}

	private static double getAnimatedSegmentCenterAngle(int index, int count, float easedProgress) {
		double target = getSegmentCenterAngle(index, count);
		return Mth.lerp(easedProgress, COLLAPSE_ANGLE, target);
	}

	private static double getSegmentCenterAngle(int index, int count) {
		double step = (Math.PI * 2D) / count;
		return (-Math.PI / 2D) + (step * index) + (step * 0.5D);
	}

	private static void drawAnimatedSegments(GuiGraphics guiGraphics, int centerX, int centerY, int count, int selectedIndex,
			int innerRadius, int outerRadius, double spanScale) {
		double step = (Math.PI * 2D) / count;
		double halfSpan = ((step - SEGMENT_GAP_RAD) * 0.5D) * spanScale;
		for (int i = 0; i < count; i++) {
			double centerAngle = getAnimatedSegmentCenterAngle(i, count, (float) spanScale);
			double start = centerAngle - halfSpan;
			double end = centerAngle + halfSpan;
			int baseColor = i == selectedIndex ? 0x88FFD84A : 0xA1101010;
			drawSegmentFill(guiGraphics, centerX, centerY, innerRadius, outerRadius, start, end, baseColor);
			drawSegmentEdge(guiGraphics, centerX, centerY, innerRadius, outerRadius, start, 0x66000000);
			drawSegmentEdge(guiGraphics, centerX, centerY, innerRadius, outerRadius, end, 0x66000000);
		}
	}

	private static void drawSegmentFill(GuiGraphics guiGraphics, int centerX, int centerY, int innerRadius, int outerRadius,
			double start, double end, int color) {
		for (int strip = 0; strip <= 18; strip++) {
			double t = strip / 18.0D;
			double angle = Mth.lerp(t, start, end);
			int x0 = centerX + (int) Math.round(Math.cos(angle) * innerRadius);
			int y0 = centerY + (int) Math.round(Math.sin(angle) * innerRadius);
			int x1 = centerX + (int) Math.round(Math.cos(angle) * outerRadius);
			int y1 = centerY + (int) Math.round(Math.sin(angle) * outerRadius);
			drawLine(guiGraphics, x0, y0, x1, y1, color, 2);
		}
	}

	private static void drawSegmentEdge(GuiGraphics guiGraphics, int centerX, int centerY, int innerRadius, int outerRadius,
			double angle, int color) {
		int x0 = centerX + (int) Math.round(Math.cos(angle) * innerRadius);
		int y0 = centerY + (int) Math.round(Math.sin(angle) * innerRadius);
		int x1 = centerX + (int) Math.round(Math.cos(angle) * outerRadius);
		int y1 = centerY + (int) Math.round(Math.sin(angle) * outerRadius);
		drawLine(guiGraphics, x0, y0, x1, y1, color, 2);
	}

	private float getOpenProgress() {
		if (openedAtMs <= 0L) {
			return 1.0F;
		}
		long elapsed = Util.getMillis() - openedAtMs;
		return Mth.clamp(elapsed / (float) OPEN_ANIM_MS, 0.0F, 1.0F);
	}

	private static float easeInOutSmoother(float t) {
		float x = Mth.clamp(t, 0.0F, 1.0F);
		return x * x * x * (x * (x * 6.0F - 15.0F) + 10.0F);
	}

	private static void drawEmoteIcon(GuiGraphics guiGraphics, int x, int y, int size, EmoteType emote) {
		int frame = Math.max(0, Math.min(63, emote.iconIndex()));
		int pixelX = (frame * 16) % EMOTES_TEX_W;
		int pixelY = ((frame * 16) / EMOTES_TEX_W) * 16;
		guiGraphics.blit(EMOTES_TEXTURE, x, y, size, size, pixelX, pixelY, 16, 16, EMOTES_TEX_W, EMOTES_TEX_H);
	}

	private static void drawCircle(GuiGraphics guiGraphics, int centerX, int centerY, int radius, int color) {
		for (int dy = -radius; dy <= radius; dy++) {
			int span = (int) Math.floor(Math.sqrt(radius * radius - dy * dy));
			guiGraphics.fill(centerX - span, centerY + dy, centerX + span + 1, centerY + dy + 1, color);
		}
	}

	private static void drawLine(GuiGraphics guiGraphics, int x0, int y0, int x1, int y1, int color, int thickness) {
		int dx = Math.abs(x1 - x0);
		int dy = Math.abs(y1 - y0);
		int sx = x0 < x1 ? 1 : -1;
		int sy = y0 < y1 ? 1 : -1;
		int err = dx - dy;

		while (true) {
			guiGraphics.fill(x0 - thickness / 2, y0 - thickness / 2, x0 + thickness / 2 + 1, y0 + thickness / 2 + 1, color);
			if (x0 == x1 && y0 == y1) {
				break;
			}
			int e2 = err * 2;
			if (e2 > -dy) {
				err -= dy;
				x0 += sx;
			}
			if (e2 < dx) {
				err += dx;
				y0 += sy;
			}
		}
	}
}
