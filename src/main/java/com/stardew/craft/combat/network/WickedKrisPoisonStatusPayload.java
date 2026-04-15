package com.stardew.craft.combat.network;

import com.stardew.craft.StardewCraft;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record WickedKrisPoisonStatusPayload(int stacks, int durationTicks, int detonateRemainingTicks, int detonateTotalTicks)
    implements CustomPacketPayload {

    @SuppressWarnings("null")
    public static final Type<WickedKrisPoisonStatusPayload> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "wicked_kris_poison_status")
    );

    @SuppressWarnings("null")
    public static final StreamCodec<ByteBuf, WickedKrisPoisonStatusPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT,
        WickedKrisPoisonStatusPayload::stacks,
        ByteBufCodecs.VAR_INT,
        WickedKrisPoisonStatusPayload::durationTicks,
        ByteBufCodecs.INT,
        WickedKrisPoisonStatusPayload::detonateRemainingTicks,
        ByteBufCodecs.INT,
        WickedKrisPoisonStatusPayload::detonateTotalTicks,
        WickedKrisPoisonStatusPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(WickedKrisPoisonStatusPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> handleClient(payload));
    }

    @net.neoforged.api.distmarker.OnlyIn(net.neoforged.api.distmarker.Dist.CLIENT)
    private static void handleClient(WickedKrisPoisonStatusPayload payload) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }
        long nowTick = mc.level.getGameTime();
        if (payload.stacks() > 0 && payload.durationTicks() > 0) {
            com.stardew.craft.client.weapon.WickedKrisPoisonClientState.updatePoison(nowTick, payload.durationTicks(), payload.stacks());
        } else if (payload.stacks() <= 0) {
            com.stardew.craft.client.weapon.WickedKrisPoisonClientState.clearPoison();
        }
        if (payload.detonateRemainingTicks() >= 0) {
            if (payload.detonateRemainingTicks() > 0) {
                com.stardew.craft.client.weapon.WickedKrisPoisonClientState.updateDetonation(
                    nowTick,
                    payload.detonateRemainingTicks(),
                    payload.detonateTotalTicks()
                );
            } else {
                com.stardew.craft.client.weapon.WickedKrisPoisonClientState.clearDetonation();
            }
        }
    }
}
