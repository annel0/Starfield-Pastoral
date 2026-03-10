# data/stardew/function/menu/buttons/get_fish_special_base.mcfunction
# 获取所有特殊普通品质鱼类
# 执行者: 玩家 (@s)

# 使用loot spawn在玩家位置生成所有特殊普通品质鱼类
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/special/ghostfish_base
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/special/ice_pip_base
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/special/lava_eel_base
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/special/sandfish_base
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/special/scorpion_carp_base
execute at @s run loot spawn ~ ~ ~ loot stardew:items/fish/special/stonefish_base

# 提示信息
tellraw @s [{"text":"[物品获取] ","color":"gold","bold":true},{"text":"已获取所有特殊普通品质鱼类!","color":"green"}]
playsound entity.item.pickup player @s ~ ~ ~ 1 1
