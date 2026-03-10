# data/stardew/functions/farming/fertilizer/apply_retaining.mcfunction
# 保湿土壤功能：根据等级有不同概率保持湿润
# 执行者: fertilizer_marker (type=3)
# 位置: 在作物marker位置 (~1.375)

# 保湿土壤的效果（原版）：
# - 基础保水土壤 (level 1): 33%概率保持湿润
# - 高级保水土壤 (level 2): 66%概率保持湿润
# - 顶级保水土壤 (level 3): 100%概率保持湿润

# 生成随机数 1-100
execute store result score @s sd_rng run random value 1..100

# 等级1: 33%概率（随机数<=33）
execute if score @s sd_fertilizer_level matches 1 if score @s sd_rng matches 1..33 positioned ~ ~-0.375 ~ if block ~ ~ ~ minecraft:farmland run setblock ~ ~ ~ minecraft:farmland[moisture=7]

# 等级2: 66%概率（随机数<=66）
execute if score @s sd_fertilizer_level matches 2 if score @s sd_rng matches 1..66 positioned ~ ~-0.375 ~ if block ~ ~ ~ minecraft:farmland run setblock ~ ~ ~ minecraft:farmland[moisture=7]

# 等级3: 100%概率（必定保持）
execute if score @s sd_fertilizer_level matches 3 positioned ~ ~-0.375 ~ if block ~ ~ ~ minecraft:farmland run setblock ~ ~ ~ minecraft:farmland[moisture=7]
