# data/stardew/function/menu/trigger_check.mcfunction
# [每tick执行] 检查玩家触发菜单

# 1. 启用触发器
scoreboard players enable @a sd_menu_open

# 2. 检测玩家使用触发器
execute as @a[scores={sd_menu_open=1..}] run function stardew:menu/toggle

# 3. 重置触发器
scoreboard players set @a[scores={sd_menu_open=1..}] sd_menu_open 0
