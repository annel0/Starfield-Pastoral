# stardew:monsters/debug/spawn_test_monsters.mcfunction
# 生成测试怪物（所有类型）

# 设置测试层数
scoreboard players set @s sd_mine_floor 1

tellraw @s {"text":"=== 测试怪物生成 (1-40层) ===","color":"gold"}
execute positioned ~ ~ ~5 run function stardew:monsters/spawn/types/slime
execute positioned ~ ~ ~10 run function stardew:monsters/spawn/types/spider
execute positioned ~ ~ ~15 run function stardew:monsters/spawn/types/silverfish

scoreboard players set @s sd_mine_floor 50
tellraw @s {"text":"=== 测试怪物生成 (41-80层) ===","color":"gold"}
execute positioned ~ ~ ~20 run function stardew:monsters/spawn/types/slime
execute positioned ~ ~ ~25 run function stardew:monsters/spawn/types/skeleton

scoreboard players set @s sd_mine_floor 100
tellraw @s {"text":"=== 测试怪物生成 (81-120层) ===","color":"gold"}
execute positioned ~ ~ ~30 run function stardew:monsters/spawn/types/slime
execute positioned ~ ~ ~35 run function stardew:monsters/spawn/types/spider
execute positioned ~ ~ ~40 run function stardew:monsters/spawn/types/skeleton

tellraw @s {"text":"[DEBUG] 已生成所有测试怪物！","color":"green"}
