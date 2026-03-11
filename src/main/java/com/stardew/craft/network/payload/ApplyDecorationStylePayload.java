package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.deco.DecorationService;
import com.stardew.craft.deco.DecorationStyleRegistry;
import com.stardew.craft.deco.DecorationType;
import com.stardew.craft.item.ModItems;
import com.stardew.craft.player.PlayerStardewDataAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ApplyDecorationStylePayload(String decorationType, BlockPos targetPos, String styleId) implements CustomPacketPayload {
    @SuppressWarnings("null")
    public static final Type<ApplyDecorationStylePayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "apply_decoration_style"));

    @SuppressWarnings("null")
    public static final StreamCodec<FriendlyByteBuf, ApplyDecorationStylePayload> STREAM_CODEC = StreamCodec.of(
        (buf, payload) -> {
            buf.writeUtf(payload.decorationType(), 32);
            buf.writeBlockPos(payload.targetPos());
            buf.writeUtf(payload.styleId(), 64);
        },
        buf -> new ApplyDecorationStylePayload(buf.readUtf(32), buf.readBlockPos(), buf.readUtf(64))
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @SuppressWarnings("null")
    public static void handle(ApplyDecorationStylePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }

            DecorationType type;
            try {
                type = DecorationType.valueOf(payload.decorationType());
            } catch (IllegalArgumentException ex) {
                player.sendSystemMessage(Component.literal("Decoration apply failed: invalid decoration type."));
                return;
            }

            if (DecorationStyleRegistry.getStyle(type, payload.styleId()) == null) {
                player.sendSystemMessage(Component.literal("Decoration apply failed: unknown style id " + payload.styleId()));
                return;
            }

            if (!PlayerStardewDataAPI.getData(player).isDecorationUnlocked(type, payload.styleId())) {
                player.sendSystemMessage(Component.literal("Decoration apply failed: style is locked on server."));
                return;
            }

            boolean holdingPaintbrush = player.getMainHandItem().is(ModItems.PAINTBRUSH.get()) || player.getOffhandItem().is(ModItems.PAINTBRUSH.get());
            if (!holdingPaintbrush) {
                player.sendSystemMessage(Component.literal("Decoration apply failed: hold paintbrush in main/off hand."));
                return;
            }

            if (player.distanceToSqr(payload.targetPos().getX() + 0.5, payload.targetPos().getY() + 0.5, payload.targetPos().getZ() + 0.5) > 100.0) {
                player.sendSystemMessage(Component.literal("Decoration apply failed: target is too far away."));
                return;
            }

            int changed = DecorationService.applyToConnected(player.level(), payload.targetPos(), type, payload.styleId());
            if (changed <= 0) {
                player.sendSystemMessage(Component.literal("Decoration apply failed: no decor block entities found in connected area."));
                StardewCraft.LOGGER.warn("Decoration apply produced 0 changes. Player={}, pos={}, type={}, style={}",
                    player.getGameProfile().getName(), payload.targetPos(), payload.decorationType(), payload.styleId());
            }
        });
    }
}
