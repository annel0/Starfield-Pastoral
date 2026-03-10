package com.stardew.craft.combat;

import com.stardew.craft.combat.buff.CombatBuffManager;
import com.stardew.craft.effect.ModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import com.stardew.craft.combat.equipment.EquipmentStats;
import com.stardew.craft.combat.skill.DragontoothShivBreathTracker;
import com.stardew.craft.combat.skill.SkillContext;
import com.stardew.craft.combat.skill.SingularityTracker;
import com.stardew.craft.combat.skill.StartrailTracker;
import com.stardew.craft.player.PlayerStardewData;
import com.stardew.craft.player.PlayerDataManager;
import com.stardew.craft.player.ProfessionType;
import com.stardew.craft.item.weapon.IStardewWeapon;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Random;

/**
 * 星露谷物语战斗系�?- 核心伤害计算�?
 * 
 * 伤害计算流程（乘区设计）:
 * 
 * 最终伤�?= 基础伤害 × 暴击乘区 × 职业乘区 × 技能乘�?× Buff乘区 × 随机波动 - 防御减免
 * 
 * 乘区说明:
 * 1. 基础伤害�? 武器基础伤害 (min ~ max 随机)
 * 2. 暴击乘区: 暴击�?× 暴击倍率 (默认3.0x, 可被装备/职业修改)
 * 3. 职业乘区: Fighter +10%, Brute +15% (叠加计算)
 * 4. 技能乘�? 武器小技�?大技能的伤害倍率
 * 5. Buff乘区: 攻击力buff的加�?
 * 6. 随机波动: ±12.5% 随机
 * 7. 防御减免: 最后减去目标防御�?(非乘区，是减�?
 */
public class DamageCalculator {
    
    private static final Random random = new Random();
    
    // 随机波动范围
    private static final float DAMAGE_VARIANCE_MIN = 0.875f;  // -12.5%
    private static final float DAMAGE_VARIANCE_MAX = 1.125f;  // +12.5%
    
    // 默认暴击倍率
    public static final float DEFAULT_CRIT_MULTIPLIER = 3.0f;
    
    // 默认暴击�?
    public static final float DEFAULT_CRIT_CHANCE = 0.02f;  // 2%
    
    // 防御衰减阈�?(当防御�?>= 伤害�?0%时触发衰�?
    private static final float DEFENSE_DECAY_THRESHOLD = 0.5f;
    private static final float DEFENSE_DECAY_MAX = 0.2f;  // 最多衰�?0%
    
