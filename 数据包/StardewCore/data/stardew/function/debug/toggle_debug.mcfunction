# =========================================================
# Debug: 切换 Debug 模式
# =========================================================
# 用法: /function stardew:debug/toggle_debug
# 功能: 给玩家添加/移除 sd_debug 标签,开启/关闭 debug 信息
# =========================================================

# 保存当前状态
tag @s[tag=sd_debug] add sd_was_debug

# 切换 debug 标签
tag @s[tag=!sd_was_debug] add sd_debug
tag @s[tag=sd_was_debug] remove sd_debug

# 提示
tellraw @s[tag=sd_debug] [{"text":"[Debug] ","color":"aqua"},{"text":"Debug 模式已开启","color":"green"}]
tellraw @s[tag=!sd_debug] [{"text":"[Debug] ","color":"aqua"},{"text":"Debug 模式已关闭","color":"gray"}]

# 清除临时标签
tag @s remove sd_was_debug
