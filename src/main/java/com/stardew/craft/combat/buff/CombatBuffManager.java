package com.stardew.craft.combat.buff;

import java.util.*;

/**
 * Buff管理器
 * 管理玩家身上的所有战斗相关buff
 */
public class CombatBuffManager {
    
    // 当前激活的buff，同类型buff只保留最高等级
    private final Map<CombatBuffType, CombatBuff> activeBuffs = new HashMap<>();
    
    /**
     * 添加或更新buff
     * 如果已有同类型buff，只有在新buff等级更高或持续时间更长时才会替换
     */
    public void addBuff(CombatBuff buff) {
        CombatBuffType type = buff.getType();
        CombatBuff existing = activeBuffs.get(type);
        
        if (existing == null) {
            // 没有现有buff，直接添加
            activeBuffs.put(type, buff);
        } else if (buff.getLevel() > existing.getLevel()) {
            // 新buff等级更高，替换
            activeBuffs.put(type, buff);
        } else if (buff.getLevel() == existing.getLevel() && 
                   buff.getDuration() > existing.getDuration()) {
            // 等级相同但持续时间更长，替换
            activeBuffs.put(type, buff);
        }
        // 否则保留现有buff
    }
    
    /**
     * 移除指定类型的buff
     */
    public void removeBuff(CombatBuffType type) {
        activeBuffs.remove(type);
    }
    
    /**
     * 获取指定类型的buff
     */
    public Optional<CombatBuff> getBuff(CombatBuffType type) {
        return Optional.ofNullable(activeBuffs.get(type));
    }
    
    /**
     * 检查是否有指定类型的buff
     */
    public boolean hasBuff(CombatBuffType type) {
        return activeBuffs.containsKey(type);
    }
    
    /**
     * 获取指定类型buff的等级，如果没有则返回0
     */
    public int getBuffLevel(CombatBuffType type) {
        CombatBuff buff = activeBuffs.get(type);
        return buff != null ? buff.getLevel() : 0;
    }
    
    /**
     * 获取所有激活的buff
     */
    public Collection<CombatBuff> getActiveBuffs() {
        return Collections.unmodifiableCollection(activeBuffs.values());
    }
    
    /**
     * 每tick调用，更新所有buff的持续时间
     */
    public void tick() {
        Iterator<Map.Entry<CombatBuffType, CombatBuff>> it = activeBuffs.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<CombatBuffType, CombatBuff> entry = it.next();
            CombatBuff updated = entry.getValue().withReducedDuration(1);
            
            if (updated.isExpired()) {
                it.remove();
            } else {
                entry.setValue(updated);
            }
        }
    }
    
    /**
     * 清除所有buff
     */
    public void clearAll() {
        activeBuffs.clear();
    }
    
    /**
     * 清除所有负面buff
     */
    public void clearNegativeBuffs() {
        activeBuffs.entrySet().removeIf(entry -> !entry.getKey().isBeneficial());
    }
    
    // ==================== 战斗相关便捷方法 ====================
    
    /**
     * 获取攻击力加成
     * 每级攻击buff = +3攻击力
     */
    public int getAttackBonus() {
        return getBuffLevel(CombatBuffType.ATTACK) * 3;
    }
    
    /**
     * 获取防御力加成
     * 每级防御buff = +3防御力
     */
    public int getDefenseBonus() {
        return getBuffLevel(CombatBuffType.DEFENSE) * 3;
    }
    
    /**
     * 获取免疫加成
     * 每级免疫buff = +1免疫值
     */
    public int getImmunityBonus() {
        return getBuffLevel(CombatBuffType.IMMUNITY);
    }
    
    /**
     * 获取暴击率加成
     * 每级暴击buff = +2%暴击率
     */
    public float getCritChanceBonus() {
        return getBuffLevel(CombatBuffType.CRITICAL) * 0.02f;
    }
    
    /**
     * 获取速度加成
     * 每级速度buff = +10%移动速度
     */
    public float getSpeedBonus() {
        return getBuffLevel(CombatBuffType.SPEED) * 0.10f;
    }
}
