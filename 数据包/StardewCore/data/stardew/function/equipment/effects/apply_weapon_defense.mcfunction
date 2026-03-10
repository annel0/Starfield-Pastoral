# 应用主手武器的防御值
# @s = 玩家
# 从 SelectedItem 的 weapon_defense 读取防御值并累加到 sd_defense

# 从武器读取防御值 (weapon_defense 字段)
execute store result score #weapon_defense sd_temp run data get entity @s SelectedItem.components."minecraft:custom_data".weapon_defense 1

# 累加到玩家的总防御值
execute if score #weapon_defense sd_temp matches 1.. run scoreboard players operation @s sd_defense += #weapon_defense sd_temp
