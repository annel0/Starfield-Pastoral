# data/stardew/function/menu/buttons/get_items_gems.mcfunction
# 获取所有宝石物品
# 执行者: 玩家 (@s)

# 使用loot spawn在玩家位置生成所有宝石
execute at @s run loot spawn ~ ~ ~ loot stardew:items/gems/quartz
execute at @s run loot spawn ~ ~ ~ loot stardew:items/gems/earth_crystal
execute at @s run loot spawn ~ ~ ~ loot stardew:items/gems/frozen_tear
execute at @s run loot spawn ~ ~ ~ loot stardew:items/gems/jade
execute at @s run loot spawn ~ ~ ~ loot stardew:items/gems/ruby
execute at @s run loot spawn ~ ~ ~ loot stardew:items/gems/amethyst
execute at @s run loot spawn ~ ~ ~ loot stardew:items/gems/prismatic_shard

# 提示信息
tellraw @s [{"text":"[物品获取] ","color":"gold","bold":true},{"text":"已获取所有宝石!","color":"green"}]
playsound entity.item.pickup player @s ~ ~ ~ 1 1
