# data/stardew/function/menu/buttons/get_fish_special_diamond.mcfunction
# 获取所有特殊钻石星品质鱼类
# 执行者: 玩家 (@s)

# 使用loot spawn在玩家位置生成所有特殊钻石星品质鱼类
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/special/ghostfish_diamond
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/special/ice_pip_diamond
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/special/lava_eel_diamond
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/special/sandfish_diamond
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/special/scorpion_carp_diamond
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/special/stonefish_diamond

# 提示信息
tellraw @s [{"text":"[物品获取] ","color":"gold","bold":true},{"text":"已获取所有特殊钻石星品质鱼类!","color":"green"}]
playsound entity.item.pickup player @s ~ ~ ~ 1 1
