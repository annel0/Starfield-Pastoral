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

public record WeaponSkillAnimPayload(String weaponId, String skillId, int durationTicks) implements CustomPacketPayload {
    @SuppressWarnings("null")
    public static final Type<WeaponSkillAnimPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "weapon_skill_anim")
    );

        @SuppressWarnings("null")
        public static final StreamCodec<ByteBuf, WeaponSkillAnimPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            WeaponSkillAnimPayload::weaponId,
            ByteBufCodecs.STRING_UTF8,
            WeaponSkillAnimPayload::skillId,
            ByteBufCodecs.VAR_INT,
            WeaponSkillAnimPayload::durationTicks,
            WeaponSkillAnimPayload::new
        );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(WeaponSkillAnimPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> WeaponSkillAnimationClient.start(payload.weaponId(), payload.skillId(), payload.durationTicks()));
    }
}