    /**
     * 计算玩家对目标造成的伤�?
     * 
     * @param attacker 攻击者（玩家�?
     * @param target 目标实体
     * @param weapon 使用的武�?
     * @param skillContext 技能上下文（可为null表示普通攻击）
     * @param buffManager Buff管理器（可为null�?
     * @param equipmentStats 装备属性（可为null�?
     */
    @SuppressWarnings("null")
    public static DamageResult calculatePlayerDamage(
            Player attacker,
            LivingEntity target,
            ItemStack weapon,
            SkillContext skillContext,
            CombatBuffManager buffManager,
            EquipmentStats equipmentStats
    ) {
        DamageResult.Builder result = DamageResult.builder();
        
        // 获取武器属�?
        WeaponStats weaponStats = WeaponStats.fromItemStack(weapon);
        
        // 获取玩家星露谷数据（如果是服务端玩家�?
        PlayerStardewData playerData = null;
        if (attacker instanceof ServerPlayer serverPlayer) {
            playerData = PlayerDataManager.getPlayerData(serverPlayer);
        }
        
        // 获取怪物属�?
        MonsterStats targetStats = MonsterStats.fromEntity(target);
        
        // 检测维度，确定伤害模式
        boolean inStardewDimension = DimensionDamageMapper.isInStardewDimension(target);
        result.inStardewDimension(inStardewDimension);
        
        // ==================== �?�? 基础伤害�?====================
        float baseDamage = calculateBaseDamage(weaponStats);

        if (attacker instanceof ServerPlayer serverPlayer && weapon.getItem() instanceof IStardewWeapon IStardewWeapon) {
            String weaponId = IStardewWeapon.getWeaponId();
            if ("galaxy_sword".equals(weaponId)) {
                int stacks = StartrailTracker.getStacks(serverPlayer);
                baseDamage += stacks * 2.0f;
            } else if ("infinity_blade".equals(weaponId)) {
                int stacks = SingularityTracker.getStacks(serverPlayer);
                baseDamage += stacks * 2.0f;
            }
        }
        
        // 添加装备攻击加成
        if (equipmentStats != null) {
            baseDamage += equipmentStats.getAttack();
        }
        
        // 添加buff攻击加成
        if (buffManager != null) {
            baseDamage += buffManager.getAttackBonus();
        }
        
        result.baseDamage(baseDamage);
        
        float totalDamage = baseDamage;
        
        boolean forceCrit = skillContext != null && skillContext.isGuaranteedCrit();
        float skillCritBonus = skillContext != null ? skillContext.getCritChanceBonus() : 0.0f;
        if (attacker instanceof ServerPlayer serverPlayer && weapon.getItem() instanceof IStardewWeapon IStardewWeapon) {
            if ("infinity_blade".equals(IStardewWeapon.getWeaponId()) && SingularityTracker.isEvolved(serverPlayer)) {
                skillCritBonus += 0.20f;
            }
        }
        
        long nowTick = attacker.level() != null ? attacker.level().getGameTime() : 0L;
        boolean dragontoothBreathActive = attacker instanceof ServerPlayer serverPlayer
            && weapon.getItem() instanceof IStardewWeapon IStardewWeapon
            && "dragontooth_shiv".equals(IStardewWeapon.getWeaponId())
            && DragontoothShivBreathTracker.isActive(serverPlayer, nowTick);

        CritResult critResult = calculateCrit(weaponStats, attacker, target, playerData, buffManager, equipmentStats, forceCrit, skillCritBonus);
        float critMultiplier = critResult.multiplier();
        if (critResult.isCrit() && dragontoothBreathActive) {
            critMultiplier = DEFAULT_CRIT_MULTIPLIER;
        }
        result.isCrit(critResult.isCrit());
        result.critMultiplier(critMultiplier);
        
        if (critResult.isCrit()) {
            totalDamage *= critMultiplier;
        }
        
        // ==================== �?�? 职业乘区 ====================
        float professionMultiplier = calculateProfessionMultiplier(playerData);
        result.professionMultiplier(professionMultiplier);
        totalDamage *= professionMultiplier;
        
        // ==================== �?�? 技能乘�?====================
        float skillMultiplier = 1.0f;
        if (skillContext != null) {
            skillMultiplier = skillContext.getDamageMultiplier();
            result.skillMultiplier(skillMultiplier);
            result.skillId(skillContext.getSkillId());
        }
        totalDamage *= skillMultiplier;
        
        // ==================== �?�? Buff乘区 ====================
        float buffMultiplier = calculateBuffMultiplier(attacker, playerData, buffManager);
        result.buffMultiplier(buffMultiplier);
        totalDamage *= buffMultiplier;

        // ==================== 目标易伤乘区 ====================
        MobEffectInstance vulnerable = target.getEffect(ModMobEffects.VULNERABLE);
        if (vulnerable != null) {
            int amp = vulnerable.getAmplifier();
            float bonus = 0.10f * (amp + 1);
            totalDamage *= (1.0f + bonus);
        }
        
        // ==================== �?�? 随机波动 ====================
        float variance = randomRange(DAMAGE_VARIANCE_MIN, DAMAGE_VARIANCE_MAX);
        result.damageVariance(variance);
        totalDamage *= variance;
        
        // ==================== �?�? 防御减免 ====================
        boolean ignoreDefense = skillContext != null && skillContext.isIgnoreDefense();
        float defenseReduction = 0;
        if (!ignoreDefense) {
            defenseReduction = calculateDefenseReduction(totalDamage, targetStats);
        }
        result.defenseReduction(defenseReduction);
        totalDamage -= defenseReduction;
        
        // 最小伤害保证为1
        totalDamage = Math.max(1.0f, totalDamage);
        
        // ==================== �?�? 维度映射 ====================
        if (!inStardewDimension) {
            totalDamage = DimensionDamageMapper.mapDamage(totalDamage, false);
        }
        
        result.finalDamage(totalDamage);
        
        // 检测是否被闪避
        boolean dodged = checkDodge(targetStats, weaponStats, equipmentStats);
        result.dodged(dodged);
        
        if (dodged) {
            result.finalDamage(0);
        }
        
        return result.build();
    }
    
    /**
     * 计算玩家对目标造成的伤害（简化版本，不传buff和装备）
     */
    public static DamageResult calculatePlayerDamage(
            Player attacker,
            LivingEntity target,
            ItemStack weapon,
            SkillContext skillContext
    ) {
        return calculatePlayerDamage(attacker, target, weapon, skillContext, null, null);
    }
    
