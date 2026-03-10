# data/stardew/function/mining/tick.mcfunction
# 挖矿系统主循环 - 每tick执行

# 检测石头/矿石实体的交互（内部会自动清理数据）
execute as @e[type=interaction,tag=sd_stone] at @s run function stardew:mining/check_interaction

# 挖矿升级检查 (每tick对所有玩家执行)
execute as @a run function stardew:mining/level_up

# ===== 矿石高亮系统 =====
# 1. 对持有镐子的玩家进行射线检测
execute as @a at @s run function stardew:mining/highlight/raycast_start

# 2. 检测哪些矿石失去了瞄准 (本tick未被标记为targeted)
# 但是不要移除最后一块石头的高亮（sd_last_stone_glow）
execute as @e[type=item_display,tag=sd_stone_display,tag=!sd_last_stone_glow,scores={sd_mining_targeted_prev=1}] unless score @s sd_mining_targeted matches 1 at @s run function stardew:mining/highlight/highlight_off

# 3. 更新所有矿石的 targeted_prev 状态
execute as @e[type=item_display,tag=sd_stone_display] run scoreboard players operation @s sd_mining_targeted_prev = @s sd_mining_targeted

# 4. 重置本tick的 targeted 标记
execute as @e[type=item_display,tag=sd_stone_display] run scoreboard players set @s sd_mining_targeted 0

# 5. 保持最后一块石头的持续高亮（每tick重新应用，防止被清除）
execute as @e[type=item_display,tag=sd_stone_display,tag=sd_last_stone_glow] run data merge entity @s {Glowing:1b,glow_color_override:16776960}


