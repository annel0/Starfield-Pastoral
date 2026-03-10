# 草生长和扩散系统
# 每天调用，除冬季外都有50%概率向相邻格子扩散

# 只在非冬季执行
execute if score Global sd_season matches 4 run return 0

# 为每个草实体执行扩散检查
execute as @e[type=interaction,tag=sd_grass] at @s run function stardew:grass/try_spread