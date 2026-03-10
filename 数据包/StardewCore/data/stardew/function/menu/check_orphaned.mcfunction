# data/stardew/function/menu/check_orphaned.mcfunction
# [执行者: 菜单实体] 检查是否为孤立实体

# 存储此实体的编号
execute store result score #CheckNum sd_menu_ctrl run scoreboard players get @s sd_menu_entity_num

# 检查是否有玩家拥有这个序列号
execute store result score #HasOwner sd_menu_ctrl if entity @a[scores={sd_menu_sequence=0..}] if score @a[scores={sd_menu_sequence=0..},limit=1] sd_menu_sequence = #CheckNum sd_menu_ctrl

# 如果没有对应的玩家，清理这个实体
execute if score #HasOwner sd_menu_ctrl matches 0 run kill @s
