# stardew:weeds/try_spawn_area_weed.mcfunction
# 在当前位置尝试生成杂草（用于区域生成）
# 40%概率 + 基本检测条件

# 40%概率检测
execute unless predicate stardew:random_40 run return 0

# 使用与原版相同的检测逻辑：只检查上方空气和是否已有杂草
execute unless block ~ ~1 ~ #minecraft:air run return 0
execute align xyz positioned ~0.5 ~1 ~0.5 if entity @e[type=interaction,tag=weed_hitbox,distance=..0.1] run return 0

# 生成杂草
execute align xyz positioned ~0.5 ~1 ~0.5 run function stardew:weeds/spawn_random_weed

# 增加计数
scoreboard players add #weed_count sd_temp 1