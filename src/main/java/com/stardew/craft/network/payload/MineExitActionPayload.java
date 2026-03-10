package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.menu.MineExitMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * 矿井出口传送操作数据包
 * 客户端 -> 服务端
 */
public record MineExitActionPayload(Action action) implements CustomPacketPayload {
	
	@SuppressWarnings("null")
	public static final Type<MineExitActionPayload> TYPE = 
			new Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "mine_exit_action"));
	
	@SuppressWarnings("null")
	public static final StreamCodec<FriendlyByteBuf, MineExitActionPayload> STREAM_CODEC = StreamCodec.of(
			(buf, payload) -> buf.writeEnum(payload.action),
			buf -> new MineExitActionPayload(buf.readEnum(Action.class))
	);
	
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
	
	/**
	 * 传送操作类型
	 */
	public enum Action {
		GO_UP_FLOOR,  // 返回上一层
		GO_TO_FLOOR_0, // 返回第0层
		EXIT_MINE      // 退出矿井
	}
	
	/**
	 * 服务端处理逻辑
	 */
	public static void handle(MineExitActionPayload payload, IPayloadContext context) {
		context.enqueueWork(() -> {
			if (context.player() instanceof ServerPlayer serverPlayer) {
				// 获取玩家当前打开的Menu
				if (serverPlayer.containerMenu instanceof MineExitMenu mineExitMenu) {
					switch (payload.action) {
						case GO_UP_FLOOR -> {
							int currentFloor = mineExitMenu.getCurrentFloor();
							if (currentFloor > 0) {
								mineExitMenu.teleportToFloor(currentFloor - 1);
							}
						}
						case GO_TO_FLOOR_0 -> mineExitMenu.teleportToFloor(0);
						case EXIT_MINE -> mineExitMenu.exitMine();
					}
				}
			}
		});
	}
}
