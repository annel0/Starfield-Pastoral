# data/stardew/functions/tools/watering_can/water_single_block.mcfunction
# [执行位置: 目标方块]

# 0. 浇水格数计数（用于能量消耗计算）
scoreboard players add @s sd_temp 1

# 1. 视觉与方块更新
setblock ~ ~ ~ minecraft:farmland[moisture=7]
particle minecraft:splash ~ ~1 ~ 0.5 0 0.5 0 10
playsound minecraft:item.bucket.empty player @s ~ ~ ~ 1 1

# 2. 标记作物 (sd_watered = 1)
# [修复] 向上搜索作物逻辑实体 (marker，不是item_display！)
execute positioned ~ ~1 ~ as @e[type=marker,tag=sd_crop,distance=..0.8,limit=1,sort=nearest] run scoreboard players set @s sd_watered 1