# data/stardew/functions/crops/planting/plant_bok_choy.mcfunction

# 1. 前置检查 (防堆叠/是否有耕地)
execute align xyz positioned ~0.5 ~1.375 ~0.5 if entity @e[type=marker,tag=sd_crop,distance=..0.5] run tellraw @s {"text":"这里已经种了东西！","color":"red"}
execute align xyz positioned ~0.5 ~1.375 ~0.5 if entity @e[type=marker,tag=sd_crop,distance=..0.5] run return 1

# 2. 季节判断 (检查当前全局 sd_season 分数是否在允许范围内)
# Global sd_season 1=春, 2=夏, 3=秋, 4=冬
execute unless score Global sd_season matches 3 run tellraw @s {"text":"小白菜只能在秋季种植","color":"red"}
execute unless score Global sd_season matches 3 run return 1

# 3. 种植 (召唤实体)
execute if block ~ ~ ~ minecraft:farmland align xyz run function stardew:crops/summon_entities/bok_choy/summon_entities
execute if block ~ ~-1 ~ minecraft:farmland align xyz positioned ~ ~-1 ~ run function stardew:crops/summon_entities/bok_choy/summon_entities

# 4. 反馈特效
execute as @e[tag=init_crop,distance=..0.5,limit=1] at @s run playsound minecraft:item.hoe.till block @a ~ ~ ~ 1 1
execute as @e[tag=init_crop,distance=..0.5,limit=1] at @s run particle minecraft:block{block_state:"minecraft:dirt"} ~ ~ ~ 0.2 0.1 0.2 0 15

# 5. 消耗种子与初始化
execute if entity @e[tag=init_crop,distance=..0.5] as @s[gamemode=!creative,gamemode=!spectator] run function stardew:farming/consume_seed
execute as @e[tag=init_crop,distance=..0.5] at @s if block ~ ~ ~ minecraft:air run setblock ~ ~ ~ minecraft:structure_void
execute as @e[tag=init_crop,distance=..0.5] run scoreboard players set @s sd_crop_age 0
execute as @e[tag=init_crop,distance=..0.5] run scoreboard players set @s sd_max_crop_age 4
execute as @e[tag=init_crop,distance=..0.5] run scoreboard players set @s sd_watered 0
# 添加季节标签
execute as @e[tag=init_crop,distance=..0.5] run tag @s add season_3
# 5.5 鑲ユ枡缁ф壙 - 妫€鏌ュ苟缁ф壙宸叉湁鑲ユ枡
execute as @e[tag=init_crop,distance=..0.5] at @s run tag @s add sd_new_crop
execute as @e[tag=sd_new_crop] at @s run function stardew:farming/fertilizer/inherit_on_plant
execute as @e[tag=sd_new_crop] run tag @s remove sd_new_crop

# 绉婚櫎鍒濆鍖栨爣绛?
execute as @e[tag=init_crop,distance=..0.5] run tag @s remove init_crop
