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

@SuppressWarnings("null")
public record OpenBooksellerMenuPayload() implements CustomPacketPayload {
    public static final Type<OpenBooksellerMenuPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "open_bookseller_menu"));

    public static final StreamCodec<FriendlyByteBuf, OpenBooksellerMenuPayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {
            },
            buf -> new OpenBooksellerMenuPayload()
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OpenBooksellerMenuPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> handleClient());
    }

    @net.neoforged.api.distmarker.OnlyIn(net.neoforged.api.distmarker.Dist.CLIENT)
    private static void handleClient() {
        net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }

        List<Component> responses = List.of(
                Component.translatable("stardewcraft.bookseller.menu.buy"),
                Component.translatable("stardewcraft.bookseller.menu.trade"),
                Component.translatable("stardewcraft.bookseller.menu.leave")
        );
        com.stardew.craft.client.gui.common.StardewQuestionDialogSpec spec =
                com.stardew.craft.client.gui.common.StardewQuestionDialogSpec.of(
                        Component.translatable("stardewcraft.bookseller.menu.question"),
                        responses,
                        choiceIndex -> PacketDistributor.sendToServer(new BooksellerActionPayload(choiceIndex)),
                        2
                );
        minecraft.setScreen(com.stardew.craft.client.gui.common.StardewConfirmDialogScreen.createQuestionDialog(spec));
    }
}
