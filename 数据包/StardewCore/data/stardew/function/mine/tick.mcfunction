# stardew:mine/tick.mcfunction
# 矿洞系统主循环 - 每tick执行

# 检测矿洞入口交互 (主世界) - 只对在主世界的玩家
execute as @a[nbt={Dimension:"minecraft:overworld"}] at @s run function stardew:mine/enter/check_entrance

# 以下只对在矿洞维度内的玩家执行
# 检测梯子交互
execute as @a[nbt={Dimension:"stardew:mine"}] at @s run function stardew:mine/ladder/check_interact

# 检测电梯交互
execute as @a[nbt={Dimension:"stardew:mine"}] at @s run function stardew:mine/elevator/check_interact

# 检测下层入口交互
execute as @a[nbt={Dimension:"stardew:mine"}] at @s run function stardew:mine/enter/check_next_floor

# 检测返回地面交互
execute as @a[nbt={Dimension:"stardew:mine"}] at @s run function stardew:mine/exit/check

# 检测木桶交互
execute as @e[type=interaction,tag=sd_barrel_interaction] at @s run function stardew:mine/barrel/check_interaction

# 持续高亮最后一个石头（针对有sd_mine_last_stone标签的玩家）
execute as @a[tag=sd_mine_last_stone,nbt={Dimension:"stardew:mine"}] at @s run function stardew:mine/ladder/maintain_glow
