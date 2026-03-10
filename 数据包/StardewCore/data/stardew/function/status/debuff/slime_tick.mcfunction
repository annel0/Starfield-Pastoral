# data/stardew/function/status/debuff/slime_tick.mcfunction
# 粘液效果 tick 处理 (史莱姆攻击造成)
# 效果: 移动速度减慢 + 轻微反胃

# 持续时间倒计时
scoreboard players remove @s sd_slime_duration 1

# 应用速度减慢效果
execute if score @s sd_slime_level matches 1 run effect give @s minecraft:slowness 1 0 true
execute if score @s sd_slime_level matches 2 run effect give @s minecraft:slowness 1 1 true
execute if score @s sd_slime_level matches 3.. run effect give @s minecraft:slowness 1 2 true

# 应用反胃效果 (轻微视觉效果)
execute if score @s sd_slime_level matches 1 run effect give @s minecraft:nausea 5 0 true
execute if score @s sd_slime_level matches 2.. run effect give @s minecraft:nausea 8 0 true

# 粒子效果
particle minecraft:item_slime ~ ~1 ~ 0.3 0.5 0.3 0.1 3

# 持续时间结束时清除效果
execute if score @s sd_slime_duration matches ..0 run function stardew:status/debuff/slime_remove
