package com.stardew.craft.client.emote;

import org.lwjgl.glfw.GLFW;

import com.stardew.craft.client.ModKeyMappings;
import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.Minecraft;

@SuppressWarnings("null")
public final class EmoteWheelClient {

	private static boolean wasDown;

	private EmoteWheelClient() {
	}

	public static void onClientTick() {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null || mc.level == null) {
			wasDown = false;
			if (mc.screen instanceof EmoteWheelScreen) {
				mc.setScreen(null);
			}
			return;
		}

		boolean down = isWheelKeyHeld(mc);
		if (down && !wasDown && mc.screen == null) {
			mc.setScreen(new EmoteWheelScreen());
		}
		if (!down && wasDown && mc.screen instanceof EmoteWheelScreen screen) {
			screen.confirmAndClose();
		}

		wasDown = down;
	}

	public static boolean isWheelKeyHeld(Minecraft mc) {
		InputConstants.Key key = ModKeyMappings.EMOTE_WHEEL.getKey();
		long window = mc.getWindow().getWindow();
		if (key.getType() == InputConstants.Type.MOUSE) {
			return GLFW.glfwGetMouseButton(window, key.getValue()) == GLFW.GLFW_PRESS;
		}
		return InputConstants.isKeyDown(window, key.getValue());
	}

	public static void render(net.minecraft.client.gui.GuiGraphics guiGraphics) {
	}
}
