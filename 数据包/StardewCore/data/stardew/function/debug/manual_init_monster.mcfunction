# 手动初始化附近的怪物

# 给附近的怪物加上初始化标签
tag @e[type=minecraft:zombie,distance=..10] add sd_monster
tag @e[type=minecraft:zombie,distance=..10] add sd_monster_init

tellraw @s {"text":"[Debug] 已给附近僵尸添加标签","color":"yellow"}

# 立即执行初始化
execute as @e[tag=sd_monster_init,distance=..10] run function stardew:combat/init_monster

# 检查效果
execute as @e[tag=sd_monster,distance=..10] run tellraw @a [{"text":"怪物: ","color":"green"},{"selector":"@s"},{"text":" - 抗性: ","color":"gray"},{"nbt":"ActiveEffects[{Id:11}].Amplifier","entity":"@s","color":"yellow"}]

tellraw @s {"text":"[Debug] 初始化完成，检查上方信息","color":"green"}
