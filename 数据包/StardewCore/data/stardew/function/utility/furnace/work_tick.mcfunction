# data/stardew/function/utility/furnace/work_tick.mcfunction
# 熔炉工作tick处理（改为基于 Global sd_time 的增量更新）
# 执行者: 正在工作的熔炉 interaction (@s)

# 0. 计算从上次更新到当前的时间差（分钟），支持跨天
# delta = Global sd_time - sd_furnace_last_time  (若负，则加上一天长度)
scoreboard players operation #delta sd_temp = Global sd_time

# 如果当前时间 >= last_time： delta = Global - last_time
execute if score Global sd_time >= @s sd_furnace_last_time run scoreboard players operation #delta sd_temp -= @s sd_furnace_last_time

# 否则跨天：delta = Global + day_length - last_time  （day_length = 1560 - 360 = 1200）
execute if score Global sd_time < @s sd_furnace_last_time run scoreboard players set #daylen sd_temp 1200
execute if score Global sd_time < @s sd_furnace_last_time run scoreboard players operation #delta sd_temp += #daylen sd_temp
execute if score Global sd_time < @s sd_furnace_last_time run scoreboard players operation #delta sd_temp -= @s sd_furnace_last_time

# 1. 如果有时间差（包括由时间法杖或菜单跳过），将差值累加到已工作时间
execute if score #delta sd_temp matches 1.. run scoreboard players operation @s sd_furnace_timer += #delta sd_temp

# 1.5 如果存在 new_day 给出的通用奖励（sd_utility_bonus），把奖励也加入已工作时间并清零
execute if score @s sd_utility_bonus matches 1.. run scoreboard players operation @s sd_furnace_timer += @s sd_utility_bonus
execute if score @s sd_utility_bonus matches 1.. run scoreboard players set @s sd_utility_bonus 0

# 2. 更新上次更新时间为当前 Global sd_time（无论是否有差值，都同步）
execute run scoreboard players operation @s sd_furnace_last_time = Global sd_time

# 3. 检查是否完成（已工作时间 >= 需要的总时间）
execute if score @s sd_furnace_timer >= @s sd_furnace_max_time run function stardew:utility/furnace/complete_smelt
execute if score @s sd_furnace_timer >= @s sd_furnace_max_time run return 1

# 4. 计算剩余分钟数
scoreboard players operation @s sd_temp = @s sd_furnace_max_time
scoreboard players operation @s sd_temp -= @s sd_furnace_timer

# 5. 找到对应的视觉实体（通过距离），获取其ID
execute as @e[type=item_display,tag=sd_furnace_visual,distance=..1,limit=1] run scoreboard players operation #current_id sd_furnace_id = @s sd_furnace_id

# 6. 更新有相同ID的文本显示
execute store result storage stardew:temp time int 1 run scoreboard players get @s sd_temp
execute if score @s sd_temp matches 120.. as @e[type=text_display,tag=sd_furnace_time] if score @s sd_furnace_id = #current_id sd_furnace_id run data merge entity @s {text:'[{"text":"剩余 ","color":"yellow"},{"nbt":"time","storage":"stardew:temp","color":"gold"},{"text":" 分钟","color":"yellow"}]',transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.8f,0.8f,0.8f]}}
execute if score @s sd_temp matches 60..119 as @e[type=text_display,tag=sd_furnace_time] if score @s sd_furnace_id = #current_id sd_furnace_id run data merge entity @s {text:'[{"text":"剩余 ","color":"yellow"},{"nbt":"time","storage":"stardew:temp","color":"gold"},{"text":" 分钟","color":"yellow"}]',transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.8f,0.8f,0.8f]}}
execute if score @s sd_temp matches 30..59 as @e[type=text_display,tag=sd_furnace_time] if score @s sd_furnace_id = #current_id sd_furnace_id run data merge entity @s {text:'[{"text":"剩余 ","color":"yellow"},{"nbt":"time","storage":"stardew:temp","color":"gold"},{"text":" 分钟","color":"yellow"}]',transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.8f,0.8f,0.8f]}}
execute if score @s sd_temp matches 10..29 as @e[type=text_display,tag=sd_furnace_time] if score @s sd_furnace_id = #current_id sd_furnace_id run data merge entity @s {text:'[{"text":"剩余 ","color":"yellow"},{"nbt":"time","storage":"stardew:temp","color":"gold"},{"text":" 分钟","color":"gold"}]',transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.8f,0.8f,0.8f]}}
execute if score @s sd_temp matches 1..9 as @e[type=text_display,tag=sd_furnace_time] if score @s sd_furnace_id = #current_id sd_furnace_id run data merge entity @s {text:'[{"text":"剩余 ","color":"gold"},{"nbt":"time","storage":"stardew:temp","color":"red","bold":true},{"text":" 分钟","color":"gold"}]',transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.8f,0.8f,0.8f]}}
