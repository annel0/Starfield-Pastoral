# 0. 空间检测：如果周围 1 格内已经有树，就不允许种植
execute align xyz positioned ~0.5 ~1.5 ~0.5 if entity @e[tag=sd_tree,distance=..3.0] run tellraw @s {"text":"这里太挤了，树苗需要更多空间！","color":"red"}
execute align xyz positioned ~0.5 ~1.5 ~0.5 if entity @e[tag=sd_tree,distance=..3.0] run return 1

# 1. 防堆叠
execute align xyz positioned ~0.5 ~1 ~0.5 if entity @e[tag=sd_tree,distance=..0.5] run return 1

# 2. 种植 (注意：树苗阶段也是单格，只有成熟才会变大，或者你希望树苗就占2x2？这里先按单格处理)
execute align xyz positioned ~0.5 ~1.5 ~0.5 run summon interaction ~ ~ ~ {Tags:["sd_tree","tree_mahogany","init_sapling"],width:0.5f,height:0.8f,response:1b}
execute align xyz positioned ~0.5 ~1.5 ~0.5 run summon item_display ~ ~ ~ {Tags:["sd_tree_vis"],item_display:"fixed",transformation:{scale:[1.0f,1.0f,1.0f],translation:[0.0f,0.0f,0.0f],left_rotation:[0.0f,0.0f,0.0f,1.0f],right_rotation:[0.0f,0.0f,0.0f,1.0f]},item:{id:"minecraft:paper",Count:1b,components:{"minecraft:custom_model_data":831}}}
execute align xyz positioned ~0.5 ~2.5 ~0.5 run summon text_display ~ ~ ~ {Tags:["sd_info_text","init_text"], billboard:"center", text:'{"text":"桃花心木\\n0/28","color":"red"}', transformation:{scale:[0.5f,0.5f,0.5f],translation:[0.0f,0.0f,0.0f],left_rotation:[0.0f,0.0f,0.0f,1.0f],right_rotation:[0.0f,0.0f,0.0f,1.0f]}, background_color:1073741824}

# 3. 初始化
execute as @e[tag=init_sapling] run scoreboard players set @s sd_tree_hp 1
execute as @e[tag=init_sapling] run scoreboard players set @s sd_tree_type 4
execute as @e[tag=init_sapling] run scoreboard players set @s sd_crop_age 0

# 4. 消耗
execute if entity @e[tag=init_sapling,distance=..3] if entity @s[gamemode=!creative] run clear @s carrot_on_a_stick[minecraft:custom_model_data=2504] 1
tag @e[tag=init_sapling] remove init_sapling
playsound minecraft:block.grass.place player @s ~ ~ ~ 1 1