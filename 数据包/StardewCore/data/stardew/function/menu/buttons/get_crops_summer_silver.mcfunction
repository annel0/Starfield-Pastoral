# data/stardew/function/menu/buttons/get_crops_summer_silver.mcfunction
# 获取所有夏季银星品质作物
# 执行者: 玩家 (@s)

# 使用loot spawn在玩家位置生成所有夏季银星品质作物
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/summer/blueberry_silver
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/summer/corn_silver
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/summer/hops_silver
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/summer/hot_pepper_silver
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/summer/melon_silver
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/summer/poppy_silver
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/summer/radish_silver
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/summer/red_cabbage_silver
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/summer/starfruit_silver
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/summer/summer_spangle_silver
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/summer/tomato_silver
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/summer/wheat_silver

# 提示信息
tellraw @s [{"text":"[物品获取] ","color":"gold","bold":true},{"text":"已获取所有夏季银星品质作物!","color":"green"}]
playsound entity.item.pickup player @s ~ ~ ~ 1 1
