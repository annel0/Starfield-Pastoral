package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.client.gui.DecorationSelectionScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record OpenDecorationScreenPayload(
    String decorationType,
    BlockPos targetPos,
    String currentStyleId,
    List<DecorationOption> options
) implements CustomPacketPayload {

    public record DecorationOption(
        String styleId,
        String texture,
        int texWidth,
        int texHeight,
        int sourceX,
        int sourceY,
        int sourceWidth,
        int sourceHeight,
        boolean unlocked,
        String unlockHintKey,
        int sortOrder
    ) {
        @SuppressWarnings("null")
        public static final StreamCodec<RegistryFriendlyByteBuf, DecorationOption> STREAM_CODEC = StreamCodec.of(
            (buf, option) -> {
                buf.writeUtf(option.styleId());
                buf.writeUtf(option.texture());
                buf.writeInt(option.texWidth());
                buf.writeInt(option.texHeight());
                buf.writeInt(option.sourceX());
                buf.writeInt(option.sourceY());
                buf.writeInt(option.sourceWidth());
                buf.writeInt(option.sourceHeight());
                buf.writeBoolean(option.unlocked());
                buf.writeUtf(option.unlockHintKey());
                buf.writeInt(option.sortOrder());
            },
            buf -> new DecorationOption(
                buf.readUtf(),
                buf.readUtf(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readBoolean(),
                buf.readUtf(),
                buf.readInt()
            )
        );
    }

    @SuppressWarnings("null")
    public static final Type<OpenDecorationScreenPayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "open_decoration_screen"));

    @SuppressWarnings("null")
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenDecorationScreenPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8,
        OpenDecorationScreenPayload::decorationType,
        BlockPos.STREAM_CODEC,
        OpenDecorationScreenPayload::targetPos,
        ByteBufCodecs.STRING_UTF8,
        OpenDecorationScreenPayload::currentStyleId,
        DecorationOption.STREAM_CODEC.apply(ByteBufCodecs.list()),
        OpenDecorationScreenPayload::options,
        OpenDecorationScreenPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OpenDecorationScreenPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> handleClient(payload));
    }

    @net.neoforged.api.distmarker.OnlyIn(net.neoforged.api.distmarker.Dist.CLIENT)
    private static void handleClient(OpenDecorationScreenPayload payload) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }
        minecraft.setScreen(new DecorationSelectionScreen(payload));
    }
}
