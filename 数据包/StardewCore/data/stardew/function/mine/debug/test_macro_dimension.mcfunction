# stardew:mine/debug/test_macro_dimension.mcfunction
# 测试宏函数中的维度命令

tellraw @s {"text":"===== 测试宏函数维度 =====","color":"gold"}

# 方法1: 直接 fill (在主世界执行)
tellraw @s {"text":"测试1: 直接 execute in stardew:mine run fill","color":"yellow"}
execute in stardew:mine run fill 0 64 150 5 64 155 minecraft:gold_block

# 检查
execute in stardew:mine if block 2 64 152 minecraft:gold_block run tellraw @s {"text":"✓ 直接命令成功","color":"green"}
execute in stardew:mine unless block 2 64 152 minecraft:gold_block run tellraw @s {"text":"✗ 直接命令失败","color":"red"}

# 方法2: 使用 storage + 宏
tellraw @s {"text":"测试2: 使用宏函数","color":"yellow"}
data modify storage stardew:mine test_z set value 160
function stardew:mine/debug/test_macro_fill with storage stardew:mine

# 检查
execute in stardew:mine if block 2 64 162 minecraft:diamond_block run tellraw @s {"text":"✓ 宏函数成功","color":"green"}
execute in stardew:mine unless block 2 64 162 minecraft:diamond_block run tellraw @s {"text":"✗ 宏函数失败","color":"red"}

tellraw @s {"text":"===== 测试完成 =====","color":"gold"}
