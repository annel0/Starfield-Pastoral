# data/stardew/functions/fishing/check_for_land.mcfunction
# [执行者: 鱼钩 (fishing_bobber)，在 at @s 处运行]
# 作用: 精准检测鱼钩当前位置 (~ ~ ~) 或下方一格 (~ ~-1 ~) 是否为水。

# 1. 默认：不是水 (1)。
scoreboard players set @s sd_const 1

# =========================================================
# 2. 核心检测：检查当前或下方是否是水 (如果发现水，sd_const = 0)
# =========================================================

# --- 检查当前位置 (~ ~ ~) ---
execute if block ~ ~ ~ minecraft:water[level=0] run scoreboard players set @s sd_const 0
execute if block ~ ~ ~ minecraft:water[level=1] run scoreboard players set @s sd_const 0
execute if block ~ ~ ~ minecraft:water[level=2] run scoreboard players set @s sd_const 0
execute if block ~ ~ ~ minecraft:water[level=3] run scoreboard players set @s sd_const 0
execute if block ~ ~ ~ minecraft:water[level=4] run scoreboard players set @s sd_const 0
execute if block ~ ~ ~ minecraft:water[level=5] run scoreboard players set @s sd_const 0
execute if block ~ ~ ~ minecraft:water[level=6] run scoreboard players set @s sd_const 0
execute if block ~ ~ ~ minecraft:water[level=7] run scoreboard players set @s sd_const 0
execute if block ~ ~ ~ minecraft:water[level=8] run scoreboard players set @s sd_const 0
execute if block ~ ~ ~ minecraft:water[level=9] run scoreboard players set @s sd_const 0
execute if block ~ ~ ~ minecraft:water[level=10] run scoreboard players set @s sd_const 0
execute if block ~ ~ ~ minecraft:water[level=11] run scoreboard players set @s sd_const 0
execute if block ~ ~ ~ minecraft:water[level=12] run scoreboard players set @s sd_const 0
execute if block ~ ~ ~ minecraft:water[level=13] run scoreboard players set @s sd_const 0
execute if block ~ ~ ~ minecraft:water[level=14] run scoreboard players set @s sd_const 0
execute if block ~ ~ ~ minecraft:water[level=15] run scoreboard players set @s sd_const 0


# --- 检查下方位置 (~ ~-1 ~) - 用于浮动修正 ---
execute if block ~ ~-1 ~ minecraft:water[level=0] run scoreboard players set @s sd_const 0
execute if block ~ ~-1 ~ minecraft:water[level=1] run scoreboard players set @s sd_const 0
execute if block ~ ~-1 ~ minecraft:water[level=2] run scoreboard players set @s sd_const 0
execute if block ~ ~-1 ~ minecraft:water[level=3] run scoreboard players set @s sd_const 0
execute if block ~ ~-1 ~ minecraft:water[level=4] run scoreboard players set @s sd_const 0
execute if block ~ ~-1 ~ minecraft:water[level=5] run scoreboard players set @s sd_const 0
execute if block ~ ~-1 ~ minecraft:water[level=6] run scoreboard players set @s sd_const 0
execute if block ~ ~-1 ~ minecraft:water[level=7] run scoreboard players set @s sd_const 0
execute if block ~ ~-1 ~ minecraft:water[level=8] run scoreboard players set @s sd_const 0
execute if block ~ ~-1 ~ minecraft:water[level=9] run scoreboard players set @s sd_const 0
execute if block ~ ~-1 ~ minecraft:water[level=10] run scoreboard players set @s sd_const 0
execute if block ~ ~-1 ~ minecraft:water[level=11] run scoreboard players set @s sd_const 0
execute if block ~ ~-1 ~ minecraft:water[level=12] run scoreboard players set @s sd_const 0
execute if block ~ ~-1 ~ minecraft:water[level=13] run scoreboard players set @s sd_const 0
execute if block ~ ~-1 ~ minecraft:water[level=14] run scoreboard players set @s sd_const 0
execute if block ~ ~-1 ~ minecraft:water[level=15] run scoreboard players set @s sd_const 0


# --- 检查水生植物 (确保鱼钩落在植被上不会死) ---
execute if block ~ ~ ~ minecraft:sea_pickle run scoreboard players set @s sd_const 0
execute if block ~ ~ ~ minecraft:kelp run scoreboard players set @s sd_const 0
execute if block ~ ~ ~ minecraft:seagrass run scoreboard players set @s sd_const 0
execute if block ~ ~-1 ~ minecraft:sea_pickle run scoreboard players set @s sd_const 0
execute if block ~ ~-1 ~ minecraft:kelp run scoreboard players set @s sd_const 0
execute if block ~ ~-1 ~ minecraft:seagrass run scoreboard players set @s sd_const 0


# 3. 如果 sd_const = 0 (检测到水)，则设置安全锁为 1。
execute if score @s sd_const matches 0 run scoreboard players set @s sd_hook_safe 1

# 4. 如果 sd_hook_safe 仍为 0，说明它是陆地，立即清理。
execute if score @s sd_hook_safe matches 0 run function stardew:fishing/hook_landed_on_land