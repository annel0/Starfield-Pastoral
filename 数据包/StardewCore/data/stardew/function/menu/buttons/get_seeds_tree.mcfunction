# data/stardew/function/menu/buttons/get_seeds_tree.mcfunction
# 获取所有树木种子
# 执行者: 玩家 (@s)

# 使用loot spawn在玩家位置生成所有树木种子
execute at @s run loot spawn ~ ~ ~ loot stardew:items/seeds/tree_oak
execute at @s run loot spawn ~ ~ ~ loot stardew:items/seeds/tree_maple
execute at @s run loot spawn ~ ~ ~ loot stardew:items/seeds/tree_pine
execute at @s run loot spawn ~ ~ ~ loot stardew:items/seeds/tree_mahogany

# 提示信息
tellraw @s [{"text":"[物品获取] ","color":"gold","bold":true},{"text":"已获取所有树木种子!","color":"green"}]
playsound entity.item.pickup player @s ~ ~ ~ 1 1
