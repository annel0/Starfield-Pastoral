# 检查最近的怪物的 DeathLootTable
execute as @e[tag=sd_monster,limit=1,sort=nearest] run data get entity @s DeathLootTable
execute as @e[tag=sd_monster,limit=1,sort=nearest] run data get entity @s
