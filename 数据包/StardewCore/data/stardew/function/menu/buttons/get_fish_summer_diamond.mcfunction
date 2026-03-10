# data/stardew/function/menu/buttons/get_fish_summer_diamond.mcfunction
# 获取所有夏季钻石星品质鱼类
# 执行者: 玩家 (@s)

# 使用loot spawn在玩家位置生成所有夏季钻石星品质鱼类
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/summer/dorado_diamond
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/summer/legend_angler_diamond
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/summer/octopus_diamond
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/summer/pike_diamond
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/summer/pufferfish_diamond
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/summer/rainbow_trout_diamond
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/summer/red_mullet_diamond
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/summer/sturgeon_diamond
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/summer/super_cucumber_diamond
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/summer/tilapia_diamond
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/summer/tuna_diamond

# 提示信息
tellraw @s [{"text":"[物品获取] ","color":"gold","bold":true},{"text":"已获取所有夏季钻石星品质鱼类!","color":"green"}]
playsound entity.item.pickup player @s ~ ~ ~ 1 1
