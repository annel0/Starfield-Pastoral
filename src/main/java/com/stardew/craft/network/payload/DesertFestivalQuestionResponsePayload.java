package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.festival.desert.DesertFestivalCookService;
import com.stardew.craft.festival.desert.DesertFestivalSpecialInteractionService;
import com.stardew.craft.festival.desert.DesertFestivalWillyFishingService;
import com.stardew.craft.festival.trout.TroutDerbyService;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@SuppressWarnings("null")
public record DesertFestivalQuestionResponsePayload(
    String context,
    int questionIndex,
    String choiceId
) implements CustomPacketPayload {

    public static final Type<DesertFestivalQuestionResponsePayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "desert_festival_question_response"));

    public static final StreamCodec<FriendlyByteBuf, DesertFestivalQuestionResponsePayload> STREAM_CODEC = StreamCodec.of(
        (buf, payload) -> {
            buf.writeUtf(payload.context(), 64);
            buf.writeVarInt(payload.questionIndex());
            buf.writeUtf(payload.choiceId(), 64);
        },
        buf -> new DesertFestivalQuestionResponsePayload(buf.readUtf(64), buf.readVarInt(), buf.readUtf(64))
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(DesertFestivalQuestionResponsePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                if ("willy_fishing".equals(payload.context())) {
                    DesertFestivalWillyFishingService.handleQuestionResponse(player, payload.context(), payload.choiceId());
                    return;
                }
                if ("trout_derby_booth".equals(payload.context())) {
                    TroutDerbyService.handleQuestionResponse(player, payload.choiceId());
                    return;
                }
                if (payload.context().startsWith("cook_")) {
                    DesertFestivalCookService.handleQuestionResponse(
                        player, payload.context(), payload.questionIndex(), payload.choiceId());
                    return;
                }
                DesertFestivalSpecialInteractionService.handleQuestionResponse(
                    player, payload.context(), payload.questionIndex(), payload.choiceId());
            }
        });
    }
}