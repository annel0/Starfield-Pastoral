# data/stardew/functions/fishing/api/drop_quality.mcfunction
# 宏：根据随机数决定品质并掉落

# 1. 计算随机数 (1-100)
execute store result score Global sd_rng run random value 1..100

# 2. 根据随机数调用分支
$execute if score Global sd_rng matches 1..50 run loot give @s loot stardew:items/fish/$(id)_base
$execute if score Global sd_rng matches 51..80 run loot give @s loot stardew:items/fish/$(id)_silver
$execute if score Global sd_rng matches 81..95 run loot give @s loot stardew:items/fish/$(id)_gold
$execute if score Global sd_rng matches 96..100 run loot give @s loot stardew:items/fish/$(id)_diamond

# 3. 播放音效
playsound minecraft:entity.item.pickup player @s ~ ~ ~ 1 1