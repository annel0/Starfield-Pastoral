# data/stardew/function/mining/highlight/apply_highlight.mcfunction
# 应用高亮逻辑 (类似菜单的 on_hover)
# 执行者: 矿石 item_display (@s)
# 执行位置: 矿石位置

# 1. 标记为被瞄准
scoreboard players set @s sd_mining_targeted 1

# 2. 如果是首次被瞄准,添加高亮效果
execute if score @s sd_mining_targeted_prev matches 0 run function stardew:mining/highlight/highlight_on
