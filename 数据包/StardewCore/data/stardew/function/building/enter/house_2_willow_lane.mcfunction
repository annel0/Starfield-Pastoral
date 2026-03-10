# 进入柳巷2号 (艾米丽、海莉的家)
execute in stardew:interiors run tp @s 2000 64 1000 0 0
tag @s add inside_building
tag @s add inside_house
tag @s add inside_2_willow_lane
playsound minecraft:block.wooden_door.open master @s ~ ~ ~ 1 1
particle minecraft:portal ~ ~1 ~ 0.5 0.5 0.5 0.5 20
tellraw @s {"text":"进入柳巷2号","color":"green"}
