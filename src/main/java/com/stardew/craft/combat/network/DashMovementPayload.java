package com.stardew.craft.combat.network;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.client.weapon.DashMovementClientState;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record DashMovementPayload(boolean active, int durationTicks, double endX, double endY, double endZ)
    implements CustomPacketPayload {

    @SuppressWarnings("null")
    public static final Type<DashMovementPayload> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "dash_movement_state")
    );

    @SuppressWarnings("null")
    public static final StreamCodec<ByteBuf, DashMovementPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL,
        DashMovementPayload::active,
        ByteBufCodecs.VAR_INT,
        DashMovementPayload::durationTicks,
        ByteBufCodecs.DOUBLE,
        DashMovementPayload::endX,
        ByteBufCodecs.DOUBLE,
        DashMovementPayload::endY,
        ByteBufCodecs.DOUBLE,
        DashMovementPayload::endZ,
        DashMovementPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(DashMovementPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> handleClient(payload));
    }

    @net.neoforged.api.distmarker.OnlyIn(net.neoforged.api.distmarker.Dist.CLIENT)
    private static void handleClient(DashMovementPayload payload) {
        if (payload.active()) {
            Minecraft mc = Minecraft.getInstance();
            long nowTick = mc.level != null ? mc.level.getGameTime() : 0L;
            Vec3 end = new Vec3(payload.endX(), payload.endY(), payload.endZ());
            DashMovementClientState.start(nowTick, payload.durationTicks(), end);
        } else {
            DashMovementClientState.clear();
        }
    }
}
