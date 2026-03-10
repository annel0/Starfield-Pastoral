# 冷却时间递减
# 在 main.mcfunction 中每 tick 调用

execute as @a[scores={sd_food_cooldown=1..}] run scoreboard players remove @s sd_food_cooldown 1
