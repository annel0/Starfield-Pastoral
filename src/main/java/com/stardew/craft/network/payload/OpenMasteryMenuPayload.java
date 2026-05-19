package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * S → C：打开 MasteryTrackerMenu。
 * whichSkill = -1 总览页；0..4 = SkillType.getId()。
 */
public record OpenMasteryMenuPayload(int whichSkill) implements CustomPacketPayload {

    @SuppressWarnings("null")
    public static final Type<OpenMasteryMenuPayload> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "open_mastery_menu"));

    public static final StreamCodec<ByteBuf, OpenMasteryMenuPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, OpenMasteryMenuPayload::whichSkill,
        OpenMasteryMenuPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(OpenMasteryMenuPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> com.stardew.craft.client.gui.mastery.MasteryTrackerMenuScreen.open(payload.whichSkill()));
    }
}
