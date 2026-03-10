package com.stardew.craft.combat.buff;

/**
 * 单个Buff实例
 * 存储buff类型、数值和持续时间
 */
public class CombatBuff {
    
    private final CombatBuffType type;
    private final int level;            // buff等级/数值
    private final int duration;         // 剩余持续时间（tick）
    private final int maxDuration;      // 最大持续时间（tick）
    private final String source;        // 来源（食物ID、装备ID等）
    
    private CombatBuff(Builder builder) {
        this.type = builder.type;
        this.level = builder.level;
        this.duration = builder.duration;
        this.maxDuration = builder.maxDuration;
        this.source = builder.source;
    }
    
    // Getters
    public CombatBuffType getType() { return type; }
    public int getLevel() { return level; }
    public int getDuration() { return duration; }
    public int getMaxDuration() { return maxDuration; }
    public String getSource() { return source; }
    
    /**
     * 创建一个减少了持续时间的新buff实例
     */
    public CombatBuff withReducedDuration(int ticksReduced) {
        int newDuration = Math.max(0, this.duration - ticksReduced);
        return new Builder()
            .type(this.type)
            .level(this.level)
            .duration(newDuration)
            .maxDuration(this.maxDuration)
            .source(this.source)
            .build();
    }
    
    /**
     * buff是否已过期
     */
    public boolean isExpired() {
        return duration <= 0;
    }
    
    /**
     * 获取剩余时间百分比
     */
    public float getRemainingPercent() {
        if (maxDuration <= 0) return 0;
        return (float) duration / maxDuration;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private CombatBuffType type;
        private int level = 1;
        private int duration = 0;
        private int maxDuration = 0;
        private String source = "unknown";
        
        public Builder type(CombatBuffType val) { this.type = val; return this; }
        public Builder level(int val) { this.level = val; return this; }
        public Builder duration(int val) { this.duration = val; return this; }
        public Builder maxDuration(int val) { this.maxDuration = val; return this; }
        public Builder source(String val) { this.source = val; return this; }
        
        /**
         * 便捷方法：同时设置duration和maxDuration
         */
        public Builder durationTicks(int ticks) {
            this.duration = ticks;
            this.maxDuration = ticks;
            return this;
        }
        
        /**
         * 便捷方法：以秒为单位设置持续时间
         */
        public Builder durationSeconds(int seconds) {
            return durationTicks(seconds * 20);  // 20 ticks per second
        }
        
        /**
         * 便捷方法：以分钟为单位设置持续时间
         */
        public Builder durationMinutes(int minutes) {
            return durationSeconds(minutes * 60);
        }
        
        public CombatBuff build() {
            if (type == null) {
                throw new IllegalStateException("Buff type cannot be null");
            }
            return new CombatBuff(this);
        }
    }
}
