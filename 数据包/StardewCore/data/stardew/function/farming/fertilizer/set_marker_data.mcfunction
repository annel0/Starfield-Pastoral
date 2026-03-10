# data/stardew/functions/farming/fertilizer/set_marker_data.mcfunction
# 给肥料marker设置数据

# 从玩家获取肥料类型和等级
execute store result score @s sd_fertilizer_type run scoreboard players get @p sd_temp_fert_type
execute store result score @s sd_fertilizer_level run scoreboard players get @p sd_temp_fert_level

# 移除新建标记
tag @s remove sd_new_fertilizer
