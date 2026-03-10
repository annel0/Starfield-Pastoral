# data/stardew/functions/farming/fertilizer/copy_fertilizer_to_crop_inline.mcfunction
# 从附近的fertilizer_marker复制数据到当前作物
# 执行者: sd_crop
# 注意: 保持@s为crop，不改变执行者

# 存储fertilizer数据到临时分数
execute store result score #temp_fert_type sd_temp run scoreboard players get @e[type=marker,tag=sd_fertilizer_marker,distance=..0.1,limit=1] sd_fertilizer_type
execute store result score #temp_fert_level sd_temp run scoreboard players get @e[type=marker,tag=sd_fertilizer_marker,distance=..0.1,limit=1] sd_fertilizer_level

# 复制到当前作物
scoreboard players operation @s sd_fertilizer_type = #temp_fert_type sd_temp
scoreboard players operation @s sd_fertilizer_level = #temp_fert_level sd_temp
