# data/stardew/function/status/buff/regen_tick.mcfunction
# 再生效果 tick 处理
# 效果: 持续恢复生命值

# 持续时间倒计时
scoreboard players remove @s sd_regen_duration 1

# 每20 tick (1秒) 恢复生命
execute if score @s sd_regen_duration matches 1.. if score @s sd_regen_duration modulo 20 matches 0 run function stardew:status/buff/regen_heal

# 粒子效果
particle minecraft:heart ~ ~1 ~ 0.3 0.5 0.3 0.05 1

# 持续时间结束时清除效果
execute if score @s sd_regen_duration matches ..0 run function stardew:status/buff/regen_remove
