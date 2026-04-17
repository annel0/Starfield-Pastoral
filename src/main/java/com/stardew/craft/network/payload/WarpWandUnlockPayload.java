package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.item.ModItems;
import com.stardew.craft.player.PlayerStardewDataAPI;
import com.stardew.craft.sound.ModSounds;
import com.stardew.craft.warp.WarpDestination;
import com.stardew.craft.warp.WarpDestinations;
import com.stardew.craft.warp.WarpWandSavedData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Client → Server: 玩家请求用金币解锁传送目的地。
 */
@SuppressWarnings("null")
public record WarpWandUnlockPayload(String destinationId) implements CustomPacketPayload {

    public static final Type<WarpWandUnlockPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "warp_wand_unlock"));

    public static final StreamCodec<FriendlyByteBuf, WarpWandUnlockPayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> buf.writeUtf(payload.destinationId),
            buf -> new WarpWandUnlockPayload(buf.readUtf())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(WarpWandUnlockPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            // 校验手持传送魔杖
            if (!player.getMainHandItem().is(ModItems.WARP_WAND.get())
                    && !player.getOffhandItem().is(ModItems.WARP_WAND.get())) {
                return;
            }

            WarpDestination dest = WarpDestinations.getById(payload.destinationId);
            if (dest == null) return;

            WarpWandSavedData data = WarpWandSavedData.get();

            // 已解锁则忽略
            if (data.isUnlocked(player.getUUID(), dest.id())) return;

            // 扣除金币
            if (dest.cost() > 0) {
                boolean ok = PlayerStardewDataAPI.removeMoney(player, dest.cost());
                if (!ok) {
                    // 金币不足 — 客户端自行显示提示
                    return;
                }
            }

            // 解锁
            data.unlock(player.getUUID(), dest.id());
            StardewCraft.LOGGER.info("[WARP] Player {} unlocked destination '{}' for {}g",
                    player.getName().getString(), dest.id(), dest.cost());

            // 播放解锁音效
            player.serverLevel().playSound(null, player.blockPosition(),
                    ModSounds.NEW_RECIPE.get(), SoundSource.PLAYERS, 1.0f, 1.0f);

            // 同步回客户端
            PacketDistributor.sendToPlayer(player,
                    new WarpWandSyncPayload(data.getUnlockedDestinations(player.getUUID())));
        });
    }
}
