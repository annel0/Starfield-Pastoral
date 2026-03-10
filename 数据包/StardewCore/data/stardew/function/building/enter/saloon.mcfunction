# 进入星之果实酒吧
execute in stardew:interiors run tp @s 1000 64 0 0 0
tag @s add inside_building
tag @s add inside_saloon
playsound minecraft:block.wooden_door.open master @s ~ ~ ~ 1 1
particle minecraft:portal ~ ~1 ~ 0.5 0.5 0.5 0.5 20
tellraw @s {"text":"进入星之果实酒吧","color":"green"}
