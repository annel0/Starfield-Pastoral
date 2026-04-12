package com.stardew.craft.network;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.blockentity.BeeHouseBlockEntity;
import com.stardew.craft.blockentity.CheesePressBlockEntity;
import com.stardew.craft.blockentity.CrabPotBlockEntity;
import com.stardew.craft.blockentity.CharcoalKilnBlockEntity;
import com.stardew.craft.blockentity.CrystalariumBlockEntity;
import com.stardew.craft.blockentity.DeluxeWormBinBlockEntity;
import com.stardew.craft.blockentity.DehydratorBlockEntity;
import com.stardew.craft.blockentity.BaitMakerBlockEntity;
import com.stardew.craft.blockentity.FishSmokerBlockEntity;
import com.stardew.craft.blockentity.FurnaceBlockEntity;
import com.stardew.craft.blockentity.KegBlockEntity;
import com.stardew.craft.blockentity.LightningRodBlockEntity;
import com.stardew.craft.blockentity.RecyclingMachineBlockEntity;
import com.stardew.craft.blockentity.MayonnaiseMachineBlockEntity;
import com.stardew.craft.blockentity.OilMakerBlockEntity;
import com.stardew.craft.blockentity.PreservesJarBlockEntity;
import com.stardew.craft.blockentity.SeedMakerBlockEntity;
import com.stardew.craft.blockentity.TapperBlockEntity;
import com.stardew.craft.blockentity.LoomBlockEntity;
import com.stardew.craft.blockentity.SolarPanelBlockEntity;
import com.stardew.craft.blockentity.WormBinBlockEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record AdvanceUtilitiesPayload() implements CustomPacketPayload {
	@SuppressWarnings("null")
	public static final Type<AdvanceUtilitiesPayload> TYPE = new Type<>(
			ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "advance_utilities")
	);
	public static final StreamCodec<ByteBuf, AdvanceUtilitiesPayload> STREAM_CODEC = StreamCodec.unit(new AdvanceUtilitiesPayload());

	@Override
	public Type<AdvanceUtilitiesPayload> type() {
		return TYPE;
	}

	@SuppressWarnings("null")
	public static void handle(AdvanceUtilitiesPayload payload, IPayloadContext context) {
		context.enqueueWork(() -> {
			Player player = context.player();
			Level level = player.level();
			BlockPos playerPos = player.blockPosition();

			if (!(level instanceof ServerLevel serverLevel)) {
				return;
			}

			for (int x = -5; x <= 5; x++) {
				for (int y = -2; y <= 2; y++) {
					for (int z = -5; z <= 5; z++) {
						BlockPos pos = playerPos.offset(x, y, z);
						if (!serverLevel.isLoaded(pos)) {
							continue;
						}
						@SuppressWarnings("null")
						var be = serverLevel.getBlockEntity(pos);
						if (be instanceof TapperBlockEntity tapper) {
							tapper.advanceDays(1);
						} else if (be instanceof KegBlockEntity keg) {
							keg.advanceDays(1);
						} else if (be instanceof PreservesJarBlockEntity preservesJar) {
							preservesJar.advanceDays(1);
						} else if (be instanceof CheesePressBlockEntity cheesePress) {
							cheesePress.advanceDays(1);
						} else if (be instanceof MayonnaiseMachineBlockEntity mayonnaiseMachine) {
							mayonnaiseMachine.advanceDays(1);
						} else if (be instanceof OilMakerBlockEntity oilMaker) {
							oilMaker.advanceDays(1);
						} else if (be instanceof BeeHouseBlockEntity beeHouse) {
							beeHouse.advanceDays(1);
						} else if (be instanceof LoomBlockEntity loom) {
							loom.advanceDays(1);
						} else if (be instanceof CrabPotBlockEntity crabPot) {
							crabPot.advanceDays(1);
						} else if (be instanceof CrystalariumBlockEntity crystalarium) {
							crystalarium.advanceDays(1);
						} else if (be instanceof DehydratorBlockEntity dehydrator) {
							dehydrator.advanceDays(1);
						} else if (be instanceof BaitMakerBlockEntity baitMaker) {
							baitMaker.advanceDays(1);
						} else if (be instanceof FishSmokerBlockEntity fishSmoker) {
							fishSmoker.advanceDays(1);
						} else if (be instanceof SeedMakerBlockEntity seedMaker) {
							seedMaker.advanceDays(1);
						} else if (be instanceof FurnaceBlockEntity furnace) {
							furnace.advanceDays(1);
						} else if (be instanceof CharcoalKilnBlockEntity kiln) {
							kiln.advanceDays(1);
						} else if (be instanceof LightningRodBlockEntity rod) {
							rod.advanceDays(1);
						} else if (be instanceof SolarPanelBlockEntity panel) {
							panel.advanceDays(1);
						} else if (be instanceof RecyclingMachineBlockEntity recyclingMachine) {
							recyclingMachine.advanceDays(1);
						} else if (be instanceof WormBinBlockEntity wormBin) {
							wormBin.advanceDays(1);
						} else if (be instanceof DeluxeWormBinBlockEntity wormBin) {
							wormBin.advanceDays(1);
						}
					}
				}
			}
		});
	}
}
