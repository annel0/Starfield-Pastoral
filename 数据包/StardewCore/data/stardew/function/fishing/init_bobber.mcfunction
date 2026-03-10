# data/stardew/functions/fishing/init_bobber.mcfunction
# 每 tick 运行，负责发现新生成的鱼钩并检测其位置

# 1. 初始化新鱼钩的年龄 (sd_age) 和 安全锁 (sd_hook_safe)
execute as @e[type=fishing_bobber] unless score @s sd_age matches 0.. run scoreboard players set @s sd_age 0
execute as @e[type=fishing_bobber] unless score @s sd_hook_safe matches 0..1 run scoreboard players set @s sd_hook_safe 0

# 2. 年龄增长
execute as @e[type=fishing_bobber] run scoreboard players add @s sd_age 1

# 3. [核心逻辑] 检查并设置安全锁
# 只有在安全锁未打开 (sd_hook_safe = 0) 且年龄 > 5 时，才进行陆地检测。
execute as @e[type=fishing_bobber,scores={sd_age=6..,sd_hook_safe=0}] at @s run function stardew:fishing/check_for_land