# data/stardew/function/status/buff/resistance_tick.mcfunction
# 抗性效果 tick 处理
# 效果: 伤害减免 (在战斗计算中处理)

# 持续时间倒计时
scoreboard players remove @s sd_resistance_duration 1

# 粒子效果
particle minecraft:enchanted_hit ~ ~1 ~ 0.3 0.5 0.3 0.05 2

# 持续时间结束时清除效果
execute if score @s sd_resistance_duration matches ..0 run function stardew:status/buff/resistance_remove
