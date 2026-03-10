# data/stardew/function/utility/tapper/cleanup_tapper.mcfunction
# 清理提取器实体（当树被砍倒时）
# 执行者: 树interaction实体 (@s)

# 1. 检查树是否有提取器
execute unless score @s sd_tapper_state matches 1.. run return 0

# 2. 获取提取器ID
scoreboard players operation #cleanup_id sd_tapper_id = @s sd_tapper_id

# 3. 删除所有相关实体（视觉实体、产物、文本）
execute as @e[type=item_display,tag=sd_tapper_visual] if score @s sd_tapper_id = #cleanup_id sd_tapper_id run kill @s
execute as @e[type=item_display,tag=sd_tapper_product] if score @s sd_tapper_id = #cleanup_id sd_tapper_id run kill @s
execute as @e[type=text_display,tag=sd_tapper_time] if score @s sd_tapper_id = #cleanup_id sd_tapper_id run kill @s

# 4. 重置树的提取器状态
tag @s remove sd_has_tapper
scoreboard players set @s sd_tapper_state 0
scoreboard players set @s sd_tapper_type 0
scoreboard players set @s sd_tapper_timer 0
scoreboard players set @s sd_tapper_max_time 0
scoreboard players set @s sd_utility_active 0