    /**
     * 计算怪物对玩家造成的伤�?
     * 
     * @param attacker 攻击者（怪物�?
     * @param target 目标（玩家）
     * @param monsterStats 怪物属�?
     * @param buffManager 玩家Buff管理器（可为null�?
     * @param equipmentStats 玩家装备属性（可为null�?
     */
    public static DamageResult calculateMonsterDamage(
            LivingEntity attacker,
            Player target,
            MonsterStats monsterStats,
            CombatBuffManager buffManager,
            EquipmentStats equipmentStats
    ) {
        DamageResult.Builder result = DamageResult.builder();
        
        // 获取玩家防御数据
        PlayerStardewData playerData = null;
        if (target instanceof ServerPlayer serverPlayer) {
            playerData = PlayerDataManager.getPlayerData(serverPlayer);
        }
        
        // 检测维�?
        boolean inStardewDimension = DimensionDamageMapper.isInStardewDimension(target);
        result.inStardewDimension(inStardewDimension);
        
        // 怪物基础伤害
        float baseDamage = monsterStats.getDamage();
        result.baseDamage(baseDamage);
        
        float totalDamage = baseDamage;
        
        // 随机波动 ±12.5%
        float variance = randomRange(DAMAGE_VARIANCE_MIN, DAMAGE_VARIANCE_MAX);
        result.damageVariance(variance);
        totalDamage *= variance;
        
        // 玩家防御减免
        float defense = calculatePlayerDefense(playerData, buffManager, equipmentStats);
        float defenseReduction = calculatePlayerDefenseReduction(totalDamage, defense);
        result.defenseReduction(defenseReduction);
        totalDamage -= defenseReduction;
        
        // 最小伤害保证为1
        totalDamage = Math.max(1.0f, totalDamage);
        
        // 维度映射
        if (!inStardewDimension) {
            totalDamage = DimensionDamageMapper.mapDamage(totalDamage, false);
        }
        
        result.finalDamage(totalDamage);
        
        return result.build();
    }
    
    /**
     * 计算怪物对玩家造成的伤害（简化版本）
     */
    public static DamageResult calculateMonsterDamage(
            LivingEntity attacker,
            Player target,
            MonsterStats monsterStats
    ) {
        return calculateMonsterDamage(attacker, target, monsterStats, null, null);
    }
    
    /**
     * 计算基础伤害 (在min和max之间随机)
     */
    private static float calculateBaseDamage(WeaponStats stats) {
        return randomRange(stats.getMinDamage(), stats.getMaxDamage());
    }
    
    /**
     * 计算暴击
     */
    @SuppressWarnings("null")
        private static CritResult calculateCrit(
            WeaponStats weaponStats,
            Player attacker,
            LivingEntity target,
            PlayerStardewData playerData,
            CombatBuffManager buffManager,
            EquipmentStats equipmentStats,
            boolean forceCrit,
            float skillCritBonus
        ) {
        // 强制暴击（某些技能）
        if (forceCrit) {
            return new CritResult(true, calculateCritMultiplier(weaponStats, attacker, target, playerData, equipmentStats));
        }
        
        // 基础暴击�?
        float critChance = weaponStats.getCritChance();
        
        // 加上武器额外暴击�?
        critChance += weaponStats.getBonusCritChance();
        
        // 加上装备暴击�?
        if (equipmentStats != null) {
            critChance += equipmentStats.getCritChance();
        }
        
        // 加上buff暴击�?
        if (buffManager != null) {
            critChance += buffManager.getCritChanceBonus();
        }
        
        // 技能额外暴击率加成
        if (skillCritBonus > 0.0f) {
            critChance += skillCritBonus;
        }

        // 匕首额外暴击率加�?
        if (weaponStats.getWeaponType() == WeaponType.DAGGER) {
            critChance += 0.005f;  // +0.5%
            critChance *= 1.12f;   // 再乘1.12
        }
        
        // Scout职业: 暴击�?50%
        if (playerData != null && playerData.hasProfession(ProfessionType.SCOUT)) {
            critChance *= 1.5f;
        }

        if (attacker != null && target != null) {
            long nowTick = attacker.level() != null ? attacker.level().getGameTime() : 0L;
            critChance += com.stardew.craft.combat.skill.OssifiedMarkTracker.getCritChanceBonus(target, attacker, nowTick);
            critChance += com.stardew.craft.combat.skill.ElfBladeMarkTracker.getCritChanceBonus(target, attacker, nowTick);
        }

        if (target != null) {
            net.minecraft.world.effect.MobEffect weakPointEffect = ModMobEffects.WEAK_POINT.get();
            MobEffectInstance weakPoint = target.getEffect(net.minecraft.core.Holder.direct(weakPointEffect));
            if (weakPoint != null) {
                int amp = weakPoint.getAmplifier();
                critChance += 0.05f * (amp + 1);
            }
        }
        
        boolean isCrit = random.nextFloat() < critChance;
        
        if (!isCrit) {
            return new CritResult(false, 1.0f);
        }
        
        return new CritResult(true, calculateCritMultiplier(weaponStats, attacker, target, playerData, equipmentStats));
    }
    
