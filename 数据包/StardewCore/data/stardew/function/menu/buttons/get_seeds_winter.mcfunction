# data/stardew/function/menu/buttons/get_seeds_winter.mcfunction
# 获取所有冬季作物种子
# 执行者: 玩家 (@s)

# 使用loot spawn在玩家位置生成所有冬季作物种子（冬季种子包）
execute at @s run loot spawn ~ ~ ~ loot stardew:items/seeds/crop_winter_seeds

# 提示信息
tellraw @s [{"text":"[物品获取] ","color":"gold","bold":true},{"text":"已获取冬季种子包!","color":"green"}]
playsound entity.item.pickup player @s ~ ~ ~ 1 1
