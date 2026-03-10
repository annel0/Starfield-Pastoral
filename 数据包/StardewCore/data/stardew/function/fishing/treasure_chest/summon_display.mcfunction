# 召唤展示用的item_display
# macro参数: display_cmd
$execute anchored eyes positioned ^ ^ ^ rotated ~ 0 run summon item_display ^ ^ ^ {Tags:["sd_treasure_item_display"],billboard:"fixed",item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":$(display_cmd)}},transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[2.2f,-0.2f,-2.2f],scale:[0f,0f,0f]}}
