# data/stardew/functions/crops/visual/broccoli/visual.mcfunction
# [执行者: 作物Marker]
# Max Age: 8

# 1. 模型更新 (CMD = Base + Stage Index)
# Stage 0: 0..1
execute if score @s sd_crop_age matches 0..1 at @s run data merge entity @e[type=item_display,tag=sd_crop_vis,distance=..0.5,limit=1] {item:{components:{"minecraft:custom_model_data":1160}}}

# Stage 1: 2..3
execute if score @s sd_crop_age matches 2..3 at @s run data merge entity @e[type=item_display,tag=sd_crop_vis,distance=..0.5,limit=1] {item:{components:{"minecraft:custom_model_data":1161}}}

# Stage 2: 4..7
execute if score @s sd_crop_age matches 4..7 at @s run data merge entity @e[type=item_display,tag=sd_crop_vis,distance=..0.5,limit=1] {item:{components:{"minecraft:custom_model_data":1162}}}

# Stage 3 (Mature): 8..8
execute if score @s sd_crop_age matches 8..8 at @s run data merge entity @e[type=item_display,tag=sd_crop_vis,distance=..0.5,limit=1] {item:{components:{"minecraft:custom_model_data":1163}}}

# 2. 文字更新 (Max: 8)
# 临时打标以供 text_display 读取分数
tag @s add current_crop
execute at @s run scoreboard players operation @e[type=text_display,tag=sd_info_text,distance=..1.0,limit=1,sort=nearest] sd_crop_age = @s sd_crop_age

execute at @s as @e[type=text_display,tag=sd_info_text,distance=..1.0,limit=1,sort=nearest] run data modify entity @s text set value '[{"text":"西兰花\\n","color":"white","bold":true},{"score":{"name":"@s","objective":"sd_crop_age"},"color":"white","bold":false},{"text":"/8","color":"gray"}]'
tag @s remove current_crop

# 3. 特效 (成熟后才执行)
execute if score @s sd_crop_age matches 8.. run particle minecraft:happy_villager ~ ~0.5 ~ 0.2 0.2 0.2 0 5

