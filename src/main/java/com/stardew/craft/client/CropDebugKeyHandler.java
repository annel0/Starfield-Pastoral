package com.stardew.craft.client;

/**
 * Legacy debug handler.
 *
 * NOTE: This class used to register the same key mapping as {@link DebugKeybinds},
 * which causes a hard crash during early display initialization (Duplicate key).
 * Keep it as an empty shell to avoid breaking old references.
 */
public final class CropDebugKeyHandler {
	private CropDebugKeyHandler() {
	}
}
