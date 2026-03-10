# 进入社区中心
execute in stardew:interiors run tp @s 5000 64 0 0 0
tag @s add inside_building
tag @s add inside_community_center
playsound minecraft:block.wooden_door.open master @s ~ ~ ~ 1 1
particle minecraft:portal ~ ~1 ~ 0.5 0.5 0.5 0.5 20
tellraw @s {"text":"进入社区中心","color":"green"}
