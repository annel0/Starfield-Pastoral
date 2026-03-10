# data/stardew/function/menu/buttons/debug_mode.mcfunction
# 作弊模式按钮点击处理
# 执行者: 玩家 (@s)

# 1. 检查权限
execute unless entity @s[tag=sd_debug] run tellraw @s {"text":"您无权开启作弊模式！","color":"red","bold":true}
execute unless entity @s[tag=sd_debug] run playsound entity.villager.no player @s ~ ~ ~ 1 1
execute unless entity @s[tag=sd_debug] run return 0

# 2. 有权限,打开debug菜单
tag @s add sd_menu_opener
function stardew:menu/pages/debug_main
tag @s remove sd_menu_opener
