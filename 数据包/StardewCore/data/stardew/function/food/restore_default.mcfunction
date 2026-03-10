# 默认恢复函数
# 当无法读取物品数据时使用默认值

# 恢复生命
scoreboard players add @s sd_health 10
execute store result score #max_health sd_temp run scoreboard players get @s sd_max_health
execute if score @s sd_health > #max_health sd_temp run scoreboard players operation @s sd_health = #max_health sd_temp

# 恢复能量
scoreboard players add @s sd_energy 15
execute store result score #max_energy sd_temp run scoreboard players get @s sd_max_energy
execute if score @s sd_energy > #max_energy sd_temp run scoreboard players operation @s sd_energy = #max_energy sd_temp
