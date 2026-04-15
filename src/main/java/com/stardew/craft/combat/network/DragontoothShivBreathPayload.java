package com.stardew.craft.combat.network;

import com.stardew.craft.StardewCraft;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record DragontoothShivBreathPayload(boolean active, int durationTicks) implements CustomPacketPayload {

    @SuppressWarnings("null")
    public static final Type<DragontoothShivBreathPayload> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "dragontooth_shiv_breath_state")
    );

    @SuppressWarnings("null")
    public static final StreamCodec<ByteBuf, DragontoothShivBreathPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL,
        DragontoothShivBreathPayload::active,
        ByteBufCodecs.VAR_INT,
        DragontoothShivBreathPayload::durationTicks,
        DragontoothShivBreathPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(DragontoothShivBreathPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> handleClient(payload));
    }

    @net.neoforged.api.distmarker.OnlyIn(net.neoforged.api.distmarker.Dist.CLIENT)
    private static void handleClient(DragontoothShivBreathPayload payload) {
        if (payload.active()) {
            net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
            long nowTick = mc.level != null ? mc.level.getGameTime() : 0L;
            com.stardew.craft.client.weapon.DragontoothShivBreathClientState.start(nowTick, payload.durationTicks());
        } else {
            com.stardew.craft.client.weapon.DragontoothShivBreathClientState.clear();
        }
    }
}
