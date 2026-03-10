# data/stardew/function/menu/buttons/get_fish_spring_silver.mcfunction
# 获取所有春季银星品质鱼类
# 执行者: 玩家 (@s)

# 使用loot spawn在玩家位置生成所有春季银星品质鱼类
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/spring/anchovy_silver
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/spring/bullhead_silver
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/spring/catfish_silver
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/spring/eel_silver
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/spring/halibut_silver
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/spring/legend_crimson_silver
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/spring/sardine_silver
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/spring/shad_silver
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/spring/smallmouth_silver
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/spring/sunfish_silver

# 提示信息
tellraw @s [{"text":"[物品获取] ","color":"gold","bold":true},{"text":"已获取所有春季银星品质鱼类!","color":"green"}]
playsound entity.item.pickup player @s ~ ~ ~ 1 1
