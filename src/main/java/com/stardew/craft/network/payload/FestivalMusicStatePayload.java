package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.sound.ModSounds;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@SuppressWarnings("null")
public record FestivalMusicStatePayload(String track) implements CustomPacketPayload {
    public static final String RELEASE = "release";
    public static final String NONE = "none";
    public static final String FALL_FEST = "fall_fest";
    public static final String EVENT1 = "event1";
    public static final String TICK_TOCK = "tick_tock";
    public static final String FLOWER_DANCE = "flower_dance";

    public static final Type<FestivalMusicStatePayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "festival_music_state"));

    public static final StreamCodec<FriendlyByteBuf, FestivalMusicStatePayload> STREAM_CODEC = StreamCodec.of(
        (buf, payload) -> buf.writeUtf(payload.track() == null ? RELEASE : payload.track()),
        buf -> new FestivalMusicStatePayload(buf.readUtf(32))
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(FestivalMusicStatePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> handleClient(payload));
    }

    @net.neoforged.api.distmarker.OnlyIn(net.neoforged.api.distmarker.Dist.CLIENT)
    private static void handleClient(FestivalMusicStatePayload payload) {
        switch (payload.track()) {
            case FALL_FEST -> com.stardew.craft.client.sound.StardewMusicManager.playForCutscene(ModSounds.MUSIC_FALL_FEST.get());
            case EVENT1 -> com.stardew.craft.client.sound.StardewMusicManager.playForCutscene(ModSounds.MUSIC_EVENT1.get());
            case TICK_TOCK -> com.stardew.craft.client.sound.StardewMusicManager.playForCutscene(ModSounds.MUSIC_TICK_TOCK.get());
            case FLOWER_DANCE -> com.stardew.craft.client.sound.StardewMusicManager.playForCutscene(ModSounds.MUSIC_FLOWER_DANCE.get());
            case NONE -> com.stardew.craft.client.sound.StardewMusicManager.stopForCutsceneSilence();
            default -> com.stardew.craft.client.sound.StardewMusicManager.releaseCutsceneOverride();
        }
    }
}