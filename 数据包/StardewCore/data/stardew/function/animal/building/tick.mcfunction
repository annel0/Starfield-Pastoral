# ================================================================
# 星露谷物语 - 建筑系统主循环
# ================================================================
# 用途：管理所有畜棚和鸡舍
# ================================================================

# 检测建筑交互（开关门）
function stardew:building/interact/manage_interactions

# 动物范围限制（门控制）
function stardew:building/animal/manage_range

# 动物回家移动（持续处理所有正在回家的动物）
execute as @e[type=#stardew:animals,tag=stardew.animal.going_home] at @s run function stardew:building/animal/process_going_home
