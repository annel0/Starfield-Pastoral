# 生成测试骷髅（用于调试AI攻击）
# 使用方法: /function stardew:combat/debug/spawn_test_skeleton

summon minecraft:skeleton ~ ~ ~3 {Tags:["sd_monster_init","sd_monster","sd_mob_skeleton","sd_tier_2","sd_hp_50","sd_atk_10"],DeathLootTable:"stardew:monsters/skeleton",CustomNameVisible:1b,CustomName:'{"text":"测试骷髅","color":"white"}',Attributes:[{id:"minecraft:generic.attack_damage",base:5.0d}]}

# 立即初始化
execute as @e[tag=sd_monster_init,distance=..5] run function stardew:combat/init_monster

tellraw @a [{"text":"✓ ","color":"green"},{"text":"已生成测试骷髅（让它自然生成弓，不手动干预）","color":"yellow"}]
