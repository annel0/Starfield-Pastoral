# data/stardew/functions/farming/fertilizer/summon_visual.mcfunction
# 召唤肥料视觉实体

# 根据肥料类型和等级召唤对应的item_display
# 品质肥料
execute if score @p sd_temp_fert_type matches 1 if score @p sd_temp_fert_level matches 1 run summon item_display ~ ~0.4422 ~ {Tags:["sd_fertilizer_visual"],item:{id:"minecraft:carrot_on_a_stick",count:1,components:{"minecraft:custom_model_data":4011}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,1f,1f]},brightness:{sky:15,block:15}}

execute if score @p sd_temp_fert_type matches 1 if score @p sd_temp_fert_level matches 2 run summon item_display ~ ~0.4422 ~ {Tags:["sd_fertilizer_visual"],item:{id:"minecraft:carrot_on_a_stick",count:1,components:{"minecraft:custom_model_data":4012}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,1f,1f]},brightness:{sky:15,block:15}}

execute if score @p sd_temp_fert_type matches 1 if score @p sd_temp_fert_level matches 3 run summon item_display ~ ~0.4422 ~ {Tags:["sd_fertilizer_visual"],item:{id:"minecraft:carrot_on_a_stick",count:1,components:{"minecraft:custom_model_data":4013}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,1f,1f]},brightness:{sky:15,block:15}}

# 生长激素
execute if score @p sd_temp_fert_type matches 2 if score @p sd_temp_fert_level matches 1 run summon item_display ~ ~0.4422 ~ {Tags:["sd_fertilizer_visual"],item:{id:"minecraft:carrot_on_a_stick",count:1,components:{"minecraft:custom_model_data":4014}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,1f,1f]},brightness:{sky:15,block:15}}

execute if score @p sd_temp_fert_type matches 2 if score @p sd_temp_fert_level matches 2 run summon item_display ~ ~0.4422 ~ {Tags:["sd_fertilizer_visual"],item:{id:"minecraft:carrot_on_a_stick",count:1,components:{"minecraft:custom_model_data":4015}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,1f,1f]},brightness:{sky:15,block:15}}

execute if score @p sd_temp_fert_type matches 2 if score @p sd_temp_fert_level matches 3 run summon item_display ~ ~0.4422 ~ {Tags:["sd_fertilizer_visual"],item:{id:"minecraft:carrot_on_a_stick",count:1,components:{"minecraft:custom_model_data":4016}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,1f,1f]},brightness:{sky:15,block:15}}

# 保湿土壤
execute if score @p sd_temp_fert_type matches 3 if score @p sd_temp_fert_level matches 1 run summon item_display ~ ~0.4422 ~ {Tags:["sd_fertilizer_visual"],item:{id:"minecraft:carrot_on_a_stick",count:1,components:{"minecraft:custom_model_data":4017}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,1f,1f]},brightness:{sky:15,block:15}}

execute if score @p sd_temp_fert_type matches 3 if score @p sd_temp_fert_level matches 2 run summon item_display ~ ~0.4422 ~ {Tags:["sd_fertilizer_visual"],item:{id:"minecraft:carrot_on_a_stick",count:1,components:{"minecraft:custom_model_data":4018}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,1f,1f]},brightness:{sky:15,block:15}}

execute if score @p sd_temp_fert_type matches 3 if score @p sd_temp_fert_level matches 3 run summon item_display ~ ~0.4422 ~ {Tags:["sd_fertilizer_visual"],item:{id:"minecraft:carrot_on_a_stick",count:1,components:{"minecraft:custom_model_data":4019}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1f,1f,1f]},brightness:{sky:15,block:15}}
