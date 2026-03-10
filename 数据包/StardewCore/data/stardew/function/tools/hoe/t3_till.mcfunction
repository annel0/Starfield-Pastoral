# data/stardew/functions/tools/hoe/t3_till.mcfunction
# 金锄头：5x5 范围 (以目标为中心，X: -2~2, Z: -2~2)
# 每一行都包含 #stardew:tillable 检查

# Row 1 (Z = -2)
execute positioned ~-2 ~ ~-2 if block ~ ~ ~ #stardew:tillable run function stardew:tools/hoe/till_single_block
execute positioned ~-1 ~ ~-2 if block ~ ~ ~ #stardew:tillable run function stardew:tools/hoe/till_single_block
execute positioned ~0 ~ ~-2 if block ~ ~ ~ #stardew:tillable run function stardew:tools/hoe/till_single_block
execute positioned ~1 ~ ~-2 if block ~ ~ ~ #stardew:tillable run function stardew:tools/hoe/till_single_block
execute positioned ~2 ~ ~-2 if block ~ ~ ~ #stardew:tillable run function stardew:tools/hoe/till_single_block

# Row 2 (Z = -1)
execute positioned ~-2 ~ ~-1 if block ~ ~ ~ #stardew:tillable run function stardew:tools/hoe/till_single_block
execute positioned ~-1 ~ ~-1 if block ~ ~ ~ #stardew:tillable run function stardew:tools/hoe/till_single_block
execute positioned ~0 ~ ~-1 if block ~ ~ ~ #stardew:tillable run function stardew:tools/hoe/till_single_block
execute positioned ~1 ~ ~-1 if block ~ ~ ~ #stardew:tillable run function stardew:tools/hoe/till_single_block
execute positioned ~2 ~ ~-1 if block ~ ~ ~ #stardew:tillable run function stardew:tools/hoe/till_single_block

# Row 3 (Z = 0, 中心行)
execute positioned ~-2 ~ ~0 if block ~ ~ ~ #stardew:tillable run function stardew:tools/hoe/till_single_block
execute positioned ~-1 ~ ~0 if block ~ ~ ~ #stardew:tillable run function stardew:tools/hoe/till_single_block
execute positioned ~0 ~ ~0 if block ~ ~ ~ #stardew:tillable run function stardew:tools/hoe/till_single_block
execute positioned ~1 ~ ~0 if block ~ ~ ~ #stardew:tillable run function stardew:tools/hoe/till_single_block
execute positioned ~2 ~ ~0 if block ~ ~ ~ #stardew:tillable run function stardew:tools/hoe/till_single_block

# Row 4 (Z = 1)
execute positioned ~-2 ~ ~1 if block ~ ~ ~ #stardew:tillable run function stardew:tools/hoe/till_single_block
execute positioned ~-1 ~ ~1 if block ~ ~ ~ #stardew:tillable run function stardew:tools/hoe/till_single_block
execute positioned ~0 ~ ~1 if block ~ ~ ~ #stardew:tillable run function stardew:tools/hoe/till_single_block
execute positioned ~1 ~ ~1 if block ~ ~ ~ #stardew:tillable run function stardew:tools/hoe/till_single_block
execute positioned ~2 ~ ~1 if block ~ ~ ~ #stardew:tillable run function stardew:tools/hoe/till_single_block

# Row 5 (Z = 2)
execute positioned ~-2 ~ ~2 if block ~ ~ ~ #stardew:tillable run function stardew:tools/hoe/till_single_block
execute positioned ~-1 ~ ~2 if block ~ ~ ~ #stardew:tillable run function stardew:tools/hoe/till_single_block
execute positioned ~0 ~ ~2 if block ~ ~ ~ #stardew:tillable run function stardew:tools/hoe/till_single_block
execute positioned ~1 ~ ~2 if block ~ ~ ~ #stardew:tillable run function stardew:tools/hoe/till_single_block
execute positioned ~2 ~ ~2 if block ~ ~ ~ #stardew:tillable run function stardew:tools/hoe/till_single_block