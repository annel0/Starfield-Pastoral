package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

/**
 * 打开宝箱UI的网络数据包
 * 服务器 -> 客户端
 * 注意：这个数据包会在鱼动画播放完毕后，由客户端延迟显示
 */
public record OpenTreasureChestPayload(
		List<ItemStack> items,
		boolean isGolden
) implements CustomPacketPayload {
	
	@SuppressWarnings("null")
	public static final Type<OpenTreasureChestPayload> TYPE = 
			new Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "open_treasure_chest"));
	
	@SuppressWarnings("null")
	public static final StreamCodec<RegistryFriendlyByteBuf, OpenTreasureChestPayload> STREAM_CODEC = StreamCodec.composite(
			ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()),
			OpenTreasureChestPayload::items,
			ByteBufCodecs.BOOL,
			OpenTreasureChestPayload::isGolden,
			OpenTreasureChestPayload::new
	);
	
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
	
	public static void handle(OpenTreasureChestPayload payload, IPayloadContext context) {
		context.enqueueWork(() -> {
			Minecraft mc = Minecraft.getInstance();
			if (mc.player != null && mc.level != null) {
				// 将宝箱数据传递给FishingCatchVisuals，让它在鱼动画结束后打开
				com.stardew.craft.client.fishing.FishingCatchVisuals.setPendingTreasure(payload.items(), payload.isGolden());
			}
		});
	}
}

