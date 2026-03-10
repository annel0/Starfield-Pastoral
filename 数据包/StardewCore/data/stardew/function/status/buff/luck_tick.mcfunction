# data/stardew/function/status/buff/luck_tick.mcfunction
# 幸运效果 tick 处理
# 效果: 暴击率提升 (在战斗计算中处理)

# 持续时间倒计时
scoreboard players remove @s sd_luck_duration 1

# 粒子效果
particle minecraft:glow_squid_ink ~ ~1 ~ 0.3 0.5 0.3 0.02 1

# 持续时间结束时清除效果
execute if score @s sd_luck_duration matches ..0 run function stardew:status/buff/luck_remove
