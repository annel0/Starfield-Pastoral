# -*- coding: utf-8 -*-
import io

packet_path = 'src/main/java/com/stardew/craft/network/MiningLadderHighlightPacket.java'
cache_path = 'src/main/java/com/stardew/craft/client/hud/MiningLadderHighlightCache.java'

packet_code = '''package com.stardew.craft.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import com.stardew.craft.StardewCraft;

public record MiningLadderHighlightPacket(BlockPos pos) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MiningLadderHighlightPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "mining_ladder_highlight"));

    public static final StreamCodec<FriendlyByteBuf, MiningLadderHighlightPacket> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC, MiningLadderHighlightPacket::pos,
        MiningLadderHighlightPacket::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}'''

cache_code = '''package com.stardew.craft.client.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import com.stardew.craft.network.MiningLadderHighlightPacket;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class MiningLadderHighlightCache {
    private static BlockPos ladderPos = null;
    private static long highlightEndTime = 0;
    
    // 我们保留梯子高光 60 秒或者玩家走太远
    public static void handle(MiningLadderHighlightPacket payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ladderPos = payload.pos();
            highlightEndTime = System.currentTimeMillis() + 60000;
        });
    }

    public static BlockPos getActiveHighlight() {
        if (ladderPos != null && System.currentTimeMillis() < highlightEndTime) {
            return ladderPos;
        }
        return null;
    }
    
    public static void clearHighlight() {
        ladderPos = null;
    }
}'''

with io.open(packet_path, 'w', encoding='utf-8') as f: f.write(packet_code)
with io.open(cache_path, 'w', encoding='utf-8') as f: f.write(cache_code)

print('Packet and Cache written')
