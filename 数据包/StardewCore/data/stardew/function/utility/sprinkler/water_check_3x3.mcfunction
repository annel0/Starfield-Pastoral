# =========================================================
# 洒水器浇水 - 3x3区域 (不含中心)
# =========================================================

execute positioned ~-1 ~ ~-1 if block ~ ~ ~ minecraft:farmland positioned ~ ~ ~ run setblock ~ ~ ~ minecraft:farmland[moisture=7]
execute positioned ~-1 ~ ~-1 if block ~ ~ ~ minecraft:farmland positioned ~ ~ ~ run particle minecraft:splash ~ ~1 ~ 0.5 0 0.5 0 10
execute positioned ~-1 ~ ~-1 if block ~ ~ ~ minecraft:farmland positioned ~ ~1 ~ as @e[type=marker,tag=sd_crop,distance=..0.8,limit=1,sort=nearest] run scoreboard players set @s sd_watered 1

execute positioned ~-1 ~ ~0 if block ~ ~ ~ minecraft:farmland positioned ~ ~ ~ run setblock ~ ~ ~ minecraft:farmland[moisture=7]
execute positioned ~-1 ~ ~0 if block ~ ~ ~ minecraft:farmland positioned ~ ~ ~ run particle minecraft:splash ~ ~1 ~ 0.5 0 0.5 0 10
execute positioned ~-1 ~ ~0 if block ~ ~ ~ minecraft:farmland positioned ~ ~1 ~ as @e[type=marker,tag=sd_crop,distance=..0.8,limit=1,sort=nearest] run scoreboard players set @s sd_watered 1

execute positioned ~-1 ~ ~1 if block ~ ~ ~ minecraft:farmland positioned ~ ~ ~ run setblock ~ ~ ~ minecraft:farmland[moisture=7]
execute positioned ~-1 ~ ~1 if block ~ ~ ~ minecraft:farmland positioned ~ ~ ~ run particle minecraft:splash ~ ~1 ~ 0.5 0 0.5 0 10
execute positioned ~-1 ~ ~1 if block ~ ~ ~ minecraft:farmland positioned ~ ~1 ~ as @e[type=marker,tag=sd_crop,distance=..0.8,limit=1,sort=nearest] run scoreboard players set @s sd_watered 1

execute positioned ~0 ~ ~-1 if block ~ ~ ~ minecraft:farmland positioned ~ ~ ~ run setblock ~ ~ ~ minecraft:farmland[moisture=7]
execute positioned ~0 ~ ~-1 if block ~ ~ ~ minecraft:farmland positioned ~ ~ ~ run particle minecraft:splash ~ ~1 ~ 0.5 0 0.5 0 10
execute positioned ~0 ~ ~-1 if block ~ ~ ~ minecraft:farmland positioned ~ ~1 ~ as @e[type=marker,tag=sd_crop,distance=..0.8,limit=1,sort=nearest] run scoreboard players set @s sd_watered 1

execute positioned ~0 ~ ~1 if block ~ ~ ~ minecraft:farmland positioned ~ ~ ~ run setblock ~ ~ ~ minecraft:farmland[moisture=7]
execute positioned ~0 ~ ~1 if block ~ ~ ~ minecraft:farmland positioned ~ ~ ~ run particle minecraft:splash ~ ~1 ~ 0.5 0 0.5 0 10
execute positioned ~0 ~ ~1 if block ~ ~ ~ minecraft:farmland positioned ~ ~1 ~ as @e[type=marker,tag=sd_crop,distance=..0.8,limit=1,sort=nearest] run scoreboard players set @s sd_watered 1

execute positioned ~1 ~ ~-1 if block ~ ~ ~ minecraft:farmland positioned ~ ~ ~ run setblock ~ ~ ~ minecraft:farmland[moisture=7]
execute positioned ~1 ~ ~-1 if block ~ ~ ~ minecraft:farmland positioned ~ ~ ~ run particle minecraft:splash ~ ~1 ~ 0.5 0 0.5 0 10
execute positioned ~1 ~ ~-1 if block ~ ~ ~ minecraft:farmland positioned ~ ~1 ~ as @e[type=marker,tag=sd_crop,distance=..0.8,limit=1,sort=nearest] run scoreboard players set @s sd_watered 1

execute positioned ~1 ~ ~0 if block ~ ~ ~ minecraft:farmland positioned ~ ~ ~ run setblock ~ ~ ~ minecraft:farmland[moisture=7]
execute positioned ~1 ~ ~0 if block ~ ~ ~ minecraft:farmland positioned ~ ~ ~ run particle minecraft:splash ~ ~1 ~ 0.5 0 0.5 0 10
execute positioned ~1 ~ ~0 if block ~ ~ ~ minecraft:farmland positioned ~ ~1 ~ as @e[type=marker,tag=sd_crop,distance=..0.8,limit=1,sort=nearest] run scoreboard players set @s sd_watered 1

execute positioned ~1 ~ ~1 if block ~ ~ ~ minecraft:farmland positioned ~ ~ ~ run setblock ~ ~ ~ minecraft:farmland[moisture=7]
execute positioned ~1 ~ ~1 if block ~ ~ ~ minecraft:farmland positioned ~ ~ ~ run particle minecraft:splash ~ ~1 ~ 0.5 0 0.5 0 10
execute positioned ~1 ~ ~1 if block ~ ~ ~ minecraft:farmland positioned ~ ~1 ~ as @e[type=marker,tag=sd_crop,distance=..0.8,limit=1,sort=nearest] run scoreboard players set @s sd_watered 1

