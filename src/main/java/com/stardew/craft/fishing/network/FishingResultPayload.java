package com.stardew.craft.fishing.network;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.fishing.server.FishingSessionManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record FishingResultPayload(UUID sessionId, boolean success, float catchProgress, boolean treasureCaught, int numCaught) implements CustomPacketPayload {
	@SuppressWarnings("null")
	public static final Type<FishingResultPayload> TYPE = new Type<>(
			ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "fishing_result")
	);

	private static final StreamCodec<ByteBuf, UUID> UUID_STREAM_CODEC = new StreamCodec<>() {
		@Override
		public UUID decode(@SuppressWarnings("null") ByteBuf buf) {
			long msb = buf.readLong();
			long lsb = buf.readLong();
			return new UUID(msb, lsb);
		}

		@Override
		public void encode(@SuppressWarnings("null") ByteBuf buf, @SuppressWarnings("null") UUID value) {
			buf.writeLong(value.getMostSignificantBits());
			buf.writeLong(value.getLeastSignificantBits());
		}
	};

	@SuppressWarnings("null")
	public static final StreamCodec<ByteBuf, FishingResultPayload> STREAM_CODEC = StreamCodec.composite(
			UUID_STREAM_CODEC,
			FishingResultPayload::sessionId,
			ByteBufCodecs.BOOL,
			FishingResultPayload::success,
			ByteBufCodecs.FLOAT,
			FishingResultPayload::catchProgress,
			ByteBufCodecs.BOOL,
			FishingResultPayload::treasureCaught,
			ByteBufCodecs.VAR_INT,
			FishingResultPayload::numCaught,
			FishingResultPayload::new
	);

	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(FishingResultPayload payload, IPayloadContext context) {
		context.enqueueWork(() -> {
			if (!(context.player() instanceof net.minecraft.server.level.ServerPlayer sp)) {
				return;
			}
			FishingSessionManager.get(sp.server).handleResult(sp, payload.sessionId(), payload.success(), payload.catchProgress(), payload.treasureCaught(), payload.numCaught());
		});
	}
}
