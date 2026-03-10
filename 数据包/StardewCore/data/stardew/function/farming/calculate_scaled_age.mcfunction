# data/stardew/functions/farming/calculate_scaled_age.mcfunction
# 计算缩放后的作物年龄,用于视觉系统
# 如果使用了速成肥(sd_original_max_age > 0),则按比例缩放当前年龄
# 公式: scaled_age = sd_crop_age × sd_original_max_age / sd_max_crop_age
# 例如: 花椰菜当前10天,最大10天,原始12天 → 10 × 12 / 10 = 12

# 检查是否使用了速成肥
execute unless score @s sd_original_max_age matches 1.. run scoreboard players operation @s sd_original_max_age = @s sd_max_crop_age

# 计算缩放年龄
scoreboard players operation #scaled_age sd_temp = @s sd_crop_age
scoreboard players operation #scaled_age sd_temp *= @s sd_original_max_age
scoreboard players operation #scaled_age sd_temp /= @s sd_max_crop_age

# 将缩放年龄临时存储到marker的sd_rng中(借用这个变量)
scoreboard players operation @s sd_rng = #scaled_age sd_temp
