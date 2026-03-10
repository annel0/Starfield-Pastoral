# data/stardew/functions/tools/debug_time_wand.mcfunction
# [执行者: 玩家]

# 1. 光阴似箭！(+60分钟)
scoreboard players add Global sd_time 60
playsound minecraft:block.bell.use player @s ~ ~ ~ 1.0 2.0
particle minecraft:sculk_charge_pop ~ ~1.5 ~ 0.2 0.2 0.2 0.1 10

# 2. 强制过夜检测
# 防止直接跳过了 02:00 (1560)
execute if score Global sd_time matches 1560.. run function stardew:time/new_day

# 3. 反馈
tellraw @s {"text":"[Debug] 时间飞逝... +1小时","color":"gold","bold":true}