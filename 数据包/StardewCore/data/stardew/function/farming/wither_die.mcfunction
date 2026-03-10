# data/stardew/functions/farming/wither_die.mcfunction

# 1. 视觉特效
particle minecraft:smoke ~ ~0.5 ~ 0.2 0.2 0.2 0.05 10
playsound minecraft:block.grass.break block @a ~ ~ ~ 1 0.5

# 2. 改变模型 - 修改 item_display 实体 (sd_crop_vis)
execute at @s as @e[type=item_display,tag=sd_crop_vis,distance=..0.5,limit=1] run data modify entity @s item.components."minecraft:custom_model_data" set value 9999

# 3. 标签管理
tag @s remove crop_wheat
tag @s remove crop_tomato
tag @s remove crop_strawberry
tag @s remove crop_garlic
tag @s add crop_dead

# 4. 更新附属显示
# 文字 -> "已枯萎"
execute at @s as @e[type=text_display,tag=sd_info_text,distance=..0.8,limit=1] run data modify entity @s text set value '{"text":"已枯萎","color":"gray"}'
# [新增] 图标 -> 直接删除
execute at @s run kill @e[type=item_display,tag=sd_crop_icon,distance=..1.5]