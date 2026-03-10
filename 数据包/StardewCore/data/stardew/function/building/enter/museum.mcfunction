# 进入博物馆
execute in stardew:interiors run tp @s 4000 64 0 0 0
tag @s add inside_building
tag @s add inside_museum
playsound minecraft:block.wooden_door.open master @s ~ ~ ~ 1 1
particle minecraft:portal ~ ~1 ~ 0.5 0.5 0.5 0.5 20
tellraw @s {"text":"进入博物馆","color":"green"}
