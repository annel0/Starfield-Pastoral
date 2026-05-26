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
public record OpenFlowerDanceInvitePayload(String npcId) implements CustomPacketPayload {
    public static final Type<OpenFlowerDanceInvitePayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "open_flower_dance_invite"));

    public static final StreamCodec<FriendlyByteBuf, OpenFlowerDanceInvitePayload> STREAM_CODEC = StreamCodec.of(
        (buf, payload) -> buf.writeUtf(payload.npcId(), 64),
        buf -> new OpenFlowerDanceInvitePayload(buf.readUtf(64))
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OpenFlowerDanceInvitePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> handleClient(payload));
    }

    @net.neoforged.api.distmarker.OnlyIn(net.neoforged.api.distmarker.Dist.CLIENT)
    private static void handleClient(OpenFlowerDanceInvitePayload payload) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }
        Component npcName = Component.translatable("entity.stardewcraft.npc." + payload.npcId());
        Component question = Component.translatable("message.stardewcraft.festival.flower_dance.invite_npc", npcName);
        mc.setScreen(com.stardew.craft.client.gui.common.StardewConfirmDialogScreen.createQuestionDialog(
            com.stardew.craft.client.gui.common.StardewQuestionDialogSpec.of(
                question,
                List.of(
                    Component.translatable("message.stardewcraft.festival.confirm.yes"),
                    Component.translatable("message.stardewcraft.festival.confirm.no")
                ),
                index -> PacketDistributor.sendToServer(new FlowerDanceInviteResponsePayload(payload.npcId(), index == 0)),
                -1
            )
        ));
    }
}
