# =========================================================
# 洒水器浇水 - 检查4个方向
# =========================================================
# 执行位置: 洒水器下方(地面高度)

# 上下左右4个方向 - 检测当前位置是否是耕地
execute positioned ~1 ~ ~ if block ~ ~ ~ minecraft:farmland positioned ~ ~ ~ run setblock ~ ~ ~ minecraft:farmland[moisture=7]
execute positioned ~1 ~ ~ if block ~ ~ ~ minecraft:farmland positioned ~ ~ ~ run particle minecraft:splash ~ ~1 ~ 0.5 0 0.5 0 10
execute positioned ~1 ~ ~ if block ~ ~ ~ minecraft:farmland positioned ~ ~1 ~ as @e[type=marker,tag=sd_crop,distance=..0.8,limit=1,sort=nearest] run scoreboard players set @s sd_watered 1

execute positioned ~-1 ~ ~ if block ~ ~ ~ minecraft:farmland positioned ~ ~ ~ run setblock ~ ~ ~ minecraft:farmland[moisture=7]
execute positioned ~-1 ~ ~ if block ~ ~ ~ minecraft:farmland positioned ~ ~ ~ run particle minecraft:splash ~ ~1 ~ 0.5 0 0.5 0 10
execute positioned ~-1 ~ ~ if block ~ ~ ~ minecraft:farmland positioned ~ ~1 ~ as @e[type=marker,tag=sd_crop,distance=..0.8,limit=1,sort=nearest] run scoreboard players set @s sd_watered 1

execute positioned ~ ~ ~1 if block ~ ~ ~ minecraft:farmland positioned ~ ~ ~ run setblock ~ ~ ~ minecraft:farmland[moisture=7]
execute positioned ~ ~ ~1 if block ~ ~ ~ minecraft:farmland positioned ~ ~ ~ run particle minecraft:splash ~ ~1 ~ 0.5 0 0.5 0 10
execute positioned ~ ~ ~1 if block ~ ~ ~ minecraft:farmland positioned ~ ~1 ~ as @e[type=marker,tag=sd_crop,distance=..0.8,limit=1,sort=nearest] run scoreboard players set @s sd_watered 1

execute positioned ~ ~ ~-1 if block ~ ~ ~ minecraft:farmland positioned ~ ~ ~ run setblock ~ ~ ~ minecraft:farmland[moisture=7]
execute positioned ~ ~ ~-1 if block ~ ~ ~ minecraft:farmland positioned ~ ~ ~ run particle minecraft:splash ~ ~1 ~ 0.5 0 0.5 0 10
execute positioned ~ ~ ~-1 if block ~ ~ ~ minecraft:farmland positioned ~ ~1 ~ as @e[type=marker,tag=sd_crop,distance=..0.8,limit=1,sort=nearest] run scoreboard players set @s sd_watered 1
