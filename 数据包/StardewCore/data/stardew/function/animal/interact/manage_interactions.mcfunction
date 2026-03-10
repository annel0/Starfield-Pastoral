# ================================================================
# 星露谷物语 - 交互实体系统管理
# ================================================================
# 用途：管理所有interaction实体的位置同步和点击检测
# 注意：不在这里创建interaction，创建应该在动物生成时完成
# ================================================================

# 1. 同步所有interaction实体位置到对应动物位置
execute as @e[type=interaction,tag=stardew.animal.interaction] at @s run function stardew:animal/interact/sync_interaction_position

# 2. 检测玩家是否右键了interaction（只检测interaction，不检测attack）
execute as @e[type=interaction,tag=stardew.animal.interaction] at @s if data entity @s interaction run function stardew:animal/interact/on_interaction_clicked

# 3. 为新动物创建interaction（仅在tick中检查是否有遗漏的）
execute as @e[type=#stardew:animals,tag=stardew.animal,tag=!stardew.animal.has_interaction] at @s run function stardew:animal/interact/create_interaction
