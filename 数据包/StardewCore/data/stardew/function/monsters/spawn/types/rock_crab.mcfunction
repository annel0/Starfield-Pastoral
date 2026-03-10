# stardew:monsters/spawn/types/rock_crab.mcfunction
# 生成岩石蟹 (使用蜘蛛)

execute positioned ~-15 ~ ~-15 run summon minecraft:spider ~ ~ ~ {Tags:["sd_monster","sd_monster_crab","sd_monster_rock_crab","sd_new_spawn"],Health:30.0f,Attributes:[{Name:"generic.max_health",Base:30.0},{Name:"generic.attack_damage",Base:4.0},{Name:"generic.movement_speed",Base:0.25}],CustomName:'{"text":"岩石蟹","color":"gray"}'}

execute as @e[type=spider,tag=sd_new_spawn,limit=1] at @s run spreadplayers ~ ~ 0 15 false @s
tag @e[type=spider,tag=sd_new_spawn] remove sd_new_spawn
