# stardew:monsters/spawn/types/bug.mcfunction
# 生成昆虫 (使用蠹虫)

execute positioned ~-15 ~ ~-15 run summon minecraft:silverfish ~ ~ ~ {Tags:["sd_monster","sd_monster_bug","sd_new_spawn"],Health:15.0f,Attributes:[{Name:"generic.max_health",Base:15.0},{Name:"generic.attack_damage",Base:2.0}],CustomName:'{"text":"昆虫","color":"#8B4513"}'}

execute as @e[type=silverfish,tag=sd_new_spawn,limit=1] at @s run spreadplayers ~ ~ 0 15 false @s
tag @e[type=silverfish,tag=sd_new_spawn] remove sd_new_spawn
