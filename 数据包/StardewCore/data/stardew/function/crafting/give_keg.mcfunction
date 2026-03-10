# data/stardew/function/crafting/give_keg.mcfunction
# [执行者: 玩家] 给予小桶
# 参数: amount (但由于loot命令限制，实际通过检查#CraftAmount来循环给予)

# 每次给予1个，根据数量重复执行
execute if score #CraftAmount sd_temp matches 1.. run loot give @s loot stardew:items/utility/keg
execute if score #CraftAmount sd_temp matches 2.. run loot give @s loot stardew:items/utility/keg
execute if score #CraftAmount sd_temp matches 3.. run loot give @s loot stardew:items/utility/keg
execute if score #CraftAmount sd_temp matches 4.. run loot give @s loot stardew:items/utility/keg
execute if score #CraftAmount sd_temp matches 5.. run loot give @s loot stardew:items/utility/keg
