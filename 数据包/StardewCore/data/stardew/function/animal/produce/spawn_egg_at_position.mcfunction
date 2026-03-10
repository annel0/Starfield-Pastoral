# ================================================================
# 星露谷物语 - 在指定位置生成鸡蛋实体
# ================================================================
# 用途：在当前位置附近随机位置生成鸡蛋视觉实体和交互体
# 调用：从 spawn_egg_at_building.mcfunction 或 spawn_egg_entity.mcfunction 调用

# 生成随机偏移（-5到5格的范围）
execute store result score #offset_x stardew.animal.temp run random value -5..5
execute store result score #offset_z stardew.animal.temp run random value -5..5

# 计算生成位置
execute store result storage stardew:temp egg_pos.x int 1 run scoreboard players operation #spawn_x stardew.animal.temp = #offset_x stardew.animal.temp
execute store result storage stardew:temp egg_pos.z int 1 run scoreboard players operation #spawn_z stardew.animal.temp = #offset_z stardew.animal.temp

# 生成视觉实体（item_display）
# 根据visual_cmd直接生成对应的模型
# 鸡蛋：103=普通鸡蛋, 104=大鸡蛋
execute if score #visual_cmd stardew.animal.temp matches 103 positioned ~0 ~0.575 ~0 run summon item_display ~ ~ ~ {Tags:["stardew.egg.visual","stardew.new"],billboard:"fixed",transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,1f,1f]},item:{id:"minecraft:oak_wood",count:1,components:{"minecraft:custom_model_data":103}},interpolation_duration:2}
execute if score #visual_cmd stardew.animal.temp matches 104 positioned ~0 ~0.575 ~0 run summon item_display ~ ~ ~ {Tags:["stardew.egg.visual","stardew.new"],billboard:"fixed",transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,1f,1f]},item:{id:"minecraft:oak_wood",count:1,components:{"minecraft:custom_model_data":104}},interpolation_duration:2}

# 鸭子产物：106=鸭蛋, 107=鸭毛
execute if score #visual_cmd stardew.animal.temp matches 106 positioned ~0 ~0.575 ~0 run summon item_display ~ ~ ~ {Tags:["stardew.egg.visual","stardew.new"],billboard:"fixed",transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,1f,1f]},item:{id:"minecraft:oak_wood",count:1,components:{"minecraft:custom_model_data":106}},interpolation_duration:2}
execute if score #visual_cmd stardew.animal.temp matches 107 positioned ~0 ~0.575 ~0 run summon item_display ~ ~ ~ {Tags:["stardew.egg.visual","stardew.new"],billboard:"fixed",transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,1f,1f]},item:{id:"minecraft:oak_wood",count:1,components:{"minecraft:custom_model_data":107}},interpolation_duration:2}

# 兔子产物：108=羊毛, 109=兔子脚
execute if score #visual_cmd stardew.animal.temp matches 108 positioned ~0 ~0.575 ~0 run summon item_display ~ ~ ~ {Tags:["stardew.egg.visual","stardew.new"],billboard:"fixed",transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,1f,1f]},item:{id:"minecraft:oak_wood",count:1,components:{"minecraft:custom_model_data":108}},interpolation_duration:2}
execute if score #visual_cmd stardew.animal.temp matches 109 positioned ~0 ~0.575 ~0 run summon item_display ~ ~ ~ {Tags:["stardew.egg.visual","stardew.new"],billboard:"fixed",transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,1f,1f]},item:{id:"minecraft:oak_wood",count:1,components:{"minecraft:custom_model_data":109}},interpolation_duration:2}

# 清除new标记
tag @e[type=item_display,tag=stardew.new] remove stardew.new

# 生成交互实体
execute positioned ~0 ~0 ~0 run summon interaction ~ ~ ~ {Tags:["stardew.egg.interaction","stardew.new"],width:0.5f,height:0.5f}

# 绑定 CMD 信息到交互体（用于拾取时给物品）
execute as @e[type=interaction,tag=stardew.new,limit=1] store result score @s stardew.item.cmd run scoreboard players get #egg_cmd stardew.animal.temp
execute as @e[type=interaction,tag=stardew.new,limit=1] run tag @s remove stardew.new

# 清除标记
tag @e[tag=stardew.new] remove stardew.new
