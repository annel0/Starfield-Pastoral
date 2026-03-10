# data/stardew/functions/debug/sync_one_crop.mcfunction
# 同步单个作物的肥料数据

# 检查是否有肥料marker
execute unless entity @e[type=marker,tag=sd_fertilizer_marker,distance=..0.1] run return 0

# 复制肥料数据到作物
execute as @e[type=marker,tag=sd_fertilizer_marker,distance=..0.1,limit=1] run scoreboard players operation @e[type=marker,tag=sd_crop,distance=..0.1,limit=1,sort=nearest] sd_fertilizer_type = @s sd_fertilizer_type
execute as @e[type=marker,tag=sd_fertilizer_marker,distance=..0.1,limit=1] run scoreboard players operation @e[type=marker,tag=sd_crop,distance=..0.1,limit=1,sort=nearest] sd_fertilizer_level = @s sd_fertilizer_level

tellraw @a[distance=..5] [{"text":"[调试] 同步成功: type=","color":"green"},{"score":{"name":"@s","objective":"sd_fertilizer_type"}},{"text":" level="},{"score":{"name":"@s","objective":"sd_fertilizer_level"}}]
