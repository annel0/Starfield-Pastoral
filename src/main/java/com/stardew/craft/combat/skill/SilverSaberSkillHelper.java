package com.stardew.craft.combat.skill;

import com.stardew.craft.combat.network.SilverSaberFoldbackPayload;
import com.stardew.craft.item.weapon.WeaponSkillData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * 银军刀"银纹折返"技能的统一操作辅助类
 * 集中管理所有技能相关的操作，避免代码重复和不一致
 */
public final class SilverSaberSkillHelper {

    public static final int SKILL_ANIM_TICKS = 8;

    private SilverSaberSkillHelper() {}

    /**
     * 创建技能上下文并设置待处理
     */
    public static void setupSkillContext(Player player, WeaponSkillData skill, long nowTick) {
        SkillContext context = SkillContext.builder()
            .skillId(skill.getId())
            .tier(SkillContext.SkillTier.MINOR)
            .damageMultiplier(skill.getDamagePercent() / 100.0f)
            .build();
        WeaponSkillContextStore.setPending(player, context, nowTick + 5);
    }

    /**
     * 攻击目标（带技能上下文）
     */
    @SuppressWarnings("null")
    public static void attackWithSkillContext(Player player, LivingEntity target, WeaponSkillData skill, long nowTick) {
        setupSkillContext(player, skill, nowTick);
        player.attack(target);
    }

    /**
     * 进入折返状态
     */
    public static void enterFoldbackState(Player player, long nowTick, String weaponId, Vec3 origin) {
        SilverSaberFoldbackState.start(player, nowTick, SilverSaberFoldbackState.DEFAULT_DURATION_TICKS, weaponId, origin);
        if (player instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer, 
                new SilverSaberFoldbackPayload(true, SilverSaberFoldbackState.DEFAULT_DURATION_TICKS));
        }
    }

    /**
     * 退出折返状态（不返回原点）
     */
    public static void exitFoldbackState(Player player) {
        SilverSaberFoldbackState.clear(player);
        if (player instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer, new SilverSaberFoldbackPayload(false, 0));
        }
    }

    /**
     * 进入冷却并发送动画
     */
    public static void enterCooldownWithAnim(Player player, String weaponId, WeaponSkillData skill, long nowTick) {
        int cooldownTicks = skill.getCooldown() * 20;
        WeaponSkillCooldowns.setCooldown(player, weaponId, skill.getId(), nowTick, cooldownTicks);
        WeaponSkillAnimationLock.setLock(player, nowTick, SKILL_ANIM_TICKS);
        if (player instanceof ServerPlayer serverPlayer) {
            WeaponSkillAnimationDispatcher.sendSkillAnim(serverPlayer, weaponId, skill.getId(), SKILL_ANIM_TICKS);
        }
    }

    /**
     * 完整的"折返中右键"操作：攻击 + 返回原点 + 进入冷却
     */
    public static void executeReturnStrike(Player player, LivingEntity target, Vec3 origin, 
                                           String weaponId, WeaponSkillData skill, long nowTick,
                                           TeleportFunction teleportFunc) {
        exitFoldbackState(player);
        
        if (target != null) {
            attackWithSkillContext(player, target, skill, nowTick);
        }
        
        teleportFunc.teleport(player, origin);
        enterCooldownWithAnim(player, weaponId, skill, nowTick);
    }

    /**
     * 完整的"折返中左键"操作：攻击 + 不返回 + 进入冷却
     */
    public static void executeStayStrike(Player player, LivingEntity target, 
                                         String weaponId, WeaponSkillData skill, long nowTick) {
        exitFoldbackState(player);
        
        if (target != null) {
            attackWithSkillContext(player, target, skill, nowTick);
        }
        
        enterCooldownWithAnim(player, weaponId, skill, nowTick);
    }

    /**
     * 完整的"首次右键有目标"操作（传送完成后调用）：攻击 + 进入折返状态
     * 顺序：攻击 → 进入折返状态
     */
    @SuppressWarnings("null")
    public static void executeInitialDashAfterTeleport(Player player, LivingEntity target, Vec3 origin,
                                                       String weaponId, WeaponSkillData skill, long nowTick) {
        // 1. 传送完成后，再攻击目标
        setupSkillContext(player, skill, nowTick);
        player.attack(target);
        
        // 2. 进入折返状态（记录的是传送前的原点）
        enterFoldbackState(player, nowTick, weaponId, origin);
        
        // 3. 设置动画锁和发送动画包
        WeaponSkillAnimationLock.setLock(player, nowTick, SKILL_ANIM_TICKS);
        if (player instanceof ServerPlayer serverPlayer) {
            WeaponSkillAnimationDispatcher.sendSkillAnim(serverPlayer, weaponId, skill.getId(), SKILL_ANIM_TICKS);
        }
    }

    /**
     * 完整的"首次右键无目标"操作：突进 + 直接冷却
     */
    public static void executeEmptyDash(Player player, String weaponId, WeaponSkillData skill, 
                                        long nowTick, DashFunction dashFunc) {
        dashFunc.dash(player, 5.0);
        enterCooldownWithAnim(player, weaponId, skill, nowTick);
    }

    /**
     * 折返状态超时处理
     */
    public static void handleTimeout(Player player, String weaponId, WeaponSkillData skill, long nowTick) {
        exitFoldbackState(player);
        int cooldownTicks = skill.getCooldown() * 20;
        WeaponSkillCooldowns.setCooldown(player, weaponId, skill.getId(), nowTick, cooldownTicks);
    }

    @FunctionalInterface
    public interface TeleportFunction {
        void teleport(Player player, Vec3 pos);
    }

    @FunctionalInterface
    public interface DashFunction {
        void dash(Player player, double distance);
    }
}
