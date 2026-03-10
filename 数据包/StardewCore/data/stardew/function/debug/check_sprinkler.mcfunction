# =========================================================
# Debug: 检查最近的洒水器状态
# =========================================================
# 用法: /function stardew:debug/check_sprinkler
# 功能: 检查最近5格内洒水器的状态(tags, 分数等)
# =========================================================

# 1. 查找最近的洒水器
execute as @e[type=interaction,tag=sd_sprinkler,distance=..5,limit=1,sort=nearest] at @s run tellraw @a[distance=..5] [{"text":"[Debug] ","color":"aqua"},{"text":"找到洒水器实体","color":"green"}]

# 2. 显示实体Tags
execute as @e[type=interaction,tag=sd_sprinkler,distance=..5,limit=1,sort=nearest] run tellraw @a[distance=..5] [{"text":"  Tags: ","color":"gray"},{"nbt":"Tags","entity":"@s","color":"yellow"}]

# 3. 显示洒水器类型
execute as @e[type=interaction,tag=sd_sprinkler,distance=..5,limit=1,sort=nearest] run tellraw @a[distance=..5] [{"text":"  Type (sd_sprinkler_type): ","color":"gray"},{"score":{"name":"@s","objective":"sd_sprinkler_type"},"color":"yellow"}]

# 4. 显示是否激活
execute as @e[type=interaction,tag=sd_sprinkler,distance=..5,limit=1,sort=nearest] run tellraw @a[distance=..5] [{"text":"  Active (sd_utility_active): ","color":"gray"},{"score":{"name":"@s","objective":"sd_utility_active"},"color":"yellow"}]

# 5. 显示位置
execute as @e[type=interaction,tag=sd_sprinkler,distance=..5,limit=1,sort=nearest] at @s run tellraw @a[distance=..5] [{"text":"  Position: ","color":"gray"},{"text":"X: "},{"score":{"name":"@s","objective":"sd_pos_x"},"color":"yellow"},{"text":" Y: "},{"score":{"name":"@s","objective":"sd_pos_y"},"color":"yellow"},{"text":" Z: "},{"score":{"name":"@s","objective":"sd_pos_z"},"color":"yellow"}]

# 6. 如果找不到洒水器
execute unless entity @e[type=interaction,tag=sd_sprinkler,distance=..5] run tellraw @s [{"text":"[Debug] ","color":"aqua"},{"text":"附近5格内没有找到洒水器","color":"red"}]

# 7. 显示附近的 display entity(视觉模型)
execute as @e[type=item_display,distance=..5,limit=5] run tellraw @a[distance=..5] [{"text":"[Debug] ","color":"aqua"},{"text":"找到 item_display: ","color":"gray"},{"nbt":"item","entity":"@s","color":"yellow"}]

# 8. 手动尝试浇水
tellraw @s [{"text":"[Debug] ","color":"aqua"},{"text":"尝试手动触发浇水...","color":"yellow"}]
execute as @e[type=interaction,tag=sd_sprinkler,distance=..5,limit=1,sort=nearest] at @s run function stardew:utility/sprinkler/water_router
tellraw @s [{"text":"[Debug] ","color":"aqua"},{"text":"手动浇水完成","color":"green"}]
