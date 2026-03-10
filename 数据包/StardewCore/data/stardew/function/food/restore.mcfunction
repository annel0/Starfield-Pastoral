# 宏函数：恢复生命和能量
# 参数: food_health, food_energy

# 恢复生命
$scoreboard players add @s sd_health $(food_health)
execute store result score #max_health sd_temp run scoreboard players get @s sd_max_health
execute if score @s sd_health > #max_health sd_temp run scoreboard players operation @s sd_health = #max_health sd_temp

# 恢复能量
$scoreboard players add @s sd_energy $(food_energy)
execute store result score #max_energy sd_temp run scoreboard players get @s sd_max_energy
execute if score @s sd_energy > #max_energy sd_temp run scoreboard players operation @s sd_energy = #max_energy sd_temp
