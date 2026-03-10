# stardew:combat/debug/spawn_test_slime.mcfunction
# Debug: 生成测试用大史莱姆

# 生成大史莱姆 (Size:2, 100HP, 8攻击力)
summon minecraft:slime ~ ~ ~3 {Size:2,Tags:["sd_monster_init","sd_monster","sd_mob_slime","sd_hp_100","sd_atk_8"],DeathLootTable:"stardew:monsters/slime_large",CustomNameVisible:1b,CustomName:'{"text":"测试大史莱姆","color":"green"}'}

# 初始化怪物
execute as @e[tag=sd_monster_init,distance=..5] run function stardew:combat/init_monster

# 提示
tellraw @a {"text":"✔ 已生成测试大史莱姆 (Size:2, 100HP, 8ATK)","color":"green"}
