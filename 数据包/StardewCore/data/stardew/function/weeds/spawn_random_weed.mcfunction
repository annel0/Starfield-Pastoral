# 在当前位置生成一个杂草
# 执行位置: 已对齐到方块中心底部 (X.5, Y, Z.5)
# 随机从 CMD 4700, 4702, 4703 中选择一个

# 生成随机数 (0-2)
execute store result score #weed_type sd_temp run random value 0..2

# 1. 生成 interaction 实体（占据整个方块，用于碰撞检测）
# 位置：方块底部中心 (~ ~ ~)
# 尺寸：1x1 正好占据一个方块
summon interaction ~ ~ ~ {Tags:["weed_hitbox","new_weed_hitbox"],width:1.0f,height:1.0f,response:true}

# 2. 生成 item_display 实体（视觉模型，在方块中心）
# 位置：方块中心 (~ ~0.5 ~)
execute if score #weed_type sd_temp matches 0 run summon item_display ~ ~0.5 ~ {Tags:["weed","new_weed"],billboard:"fixed",transformation:{left_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,1f,1f],right_rotation:[0f,0f,0f,1f]},item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":4700}}}
execute if score #weed_type sd_temp matches 1 run summon item_display ~ ~0.5 ~ {Tags:["weed","new_weed"],billboard:"fixed",transformation:{left_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,1f,1f],right_rotation:[0f,0f,0f,1f]},item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":4702}}}
execute if score #weed_type sd_temp matches 2 run summon item_display ~ ~0.5 ~ {Tags:["weed","new_weed"],billboard:"fixed",transformation:{left_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,1f,1f],right_rotation:[0f,0f,0f,1f]},item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":4703}}}

# 移除临时标签
tag @e[tag=new_weed] remove new_weed
tag @e[tag=new_weed_hitbox] remove new_weed_hitbox

# 播放音效
playsound minecraft:block.grass.place block @a ~ ~ ~ 1 1
