# data/stardew/functions/crops/summon_entities/starfruit/summon_entities.mcfunction

# 实体展示 (Marker + Visual)
# Marker (逻辑核心)
summon marker ~0.5 ~1.375 ~0.5 {Tags:["sd_crop","crop_starfruit","init_crop"]}

# Visual (模型) - Stage 0 - 缩放[1.3f,1.6f,1.3f],上移0.375格到~1.75
summon item_display ~0.5 ~1.75 ~0.5 {Tags:["sd_crop_vis"],item_display:"fixed",transformation:{scale:[1.3f,1.6f,1.3f],translation:[0.0f,0.0f,0.0f],left_rotation:[0.0f,0.0f,0.0f,1.0f],right_rotation:[0.0f,0.0f,0.0f,1.0f]},item:{id:"minecraft:paper",Count:1b,components:{"minecraft:custom_model_data":3200}}}

# Text (信息文字 0/MAX) - 上移0.375格到~2.45
summon text_display ~0.5 ~2.45 ~0.5 {Tags:["sd_info_text","init_text"], billboard:"center", text:'{"text":"杨桃\\n0/13","color":"white"}', transformation:{scale:[0.5f,0.5f,0.5f],translation:[0.0f,0.0f,0.0f],left_rotation:[0.0f,0.0f,0.0f,1.0f],right_rotation:[0.0f,0.0f,0.0f,1.0f]}, background_color:1073741824}

# Icon (图标) - 上移0.375格到~2.95
summon item_display ~0.5 ~2.95 ~0.5 {Tags:["sd_crop_icon","icon_starfruit"],item_display:"fixed",transformation:{scale:[0.25f,0.25f,0.25f],translation:[0.0f,0.0f,0.0f],left_rotation:[0.0f,0.0f,0.0f,1.0f],right_rotation:[0.0f,0.0f,0.0f,1.0f]},item:{id:"minecraft:paper",Count:1b,components:{"minecraft:custom_model_data":3204}}}
