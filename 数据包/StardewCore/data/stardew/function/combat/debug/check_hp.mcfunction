# 检查怪物的血量数据

execute as @e[type=minecraft:zombie,distance=..10,limit=1,sort=nearest] run tellraw @a [{"text":"=== 怪物数据检查 ===","color":"gold"}]

# 显示原版 Health 数据
execute as @e[type=minecraft:zombie,distance=..10,limit=1,sort=nearest] run tellraw @a [{"text":"原版 Health: ","color":"yellow"},{"nbt":"Health","entity":"@s","color":"green"}]

# 显示自定义血量
execute as @e[type=minecraft:zombie,distance=..10,limit=1,sort=nearest] run tellraw @a [{"text":"sd_monster_hp: ","color":"yellow"},{"score":{"name":"@s","objective":"sd_monster_hp"},"color":"green"}]

execute as @e[type=minecraft:zombie,distance=..10,limit=1,sort=nearest] run tellraw @a [{"text":"sd_monster_max_hp: ","color":"yellow"},{"score":{"name":"@s","objective":"sd_monster_max_hp"},"color":"green"}]

# 显示标签
execute as @e[type=minecraft:zombie,distance=..10,limit=1,sort=nearest] run tellraw @a [{"text":"Tags: ","color":"yellow"},{"nbt":"Tags","entity":"@s","color":"aqua"}]

tellraw @a [{"text":"==================","color":"gold"}]
