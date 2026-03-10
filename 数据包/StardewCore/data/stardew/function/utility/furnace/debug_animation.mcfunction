# data/stardew/function/utility/furnace/debug_animation.mcfunction
# 调试熔炉动画系统
# 使用方法: /function stardew:utility/furnace/debug_animation

# 1. 显示所有工作中的熔炉
tellraw @a [{"text":"=== 熔炉动画调试 ===","color":"gold","bold":true}]
tellraw @a ""

# 2. 统计工作中的熔炉数量
execute store result score #working_furnaces sd_temp if entity @e[type=interaction,tag=sd_furnace_interaction,scores={sd_furnace_state=1}]
tellraw @a [{"text":"工作中的熔炉: ","color":"yellow"},{"score":{"name":"#working_furnaces","objective":"sd_temp"},"color":"green"}]

# 3. 显示每个工作中熔炉的详细信息
execute as @e[type=interaction,tag=sd_furnace_interaction,scores={sd_furnace_state=1}] run tellraw @a [{"text":"  - 熔炉 @ ","color":"gray"},{"nbt":"Pos","entity":"@s","color":"white"},{"text":" | ID: ","color":"gray"},{"score":{"name":"@s","objective":"sd_furnace_id"},"color":"yellow"},{"text":" | 动画tick: ","color":"gray"},{"score":{"name":"@s","objective":"sd_anim_tick"},"color":"aqua"}]

# 4. 显示视觉实体信息
tellraw @a ""
execute store result score #visual_entities sd_temp if entity @e[type=item_display,tag=sd_furnace_visual]
tellraw @a [{"text":"熔炉视觉实体: ","color":"yellow"},{"score":{"name":"#visual_entities","objective":"sd_temp"},"color":"green"}]

execute as @e[type=item_display,tag=sd_furnace_visual] run tellraw @a [{"text":"  - 视觉 @ ","color":"gray"},{"nbt":"Pos","entity":"@s","color":"white"},{"text":" | ID: ","color":"gray"},{"score":{"name":"@s","objective":"sd_furnace_id"},"color":"yellow"}]

# 5. 检查动画函数调用条件
tellraw @a ""
tellraw @a [{"text":"附近16格内的玩家: ","color":"yellow"}]
execute as @e[type=interaction,tag=sd_furnace_interaction,scores={sd_furnace_state=1}] at @s run tellraw @a[distance=..16] [{"text":"  ✓ 你在熔炉 ","color":"green"},{"score":{"name":"@s","objective":"sd_furnace_id"},"color":"yellow"},{"text":" 附近","color":"green"}]

# 6. 手动触发一次动画帧更新（用于测试）
tellraw @a ""
tellraw @a [{"text":"手动触发动画更新...","color":"aqua"}]
execute as @e[type=interaction,tag=sd_furnace_interaction,scores={sd_furnace_state=1}] at @s run function stardew:utility/furnace/animate_working

tellraw @a ""
tellraw @a [{"text":"=== 调试完成 ===","color":"gold","bold":true}]
