# stardew:mine/debug/check_entities.mcfunction
# 调试：检查矿洞维度中所有实体的位置

tellraw @a [{"text":"===== 实体检测 =====","color":"gold"}]

# 检测所有 sd_mine_entity
execute in stardew:mine as @e[tag=sd_mine_entity] run tellraw @a [{"text":"sd_mine_entity: ","color":"yellow"},{"selector":"@s"},{"text":" 位置: "},{"text":"X="},{"score":{"name":"@s","objective":"sd_mine_temp"}},{"text":" 实际: "},{"nbt":"Pos","entity":"@s"}]

# 检测所有 sd_mine_stone
execute in stardew:mine run tellraw @a [{"text":"sd_mine_stone 数量: ","color":"aqua"}]
execute store result score #stone_count sd_mine_temp in stardew:mine if entity @e[tag=sd_mine_stone]
tellraw @a [{"score":{"name":"#stone_count","objective":"sd_mine_temp"}}]

# 显示前3个石头的位置
execute in stardew:mine as @e[tag=sd_mine_stone,limit=3] run tellraw @a [{"text":"  石头位置: ","color":"gray"},{"nbt":"Pos","entity":"@s"}]

# 检测所有 sd_mine_elevator (排除大厅)
execute in stardew:mine run tellraw @a [{"text":"sd_mine_elevator 数量: ","color":"aqua"}]
execute store result score #elevator_count sd_mine_temp in stardew:mine if entity @e[tag=sd_mine_elevator,tag=!sd_mine_lobby_elevator]
tellraw @a [{"score":{"name":"#elevator_count","objective":"sd_mine_temp"}}]
execute in stardew:mine as @e[tag=sd_mine_elevator,tag=!sd_mine_lobby_elevator,limit=3] run tellraw @a [{"text":"  电梯位置: ","color":"gray"},{"nbt":"Pos","entity":"@s"}]

# 检测所有 sd_mine_ladder_exit
execute in stardew:mine run tellraw @a [{"text":"sd_mine_ladder_exit 数量: ","color":"aqua"}]
execute store result score #exit_count sd_mine_temp in stardew:mine if entity @e[tag=sd_mine_ladder_exit]
tellraw @a [{"score":{"name":"#exit_count","objective":"sd_mine_temp"}}]
execute in stardew:mine as @e[tag=sd_mine_ladder_exit,limit=3] run tellraw @a [{"text":"  出口位置: ","color":"gray"},{"nbt":"Pos","entity":"@s"}]

tellraw @a [{"text":"===== 检测完成 =====","color":"gold"}]
