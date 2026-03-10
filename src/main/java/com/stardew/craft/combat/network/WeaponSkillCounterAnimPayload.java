package com.stardew.craft.combat.network;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.client.weapon.WeaponSkillAnimationClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record WeaponSkillCounterAnimPayload(String weaponId, String skillId, int durationTicks) implements CustomPacketPayload {
    @SuppressWarnings("null")
    public static final Type<WeaponSkillCounterAnimPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "weapon_skill_counter_anim")
    );

    @SuppressWarnings("null")
    public static final StreamCodec<ByteBuf, WeaponSkillCounterAnimPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            WeaponSkillCounterAnimPayload::weaponId,
            ByteBufCodecs.STRING_UTF8,
            WeaponSkillCounterAnimPayload::skillId,
            ByteBufCodecs.VAR_INT,
            WeaponSkillCounterAnimPayload::durationTicks,
            WeaponSkillCounterAnimPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(WeaponSkillCounterAnimPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> WeaponSkillAnimationClient.start(payload.weaponId(), payload.skillId(), payload.durationTicks()));
    }
}
