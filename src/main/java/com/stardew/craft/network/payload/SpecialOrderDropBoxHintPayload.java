package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import io.netty.buffer.ByteBuf;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public record SpecialOrderDropBoxHintPayload(List<String> dropBoxIds) implements CustomPacketPayload {
    public static final Type<SpecialOrderDropBoxHintPayload> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "special_order_dropbox_hints"));

    public static final net.minecraft.network.codec.StreamCodec<ByteBuf, SpecialOrderDropBoxHintPayload> STREAM_CODEC =
        new net.minecraft.network.codec.StreamCodec<>() {
            @Override
            public SpecialOrderDropBoxHintPayload decode(ByteBuf buf) {
                List<String> ids = ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()).decode(buf);
                return new SpecialOrderDropBoxHintPayload(ids);
            }

            @Override
            public void encode(ByteBuf buf, SpecialOrderDropBoxHintPayload payload) {
                ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()).encode(buf, payload.dropBoxIds);
            }
        };

    public SpecialOrderDropBoxHintPayload(Collection<String> dropBoxIds) {
        this(new ArrayList<>(dropBoxIds));
    }

    public static void handle(SpecialOrderDropBoxHintPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> handleClient(payload));
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleClient(SpecialOrderDropBoxHintPayload payload) {
        com.stardew.craft.client.render.ClientSpecialOrderDropBoxHints.replace(payload.dropBoxIds);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
