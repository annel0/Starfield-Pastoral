package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.farm.FarmInstanceRegistry;
import com.stardew.craft.item.ModItems;
import com.stardew.craft.player.PlayerDataManager;
import com.stardew.craft.sewer.SewerStoryFlags;
import com.stardew.craft.warp.WarpDestination;
import com.stardew.craft.warp.WarpDestinations;
import com.stardew.craft.warp.WarpEffects;
import com.stardew.craft.warp.WarpWandSavedData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Client → Server: 玩家选择传送目的地。
 */
@SuppressWarnings("null")
public record WarpWandTeleportPayload(String destinationId) implements CustomPacketPayload {

    public static final Type<WarpWandTeleportPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "warp_wand_teleport"));

    public static final StreamCodec<FriendlyByteBuf, WarpWandTeleportPayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> buf.writeUtf(payload.destinationId),
            buf -> new WarpWandTeleportPayload(buf.readUtf())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(WarpWandTeleportPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            // 校验手持传送魔杖
            if (!player.getMainHandItem().is(ModItems.WARP_WAND.get())
                    && !player.getOffhandItem().is(ModItems.WARP_WAND.get())) {
                return;
            }
            if (!PlayerDataManager.getPlayerData(player).hasMailFlag(SewerStoryFlags.RETURN_SCEPTER_PURCHASED)) {
                player.displayClientMessage(Component.translatable("stardewcraft.warp_wand.not_purchased"), true);
                return;
            }

            WarpDestination dest = WarpDestinations.getById(payload.destinationId);
            if (dest == null) return;

            // 校验已解锁
            WarpWandSavedData data = WarpWandSavedData.get();
            if (!data.isUnlocked(player.getUUID(), dest.id())) return;

            if (dest.requiresPlayerFarm()
                    && FarmInstanceRegistry.get().getFarmForPlayer(player.getUUID()) == null) {
                player.displayClientMessage(Component.translatable("stardewcraft.warp.farm.unavailable"), true);
                return;
            }

            // 执行传送
            WarpEffects.teleport(player, dest);
        });
    }
}
