# data/stardew/functions/fishing/init_bobber_age.mcfunction
# 直接对所有未初始化的鱼钩进行初始化
# 无需 schedule，直接在 main.mcfunction 或 fight_check 里每 tick 调用一次即可

# 给所有还没有分数的鱼钩设置为 0
execute as @e[type=fishing_bobber] unless score @s sd_age matches 0.. run scoreboard players set @s sd_age 0