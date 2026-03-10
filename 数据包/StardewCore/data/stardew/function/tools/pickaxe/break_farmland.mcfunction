# data/stardew/function/tools/pickaxe/break_farmland.mcfunction
# 破坏耕地并移除上面的作物
# [执行位置: 耕地方块内部]

# 1. 播放破坏音效（更柔和的声音）
playsound minecraft:block.gravel.break block @a ~ ~ ~ 0.8 1.0
playsound minecraft:block.grass.break block @a ~ ~ ~ 1.0 0.9

# 2. 泥土粒子效果
particle minecraft:block{block_state:"minecraft:dirt"} ~ ~0.5 ~ 0.3 0.3 0.3 0 30
particle minecraft:poof ~ ~0.5 ~ 0.2 0.2 0.2 0.05 10

# 3. 破坏上面的作物实体（如果有）
# 对齐到方块网格,精确定位到作物marker的位置(y=1.375)
execute align xyz positioned ~0.5 ~1.375 ~0.5 as @e[type=marker,tag=sd_crop,distance=..0.1] run function stardew:tools/pickaxe/remove_single_crop

# 3.5 清除肥料实体（如果有）
execute align xyz positioned ~0.5 ~ ~0.5 run function stardew:farming/fertilizer/clear_at_position

# 4. 将耕地变成泥土
execute align xyz run setblock ~ ~ ~ minecraft:dirt
