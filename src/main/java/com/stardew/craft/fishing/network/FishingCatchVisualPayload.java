package com.stardew.craft.fishing.network;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.client.fishing.FishingCatchVisuals;
import com.stardew.craft.sound.ModSounds;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/**
 * Server -> client: trigger the post-catch presentation (SV-style popup, then item-activation animation).
 */
@SuppressWarnings("unused")
public record FishingCatchVisualPayload(ResourceLocation itemId, int count) implements CustomPacketPayload {
	@SuppressWarnings("null")
	public static final Type<FishingCatchVisualPayload> TYPE = new Type<>(
			ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "fishing_catch_visual")
	);

	private static final StreamCodec<ByteBuf, ResourceLocation> RESOURCE_LOCATION_STREAM_CODEC = new StreamCodec<>() {
		@SuppressWarnings("null")
		@Override
		public ResourceLocation decode(@SuppressWarnings("null") ByteBuf buf) {
			@SuppressWarnings("null")
			String s = ByteBufCodecs.STRING_UTF8.decode(buf);
			return ResourceLocation.tryParse(s);
		}

		@SuppressWarnings("null")
		@Override
		public void encode(@SuppressWarnings("null") ByteBuf buf, @SuppressWarnings("null") ResourceLocation value) {
			ByteBufCodecs.STRING_UTF8.encode(buf, value.toString());
		}
	};

	@SuppressWarnings("null")
	public static final StreamCodec<ByteBuf, FishingCatchVisualPayload> STREAM_CODEC = StreamCodec.composite(
			RESOURCE_LOCATION_STREAM_CODEC,
			FishingCatchVisualPayload::itemId,
			ByteBufCodecs.VAR_INT,
			FishingCatchVisualPayload::count,
			FishingCatchVisualPayload::new
	);

	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	@SuppressWarnings("null")
	public static void handle(FishingCatchVisualPayload payload, IPayloadContext context) {
		context.enqueueWork(() -> {
			Minecraft mc = Minecraft.getInstance();
			if (mc.player == null) {
				return;
			}
			if (payload.itemId() == null || payload.count() <= 0) {
				return;
			}
			var item = BuiltInRegistries.ITEM.get(payload.itemId());
			if (item == null) {
				return;
			}
			ItemStack stack = new ItemStack(item, payload.count());
			if (stack.isEmpty()) {
				return;
			}

			// Stardew FishingRod.cs: on pullFishFromWater it plays pullItemFromWater + dwop.
			// Use playLocalSound for reliable client playback (mirrors SV's location.playSound semantics).
			mc.player.displayClientMessage(Component.translatable("stardewcraft.fishing.caught", stack.getHoverName()), true);

			FishingCatchVisuals.start(stack);
		});
	}
}
