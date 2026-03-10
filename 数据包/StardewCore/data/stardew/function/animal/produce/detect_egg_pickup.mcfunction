# ================================================================
# 星露谷物语 - 鸡蛋拾取检测
# ================================================================
# 用途：检测玩家是否右键了鸡蛋
# 调用：从 animal/core/tick.mcfunction 调用

# 检测所有被右键的鸡蛋交互体
execute as @e[type=interaction,tag=stardew.egg.interaction] at @s if data entity @s interaction run function stardew:animal/produce/pickup_egg
