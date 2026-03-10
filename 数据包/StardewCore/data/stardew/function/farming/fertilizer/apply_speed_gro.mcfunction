# data/stardew/functions/farming/fertilizer/apply_speed_gro.mcfunction
# 应用生长激素效果,直接增加初始生长进度
# 执行者: sd_crop marker

# 根据等级计算需要增加的生长进度
# Level 1 (生长激素): +15% (保证至少1天)
# Level 2 (高级生长激素): +30% (保证至少2天)
# Level 3 (顶级生长激素): +50% (保证至少3天)

# 临时变量: 计算增加的天数
scoreboard players operation #boost_days sd_temp = @s sd_max_crop_age

# 根据等级计算加成（使用更大的百分比确保效果明显）
execute if score @s sd_fertilizer_level matches 1 run scoreboard players operation #boost_days sd_temp *= #15 sd_const
execute if score @s sd_fertilizer_level matches 1 run scoreboard players operation #boost_days sd_temp /= #100 sd_const

execute if score @s sd_fertilizer_level matches 2 run scoreboard players operation #boost_days sd_temp *= #30 sd_const
execute if score @s sd_fertilizer_level matches 2 run scoreboard players operation #boost_days sd_temp /= #100 sd_const

execute if score @s sd_fertilizer_level matches 3 run scoreboard players operation #boost_days sd_temp *= #50 sd_const
execute if score @s sd_fertilizer_level matches 3 run scoreboard players operation #boost_days sd_temp /= #100 sd_const

# 保底：根据等级设置最小加成天数
execute if score @s sd_fertilizer_level matches 1 if score #boost_days sd_temp matches ..0 run scoreboard players set #boost_days sd_temp 1
execute if score @s sd_fertilizer_level matches 2 if score #boost_days sd_temp matches ..1 run scoreboard players set #boost_days sd_temp 2
execute if score @s sd_fertilizer_level matches 3 if score #boost_days sd_temp matches ..2 run scoreboard players set #boost_days sd_temp 3

# 直接增加作物年龄
scoreboard players operation @s sd_crop_age += #boost_days sd_temp

# 确保不超过最大生长天数-1 (避免直接成熟)
scoreboard players operation #max_minus_1 sd_temp = @s sd_max_crop_age
scoreboard players remove #max_minus_1 sd_temp 1
execute if score @s sd_crop_age > #max_minus_1 sd_temp run scoreboard players operation @s sd_crop_age = #max_minus_1 sd_temp

# 更新视觉
function stardew:farming/visual_update_router
