# =========================================================
# Debug: 强制触发新的一天
# =========================================================
# 用法: /function stardew:debug/force_new_day
# 功能: 手动触发 new_day 函数,用于测试洒水器等系统
# =========================================================

tellraw @s [{"text":"[Debug] ","color":"aqua"},{"text":"强制触发新的一天...","color":"yellow"}]

# 调用 new_day 函数
function stardew:time/new_day

tellraw @s [{"text":"[Debug] ","color":"aqua"},{"text":"完成!","color":"green"}]
