package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.client.gui.common.StardewConfirmDialogScreen;
import com.stardew.craft.client.gui.common.StardewQuestionDialogSpec;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

/**
 * Server → Client: show Marlon's question dialog (Shop / Leave).
 */
@SuppressWarnings("null")
public record OpenMarlonMenuPayload() implements CustomPacketPayload {

    public static final Type<OpenMarlonMenuPayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "open_marlon_menu"));

    public static final StreamCodec<FriendlyByteBuf, OpenMarlonMenuPayload> STREAM_CODEC = StreamCodec.of(
        (buf, payload) -> {},
        buf -> new OpenMarlonMenuPayload()
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
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        mc.setScreen(StardewConfirmDialogScreen.createQuestionDialog(
            StardewQuestionDialogSpec.of(
                Component.translatable("stardewcraft.npc.marlon.menu.question"),
                List.of(
                    Component.translatable("stardewcraft.npc.marlon.menu.shop"),
                    Component.translatable("stardewcraft.npc.marlon.menu.gil"),
                    Component.translatable("stardewcraft.npc.marlon.menu.leave")
                ),
                index -> {
                    if (index == 0) {
                        PacketDistributor.sendToServer(new MarlonMenuChoicePayload(0));
                    } else if (index == 1) {
                        PacketDistributor.sendToServer(new MarlonMenuChoicePayload(1));
                    }
                    // index == 2 → Leave, do nothing
                },
                -1
            )
        ));
    }
}
