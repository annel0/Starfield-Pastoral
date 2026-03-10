# stardew:mine/debug/test_single_position.mcfunction
# 测试单个位置的矿物生成条件

tellraw @s {"text":"===== 测试位置 (0, 65, 110) =====","color":"gold"}

# 检查方块条件
execute in stardew:mine if block 0 65 110 minecraft:air run tellraw @s {"text":"✓ (0,65,110) 是空气","color":"green"}
execute in stardew:mine unless block 0 65 110 minecraft:air run tellraw @s {"text":"✗ (0,65,110) 不是空气","color":"red"}

execute in stardew:mine if block 0 64 110 minecraft:stone run tellraw @s {"text":"✓ (0,64,110) 是石头","color":"green"}
execute in stardew:mine unless block 0 64 110 minecraft:stone run tellraw @s {"text":"✗ (0,64,110) 不是石头","color":"red"}

# 显示实际方块
execute in stardew:mine run tellraw @s [{"text":"(0,65,110) 方块: ","color":"gray"},{"text":"?","color":"aqua"}]
execute in stardew:mine run tellraw @s [{"text":"(0,64,110) 方块: ","color":"gray"},{"text":"?","color":"aqua"}]

# 手动测试生成
tellraw @s {"text":"手动测试生成矿物...","color":"yellow"}
scoreboard players set @s sd_mine_floor 1

execute in stardew:mine positioned 0 65 110 if block ~ ~ ~ minecraft:air if block ~ ~-1 ~ minecraft:stone run tellraw @s {"text":"✓ 条件满足! 调用 spawn_random_ore","color":"green"}
execute in stardew:mine positioned 0 65 110 if block ~ ~ ~ minecraft:air if block ~ ~-1 ~ minecraft:stone run function stardew:mine/ore/spawn_random_ore

execute in stardew:mine positioned 0 65 110 unless block ~ ~ ~ minecraft:air run tellraw @s {"text":"✗ 空气条件不满足","color":"red"}
execute in stardew:mine positioned 0 65 110 if block ~ ~ ~ minecraft:air unless block ~ ~-1 ~ minecraft:stone run tellraw @s {"text":"✗ 石头条件不满足 (下方不是stone)","color":"red"}

# 检查是否生成了 barrier
execute in stardew:mine if block 0 65 110 minecraft:barrier run tellraw @s {"text":"✓ 成功生成了 barrier (矿物)","color":"green"}
execute in stardew:mine unless block 0 65 110 minecraft:barrier unless block 0 65 110 minecraft:air run tellraw @s [{"text":"生成了其他方块","color":"yellow"}]

tellraw @s {"text":"===== 测试完成 =====","color":"gold"}
