# 文字更新
execute at @s run scoreboard players operation @e[type=text_display,tag=sd_info_text,distance=..2.0,limit=1,sort=nearest] sd_crop_age = @s sd_crop_age
execute at @s as @e[type=text_display,tag=sd_info_text,distance=..2.0,limit=1,sort=nearest] run data modify entity @s text set value '[{"text":"枫树\\n","color":"gold"},{"score":{"name":"@s","objective":"sd_crop_age"},"color":"white"},{"text":"/28","color":"gray"}]'

# Stage 1 (0-13)
execute if score @s sd_crop_age matches 0..13 at @s run data merge entity @e[type=item_display,tag=sd_tree_vis,distance=..2.0,limit=1] {item:{components:{"minecraft:custom_model_data":811}},transformation:{scale:[1.0f,1.0f,1.0f],translation:[0.0f,0.0f,0.0f]}}

# Stage 2 (14-27)
execute if score @s sd_crop_age matches 14..27 at @s run data merge entity @e[type=item_display,tag=sd_tree_vis,distance=..2.0,limit=1] {item:{components:{"minecraft:custom_model_data":812}},transformation:{scale:[1.5f,1.5f,1.5f],translation:[0.0f,0.25f,0.0f]}}

# 成熟 -> 替换 (Age 28)
execute if score @s sd_crop_age matches 28 at @s run function stardew:tree/spawn_maple
execute if score @s sd_crop_age matches 28 at @s run kill @e[type=text_display,tag=sd_info_text,distance=..2.0]
execute if score @s sd_crop_age matches 28 at @s run kill @e[type=item_display,tag=sd_tree_vis,distance=..2.0,nbt={item:{components:{"minecraft:custom_model_data":811}}}]
execute if score @s sd_crop_age matches 28 at @s run kill @e[type=item_display,tag=sd_tree_vis,distance=..2.0,nbt={item:{components:{"minecraft:custom_model_data":812}}}]
execute if score @s sd_crop_age matches 28 at @s run kill @s