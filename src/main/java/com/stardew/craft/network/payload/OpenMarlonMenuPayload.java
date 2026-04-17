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
 * Server → Client: show Marlon's question dialog (Shop / Gil / Recovery / Leave).
 * hasLostItems: if true, client shows the "Item Recovery" option (SDV parity).
 */
@SuppressWarnings("null")
public record OpenMarlonMenuPayload(boolean hasLostItems) implements CustomPacketPayload {

    public static final Type<OpenMarlonMenuPayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "open_marlon_menu"));

    public static final StreamCodec<FriendlyByteBuf, OpenMarlonMenuPayload> STREAM_CODEC = StreamCodec.of(
        (buf, payload) -> buf.writeBoolean(payload.hasLostItems()),
        buf -> new OpenMarlonMenuPayload(buf.readBoolean())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OpenMarlonMenuPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> handleClient(payload));
    }

    @net.neoforged.api.distmarker.OnlyIn(net.neoforged.api.distmarker.Dist.CLIENT)
    private static void handleClient(OpenMarlonMenuPayload payload) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.player == null) return;

        // Build options list dynamically (SDV: Shop / Recovery / Gil / Leave)
        List<Component> options = new java.util.ArrayList<>();
        options.add(Component.translatable("stardewcraft.npc.marlon.menu.shop"));       // 0 → Shop
        options.add(Component.translatable("stardewcraft.npc.marlon.menu.gil"));        // 1 → Gil
        if (payload.hasLostItems()) {
            options.add(Component.translatable("stardewcraft.npc.marlon.menu.recovery"));  // 2 → Recovery
        }
        options.add(Component.translatable("stardewcraft.npc.marlon.menu.leave"));      // last → Leave

        mc.setScreen(com.stardew.craft.client.gui.common.StardewConfirmDialogScreen.createQuestionDialog(
            com.stardew.craft.client.gui.common.StardewQuestionDialogSpec.of(
                Component.translatable("stardewcraft.npc.marlon.menu.question"),
                options,
                index -> {
                    // Map index to choice: 0=Shop, 1=Gil, 2=Recovery (if present), last=Leave
                    if (index == 0) {
                        PacketDistributor.sendToServer(new MarlonMenuChoicePayload(0)); // Shop
                    } else if (index == 1) {
                        PacketDistributor.sendToServer(new MarlonMenuChoicePayload(1)); // Gil
                    } else if (payload.hasLostItems() && index == 2) {
                        PacketDistributor.sendToServer(new MarlonMenuChoicePayload(2)); // Recovery
                    }
                    // else → Leave, do nothing
                },
                -1
            )
        ));
    }
}
