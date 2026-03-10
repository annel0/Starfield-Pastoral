# data/stardew/functions/farming/fertilizer/apply_to_existing_crop.mcfunction
# 给已存在的作物施加肥料
# 执行者: sd_crop marker

# 检查作物是否已经有肥料
execute if score @s sd_fertilizer_type matches 1.. run tellraw @p {"text":"这块作物已经施过肥了!","color":"yellow"}
execute if score @s sd_fertilizer_type matches 1.. run return 0

# 从玩家获取肥料类型和等级
execute store result score @s sd_fertilizer_type run scoreboard players get @p sd_temp_fert_type
execute store result score @s sd_fertilizer_level run scoreboard players get @p sd_temp_fert_level

# 如果是生长激素,应用速度加成
execute if score @s sd_fertilizer_type matches 2 run function stardew:farming/fertilizer/apply_speed_gro

# 创建一个肥料marker用于持久化存储
summon marker ~ ~ ~ {Tags:["sd_fertilizer_marker","sd_new_fertilizer"]}
execute as @e[type=marker,tag=sd_new_fertilizer,distance=..0.1,limit=1] run function stardew:farming/fertilizer/set_marker_data
