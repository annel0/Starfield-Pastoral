# stardew:mining/try_spawn_at_location.mcfunction
# 在射线命中的方块上方尝试生成石头实体
# 执行者: 玩家 (@s)
# 执行位置: 命中的固体方块

# 检测方块上方是否有足够空间（需要 1 格高度）
execute unless block ~ ~1 ~ #minecraft:air run tellraw @s {"text":"❌ 该位置上方没有足够空间！","color":"red"}
execute unless block ~ ~1 ~ #minecraft:air run return 0

# 检测是否已有实体占据（防止重叠）
execute positioned ~ ~1 ~ align xyz positioned ~0.5 ~ ~0.5 if entity @e[type=minecraft:interaction,distance=..0.8] run tellraw @s {"text":"❌ 该位置已有实体，无法生成！","color":"red"}
execute positioned ~ ~1 ~ align xyz positioned ~0.5 ~ ~0.5 if entity @e[type=minecraft:interaction,distance=..0.8] run return 0

# 位置合法，对齐到方块网格，在方块上方生成实体
# align xyz: 对齐到整数坐标
# positioned ~0.5 ~1 ~0.5: 移动到方块上方中心（X和Z居中，Y抬高1格）
execute align xyz positioned ~0.5 ~1 ~0.5 run function stardew:mining/spawn_stone_dispatch
