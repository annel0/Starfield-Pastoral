package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.client.gui.common.StardewNpcDialogueScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Server → Client: open the NPC dialogue screen.
 * Server sends the translate key only. Client resolves via
 * {@code Component.translatable(key).getString()} — identical to TVScreen.
 */
@SuppressWarnings("null")
public record OpenNpcDialogueScreenPayload(
        String npcId,
        String translateKey,
        int friendshipPoints
) implements CustomPacketPayload {

    @SuppressWarnings("null")
    public static final Type<OpenNpcDialogueScreenPayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "open_npc_dialogue_screen"));

    @SuppressWarnings("null")
    public static final StreamCodec<FriendlyByteBuf, OpenNpcDialogueScreenPayload> STREAM_CODEC = StreamCodec.of(
        (buf, payload) -> {
            buf.writeUtf(payload.npcId(), 64);
            buf.writeUtf(payload.translateKey(), 512);
            buf.writeInt(payload.friendshipPoints());
        },
        buf -> new OpenNpcDialogueScreenPayload(
            buf.readUtf(64), buf.readUtf(512), buf.readInt())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OpenNpcDialogueScreenPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> handleClient(payload));
    }

    @net.neoforged.api.distmarker.OnlyIn(net.neoforged.api.distmarker.Dist.CLIENT)
    private static void handleClient(OpenNpcDialogueScreenPayload payload) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }

        String rawKey = payload.translateKey();
        StringBuilder prefixBuilder = new StringBuilder();
        // Extract all $x prefixes directly mapped to the translatable key.
        // E.g., "$h$bstardewcraft.key" -> prefix="$h$b", rawKey="stardewcraft.key"
        int i = 0;
        while (i + 1 < rawKey.length() && rawKey.charAt(i) == '$') {
            // Find end of token, e.g. `$h` or `$12`
            int j = i + 1;
            while (j < rawKey.length() && Character.isLetterOrDigit(rawKey.charAt(j))) {
                j++;
            }
            if (j == i + 1) break; // Just '$' without anything after
            prefixBuilder.append(rawKey, i, j);
            i = j;
        }

        String trueKey = rawKey.substring(i);
        String displayText = Component.translatable(trueKey).getString();

        String finalDisplayText = prefixBuilder.toString() + displayText;

        // Replace @ with player name
        String playerName = mc.player.getGameProfile() != null ? mc.player.getGameProfile().getName() : "player";
        if (playerName == null || playerName.isBlank()) playerName = "player";
        finalDisplayText = finalDisplayText.replace("@", playerName);

        StardewCraft.LOGGER.info("[NPC_DIALOGUE_CLIENT] key={} resolved(first80)={}",
            payload.translateKey(),
            finalDisplayText.length() > 80 ? finalDisplayText.substring(0, 80) : finalDisplayText);

        mc.setScreen(new StardewNpcDialogueScreen(payload.npcId(), finalDisplayText, payload.friendshipPoints()));
    }
}
