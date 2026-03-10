# data/stardew/function/crafting/give_chest.mcfunction
# [执行者: 玩家] 给予箱子
# 参数: amount (但由于loot命令限制，实际通过检查#CraftAmount来循环给予)

# 每次给予1个，根据数量重复执行
execute if score #CraftAmount sd_temp matches 1.. run loot give @s loot stardew:items/utility/chest
execute if score #CraftAmount sd_temp matches 2.. run loot give @s loot stardew:items/utility/chest
execute if score #CraftAmount sd_temp matches 3.. run loot give @s loot stardew:items/utility/chest
execute if score #CraftAmount sd_temp matches 4.. run loot give @s loot stardew:items/utility/chest
execute if score #CraftAmount sd_temp matches 5.. run loot give @s loot stardew:items/utility/chest
