# Update animation based on movement
# @s = villager entity

execute store result score @s stardew.npc.calc_1 run data get entity @s Pos[0] 100
execute store result score @s stardew.npc.calc_2 run data get entity @s Pos[2] 100

execute unless score @s stardew.npc.last_x matches -2147483648..2147483647 run scoreboard players operation @s stardew.npc.last_x = @s stardew.npc.calc_1
execute unless score @s stardew.npc.last_z matches -2147483648..2147483647 run scoreboard players operation @s stardew.npc.last_z = @s stardew.npc.calc_2

scoreboard players operation @s stardew.npc.calc_1 -= @s stardew.npc.last_x
scoreboard players operation @s stardew.npc.calc_2 -= @s stardew.npc.last_z

execute unless score @s stardew.npc.calc_1 matches -4..4 unless score @s stardew.animation matches 2 run function stardew:npc/abigail/animation/play_walk
execute unless score @s stardew.npc.calc_2 matches -4..4 unless score @s stardew.animation matches 2 run function stardew:npc/abigail/animation/play_walk
execute if score @s stardew.npc.calc_1 matches -4..4 if score @s stardew.npc.calc_2 matches -4..4 unless score @s stardew.animation matches 1 run function stardew:npc/abigail/animation/play_idle

execute store result score @s stardew.npc.last_x run data get entity @s Pos[0] 100
execute store result score @s stardew.npc.last_z run data get entity @s Pos[2] 100
