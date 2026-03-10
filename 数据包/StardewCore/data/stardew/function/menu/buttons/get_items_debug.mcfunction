# data/stardew/function/menu/buttons/get_items_debug.mcfunction
# 获取所有调试工具物品
# 执行者: 玩家 (@s)

# 使用loot spawn在玩家位置生成所有调试工具
execute at @s run loot spawn ~ ~ ~ loot stardew:items/debug/fish_doctor
execute at @s run loot spawn ~ ~ ~ loot stardew:items/debug/grow_hormone
execute at @s run loot spawn ~ ~ ~ loot stardew:items/debug/time_wand
execute at @s run loot spawn ~ ~ ~ loot stardew:items/debug/weather_wand

# 提示信息
tellraw @s [{"text":"[物品获取] ","color":"gold","bold":true},{"text":"已获取所有调试工具!","color":"green"}]
playsound entity.item.pickup player @s ~ ~ ~ 1 1
