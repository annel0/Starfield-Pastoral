package com.stardew.craft.combat.network;

import com.stardew.craft.StardewCraft;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ObsidianResonanceSyncPayload(boolean active, int remainingTicks, int totalTicks)
    implements CustomPacketPayload {

    @SuppressWarnings("null")
    public static final Type<ObsidianResonanceSyncPayload> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "obsidian_resonance_sync")
    );

    @SuppressWarnings("null")
    public static final StreamCodec<ByteBuf, ObsidianResonanceSyncPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL,
        ObsidianResonanceSyncPayload::active,
        ByteBufCodecs.VAR_INT,
        ObsidianResonanceSyncPayload::remainingTicks,
        ByteBufCodecs.VAR_INT,
        ObsidianResonanceSyncPayload::totalTicks,
        ObsidianResonanceSyncPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ObsidianResonanceSyncPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> handleClient(payload));
    }

    @net.neoforged.api.distmarker.OnlyIn(net.neoforged.api.distmarker.Dist.CLIENT)
    private static void handleClient(ObsidianResonanceSyncPayload payload) {
        var mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.level == null) return;
        long nowTick = mc.level.getGameTime();
        if (payload.active()) {
            com.stardew.craft.client.weapon.ObsidianResonanceClientState.sync(nowTick, payload.remainingTicks(), payload.totalTicks());
        } else {
            com.stardew.craft.client.weapon.ObsidianResonanceClientState.clear();
        }
    }
}
