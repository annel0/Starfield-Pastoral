# 进入镇长庄园 (刘易斯的家)
execute in stardew:interiors run tp @s 4000 64 1000 0 0
tag @s add inside_building
tag @s add inside_house
tag @s add inside_mayor_manor
playsound minecraft:block.wooden_door.open master @s ~ ~ ~ 1 1
particle minecraft:portal ~ ~1 ~ 0.5 0.5 0.5 0.5 20
tellraw @s {"text":"进入镇长庄园","color":"green"}
