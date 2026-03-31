package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.player.PlayerDataEventHandler;
import com.stardew.craft.player.PlayerStardewData;
import com.stardew.craft.player.PlayerStardewDataAPI;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Client → Server: unlocks a cooking recipe learned from the TV cooking channel.
 */
@SuppressWarnings("null")
public record TVRecipeUnlockPayload(String recipeId) implements CustomPacketPayload {

    public static final Type<TVRecipeUnlockPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "tv_recipe_unlock"));

    public static final StreamCodec<FriendlyByteBuf, TVRecipeUnlockPayload> STREAM_CODEC = StreamCodec.of(
            (buf, p) -> buf.writeUtf(p.recipeId, 128),
            buf -> new TVRecipeUnlockPayload(buf.readUtf(128))
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(TVRecipeUnlockPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                PlayerStardewData data = PlayerStardewDataAPI.getData(serverPlayer);
                if (!data.isRecipeUnlocked(payload.recipeId)) {
                    data.unlockRecipe(payload.recipeId);
                }
                PlayerDataEventHandler.syncPlayerData(serverPlayer, data);
            }
        });
    }
}
