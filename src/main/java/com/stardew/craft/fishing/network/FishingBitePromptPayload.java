package com.stardew.craft.fishing.network;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.client.fishing.FishingBiteVisuals;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/**
 * Server -> client: a fish has bitten. Show an obvious bite prompt (exclamation) and a short bobber dip.
 */
public record FishingBitePromptPayload(int hookEntityId, int durationTicks) implements CustomPacketPayload {
	@SuppressWarnings("null")
	public static final Type<FishingBitePromptPayload> TYPE = new Type<>(
			ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "fishing_bite_prompt")
	);

	@SuppressWarnings("null")
	public static final StreamCodec<ByteBuf, FishingBitePromptPayload> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, FishingBitePromptPayload::hookEntityId,
			ByteBufCodecs.VAR_INT, FishingBitePromptPayload::durationTicks,
			FishingBitePromptPayload::new
	);

	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(FishingBitePromptPayload payload, IPayloadContext context) {
		context.enqueueWork(() -> {
			Minecraft mc = Minecraft.getInstance();
			if (mc == null || mc.player == null) {
				return;
			}
			FishingBiteVisuals.startBitePrompt(payload.hookEntityId(), payload.durationTicks());
		});
	}
}
