# 进入哈维的诊所
execute in stardew:interiors run tp @s 2000 64 0 0 0
tag @s add inside_building
tag @s add inside_clinic
playsound minecraft:block.wooden_door.open master @s ~ ~ ~ 1 1
particle minecraft:portal ~ ~1 ~ 0.5 0.5 0.5 0.5 20
tellraw @s {"text":"进入哈维的诊所","color":"green"}
