# stardew:monsters/spawn/types/frost_bat.mcfunction
# 生成冰霜蝙蝠

execute positioned ~-15 ~ ~-15 run summon minecraft:bat ~ ~3 ~ {Tags:["sd_monster","sd_monster_bat","sd_monster_frost_bat","sd_new_spawn"],Health:35.0f,Attributes:[{Name:"generic.max_health",Base:35.0}],CustomName:'{"text":"冰霜蝙蝠","color":"aqua"}',active_effects:[{id:"minecraft:slowness",amplifier:0,duration:999999,show_particles:true}]}

execute as @e[type=bat,tag=sd_new_spawn,limit=1] at @s run spreadplayers ~ ~ 0 15 false @s
tag @e[type=bat,tag=sd_new_spawn] remove sd_new_spawn
