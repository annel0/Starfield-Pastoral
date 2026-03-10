# 进入河间大道1号 (乔治、艾芙琳、亚历克斯的家)
execute in stardew:interiors run tp @s 0 64 1000 0 0
tag @s add inside_building
tag @s add inside_house
tag @s add inside_1_river_road
playsound minecraft:block.wooden_door.open master @s ~ ~ ~ 1 1
particle minecraft:portal ~ ~1 ~ 0.5 0.5 0.5 0.5 20
tellraw @s {"text":"进入河间大道1号","color":"green"}
