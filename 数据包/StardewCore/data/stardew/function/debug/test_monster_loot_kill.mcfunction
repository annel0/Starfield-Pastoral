# stardew:debug/test_monster_loot_kill.mcfunction
# 杀死测试怪物

# 移除效果
effect clear @e[tag=sd_test_loot] minecraft:resistance
effect clear @e[tag=sd_test_loot] minecraft:regeneration

# 设置血量为0
execute as @e[tag=sd_test_loot] run data merge entity @s {Health:0.0f}

# 击杀
kill @e[tag=sd_test_loot]

# 提示
tellraw @a {"text":"✓ 测试史莱姆已击杀，检查是否掉落物品","color":"green"}
