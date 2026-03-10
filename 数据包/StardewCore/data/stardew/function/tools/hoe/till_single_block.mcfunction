# data/stardew/functions/tools/hoe/till_single_block.mcfunction
# [执行位置: 目标方块]

# 1. 安全检查：必须是可耕种方块
execute unless block ~ ~ ~ #stardew:tillable run return 0

# 2. 变成耕地 (默认干燥)
setblock ~ ~ ~ minecraft:farmland[moisture=0]

# 3. 计数：每耕一块地，sd_temp +1（用于能量计算）
scoreboard players add @s sd_temp 1

# 3. 音效与粒子
playsound minecraft:item.hoe.till player @s ~ ~ ~ 1 1
particle minecraft:block{block_state: "minecraft:dirt"} ~ ~0.5 ~ 0.2 0.2 0.2 1 10