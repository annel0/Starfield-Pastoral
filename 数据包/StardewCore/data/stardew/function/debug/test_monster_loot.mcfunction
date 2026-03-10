# stardew:debug/test_monster_loot.mcfunction
# 测试怪物掉落是否正常

# 提示
tellraw @a {"text":"=== 怪物掉落测试 ===","color":"gold","bold":true}

# 生成一个测试史莱姆
summon minecraft:slime ~ ~1 ~ {Size:0,Tags:["sd_test_loot"],DeathLootTable:"stardew:monsters/slime_small",CustomName:'{"text":"测试史莱姆","color":"green"}',CustomNameVisible:1b}

# 等待1秒后杀死
schedule function stardew:debug/test_monster_loot_kill 20t

tellraw @a {"text":"✓ 已生成测试史莱姆，1秒后自动击杀","color":"green"}
tellraw @a {"text":"如果掉落物品，说明战利品表正常工作","color":"yellow"}
