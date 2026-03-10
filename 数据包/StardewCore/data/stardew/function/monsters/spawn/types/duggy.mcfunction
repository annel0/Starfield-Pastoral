# stardew:monsters/spawn/types/duggy.mcfunction
# 生成地鼠 (使用蠹虫，但不同标签)

execute positioned ~-15 ~ ~-15 run summon minecraft:silverfish ~ ~ ~ {Tags:["sd_monster","sd_monster_duggy","sd_new_spawn"],Health:12.0f,Attributes:[{Name:"generic.max_health",Base:12.0},{Name:"generic.attack_damage",Base:3.0}],CustomName:'{"text":"地鼠","color":"#654321"}'}

execute as @e[type=silverfish,tag=sd_new_spawn,limit=1] at @s run spreadplayers ~ ~ 0 15 false @s
tag @e[type=silverfish,tag=sd_new_spawn] remove sd_new_spawn
