# data/stardew/functions/farming/visual/melon.mcfunction
# [执行者: 作物Marker]
# Max Age: 12

# 1. 更新文字显示
execute at @s as @e[type=text_display,tag=sd_info_text,distance=..0.8,limit=1] run data modify entity @s text set value '[{"text":"甜瓜\\n","color":"white"},{"score":{"name":"@e[tag=current_crop,limit=1]","objective":"sd_crop_age"},"color":"gold"},{"text":"/12","color":"gray"}]'

# 为了让 text_display 能读取到 marker 的分数，我们需要临时打个标
tag @s add current_crop
# (上面的 text json 里用了 selector 读取 current_crop 的分数)
tag @s remove current_crop

# 2. 更新模型 (Item Display)
# Stage 0
execute if score @s sd_crop_age matches 0 at @s run data merge entity @e[type=item_display,tag=sd_crop_vis,distance=..0.5,limit=1] {item:{id:"minecraft:paper",Count:1b,components:{"minecraft:custom_model_data":3100}}}

# Stage 1
execute if score @s sd_crop_age matches 1..5 at @s run data merge entity @e[type=item_display,tag=sd_crop_vis,distance=..0.5,limit=1] {item:{id:"minecraft:paper",Count:1b,components:{"minecraft:custom_model_data":3101}}}

# Stage 2
execute if score @s sd_crop_age matches 6..11 at @s run data merge entity @e[type=item_display,tag=sd_crop_vis,distance=..0.5,limit=1] {item:{id:"minecraft:paper",Count:1b,components:{"minecraft:custom_model_data":3102}}}

# Stage 3 (Mature)
execute if score @s sd_crop_age matches 12.. at @s run data merge entity @e[type=item_display,tag=sd_crop_vis,distance=..0.5,limit=1] {item:{id:"minecraft:paper",Count:1b,components:{"minecraft:custom_model_data":3103}}}
