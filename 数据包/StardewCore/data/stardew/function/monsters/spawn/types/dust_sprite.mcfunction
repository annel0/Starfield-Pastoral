# stardew:monsters/spawn/types/dust_sprite.mcfunction
# 生成尘埃精灵 (使用末影螨)

execute positioned ~-15 ~ ~-15 run summon minecraft:endermite ~ ~ ~ {Tags:["sd_monster","sd_monster_dust_sprite","sd_new_spawn"],Health:20.0f,Attributes:[{Name:"generic.max_health",Base:20.0},{Name:"generic.attack_damage",Base:4.0},{Name:"generic.movement_speed",Base:0.35}],CustomName:'{"text":"尘埃精灵","color":"#A9A9A9"}'}

execute as @e[type=endermite,tag=sd_new_spawn,limit=1] at @s run spreadplayers ~ ~ 0 15 false @s
tag @e[type=endermite,tag=sd_new_spawn] remove sd_new_spawn
