package com.stardew.craft.cutscene.command;

import com.stardew.craft.cutscene.runtime.EventPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

/**
 * message: display a message in the chat (non-NPC info text).
 * JSON: {"cmd":"message", "text":"stardewcraft.event.some_message"}
 * If text starts with "stardewcraft." or "event.", it's treated as a translation key.
 */
public class MessageCommand implements EventCommand {
    private final String text;

    public MessageCommand(String text) {
        this.text = text;
    }

    @SuppressWarnings("null")
    @Override
    public void start(EventPlayer player) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        Component message;
        if (text.startsWith("stardewcraft.") || text.startsWith("event.")) {
            message = Component.translatable(text);
        } else {
            message = Component.literal(text);
        }
        mc.player.sendSystemMessage(message);
    }

    @Override public void tick(EventPlayer player) {}
    @Override public boolean isComplete() { return true; }
}
