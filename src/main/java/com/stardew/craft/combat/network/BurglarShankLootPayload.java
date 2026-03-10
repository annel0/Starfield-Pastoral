package com.stardew.craft.combat.network;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.client.hud.StardewTimeHud;
import com.stardew.craft.client.weapon.SkillEffectsClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record BurglarShankLootPayload() implements CustomPacketPayload {

    @SuppressWarnings("null")
    public static final Type<BurglarShankLootPayload> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "burglar_shank_loot")
    );

    public static final StreamCodec<ByteBuf, BurglarShankLootPayload> STREAM_CODEC = StreamCodec.unit(new BurglarShankLootPayload());

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(BurglarShankLootPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                SkillEffectsClient.playBurglarShankLoot(mc.player);
                StardewTimeHud.triggerMoneyShake();
            }
        });
    }
}
