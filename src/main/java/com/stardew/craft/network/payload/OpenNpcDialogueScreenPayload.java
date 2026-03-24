package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.client.gui.common.StardewNpcDialogueScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@SuppressWarnings("null")
public record OpenNpcDialogueScreenPayload(String npcId, String text, int friendshipPoints) implements CustomPacketPayload {
    @SuppressWarnings("null")
    public static final Type<OpenNpcDialogueScreenPayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "open_npc_dialogue_screen"));

    @SuppressWarnings("null")
    public static final StreamCodec<FriendlyByteBuf, OpenNpcDialogueScreenPayload> STREAM_CODEC = StreamCodec.of(
        (buf, payload) -> {
            buf.writeUtf(payload.npcId(), 64);
            buf.writeUtf(payload.text(), 2048);
            buf.writeInt(payload.friendshipPoints());
        },
        buf -> new OpenNpcDialogueScreenPayload(buf.readUtf(64), buf.readUtf(2048), buf.readInt())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OpenNpcDialogueScreenPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) {
                return;
            }
            mc.setScreen(new StardewNpcDialogueScreen(payload.npcId(), payload.text(), payload.friendshipPoints()));
        });
    }
}
