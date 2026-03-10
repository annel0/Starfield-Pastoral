# data/stardew/function/menu/buttons/get_fish_trash.mcfunction
# 获取所有钓鱼垃圾
# 执行者: 玩家 (@s)

# 使用loot spawn在玩家位置生成所有钓鱼垃圾
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/trash/cola_base
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/trash/driftwood_base
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/trash/garbage_base
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/trash/glasses_base
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/trash/newspaper_base
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/trash/plant_base

# 提示信息
tellraw @s [{"text":"[物品获取] ","color":"gold","bold":true},{"text":"已获取所有钓鱼垃圾!","color":"green"}]
playsound entity.item.pickup player @s ~ ~ ~ 1 1
