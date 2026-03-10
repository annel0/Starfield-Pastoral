# data/stardew/functions/farming/fertilizer/transfer_data_to_crop.mcfunction
# 将肥料marker的数据传递给新作物marker
# 执行者: sd_fertilizer_marker
# 目标: sd_new_crop (在同一位置)

# 将肥料数据传递给同位置的新作物
execute as @e[type=marker,tag=sd_crop,tag=sd_new_crop,distance=..0.1,limit=1] run scoreboard players operation @s sd_fertilizer_type = @e[type=marker,tag=sd_fertilizer_marker,distance=..0.1,limit=1,sort=nearest] sd_fertilizer_type

execute as @e[type=marker,tag=sd_crop,tag=sd_new_crop,distance=..0.1,limit=1] run scoreboard players operation @s sd_fertilizer_level = @e[type=marker,tag=sd_fertilizer_marker,distance=..0.1,limit=1,sort=nearest] sd_fertilizer_level

# 如果是生长激素,应用速度加成
execute as @e[type=marker,tag=sd_crop,tag=sd_new_crop,distance=..0.1,limit=1] if score @s sd_fertilizer_type matches 2 run function stardew:farming/fertilizer/apply_speed_gro
