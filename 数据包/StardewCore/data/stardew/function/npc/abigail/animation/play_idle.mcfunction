# Play idle animation
# @s = villager entity

execute as @e[tag=npc.abigail.visual,limit=1,sort=nearest] at @s run function animated_java:abigail/animations/pause_all
execute as @e[tag=npc.abigail.visual,limit=1,sort=nearest] at @s run function animated_java:abigail/animations/idle/play

scoreboard players set @s stardew.animation 1
