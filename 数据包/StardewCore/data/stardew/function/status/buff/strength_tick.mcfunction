# data/stardew/function/status/buff/strength_tick.mcfunction
# 力量效果 tick 处理
# 效果: 攻击伤害增加 (在战斗计算中处理)

# 持续时间倒计时
scoreboard players remove @s sd_strength_duration 1

# 粒子效果
particle minecraft:crit ~ ~1 ~ 0.3 0.5 0.3 0.05 2

# 持续时间结束时清除效果
execute if score @s sd_strength_duration matches ..0 run function stardew:status/buff/strength_remove
