# data/stardew/function/menu/buttons/get_crops_summer_gold.mcfunction
# 获取所有夏季金星品质作物
# 执行者: 玩家 (@s)

# 使用loot spawn在玩家位置生成所有夏季金星品质作物
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/summer/blueberry_gold
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/summer/corn_gold
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/summer/hops_gold
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/summer/hot_pepper_gold
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/summer/melon_gold
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/summer/poppy_gold
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/summer/radish_gold
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/summer/red_cabbage_gold
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/summer/starfruit_gold
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/summer/summer_spangle_gold
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/summer/tomato_gold
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/summer/wheat_gold

# 提示信息
tellraw @s [{"text":"[物品获取] ","color":"gold","bold":true},{"text":"已获取所有夏季金星品质作物!","color":"green"}]
playsound entity.item.pickup player @s ~ ~ ~ 1 1
