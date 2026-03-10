# data/stardew/function/status/buff/shield_tick.mcfunction
# 护盾效果 tick 处理
# 效果: 吸收伤害 (在战斗计算中处理)

# 持续时间倒计时
scoreboard players remove @s sd_shield_duration 1

# 粒子效果
particle minecraft:wax_on ~ ~1 ~ 0.3 0.5 0.3 0.05 2

# 持续时间结束时清除效果
execute if score @s sd_shield_duration matches ..0 run function stardew:status/buff/shield_remove
