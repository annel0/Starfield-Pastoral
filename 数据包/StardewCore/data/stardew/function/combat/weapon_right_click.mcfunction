# 武器右键检测

# 检查是否持有武器
execute unless data entity @s SelectedItem.components."minecraft:custom_data".stardew_weapon run return run scoreboard players reset @s sd_use_carrot

# 检查玩家是否潜行 - 如果潜行则触发特殊技能（weapon_special_2）
execute if predicate stardew:is_sneaking run function stardew:combat/weapon_special_skill

# 如果没有潜行，触发主技能（weapon_special）
execute unless predicate stardew:is_sneaking run function stardew:combat/weapon_main_skill

# 重置计分板
scoreboard players reset @s sd_use_carrot
