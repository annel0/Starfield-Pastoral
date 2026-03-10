# data/stardew/function/menu/buttons/get_items_fishing.mcfunction
# 获取所有渔具物品
# 执行者: 玩家 (@s)

# 使用loot spawn在玩家位置生成所有渔具
# 鱼竿 (4个等级)
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fishing/rod_copper
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fishing/rod_iron
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fishing/rod_gold
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fishing/rod_diamond

# 鱼饵配件 (7种)
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fishing/tackle_barbed
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fishing/tackle_cork
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fishing/tackle_quality
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fishing/tackle_sonar
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fishing/tackle_spinner
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fishing/tackle_trap
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fishing/tackle_treasure

# 提示信息
tellraw @s [{"text":"[物品获取] ","color":"gold","bold":true},{"text":"已获取所有渔具!","color":"green"}]
playsound entity.item.pickup player @s ~ ~ ~ 1 1
