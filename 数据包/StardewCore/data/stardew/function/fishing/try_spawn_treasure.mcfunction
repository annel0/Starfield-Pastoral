# data/stardew/functions/fishing/try_spawn_treasure.mcfunction
# [执行者: 玩家]
# 独立计算三种宝箱的掉落

# 先读取渔具
function stardew:fishing/utils/check_tackle

# 定义宝藏概率加成 (默认 0)
scoreboard players set @s sd_treasure_bonus 0
# 如果是寻宝者 (ID 5006)，加成 5%（原版寻宝者加成）
execute if score @s sd_tackle_id matches 5006 run scoreboard players set @s sd_treasure_bonus 5

# =========================================================
# 1. 普通宝箱 (CMD 40510) - 概率 15% (寻宝者 20%)
# =========================================================
execute store result score Global sd_rng run random value 1..100
scoreboard players operation Global sd_rng -= @s sd_treasure_bonus
execute if score Global sd_rng matches 1..15 run loot give @s loot stardew:items/fishing/treasure_chest_common
execute if score Global sd_rng matches 1..15 run tellraw @s [{"text":"✨ ","color":"gold"},{"text":"钓到了 ","color":"yellow"},{"text":"普通宝箱","color":"green","bold":true},{"text":"! 右键打开查看内容物。","color":"yellow"}]

# =========================================================
# 2. 稀有宝箱 (CMD 40520) - 概率 5% (寻宝者 10%)
# =========================================================
execute store result score Global sd_rng run random value 1..100
scoreboard players operation Global sd_rng -= @s sd_treasure_bonus
execute if score Global sd_rng matches 1..5 run loot give @s loot stardew:items/fishing/treasure_chest_rare
execute if score Global sd_rng matches 1..5 run tellraw @s [{"text":"✨ ","color":"gold"},{"text":"钓到了 ","color":"yellow"},{"text":"稀有宝箱","color":"aqua","bold":true},{"text":"! 右键打开查看内容物。","color":"yellow"}]

# =========================================================
# 3. 史诗宝箱 (CMD 40530) - 概率 1% (寻宝者 6%)
# =========================================================
execute store result score Global sd_rng run random value 1..100
scoreboard players operation Global sd_rng -= @s sd_treasure_bonus
execute if score Global sd_rng matches 1..1 run loot give @s loot stardew:items/fishing/treasure_chest_epic
execute if score Global sd_rng matches 1..1 run tellraw @s [{"text":"🌟 ","color":"light_purple"},{"text":"钓到了 ","color":"yellow"},{"text":"史诗宝箱","color":"light_purple","bold":true},{"text":"!!! 右键打开查看内容物。","color":"yellow"}]
