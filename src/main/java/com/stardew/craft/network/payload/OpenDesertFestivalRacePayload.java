package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@SuppressWarnings("null")
public record OpenDesertFestivalRacePayload(String screen, DesertFestivalRaceSnapshot snapshot) implements CustomPacketPayload {
    public static final Type<OpenDesertFestivalRacePayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "open_desert_festival_race"));

    public static final StreamCodec<FriendlyByteBuf, OpenDesertFestivalRacePayload> STREAM_CODEC = StreamCodec.of(
        (buf, payload) -> {
            buf.writeUtf(payload.screen());
            DesertFestivalRaceSnapshot.write(buf, payload.snapshot());
        },
        buf -> new OpenDesertFestivalRacePayload(buf.readUtf(), DesertFestivalRaceSnapshot.read(buf))
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OpenDesertFestivalRacePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> handleClient(payload));
    }

    @net.neoforged.api.distmarker.OnlyIn(net.neoforged.api.distmarker.Dist.CLIENT)
    private static void handleClient(OpenDesertFestivalRacePayload payload) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.player == null) return;
        mc.setScreen(switch (payload.screen()) {
            case "shady" -> new com.stardew.craft.client.gui.DesertFestivalShadyGuyScreen(payload.snapshot());
            case "watch" -> new com.stardew.craft.client.gui.DesertFestivalRaceWatchScreen(payload.snapshot());
            case "single" -> new com.stardew.craft.client.gui.DesertFestivalRaceSingleBetScreen(payload.snapshot());
            case "rooms" -> new com.stardew.craft.client.gui.DesertFestivalRaceRoomListScreen(payload.snapshot());
            default -> new com.stardew.craft.client.gui.DesertFestivalRaceHubScreen(payload.snapshot());
        });
    }
}