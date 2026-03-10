# data/stardew/function/menu/buttons/get_tools_watering_can.mcfunction
# 获取所有水壶
# 执行者: 玩家 (@s)

# 使用loot spawn在玩家位置生成所有水壶
execute at @s run loot spawn ~ ~ ~ loot stardew:items/tools/watering_can_copper
execute at @s run loot spawn ~ ~ ~ loot stardew:items/tools/watering_can_iron
execute at @s run loot spawn ~ ~ ~ loot stardew:items/tools/watering_can_gold
execute at @s run loot spawn ~ ~ ~ loot stardew:items/tools/watering_can_diamond

# 提示信息
tellraw @s [{"text":"[物品获取] ","color":"gold","bold":true},{"text":"已获取所有水壶!","color":"green"}]
playsound entity.item.pickup player @s ~ ~ ~ 1 1
