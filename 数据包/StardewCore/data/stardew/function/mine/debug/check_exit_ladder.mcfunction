# stardew:mine/debug/check_exit_ladder.mcfunction
# 检查出口梯子状态

tellraw @s {"text":"===== 检查出口梯子 =====","color":"gold"}

# 显示当前层数
tellraw @s [{"text":"当前层数: ","color":"gray"},{"score":{"name":"@s","objective":"sd_mine_floor"}}]

# 检查附近的出口梯子交互实体
execute as @e[type=interaction,tag=sd_mine_ladder_exit,distance=..10] run tellraw @a [{"text":"找到出口梯子实体: ","color":"green"},{"selector":"@s"},{"text":" 位置: "},{"nbt":"Pos","entity":"@s"}]

# 如果没找到
execute unless entity @e[type=interaction,tag=sd_mine_ladder_exit,distance=..10] run tellraw @s {"text":"附近没有找到出口梯子交互实体!","color":"red"}

# 检查梯子方块
execute in stardew:mine positioned 0 65 102 if block ~ ~ ~ minecraft:ladder run tellraw @s {"text":"在 0 65 102 找到梯子方块","color":"green"}
execute in stardew:mine positioned 0 65 102 unless block ~ ~ ~ minecraft:ladder run tellraw @s [{"text":"在 0 65 102 没有梯子，方块是: ","color":"red"}]

tellraw @s {"text":"===== 检查完成 =====","color":"gold"}
