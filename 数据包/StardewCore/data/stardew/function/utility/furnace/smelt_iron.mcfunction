# data/stardew/function/utility/f# 6. 改变视觉实体为工作状态 (CMD 102)
execute as @e[tag=sd_current_furnace_visual] run data merge entity @s {item:{id:"minecraft:oak_log",count:1,components:{"minecraft:custom_model_data":102}}}

# 7. 获取视觉实体的UUID前半部分作为唯一标识，并同步到 interaction 和 visual 实体
execute store result score @e[tag=sd_current_furnace_visual] sd_furnace_id run data get entity @e[tag=sd_current_furnace_visual,limit=1] UUID[0]
execute as @e[tag=sd_current_furnace_visual] at @s run scoreboard players operation @e[type=interaction,tag=sd_current_furnace,distance=..2,limit=1] sd_furnace_id = @s sd_furnace_id

# 8. 在熔炉上方召唤产物展示 (铁锭)，并设置相同的IDsmelt_iron.mcfunction
# 熔炼铁锭 - 玩家拿铁粒右键熔炉时调用
# 执行者: 玩家 (@s)
# 前提: 附近有一个标记为 sd_interacting_furnace 的熔炉

# 1. 检查玩家是否有5个铁粒和1个煤炭
execute store result score @s sd_temp run clear @s minecraft:paper[custom_model_data=7004] 0
execute unless score @s sd_temp matches 5.. run tellraw @s {"text":"需要5个铁粒！","color":"red"}
execute unless score @s sd_temp matches 5.. run return 0

execute store result score @s sd_temp run clear @s minecraft:paper[custom_model_data=7002] 0
execute unless score @s sd_temp matches 1.. run tellraw @s {"text":"需要1个煤炭！","color":"red"}
execute unless score @s sd_temp matches 1.. run return 0

# 2. 扣除材料
clear @s minecraft:paper[custom_model_data=7004] 5
clear @s minecraft:paper[custom_model_data=7002] 1

# 3. 找到被标记的熔炉实体
execute as @e[type=interaction,tag=sd_interacting_furnace,distance=..5,limit=1] run tag @s add sd_current_furnace
execute as @e[type=item_display,tag=sd_furnace_visual,distance=..5,limit=1,sort=nearest] run tag @s add sd_current_furnace_visual

# 4. 设置熔炉状态为工作中（使用增量计时）
scoreboard players set @e[tag=sd_current_furnace] sd_furnace_state 1
scoreboard players set @e[tag=sd_current_furnace] sd_furnace_type 2
# 初始化已工作时间为 0，设置需要的总时间（120分钟）
scoreboard players set @e[tag=sd_current_furnace] sd_furnace_timer 0
scoreboard players set @e[tag=sd_current_furnace] sd_furnace_max_time 120
# 记录上次更新时间为当前游戏时间（用于支持时间跳过）
execute as @e[tag=sd_current_furnace] run scoreboard players operation @s sd_furnace_last_time = Global sd_time
# 标记为通用实用设施活跃（用于 new_day 统一奖励）
execute as @e[tag=sd_current_furnace] run scoreboard players set @s sd_utility_active 1

# 5. 改变视觉实体为工作状态 (CMD 102)，保持旋转
execute as @e[tag=sd_current_furnace_visual] run data merge entity @s {item:{id:"minecraft:oak_log",count:1,components:{"minecraft:custom_model_data":102}}}
execute as @e[tag=sd_current_furnace_visual] run function stardew:utility/apply_rotation

# 6. 获取视觉实体的UUID前半部分作为唯一标识
execute store result score @e[tag=sd_current_furnace_visual] sd_furnace_id run data get entity @e[tag=sd_current_furnace_visual,limit=1] UUID[0]
execute as @e[tag=sd_current_furnace_visual] at @s run scoreboard players operation @e[type=interaction,tag=sd_current_furnace,distance=..2,limit=1] sd_furnace_id = @s sd_furnace_id

# 6.5 获取旋转角度
execute as @e[tag=sd_current_furnace_visual] run scoreboard players operation #rotation sd_temp = @s sd_rotation

# 7. 在熔炉上方召唤产物展示（根据旋转角度）(铁锭)，并设置相同的ID
execute if score #rotation sd_temp matches 0 as @e[tag=sd_current_furnace_visual] at @s run summon minecraft:item_display ~ ~1.3 ~ {Tags:["sd_furnace_product","sd_current_product"],transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.5f,0.5f,0.5f]},item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":7008}}}
execute if score #rotation sd_temp matches 90 as @e[tag=sd_current_furnace_visual] at @s run summon minecraft:item_display ~ ~1.3 ~ {Tags:["sd_furnace_product","sd_current_product"],transformation:{left_rotation:[0f,0.7071068f,0f,0.7071068f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.5f,0.5f,0.5f]},item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":7008}}}
execute if score #rotation sd_temp matches 180 as @e[tag=sd_current_furnace_visual] at @s run summon minecraft:item_display ~ ~1.3 ~ {Tags:["sd_furnace_product","sd_current_product"],transformation:{left_rotation:[0f,1f,0f,0f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.5f,0.5f,0.5f]},item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":7008}}}
execute if score #rotation sd_temp matches 270 as @e[tag=sd_current_furnace_visual] at @s run summon minecraft:item_display ~ ~1.3 ~ {Tags:["sd_furnace_product","sd_current_product"],transformation:{left_rotation:[0f,-0.7071068f,0f,0.7071068f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.5f,0.5f,0.5f]},item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":7008}}}
execute as @e[tag=sd_current_furnace_visual] run scoreboard players operation @e[tag=sd_current_product,limit=1,sort=nearest] sd_furnace_id = @s sd_furnace_id

# 8. 召唤时间文本显示，并设置相同的ID
execute as @e[tag=sd_current_furnace_visual] at @s run summon minecraft:text_display ~ ~1.7 ~ {Tags:["sd_furnace_time","sd_current_time"],text:'{"text":"120分钟","color":"yellow"}',billboard:"center",alignment:"center",background:0}
execute as @e[tag=sd_current_furnace_visual] run scoreboard players operation @e[tag=sd_current_time,limit=1,sort=nearest] sd_furnace_id = @s sd_furnace_id

# 9. 在屏障上方放置光源方块 (light level 8) - 基于视觉实体位置
execute as @e[tag=sd_current_furnace_visual] at @s align xyz positioned ~ ~1 ~ run setblock ~ ~ ~ minecraft:light[level=8]

# 9. 播放音效
execute as @e[tag=sd_current_furnace] at @s run playsound minecraft:block.furnace.fire_crackle block @a ~ ~ ~ 1 1
execute as @e[tag=sd_current_furnace] at @s run playsound minecraft:block.blastfurnace.fire_crackle block @a ~ ~ ~ 0.8 0.9

# 10. 清除标记
tag @e[tag=sd_current_furnace] remove sd_current_furnace
tag @e[tag=sd_current_furnace_visual] remove sd_current_furnace_visual
tag @e[tag=sd_current_product] remove sd_current_product
tag @e[tag=sd_current_time] remove sd_current_time
