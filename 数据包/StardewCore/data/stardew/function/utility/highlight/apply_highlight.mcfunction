# data/stardew/function/utility/highlight/apply_highlight.mcfunction
# 应用高亮逻辑
# 执行者: 实用设施 item_display (@s)
# 执行位置: 实用设施位置

# 1. 标记为被瞄准
scoreboard players set @s sd_utility_targeted 1

# 2. 如果是首次被瞄准,添加高亮效果
execute if score @s sd_utility_targeted_prev matches 0 run function stardew:utility/highlight/highlight_on
