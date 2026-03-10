# data/stardew/functions/crops/visual/powder_melon/visual.mcfunction
# [执行者: 作物Marker]
# Max Age: 7

# 1. 模型更新 (CMD = Base + Stage Index)
# Stage 0: 0..1
execute if score @s sd_crop_age matches 0..1 at @s run data merge entity @e[type=item_display,tag=sd_crop_vis,distance=..0.5,limit=1] {item:{components:{"minecraft:custom_model_data":1170}}}

# Stage 1: 2..3
execute if score @s sd_crop_age matches 2..3 at @s run data merge entity @e[type=item_display,tag=sd_crop_vis,distance=..0.5,limit=1] {item:{components:{"minecraft:custom_model_data":1171}}}

# Stage 2: 4..6
execute if score @s sd_crop_age matches 4..6 at @s run data merge entity @e[type=item_display,tag=sd_crop_vis,distance=..0.5,limit=1] {item:{components:{"minecraft:custom_model_data":1172}}}

# Stage 3 (Mature): 7..7
execute if score @s sd_crop_age matches 7..7 at @s run data merge entity @e[type=item_display,tag=sd_crop_vis,distance=..0.5,limit=1] {item:{components:{"minecraft:custom_model_data":1173}}}

# 2. 文字更新 (Max: 7)
# 临时打标以供 text_display 读取分数
tag @s add current_crop
execute at @s run scoreboard players operation @e[type=text_display,tag=sd_info_text,distance=..1.0,limit=1,sort=nearest] sd_crop_age = @s sd_crop_age

execute at @s as @e[type=text_display,tag=sd_info_text,distance=..1.0,limit=1,sort=nearest] run data modify entity @s text set value '[{"text":"霜瓜\\n","color":"white","bold":true},{"score":{"name":"@s","objective":"sd_crop_age"},"color":"white","bold":false},{"text":"/7","color":"gray"}]'
tag @s remove current_crop

# 3. 特效 (成熟后才执行)
execute if score @s sd_crop_age matches 7.. run particle minecraft:happy_villager ~ ~0.5 ~ 0.2 0.2 0.2 0 5

# [执行者: 作物marker实体]
# 功能: 根据sd_crop_age更新霜瓜的视觉效果

# 阶段0: age 0-1 (播种期)
execute if score @s sd_crop_age matches 0..1 at @s run data merge entity @e[type=item_display,tag=sd_crop_vis,distance=..0.5,limit=1] {item:{components:{"minecraft:custom_model_data":1170}}}

# 阶段1: age 2-3 (幼苗期)
execute if score @s sd_crop_age matches 2..3 at @s run data merge entity @e[type=item_display,tag=sd_crop_vis,distance=..0.5,limit=1] {item:{components:{"minecraft:custom_model_data":1171}}}

# 阶段2: age 4-5 (生长期)
execute if score @s sd_crop_age matches 4..5 at @s run data merge entity @e[type=item_display,tag=sd_crop_vis,distance=..0.5,limit=1] {item:{components:{"minecraft:custom_model_data":1172}}}

# 阶段3: age 6-7 (成熟期)
execute if score @s sd_crop_age matches 6..7 at @s run data merge entity @e[type=item_display,tag=sd_crop_vis,distance=..0.5,limit=1] {item:{components:{"minecraft:custom_model_data":1173}}}

# 成熟特效 (age >= 7)
execute if score @s sd_crop_age matches 7.. at @s run particle happy_villager ~ ~1.5 ~ 0.3 0.3 0.3 0 3

# 更新文本显示
execute store result storage stardew:temp crop_age int 1 run scoreboard players get @s sd_crop_age
execute store result storage stardew:temp max_age int 1 run scoreboard players get @s sd_max_crop_age
data modify entity @e[type=text_display,tag=sd_crop_text,distance=..0.5,limit=1] text set value '{"text":"霜瓜\\n","extra":[{"storage":"stardew:temp","nbt":"crop_age"},{"text":"/"},{"storage":"stardew:temp","nbt":"max_age"}],"color":"yellow"}'
