# data/stardew/function/menu/buttons/get_fish_fall_diamond.mcfunction
# 获取所有秋季钻石星品质鱼类
# 执行者: 玩家 (@s)

# 使用loot spawn在玩家位置生成所有秋季钻石星品质鱼类
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/fall/largemouth_bass_diamond
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/fall/legend_anglerfish_diamond
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/fall/midnight_carp_diamond
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/fall/red_snapper_diamond
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/fall/salmon_diamond
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/fall/sea_cucumber_diamond
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/fall/sea_eel_diamond
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/fall/tiger_trout_diamond
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/fall/walleye_diamond

# 提示信息
tellraw @s [{"text":"[物品获取] ","color":"gold","bold":true},{"text":"已获取所有秋季钻石星品质鱼类!","color":"green"}]
playsound entity.item.pickup player @s ~ ~ ~ 1 1
