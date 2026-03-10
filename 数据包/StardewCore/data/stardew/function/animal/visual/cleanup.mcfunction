# ================================================================
# 星露谷物语 - 清理孤儿视觉实体
# ================================================================
# 用途：删除没有对应逻辑实体的视觉模型
# 调用：每5秒执行一次

# 遍历所有视觉实体，检查其对应的逻辑实体是否存在
execute as @e[type=item_display,tag=stardew.animal.visual] run function stardew:animal/visual/check_and_remove_orphan

# 为没有视觉实体的逻辑实体生成模型
execute as @e[type=#stardew:animals,tag=stardew.animal] run function stardew:animal/visual/ensure_visual
