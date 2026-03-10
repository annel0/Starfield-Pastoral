# ================================================================
# 星露谷物语 - 测试猪产松露（调试用）
# ================================================================

# 给所有成年猪设置高友谊值来测试额外松露
execute as @e[type=pig,tag=stardew.animal,scores={stardew.animal.type=204,stardew.animal.age=10..}] run scoreboard players set @s stardew.animal.friendship 800

# 强制触发产松露
execute as @e[type=pig,tag=stardew.animal,scores={stardew.animal.type=204,stardew.animal.age=10..}] run function stardew:animal/produce/check_single_pig

tellraw @s ["",{"text":"[调试] ","color":"gold","bold":true},{"text":"已触发所有成年猪产松露（友谊值设为800）","color":"light_purple"}]