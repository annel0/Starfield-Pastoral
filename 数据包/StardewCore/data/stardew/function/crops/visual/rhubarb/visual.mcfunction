# data/stardew/functions/crops/visual/rhubarb/visual.mcfunction
# [执行者: 作物Marker]
# Max Age: 13

# 1. 模型更新 (CMD = Base + Stage Index)
# Stage 0: 0..3
execute if score @s sd_crop_age matches 0..3 at @s run data merge entity @e[type=item_display,tag=sd_crop_vis,distance=..0.5,limit=1,sort=nearest] {item:{components:{"minecraft:custom_model_data":1120}}}

# Stage 1: 4..7
execute if score @s sd_crop_age matches 4..7 at @s run data merge entity @e[type=item_display,tag=sd_crop_vis,distance=..0.5,limit=1,sort=nearest] {item:{components:{"minecraft:custom_model_data":1121}}}

# Stage 2: 8..12
execute if score @s sd_crop_age matches 8..12 at @s run data merge entity @e[type=item_display,tag=sd_crop_vis,distance=..0.5,limit=1,sort=nearest] {item:{components:{"minecraft:custom_model_data":1122}}}

# Stage 3 (Mature): 13..13
execute if score @s sd_crop_age matches 13..13 at @s run data merge entity @e[type=item_display,tag=sd_crop_vis,distance=..0.5,limit=1,sort=nearest] {item:{components:{"minecraft:custom_model_data":1123}}}

# 2. 文字更新 (Max: 13)
# 临时打标以供 text_display 读取分数
tag @s add current_crop
execute at @s run scoreboard players operation @e[type=text_display,tag=sd_info_text,distance=..1.0,limit=1,sort=nearest] sd_crop_age = @s sd_crop_age

execute at @s as @e[type=text_display,tag=sd_info_text,distance=..1.0,limit=1,sort=nearest] run data modify entity @s text set value '[{"text":"大黄\\n","color":"white","bold":true},{"score":{"name":"@s","objective":"sd_crop_age"},"color":"white","bold":false},{"text":"/13","color":"gray"}]'
tag @s remove current_crop

# 3. 特效 (成熟后才执行)
execute if score @s sd_crop_age matches 13.. run particle minecraft:happy_villager ~ ~0.5 ~ 0.2 0.2 0.2 0 5
