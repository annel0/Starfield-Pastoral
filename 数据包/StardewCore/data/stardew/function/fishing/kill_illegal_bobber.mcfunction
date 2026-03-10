# data/stardew/functions/fishing/kill_illegal_bobber.mcfunction
# 强制清理等级不足的玩家抛出的鱼钩

# 清理所有标记为 sd_level_failed 的玩家附近的鱼钩
execute as @a[tag=sd_level_failed] at @s run kill @e[type=fishing_bobber,distance=..64]

# 移除标记
tag @a remove sd_level_failed
