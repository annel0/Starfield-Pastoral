package com.stardew.craft.combat.network;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.client.weapon.DarkSwordBloodDebtClientState;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record DarkSwordBloodDebtPayload(boolean active, int durationTicks) implements CustomPacketPayload {

    @SuppressWarnings("null")
    public static final Type<DarkSwordBloodDebtPayload> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "dark_sword_blood_debt_state")
    );

    @SuppressWarnings("null")
    public static final StreamCodec<ByteBuf, DarkSwordBloodDebtPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL,
        DarkSwordBloodDebtPayload::active,
        ByteBufCodecs.VAR_INT,
        DarkSwordBloodDebtPayload::durationTicks,
        DarkSwordBloodDebtPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(DarkSwordBloodDebtPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> handleClient(payload));
    }

    @net.neoforged.api.distmarker.OnlyIn(net.neoforged.api.distmarker.Dist.CLIENT)
    private static void handleClient(DarkSwordBloodDebtPayload payload) {
        if (payload.active()) {
            Minecraft mc = Minecraft.getInstance();
            long nowTick = mc.level != null ? mc.level.getGameTime() : 0L;
            DarkSwordBloodDebtClientState.start(nowTick, payload.durationTicks());
        } else {
            DarkSwordBloodDebtClientState.clear();
        }
    }
}
