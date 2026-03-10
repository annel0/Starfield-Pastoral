# data/stardew/function/menu/buttons/get_crops_winter_diamond.mcfunction
# 获取所有冬季钻石星品质作物
# 执行者: 玩家 (@s)

# 使用loot spawn在玩家位置生成所有冬季钻石星品质作物
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/winter/ancient_fruit_diamond
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/winter/crystal_fruit_diamond
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/winter/snow_yam_diamond
execute at @s run loot spawn ~ ~ ~ loot stardew:items/crops/winter/winter_root_diamond

# 提示信息
tellraw @s [{"text":"[物品获取] ","color":"gold","bold":true},{"text":"已获取所有冬季钻石星品质作物!","color":"green"}]
playsound entity.item.pickup player @s ~ ~ ~ 1 1
