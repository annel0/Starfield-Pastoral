# 进入拖车 (潘姆、潘妮的家)
execute in stardew:interiors run tp @s 3000 64 1000 0 0
tag @s add inside_building
tag @s add inside_house
tag @s add inside_trailer
playsound minecraft:block.wooden_door.open master @s ~ ~ ~ 1 1
particle minecraft:portal ~ ~1 ~ 0.5 0.5 0.5 0.5 20
tellraw @s {"text":"进入拖车","color":"green"}
