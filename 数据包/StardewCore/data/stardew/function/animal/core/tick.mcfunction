# ================================================================
# 星露谷物语 - 畜牧业系统主循环
# ================================================================
# 用途：每tick执行的畜牧业系统逻辑
# 调用：从 main.mcfunction 调用

# 同步 Animated Java 鸡模型
function stardew:animal/animated_java/sync_all_chickens

# 自动修复缺失模型的动物（每5秒检查一次）
execute if score Global sd_time matches 0 run function stardew:animal/visual/auto_fix_missing_models

# 检测玩家与动物的交互
function stardew:animal/interact/detect_interaction

# 检测鸡蛋拾取
function stardew:animal/produce/detect_egg_pickup

# 检测松露拾取
function stardew:animal/produce/detect_truffle_pickup

# 管理年龄和成长
function stardew:animal/data/age_manager

# 清理死亡动物（每5秒检查一次）
execute if score Global sd_time matches 0 run function stardew:animal/data/cleanup_dead_animals

# 更新动物状态显示
function stardew:animal/data/update_display

# 建筑管理
function stardew:animal/building/tick
