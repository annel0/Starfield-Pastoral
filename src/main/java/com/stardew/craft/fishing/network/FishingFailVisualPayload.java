package com.stardew.craft.fishing.network;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.client.fishing.FishingCatchVisuals;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/** Server -> client: show a short "failed/escaped" visual. */
public record FishingFailVisualPayload() implements CustomPacketPayload {
	@SuppressWarnings("null")
	public static final Type<FishingFailVisualPayload> TYPE = new Type<>(
			ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "fishing_fail_visual")
	);
	public static final StreamCodec<ByteBuf, FishingFailVisualPayload> STREAM_CODEC = StreamCodec.unit(new FishingFailVisualPayload());

	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(FishingFailVisualPayload payload, IPayloadContext context) {
		context.enqueueWork(() -> {
			Minecraft mc = Minecraft.getInstance();
			if (mc.player == null) {
				return;
			}
			FishingCatchVisuals.startFail();
		});
	}
}
