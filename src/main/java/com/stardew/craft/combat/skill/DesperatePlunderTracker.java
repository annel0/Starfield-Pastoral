package com.stardew.craft.combat.skill;

import com.stardew.craft.effect.ModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * 追踪「亡命掠夺」技能的击杀判定
 * 用于在攻击后检查目标是否被击杀，以决定是恢复生命还是给予buff
 */
public final class DesperatePlunderTracker {

    private DesperatePlunderTracker() {}

    private static final Map<UUID, PendingPlunder> PENDING = new WeakHashMap<>();

    private static class PendingPlunder {
        final LivingEntity target;
        final long timestamp;

        PendingPlunder(LivingEntity target, float healthBefore, long timestamp) {
            this.target = target;
            this.timestamp = timestamp;
        }
    }

    /**
     * 标记一次待检查的掠夺攻击
     */
    public static void setPending(Player player, LivingEntity target, float healthBefore) {
        PENDING.put(player.getUUID(), new PendingPlunder(target, healthBefore, System.currentTimeMillis()));
    }

    /**
     * 在攻击完成后检查结果（应在 attack 之后调用）
     * @return true 如果处理了掠夺效果
     */
    @SuppressWarnings("null")
    public static boolean checkAndResolve(Player player) {
        PendingPlunder pending = PENDING.remove(player.getUUID());
        if (pending == null) {
            return false;
        }

        // 超时检查（500ms内有效）
        if (System.currentTimeMillis() - pending.timestamp > 500) {
            return false;
        }

        LivingEntity target = pending.target;
        
        // 检查目标是否死亡或血量降低到0
        boolean killed = target.isDeadOrDying() || target.getHealth() <= 0;

        if (killed) {
            // 击杀成功：恢复4点生命值
            float maxHealth = player.getMaxHealth();
            float newHealth = Math.min(player.getHealth() + 4.0f, maxHealth);
            player.setHealth(newHealth);
            
            // 播放治疗音效（在服务端广播）
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    net.minecraft.sounds.SoundEvents.PLAYER_LEVELUP, 
                    net.minecraft.sounds.SoundSource.PLAYERS, 0.5f, 1.2f);
        } else {
            // 未击杀：给予「愤怒」buff（3秒，攻击+10%）
            player.addEffect(new MobEffectInstance(ModMobEffects.FURY, 60, 0, false, true, true));
        }

        return true;
    }

    /**
     * 清理过期的待处理记录
     */
    public static void cleanup() {
        long now = System.currentTimeMillis();
        PENDING.entrySet().removeIf(entry -> now - entry.getValue().timestamp > 1000);
    }

    /** Clean up state when a player logs out to prevent memory leaks. */
    public static void removePlayer(UUID playerId) {
        PENDING.remove(playerId);
    }
}
