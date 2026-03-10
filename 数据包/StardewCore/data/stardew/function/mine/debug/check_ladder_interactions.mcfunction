# stardew:mine/debug/check_ladder_interactions.mcfunction
# 详细检查梯子交互实体状态

tellraw @s {"text":"===== 检查梯子交互实体 =====","color":"gold"}

# 显示玩家位置
tellraw @s [{"text":"你的位置: ","color":"gray"},{"text":"X="},{"nbt":"Pos[0]","entity":"@s","color":"aqua"},{"text":" Y="},{"nbt":"Pos[1]","entity":"@s","color":"aqua"},{"text":" Z="},{"nbt":"Pos[2]","entity":"@s","color":"aqua"}]
tellraw @s [{"text":"你的维度: ","color":"gray"},{"nbt":"Dimension","entity":"@s","color":"aqua"}]

# 列出 3 格内的所有交互实体
tellraw @s {"text":"--- 3格内的交互实体 ---","color":"yellow"}
execute as @e[type=interaction,distance=..3] run tellraw @a [{"text":"  交互实体: ","color":"gray"},{"nbt":"Tags","entity":"@s","color":"white"},{"text":" @ "},{"nbt":"Pos","entity":"@s","color":"aqua"}]
execute as @e[type=interaction,distance=..3] run tellraw @a [{"text":"    interaction数据: ","color":"gray"},{"nbt":"interaction","entity":"@s","color":"white"}]

# 列出所有出口梯子
tellraw @s {"text":"--- 所有出口梯子实体 (sd_mine_ladder_exit) ---","color":"yellow"}
execute as @e[type=interaction,tag=sd_mine_ladder_exit] run tellraw @a [{"text":"  位置: ","color":"gray"},{"nbt":"Pos","entity":"@s","color":"aqua"},{"text":" 有交互: "},{"nbt":"interaction","entity":"@s","color":"white"}]

# 统计
execute store result score #exit_count sd_mine_temp run execute if entity @e[type=interaction,tag=sd_mine_ladder_exit]
tellraw @s [{"text":"出口梯子总数: ","color":"gray"},{"score":{"name":"#exit_count","objective":"sd_mine_temp"},"color":"aqua"}]

# 检查第一层特定位置 (0, 65, 102)
tellraw @s {"text":"--- 检查第一层出口 (0, 65, 102) ---","color":"yellow"}
execute in stardew:mine positioned 0 65 102 if entity @e[type=interaction,tag=sd_mine_ladder_exit,distance=..1] run tellraw @s {"text":"✓ 在 (0,65,102) 找到出口梯子实体","color":"green"}
execute in stardew:mine positioned 0 65 102 unless entity @e[type=interaction,tag=sd_mine_ladder_exit,distance=..1] run tellraw @s {"text":"✗ 在 (0,65,102) 没有出口梯子实体!","color":"red"}
execute in stardew:mine positioned 0 65 102 as @e[type=interaction,distance=..1] run tellraw @a [{"text":"  附近实体: ","color":"gray"},{"nbt":"Tags","entity":"@s"}]

# 检查第二层特定位置 (0, 65, 202)
tellraw @s {"text":"--- 检查第二层出口 (0, 65, 202) ---","color":"yellow"}
execute in stardew:mine positioned 0 65 202 if entity @e[type=interaction,tag=sd_mine_ladder_exit,distance=..1] run tellraw @s {"text":"✓ 在 (0,65,202) 找到出口梯子实体","color":"green"}
execute in stardew:mine positioned 0 65 202 unless entity @e[type=interaction,tag=sd_mine_ladder_exit,distance=..1] run tellraw @s {"text":"✗ 在 (0,65,202) 没有出口梯子实体!","color":"red"}

tellraw @s {"text":"===== 检查完成 =====","color":"gold"}
