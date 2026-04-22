package com.stardew.craft.network;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.fishing.data.FishingDataManager;
import com.stardew.craft.item.artisan.ArtisanRecipeDataManager;
import com.stardew.craft.item.artisan.PreservesIngredientDataManager;
import com.stardew.craft.npc.data.NpcDataRegistry;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.nio.charset.StandardCharsets;

/**
 * S→C: 同步所有 DataManager 的原始 JSON 到客户端。
 * <p>
 * 专用服务器场景下，客户端没有 datapack ReloadListener，
 * 导致 ArtisanRecipe/PreservesIngredient/Fishing/NpcEvents 数据为空。
 * 此 payload 在玩家登录时发送，客户端收到后重放 JSON 解析逻辑。
 */
@SuppressWarnings("null")
public record DataRegistrySyncPayload(
        String artisanJson,
        String preservesJson,
        String fishingJson,
        String npcEventsJson
) implements CustomPacketPayload {

    public static final Type<DataRegistrySyncPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "data_registry_sync")
    );

    public static final StreamCodec<ByteBuf, DataRegistrySyncPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public DataRegistrySyncPayload decode(ByteBuf buf) {
            String artisan = readLargeString(buf);
            String preserves = readLargeString(buf);
            String fishing = readLargeString(buf);
            String npcEvents = readLargeString(buf);
            return new DataRegistrySyncPayload(artisan, preserves, fishing, npcEvents);
        }

        @Override
        public void encode(ByteBuf buf, DataRegistrySyncPayload payload) {
            writeLargeString(buf, payload.artisanJson);
            writeLargeString(buf, payload.preservesJson);
            writeLargeString(buf, payload.fishingJson);
            writeLargeString(buf, payload.npcEventsJson);
        }
    };

    /** 写入不受 32767 字符限制的 UTF-8 字符串 */
    private static void writeLargeString(ByteBuf buf, String s) {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        VarInt.write(buf, bytes.length);
        buf.writeBytes(bytes);
    }

    /** 读取不受 32767 字符限制的 UTF-8 字符串 */
    private static String readLargeString(ByteBuf buf) {
        int len = VarInt.read(buf);
        byte[] bytes = new byte[len];
        buf.readBytes(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    /**
     * Client-side handler: replay JSON parsing for each DataManager.
     */
    public static void handle(DataRegistrySyncPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!payload.artisanJson.isEmpty()) {
                ArtisanRecipeDataManager.applyFromJson(payload.artisanJson);
            }
            if (!payload.preservesJson.isEmpty()) {
                PreservesIngredientDataManager.applyFromJson(payload.preservesJson);
            }
            if (!payload.fishingJson.isEmpty()) {
                FishingDataManager.applyFromJson(payload.fishingJson);
            }
            if (!payload.npcEventsJson.isEmpty()) {
                NpcDataRegistry.applyEventsFromJson(payload.npcEventsJson);
            }
            StardewCraft.LOGGER.info("[DATA-SYNC] Received data registry sync from server");
        });
    }

    /**
     * 从服务端发送给指定玩家。
     */
    public static void sendFullSync(ServerPlayer player) {
        String artisan = ArtisanRecipeDataManager.getCachedJson();
        String preserves = PreservesIngredientDataManager.getCachedJson();
        String fishing = FishingDataManager.getCachedJson();
        String npcEvents = NpcDataRegistry.getCachedEventsJson();
        PacketDistributor.sendToPlayer(player, new DataRegistrySyncPayload(artisan, preserves, fishing, npcEvents));
    }
}
