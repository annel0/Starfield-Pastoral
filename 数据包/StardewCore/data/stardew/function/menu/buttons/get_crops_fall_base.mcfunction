# data/stardew/function/menu/buttons/get_crops_fall_base.mcfunction
# 获取所有秋季普通品质作物
# 执行者: 玩家 (@s)

# 使用loot spawn在玩家位置生成所有秋季普通品质作物
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/fall/amaranth_base
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/fall/artichoke_base
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/fall/bok_choy_base
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/fall/cranberry_base
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/fall/eggplant_base
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/fall/fairy_rose_base
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/fall/grape_base
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/fall/pumpkin_base
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/fall/sunflower_base
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/fall/yam_base

# 提示信息
tellraw @s [{"text":"[物品获取] ","color":"gold","bold":true},{"text":"已获取所有秋季普通品质作物!","color":"green"}]
playsound entity.item.pickup player @s ~ ~ ~ 1 1
