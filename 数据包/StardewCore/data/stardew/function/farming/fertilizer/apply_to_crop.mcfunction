# data/stardew/functions/farming/fertilizer/apply_to_crop.mcfunction
# 将肥料效果应用到作物marker

# 从玩家获取肥料类型和等级
execute store result score @s sd_fertilizer_type run scoreboard players get @p sd_temp_fert_type
execute store result score @s sd_fertilizer_level run scoreboard players get @p sd_temp_fert_level

# 如果是生长激素,立即修改生长周期
execute if score @s sd_fertilizer_type matches 2 run function stardew:farming/fertilizer/apply_speed_gro
