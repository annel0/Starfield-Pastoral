# data/stardew/function/utility/furnace/update_time_text.mcfunction
# 更新熔炉的时间显示文本
# 执行者: text_display 实体
# 上下文: 附近有一个熔炉 interaction 实体，其 sd_temp 存储了剩余分钟数

# 1. 找到附近的熔炉并获取剩余时间（通过计分板传递）
scoreboard players operation @s sd_temp = @e[type=interaction,tag=sd_furnace_interaction,distance=..2,limit=1,sort=nearest] sd_temp

# 2. 根据剩余时间显示不同的文本
execute if score @s sd_temp matches 30.. run data merge entity @s {text:'{"text":"30分钟","color":"yellow"}'}
execute if score @s sd_temp matches 20..29 run data merge entity @s {text:'{"text":"20分钟","color":"yellow"}'}
execute if score @s sd_temp matches 10..19 run data merge entity @s {text:'{"text":"10分钟","color":"yellow"}'}
execute if score @s sd_temp matches 5..9 run data merge entity @s {text:'{"text":"5分钟","color":"gold"}'}
execute if score @s sd_temp matches 1..4 run data merge entity @s {text:'{"text":"即将完成...","color":"gold"}'}
execute if score @s sd_temp matches ..0 run data merge entity @s {text:'{"text":"即将完成...","color":"gold"}'}
