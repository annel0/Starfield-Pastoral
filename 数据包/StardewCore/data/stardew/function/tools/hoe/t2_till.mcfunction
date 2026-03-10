# 铁锄头 (3x3)
# 在调用函数前，先检查目标方块是否符合标签

execute positioned ~-1 ~ ~-1 if block ~ ~ ~ #stardew:tillable run function stardew:tools/hoe/till_single_block
execute positioned ~-1 ~ ~0 if block ~ ~ ~ #stardew:tillable run function stardew:tools/hoe/till_single_block
execute positioned ~-1 ~ ~1 if block ~ ~ ~ #stardew:tillable run function stardew:tools/hoe/till_single_block
execute positioned ~0 ~ ~-1 if block ~ ~ ~ #stardew:tillable run function stardew:tools/hoe/till_single_block
execute positioned ~0 ~ ~0 if block ~ ~ ~ #stardew:tillable run function stardew:tools/hoe/till_single_block
execute positioned ~0 ~ ~1 if block ~ ~ ~ #stardew:tillable run function stardew:tools/hoe/till_single_block
execute positioned ~1 ~ ~-1 if block ~ ~ ~ #stardew:tillable run function stardew:tools/hoe/till_single_block
execute positioned ~1 ~ ~0 if block ~ ~ ~ #stardew:tillable run function stardew:tools/hoe/till_single_block
execute positioned ~1 ~ ~1 if block ~ ~ ~ #stardew:tillable run function stardew:tools/hoe/till_single_block