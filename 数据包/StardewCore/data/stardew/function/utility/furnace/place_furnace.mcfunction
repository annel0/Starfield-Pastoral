# data/stardew/function/utility/furnace/place_furnace.mcfunction
# 在射线击中的位置放置熔炉实用设施
# 执行位置：射线击中的方块表面

# 0. 获取玩家朝向并计算旋转角度
execute as @a[tag=sd_placing_furnace,limit=1] run function stardew:utility/get_rotation

# 1. 检查是否已有设施 (防止重复放置)
execute align xyz positioned ~0.5 ~1 ~0.5 if entity @e[type=interaction,tag=sd_utility,tag=sd_furnace,distance=..0.5] as @a[tag=sd_placing_furnace,limit=1] run tellraw @s {"text":"这里已经有设施了！","color":"red"}
execute align xyz positioned ~0.5 ~1 ~0.5 if entity @e[type=interaction,tag=sd_utility,tag=sd_furnace,distance=..0.5] as @a[tag=sd_placing_furnace,limit=1] run tag @s remove sd_placing_furnace
execute align xyz positioned ~0.5 ~1 ~0.5 if entity @e[type=interaction,tag=sd_utility,tag=sd_furnace,distance=..0.5] run return 1

# 2. 根据朝向召唤视觉实体 (oak_log with CMD 101, 位置上移0.625格, 缩放1.2倍)
execute if score #rotation sd_temp matches 0 align xyz positioned ~0.5 ~1.625 ~0.5 run summon minecraft:item_display ~ ~ ~ {Tags:["sd_utility","sd_furnace","sd_furnace_visual","init_utility"],transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1.2f,1.2f,1.2f]},item:{id:"minecraft:oak_log",count:1,components:{"minecraft:custom_model_data":101}}}
execute if score #rotation sd_temp matches 90 align xyz positioned ~0.5 ~1.625 ~0.5 run summon minecraft:item_display ~ ~ ~ {Tags:["sd_utility","sd_furnace","sd_furnace_visual","init_utility"],transformation:{left_rotation:[0f,0.7071068f,0f,0.7071068f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1.2f,1.2f,1.2f]},item:{id:"minecraft:oak_log",count:1,components:{"minecraft:custom_model_data":101}}}
execute if score #rotation sd_temp matches 180 align xyz positioned ~0.5 ~1.625 ~0.5 run summon minecraft:item_display ~ ~ ~ {Tags:["sd_utility","sd_furnace","sd_furnace_visual","init_utility"],transformation:{left_rotation:[0f,1f,0f,0f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1.2f,1.2f,1.2f]},item:{id:"minecraft:oak_log",count:1,components:{"minecraft:custom_model_data":101}}}
execute if score #rotation sd_temp matches 270 align xyz positioned ~0.5 ~1.625 ~0.5 run summon minecraft:item_display ~ ~ ~ {Tags:["sd_utility","sd_furnace","sd_furnace_visual","init_utility"],transformation:{left_rotation:[0f,-0.7071068f,0f,0.7071068f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1.2f,1.2f,1.2f]},item:{id:"minecraft:oak_log",count:1,components:{"minecraft:custom_model_data":101}}}

# 3. 放置屏障方块 (真实碰撞箱)
execute align xyz positioned ~ ~1 ~ run setblock ~ ~ ~ minecraft:barrier

# 4. 召唤交互实体 (稍微大一点以便右键)
execute align xyz positioned ~0.5 ~1 ~0.5 run summon minecraft:interaction ~ ~ ~ {Tags:["sd_utility","sd_furnace","sd_furnace_interaction","init_utility"],width:1.1f,height:1.1f,response:1b}

# 5. 给交互实体添加自定义数据
execute as @e[tag=init_utility,tag=sd_furnace_interaction,distance=..2] run data modify entity @s Tags append value "sd_utility_interaction"

# 6. 初始化计分板数据
execute as @e[tag=init_utility,tag=sd_furnace_interaction,distance=..2] run scoreboard players set @s sd_furnace_state 0
execute as @e[tag=init_utility,tag=sd_furnace_interaction,distance=..2] run scoreboard players set @s sd_furnace_type 0
execute as @e[tag=init_utility,tag=sd_furnace_interaction,distance=..2] run scoreboard players set @s sd_furnace_timer 0
execute as @e[tag=init_utility,tag=sd_furnace_interaction,distance=..2] run scoreboard players set @s sd_furnace_max_time 0
execute as @e[tag=init_utility,tag=sd_furnace_interaction,distance=..2] run scoreboard players set @s sd_utility_active 0
# 存储旋转角度到视觉实体和交互实体
execute as @e[tag=init_utility,tag=sd_furnace_visual,distance=..2] run scoreboard players operation @s sd_rotation = #rotation sd_temp
execute as @e[tag=init_utility,tag=sd_furnace_interaction,distance=..2] run scoreboard players operation @s sd_rotation = #rotation sd_temp

# 7. 播放放置音效和粒子效果
execute as @e[tag=init_utility,distance=..2,limit=1] at @s run playsound minecraft:block.stone.place block @a ~ ~ ~ 1 0.8
execute align xyz positioned ~0.5 ~1.5 ~0.5 run particle minecraft:block{block_state:"minecraft:furnace"} ~ ~ ~ 0.3 0.3 0.3 0 20

# 8. 消耗物品 (非创造模式)
execute if entity @e[tag=init_utility,distance=..2] as @a[tag=sd_placing_furnace,limit=1,gamemode=!creative,gamemode=!spectator] run item modify entity @s weapon.mainhand stardew:consume_one

# 8.5 初始化视觉实体的高亮系统
execute as @e[tag=init_utility,type=item_display,distance=..2] run function stardew:utility/init_highlight

# 9. 初始化完成
tag @e[tag=init_utility] remove init_utility

# 10. 清除标记
tag @a[tag=sd_placing_furnace] remove sd_placing_furnace
