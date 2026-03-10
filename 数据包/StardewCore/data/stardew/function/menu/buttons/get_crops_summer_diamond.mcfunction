# data/stardew/function/menu/buttons/get_crops_summer_diamond.mcfunction
# 获取所有夏季钻石星品质作物
# 执行者: 玩家 (@s)

# 使用loot spawn在玩家位置生成所有夏季钻石星品质作物
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/summer/blueberry_diamond
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/summer/corn_diamond
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/summer/hops_diamond
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/summer/hot_pepper_diamond
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/summer/melon_diamond
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/summer/poppy_diamond
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/summer/radish_diamond
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/summer/red_cabbage_diamond
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/summer/starfruit_diamond
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/summer/summer_spangle_diamond
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/summer/tomato_diamond
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/summer/wheat_diamond

# 提示信息
tellraw @s [{"text":"[物品获取] ","color":"gold","bold":true},{"text":"已获取所有夏季钻石星品质作物!","color":"green"}]
playsound entity.item.pickup player @s ~ ~ ~ 1 1
