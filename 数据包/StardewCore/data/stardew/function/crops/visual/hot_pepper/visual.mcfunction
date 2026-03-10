# data/stardew/functions/crops/visual/hot_pepper/visual.mcfunction
# [执行�? 作物Marker]
# Max Age: 5

# 1. 模型更新 (CMD = Base + Stage Index)
# Stage 0: 0..0
execute if score @s sd_crop_age matches 0..0 at @s run data merge entity @e[type=item_display,tag=sd_crop_vis,distance=..0.5,limit=1] {item:{components:{"minecraft:custom_model_data":2700}}}

# Stage 1: 1..1
execute if score @s sd_crop_age matches 1..1 at @s run data merge entity @e[type=item_display,tag=sd_crop_vis,distance=..0.5,limit=1] {item:{components:{"minecraft:custom_model_data":2701}}}

# Stage 2: 2..4
execute if score @s sd_crop_age matches 2..4 at @s run data merge entity @e[type=item_display,tag=sd_crop_vis,distance=..0.5,limit=1] {item:{components:{"minecraft:custom_model_data":2702}}}

# Stage 3 (Mature): 5..5
execute if score @s sd_crop_age matches 5..5 at @s run data merge entity @e[type=item_display,tag=sd_crop_vis,distance=..0.5,limit=1] {item:{components:{"minecraft:custom_model_data":2703}}}

# 2. 文字更新 (Max: 5)
# 临时打标以供 text_display 读取分数
tag @s add current_crop
execute at @s run scoreboard players operation @e[type=text_display,tag=sd_info_text,distance=..1.0,limit=1,sort=nearest] sd_crop_age = @s sd_crop_age

execute at @s as @e[type=text_display,tag=sd_info_text,distance=..1.0,limit=1,sort=nearest] run data modify entity @s text set value '[{"text":"辣椒\\n","color":"white","bold":true},{"score":{"name":"@s","objective":"sd_crop_age"},"color":"white","bold":false},{"text":"/5","color":"gray"}]'
tag @s remove current_crop

# 3. 特效 (成熟后才执行)
execute if score @s sd_crop_age matches 5.. run particle minecraft:happy_villager ~ ~0.5 ~ 0.2 0.2 0.2 0 5
