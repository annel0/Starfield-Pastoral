# data/stardew/function/menu/hover/on_hover.mcfunction
# [执行者: 被瞄准的按钮] 处理悬停效果

# 0. 检查是否是空槽位(CMD:2),如果是则不处理任何hover效果
execute store result score #ButtonCMD sd_menu_ctrl run data get entity @s item.components."minecraft:custom_model_data"
execute if score #ButtonCMD sd_menu_ctrl matches 2 run return 0

# 1. 标记为被瞄准
scoreboard players set @s sd_menu_targeted 1

# 2. 如果是首次被瞄准(上一tick未被瞄准),触发高光效果
execute if score @s sd_menu_targeted_prev matches 0 run function stardew:menu/hover/highlight_on

# 3. 持续更新标题文本位置到当前悬停按钮上方
execute store result score #Temp sd_menu_ctrl run scoreboard players get @s sd_menu_entity_num
execute positioned ~ ~0.55 ~ as @e[tag=sd_menu_text,tag=sd_text_title] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run tp @s ~ ~ ~
# 4. 更新描述文本位置到标题下方（稍微低一点）- 下移至图标下方
execute as @e[tag=sd_menu_text,tag=sd_text_desc] if score @s sd_menu_entity_num = #Temp sd_menu_ctrl run tp @s ^ ^-0.6 ^-0.5
