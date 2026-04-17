package com.stardew.craft.core;

import net.minecraft.world.level.GameRules;

/**
 * 模组自定义 GameRules。
 * 用 /gamerule 命令设置，与原版 gamerule 体验一致。
 */
public final class ModGameRules {

    /**
     * 星露谷维度中需要多少百分比的玩家睡觉才能过夜。
     * 默认 100（全员制），设为 0 则一人即可过夜。
     * 与原版 playersSleepingPercentage 类似。
     */
    public static final GameRules.Key<GameRules.IntegerValue> RULE_STARDEW_SLEEPING_PERCENTAGE =
            GameRules.register("stardewSleepingPercentage",
                    GameRules.Category.PLAYER,
                    GameRules.IntegerValue.create(100));

    /**
     * 挂机超过多少秒的玩家在睡眠投票中被视为 AFK（不计入分母）。
     * 默认 300 秒（5 分钟）。设为 0 则禁用 AFK 检测。
     * "挂机"定义：没有移动、旋转视角、放置/破坏方块、交互、攻击。
     */
    public static final GameRules.Key<GameRules.IntegerValue> RULE_STARDEW_AFK_TIMEOUT =
            GameRules.register("stardewAfkTimeout",
                    GameRules.Category.PLAYER,
                    GameRules.IntegerValue.create(300));

    private ModGameRules() {}

    /** 在 mod 初始化时调用，触发静态字段注册 */
    public static void init() {
        // 静态字段在类加载时注册
    }
}
