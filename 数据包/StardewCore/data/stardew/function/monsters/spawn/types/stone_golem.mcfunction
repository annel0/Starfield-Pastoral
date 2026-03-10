# stardew:monsters/spawn/types/stone_golem.mcfunction
# 生成石头傀儡 (使用小型铁傀儡)

execute positioned ~-15 ~ ~-15 run summon minecraft:iron_golem ~ ~ ~ {Tags:["sd_monster","sd_monster_golem","sd_monster_stone_golem","sd_new_spawn"],Health:80.0f,Attributes:[{Name:"generic.max_health",Base:80.0},{Name:"generic.attack_damage",Base:10.0},{Name:"generic.scale",Base:0.6}],CustomName:'{"text":"石头傀儡","color":"gray"}'}

execute as @e[type=iron_golem,tag=sd_new_spawn,limit=1] at @s run spreadplayers ~ ~ 0 15 false @s
tag @e[type=iron_golem,tag=sd_new_spawn] remove sd_new_spawn
