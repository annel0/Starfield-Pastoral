# stardew:weeds/try_spawn_at_location.mcfunction
# 尝试在目标位置生成杂草
# 执行者: 玩家 (@s)
# 执行位置: 射线命中的方块位置

# 检查上方是否为空气（可放置）
execute unless block ~ ~1 ~ #minecraft:air run tellraw @s {"text":"❌ 该位置上方有方块，无法放置杂草","color":"red"}
execute unless block ~ ~1 ~ #minecraft:air run return 0

# 对齐到方块底部，然后检查该方块内是否已有杂草
# 使用distance=..0.1在对齐后的位置检测，更可靠
execute align xyz positioned ~0.5 ~1 ~0.5 if entity @e[type=interaction,tag=weed_hitbox,distance=..0.1] run tellraw @s {"text":"❌ 该位置已有杂草","color":"yellow"}
execute align xyz positioned ~0.5 ~1 ~0.5 if entity @e[type=interaction,tag=weed_hitbox,distance=..0.1] run return 0

# 对齐到方块中心底部，生成杂草（在方块上方的中心位置）
execute align xyz positioned ~0.5 ~1 ~0.5 run function stardew:weeds/spawn_random_weed
