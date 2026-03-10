# stardew:monsters/spawn/types/blue_slime.mcfunction
# 生成蓝色史莱姆 (中型)

execute positioned ~-15 ~ ~-15 run summon minecraft:slime ~ ~ ~ {Size:2,Tags:["sd_monster","sd_monster_slime","sd_monster_blue_slime","sd_new_spawn"],Health:40.0f,Attributes:[{Name:"generic.max_health",Base:40.0},{Name:"generic.attack_damage",Base:5.0}],DeathLootTable:"stardew:monsters/slime_large",CustomName:'{"text":"蓝色史莱姆","color":"blue"}'}

execute as @e[type=slime,tag=sd_new_spawn,limit=1] at @s run spreadplayers ~ ~ 0 15 false @s
tag @e[type=slime,tag=sd_new_spawn] remove sd_new_spawn
