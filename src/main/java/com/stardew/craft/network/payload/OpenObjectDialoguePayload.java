package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("null")
public record OpenObjectDialoguePayload(List<Component> messages) implements CustomPacketPayload {
    public static final Type<OpenObjectDialoguePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "open_object_dialogue"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenObjectDialoguePayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeVarInt(payload.messages().size());
                for (Component message : payload.messages()) {
                    ComponentSerialization.TRUSTED_STREAM_CODEC.encode(buf, message);
                }
            },
            buf -> {
                int count = buf.readVarInt();
                List<Component> messages = new ArrayList<>(count);
                for (int i = 0; i < count; i++) {
                    messages.add(ComponentSerialization.TRUSTED_STREAM_CODEC.decode(buf));
                }
                return new OpenObjectDialoguePayload(messages);
            });

    public OpenObjectDialoguePayload(Component message) {
        this(List.of(message));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OpenObjectDialoguePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> handleClient(payload));
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleClient(OpenObjectDialoguePayload payload) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }
        mc.setScreen(new com.stardew.craft.client.gui.common.StardewObjectDialogueScreen(payload.messages()));
    }
}
