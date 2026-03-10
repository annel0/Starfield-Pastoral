package com.stardew.craft.combat.skill;

import com.stardew.craft.combat.network.WeaponSkillAnimPayload;
import com.stardew.craft.combat.network.WeaponSkillCounterAnimPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

public final class WeaponSkillAnimationDispatcher {

    private WeaponSkillAnimationDispatcher() {}

    @SuppressWarnings("null")
    public static void sendSkillAnim(ServerPlayer player, String weaponId, String skillId, int durationTicks) {
        PacketDistributor.sendToPlayer(
                player,
                new WeaponSkillAnimPayload(weaponId, skillId, durationTicks)
        );
    }

    @SuppressWarnings("null")
    public static void sendCounterAnim(ServerPlayer player, String weaponId, String skillId, int durationTicks) {
        PacketDistributor.sendToPlayer(
                player,
                new WeaponSkillCounterAnimPayload(weaponId, skillId, durationTicks)
        );
    }
}
