package com.stardew.craft.combat.network;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.client.weapon.BrokenTridentCatchClientState;
import com.stardew.craft.client.weapon.SkillEffectsClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record BrokenTridentCatchPayload(boolean active, int durationTicks) implements CustomPacketPayload {

    @SuppressWarnings("null")
    public static final Type<BrokenTridentCatchPayload> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "broken_trident_catch")
    );

    @SuppressWarnings("null")
    public static final StreamCodec<ByteBuf, BrokenTridentCatchPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL,
        BrokenTridentCatchPayload::active,
        ByteBufCodecs.VAR_INT,
        BrokenTridentCatchPayload::durationTicks,
        BrokenTridentCatchPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(BrokenTridentCatchPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (payload.active()) {
                Minecraft mc = Minecraft.getInstance();
                long nowTick = mc.level != null ? mc.level.getGameTime() : 0L;
                boolean wasActive = mc.player != null && BrokenTridentCatchClientState.isActive(mc.player);
                BrokenTridentCatchClientState.start(nowTick, payload.durationTicks());
                if (!wasActive && mc.player != null) {
                    SkillEffectsClient.playFishcatchReady(mc.player);
                }
            } else {
                BrokenTridentCatchClientState.clear();
            }
        });
    }
}
