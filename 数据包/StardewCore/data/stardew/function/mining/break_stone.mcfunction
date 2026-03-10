# stardew:mining/break_stone.mcfunction
# 石头破碎逻辑
# 执行者: interaction 实体 (@s)

# 1. 视觉与听觉效果
playsound minecraft:block.stone.break master @a ~ ~ ~ 1 0.8
particle minecraft:explosion ~ ~0.5 ~ 0.15 0.15 0.15 0.02 1 normal

# 1.5 清除屏障方块
setblock ~ ~ ~ minecraft:air

# 2. 掉落物生成 (在方块上方生成)
# 将破坏者(玩家)的挖矿等级存到临时变量
execute store result score #mining_lvl sd_temp_val run scoreboard players get @p sd_mining_lvl

# 使用tag检测类型,更简单可靠
# 石头类 - 固定掉落1个
execute if entity @s[tag=sd_type_stone] run summon minecraft:item ~ ~1 ~ {Item:{id:"minecraft:paper",count:1,components:{"minecraft:custom_model_data":7001,"minecraft:max_stack_size":99,"minecraft:custom_name":'{"text":"石头","color":"white","bold":false,"italic":false}',"minecraft:lore":['{"text":"售价：","color":"white","italic":false,"extra":[{"text":"1G","color":"gold","bold":true,"italic":false}]}','{"text":"物品种类：","color":"white","italic":false,"extra":[{"text":"资源","color":"yellow","bold":true,"italic":false}]}','{"text":"基础建筑材料。","color":"gray","italic":false}'],"minecraft:custom_data":{stardew_item:1,item_type:"resource",resource_type:"stone",sd_price:1}}}}

# 矿物类 - 随机掉落数量 (基于挖矿等级)
execute if entity @s[tag=sd_type_coal] run function stardew:mining/drop_ore_loot {ore_type:"coal",loot_path:"stardew:items/resource/coal"}
execute if entity @s[tag=sd_type_copper] run function stardew:mining/drop_ore_loot {ore_type:"copper",loot_path:"stardew:items/resource/copper_ore"}
execute if entity @s[tag=sd_type_iron] run function stardew:mining/drop_ore_loot {ore_type:"iron",loot_path:"stardew:items/resource/iron_ore"}
execute if entity @s[tag=sd_type_gold] run function stardew:mining/drop_ore_loot {ore_type:"gold",loot_path:"stardew:items/resource/gold_ore"}
execute if entity @s[tag=sd_type_diamond] run function stardew:mining/drop_ore_loot {ore_type:"diamond",loot_path:"stardew:items/resource/diamond"}

# 宝石类 (默认掉落未鉴定版本)
execute if entity @s[tag=sd_type_quartz] run loot spawn ~ ~1 ~ loot stardew:items/gems/quartz_unknown
execute if entity @s[tag=sd_type_earth_crystal] run loot spawn ~ ~1 ~ loot stardew:items/gems/earth_crystal_unknown
execute if entity @s[tag=sd_type_frozen_tear] run loot spawn ~ ~1 ~ loot stardew:items/gems/frozen_tear_unknown
execute if entity @s[tag=sd_type_jade] run loot spawn ~ ~1 ~ loot stardew:items/gems/jade_unknown
execute if entity @s[tag=sd_type_ruby] run loot spawn ~ ~1 ~ loot stardew:items/gems/ruby_unknown
execute if entity @s[tag=sd_type_amethyst] run loot spawn ~ ~1 ~ loot stardew:items/gems/amethyst_unknown
execute if entity @s[tag=sd_type_prismatic_shard] run loot spawn ~ ~1 ~ loot stardew:items/gems/prismatic_shard_unknown

# 3. 给予经验 (给予最近的玩家)
execute if entity @s[tag=sd_type_stone] run scoreboard players add @p sd_mining_xp 1
execute if entity @s[tag=sd_type_coal] run scoreboard players add @p sd_mining_xp 5
execute if entity @s[tag=sd_type_copper] run scoreboard players add @p sd_mining_xp 5
execute if entity @s[tag=sd_type_iron] run scoreboard players add @p sd_mining_xp 10
execute if entity @s[tag=sd_type_gold] run scoreboard players add @p sd_mining_xp 15
execute if entity @s[tag=sd_type_diamond] run scoreboard players add @p sd_mining_xp 25

# 宝石类经验
execute if entity @s[tag=sd_type_quartz] run scoreboard players add @p sd_mining_xp 25
execute if entity @s[tag=sd_type_earth_crystal] run scoreboard players add @p sd_mining_xp 25
execute if entity @s[tag=sd_type_frozen_tear] run scoreboard players add @p sd_mining_xp 25
execute if entity @s[tag=sd_type_jade] run scoreboard players add @p sd_mining_xp 25
execute if entity @s[tag=sd_type_ruby] run scoreboard players add @p sd_mining_xp 25
execute if entity @s[tag=sd_type_amethyst] run scoreboard players add @p sd_mining_xp 25
execute if entity @s[tag=sd_type_prismatic_shard] run scoreboard players add @p sd_mining_xp 50

# 3.5 矿洞梯子检测 (如果是矿洞中的石头)
execute if entity @s[tag=sd_mine_stone] run function stardew:mine/ladder/try_spawn

# 4. 移除实体
# 移除关联的 item_display (距离最近的 sd_stone_display)
kill @e[type=item_display,tag=sd_stone_display,distance=..1,limit=1,sort=nearest]
# 移除自身
kill @s
