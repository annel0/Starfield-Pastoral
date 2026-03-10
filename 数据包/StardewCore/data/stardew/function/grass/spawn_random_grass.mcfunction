# 在当前位置生成一个草
# 执行位置: 已对齐到方块中心底部 (X.5, Y, Z.5)
# 随机从 CMD 4710, 4711, 4712 中选择一个

# 生成随机数 (0-2)
execute store result score #grass_type sd_temp run random value 0..2

# 1. 生成 interaction 实体（占据整个方块，用于碰撞检测）
# 位置：方块底部中心 (~ ~ ~)
# 尺寸：1x1 正好占据一个方块
summon interaction ~ ~ ~ {Tags:["sd_grass","grass_hitbox","new_grass_hitbox"],width:1.0f,height:1.0f,response:true}

# 2. 生成 item_display 实体（视觉模型，在方块中心）
# 位置：方块中心 (~ ~0.5 ~)
execute if score #grass_type sd_temp matches 0 run summon item_display ~ ~0.5 ~ {Tags:["sd_grass","grass","new_grass"],billboard:"fixed",transformation:{left_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,1f,1f],right_rotation:[0f,0f,0f,1f]},item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":4710}}}
execute if score #grass_type sd_temp matches 1 run summon item_display ~ ~0.5 ~ {Tags:["sd_grass","grass","new_grass"],billboard:"fixed",transformation:{left_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,1f,1f],right_rotation:[0f,0f,0f,1f]},item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":4711}}}
execute if score #grass_type sd_temp matches 2 run summon item_display ~ ~0.5 ~ {Tags:["sd_grass","grass","new_grass"],billboard:"fixed",transformation:{left_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,1f,1f],right_rotation:[0f,0f,0f,1f]},item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":4712}}}

# 移除临时标签
tag @e[tag=new_grass] remove new_grass
tag @e[tag=new_grass_hitbox] remove new_grass_hitbox