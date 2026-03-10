# data/stardew/function/menu/buttons/get_crops_spring_silver.mcfunction
# 获取所有春季银星品质作物
# 执行者: 玩家 (@s)

# 使用loot spawn在玩家位置生成所有春季银星品质作物
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/spring/parsnip_silver
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/spring/green_bean_silver
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/spring/cauliflower_silver
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/spring/potato_silver
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/spring/strawberry_silver
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/spring/kale_silver
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/spring/tulip_silver
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/spring/garlic_silver
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/spring/coffee_bean_silver
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/spring/blue_jazz_silver

# 提示信息
tellraw @s [{"text":"[物品获取] ","color":"gold","bold":true},{"text":"已获取所有春季银星品质作物!","color":"green"}]
playsound entity.item.pickup player @s ~ ~ ~ 1 1
