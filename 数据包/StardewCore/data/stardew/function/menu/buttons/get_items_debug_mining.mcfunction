# data/stardew/function/menu/buttons/get_items_debug_mining.mcfunction
# 获取所有矿物生成器物品
# 执行者: 玩家 (@s)

# 使用loot spawn在玩家位置生成所有矿脉生成器和宝石生成器
# 石头矿脉生成器 (4个主题)
execute at @s run loot spawn ~ ~ ~ loot stardew:items/debug_mining/stone_theme1
execute at @s run loot spawn ~ ~ ~ loot stardew:items/debug_mining/stone_theme2
execute at @s run loot spawn ~ ~ ~ loot stardew:items/debug_mining/stone_theme3
execute at @s run loot spawn ~ ~ ~ loot stardew:items/debug_mining/stone_theme4

# 煤炭矿脉生成器 (4个主题)
execute at @s run loot spawn ~ ~ ~ loot stardew:items/debug_mining/coal_theme1
execute at @s run loot spawn ~ ~ ~ loot stardew:items/debug_mining/coal_theme2
execute at @s run loot spawn ~ ~ ~ loot stardew:items/debug_mining/coal_theme3
execute at @s run loot spawn ~ ~ ~ loot stardew:items/debug_mining/coal_theme4

# 铜矿脉生成器 (4个主题)
execute at @s run loot spawn ~ ~ ~ loot stardew:items/debug_mining/copper_theme1
execute at @s run loot spawn ~ ~ ~ loot stardew:items/debug_mining/copper_theme2
execute at @s run loot spawn ~ ~ ~ loot stardew:items/debug_mining/copper_theme3
execute at @s run loot spawn ~ ~ ~ loot stardew:items/debug_mining/copper_theme4

# 铁矿脉生成器 (4个主题)
execute at @s run loot spawn ~ ~ ~ loot stardew:items/debug_mining/iron_theme1
execute at @s run loot spawn ~ ~ ~ loot stardew:items/debug_mining/iron_theme2
execute at @s run loot spawn ~ ~ ~ loot stardew:items/debug_mining/iron_theme3
execute at @s run loot spawn ~ ~ ~ loot stardew:items/debug_mining/iron_theme4

# 金矿脉生成器 (4个主题)
execute at @s run loot spawn ~ ~ ~ loot stardew:items/debug_mining/gold_theme1
execute at @s run loot spawn ~ ~ ~ loot stardew:items/debug_mining/gold_theme2
execute at @s run loot spawn ~ ~ ~ loot stardew:items/debug_mining/gold_theme3
execute at @s run loot spawn ~ ~ ~ loot stardew:items/debug_mining/gold_theme4

# 钻石矿脉生成器 (4个主题)
execute at @s run loot spawn ~ ~ ~ loot stardew:items/debug_mining/diamond_theme1
execute at @s run loot spawn ~ ~ ~ loot stardew:items/debug_mining/diamond_theme2
execute at @s run loot spawn ~ ~ ~ loot stardew:items/debug_mining/diamond_theme3
execute at @s run loot spawn ~ ~ ~ loot stardew:items/debug_mining/diamond_theme4

# 宝石生成器 (7种)
execute at @s run loot spawn ~ ~ ~ loot stardew:items/debug_mining/gem_quartz
execute at @s run loot spawn ~ ~ ~ loot stardew:items/debug_mining/gem_earth_crystal
execute at @s run loot spawn ~ ~ ~ loot stardew:items/debug_mining/gem_frozen_tear
execute at @s run loot spawn ~ ~ ~ loot stardew:items/debug_mining/gem_jade
execute at @s run loot spawn ~ ~ ~ loot stardew:items/debug_mining/gem_ruby
execute at @s run loot spawn ~ ~ ~ loot stardew:items/debug_mining/gem_amethyst
execute at @s run loot spawn ~ ~ ~ loot stardew:items/debug_mining/gem_prismatic_shard

# 提示信息
tellraw @s [{"text":"[物品获取] ","color":"gold","bold":true},{"text":"已获取所有矿物生成器!","color":"green"}]
playsound entity.item.pickup player @s ~ ~ ~ 1 1
