# data/stardew/function/menu/buttons/get_fish_summer_silver.mcfunction
# 获取所有夏季银星品质鱼类
# 执行者: 玩家 (@s)

# 使用loot spawn在玩家位置生成所有夏季银星品质鱼类
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/summer/dorado_silver
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/summer/legend_angler_silver
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/summer/octopus_silver
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/summer/pike_silver
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/summer/pufferfish_silver
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/summer/rainbow_trout_silver
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/summer/red_mullet_silver
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/summer/sturgeon_silver
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/summer/super_cucumber_silver
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/summer/tilapia_silver
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/summer/tuna_silver

# 提示信息
tellraw @s [{"text":"[物品获取] ","color":"gold","bold":true},{"text":"已获取所有夏季银星品质鱼类!","color":"green"}]
playsound entity.item.pickup player @s ~ ~ ~ 1 1
