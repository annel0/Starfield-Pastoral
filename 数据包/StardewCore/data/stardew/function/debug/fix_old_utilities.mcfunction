# =========================================================
# 修复旧设施的高亮系统
# =========================================================
# 用法: /function stardew:debug/fix_old_utilities
# 说明: 为所有已存在的设施视觉实体初始化高亮分数
# =========================================================

# 统计需要修复的设施数量
execute store result score #utility_count sd_temp if entity @e[type=item_display,tag=sd_utility]

# 为所有设施视觉实体初始化高亮分数
execute as @e[type=item_display,tag=sd_utility] run function stardew:utility/init_highlight

# 提示
tellraw @s [{"text":"[修复] ","color":"green"},{"text":"已为 ","color":"gray"},{"score":{"name":"#utility_count","objective":"sd_temp"},"color":"yellow"},{"text":" 个设施初始化高亮系统","color":"gray"}]
