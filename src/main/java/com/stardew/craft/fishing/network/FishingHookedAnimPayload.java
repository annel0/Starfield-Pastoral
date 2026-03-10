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
 * Server -> client: the player reacted to the bite. Play the "Hooked!" animation before opening the minigame.
 */
public record FishingHookedAnimPayload(int durationTicks) implements CustomPacketPayload {
	@SuppressWarnings("null")
	public static final Type<FishingHookedAnimPayload> TYPE = new Type<>(
			ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "fishing_hooked_anim")
	);

	@SuppressWarnings("null")
	public static final StreamCodec<ByteBuf, FishingHookedAnimPayload> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, FishingHookedAnimPayload::durationTicks,
			FishingHookedAnimPayload::new
	);

	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(FishingHookedAnimPayload payload, IPayloadContext context) {
		context.enqueueWork(() -> {
			Minecraft mc = Minecraft.getInstance();
			if (mc == null || mc.player == null) {
				return;
			}
			FishingBiteVisuals.startHookedAnim(payload.durationTicks());
		});
	}
}
