# ================================================================
# 星露谷物语 - 测试山羊产奶（调试用）
# ================================================================

# 给所有成年山羊设置产奶状态
execute as @e[type=sheep,tag=stardew.animal,scores={stardew.animal.type=202,stardew.animal.age=5..}] run scoreboard players set @s stardew.animal.has_produce 1

# 设置一个羊奶 CMD（普通羊奶）
execute as @e[type=sheep,tag=stardew.animal,scores={stardew.animal.type=202,stardew.animal.age=5..}] run scoreboard players set @s stardew.animal.produce_cmd 8036

tellraw @s ["",{"text":"[调试] ","color":"gold","bold":true},{"text":"已给所有成年山羊设置产奶状态","color":"yellow"}]
