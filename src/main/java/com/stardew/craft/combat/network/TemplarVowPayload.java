package com.stardew.craft.combat.network;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.client.weapon.TemplarVowClientState;
import com.stardew.craft.client.weapon.WeaponSkillAnimationClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record TemplarVowPayload(boolean active, int durationTicks) implements CustomPacketPayload {

    @SuppressWarnings("null")
    public static final Type<TemplarVowPayload> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "templar_vow")
    );

    @SuppressWarnings("null")
    public static final StreamCodec<ByteBuf, TemplarVowPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL,
        TemplarVowPayload::active,
        ByteBufCodecs.VAR_INT,
        TemplarVowPayload::durationTicks,
        TemplarVowPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(TemplarVowPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            var mc = net.minecraft.client.Minecraft.getInstance();
            if (mc.level == null) return;
            long nowTick = mc.level.getGameTime();
            if (payload.active()) {
                TemplarVowClientState.start(nowTick, payload.durationTicks());
            } else {
                TemplarVowClientState.clear();
                WeaponSkillAnimationClient.stop();
            }
        });
    }
}
