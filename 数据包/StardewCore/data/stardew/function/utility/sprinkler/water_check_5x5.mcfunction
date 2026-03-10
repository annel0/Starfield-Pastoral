# =========================================================
# 洒水器浇水 - 5x5区域 (不含中心)
# =========================================================

# 第一行(Z=-2)
execute positioned ~-2 ~ ~-2 if block ~ ~ ~ minecraft:farmland run setblock ~ ~ ~ minecraft:farmland[moisture=7]
execute positioned ~-2 ~ ~-2 if block ~ ~ ~ minecraft:farmland run particle minecraft:splash ~ ~1 ~ 0.5 0 0.5 0 10
execute positioned ~-2 ~ ~-2 if block ~ ~ ~ minecraft:farmland positioned ~ ~1 ~ as @e[type=marker,tag=sd_crop,distance=..0.8,limit=1] run scoreboard players set @s sd_watered 1

execute positioned ~-1 ~ ~-2 if block ~ ~ ~ minecraft:farmland run setblock ~ ~ ~ minecraft:farmland[moisture=7]
execute positioned ~-1 ~ ~-2 if block ~ ~ ~ minecraft:farmland run particle minecraft:splash ~ ~1 ~ 0.5 0 0.5 0 10
execute positioned ~-1 ~ ~-2 if block ~ ~ ~ minecraft:farmland positioned ~ ~1 ~ as @e[type=marker,tag=sd_crop,distance=..0.8,limit=1] run scoreboard players set @s sd_watered 1

execute positioned ~0 ~ ~-2 if block ~ ~ ~ minecraft:farmland run setblock ~ ~ ~ minecraft:farmland[moisture=7]
execute positioned ~0 ~ ~-2 if block ~ ~ ~ minecraft:farmland run particle minecraft:splash ~ ~1 ~ 0.5 0 0.5 0 10
execute positioned ~0 ~ ~-2 if block ~ ~ ~ minecraft:farmland positioned ~ ~1 ~ as @e[type=marker,tag=sd_crop,distance=..0.8,limit=1] run scoreboard players set @s sd_watered 1

execute positioned ~1 ~ ~-2 if block ~ ~ ~ minecraft:farmland run setblock ~ ~ ~ minecraft:farmland[moisture=7]
execute positioned ~1 ~ ~-2 if block ~ ~ ~ minecraft:farmland run particle minecraft:splash ~ ~1 ~ 0.5 0 0.5 0 10
execute positioned ~1 ~ ~-2 if block ~ ~ ~ minecraft:farmland positioned ~ ~1 ~ as @e[type=marker,tag=sd_crop,distance=..0.8,limit=1] run scoreboard players set @s sd_watered 1

execute positioned ~2 ~ ~-2 if block ~ ~ ~ minecraft:farmland run setblock ~ ~ ~ minecraft:farmland[moisture=7]
execute positioned ~2 ~ ~-2 if block ~ ~ ~ minecraft:farmland run particle minecraft:splash ~ ~1 ~ 0.5 0 0.5 0 10
execute positioned ~2 ~ ~-2 if block ~ ~ ~ minecraft:farmland positioned ~ ~1 ~ as @e[type=marker,tag=sd_crop,distance=..0.8,limit=1] run scoreboard players set @s sd_watered 1

# 第二行(Z=-1)
execute positioned ~-2 ~ ~-1 if block ~ ~ ~ minecraft:farmland run setblock ~ ~ ~ minecraft:farmland[moisture=7]
execute positioned ~-2 ~ ~-1 if block ~ ~ ~ minecraft:farmland run particle minecraft:splash ~ ~1 ~ 0.5 0 0.5 0 10
execute positioned ~-2 ~ ~-1 if block ~ ~ ~ minecraft:farmland positioned ~ ~1 ~ as @e[type=marker,tag=sd_crop,distance=..0.8,limit=1] run scoreboard players set @s sd_watered 1

execute positioned ~-1 ~ ~-1 if block ~ ~ ~ minecraft:farmland run setblock ~ ~ ~ minecraft:farmland[moisture=7]
execute positioned ~-1 ~ ~-1 if block ~ ~ ~ minecraft:farmland run particle minecraft:splash ~ ~1 ~ 0.5 0 0.5 0 10
execute positioned ~-1 ~ ~-1 if block ~ ~ ~ minecraft:farmland positioned ~ ~1 ~ as @e[type=marker,tag=sd_crop,distance=..0.8,limit=1] run scoreboard players set @s sd_watered 1

execute positioned ~0 ~ ~-1 if block ~ ~ ~ minecraft:farmland run setblock ~ ~ ~ minecraft:farmland[moisture=7]
execute positioned ~0 ~ ~-1 if block ~ ~ ~ minecraft:farmland run particle minecraft:splash ~ ~1 ~ 0.5 0 0.5 0 10
execute positioned ~0 ~ ~-1 if block ~ ~ ~ minecraft:farmland positioned ~ ~1 ~ as @e[type=marker,tag=sd_crop,distance=..0.8,limit=1] run scoreboard players set @s sd_watered 1

