package com.stardew.craft.network;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.manager.TreeGrowthManager;
import com.stardew.craft.tree.WildTrees;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record GrowTreesPayload() implements CustomPacketPayload {
	@SuppressWarnings("null")
	public static final Type<GrowTreesPayload> TYPE = new Type<>(
			ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "grow_trees")
	);
	public static final StreamCodec<ByteBuf, GrowTreesPayload> STREAM_CODEC = StreamCodec.unit(new GrowTreesPayload());

	@Override
	public Type<GrowTreesPayload> type() {
		return TYPE;
	}

	@SuppressWarnings("null")
	public static void handle(GrowTreesPayload payload, IPayloadContext context) {
		context.enqueueWork(() -> {
			Player player = context.player();
			Level level = player.level();
			BlockPos playerPos = player.blockPosition();

			if (!(level instanceof ServerLevel serverLevel)) {
				return;
			}

			TreeGrowthManager manager = TreeGrowthManager.get(serverLevel);

			for (int x = -5; x <= 5; x++) {
				for (int y = -2; y <= 2; y++) {
					for (int z = -5; z <= 5; z++) {
						BlockPos pos = playerPos.offset(x, y, z);
						@SuppressWarnings("null")
						BlockState state = level.getBlockState(pos);
						if (WildTrees.findBySapling(state) != null) {
							manager.growOneDay(serverLevel, pos);
						}
					}
				}
			}
		});
	}
}
