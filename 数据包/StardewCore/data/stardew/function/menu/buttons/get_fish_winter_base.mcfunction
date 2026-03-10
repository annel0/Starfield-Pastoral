# data/stardew/function/menu/buttons/get_fish_winter_base.mcfunction
# 获取所有冬季普通品质鱼类
# 执行者: 玩家 (@s)

# 使用loot spawn在玩家位置生成所有冬季普通品质鱼类
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/winter/albacore_base
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/winter/legend_glacier_base
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/winter/lingcod_base
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/winter/perch_base
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/winter/squid_base

# 提示信息
tellraw @s [{"text":"[物品获取] ","color":"gold","bold":true},{"text":"已获取所有冬季普通品质鱼类!","color":"green"}]
playsound entity.item.pickup player @s ~ ~ ~ 1 1
