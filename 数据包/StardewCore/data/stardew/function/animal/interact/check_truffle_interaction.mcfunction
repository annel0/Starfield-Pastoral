# ================================================================
# 星露谷物语 - 松露交互检测
# ================================================================
# 用途：检测玩家是否右键点击了松露
# 每tick运行，检测松露附近的玩家交互
# ================================================================

# 检查所有松露实体，如果有玩家在附近右键交互
execute as @e[type=item,tag=stardew.truffle] at @s if entity @a[distance=..2,predicate=stardew:is_using_item] run function stardew:animal/interact/collect_truffle