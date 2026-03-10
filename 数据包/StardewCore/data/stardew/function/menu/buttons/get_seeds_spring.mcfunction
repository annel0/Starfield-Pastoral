# data/stardew/function/menu/buttons/get_seeds_spring.mcfunction
# 获取所有春季作物种子
# 执行者: 玩家 (@s)

# 使用loot spawn在玩家位置生成所有春季作物种子
execute at @s run loot spawn ~ ~ ~ loot stardew:items/seeds/crop_parsnip
execute at @s run loot spawn ~ ~ ~ loot stardew:items/seeds/crop_green_bean
execute at @s run loot spawn ~ ~ ~ loot stardew:items/seeds/crop_cauliflower
execute at @s run loot spawn ~ ~ ~ loot stardew:items/seeds/crop_potato
execute at @s run loot spawn ~ ~ ~ loot stardew:items/seeds/crop_tulip
execute at @s run loot spawn ~ ~ ~ loot stardew:items/seeds/crop_kale
execute at @s run loot spawn ~ ~ ~ loot stardew:items/seeds/crop_blue_jazz
execute at @s run loot spawn ~ ~ ~ loot stardew:items/seeds/crop_garlic
execute at @s run loot spawn ~ ~ ~ loot stardew:items/seeds/crop_coffee
execute at @s run loot spawn ~ ~ ~ loot stardew:items/seeds/crop_strawberry
execute at @s run loot spawn ~ ~ ~ loot stardew:items/seeds/crop_carrot
execute at @s run loot spawn ~ ~ ~ loot stardew:items/seeds/crop_rhubarb

# 提示信息
tellraw @s [{"text":"[物品获取] ","color":"gold","bold":true},{"text":"已获取所有春季作物种子!","color":"green"}]
playsound entity.item.pickup player @s ~ ~ ~ 1 1
