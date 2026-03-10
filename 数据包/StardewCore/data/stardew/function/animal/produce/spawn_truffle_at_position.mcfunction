# ================================================================
# 星露谷物语 - 在指定位置生成松露实体
# ================================================================
# 用途：在当前位置附近随机位置生成松露视觉实体和交互体
# 调用：从 spawn_truffle_at_building.mcfunction 或 produce_truffle.mcfunction 调用

# 生成随机偏移（-5到5格的范围）
execute store result score #offset_x stardew.animal.temp run random value -5..5
execute store result score #offset_z stardew.animal.temp run random value -5..5

# 计算生成位置
execute store result storage stardew:temp truffle_pos.x int 1 run scoreboard players operation #spawn_x stardew.animal.temp = #offset_x stardew.animal.temp
execute store result storage stardew:temp truffle_pos.z int 1 run scoreboard players operation #spawn_z stardew.animal.temp = #offset_z stardew.animal.temp

# 生成视觉实体（item_display）
# 松露使用 CMD 110
execute positioned ~0 ~0.575 ~0 run summon item_display ~ ~ ~ {Tags:["stardew.truffle.visual","stardew.new"],billboard:"fixed",transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,1f,1f]},item:{id:"minecraft:oak_wood",count:1,components:{"minecraft:custom_model_data":110}},interpolation_duration:2}

# 清除new标记
tag @e[type=item_display,tag=stardew.new] remove stardew.new

# 生成交互实体
execute positioned ~0 ~0 ~0 run summon interaction ~ ~ ~ {Tags:["stardew.truffle.interaction","stardew.new"],width:0.5f,height:0.5f}

# 绑定 CMD 信息到交互体（用于拾取时给物品）
execute as @e[type=interaction,tag=stardew.new,limit=1] store result score @s stardew.item.cmd run scoreboard players get #truffle_cmd stardew.animal.temp
execute as @e[type=interaction,tag=stardew.new,limit=1] run tag @s remove stardew.new

# 清除标记
tag @e[tag=stardew.new] remove stardew.new