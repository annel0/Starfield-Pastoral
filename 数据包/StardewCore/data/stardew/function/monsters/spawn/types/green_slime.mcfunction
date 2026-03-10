# stardew:monsters/spawn/types/green_slime.mcfunction
# 生成绿色史莱姆

# 在随机位置生成小型史莱姆
execute store result storage stardew:temp spawn_x int 1 run scoreboard players get #spawn_x sd_temp
execute store result storage stardew:temp spawn_z int 1 run scoreboard players get #spawn_z sd_temp

# 生成史莱姆
execute positioned ~-15 ~ ~-15 run summon minecraft:slime ~ ~ ~ {Size:1,Tags:["sd_monster","sd_monster_slime","sd_monster_green_slime","sd_new_spawn"],Health:20.0f,Attributes:[{Name:"generic.max_health",Base:20.0},{Name:"generic.attack_damage",Base:3.0}],DeathLootTable:"stardew:monsters/slime_medium",CustomName:'{"text":"绿色史莱姆","color":"green"}'}

# 移动到随机位置
execute as @e[type=slime,tag=sd_new_spawn,limit=1] at @s run spreadplayers ~ ~ 0 15 false @s

# 移除新生成标签
tag @e[type=slime,tag=sd_new_spawn] remove sd_new_spawn
