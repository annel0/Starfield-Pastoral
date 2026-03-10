# ================================================================
# 星露谷物语 - 建筑交互管理器
# ================================================================
# 用途：每tick检测玩家与建筑的交互
# 调用：从主tick调用

# 处理所有有交互数据的建筑交互实体
execute as @e[type=interaction,tag=stardew.building.interaction] if data entity @s interaction run function stardew:building/interact/on_clicked
