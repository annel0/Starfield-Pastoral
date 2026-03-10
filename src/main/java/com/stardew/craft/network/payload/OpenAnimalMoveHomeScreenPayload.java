package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record OpenAnimalMoveHomeScreenPayload(
    long animalId,
    String animalName,
    String currentBuildingId,
    List<BuildingOption> options
) implements CustomPacketPayload {

    public record BuildingOption(
        String buildingId,
        String displayName,
        int animalCount,
        int capacity,
        boolean selectable
    ) {
        @SuppressWarnings("null")
        public static final StreamCodec<RegistryFriendlyByteBuf, BuildingOption> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            BuildingOption::buildingId,
            ByteBufCodecs.STRING_UTF8,
            BuildingOption::displayName,
            ByteBufCodecs.INT,
            BuildingOption::animalCount,
            ByteBufCodecs.INT,
            BuildingOption::capacity,
            ByteBufCodecs.BOOL,
            BuildingOption::selectable,
            BuildingOption::new
        );
    }

    @SuppressWarnings("null")
    public static final Type<OpenAnimalMoveHomeScreenPayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "open_animal_move_home_screen"));

    @SuppressWarnings("null")
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenAnimalMoveHomeScreenPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_LONG,
        OpenAnimalMoveHomeScreenPayload::animalId,
        ByteBufCodecs.STRING_UTF8,
        OpenAnimalMoveHomeScreenPayload::animalName,
        ByteBufCodecs.STRING_UTF8,
        OpenAnimalMoveHomeScreenPayload::currentBuildingId,
        BuildingOption.STREAM_CODEC.apply(ByteBufCodecs.list()),
        OpenAnimalMoveHomeScreenPayload::options,
        OpenAnimalMoveHomeScreenPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @SuppressWarnings("null")
    public static void handle(OpenAnimalMoveHomeScreenPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player == null) {
                return;
            }
            minecraft.setScreen(new com.stardew.craft.client.gui.AnimalMoveHomeSelectScreen(payload));
        });
    }
}
