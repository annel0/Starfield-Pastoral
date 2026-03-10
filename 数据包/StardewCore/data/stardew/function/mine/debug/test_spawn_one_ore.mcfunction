# stardew:mine/debug/test_spawn_one_ore.mcfunction
# 测试生成单个矿物

tellraw @s {"text":"===== 测试生成单个矿物 =====","color":"gold"}

# 确保在矿洞
execute unless entity @s[nbt={Dimension:"stardew:mine"}] run tellraw @s {"text":"警告: 请先进入矿洞!","color":"red"}
execute unless entity @s[nbt={Dimension:"stardew:mine"}] run return 0

# 设置层数
scoreboard players set @s sd_mine_floor 1

# 检查位置 (0, 65, 110) 的条件
tellraw @s {"text":"检查位置 (0, 65, 110):","color":"yellow"}
execute in stardew:mine if block 0 65 110 minecraft:air run tellraw @s {"text":"  ✓ Y=65 是空气","color":"green"}
execute in stardew:mine unless block 0 65 110 minecraft:air run tellraw @s {"text":"  ✗ Y=65 不是空气","color":"red"}
execute in stardew:mine if block 0 64 110 minecraft:stone run tellraw @s {"text":"  ✓ Y=64 是石头","color":"green"}
execute in stardew:mine unless block 0 64 110 minecraft:stone run tellraw @s {"text":"  ✗ Y=64 不是石头","color":"red"}

# 直接在该位置生成
tellraw @s {"text":"直接调用 spawn_random_ore...","color":"yellow"}
execute in stardew:mine positioned 0 65 110 if block ~ ~ ~ minecraft:air if block ~ ~-1 ~ minecraft:stone run function stardew:mine/ore/spawn_random_ore
execute in stardew:mine positioned 0 65 110 unless block ~ ~ ~ minecraft:air run tellraw @s {"text":"  跳过: 不是空气","color":"red"}
execute in stardew:mine positioned 0 65 110 if block ~ ~ ~ minecraft:air unless block ~ ~-1 ~ minecraft:stone run tellraw @s {"text":"  跳过: 下方不是石头","color":"red"}

# 检查结果
execute in stardew:mine if block 0 65 110 minecraft:barrier run tellraw @s {"text":"✓ 成功生成了 barrier!","color":"green"}
execute in stardew:mine if block 0 65 110 minecraft:air run tellraw @s {"text":"✗ 位置仍是空气，生成失败","color":"red"}

# 检查实体
execute in stardew:mine positioned 0 65 110 if entity @e[type=interaction,tag=sd_stone,distance=..2] run tellraw @s {"text":"✓ 找到矿物实体!","color":"green"}
execute in stardew:mine positioned 0 65 110 unless entity @e[type=interaction,tag=sd_stone,distance=..2] run tellraw @s {"text":"✗ 没有矿物实体","color":"red"}

tellraw @s {"text":"===== 测试完成 =====","color":"gold"}
