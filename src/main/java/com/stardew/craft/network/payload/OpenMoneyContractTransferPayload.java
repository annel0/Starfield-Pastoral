package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

@SuppressWarnings("null")
public record OpenMoneyContractTransferPayload(int money, UUID targetId, String targetName) implements CustomPacketPayload {
    public static final Type<OpenMoneyContractTransferPayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "open_money_contract_transfer"));

    public static final StreamCodec<FriendlyByteBuf, OpenMoneyContractTransferPayload> STREAM_CODEC = StreamCodec.of(
        (buf, payload) -> {
            buf.writeVarInt(payload.money());
            buf.writeUUID(payload.targetId());
            buf.writeUtf(payload.targetName(), 128);
        },
        buf -> new OpenMoneyContractTransferPayload(buf.readVarInt(), buf.readUUID(), buf.readUtf(128)));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OpenMoneyContractTransferPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> handleClient(payload));
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleClient(OpenMoneyContractTransferPayload payload) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.player != null) {
            mc.setScreen(new com.stardew.craft.client.gui.MoneyContractTransferScreen(
                    payload.money(), payload.targetId(), payload.targetName()));
        }
    }
}
