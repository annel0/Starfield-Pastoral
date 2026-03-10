# data/stardew/functions/fishing/check_struggle_trigger.mcfunction
# [执行者: 玩家]
# 随机判定是否进入挣扎期

# 生成随机数 1-100
execute store result score @s sd_temp run random value 1..100

# 根据难度设置触发概率
# 低难度: 15% 每tick (平均要等 6-7 tick)
# 高难度: 50% 每tick (平均要等 2 tick,更快更随机)
scoreboard players set @s sd_const 15
execute if score @s sd_final_difficulty matches 2..3 run scoreboard players set @s sd_const 18
execute if score @s sd_final_difficulty matches 4..6 run scoreboard players set @s sd_const 22
execute if score @s sd_final_difficulty matches 7..9 run scoreboard players set @s sd_const 28
execute if score @s sd_final_difficulty matches 10..12 run scoreboard players set @s sd_const 35
execute if score @s sd_final_difficulty matches 13.. run scoreboard players set @s sd_const 50

# 如果随机数小于等于概率阈值,触发挣扎期
execute if score @s sd_temp <= @s sd_const run function stardew:fishing/start_pull_phase
