# data/stardew/function/menu/buttons/get_fish_resource.mcfunction
# 获取所有钓鱼资源
# 执行者: 玩家 (@s)

# 使用loot spawn在玩家位置生成所有钓鱼资源
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/resource/green_algae_base
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/resource/seaweed_base
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/resource/white_algae_base

# 提示信息
tellraw @s [{"text":"[物品获取] ","color":"gold","bold":true},{"text":"已获取所有钓鱼资源!","color":"green"}]
playsound entity.item.pickup player @s ~ ~ ~ 1 1
