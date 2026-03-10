# 检查附近怪物的状态

# 检查是否有怪物
execute unless entity @e[tag=sd_monster,distance=..10] run tellraw @s {"text":"❌ 附近10格内没有怪物！","color":"red"}
execute if entity @e[tag=sd_monster,distance=..10] run tellraw @s {"text":"✓ 找到怪物","color":"green"}

# 显示怪物信息
execute as @e[tag=sd_monster,distance=..10,limit=1] run tellraw @a [{"text":"==== 怪物状态 ====","color":"gold","bold":true}]
execute as @e[tag=sd_monster,distance=..10,limit=1] run tellraw @a [{"text":"怪物类型: ","color":"yellow"},{"selector":"@s","color":"white"}]
execute as @e[tag=sd_monster,distance=..10,limit=1] run tellraw @a [{"text":"sd_monster_hp: ","color":"yellow"},{"score":{"name":"@s","objective":"sd_monster_hp"},"color":"white"}]
execute as @e[tag=sd_monster,distance=..10,limit=1] run tellraw @a [{"text":"sd_monster_max_hp: ","color":"yellow"},{"score":{"name":"@s","objective":"sd_monster_max_hp"},"color":"white"}]
execute as @e[tag=sd_monster,distance=..10,limit=1] run tellraw @a [{"text":"sd_monster_damage: ","color":"yellow"},{"score":{"name":"@s","objective":"sd_monster_damage"},"color":"white"}]

# 检查标签
execute as @e[tag=sd_monster,distance=..10,limit=1] if entity @s[tag=sd_monster] run tellraw @a {"text":"✓ 有 sd_monster 标签","color":"green"}
execute as @e[tag=sd_monster,distance=..10,limit=1] if entity @s[tag=sd_monster_init] run tellraw @a {"text":"❌ 还有 sd_monster_init 标签（未初始化）","color":"red"}
execute as @e[tag=sd_monster,distance=..10,limit=1] unless entity @s[tag=sd_monster_init] run tellraw @a {"text":"✓ 已移除 sd_monster_init 标签（已初始化）","color":"green"}

# 检查效果
execute as @e[tag=sd_monster,distance=..10,limit=1] if entity @s[nbt={ActiveEffects:[{Id:11}]}] run tellraw @a {"text":"✓ 有抗性效果","color":"green"}
execute as @e[tag=sd_monster,distance=..10,limit=1] unless entity @s[nbt={ActiveEffects:[{Id:11}]}] run tellraw @a {"text":"❌ 没有抗性效果","color":"red"}

execute as @e[tag=sd_monster,distance=..10,limit=1] if entity @s[nbt={ActiveEffects:[{Id:24}]}] run tellraw @a {"text":"✓ 有发光效果","color":"green"}

tellraw @s {"text":"================","color":"gold","bold":true}
