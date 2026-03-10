# data/stardew/function/menu/buttons/get_fish_fall_base.mcfunction
# 获取所有秋季普通品质鱼类
# 执行者: 玩家 (@s)

# 使用loot spawn在玩家位置生成所有秋季普通品质鱼类
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/fall/largemouth_bass_base
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/fall/legend_anglerfish_base
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/fall/midnight_carp_base
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/fall/red_snapper_base
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/fall/salmon_base
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/fall/sea_cucumber_base
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/fall/sea_eel_base
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/fall/tiger_trout_base
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/fall/walleye_base

# 提示信息
tellraw @s [{"text":"[物品获取] ","color":"gold","bold":true},{"text":"已获取所有秋季普通品质鱼类!","color":"green"}]
playsound entity.item.pickup player @s ~ ~ ~ 1 1
