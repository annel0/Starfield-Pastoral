# ================================================================
# 星露谷物语 - 松露拾取检测
# ================================================================
# 用途：检测玩家是否右键了松露
# 调用：从 animal/core/tick.mcfunction 调用

# 检测所有被右键的松露交互体
execute as @e[type=interaction,tag=stardew.truffle.interaction] at @s if data entity @s interaction run function stardew:animal/produce/pickup_truffle