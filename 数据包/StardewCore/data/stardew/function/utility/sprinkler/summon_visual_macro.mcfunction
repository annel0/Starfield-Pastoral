# data/stardew/function/utility/sprinkler/summon_visual_macro.mcfunction
# 宏召唤洒水器视觉实体
# 参数: $(cmd) - visual CMD

$summon minecraft:item_display ~ ~ ~ {Tags:["sd_utility","sd_sprinkler","sd_sprinkler_visual","init_utility"],transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[1.2f,1.2f,1.2f]},item:{id:"minecraft:oak_log",count:1,components:{"minecraft:custom_model_data":$(cmd)}}}
