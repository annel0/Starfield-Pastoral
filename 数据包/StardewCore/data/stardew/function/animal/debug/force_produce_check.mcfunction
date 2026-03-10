# ================================================================
# 星露谷物语 - 强制触发产蛋检查
# ================================================================
# 用途：立即触发产蛋判定（不需要等new_day）
# 调用：手动执行 /function stardew:animal/debug/force_produce_check

tellraw @a [{"text":"[调试] ","color":"yellow"},{"text":"正在执行产蛋检查...","color":"aqua"}]

# 执行产蛋检查
function stardew:animal/produce/check_chicken_produce

tellraw @a [{"text":"[调试] ","color":"yellow"},{"text":"产蛋检查完成！检查建筑内是否有鸡蛋实体","color":"green"}]
