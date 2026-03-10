# data/stardew/functions/crops/summon_entities/pumpkin/summon_entities.mcfunction

# 实体展示 (Marker + Visual)
# Marker (逻辑核心)
summon marker ~0.5 ~1.375 ~0.5 {Tags:["sd_crop","crop_pumpkin","init_crop"]}

# Visual (模型) - Stage 0 - 缩放1.5倍,Y位置降低到~1.6875
summon item_display ~0.5 ~1.6875 ~0.5 {Tags:["sd_crop_vis"],item_display:"fixed",transformation:{scale:[1.5f,1.5f,1.5f],translation:[0.0f,0.0f,0.0f],left_rotation:[0.0f,0.0f,0.0f,1.0f],right_rotation:[0.0f,0.0f,0.0f,1.0f]},item:{id:"minecraft:paper",Count:1b,components:{"minecraft:custom_model_data":4200}}}

# Text (信息文字 0/MAX)
summon text_display ~0.5 ~2.075 ~0.5 {Tags:["sd_info_text","init_text"], billboard:"center", text:'{"text":"南瓜\\n0/13","color":"white"}', transformation:{scale:[0.5f,0.5f,0.5f],translation:[0.0f,0.0f,0.0f],left_rotation:[0.0f,0.0f,0.0f,1.0f],right_rotation:[0.0f,0.0f,0.0f,1.0f]}, background_color:1073741824}

# Icon (图标) - 使用 Item CMD Base (item CMD base 是 visual_base_cmd+4)
summon item_display ~0.5 ~2.575 ~0.5 {Tags:["sd_crop_icon","icon_pumpkin"],item_display:"fixed",transformation:{scale:[0.25f,0.25f,0.25f],translation:[0.0f,0.0f,0.0f],left_rotation:[0.0f,0.0f,0.0f,1.0f],right_rotation:[0.0f,0.0f,0.0f,1.0f]},item:{id:"minecraft:paper",Count:1b,components:{"minecraft:custom_model_data":4204}}}
