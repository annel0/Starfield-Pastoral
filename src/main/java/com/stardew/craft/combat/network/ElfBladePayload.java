package com.stardew.craft.combat.network;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.client.weapon.ElfBladeClientState;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ElfBladePayload(boolean active, int durationTicks) implements CustomPacketPayload {

    @SuppressWarnings("null")
    public static final Type<ElfBladePayload> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "elf_blade_state")
    );

    @SuppressWarnings("null")
    public static final StreamCodec<ByteBuf, ElfBladePayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL,
        ElfBladePayload::active,
        ByteBufCodecs.VAR_INT,
        ElfBladePayload::durationTicks,
        ElfBladePayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ElfBladePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (payload.active()) {
                Minecraft mc = Minecraft.getInstance();
                long nowTick = mc.level != null ? mc.level.getGameTime() : 0L;
                ElfBladeClientState.start(nowTick, payload.durationTicks());
            } else {
                ElfBladeClientState.clear();
            }
        });
    }
}