    /**
     * 计算暴击倍率
     */
        private static float calculateCritMultiplier(
            WeaponStats weaponStats,
            Player attacker,
            LivingEntity target,
            PlayerStardewData playerData,
            EquipmentStats equipmentStats
        ) {
        float critMultiplier = DEFAULT_CRIT_MULTIPLIER;
        
        // 武器暴击伤害加成
        critMultiplier += weaponStats.getBonusCritPower() / 100.0f;
        
        // 装备暴击伤害加成
        if (equipmentStats != null) {
            critMultiplier += equipmentStats.getCritPower();
        }
        
        // Desperado职业: 暴击伤害翻�?
        if (playerData != null && playerData.hasProfession(ProfessionType.DESPERADO)) {
            critMultiplier *= 2.0f;
        }

        if (attacker != null && target != null) {
            long nowTick = attacker.level() != null ? attacker.level().getGameTime() : 0L;
            critMultiplier += com.stardew.craft.combat.skill.OssifiedExecutionTracker.getCritDamageBonus(attacker, target, nowTick);
        }
        
        return critMultiplier;
    }
    
    /**
     * 计算职业乘区
     */
    private static float calculateProfessionMultiplier(PlayerStardewData playerData) {
        if (playerData == null) return 1.0f;
        
        float multiplier = 1.0f;
        
        // Fighter: 伤害+10%
        if (playerData.hasProfession(ProfessionType.FIGHTER)) {
            multiplier *= 1.10f;
        }
        
        // Brute: 伤害+15%
        if (playerData.hasProfession(ProfessionType.BRUTE)) {
            multiplier *= 1.15f;
        }
        
        return multiplier;
    }
    
    /**
     * 计算Buff乘区
     */
    private static float calculateBuffMultiplier(Player player, PlayerStardewData playerData, CombatBuffManager buffManager) {
        float multiplier = 1.0f;
        
        // �?CombatBuffManager 获取攻击力百分比buff
        // 注意：这里只计算百分比乘区，固定值加成在基础伤害阶段处理
        // 暂时没有百分比伤害buff，保留扩展空�?
        
        return Math.max(0.1f, multiplier);  // 最�?0%伤害
    }
    
    /**
     * 计算玩家总防御�?
     */
    private static float calculatePlayerDefense(
            PlayerStardewData playerData,
            CombatBuffManager buffManager,
            EquipmentStats equipmentStats
    ) {
        float defense = 0;
        
        // 装备防御
        if (equipmentStats != null) {
            defense += equipmentStats.getDefense();
        }
        
        // Buff防御
        if (buffManager != null) {
            defense += buffManager.getDefenseBonus();
        }
        
        // Defender职业: +25防御
        if (playerData != null && playerData.hasProfession(ProfessionType.DEFENDER)) {
            defense += 25;
        }
        
        return defense;
    }
    
    /**
     * 计算目标防御减免 (怪物韧�?
     */
    private static float calculateDefenseReduction(float damage, MonsterStats targetStats) {
        float resilience = targetStats.getResilience();
        
        // 防御衰减机制: 当防御�?>= 伤害�?0%时，防御值随机衰�?-20%
        if (resilience >= damage * DEFENSE_DECAY_THRESHOLD) {
            float decay = random.nextFloat() * DEFENSE_DECAY_MAX;
            resilience *= (1.0f - decay);
        }
        
        return resilience;
    }
    
    /**
     * 计算玩家防御减免
     */
    private static float calculatePlayerDefenseReduction(float damage, float defense) {
        // 同样的防御衰减机�?
        if (defense >= damage * DEFENSE_DECAY_THRESHOLD) {
            float decay = random.nextFloat() * DEFENSE_DECAY_MAX;
            defense *= (1.0f - decay);
        }
        
        return defense;
    }

    /**
     * 根据防御值计算最终减免（公开给事件层使用�?
     */
    public static float calculateDefenseReductionFromDefense(float damage, float defense) {
        if (damage <= 0 || defense <= 0) return 0.0f;
        return calculatePlayerDefenseReduction(damage, defense);
    }
    
    /**
     * 检测闪�?
     */
    private static boolean checkDodge(MonsterStats targetStats, WeaponStats weaponStats, EquipmentStats equipmentStats) {
        float missChance = targetStats.getMissChance();
        float precision = weaponStats.getPrecision();
        
        // 装备精确度加�?
        // 注意：当前EquipmentStats没有precision字段，如需要可以添�?
        
        // 精确度降低闪避率
        float actualMissChance = missChance - (missChance * precision / 100.0f);
        actualMissChance = Math.max(0, actualMissChance);
        
        return random.nextFloat() < actualMissChance;
    }
    
    /**
     * 随机范围�?
     */
    private static float randomRange(float min, float max) {
        return min + random.nextFloat() * (max - min);
    }
    
    /**
     * 暴击结果
     */
    private record CritResult(boolean isCrit, float multiplier) {}
}

