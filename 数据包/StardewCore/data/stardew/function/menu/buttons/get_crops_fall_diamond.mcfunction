# data/stardew/function/menu/buttons/get_crops_fall_diamond.mcfunction
# 获取所有秋季钻石星品质作物
# 执行者: 玩家 (@s)

# 使用loot spawn在玩家位置生成所有秋季钻石星品质作物
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/fall/amaranth_diamond
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/fall/artichoke_diamond
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/fall/bok_choy_diamond
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/fall/cranberry_diamond
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/fall/eggplant_diamond
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/fall/fairy_rose_diamond
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/fall/grape_diamond
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/fall/pumpkin_diamond
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/fall/sunflower_diamond
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/fall/yam_diamond

# 提示信息
tellraw @s [{"text":"[物品获取] ","color":"gold","bold":true},{"text":"已获取所有秋季钻石星品质作物!","color":"green"}]
playsound entity.item.pickup player @s ~ ~ ~ 1 1
