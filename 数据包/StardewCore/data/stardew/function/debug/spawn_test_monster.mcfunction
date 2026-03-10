# 生成测试用怪物（僵尸）

# 清除旧的测试怪物
kill @e[tag=sd_test_monster]

# 生成新的
summon minecraft:zombie ~ ~1 ~2 {Tags:["sd_monster","sd_test_monster","sd_monster_init"],CustomName:'{"text":"测试僵尸","color":"red"}',CustomNameVisible:1b,Health:20f,Attributes:[{Name:"generic.max_health",Base:100},{Name:"generic.attack_damage",Base:5}]}

# 初始化怪物数据
execute as @e[tag=sd_monster_init] run function stardew:combat/init_monster

tellraw @s [{"text":"[Debug] 已生成测试怪物！","color":"green"}]
tellraw @s [{"text":"提示：用剑左键攻击怪物","color":"yellow"}]
