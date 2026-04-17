package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.client.warp.WarpWandClientState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashSet;
import java.util.Set;

/**
 * Server → Client: 同步玩家已解锁的传送目的地。
 */
@SuppressWarnings("null")
public record WarpWandSyncPayload(Set<String> unlockedDestinations) implements CustomPacketPayload {

    public static final Type<WarpWandSyncPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "warp_wand_sync"));

    public static final StreamCodec<FriendlyByteBuf, WarpWandSyncPayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeVarInt(payload.unlockedDestinations.size());
                for (String id : payload.unlockedDestinations) {
                    buf.writeUtf(id);
                }
            },
            buf -> {
                int size = buf.readVarInt();
                Set<String> set = new HashSet<>();
                for (int i = 0; i < size; i++) {
                    set.add(buf.readUtf());
                }
                return new WarpWandSyncPayload(set);
            }
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(WarpWandSyncPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> WarpWandClientState.setUnlocked(payload.unlockedDestinations));
    }
}
