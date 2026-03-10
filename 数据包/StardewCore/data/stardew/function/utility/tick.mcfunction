 # data/stardew/function/utility/tick.mcfunction
# 实用设施系统 tick 处理

# 1. 高亮检测 - 玩家拿镐子指向实用设施时显示高亮
execute as @a at @s run function stardew:utility/highlight/raycast_start

# 2. 更新高亮状态 - 移除不再被瞄准的实用设施的高亮
execute as @e[type=item_display,tag=sd_utility,scores={sd_utility_targeted_prev=1}] unless score @s sd_utility_targeted matches 1 at @s run function stardew:utility/highlight/highlight_off

# 3. 保存当前瞄准状态作为"前一帧状态"
execute as @e[type=item_display,tag=sd_utility] run scoreboard players operation @s sd_utility_targeted_prev = @s sd_utility_targeted

# 4. 重置当前帧的瞄准状态
execute as @e[type=item_display,tag=sd_utility] run scoreboard players set @s sd_utility_targeted 0

# 5. 检测交互 - 熔炉
execute as @e[type=interaction,tag=sd_furnace_interaction] at @s run function stardew:utility/furnace/check_interaction

# 6. 熔炉工作处理
execute as @e[type=interaction,tag=sd_furnace_interaction,scores={sd_furnace_state=1}] at @s run function stardew:utility/furnace/work_tick

# 7. 熔炉工作动画（仅玩家附近16格内）
execute as @e[type=interaction,tag=sd_furnace_interaction,scores={sd_furnace_state=1}] at @s if entity @a[distance=..16] run function stardew:utility/furnace/animate_working

# 8. 检测交互 - 小桶
execute as @e[type=interaction,tag=sd_keg_interaction] at @s run function stardew:utility/keg/check_interaction

# 9. 小桶工作处理
execute as @e[type=interaction,tag=sd_keg_interaction,scores={sd_keg_state=1}] at @s run function stardew:utility/keg/work_tick

# 10. 小桶工作动画（仅玩家附近16格内）
execute as @e[type=interaction,tag=sd_keg_interaction,scores={sd_keg_state=1}] at @s if entity @a[distance=..16] run function stardew:utility/keg/animate_working

# 11. 检测交互 - 箱子
execute as @e[type=interaction,tag=sd_chest_interaction] at @s run function stardew:utility/chest/check_interaction

# 11.5 检测交互 - 洒水器
execute as @e[type=interaction,tag=sd_sprinkler_interaction] at @s run function stardew:utility/sprinkler/check_interaction

# 12. 树液提取器工作处理（在树上，使用树的interaction实体）
execute as @e[type=interaction,tag=sd_tree,scores={sd_tapper_state=1}] at @s run function stardew:utility/tapper/work_tick
