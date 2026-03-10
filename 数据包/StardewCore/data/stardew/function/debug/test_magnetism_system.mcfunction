# 调试 apply_magnetism 执行情况

tellraw @s [{"text":"[磁力系统调试]","color":"gold"}]

# 检查临时存储
execute if data storage stardew:temp boots_effects.magnetism run tellraw @s [{"text":"✓ magnetism 数据存在: ","color":"green"},{"nbt":"boots_effects.magnetism","storage":"stardew:temp"}]
execute unless data storage stardew:temp boots_effects.magnetism run tellraw @s [{"text":"✗ magnetism 数据不存在","color":"red"}]

# 测试选择器
execute store result score #test_items sd_temp if entity @e[type=item,distance=1.5..3.5]
tellraw @s [{"text":"1.5-3.5格范围内物品数: ","color":"yellow"},{"score":{"name":"#test_items","objective":"sd_temp"}}]

# 手动测试执行
tellraw @s [{"text":"正在手动执行磁力效果...","color":"aqua"}]
execute if data storage stardew:temp boots_effects.magnetism as @e[type=item,distance=1.5..3.5] at @s run particle flame ~ ~0.5 ~ 0.1 0.1 0.1 0 5 force