execute positioned ~1 ~ ~-1 if block ~ ~ ~ minecraft:farmland run setblock ~ ~ ~ minecraft:farmland[moisture=7]
execute positioned ~1 ~ ~-1 if block ~ ~ ~ minecraft:farmland run particle minecraft:splash ~ ~1 ~ 0.5 0 0.5 0 10
execute positioned ~1 ~ ~-1 if block ~ ~ ~ minecraft:farmland positioned ~ ~1 ~ as @e[type=marker,tag=sd_crop,distance=..0.8,limit=1] run scoreboard players set @s sd_watered 1

execute positioned ~2 ~ ~-1 if block ~ ~ ~ minecraft:farmland run setblock ~ ~ ~ minecraft:farmland[moisture=7]
execute positioned ~2 ~ ~-1 if block ~ ~ ~ minecraft:farmland run particle minecraft:splash ~ ~1 ~ 0.5 0 0.5 0 10
execute positioned ~2 ~ ~-1 if block ~ ~ ~ minecraft:farmland positioned ~ ~1 ~ as @e[type=marker,tag=sd_crop,distance=..0.8,limit=1] run scoreboard players set @s sd_watered 1

# 第三行(Z=0, 跳过中心)
execute positioned ~-2 ~ ~0 if block ~ ~ ~ minecraft:farmland run setblock ~ ~ ~ minecraft:farmland[moisture=7]
execute positioned ~-2 ~ ~0 if block ~ ~ ~ minecraft:farmland run particle minecraft:splash ~ ~1 ~ 0.5 0 0.5 0 10
execute positioned ~-2 ~ ~0 if block ~ ~ ~ minecraft:farmland positioned ~ ~1 ~ as @e[type=marker,tag=sd_crop,distance=..0.8,limit=1] run scoreboard players set @s sd_watered 1

execute positioned ~-1 ~ ~0 if block ~ ~ ~ minecraft:farmland run setblock ~ ~ ~ minecraft:farmland[moisture=7]
execute positioned ~-1 ~ ~0 if block ~ ~ ~ minecraft:farmland run particle minecraft:splash ~ ~1 ~ 0.5 0 0.5 0 10
execute positioned ~-1 ~ ~0 if block ~ ~ ~ minecraft:farmland positioned ~ ~1 ~ as @e[type=marker,tag=sd_crop,distance=..0.8,limit=1] run scoreboard players set @s sd_watered 1

execute positioned ~1 ~ ~0 if block ~ ~ ~ minecraft:farmland run setblock ~ ~ ~ minecraft:farmland[moisture=7]
execute positioned ~1 ~ ~0 if block ~ ~ ~ minecraft:farmland run particle minecraft:splash ~ ~1 ~ 0.5 0 0.5 0 10
execute positioned ~1 ~ ~0 if block ~ ~ ~ minecraft:farmland positioned ~ ~1 ~ as @e[type=marker,tag=sd_crop,distance=..0.8,limit=1] run scoreboard players set @s sd_watered 1

execute positioned ~2 ~ ~0 if block ~ ~ ~ minecraft:farmland run setblock ~ ~ ~ minecraft:farmland[moisture=7]
execute positioned ~2 ~ ~0 if block ~ ~ ~ minecraft:farmland run particle minecraft:splash ~ ~1 ~ 0.5 0 0.5 0 10
execute positioned ~2 ~ ~0 if block ~ ~ ~ minecraft:farmland positioned ~ ~1 ~ as @e[type=marker,tag=sd_crop,distance=..0.8,limit=1] run scoreboard players set @s sd_watered 1

# 第四行(Z=1)
execute positioned ~-2 ~ ~1 if block ~ ~ ~ minecraft:farmland run setblock ~ ~ ~ minecraft:farmland[moisture=7]
execute positioned ~-2 ~ ~1 if block ~ ~ ~ minecraft:farmland run particle minecraft:splash ~ ~1 ~ 0.5 0 0.5 0 10
execute positioned ~-2 ~ ~1 if block ~ ~ ~ minecraft:farmland positioned ~ ~1 ~ as @e[type=marker,tag=sd_crop,distance=..0.8,limit=1] run scoreboard players set @s sd_watered 1

execute positioned ~-1 ~ ~1 if block ~ ~ ~ minecraft:farmland run setblock ~ ~ ~ minecraft:farmland[moisture=7]
execute positioned ~-1 ~ ~1 if block ~ ~ ~ minecraft:farmland run particle minecraft:splash ~ ~1 ~ 0.5 0 0.5 0 10
execute positioned ~-1 ~ ~1 if block ~ ~ ~ minecraft:farmland positioned ~ ~1 ~ as @e[type=marker,tag=sd_crop,distance=..0.8,limit=1] run scoreboard players set @s sd_watered 1

