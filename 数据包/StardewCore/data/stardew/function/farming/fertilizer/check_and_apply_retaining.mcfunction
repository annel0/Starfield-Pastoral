# data/stardew/functions/farming/fertilizer/check_and_apply_retaining.mcfunction
# 检查并应用保湿土壤
# 执行者: sd_crop

# 如果作物没有fertilizer_type分数，尝试从nearby fertilizer_marker复制
execute unless score @s sd_fertilizer_type = @s sd_fertilizer_type if entity @e[type=marker,tag=sd_fertilizer_marker,distance=..0.1,limit=1] run function stardew:farming/fertilizer/copy_fertilizer_to_crop_inline

# 检查是否为保湿土壤
execute unless score @s sd_fertilizer_type matches 3 run return 0

# 保湿土壤逻辑
# 生成随机数
execute store result score @s sd_rng run random value 1..100

# Level 1: 33%概率保持湿润
execute if score @s sd_fertilizer_level matches 1 if score @s sd_rng matches 1..33 positioned ~ ~-0.375 ~ if block ~ ~ ~ minecraft:farmland run setblock ~ ~ ~ minecraft:farmland[moisture=7]
execute if score @s sd_fertilizer_level matches 1 if score @s sd_rng matches 1..33 run scoreboard players set @s sd_watered 1

# Level 2: 66%概率保持湿润
execute if score @s sd_fertilizer_level matches 2 if score @s sd_rng matches 1..66 positioned ~ ~-0.375 ~ if block ~ ~ ~ minecraft:farmland run setblock ~ ~ ~ minecraft:farmland[moisture=7]
execute if score @s sd_fertilizer_level matches 2 if score @s sd_rng matches 1..66 run scoreboard players set @s sd_watered 1

# Level 3: 100%保持湿润
execute if score @s sd_fertilizer_level matches 3 positioned ~ ~-0.375 ~ if block ~ ~ ~ minecraft:farmland run setblock ~ ~ ~ minecraft:farmland[moisture=7]
execute if score @s sd_fertilizer_level matches 3 run scoreboard players set @s sd_watered 1
