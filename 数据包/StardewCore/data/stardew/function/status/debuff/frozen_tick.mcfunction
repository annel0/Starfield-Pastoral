# data/stardew/function/status/debuff/frozen_tick.mcfunction
# 冰冻效果 tick 处理 (冰霜怪物攻击造成)
# 效果: 移动速度大幅降低

# 持续时间倒计时
scoreboard players remove @s sd_frozen_duration 1

# 应用强力减速效果
execute if score @s sd_frozen_level matches 1 run effect give @s minecraft:slowness 1 2 true
execute if score @s sd_frozen_level matches 2 run effect give @s minecraft:slowness 1 3 true
execute if score @s sd_frozen_level matches 3.. run effect give @s minecraft:slowness 1 4 true

# 挖掘疲劳效果
execute if score @s sd_frozen_level matches 2.. run effect give @s minecraft:mining_fatigue 1 0 true

# 粒子效果
particle minecraft:snowflake ~ ~1 ~ 0.3 0.5 0.3 0.1 5
particle minecraft:white_smoke ~ ~0.5 ~ 0.2 0.3 0.2 0.02 2

# 持续时间结束时清除效果
execute if score @s sd_frozen_duration matches ..0 run function stardew:status/debuff/frozen_remove
