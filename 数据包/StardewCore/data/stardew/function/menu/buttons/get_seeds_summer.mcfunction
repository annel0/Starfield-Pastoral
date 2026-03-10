# data/stardew/function/menu/buttons/get_seeds_summer.mcfunction
# 获取所有夏季作物种子
# 执行者: 玩家 (@s)

# 使用loot spawn在玩家位置生成所有夏季作物种子
execute at @s run loot spawn ~ ~ ~ loot stardew:items/seeds/crop_blueberry
execute at @s run loot spawn ~ ~ ~ loot stardew:items/seeds/crop_melon
execute at @s run loot spawn ~ ~ ~ loot stardew:items/seeds/crop_tomato
execute at @s run loot spawn ~ ~ ~ loot stardew:items/seeds/crop_hot_pepper
execute at @s run loot spawn ~ ~ ~ loot stardew:items/seeds/crop_wheat
execute at @s run loot spawn ~ ~ ~ loot stardew:items/seeds/crop_radish
execute at @s run loot spawn ~ ~ ~ loot stardew:items/seeds/crop_poppy
execute at @s run loot spawn ~ ~ ~ loot stardew:items/seeds/crop_spangle
execute at @s run loot spawn ~ ~ ~ loot stardew:items/seeds/crop_hops
execute at @s run loot spawn ~ ~ ~ loot stardew:items/seeds/crop_corn
execute at @s run loot spawn ~ ~ ~ loot stardew:items/seeds/crop_starfruit
execute at @s run loot spawn ~ ~ ~ loot stardew:items/seeds/crop_red_cabbage
execute at @s run loot spawn ~ ~ ~ loot stardew:items/seeds/crop_summer_squash

# 提示信息
tellraw @s [{"text":"[物品获取] ","color":"gold","bold":true},{"text":"已获取所有夏季作物种子!","color":"green"}]
playsound entity.item.pickup player @s ~ ~ ~ 1 1