execute positioned ~0 ~ ~1 if block ~ ~ ~ minecraft:farmland run setblock ~ ~ ~ minecraft:farmland[moisture=7]
execute positioned ~0 ~ ~1 if block ~ ~ ~ minecraft:farmland run particle minecraft:splash ~ ~1 ~ 0.5 0 0.5 0 10
execute positioned ~0 ~ ~1 if block ~ ~ ~ minecraft:farmland positioned ~ ~1 ~ as @e[type=marker,tag=sd_crop,distance=..0.8,limit=1] run scoreboard players set @s sd_watered 1

execute positioned ~1 ~ ~1 if block ~ ~ ~ minecraft:farmland run setblock ~ ~ ~ minecraft:farmland[moisture=7]
execute positioned ~1 ~ ~1 if block ~ ~ ~ minecraft:farmland run particle minecraft:splash ~ ~1 ~ 0.5 0 0.5 0 10
execute positioned ~1 ~ ~1 if block ~ ~ ~ minecraft:farmland positioned ~ ~1 ~ as @e[type=marker,tag=sd_crop,distance=..0.8,limit=1] run scoreboard players set @s sd_watered 1

execute positioned ~2 ~ ~1 if block ~ ~ ~ minecraft:farmland run setblock ~ ~ ~ minecraft:farmland[moisture=7]
execute positioned ~2 ~ ~1 if block ~ ~ ~ minecraft:farmland run particle minecraft:splash ~ ~1 ~ 0.5 0 0.5 0 10
execute positioned ~2 ~ ~1 if block ~ ~ ~ minecraft:farmland positioned ~ ~1 ~ as @e[type=marker,tag=sd_crop,distance=..0.8,limit=1] run scoreboard players set @s sd_watered 1

# 第五行(Z=2)
execute positioned ~-2 ~ ~2 if block ~ ~ ~ minecraft:farmland run setblock ~ ~ ~ minecraft:farmland[moisture=7]
execute positioned ~-2 ~ ~2 if block ~ ~ ~ minecraft:farmland run particle minecraft:splash ~ ~1 ~ 0.5 0 0.5 0 10
execute positioned ~-2 ~ ~2 if block ~ ~ ~ minecraft:farmland positioned ~ ~1 ~ as @e[type=marker,tag=sd_crop,distance=..0.8,limit=1] run scoreboard players set @s sd_watered 1

execute positioned ~-1 ~ ~2 if block ~ ~ ~ minecraft:farmland run setblock ~ ~ ~ minecraft:farmland[moisture=7]
execute positioned ~-1 ~ ~2 if block ~ ~ ~ minecraft:farmland run particle minecraft:splash ~ ~1 ~ 0.5 0 0.5 0 10
execute positioned ~-1 ~ ~2 if block ~ ~ ~ minecraft:farmland positioned ~ ~1 ~ as @e[type=marker,tag=sd_crop,distance=..0.8,limit=1] run scoreboard players set @s sd_watered 1

execute positioned ~0 ~ ~2 if block ~ ~ ~ minecraft:farmland run setblock ~ ~ ~ minecraft:farmland[moisture=7]
execute positioned ~0 ~ ~2 if block ~ ~ ~ minecraft:farmland run particle minecraft:splash ~ ~1 ~ 0.5 0 0.5 0 10
execute positioned ~0 ~ ~2 if block ~ ~ ~ minecraft:farmland positioned ~ ~1 ~ as @e[type=marker,tag=sd_crop,distance=..0.8,limit=1] run scoreboard players set @s sd_watered 1

execute positioned ~1 ~ ~2 if block ~ ~ ~ minecraft:farmland run setblock ~ ~ ~ minecraft:farmland[moisture=7]
execute positioned ~1 ~ ~2 if block ~ ~ ~ minecraft:farmland run particle minecraft:splash ~ ~1 ~ 0.5 0 0.5 0 10
execute positioned ~1 ~ ~2 if block ~ ~ ~ minecraft:farmland positioned ~ ~1 ~ as @e[type=marker,tag=sd_crop,distance=..0.8,limit=1] run scoreboard players set @s sd_watered 1

execute positioned ~2 ~ ~2 if block ~ ~ ~ minecraft:farmland run setblock ~ ~ ~ minecraft:farmland[moisture=7]
execute positioned ~2 ~ ~2 if block ~ ~ ~ minecraft:farmland run particle minecraft:splash ~ ~1 ~ 0.5 0 0.5 0 10
execute positioned ~2 ~ ~2 if block ~ ~ ~ minecraft:farmland positioned ~ ~1 ~ as @e[type=marker,tag=sd_crop,distance=..0.8,limit=1] run scoreboard players set @s sd_watered 1

