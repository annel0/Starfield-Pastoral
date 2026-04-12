package com.stardew.craft.quest.network;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.quest.StardewQuest;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * S→C: 同步每日公告栏任务（打开公告栏时发送）
 */
@SuppressWarnings("null")
public record DailyQuestSyncPayload(CompoundTag data) implements CustomPacketPayload {

    public static final Type<DailyQuestSyncPayload> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "daily_quest_sync")
    );

    public static final StreamCodec<ByteBuf, DailyQuestSyncPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.COMPOUND_TAG,
        DailyQuestSyncPayload::data,
        DailyQuestSyncPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    /** 服务端构建 */
    public static DailyQuestSyncPayload fromQuest(@Nullable StardewQuest quest) {
        CompoundTag tag = new CompoundTag();
        if (quest != null) {
            tag.put("Quest", quest.save());
            tag.putBoolean("HasQuest", true);
        } else {
            tag.putBoolean("HasQuest", false);
        }
        return new DailyQuestSyncPayload(tag);
    }

    public static void handle(DailyQuestSyncPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> handleClient(payload));
    }

    @net.neoforged.api.distmarker.OnlyIn(net.neoforged.api.distmarker.Dist.CLIENT)
    private static void handleClient(DailyQuestSyncPayload payload) {
        CompoundTag tag = payload.data();
        if (tag.getBoolean("HasQuest") && tag.contains("Quest", 10)) {
            ClientQuestData.setDailyQuest(StardewQuest.load(tag.getCompound("Quest")));
        } else {
            ClientQuestData.setDailyQuest(null);
        }
    }
}
