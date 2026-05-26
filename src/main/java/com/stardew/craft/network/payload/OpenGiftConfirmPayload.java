package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

/**
 * Server → Client: ask the player to confirm giving a gift to an NPC.
 */
@SuppressWarnings("null")
public record OpenGiftConfirmPayload(
    String npcId,
    String itemDisplayNameJson,
    String npcDisplayName
) implements CustomPacketPayload {

    public static final Type<OpenGiftConfirmPayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "open_gift_confirm"));

    public static final StreamCodec<FriendlyByteBuf, OpenGiftConfirmPayload> STREAM_CODEC = StreamCodec.of(
        (buf, payload) -> {
            buf.writeUtf(payload.npcId(), 64);
            buf.writeUtf(payload.itemDisplayNameJson(), 1024);
            buf.writeUtf(payload.npcDisplayName(), 256);
        },
        buf -> new OpenGiftConfirmPayload(buf.readUtf(64), buf.readUtf(1024), buf.readUtf(256))
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OpenGiftConfirmPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> handleClient(payload));
    }

    @net.neoforged.api.distmarker.OnlyIn(net.neoforged.api.distmarker.Dist.CLIENT)
    private static void handleClient(OpenGiftConfirmPayload payload) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.player == null) return;

        Component itemDisplayName = parseComponent(payload.itemDisplayNameJson(), mc);
        Component question = Component.translatable(
            "stardewcraft.npc.gift.confirm",
            itemDisplayName,
            payload.npcDisplayName()
        );

        mc.setScreen(com.stardew.craft.client.gui.common.StardewConfirmDialogScreen.createQuestionDialog(
            com.stardew.craft.client.gui.common.StardewQuestionDialogSpec.of(
                question,
                List.of(
                    Component.translatable("stardewcraft.npc.gift.confirm.yes"),
                    Component.translatable("stardewcraft.npc.gift.confirm.no")
                ),
                index -> {
                    PacketDistributor.sendToServer(
                        new ConfirmGiftPayload(payload.npcId(), index == 0)
                    );
                },
                -1
            )
        ));
    }

    @net.neoforged.api.distmarker.OnlyIn(net.neoforged.api.distmarker.Dist.CLIENT)
    private static Component parseComponent(String json, net.minecraft.client.Minecraft mc) {
        try {
            Component component = Component.Serializer.fromJson(json, mc.level.registryAccess());
            if (component != null) {
                return component;
            }
        } catch (Exception ignored) {
        }
        return Component.literal(json);
    }
}
