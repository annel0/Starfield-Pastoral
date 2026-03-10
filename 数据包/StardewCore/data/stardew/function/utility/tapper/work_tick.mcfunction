# data/stardew/function/utility/tapper/work_tick.mcfunction
# 提取器工作tick处理（基于 Global sd_time 的增量更新）
# 执行者: 正在工作的树 (@s, 带有提取器的树interaction实体)

# 0. 计算从上次更新到当前的时间差（分钟），支持跨天
scoreboard players operation #delta sd_temp = Global sd_time

# 如果当前时间 >= last_time： delta = Global - last_time
execute if score Global sd_time >= @s sd_tapper_last_time run scoreboard players operation #delta sd_temp -= @s sd_tapper_last_time

# 否则跨天：delta = Global + day_length - last_time  （day_length = 1560 - 360 = 1200）
execute if score Global sd_time < @s sd_tapper_last_time run scoreboard players set #daylen sd_temp 1200
execute if score Global sd_time < @s sd_tapper_last_time run scoreboard players operation #delta sd_temp += #daylen sd_temp
execute if score Global sd_time < @s sd_tapper_last_time run scoreboard players operation #delta sd_temp -= @s sd_tapper_last_time

# 1. 如果有时间差（包括由时间法杖或菜单跳过），将差值累加到已工作时间
execute if score #delta sd_temp matches 1.. run scoreboard players operation @s sd_tapper_timer += #delta sd_temp

# 1.5 如果存在 new_day 给出的通用奖励（sd_utility_bonus），把奖励也加入已工作时间并清零
execute if score @s sd_utility_bonus matches 1.. run scoreboard players operation @s sd_tapper_timer += @s sd_utility_bonus
execute if score @s sd_utility_bonus matches 1.. run scoreboard players set @s sd_utility_bonus 0

# 2. 更新上次更新时间为当前 Global sd_time（无论是否有差值，都同步）
execute run scoreboard players operation @s sd_tapper_last_time = Global sd_time

# 3. 检查是否完成（已工作时间 >= 需要的总时间）
execute if score @s sd_tapper_timer >= @s sd_tapper_max_time run function stardew:utility/tapper/complete_tapping
execute if score @s sd_tapper_timer >= @s sd_tapper_max_time run return 1

# 4. 计算剩余时间（分钟）
scoreboard players operation @s sd_temp = @s sd_tapper_max_time
scoreboard players operation @s sd_temp -= @s sd_tapper_timer

# 5. 将剩余分钟转换为天数（1天 = 1440分钟）
scoreboard players operation #days sd_temp = @s sd_temp
scoreboard players set #1440 sd_const 1440
scoreboard players operation #days sd_temp /= #1440 sd_const

# 6. 计算剩余小时（去掉整天后的分钟 / 60）
scoreboard players operation #hours sd_temp = @s sd_temp
scoreboard players operation #temp_days sd_temp = #days sd_temp
scoreboard players operation #temp_days sd_temp *= #1440 sd_const
scoreboard players operation #hours sd_temp -= #temp_days sd_temp
scoreboard players set #60 sd_const 60
scoreboard players operation #hours sd_temp /= #60 sd_const

# 7. 获取对应的ID
execute as @e[type=item_display,tag=sd_tapper_visual,distance=..2,limit=1] run scoreboard players operation #current_id sd_tapper_id = @s sd_tapper_id

# 8. 更新有相同ID的文本显示 (优化显示格式)
# 超过1天: 显示 "X天Y小时"
execute if score #days sd_temp matches 1.. store result storage stardew:temp days int 1 run scoreboard players get #days sd_temp
execute if score #days sd_temp matches 1.. store result storage stardew:temp hours int 1 run scoreboard players get #hours sd_temp
execute if score #days sd_temp matches 1.. as @e[type=text_display,tag=sd_tapper_time] if score @s sd_tapper_id = #current_id sd_tapper_id run data merge entity @s {text:'[{"text":"剩余 ","color":"yellow"},{"nbt":"days","storage":"stardew:temp","color":"gold"},{"text":"天","color":"yellow"},{"nbt":"hours","storage":"stardew:temp","color":"gold"},{"text":"小时","color":"yellow"}]',transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.7f,0.7f,0.7f]}}

# 小于1天但大于1小时: 显示 "X小时"
execute if score #days sd_temp matches 0 if score #hours sd_temp matches 1.. store result storage stardew:temp hours int 1 run scoreboard players get #hours sd_temp
execute if score #days sd_temp matches 0 if score #hours sd_temp matches 1.. as @e[type=text_display,tag=sd_tapper_time] if score @s sd_tapper_id = #current_id sd_tapper_id run data merge entity @s {text:'[{"text":"剩余 ","color":"yellow"},{"nbt":"hours","storage":"stardew:temp","color":"gold"},{"text":"小时","color":"yellow"}]',transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.7f,0.7f,0.7f]}}

# 小于1小时: 显示 "X分钟"
execute if score #days sd_temp matches 0 if score #hours sd_temp matches 0 store result storage stardew:temp time int 1 run scoreboard players get @s sd_temp
execute if score #days sd_temp matches 0 if score #hours sd_temp matches 0 as @e[type=text_display,tag=sd_tapper_time] if score @s sd_tapper_id = #current_id sd_tapper_id run data merge entity @s {text:'[{"text":"剩余 ","color":"gold"},{"nbt":"time","storage":"stardew:temp","color":"red","bold":true},{"text":"分钟","color":"gold"}]',transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.7f,0.7f,0.7f]}}
