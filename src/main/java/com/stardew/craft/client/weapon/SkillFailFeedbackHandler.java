package com.stardew.craft.client.weapon;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * 客户端接收技能失败反馈包后，执行声音+粒子+颤抖效果
 */
public final class SkillFailFeedbackHandler {

    private SkillFailFeedbackHandler() {}

    @SuppressWarnings("null")
    public static void onReceive(boolean mainHand) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || player.level() == null) {
            return;
        }

        // 声音
        player.playSound(SoundEvents.VILLAGER_NO, 0.7f, 0.9f);

        // 粒子
        Vec3 pos = player.position().add(0, player.getBbHeight() * 0.7, 0);
        for (int i = 0; i < 4; i++) {
            player.level().addParticle(ParticleTypes.ANGRY_VILLAGER,
                pos.x + (player.level().random.nextDouble() - 0.5) * 0.4,
                pos.y + (player.level().random.nextDouble() - 0.5) * 0.3,
                pos.z + (player.level().random.nextDouble() - 0.5) * 0.4,
                0.0, 0.02, 0.0);
        }

        // 颤抖
        InteractionHand hand = mainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        SkillFailShakeState.start(hand);
    }
}
